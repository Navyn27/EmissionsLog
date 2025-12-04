package com.navyn.emissionlog.modules.mitigationProjects.Energy.waterheat.controller;
import com.navyn.emissionlog.modules.mitigationProjects.Energy.waterheat.models.WaterHeatParameter;
import com.navyn.emissionlog.modules.mitigationProjects.Energy.waterheat.service.WaterHeatParameterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/water-heat-parameters")
@CrossOrigin
public class WaterHeatParameterController {

    private final WaterHeatParameterService service;

    public WaterHeatParameterController(WaterHeatParameterService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<WaterHeatParameter> create(@RequestBody WaterHeatParameter param) {
        return ResponseEntity.ok(service.create(param));
    }

    @GetMapping
    public ResponseEntity<List<WaterHeatParameter>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WaterHeatParameter> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WaterHeatParameter> update(
            @PathVariable UUID id,
            @RequestBody WaterHeatParameter param) {
        return ResponseEntity.ok(service.update(id, param));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok("WaterHeatParameter deleted successfully");
    }
}
