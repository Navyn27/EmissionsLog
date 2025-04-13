package com.navyn.emissionlog.Controllers.EmissionFactors;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.Models.Fuel;
import com.navyn.emissionlog.Models.TransportFuelEmissionFactors;
import com.navyn.emissionlog.Models.TransportVehicleDataEmissionFactors;
import com.navyn.emissionlog.Models.Vehicle;
import com.navyn.emissionlog.Payload.Requests.CreateFuelDto;
import com.navyn.emissionlog.Payload.Requests.EmissionFactors.TransportFuelEmissionFactorsDto;
import com.navyn.emissionlog.Payload.Requests.EmissionFactors.TransportVehicleDataEmissionFactorsDto;
import com.navyn.emissionlog.Payload.Responses.ApiResponse;
import com.navyn.emissionlog.Repositories.FuelRepository;
import com.navyn.emissionlog.Services.FuelService;
import com.navyn.emissionlog.Services.TransportFuelEmissionFactorsService;
import com.navyn.emissionlog.Services.TransportVehicleEmissionFactorsService;
import com.navyn.emissionlog.Services.VehicleService;
import com.navyn.emissionlog.Utils.ExcelReader;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController(value = "TransportEmissionFactorsController")
@RequestMapping("/emission-factors/transport")
public class TransportEmissionFactorsController {

    @Autowired
    private FuelService fuelService;

    @Autowired
    private TransportFuelEmissionFactorsService transportFuelEmissionFactorsService;

    @Autowired
    private TransportVehicleEmissionFactorsService transportVehicleEmissionService;

    @Autowired
    private FuelRepository fuelRepository;

    @Autowired
    private VehicleService vehicleService;

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
                    fuelDto.setFuelSourceType(FuelSourceType.TRANSPORT);
                    fuel1 = fuelService.saveFuel(fuelDto);
                }
                else{
                    if(fuel.get().getFuelSourceTypes().contains(FuelSourceType.TRANSPORT)){
                        fuel1 = fuel.get();
                    }
                    else {
                        fuel.get().getFuelSourceTypes().add(FuelSourceType.TRANSPORT);
                        fuel1 = fuelService.updateFuel(fuel.get());
                    }
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

    @PostMapping("/uploadByVehicleData")
    public ResponseEntity<ApiResponse> uploadTransportEmissionFactorsByVehicleData(@RequestParam("file") MultipartFile file){
        try {
            List<TransportVehicleDataEmissionFactorsDto> vehicleDataEmissionFactorsDtos = ExcelReader.readEmissionsExcel(file.getInputStream(), TransportVehicleDataEmissionFactorsDto.class, ExcelType.VEHICLE_DATA_TRANSPORT_EMISSIONS);
            for(TransportVehicleDataEmissionFactorsDto dto : vehicleDataEmissionFactorsDtos){
                TransportVehicleDataEmissionFactors transportVehicleDataEmissionFactors = new TransportVehicleDataEmissionFactors();

                //Find fuel
                Optional<Fuel> fuel = fuelService.getExistingFuel(dto.getFuel());
                Fuel fuel1;
                if(fuel.isEmpty()){
                    CreateFuelDto fuelDto = new CreateFuelDto();
                    fuelDto.setFuel(dto.getFuel());
                    fuelDto.setFuelTypes(FuelTypes.valueOf(dto.getFuelType().toUpperCase().replace(' ', '_')));
                    fuelDto.setFuelSourceType(FuelSourceType.TRANSPORT);
                    fuel1 = fuelService.saveFuel(fuelDto);
                }
                else{
                    if(fuel.get().getFuelSourceTypes().contains(FuelSourceType.TRANSPORT)){
                        fuel1 = fuel.get();
                    }
                    else {
                        fuel.get().getFuelSourceTypes().add(FuelSourceType.TRANSPORT);
                        fuel1 = fuelService.updateFuel(fuel.get());
                    }
                }

                //find Vehicle
                Optional<Vehicle> vehicle = vehicleService.getExistingVehicle(dto.getVehicle(), dto.getVehicleYear(), dto.getSize(), dto.getWeightLaden());
                Vehicle vehicle1;
                if(vehicle.isEmpty()){
                    vehicle1 = new Vehicle();
                    vehicle1.setVehicle(dto.getVehicle());
                    vehicle1.setVehicleYear(dto.getVehicleYear());
                    vehicle1.setSize(dto.getSize());
                    vehicle1.setWeightLaden(dto.getWeightLaden());
                    vehicle1 = vehicleService.createVehicle(vehicle1);
                }else{
                    vehicle1 = vehicle.get();
                }

                transportVehicleDataEmissionFactors.setVehicle(vehicle1);
                transportVehicleDataEmissionFactors.setFuel(fuel1);
                transportVehicleDataEmissionFactors.setRegionGroup(RegionGroup.valueOf(dto.getRegionGroup().toUpperCase()));
                transportVehicleDataEmissionFactors.setCO2EmissionFactor(dto.getCO2EmissionFactor());
                transportVehicleDataEmissionFactors.setCH4EmissionFactor(dto.getCH4EmissionFactor());
                transportVehicleDataEmissionFactors.setN2OEmissionFactor(dto.getN2OEmissionFactor());
                transportVehicleEmissionService.createTransportVehicleEmissionFactors(transportVehicleDataEmissionFactors);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (Exception e) {
            e.printStackTrace();
            ApiResponse response = new ApiResponse(false, "Failed to upload fuel data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        ApiResponse response = new ApiResponse(true, "Vehicle data uploaded successfully", null);
        return ResponseEntity.created(null).body(response);
    }


    @GetMapping
    public ResponseEntity<List<TransportFuelEmissionFactors>> getAllTransportEmissionFactors() {
        List<TransportFuelEmissionFactors> transportEmissionFactors = transportFuelEmissionFactorsService.findAll();
        return ResponseEntity.ok(transportEmissionFactors);
    }

    @GetMapping("/supported/metrics/fuel/{fuelId}/{regionGroup}")
    public ResponseEntity<ApiResponse> supportedMetricsForFuel(@PathVariable("fuelId") UUID fuelId, @PathVariable("regionGroup") RegionGroup regionGroup){
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

    //supported fuel states
    @GetMapping("/supported/fuelStates/fuel/{fuelId}")
    public ResponseEntity<ApiResponse> supportedFuelStatesForFuel(@PathVariable("fuelId") UUID fuelId){
        Optional<Fuel> fuel = fuelRepository.findById(fuelId);
        List<FuelStates> supportedFuelStates = new ArrayList<>();

        if(fuel.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse(false, "Fuel not found", null)
            );
        }

        String fuelName = fuel.get().getFuel();

        if(fuelName.equals("Diesel") || fuelName.equals("Motor Gasoline") || fuelName.equals("Sub-bituminous Coal")){
            supportedFuelStates.add(FuelStates.SOLID);
            supportedFuelStates.add(FuelStates.LIQUID);
            supportedFuelStates.add(FuelStates.GASEOUS);
        }
        else{
            supportedFuelStates.add(FuelStates.LIQUID);
        }
        return ResponseEntity.ok(
                new ApiResponse(true, "Supported fuels states fetched successfully", supportedFuelStates)
        );
    }

    @GetMapping("/supported/regionGroup/fuel/{fuelId}")
    public ResponseEntity<ApiResponse> supportedFuelsForRegionGroup(@PathVariable("fuelId") UUID fuelId) throws BadRequestException {
        Optional<Fuel> fuels = fuelRepository.findById(fuelId);
        List<RegionGroup> supportedRegionGroups = new ArrayList<>();

        if(fuels.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse(false, "Fuel not found", null)
            );
        }

        List<TransportFuelEmissionFactors> transportFuelEmissionFactors = transportFuelEmissionFactorsService.findByFuel(fuels.get().getId());
        for(TransportFuelEmissionFactors factor : transportFuelEmissionFactors){
            if(!supportedRegionGroups.contains(factor.getRegionGroup())){
                supportedRegionGroups.add(factor.getRegionGroup());
            }
        }

        return ResponseEntity.ok(
                new ApiResponse(true, "Supported region groups fetched successfully", supportedRegionGroups)
        );
    }

    @GetMapping("/supported/transportType/{fuelId}")
    public ResponseEntity<ApiResponse> supportedFuelsForTransportType(@PathVariable("fuelId") UUID fuelId) throws BadRequestException {
        Optional<Fuel> fuels = fuelRepository.findById(fuelId);
        List<TransportType> supportedTransportTypes = new ArrayList<>();

        if(fuels.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse(false, "Fuel not found", null)
            );
        }

        List<TransportFuelEmissionFactors> transportFuelEmissionFactors = transportFuelEmissionFactorsService.findByFuel(fuels.get().getId());
        for(TransportFuelEmissionFactors factor : transportFuelEmissionFactors){
            if(!supportedTransportTypes.contains(factor.getTransportType())){
                supportedTransportTypes.add(factor.getTransportType());
            }
        }

        return ResponseEntity.ok(
                new ApiResponse(true, "Supported transport types fetched successfully", supportedTransportTypes)
        );
    }

    @GetMapping("/supported/vehicleEngineType/{fuelId}")
    public ResponseEntity<ApiResponse> supportedFuelsForVehicleEngineType(@PathVariable("fuelId") UUID fuelId) throws BadRequestException {
       Optional<Fuel> fuels = fuelRepository.findById(fuelId);
        List<VehicleEngineType> supportedVehicleEngineTypes = new ArrayList<>();

        if(fuels.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse(false, "Fuel not found", null)
            );
        }

        List<TransportFuelEmissionFactors> transportFuelEmissionFactors = transportFuelEmissionFactorsService.findByFuel(fuels.get().getId());
        for(TransportFuelEmissionFactors factor : transportFuelEmissionFactors){
            if(!supportedVehicleEngineTypes.contains(factor.getVehicleEngineType())){
                supportedVehicleEngineTypes.add(factor.getVehicleEngineType());
            }
        }

        return ResponseEntity.ok(
                new ApiResponse(true, "Supported vehicle engine types fetched successfully", supportedVehicleEngineTypes)
        );
    }
}
