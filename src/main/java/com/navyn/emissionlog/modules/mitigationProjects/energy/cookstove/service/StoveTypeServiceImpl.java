package com.navyn.emissionlog.modules.mitigationProjects.Energy.cookstove.service;

import com.navyn.emissionlog.modules.mitigationProjects.Energy.cookstove.models.StoveType;
import com.navyn.emissionlog.modules.mitigationProjects.Energy.cookstove.repository.StoveTypeRepository;
import com.navyn.emissionlog.modules.mitigationProjects.Energy.cookstove.service.StoveTypeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StoveTypeServiceImpl implements StoveTypeService {

    private final StoveTypeRepository repository;

    public StoveTypeServiceImpl(StoveTypeRepository repository) {
        this.repository = repository;
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
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
