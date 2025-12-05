package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.controller;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveType;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.service.StoveTypeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("mitigation/stove-types")
@SecurityRequirement(name = "BearerAuth")
public class StoveTypeController {

    private final StoveTypeService service;

    public StoveTypeController(StoveTypeService service) {
        this.service = service;
    }

    @PostMapping
    public StoveType create(@RequestBody StoveType stoveType) {
        return service.save(stoveType);
    }

    @GetMapping
    public List<StoveType> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoveType> getById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoveType> update(@PathVariable UUID id, @RequestBody StoveType stoveType) {
        return ResponseEntity.ok(service.update(id, stoveType));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
