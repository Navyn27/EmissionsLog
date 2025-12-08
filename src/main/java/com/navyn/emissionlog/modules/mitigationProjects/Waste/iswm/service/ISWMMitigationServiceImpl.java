package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos.ISWMMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.models.ISWMMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.repository.ISWMMitigationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.navyn.emissionlog.utils.Specifications.MitigationSpecifications.hasYear;

@Service
@RequiredArgsConstructor
public class ISWMMitigationServiceImpl implements ISWMMitigationService {
    
    private final ISWMMitigationRepository repository;
    
    @Override
    public ISWMMitigation createISWMMitigation(ISWMMitigationDto dto) {
        ISWMMitigation mitigation = new ISWMMitigation();
        
        // Convert BAU Emission to standard units (tCO₂e)
        double bauEmissionInTonnes = dto.getBauEmissionUnit().toKiloTonnesCO2e(dto.getBauEmission());
        
        // Set user inputs
        mitigation.setYear(dto.getYear());
        mitigation.setWasteProcessed(dto.getWasteProcessed());
        mitigation.setDegradableOrganicFraction(dto.getDegradableOrganicFraction());
        mitigation.setLandfillAvoidance(dto.getLandfillAvoidance());
        mitigation.setCompostingEF(dto.getCompostingEF());
        mitigation.setBauEmission(bauEmissionInTonnes);
        
        // Calculations
        // DOFDiverted = wasteProcessed * %DegradableOrganicFraction
        Double dofDiverted = dto.getWasteProcessed() * (dto.getDegradableOrganicFraction() / 100.0);
        mitigation.setDofDiverted(dofDiverted);
        
        // AvoidedLandfill = wasteProcessed * LandfillAvoidance
        Double avoidedLandfill = dto.getWasteProcessed() * dto.getLandfillAvoidance();
        mitigation.setAvoidedLandfill(avoidedLandfill);
        
        // CompostingEmissions = DOFDiverted * CompostingEF
        Double compostingEmissions = dofDiverted * dto.getCompostingEF();
        mitigation.setCompostingEmissions(compostingEmissions);
        
        // NetAnnualReduction = (AvoidedLandfill - CompostingEmissions) / 1000
        Double netAnnualReduction = (avoidedLandfill - compostingEmissions) / 1000.0;
        mitigation.setNetAnnualReduction(netAnnualReduction);
        
        // MitigationScenarioEmission = BauEmission - NetAnnualReduction
        Double mitigationScenarioEmission = bauEmissionInTonnes - netAnnualReduction;
        mitigation.setMitigationScenarioEmission(mitigationScenarioEmission);
        
        return repository.save(mitigation);
    }
    
    @Override
    public ISWMMitigation updateISWMMitigation(UUID id, ISWMMitigationDto dto) {
        ISWMMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("ISWM Mitigation record not found with id: " + id));
        
        // Convert BAU Emission to standard units (tCO₂e)
        double bauEmissionInTonnes = dto.getBauEmissionUnit().toKiloTonnesCO2e(dto.getBauEmission());
        
        // Update user inputs
        mitigation.setYear(dto.getYear());
        mitigation.setWasteProcessed(dto.getWasteProcessed());
        mitigation.setDegradableOrganicFraction(dto.getDegradableOrganicFraction());
        mitigation.setLandfillAvoidance(dto.getLandfillAvoidance());
        mitigation.setCompostingEF(dto.getCompostingEF());
        mitigation.setBauEmission(bauEmissionInTonnes);
        
        // Recalculate all derived fields
        // DOFDiverted = wasteProcessed * %DegradableOrganicFraction
        Double dofDiverted = dto.getWasteProcessed() * (dto.getDegradableOrganicFraction() / 100.0);
        mitigation.setDofDiverted(dofDiverted);
        
        // AvoidedLandfill = wasteProcessed * LandfillAvoidance
        Double avoidedLandfill = dto.getWasteProcessed() * dto.getLandfillAvoidance();
        mitigation.setAvoidedLandfill(avoidedLandfill);
        
        // CompostingEmissions = DOFDiverted * CompostingEF
        Double compostingEmissions = dofDiverted * dto.getCompostingEF();
        mitigation.setCompostingEmissions(compostingEmissions);
        
        // NetAnnualReduction = (AvoidedLandfill - CompostingEmissions) / 1000
        Double netAnnualReduction = (avoidedLandfill - compostingEmissions) / 1000.0;
        mitigation.setNetAnnualReduction(netAnnualReduction);
        
        // MitigationScenarioEmission = BauEmission - NetAnnualReduction
        Double mitigationScenarioEmission = bauEmissionInTonnes - netAnnualReduction;
        mitigation.setMitigationScenarioEmission(mitigationScenarioEmission);
        
        return repository.save(mitigation);
    }
    
    @Override
    public List<ISWMMitigation> getAllISWMMitigation(Integer year) {
        Specification<ISWMMitigation> spec = Specification.where(hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
    
    @Override
    public void deleteISWMMitigation(UUID id) {
        ISWMMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("ISWM Mitigation record not found with id: " + id));
        repository.delete(mitigation);
    }
}
