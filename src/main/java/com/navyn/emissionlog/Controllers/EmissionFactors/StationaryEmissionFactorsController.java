package com.navyn.emissionlog.Controllers.EmissionFactors;

import com.navyn.emissionlog.Models.StationaryEmissionFactors;
import com.navyn.emissionlog.Payload.Requests.EmissionFactors.StationaryEmissionFactorsDto;
import com.navyn.emissionlog.Payload.Responses.ApiResponse;
import com.navyn.emissionlog.Services.StationaryEmissionFactorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/emission-factors/stationary")
public class StationaryEmissionFactorsController {

    @Autowired
    private StationaryEmissionFactorsService stationaryEmissionFactorsService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createEmissionFactors(@RequestBody StationaryEmissionFactorsDto stationaryEmissionFactorsDto) {
        StationaryEmissionFactors stationaryEmissionFactors = stationaryEmissionFactorsService.createStationaryEmissionFactor(stationaryEmissionFactorsDto);
        return ResponseEntity.ok( new ApiResponse(true, "Emission has been created successfully", stationaryEmissionFactors));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateEmissionFactor(@PathVariable UUID id, @RequestBody StationaryEmissionFactorsDto stationaryEmissionFactorsDto) {
        StationaryEmissionFactors stationaryEmissionFactors = stationaryEmissionFactorsService.updateStationaryEmissionFactor(id, stationaryEmissionFactorsDto);
        return ResponseEntity.ok( new ApiResponse(true, "Emission has been updated successfully", stationaryEmissionFactors));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmissionFactors(@PathVariable UUID id) {
        stationaryEmissionFactorsService.deleteStationaryEmissionFactors(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getEmissionFactorsById(@PathVariable UUID id) {
        StationaryEmissionFactors stationaryEmissionFactors = stationaryEmissionFactorsService.getStationaryEmissionFactorsById(id);
        return ResponseEntity.ok( new ApiResponse(true, "Emission fetched successfully", stationaryEmissionFactors));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllEmissionFactors() {
        List<StationaryEmissionFactors> stationaryEmissionFactorsList = stationaryEmissionFactorsService.getAllStationaryEmissionFactors();
        return ResponseEntity.ok( new ApiResponse(true, "Emissions fetched successfully", stationaryEmissionFactorsList));
    }

    @GetMapping("/fuel/{fuelId}")
    public ResponseEntity<ApiResponse> getStationaryEmissionFactorsByFuelId(@PathVariable UUID fuelId) {
        List<StationaryEmissionFactors> stationaryEmissionFactors = stationaryEmissionFactorsService.getStationaryEmissionFactorsByFuelId(fuelId);
        return ResponseEntity.ok( new ApiResponse(true, "Emission fetched successfully", stationaryEmissionFactors));
    }
}