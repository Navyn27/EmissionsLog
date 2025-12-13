package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.service;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveType;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.repository.StoveTypeRepository;
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

    @Override
    public StoveType update(UUID id, StoveType stoveType) {
        StoveType existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("StoveType not found with id: " + id));

        existing.setName(stoveType.getName());
        existing.setBaselinePercentage(stoveType.getBaselinePercentage());

        return repository.save(existing);
    }

    @Override
    public StoveType findOrCreateStoveType(String name, double baselinePercentage) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Stove type name cannot be null or empty");
        }

        // Normalize name: trim whitespace
        String normalizedName = name.trim();

        // Case-insensitive lookup
        Optional<StoveType> existing = repository.findByNameIgnoreCase(normalizedName);

        if (existing.isPresent()) {
            // Stove type exists, return it (ignore baselinePercentage parameter)
            return existing.get();
        } else {
            // Create new stove type
            StoveType newStoveType = new StoveType();
            newStoveType.setName(normalizedName); // Store original (trimmed) name
            newStoveType.setBaselinePercentage(baselinePercentage);
            return repository.save(newStoveType);
        }
    }

    @Override
    public Optional<StoveType> findByNameIgnoreCase(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }
        return repository.findByNameIgnoreCase(name.trim());
    }
}
