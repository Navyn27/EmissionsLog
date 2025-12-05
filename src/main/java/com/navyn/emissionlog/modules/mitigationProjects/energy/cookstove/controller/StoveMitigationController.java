package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.controller;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveInstallationDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveMitigationYear;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.service.StoveMitigationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/mitigation/cookstoves")
@SecurityRequirement(name = "BearerAuth")
public class StoveMitigationController {

    private final StoveMitigationService mitigationService;

    public StoveMitigationController(StoveMitigationService mitigationService) {
        this.mitigationService = mitigationService;
    }

    @PostMapping
    public StoveMitigationYear create(@RequestBody StoveInstallationDTO request) {
        return mitigationService.createMitigation(request);
    }

    @GetMapping
    public List<StoveMitigationYear> getAll() {
        return mitigationService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoveMitigationYear> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(mitigationService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoveMitigationYear> updateById(@PathVariable UUID id, @RequestBody StoveInstallationDTO request) {
        return ResponseEntity.ok(mitigationService.updateById(id, request));
    }

    @GetMapping("/stove-type/{stoveTypeId}")
    public List<StoveMitigationYear> getByStoveType(@PathVariable UUID stoveTypeId) {
        return mitigationService.findByStoveType(stoveTypeId);
    }

    @GetMapping("/year/{year}")
    public List<StoveMitigationYear> getByYear(@PathVariable int year) {
        return mitigationService.findByYear(year);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        mitigationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
