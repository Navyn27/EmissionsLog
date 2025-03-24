package com.navyn.emissionlog.Controllers.EmissionFactors;

import com.navyn.emissionlog.Models.StationaryEmissionFactors;
import com.navyn.emissionlog.Payload.Requests.EmissionFactorsDto;
import com.navyn.emissionlog.Payload.Responses.ApiResponse;
import com.navyn.emissionlog.Services.EmissionFactorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/emission-factors/stationary")
public class StationaryEmissionFactorsController {

    @Autowired
    private EmissionFactorsService emissionFactorsService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createEmissionFactors(@RequestBody EmissionFactorsDto emissionFactorsDto) {
        StationaryEmissionFactors stationaryEmissionFactors = emissionFactorsService.createEmissionFactor(emissionFactorsDto);
        return ResponseEntity.ok( new ApiResponse(true, "Emission has been created successfully", stationaryEmissionFactors));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateEmissionFactor(@PathVariable UUID id, @RequestBody EmissionFactorsDto emissionFactorsDto) {
        StationaryEmissionFactors stationaryEmissionFactors = emissionFactorsService.updateEmissionFactor(id, emissionFactorsDto);
        return ResponseEntity.ok( new ApiResponse(true, "Emission has been updated successfully", stationaryEmissionFactors));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmissionFactors(@PathVariable UUID id) {
        emissionFactorsService.deleteEmissionFactorsFactor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getEmissionFactorsById(@PathVariable UUID id) {
        StationaryEmissionFactors stationaryEmissionFactors = emissionFactorsService.getEmissionFactorsFactorById(id);
        return ResponseEntity.ok( new ApiResponse(true, "Emission fetched successfully", stationaryEmissionFactors));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllEmissionFactors() {
        List<StationaryEmissionFactors> stationaryEmissionFactorsList = emissionFactorsService.getAllEmissionFactorsFactors();
        return ResponseEntity.ok( new ApiResponse(true, "Emissions fetched successfully", stationaryEmissionFactorsList));
    }

    @GetMapping("/fuel/{fuelId}")
    public ResponseEntity<ApiResponse> getEmissionFactorsByFuelId(@PathVariable UUID fuelId) {
        StationaryEmissionFactors stationaryEmissionFactors = emissionFactorsService.getEmissionFactorsFactorByFuelId(fuelId);
        return ResponseEntity.ok( new ApiResponse(true, "Emission fetched successfully", stationaryEmissionFactors));
    }
}