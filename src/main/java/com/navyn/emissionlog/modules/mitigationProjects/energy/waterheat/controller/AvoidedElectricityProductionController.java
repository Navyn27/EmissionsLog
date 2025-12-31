package com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.controller;

import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.dto.AvoidedElectricityProductionDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.dto.AvoidedElectricityProductionResponseDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.service.AvoidedElectricityProductionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/mitigation/avoided-electricity-production")
@SecurityRequirement(name = "BearerAuth")
public class AvoidedElectricityProductionController {

    private final AvoidedElectricityProductionService service;

    public AvoidedElectricityProductionController(AvoidedElectricityProductionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AvoidedElectricityProductionResponseDTO> create(
            @Valid @RequestBody AvoidedElectricityProductionDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvoidedElectricityProductionResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody AvoidedElectricityProductionDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<AvoidedElectricityProductionResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvoidedElectricityProductionResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<List<AvoidedElectricityProductionResponseDTO>> getByYear(@PathVariable int year) {
        return ResponseEntity.ok(service.getByYear(year));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok("AvoidedElectricityProduction deleted successfully");
    }
}
