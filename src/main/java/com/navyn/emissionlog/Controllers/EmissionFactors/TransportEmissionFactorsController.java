package com.navyn.emissionlog.Controllers.EmissionFactors;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.Models.Fuel;
import com.navyn.emissionlog.Models.TransportFuelEmissionFactors;
import com.navyn.emissionlog.Payload.Requests.CreateFuelDto;
import com.navyn.emissionlog.Payload.Requests.EmissionFactors.TransportFuelEmissionFactorsDto;
import com.navyn.emissionlog.Payload.Responses.ApiResponse;
import com.navyn.emissionlog.Repositories.FuelRepository;
import com.navyn.emissionlog.Services.FuelService;
import com.navyn.emissionlog.Services.TransportFuelEmissionFactorsService;
import com.navyn.emissionlog.Utils.ExcelReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController(value = "TransportEmissionFactorsController")
@RequestMapping("/api/v1/emission-factors/transport")
public class TransportEmissionFactorsController {

    @Autowired
    private FuelService fuelService;

    @Autowired
    private TransportFuelEmissionFactorsService transportFuelEmissionFactorsService;
    @Autowired
    private FuelRepository fuelRepository;

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

    @GetMapping("/supported/metrics/fuel/{fuelId}/{regionGroup}")
    public ResponseEntity<ApiResponse> supportedMetricsForFuel(@RequestParam UUID fuelId, @RequestParam RegionGroup regionGroup){
        HashSet<Metrics> metrics = new HashSet<>();
        Optional<Fuel> fuel = fuelRepository.findById(fuelId);

        if(fuel.isEmpty()){
            throw new IllegalArgumentException("Fuel not found");
        }
        if(regionGroup == RegionGroup.OTHER){
            if(fuel.get().getFuel().equals("Diesel") || fuel.get().getFuel().equals("Motor Gasoline") || fuel.get().getFuel().equals("Sub-bituminous Coal")){
                metrics.add(Metrics.MASS);
            }
            else{
                metrics.add(Metrics.VOLUME);
            }
        }
        return ResponseEntity.ok(
                new ApiResponse(true, "Supported metrics for fuel fetched successfully", metrics)
        );
    }

    @GetMapping("/supported/regionGroup/fuel/{regionGroup}")
    public ResponseEntity<ApiResponse> supportedFuelsForRegionGroup(@RequestParam RegionGroup regionGroup){
        List<Fuel> fuels = transportFuelEmissionFactorsService.findAllFuelsByRegionGroup(regionGroup);
        return ResponseEntity.ok(
                new ApiResponse(true, "Supported fuels for region group fetched successfully", fuels)
        );
    }

    @GetMapping("/supported/regionGroup/transportType/{transportType}")
    public ResponseEntity<ApiResponse> supportedFuelsForTransportType(@RequestParam TransportType transportType){
        List<Fuel> fuels = transportFuelEmissionFactorsService.findAllFuelsByTransportType(transportType);
        return ResponseEntity.ok(new ApiResponse(true, "Supported fuels for transport type fetched successfully", fuels));
    }

    @GetMapping("/supported/regionGroup/vehicleEngineType/{vehicleEngineType}")
    public ResponseEntity<ApiResponse> supportedFuelsForVehicleEngineType(@RequestParam VehicleEngineType vehicleEngineType){
        List<Fuel> fuels = transportFuelEmissionFactorsService.findAllFuelsByVehicleEngineType(vehicleEngineType);
        return ResponseEntity.ok(new ApiResponse(true, "Supported fuels for vehicle engine type fetched successfully", fuels));
    }
}
