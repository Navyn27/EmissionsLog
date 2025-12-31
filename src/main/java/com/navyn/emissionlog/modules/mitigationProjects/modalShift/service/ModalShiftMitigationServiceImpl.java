package com.navyn.emissionlog.modules.mitigationProjects.modalShift.service;

import com.navyn.emissionlog.modules.mitigationProjects.modalShift.dtos.ModalShiftMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.modalShift.dtos.ModalShiftMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.modalShift.dtos.ModalShiftParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.modalShift.enums.FuelType;
import com.navyn.emissionlog.modules.mitigationProjects.modalShift.models.ModalShiftMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.modalShift.repository.ModalShiftMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.repositories.InterventionRepository;
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
public class ModalShiftMitigationServiceImpl implements ModalShiftMitigationService {
    
    private final ModalShiftMitigationRepository repository;
    private final ModalShiftParameterService parameterService;
    private final InterventionRepository interventionRepository;
    
    /**
     * Maps ModalShiftMitigation entity to Response DTO
     * This method loads intervention data within the transaction to avoid lazy loading issues
     */
    private ModalShiftMitigationResponseDto toResponseDto(ModalShiftMitigation mitigation) {
        ModalShiftMitigationResponseDto dto = new ModalShiftMitigationResponseDto();
        dto.setId(mitigation.getId());
        dto.setYear(mitigation.getYear());
        dto.setCategoryBeforeShift(mitigation.getCategoryBeforeShift());
        dto.setCategoryAfterShift(mitigation.getCategoryAfterShift());
        dto.setVtk(mitigation.getVtk());
        dto.setFuelEconomy(mitigation.getFuelEconomy());
        dto.setFleetPopulation(mitigation.getFleetPopulation());
        dto.setFuelType(mitigation.getFuelType());
        dto.setBauOfShift(mitigation.getBauOfShift());
        dto.setTotalFuel(mitigation.getTotalFuel());
        dto.setProjectEmissionCarbon(mitigation.getProjectEmissionCarbon());
        dto.setProjectEmissionMethane(mitigation.getProjectEmissionMethane());
        dto.setProjectEmissionNitrogen(mitigation.getProjectEmissionNitrogen());
        dto.setTotalProjectEmission(mitigation.getTotalProjectEmission());
        dto.setEmissionReduction(mitigation.getEmissionReduction());

        // Map intervention - FORCE initialization within transaction to avoid lazy loading
        if (mitigation.getIntervention() != null) {
            // Force Hibernate to initialize the proxy while session is still open
            Hibernate.initialize(mitigation.getIntervention());
            Intervention intervention = mitigation.getIntervention();
            ModalShiftMitigationResponseDto.InterventionInfo interventionInfo =
                    new ModalShiftMitigationResponseDto.InterventionInfo(
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
     * Calculate emissions based on Excel formula pattern
     */
    private void calculateEmissions(ModalShiftMitigation mitigation, ModalShiftParameterResponseDto param) {
        // 1. Total Fuel (L)
        Double totalFuel = (mitigation.getVtk() * mitigation.getFuelEconomy()) / 100.0;
        mitigation.setTotalFuel(totalFuel);
        
        // Get fuel-specific parameters
        Double energyContent;
        Double emissionFactorCarbon;
        Double emissionFactorMethane;
        Double emissionFactorNitrogen;
        
        if (mitigation.getFuelType() == FuelType.DIESEL) {
            energyContent = param.getEnergyContentDiesel();
            emissionFactorCarbon = param.getEmissionFactorCarbonDiesel();
            emissionFactorMethane = param.getEmissionFactorMethaneDiesel();
            emissionFactorNitrogen = param.getEmissionFactorNitrogenDiesel();
        } else { // GASOLINE
            energyContent = param.getEnergyContentGasoline();
            emissionFactorCarbon = param.getEmissionFactorCarbonGasoline();
            emissionFactorMethane = param.getEmissionFactorMethaneGasoline();
            emissionFactorNitrogen = param.getEmissionFactorNitrogenGasoline();
        }
        
        // 2. Total Project Emission (GgCO2e) - Using Excel Formula Pattern
        // Formula: fleetPopulation * (totalFuel * energyContent * emissionFactorCarbon + 
        //                              totalFuel * emissionFactorMethane * GWP_CH4 + 
        //                              totalFuel * emissionFactorNitrogen * GWP_NO2) * 10^-12
        Double totalProjectEmission = mitigation.getFleetPopulation() * (
            totalFuel * energyContent * emissionFactorCarbon +
            totalFuel * emissionFactorMethane * param.getPotentialMethane() +
            totalFuel * emissionFactorNitrogen * param.getPotentialNitrogen()
        ) * Math.pow(10, -12);
        mitigation.setTotalProjectEmission(totalProjectEmission);
        
        // 3. Individual Emissions (for breakdown)
        // CO2 (kg) = (fleetPopulation * totalFuel * energyContent * emissionFactorCarbon) / 1,000,000,000
        Double projectEmissionCarbon = (mitigation.getFleetPopulation() * totalFuel * energyContent * emissionFactorCarbon) / 1_000_000_000.0;
        mitigation.setProjectEmissionCarbon(projectEmissionCarbon);
        
        // CH4 (kg) = (fleetPopulation * totalFuel * emissionFactorMethane) / 1,000,000
        Double projectEmissionMethane = (mitigation.getFleetPopulation() * totalFuel * emissionFactorMethane) / 1_000_000.0;
        mitigation.setProjectEmissionMethane(projectEmissionMethane);
        
        // N2O (kg) = (fleetPopulation * totalFuel * emissionFactorNitrogen) / 1,000,000
        Double projectEmissionNitrogen = (mitigation.getFleetPopulation() * totalFuel * emissionFactorNitrogen) / 1_000_000.0;
        mitigation.setProjectEmissionNitrogen(projectEmissionNitrogen);
        
        // 4. Emission Reduction (GgCO2e) - Optional
        if (mitigation.getBauOfShift() != null) {
            Double emissionReduction = mitigation.getBauOfShift() - totalProjectEmission;
            mitigation.setEmissionReduction(emissionReduction);
        } else {
            mitigation.setEmissionReduction(null);
        }
    }
    
    @Override
    @Transactional
    public ModalShiftMitigationResponseDto createModalShiftMitigation(ModalShiftMitigationDto dto) {
        ModalShiftMitigation mitigation = new ModalShiftMitigation();
        
        // Get ModalShiftParameter (latest active) - throws exception if none exists
        ModalShiftParameterResponseDto param;
        try {
            param = parameterService.getLatestActive();
        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Cannot create Modal Shift Mitigation: No active Modal Shift Parameter found. " +
                            "Please create an active parameter first before creating mitigation records.",
                    e
            );
        }
        
        // Get Intervention
        Intervention intervention = interventionRepository.findById(dto.getInterventionId())
                .orElseThrow(() -> new RuntimeException("Intervention not found with id: " + dto.getInterventionId()));
        
        // Set user inputs
        mitigation.setYear(dto.getYear());
        mitigation.setCategoryBeforeShift(dto.getCategoryBeforeShift());
        mitigation.setCategoryAfterShift(dto.getCategoryAfterShift());
        mitigation.setVtk(dto.getVtk());
        mitigation.setFuelEconomy(dto.getFuelEconomy());
        mitigation.setFleetPopulation(dto.getFleetPopulation());
        mitigation.setFuelType(dto.getFuelType());
        mitigation.setBauOfShift(dto.getBauOfShift());
        mitigation.setIntervention(intervention);
        
        // Calculate emissions
        calculateEmissions(mitigation, param);
        
        ModalShiftMitigation saved = repository.save(mitigation);
        return toResponseDto(saved);
    }
    
    @Override
    @Transactional
    public ModalShiftMitigationResponseDto updateModalShiftMitigation(UUID id, ModalShiftMitigationDto dto) {
        ModalShiftMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Modal Shift Mitigation record not found with id: " + id));
        
        // Get ModalShiftParameter (latest active) - throws exception if none exists
        ModalShiftParameterResponseDto param;
        try {
            param = parameterService.getLatestActive();
        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Cannot update Modal Shift Mitigation: No active Modal Shift Parameter found. " +
                            "Please create an active parameter first before updating mitigation records.",
                    e
            );
        }
        
        // Get Intervention
        Intervention intervention = interventionRepository.findById(dto.getInterventionId())
                .orElseThrow(() -> new RuntimeException("Intervention not found with id: " + dto.getInterventionId()));
        
        // Update user inputs
        mitigation.setYear(dto.getYear());
        mitigation.setCategoryBeforeShift(dto.getCategoryBeforeShift());
        mitigation.setCategoryAfterShift(dto.getCategoryAfterShift());
        mitigation.setVtk(dto.getVtk());
        mitigation.setFuelEconomy(dto.getFuelEconomy());
        mitigation.setFleetPopulation(dto.getFleetPopulation());
        mitigation.setFuelType(dto.getFuelType());
        mitigation.setBauOfShift(dto.getBauOfShift());
        mitigation.setIntervention(intervention);
        
        // Recalculate emissions
        calculateEmissions(mitigation, param);
        
        ModalShiftMitigation saved = repository.save(mitigation);
        return toResponseDto(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ModalShiftMitigationResponseDto> getAllModalShiftMitigation(Integer year) {
        Specification<ModalShiftMitigation> spec = Specification.where(hasYear(year));
        List<ModalShiftMitigation> mitigations = repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
        return mitigations.stream()
                .map(this::toResponseDto)
                .toList();
    }
    
    @Override
    @Transactional
    public void deleteModalShiftMitigation(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Modal Shift Mitigation record not found with id: " + id);
        }
        repository.deleteById(id);
    }
}

