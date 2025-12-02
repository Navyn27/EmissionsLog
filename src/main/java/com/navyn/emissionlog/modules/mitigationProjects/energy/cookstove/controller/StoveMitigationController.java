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
@RequestMapping("/mitigation")
@SecurityRequirement(name = "BearerAuth")
public class StoveMitigationController {

    private final StoveMitigationService mitigationService;

    public StoveMitigationController(StoveMitigationService mitigationService) {
        this.mitigationService = mitigationService;
    }

    /**
     * Create a new mitigation record based on user input.
     *
     * User provides: year, unitsInstalledThisYear, stoveTypeId, bau.
     */
    @PostMapping
    public StoveMitigationYear create(@RequestBody StoveInstallationDTO request) {
        return mitigationService.createMitigation(request);
    }

    /**
     * Get all mitigation records.
     */
    @GetMapping
    public List<StoveMitigationYear> getAll() {
        return mitigationService.findAll();
    }

    /**
     * Get a mitigation record by its id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StoveMitigationYear> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(mitigationService.findById(id));
    }

    /**
     * Get mitigation records by stove type.
     */
    @GetMapping("/stove-type/{stoveTypeId}")
    public List<StoveMitigationYear> getByStoveType(@PathVariable UUID stoveTypeId) {
        return mitigationService.findByStoveType(stoveTypeId);
    }

    /**
     * Get mitigation records by year.
     */
    @GetMapping("/year/{year}")
    public List<StoveMitigationYear> getByYear(@PathVariable int year) {
        return mitigationService.findByYear(year);
    }

    /**
     * Delete a mitigation record.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        mitigationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
