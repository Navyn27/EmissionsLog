package com.navyn.emissionlog.Controllers;

import com.navyn.emissionlog.Models.EmissionFactors;
import com.navyn.emissionlog.Payload.Requests.EmissionFactorsDto;
import com.navyn.emissionlog.Payload.Responses.ApiResponse;
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
    public ResponseEntity<ApiResponse> createEmissionFactors(@RequestBody EmissionFactorsDto emissionFactorsDto) {
        EmissionFactors emissionFactors = emissionFactorsService.createEmissionFactor(emissionFactorsDto);
        return ResponseEntity.ok( new ApiResponse(true, "Emission has been created successfully", emissionFactors));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateEmissionFactor(@PathVariable UUID id, @RequestBody EmissionFactorsDto emissionFactorsDto) {
        EmissionFactors emissionFactors = emissionFactorsService.updateEmissionFactor(id, emissionFactorsDto);
        return ResponseEntity.ok( new ApiResponse(true, "Emission has been updated successfully", emissionFactors));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmissionFactors(@PathVariable UUID id) {
        emissionFactorsService.deleteEmissionFactorsFactor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getEmissionFactorsById(@PathVariable UUID id) {
        EmissionFactors emissionFactors = emissionFactorsService.getEmissionFactorsFactorById(id);
        return ResponseEntity.ok( new ApiResponse(true, "Emission fetched successfully", emissionFactors));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllEmissionFactors() {
        List<EmissionFactors> emissionFactorsList = emissionFactorsService.getAllEmissionFactorsFactors();
        return ResponseEntity.ok( new ApiResponse(true, "Emissions fetched successfully", emissionFactorsList));
    }

    @GetMapping("/fuel/{fuelId}")
    public ResponseEntity<ApiResponse> getEmissionFactorsByFuelId(@PathVariable UUID fuelId) {
        EmissionFactors emissionFactors = emissionFactorsService.getEmissionFactorsFactorByFuelId(fuelId);
        return ResponseEntity.ok( new ApiResponse(true, "Emission fetched successfully", emissionFactors));
    }
}