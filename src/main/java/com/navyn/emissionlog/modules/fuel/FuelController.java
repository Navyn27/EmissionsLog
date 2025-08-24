package com.navyn.emissionlog.modules.fuel;

import com.navyn.emissionlog.Enums.Fuel.FuelTypes;
import com.navyn.emissionlog.modules.transportEmissions.models.TransportFuelEmissionFactors;
import com.navyn.emissionlog.modules.transportEmissions.models.TransportVehicleDataEmissionFactors;
import com.navyn.emissionlog.modules.fuel.dtos.CreateFuelDto;
import com.navyn.emissionlog.utils.ApiResponse;
import com.navyn.emissionlog.Services.FuelService;
import com.navyn.emissionlog.Services.TransportFuelEmissionFactorsService;
import com.navyn.emissionlog.Services.TransportVehicleEmissionFactorsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController("FuelController")
@RequestMapping(path = "/fuel")
@SecurityRequirement(name = "BearerAuth")
public class FuelController {
    @Autowired
    private FuelService fuelService;

    @Autowired
    private TransportFuelEmissionFactorsService transportFuelEmissionFactorsService;

    @Autowired
    private TransportVehicleEmissionFactorsService transportVehicleEmissionFactorsService;

    @Operation(summary = "Create a fuel without any associated emission factors", description = "Creates a fuel with the provided details.")
    @PostMapping
    public ResponseEntity<ApiResponse> createFuel(@RequestBody CreateFuelDto fuel) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Fuel created successfully",fuelService.saveFuel(fuel)));
    }

    @Operation(summary = "Get fuel identified by the provided Id")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getFuelById(@PathVariable("id") UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Fuel Fetched successfully",fuelService.getFuelById(id)));
    }

    @Operation(summary = "Get all fuels", description="Fetches all fuels available in the system.")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllFuels() {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Fuels Fetched successfully", fuelService.getAllFuels()));
    }

    @Operation(summary="Updates a fuel identified by the provided id", description="Updates the fuel with the provided details.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateFuel(@PathVariable("id") UUID id, @RequestBody CreateFuelDto fuel) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Fuel updated successfully", fuelService.updateFuel(id, fuel)));
    }

    @Operation(summary = "Delete a fuel identified by the provided id", description="Deletes the fuel identified by the provided id.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteFuel(@PathVariable("id") UUID id) {
        fuelService.deleteFuel(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Fuel deleted successfully"));
    }

    @Operation(summary = "Retrieves fuels associated with stationary emission factors and of the specified fuel type")
    @GetMapping("/fuelTypes/stationary/{fuelType}")
    public ResponseEntity<ApiResponse> getStationaryFuelsByFuelType(@PathVariable("fuelType") FuelTypes fuelType) {
        List<Fuel> fuels = fuelService.getStationaryFuelsByFuelType(fuelType);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Fuels fetched successfully", fuels));
    }

    @Operation(summary = "Retrieves fuels associated with fuel transport emission factors and of the specified fuel type")
    @GetMapping("/fuelTypes/transport/fuel/{fuelType}")
    public ResponseEntity<ApiResponse> getTransportFuelsByFuelType(@PathVariable("fuelType") FuelTypes fuelType) throws BadRequestException {
        List<Fuel> fuels = fuelService.getTransportFuelsByFuelType(fuelType);
        List<Fuel> supportedFuels = new ArrayList<>();

        for (Fuel fuel : fuels) {
            List<TransportFuelEmissionFactors> transportFuelEmissionFactors = transportFuelEmissionFactorsService.findByFuel(fuel.getId());
            if (!transportFuelEmissionFactors.isEmpty()) {
                supportedFuels.add(fuel);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Fuels fetched successfully", supportedFuels));
    }

    @Operation(summary = "Retrieves fuels associated with vehicle data emission factors and of the specified fuel type")
    @GetMapping("/fuelTypes/transport/vehicleData/{fuelType}")
    public ResponseEntity<ApiResponse> getTransportFuelsByVehicleData(@PathVariable("fuelType") FuelTypes fuelType) {
        List<Fuel> fuels = fuelService.getTransportFuelsByFuelType(fuelType);
        List<Fuel> supportedFuels = new ArrayList<>();

        for (Fuel fuel : fuels) {
            List<TransportVehicleDataEmissionFactors> transportVehicleEmissionFactors = transportVehicleEmissionFactorsService.findByFuel(fuel.getId());
            if (!transportVehicleEmissionFactors.isEmpty()) {
                supportedFuels.add(fuel);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Fuels fetched successfully", supportedFuels));
    }

    @Operation(summary = "Retrieves all fuel types", description = "Fetches all supported fuel types.")
    @GetMapping("/fuelTypes")
    public ResponseEntity<ApiResponse> getAllFuelTypes() {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Fuel Types Fetched successfully", FuelTypes.values()));
    }
}