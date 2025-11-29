package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.controller;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveType;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.service.StoveTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stove-types")
public class StoveTypeController {

    private final StoveTypeService service;

    public StoveTypeController(StoveTypeService service) {
        this.service = service;
    }

    // Create or update stove type
    @PostMapping
    public StoveType createOrUpdate(@RequestBody StoveType stoveType) {
        return service.save(stoveType);
    }

    // Get all stove types
    @GetMapping
    public List<StoveType> getAll() {
        return service.findAll();
    }

    // Get stove type by ID
    @GetMapping("/{id}")
    public ResponseEntity<StoveType> getById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete stove type by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
