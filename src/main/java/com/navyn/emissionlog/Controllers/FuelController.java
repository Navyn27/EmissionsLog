package com.navyn.emissionlog.Controllers;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.Models.Fuel;
import com.navyn.emissionlog.Models.StationaryEmissionFactors;
import com.navyn.emissionlog.Payload.Requests.CreateFuelDto;
import com.navyn.emissionlog.Payload.Requests.CreateFuelStationaryEmissionsExcelDto;
import com.navyn.emissionlog.Payload.Responses.ApiResponse;
import com.navyn.emissionlog.Payload.Responses.SupportedCalculationOptions;
import com.navyn.emissionlog.Services.FuelService;
import com.navyn.emissionlog.Services.StationaryEmissionFactorsService;
import com.navyn.emissionlog.Utils.ExcelReader;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/fuel")
@SecurityRequirement(name = "BearerAuth")
public class FuelController {
    @Autowired
    private FuelService fuelService;

    @Autowired
    private StationaryEmissionFactorsService stationaryEmissionFactorsService;

    @PostMapping
    public ResponseEntity<ApiResponse> createFuel(@RequestBody CreateFuelDto fuel) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Fuel created successfully",fuelService.saveFuel(fuel)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getFuelById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Fuel Fetched successfully",fuelService.getFuelById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllFuels() {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Fuels Fetched successfully", fuelService.getAllFuels()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateFuel(@PathVariable UUID id, @RequestBody CreateFuelDto fuel) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Fuel updated successfully", fuelService.updateFuel(id, fuel)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteFuel(@PathVariable UUID id) {
        fuelService.deleteFuel(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Fuel deleted successfully"));
    }

    @PostMapping("/uploadByStationaryEmissions")
    public ResponseEntity<ApiResponse> uploadStationaryFuelData(@RequestParam("file") MultipartFile file) {
        try {
            List<CreateFuelStationaryEmissionsExcelDto> fuelDtos = ExcelReader.readEmissionsExcel(file.getInputStream(), CreateFuelStationaryEmissionsExcelDto.class, ExcelType.FUEL_STATIONARY_EMISSIONS);
            for (CreateFuelStationaryEmissionsExcelDto fuelDto : fuelDtos) {
                CreateFuelDto createFuelDto = new CreateFuelDto();

                // Convert fuelType from String to Enum
                createFuelDto.setFuelTypes(FuelTypes.valueOf(fuelDto.getFuelType().toUpperCase().replace(' ', '_')));
                createFuelDto.setFuel(fuelDto.getFuel());
                createFuelDto.setLowerHeatingValue(fuelDto.getLowerHeatingValue());
                createFuelDto.setFuelDensityLiquids(fuelDto.getFuelDensityLiquids());
                createFuelDto.setFuelDensityGases(fuelDto.getFuelDensityGases());
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

    @GetMapping("/fuelTypes")
    public ResponseEntity<ApiResponse> getFuelsByFuelType(@RequestParam FuelTypes fuelType) {
        List<Fuel> fuels = fuelService.getFuelsByFuelType(fuelType);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Fuels fetched successfully", fuels));
    }

    @GetMapping("/stationaryEmissions/validOptions")
    public ResponseEntity<ApiResponse> getValidOptions(@RequestParam UUID fuel){
        StationaryEmissionFactors stationaryEmissionFactors = stationaryEmissionFactorsService.getStationaryEmissionFactorsByFuelId(fuel);
        SupportedCalculationOptions supportedCalculationOptions = new SupportedCalculationOptions();

        if(stationaryEmissionFactors.getLiquidBasis() != Double.valueOf(0)){
            supportedCalculationOptions.getSupportedCalculationFuelStates().add(FuelStates.LIQUID);
            supportedCalculationOptions.getSupportedCalculationMetrics().add(Metrics.VOLUME);
        }
        if(stationaryEmissionFactors.getGasBasis() != Double.valueOf(0)){
            supportedCalculationOptions.getSupportedCalculationFuelStates().add(FuelStates.GASEOUS);
            if(!supportedCalculationOptions.getSupportedCalculationMetrics().contains(Metrics.VOLUME))
                supportedCalculationOptions.getSupportedCalculationMetrics().add(Metrics.VOLUME);
        }
        if(stationaryEmissionFactors.getMassBasis() != Double.valueOf(0)){
            supportedCalculationOptions.getSupportedCalculationFuelStates().add(FuelStates.SOLID);
            supportedCalculationOptions.getSupportedCalculationMetrics().add(Metrics.MASS);
        }
        if(stationaryEmissionFactors.getEnergyBasis() != Double.valueOf(0)){
            supportedCalculationOptions.getSupportedCalculationFuelStates().add(FuelStates.ENERGY);
            supportedCalculationOptions.getSupportedCalculationMetrics().add(Metrics.ENERGY);
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Valid options fetched successfully", supportedCalculationOptions));
    }

}
