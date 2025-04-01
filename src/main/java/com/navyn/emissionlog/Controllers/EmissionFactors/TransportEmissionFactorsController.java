package com.navyn.emissionlog.Controllers.EmissionFactors;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.Models.Fuel;
import com.navyn.emissionlog.Models.TransportFuelEmissionFactors;
import com.navyn.emissionlog.Payload.Requests.CreateFuelDto;
import com.navyn.emissionlog.Payload.Requests.EmissionFactors.TransportFuelEmissionFactorsDto;
import com.navyn.emissionlog.Payload.Responses.ApiResponse;
import com.navyn.emissionlog.Services.FuelService;
import com.navyn.emissionlog.Services.TransportFuelEmissionFactorsService;
import com.navyn.emissionlog.Utils.ExcelReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController(value = "TransportEmissionFactorsController")
@RequestMapping("/api/v1/emission-factors/transport")
public class TransportEmissionFactorsController {

    @Autowired
    private FuelService fuelService;

    @Autowired
    private TransportFuelEmissionFactorsService transportFuelEmissionFactorsService;

    @PostMapping("/uploadByFuel")
    public ResponseEntity<ApiResponse> uploadTransportEmissionFactorsByFuel(@RequestParam("file") MultipartFile file){
        try {
            List<TransportFuelEmissionFactorsDto> transportFuelEmissionFactorsDtos = ExcelReader.readEmissionsExcel(file.getInputStream(), TransportFuelEmissionFactorsDto.class, ExcelType.FUEL_TRANSPORT_EMISSIONS);
            for (TransportFuelEmissionFactorsDto dto : transportFuelEmissionFactorsDtos) {

                //Find fuel
                Optional<Fuel> fuel = fuelService.getExistingFuel(dto.getFuel());
                Fuel fuel1;
                if(fuel.isEmpty()){
                    CreateFuelDto fuelDto = new CreateFuelDto();
                    fuelDto.setFuel(dto.getFuel());
                    fuelDto.setFuelTypes(FuelTypes.valueOf(dto.getFuelType()));
                    fuel1 = fuelService.saveFuel(fuelDto);
                }
                else{
                    fuel1 = fuel.get();
                }

                //register Emission factors
                TransportFuelEmissionFactors transportFuelEmissionFactor = new TransportFuelEmissionFactors();
                transportFuelEmissionFactor.setFuel(fuel1);
                transportFuelEmissionFactor.setRegionGroup(RegionGroup.valueOf(dto.getRegionGroup().toUpperCase()));
                transportFuelEmissionFactor.setFossilCO2EmissionFactor(dto.getFossilCO2EmissionFactor());
                transportFuelEmissionFactor.setBiogenicCO2EmissionFactor(dto.getBiogenicCO2EmissionFactor());
                transportFuelEmissionFactor.setTransportType(dto.getTransportType()==null ? null : TransportType.valueOf(dto.getTransportType().toUpperCase().replace(' ', '_')));
                transportFuelEmissionFactor.setVehicleEngineType(dto.getVehicleEngineType()==null ? null : VehicleEngineType.valueOf(dto.getVehicleEngineType().toUpperCase().replace(' ', '_')));
                transportFuelEmissionFactor.setCH4EmissionFactor(dto.getCH4EmissionFactor());
                transportFuelEmissionFactor.setN2OEmissionFactor(dto.getN2OEmissionFactor());
                transportFuelEmissionFactor = transportFuelEmissionFactorsService.saveTransportFuelEmissionFactors(transportFuelEmissionFactor);
                fuel1.getTransportFuelEmissionFactorsList().add(transportFuelEmissionFactor);
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

    @GetMapping
    public ResponseEntity<List<TransportFuelEmissionFactors>> getAllTransportEmissionFactors() {
        List<TransportFuelEmissionFactors> transportEmissionFactors = transportFuelEmissionFactorsService.findAll();
        return ResponseEntity.ok(transportEmissionFactors);
    }
}
