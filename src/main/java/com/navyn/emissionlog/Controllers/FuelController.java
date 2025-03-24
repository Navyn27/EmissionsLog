package com.navyn.emissionlog.Controllers;

import com.navyn.emissionlog.Enums.FuelTypes;
import com.navyn.emissionlog.Payload.Requests.CreateFuelDto;
import com.navyn.emissionlog.Payload.Requests.CreateFuelFromExcelDto;
import com.navyn.emissionlog.Payload.Responses.ApiResponse;
import com.navyn.emissionlog.Services.FuelService;
import com.navyn.emissionlog.Utils.ExcelReader;
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
public class FuelController {
    @Autowired
    private FuelService fuelService;

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

    @PostMapping("/uploadData")
    public ResponseEntity<ApiResponse> uploadFuelData(@RequestParam("file") MultipartFile file) {
        try {
            List<CreateFuelFromExcelDto> fuelDtos = ExcelReader.readExcel(file.getInputStream(), CreateFuelFromExcelDto.class);
            for (CreateFuelFromExcelDto fuelDto : fuelDtos) {
                CreateFuelDto createFuelDto = new CreateFuelDto();

                // Convert fuelType from String to Enum
                createFuelDto.setFuelTypes(FuelTypes.valueOf(fuelDto.getFuelType().toUpperCase()));

                // Map all other fields
                createFuelDto.setFuel(fuelDto.getFuel());
                createFuelDto.setLowerHeatingValue(fuelDto.getLowerHeatingValue());
                createFuelDto.setEnergyBasis(fuelDto.getEnergyBasis());
                createFuelDto.setMassBasis(fuelDto.getMassBasis());
                createFuelDto.setEmission(fuelDto.getEmission());
                createFuelDto.setFuelDensityLiquids(fuelDto.getFuelDensityLiquids());
                createFuelDto.setFuelDensityGases(fuelDto.getFuelDensityGases());
                createFuelDto.setLiquidBasis(fuelDto.getLiquidBasis());
                createFuelDto.setGasBasis(fuelDto.getGasBasis());

                // Save the fuel entry
                fuelService.saveFuel(createFuelDto);
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

}
