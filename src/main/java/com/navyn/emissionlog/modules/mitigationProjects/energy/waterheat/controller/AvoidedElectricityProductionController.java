package com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.controller;

import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.dto.AvoidedElectricityProductionDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.models.AvoidedElectricityProduction;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.service.AvoidedElectricityProductionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/avoided-electricity-production")
@CrossOrigin
public class AvoidedElectricityProductionController {

    private final AvoidedElectricityProductionService service;

    public AvoidedElectricityProductionController(AvoidedElectricityProductionService service) {
        this.service = service;
    }

    // CREATE using DTO
    @PostMapping
    public ResponseEntity<AvoidedElectricityProduction> create(
            @Valid @RequestBody AvoidedElectricityProductionDTO dto) {
        return ResponseEntity.ok(service.createFromDTO(dto));
    }

    @GetMapping
    public ResponseEntity<List<AvoidedElectricityProduction>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvoidedElectricityProduction> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok("AvoidedElectricityProduction deleted successfully");
    }
}
