package com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.controller;

import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.dto.RoofTopMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.dto.RoofTopMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.service.IRoofTopMitigationService;
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
@RequestMapping("/api/v1/rooftop/mitigations")
@CrossOrigin
@SecurityRequirement(name = "BearerAuth")
public class RoofTopMitigationController {
    private final IRoofTopMitigationService roofTopMitigationService;

    @PostMapping
    public ResponseEntity<RoofTopMitigationResponseDto> create(@Valid @RequestBody RoofTopMitigationDto dto) {
        RoofTopMitigationResponseDto response = roofTopMitigationService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoofTopMitigationResponseDto> getById(@PathVariable UUID id) {
        RoofTopMitigationResponseDto response = roofTopMitigationService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<RoofTopMitigationResponseDto>> getAll() {
        List<RoofTopMitigationResponseDto> responses = roofTopMitigationService.getAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<RoofTopMitigationResponseDto> getByYear(@PathVariable int year) {
        RoofTopMitigationResponseDto response = roofTopMitigationService.getByYear(year);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoofTopMitigationResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody RoofTopMitigationDto dto) {
        RoofTopMitigationResponseDto response = roofTopMitigationService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        roofTopMitigationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
