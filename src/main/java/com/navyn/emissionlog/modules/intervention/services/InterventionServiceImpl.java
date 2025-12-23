package com.navyn.emissionlog.modules.intervention.services;

import com.navyn.emissionlog.modules.intervention.Intervention;
import com.navyn.emissionlog.modules.intervention.dtos.InterventionDto;
import com.navyn.emissionlog.modules.intervention.repositories.InterventionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InterventionServiceImpl implements InterventionService {

    private final InterventionRepository interventionRepository;

    @Override
    @Transactional
    public Intervention createIntervention(InterventionDto dto) {
        // Normalize name: trim whitespace
        String normalizedName = dto.getName() != null ? dto.getName().trim() : null;
        
        if (normalizedName == null || normalizedName.isEmpty()) {
            throw new RuntimeException("Intervention name cannot be null or empty");
        }

        // Check if intervention with same name already exists (case-insensitive)
        Optional<Intervention> existingIntervention = interventionRepository.findByNameIgnoreCase(normalizedName);
        if (existingIntervention.isPresent()) {
            throw new RuntimeException("Intervention with name '" + normalizedName + "' already exists");
        }

        Intervention intervention = new Intervention();
        intervention.setName(normalizedName); // Store trimmed name
        return interventionRepository.save(intervention);
    }

    @Override
    @Transactional
    public Intervention updateIntervention(UUID id, InterventionDto dto) {
        // Normalize name: trim whitespace
        String normalizedName = dto.getName() != null ? dto.getName().trim() : null;
        
        if (normalizedName == null || normalizedName.isEmpty()) {
            throw new RuntimeException("Intervention name cannot be null or empty");
        }

        return interventionRepository.findById(id)
                .map(existingIntervention -> {
                    // Check if another intervention with the same name exists (case-insensitive, excluding current one)
                    Optional<Intervention> interventionWithSameName = interventionRepository.findByNameIgnoreCase(normalizedName);
                    if (interventionWithSameName.isPresent() && !interventionWithSameName.get().getId().equals(id)) {
                        throw new RuntimeException("Intervention with name '" + normalizedName + "' already exists");
                    }
                    
                    existingIntervention.setName(normalizedName); // Store trimmed name
                    return interventionRepository.save(existingIntervention);
                })
                .orElseThrow(() -> new RuntimeException("Intervention not found with id: " + id));
    }

    @Override
    public Optional<Intervention> getInterventionById(UUID id) {
        return interventionRepository.findById(id);
    }

    @Override
    public List<Intervention> getAllInterventions() {
        return interventionRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteIntervention(UUID id) {
        if (!interventionRepository.existsById(id)) {
            throw new RuntimeException("Intervention not found with id: " + id);
        }
        interventionRepository.deleteById(id);
    }
}

