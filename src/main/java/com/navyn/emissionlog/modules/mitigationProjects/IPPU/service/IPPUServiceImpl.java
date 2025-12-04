package com.navyn.emissionlog.modules.mitigationProjects.IPPU.service;

import com.navyn.emissionlog.modules.mitigationProjects.IPPU.dto.IPPUMitigationDTO;
import com.navyn.emissionlog.modules.mitigationProjects.IPPU.dto.IPPUMitigationResponseDTO;
import com.navyn.emissionlog.modules.mitigationProjects.IPPU.model.IPPUMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.IPPU.repository.IIPPURepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class IPPUServiceImpl implements IIPPUService {
    private final IIPPURepository iippuRepository;

    @Override
    public IPPUMitigation save(IPPUMitigationDTO ippuMitigationDTO) {
        IPPUMitigation ippuMitigation = new IPPUMitigation();
        updateIPPUMitigationFromDTO(ippuMitigation, ippuMitigationDTO);
        return iippuRepository.save(ippuMitigation);
    }

    @Override
    public IPPUMitigationResponseDTO findAll() {
        List<IPPUMitigation> mitigations = iippuRepository.findAll();
        double totalMitigationScenario = calculateTotal(mitigations, "mitigationScenario");
        double totalReducedEmissionInKtCO2e = calculateTotal(mitigations, "reducedEmissionInKtCO2e");
        return new IPPUMitigationResponseDTO(mitigations, totalMitigationScenario, totalReducedEmissionInKtCO2e);
    }

    @Override
    public Optional<IPPUMitigation> findById(UUID id) {
        return iippuRepository.findById(id);
    }

    @Override
    public void deleteById(UUID id) {
        iippuRepository.deleteById(id);
    }

    @Override
    public IPPUMitigation update(UUID id, IPPUMitigationDTO ippuMitigationDTO) {
        IPPUMitigation ippuMitigation = iippuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("IPPUMitigation not found with id: " + id));
        updateIPPUMitigationFromDTO(ippuMitigation, ippuMitigationDTO);
        return iippuRepository.save(ippuMitigation);
    }

    private void updateIPPUMitigationFromDTO(IPPUMitigation ippuMitigation, IPPUMitigationDTO ippuMitigationDTO) {
        ippuMitigation.setYear(ippuMitigationDTO.getYear());
        ippuMitigation.setBau(ippuMitigationDTO.getBau());
        ippuMitigation.setFGasName(ippuMitigationDTO.getFGasName());
        ippuMitigation.setAmountOfAvoidedFGas(ippuMitigationDTO.getAmountOfAvoidedFGas());
        ippuMitigation.setGwpFactor(ippuMitigationDTO.getGwpFactor());

        double reducedEmissionInKgCO2e = ippuMitigationDTO.getAmountOfAvoidedFGas() * ippuMitigationDTO.getGwpFactor();
        ippuMitigation.setReducedEmissionInKgCO2e(reducedEmissionInKgCO2e);

        double reducedEmissionInKtCO2e = reducedEmissionInKgCO2e / 1000000;
        ippuMitigation.setReducedEmissionInKtCO2e(reducedEmissionInKtCO2e);

        ippuMitigation.setMitigationScenario(ippuMitigationDTO.getBau() - reducedEmissionInKtCO2e);
    }

    private double calculateTotal(List<IPPUMitigation> mitigations, String field) {
        return mitigations.stream()
                .mapToDouble(m -> {
                    return switch (field) {
                        case "mitigationScenario" -> m.getMitigationScenario();
                        case "reducedEmissionInKtCO2e" -> m.getReducedEmissionInKtCO2e();
                        default -> 0.0;
                    };
                })
                .sum();
    }
}
