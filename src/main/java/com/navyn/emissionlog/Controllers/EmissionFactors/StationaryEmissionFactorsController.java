package com.navyn.emissionlog.Controllers.EmissionFactors;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.Models.Fuel;
import com.navyn.emissionlog.Models.StationaryEmissionFactors;
import com.navyn.emissionlog.Payload.Requests.Fuel.CreateFuelDto;
import com.navyn.emissionlog.Payload.Requests.EmissionFactors.CreateFuelStationaryEmissionsExcelDto;
import com.navyn.emissionlog.Payload.Requests.EmissionFactors.StationaryEmissionFactorsDto;
import com.navyn.emissionlog.Payload.Responses.ApiResponse;
import com.navyn.emissionlog.Payload.Responses.SupportedCalculationOptions;
import com.navyn.emissionlog.Services.FuelService;
import com.navyn.emissionlog.Services.StationaryEmissionFactorsService;
import com.navyn.emissionlog.Utils.ExcelReader;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController(value = "StationaryEmissionFactorsController")
@RequestMapping("/emissionFactors/stationary")
public class StationaryEmissionFactorsController {

    @Autowired
    private StationaryEmissionFactorsService stationaryEmissionFactorsService;

    @Autowired
    private FuelService fuelService;

    @Operation(summary = "Create new emission factors", description="This endpoint creates new stationary emission factors. It accepts a request body containing the details of the emission factors to be created.")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createEmissionFactors(@RequestBody StationaryEmissionFactorsDto stationaryEmissionFactorsDto) {
        StationaryEmissionFactors stationaryEmissionFactors = stationaryEmissionFactorsService.createStationaryEmissionFactor(stationaryEmissionFactorsDto);
        return ResponseEntity.ok( new ApiResponse(true, "Emission has been created successfully", stationaryEmissionFactors));
    }

    @Operation(summary = "Update emission factors by ID", description="This endpoint updates stationary emission factors associated with a specific ID. It modifies the emission factors identified by the provided ID in the system.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateEmissionFactor(@PathVariable("id") UUID id, @RequestBody StationaryEmissionFactorsDto stationaryEmissionFactorsDto) {
        StationaryEmissionFactors stationaryEmissionFactors = stationaryEmissionFactorsService.updateStationaryEmissionFactor(id, stationaryEmissionFactorsDto);
        return ResponseEntity.ok( new ApiResponse(true, "Emission has been updated successfully", stationaryEmissionFactors));
    }

    @Operation(summary = "Delete emission factors by ID", description="This endpoint deletes stationary emission factors associated with a specific ID. It removes the emission factors identified by the provided ID from the system.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmissionFactors(@PathVariable("id") UUID id) {
        stationaryEmissionFactorsService.deleteStationaryEmissionFactors(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get emission factors by ID", description="This endpoint retrieves stationary emission factors associated with a specific ID. It returns the details of the emission factors identied by the provided ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getEmissionFactorsById(@PathVariable("id") UUID id) {
        StationaryEmissionFactors stationaryEmissionFactors = stationaryEmissionFactorsService.getStationaryEmissionFactorsById(id);
        return ResponseEntity.ok( new ApiResponse(true, "Emission fetched successfully", stationaryEmissionFactors));
    }

    @Operation(summary = "Get all emission factors", description="This endpoint retrieves all stationary emission factors. It returns a list of all available emission factors in the system.")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllEmissionFactors() {
        List<StationaryEmissionFactors> stationaryEmissionFactorsList = stationaryEmissionFactorsService.getAllStationaryEmissionFactors();
        return ResponseEntity.ok( new ApiResponse(true, "Emissions fetched successfully", stationaryEmissionFactorsList));
    }

    @Operation(summary = "Get emission factors by fuel ID", description="This endpoint retrieves stationary emission factors associated with a specific fuel ID. It returns a list of emission factors related to the provided fuel ID.")
    @GetMapping("/fuel/{fuelId}")
    public ResponseEntity<ApiResponse> getStationaryEmissionFactorsByFuelId(@PathVariable("fuelId") UUID fuelId) {
        List<StationaryEmissionFactors> stationaryEmissionFactors = stationaryEmissionFactorsService.getStationaryEmissionFactorsByFuelId(fuelId);
        return ResponseEntity.ok( new ApiResponse(true, "Emission fetched successfully", stationaryEmissionFactors));
    }

    @Operation(summary = "Upload fuel data from Excel file", description="This endpoint allows users to upload stationary emissions by fuel data in bulk using an Excel file. The Excel file should contain the following columns - Fuel Type, Fuel, Lower Heating Value, Fuel Density Liquids, Fuel Density Gases, Emission, Liquid Basis, Gas Basis, Mass Basis, Energy Basis.  The uploaded data is processed and saved to the database.")
    @PostMapping("/uploadByFuelExcel")
    public ResponseEntity<ApiResponse> uploadStationaryFuelData(@RequestParam("file") MultipartFile file) {
        try {
            List<CreateFuelStationaryEmissionsExcelDto> fuelDtos = ExcelReader.readExcel(file.getInputStream(), CreateFuelStationaryEmissionsExcelDto.class, ExcelType.FUEL_STATIONARY_EMISSIONS);
            for (CreateFuelStationaryEmissionsExcelDto fuelDto : fuelDtos) {
                CreateFuelDto createFuelDto = new CreateFuelDto();

                // Convert fuelType from String to Enum
                createFuelDto.setFuelTypes(FuelTypes.valueOf(fuelDto.getFuelType().toUpperCase().replace(' ', '_')));
                createFuelDto.setFuel(fuelDto.getFuel());
                createFuelDto.setLowerHeatingValue(fuelDto.getLowerHeatingValue());
                createFuelDto.setFuelDensityLiquids(fuelDto.getFuelDensityLiquids());
                createFuelDto.setFuelDensityGases(fuelDto.getFuelDensityGases());
                createFuelDto.setFuelSourceType(FuelSourceType.STATIONARY);
                createFuelDto.setFuelDescription(fuelDto.getFuelDescription());

                Fuel fuel = fuelService.saveFuel(createFuelDto);

                //register Emission factors
                StationaryEmissionFactors stationaryEmissionFactors = new StationaryEmissionFactors();
                stationaryEmissionFactors.setEmmission(Emissions.valueOf(fuelDto.getEmission()));
                stationaryEmissionFactors.setFuel(fuel);
                stationaryEmissionFactors.setLiquidBasis(fuelDto.getLiquidBasis());
                stationaryEmissionFactors.setGasBasis(fuelDto.getGasBasis());
                stationaryEmissionFactors.setMassBasis(fuelDto.getMassBasis());
                stationaryEmissionFactors.setEnergyBasis(fuelDto.getEnergyBasis());
                stationaryEmissionFactors = stationaryEmissionFactorsService.createStationaryEmissionFactorFromExcel(stationaryEmissionFactors);
                fuel.getStationaryEmissionFactorsList().add(stationaryEmissionFactors);
            }
            ApiResponse response = new ApiResponse(true, "Fuel data uploaded successfully", null);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (Exception e) {
            e.printStackTrace();
            ApiResponse response = new ApiResponse(false, "Failed to upload fuel data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Operation(summary = "Get valid options for calculation based on fuel type", description="This endpoint returns the valid options for calculation based on the fuel Id provided. It checks the stationary emission factors associated with the given fuel ID and determines the supported calculation fuel states and metrics.")
    @GetMapping("/validOptions/{fuel}")
    public ResponseEntity<ApiResponse> getValidOptions(@PathVariable("fuel") UUID fuel){
        List<StationaryEmissionFactors> stationaryEmissionFactors = stationaryEmissionFactorsService.getStationaryEmissionFactorsByFuelId(fuel);
        SupportedCalculationOptions supportedCalculationOptions = new SupportedCalculationOptions();

        for(StationaryEmissionFactors factor : stationaryEmissionFactors) {
            if (factor.getLiquidBasis() != Double.valueOf(0) && !supportedCalculationOptions.getSupportedCalculationFuelStates().contains(FuelStates.LIQUID)) {
                supportedCalculationOptions.getSupportedCalculationFuelStates().add(FuelStates.LIQUID);
                supportedCalculationOptions.getSupportedCalculationMetrics().add(Metrics.VOLUME);
            }
            if (factor.getGasBasis() != Double.valueOf(0) && !supportedCalculationOptions.getSupportedCalculationFuelStates().contains(FuelStates.GASEOUS)) {
                supportedCalculationOptions.getSupportedCalculationFuelStates().add(FuelStates.GASEOUS);
                if (!supportedCalculationOptions.getSupportedCalculationMetrics().contains(Metrics.VOLUME))
                    supportedCalculationOptions.getSupportedCalculationMetrics().add(Metrics.VOLUME);
            }
            if (factor.getMassBasis() != Double.valueOf(0) && !supportedCalculationOptions.getSupportedCalculationFuelStates().contains(FuelStates.SOLID)) {
                supportedCalculationOptions.getSupportedCalculationFuelStates().add(FuelStates.SOLID);
                supportedCalculationOptions.getSupportedCalculationMetrics().add(Metrics.MASS);
            }
            if (factor.getEnergyBasis() != Double.valueOf(0) && !supportedCalculationOptions.getSupportedCalculationFuelStates().contains(FuelStates.ENERGY)) {
                supportedCalculationOptions.getSupportedCalculationFuelStates().add(FuelStates.ENERGY);
                supportedCalculationOptions.getSupportedCalculationMetrics().add(Metrics.ENERGY);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Valid options fetched successfully", supportedCalculationOptions));
    }
}