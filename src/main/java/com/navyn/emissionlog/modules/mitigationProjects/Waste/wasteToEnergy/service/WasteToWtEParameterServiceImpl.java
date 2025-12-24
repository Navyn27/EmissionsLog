package com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.dtos.WasteToWtEParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.models.WasteToWtEParameter;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.repository.WasteToWtEParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WasteToWtEParameterServiceImpl implements WasteToWtEParameterService {

    private final WasteToWtEParameterRepository repository;

    @Override
    public WasteToWtEParameter createWasteToWtEParameter(WasteToWtEParameterDto dto) {
        WasteToWtEParameter parameter = new WasteToWtEParameter();
        parameter.setNetEmissionFactor(dto.getNetEmissionFactor());
        return repository.save(parameter);
    }

    @Override
    public WasteToWtEParameter updateWasteToWtEParameter(UUID id, WasteToWtEParameterDto dto) {
        WasteToWtEParameter parameter = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Waste to WtE Parameter not found with id: " + id));

        parameter.setNetEmissionFactor(dto.getNetEmissionFactor());

        return repository.save(parameter);
    }

    @Override
    public WasteToWtEParameter getWasteToWtEParameterById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Waste to WtE Parameter not found with id: " + id));
    }

    @Override
    public List<WasteToWtEParameter> getAllWasteToWtEParameters() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Override
    public void deleteWasteToWtEParameter(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Waste to WtE Parameter not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public WasteToWtEParameter getLatestWasteToWtEParameter() {
        return repository.findFirstByOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("No Waste to WtE Parameter found. Please create a parameter first."));
    }
}

