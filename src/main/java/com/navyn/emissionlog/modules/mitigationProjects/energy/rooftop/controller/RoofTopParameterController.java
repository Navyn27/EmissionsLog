package com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.controller;

import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.dto.RoofTopParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.dto.RoofTopParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.service.IRoofTopParameterService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/mitigation/rooftop/parameters")
@CrossOrigin
@SecurityRequirement(name = "BearerAuth")
public class RoofTopParameterController {
    private final IRoofTopParameterService roofTopParameterService;

    @PostMapping
    public ResponseEntity<RoofTopParameterResponseDto> create(@Valid @RequestBody RoofTopParameterDto dto) {
        RoofTopParameterResponseDto response = roofTopParameterService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoofTopParameterResponseDto> getById(@PathVariable UUID id) {
        RoofTopParameterResponseDto response = roofTopParameterService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<RoofTopParameterResponseDto>> getAll() {
        List<RoofTopParameterResponseDto> responses = roofTopParameterService.getAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/latest")
    public ResponseEntity<RoofTopParameterResponseDto> getLatest() {
        RoofTopParameterResponseDto response = roofTopParameterService.getLatest();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoofTopParameterResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody RoofTopParameterDto dto) {
        RoofTopParameterResponseDto response = roofTopParameterService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        roofTopParameterService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
