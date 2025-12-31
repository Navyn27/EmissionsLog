package com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.controller;

import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.dto.WaterHeatParameterDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.dto.WaterHeatParameterResponseDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.service.WaterHeatParameterService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/mitigation/water-heat-parameters")
@CrossOrigin
@SecurityRequirement(name = "BearerAuth")
public class WaterHeatParameterController {

    private final WaterHeatParameterService service;

    public WaterHeatParameterController(WaterHeatParameterService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<WaterHeatParameterResponseDTO> create(
            @Valid @RequestBody WaterHeatParameterDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<WaterHeatParameterResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WaterHeatParameterResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/latest")
    public ResponseEntity<WaterHeatParameterResponseDTO> getLatestActive() {
        return ResponseEntity.ok(service.getLatestActive());
    }

    @PutMapping("/{id}")
    public ResponseEntity<WaterHeatParameterResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody WaterHeatParameterDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PutMapping("/{id}/disable")
    public ResponseEntity<String> disable(@PathVariable UUID id) {
        service.disable(id);
        return ResponseEntity.ok("WaterHeatParameter disabled successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok("WaterHeatParameter deleted successfully");
    }
}
