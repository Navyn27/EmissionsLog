package com.navyn.emissionlog.Controllers;

import com.navyn.emissionlog.Models.EmissionFactors;
import com.navyn.emissionlog.Payload.Requests.EmissionFactorsDto;
import com.navyn.emissionlog.Services.EmissionFactorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/emission-factors")
public class EmissionFactorsController {

    @Autowired
    private EmissionFactorsService emissionFactorsService;

    @PostMapping
    public ResponseEntity<EmissionFactors> createEmissionFactors(@RequestBody EmissionFactorsDto emissionFactorsDto) {
        EmissionFactors emissionFactors = emissionFactorsService.createEmissionFactorsFactor(emissionFactorsDto);
        return ResponseEntity.ok(emissionFactors);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmissionFactors> updateEmissionFactors(@PathVariable UUID id, @RequestBody EmissionFactorsDto emissionFactorsDto) {
        EmissionFactors emissionFactors = emissionFactorsService.updateEmissionFactorsFactor(id, emissionFactorsDto);
        return ResponseEntity.ok(emissionFactors);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmissionFactors(@PathVariable UUID id) {
        emissionFactorsService.deleteEmissionFactorsFactor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmissionFactors> getEmissionFactorsById(@PathVariable UUID id) {
        EmissionFactors emissionFactors = emissionFactorsService.getEmissionFactorsFactorById(id);
        return ResponseEntity.ok(emissionFactors);
    }

    @GetMapping
    public ResponseEntity<List<EmissionFactors>> getAllEmissionFactors() {
        List<EmissionFactors> emissionFactorsList = emissionFactorsService.getAllEmissionFactorsFactors();
        return ResponseEntity.ok(emissionFactorsList);
    }

    @GetMapping("/fuel/{fuelId}")
    public ResponseEntity<EmissionFactors> getEmissionFactorsByFuelId(@PathVariable UUID fuelId) {
        EmissionFactors emissionFactors = emissionFactorsService.getEmissionFactorsFactorByFuelId(fuelId);
        return ResponseEntity.ok(emissionFactors);
    }
}