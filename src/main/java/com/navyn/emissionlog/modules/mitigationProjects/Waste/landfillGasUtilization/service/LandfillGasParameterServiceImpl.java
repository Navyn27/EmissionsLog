package com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.dtos.LandfillGasParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.models.LandfillGasParameter;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.repository.LandfillGasParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LandfillGasParameterServiceImpl implements LandfillGasParameterService {

    private final LandfillGasParameterRepository repository;

    @Override
    public LandfillGasParameter createLandfillGasParameter(LandfillGasParameterDto dto) {
        LandfillGasParameter parameter = new LandfillGasParameter();
        parameter.setDestructionEfficiencyPercentage(dto.getDestructionEfficiencyPercentage());
        parameter.setGlobalWarmingPotentialCh4(dto.getGlobalWarmingPotentialCh4());
        return repository.save(parameter);
    }

    @Override
    public LandfillGasParameter updateLandfillGasParameter(UUID id, LandfillGasParameterDto dto) {
        LandfillGasParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Landfill Gas Parameter not found with id: " + id));

        parameter.setDestructionEfficiencyPercentage(dto.getDestructionEfficiencyPercentage());
        parameter.setGlobalWarmingPotentialCh4(dto.getGlobalWarmingPotentialCh4());

        return repository.save(parameter);
    }

    @Override
    public LandfillGasParameter getLandfillGasParameterById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Landfill Gas Parameter not found with id: " + id));
    }

    @Override
    public List<LandfillGasParameter> getAllLandfillGasParameters() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Override
    public void deleteLandfillGasParameter(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Landfill Gas Parameter not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public LandfillGasParameter getLatestLandfillGasParameter() {
        return repository.findFirstByOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No Landfill Gas Parameter found. Please create a parameter first."));
    }
}
