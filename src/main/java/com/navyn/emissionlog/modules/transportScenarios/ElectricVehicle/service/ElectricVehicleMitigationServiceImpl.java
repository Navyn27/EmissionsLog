package com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.service;

import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.repositories.InterventionRepository;
import com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.dtos.ElectricVehicleMitigationDto;
import com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.dtos.ElectricVehicleMitigationResponseDto;
import com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.dtos.ElectricVehicleParameterResponseDto;
import com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.models.ElectricVehicleMitigation;
import com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.repository.ElectricVehicleMitigationRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.navyn.emissionlog.utils.Specifications.MitigationSpecifications.hasYear;

@Service
@RequiredArgsConstructor
public class ElectricVehicleMitigationServiceImpl implements ElectricVehicleMitigationService {
    
    private final ElectricVehicleMitigationRepository repository;
    private final ElectricVehicleParameterService parameterService;
    private final InterventionRepository interventionRepository;
    
    /**
     * Maps ElectricVehicleMitigation entity to Response DTO
     * This method loads intervention data within the transaction to avoid lazy loading issues
     */
    private ElectricVehicleMitigationResponseDto toResponseDto(ElectricVehicleMitigation mitigation) {
        ElectricVehicleMitigationResponseDto dto = new ElectricVehicleMitigationResponseDto();
        dto.setId(mitigation.getId());
        dto.setYear(mitigation.getYear());
        dto.setVehicleCategory(mitigation.getVehicleCategory());
        dto.setVkt(mitigation.getVkt());
        dto.setFleetPopulation(mitigation.getFleetPopulation());
        dto.setEvPowerDemand(mitigation.getEvPowerDemand());
        dto.setBau(mitigation.getBau());
        dto.setAnnualElectricityConsumption(mitigation.getAnnualElectricityConsumption());
        dto.setTotalProjectEmission(mitigation.getTotalProjectEmission());
        dto.setEmissionReduction(mitigation.getEmissionReduction());

        // Map intervention - FORCE initialization within transaction to avoid lazy loading
        if (mitigation.getIntervention() != null) {
            // Force Hibernate to initialize the proxy while session is still open
            Hibernate.initialize(mitigation.getIntervention());
            Intervention intervention = mitigation.getIntervention();
            ElectricVehicleMitigationResponseDto.InterventionInfo interventionInfo =
                    new ElectricVehicleMitigationResponseDto.InterventionInfo(
                            intervention.getId(),
                            intervention.getName()
                    );
            dto.setIntervention(interventionInfo);
        } else {
            dto.setIntervention(null);
        }

        return dto;
    }
    
    /**
     * Calculate emissions based on provided formulas:
     * annualElectricityConsumption = ((vkt * evPowerDemand) / 100) * 10^-3 (MWh)
     * totalProjectEmission = annualElectricityConsumption * gridEmissionFactor * 10^-6 (GgCO₂e)
     * emissionReduction = bau - totalProjectEmission (GgCO₂e)
     */
    private void calculateEmissions(ElectricVehicleMitigation mitigation, ElectricVehicleParameterResponseDto param) {
        // 1. Annual Electricity Consumption (MWh)
        // Formula: ((vkt * evPowerDemand) / 100) * 10^-3
        Double annualElectricityConsumption = ((mitigation.getVkt() * mitigation.getEvPowerDemand()) / 100.0) * Math.pow(10, -3);
        mitigation.setAnnualElectricityConsumption(annualElectricityConsumption);
        
        // 2. Total Project Emission (GgCO₂e)
        // Formula: annualElectricityConsumption * gridEmissionFactor
        Double totalProjectEmission = annualElectricityConsumption * param.getGridEmissionFactor() ;
        mitigation.setTotalProjectEmission(totalProjectEmission);
        
        // 3. Emission Reduction (GgCO₂e) - Optional
        // Formula: bau - totalProjectEmission
        if (mitigation.getBau() != null) {
            Double emissionReduction = mitigation.getBau() - totalProjectEmission;
            mitigation.setEmissionReduction(emissionReduction);
        } else {
            mitigation.setEmissionReduction(null);
        }
    }
    
    @Override
    @Transactional
    public ElectricVehicleMitigationResponseDto createElectricVehicleMitigation(ElectricVehicleMitigationDto dto) {
        ElectricVehicleMitigation mitigation = new ElectricVehicleMitigation();
        
        // Get ElectricVehicleParameter (latest active) - throws exception if none exists
        ElectricVehicleParameterResponseDto param;
        try {
            param = parameterService.getLatestActive();
        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Cannot create Electric Vehicle Mitigation: No active Electric Vehicle Parameter found. " +
                            "Please create an active parameter first before creating mitigation records.",
                    e
            );
        }
        
        // Get Intervention
        Intervention intervention = interventionRepository.findById(dto.getInterventionId())
                .orElseThrow(() -> new RuntimeException("Intervention not found with id: " + dto.getInterventionId()));
        
        // Set user inputs
        mitigation.setYear(dto.getYear());
        mitigation.setVehicleCategory(dto.getVehicleCategory());
        mitigation.setVkt(dto.getVkt());
        mitigation.setFleetPopulation(dto.getFleetPopulation());
        mitigation.setEvPowerDemand(dto.getEvPowerDemand());
        mitigation.setBau(dto.getBau());
        mitigation.setIntervention(intervention);
        
        // Calculate emissions
        calculateEmissions(mitigation, param);
        
        ElectricVehicleMitigation saved = repository.save(mitigation);
        return toResponseDto(saved);
    }
    
    @Override
    @Transactional
    public ElectricVehicleMitigationResponseDto updateElectricVehicleMitigation(UUID id, ElectricVehicleMitigationDto dto) {
        ElectricVehicleMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Electric Vehicle Mitigation record not found with id: " + id));
        
        // Get ElectricVehicleParameter (latest active) - throws exception if none exists
        ElectricVehicleParameterResponseDto param;
        try {
            param = parameterService.getLatestActive();
        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Cannot update Electric Vehicle Mitigation: No active Electric Vehicle Parameter found. " +
                            "Please create an active parameter first before updating mitigation records.",
                    e
            );
        }
        
        // Get Intervention
        Intervention intervention = interventionRepository.findById(dto.getInterventionId())
                .orElseThrow(() -> new RuntimeException("Intervention not found with id: " + dto.getInterventionId()));
        
        // Update user inputs
        mitigation.setYear(dto.getYear());
        mitigation.setVehicleCategory(dto.getVehicleCategory());
        mitigation.setVkt(dto.getVkt());
        mitigation.setFleetPopulation(dto.getFleetPopulation());
        mitigation.setEvPowerDemand(dto.getEvPowerDemand());
        mitigation.setBau(dto.getBau());
        mitigation.setIntervention(intervention);
        
        // Recalculate emissions
        calculateEmissions(mitigation, param);
        
        ElectricVehicleMitigation saved = repository.save(mitigation);
        return toResponseDto(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ElectricVehicleMitigationResponseDto> getAllElectricVehicleMitigation(Integer year) {
        Specification<ElectricVehicleMitigation> spec = Specification.where(hasYear(year));
        List<ElectricVehicleMitigation> mitigations = repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
        return mitigations.stream()
                .map(this::toResponseDto)
                .toList();
    }
    
    @Override
    @Transactional
    public void deleteElectricVehicleMitigation(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Electric Vehicle Mitigation record not found with id: " + id);
        }
        repository.deleteById(id);
    }
}

