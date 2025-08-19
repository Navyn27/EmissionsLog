package com.navyn.emissionlog.modules.transportEmissions;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.modules.fuel.Fuel;
import com.navyn.emissionlog.modules.transportEmissions.models.TransportFuelEmissionFactors;
import com.navyn.emissionlog.modules.transportEmissions.models.TransportVehicleDataEmissionFactors;
import com.navyn.emissionlog.modules.vehicles.Vehicle;
import com.navyn.emissionlog.modules.fuel.dtos.CreateFuelDto;
import com.navyn.emissionlog.modules.transportEmissions.dtos.TransportFuelEmissionFactorsDto;
import com.navyn.emissionlog.modules.transportEmissions.dtos.TransportVehicleDataEmissionFactorsDto;
import com.navyn.emissionlog.utils.ApiResponse;
import com.navyn.emissionlog.modules.fuel.repositories.FuelRepository;
import com.navyn.emissionlog.Services.FuelService;
import com.navyn.emissionlog.Services.TransportFuelEmissionFactorsService;
import com.navyn.emissionlog.Services.TransportVehicleEmissionFactorsService;
import com.navyn.emissionlog.Services.VehicleService;
import com.navyn.emissionlog.utils.ExcelReader;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController(value = "TransportEmissionFactorsController")
@RequestMapping("/emissionFactors/transport")
@RequiredArgsConstructor
public class TransportEmissionFactorsController {

    private final FuelService fuelService;
    private final TransportFuelEmissionFactorsService transportFuelEmissionFactorsService;
    private final TransportVehicleEmissionFactorsService transportVehicleEmissionService;
    private final FuelRepository fuelRepository;
    private final VehicleService vehicleService;

    @Operation(summary = "Upload transport emission factors by fuel", description = "This endpoint upload transport emission factors in bulk basing on provided fuel data in an Excel File. The Excel file should contain the following columns: Fuel, Region Group, Fossil CO2 Emission Factor, Biogenic CO2 Emission Factor, Transport Type, Vehicle Engine Type, CH4 Emission Factor, N2O Emission Factor.  The uploaded data is processed and saved to the database.")
    @PostMapping("/uploadByFuelExcel")
    public ResponseEntity<ApiResponse> uploadTransportEmissionFactorsByFuel(@RequestParam("file") MultipartFile file){
        try {
            List<TransportFuelEmissionFactorsDto> transportFuelEmissionFactorsDtos = ExcelReader.readExcel(file.getInputStream(), TransportFuelEmissionFactorsDto.class, ExcelType.FUEL_TRANSPORT_EMISSIONS);
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

    @Operation(summary = "Upload transport emission factors by vehicle data", description = "This endpoint uploads tranport emission factors in bulk basing on provided vehicle data in an Excel File. The Excel file should contain the following columns: Vehicle, Vehicle Year, Size, Weight Laden, Fuel, Region Group, CO2 Emission Factor, CH4 Emission Factor, N2O Emission Factor.  The uploaded data is processed and saved to the database.")
    @PostMapping("/uploadByVehicleData")
    public ResponseEntity<ApiResponse> uploadTransportEmissionFactorsByVehicleData(@RequestParam("file") MultipartFile file){
        try {
            List<TransportVehicleDataEmissionFactorsDto> vehicleDataEmissionFactorsDtos = ExcelReader.readExcel(file.getInputStream(), TransportVehicleDataEmissionFactorsDto.class, ExcelType.VEHICLE_DATA_TRANSPORT_EMISSIONS);
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

    @Operation(summary = "Get transport emission factors by fuel", description = "This endpoint retrieves transport emission factors based on the provided fuel ID. It returns a list of transport emission factors associated with the specified fuel.")
    @GetMapping
    public ResponseEntity<List<TransportFuelEmissionFactors>> getAllTransportEmissionFactors() {
        List<TransportFuelEmissionFactors> transportEmissionFactors = transportFuelEmissionFactorsService.findAll();
        return ResponseEntity.ok(transportEmissionFactors);
    }

    @Operation(summary = "Get supported metrics on a particular fuel-regionGroup combination", description = "This endpoint retrieves the supported metrics for a specific fuel and region group combination. It returns a set of metrics that are applicable to the specified fuel and region group.")
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
        else{
            metrics.add(Metrics.VOLUME);
        }
        return ResponseEntity.ok(
                new ApiResponse(true, "Supported metrics for fuel fetched successfully", metrics)
        );
    }


    //supported fuel states
    @Operation(summary = "Get supported fuel states for a specific fuel", description = "This endpoint retrieves the supported fuel states for a specific fuel based on its ID. It returns a list of fuel states that are applicable to the specified fuel.")
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

    @Operation(summary = "Get supported region groups for a specific fuel", description = "This endpoint retrieves the supported region groups for a specific fuel based on its ID. It returns a list of region groups that are applicable to the specified fuel.")
    @GetMapping("/supported/regionGroup/fuel/{fuelId}")
    public ResponseEntity<ApiResponse> supportedRegionGroupForFuel(@PathVariable("fuelId") UUID fuelId) throws BadRequestException {
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

    @Operation(summary = "Get supported transport types for a specific fuel", description = "This endpoint retrieves the supported transport types for a specific fuel based on its ID. It returns a list of transport types that are applicable to the specified fuel.")
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

    @Operation(summary = "Get supported vehicle/engine types for a specific fuel", description = "This endpoint retrieves the supported vehicle/engine types for a specific fuel based on its ID. It returns a list of vehicle engine types that are applicable to the specified fuel.")
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

    @Operation(summary = "Get all mobile activity data types", description = "This endpoint retrieves all mobile activity data types. It returns a list of all supported mobile activity data types.")
    @GetMapping("/mobileActivityDataTypes")
    public ResponseEntity<ApiResponse> getAllMobileActivityDataTypes(){
        return ResponseEntity.ok(
                new ApiResponse(true, "Mobile activity data types fetched successfully", MobileActivityDataType.values()));
    }
}
