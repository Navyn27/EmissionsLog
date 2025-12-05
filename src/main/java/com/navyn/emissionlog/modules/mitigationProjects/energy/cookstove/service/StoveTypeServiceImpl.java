package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.service;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveType;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.repository.StoveMitigationYearRepository;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.repository.StoveTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StoveTypeServiceImpl implements StoveTypeService {

    private final StoveTypeRepository repository;
    private final StoveMitigationYearRepository mitigationYearRepository;


    public StoveTypeServiceImpl(StoveTypeRepository repository, StoveMitigationYearRepository mitigationYearRepository) {
        this.repository = repository;
        this.mitigationYearRepository = mitigationYearRepository;
    }

    @Override
    public StoveType save(StoveType stoveType) {
        return repository.save(stoveType);
    }

    @Override
    public List<StoveType> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<StoveType> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        mitigationYearRepository.deleteAll(mitigationYearRepository.findByStoveTypeId(id));
        repository.deleteById(id);
    }

    @Override
    public StoveType update(UUID id, StoveType stoveType) {
        StoveType existingStoveType = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("StoveType not found with id: " + id));
        existingStoveType.setName(stoveType.getName());
        existingStoveType.setBaselinePercentage(stoveType.getBaselinePercentage());
        return repository.save(existingStoveType);
    }
}
