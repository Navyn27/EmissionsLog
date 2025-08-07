package com.navyn.emissionlog.ServiceImpls;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.Models.*;
import com.navyn.emissionlog.Models.ActivityData.*;
import com.navyn.emissionlog.Models.Agriculture.*;
import com.navyn.emissionlog.Models.WasteData.WasteDataAbstract;
import com.navyn.emissionlog.Payload.Requests.Activity.CreateTransportActivityByFuelDto;
import com.navyn.emissionlog.Payload.Requests.Activity.CreateTransportActivityByVehicleDataDto;
import com.navyn.emissionlog.Payload.Requests.Activity.CreateStationaryActivityDto;
import com.navyn.emissionlog.Payload.Responses.DashboardData;
import com.navyn.emissionlog.Repositories.*;
import com.navyn.emissionlog.Repositories.Agriculture.*;
import com.navyn.emissionlog.ServiceImpls.EmissionCalculation.StationaryEmissionCalculationServiceImpl;
import com.navyn.emissionlog.ServiceImpls.EmissionCalculation.TransportEmissionCalculationServiceImpl;
import com.navyn.emissionlog.Services.ActivityService;
import com.navyn.emissionlog.Services.TransportFuelEmissionFactorsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final FuelRepository fuelRepository;
    private final ActivityDataRepository activityDataRepository;
    private final FuelDataRepository fuelDataRepository;
    private final StationaryEmissionCalculationServiceImpl stationaryEmissionCalculationService;
    private final RegionRepository regionRepository;
    private final TransportEmissionCalculationServiceImpl transportEmissionCalculationService;
    private final TransportFuelEmissionFactorsService transportFuelEmissionFactorsService;
    private final VehicleRepository vehicleRepository;
    private final VehicleDataRepository vehicleDataRepository;
    private final WasteDataRepository wasteDataAbstractRepository;
    private final AquacultureEmissionsRepository aquacultureEmissionsRepository;
    private final EntericFermentationEmissionsRepository entericFermentationEmissionsRepository;
    private final LimingEmissionsRepository limingEmissionsRepository;
    private final ManureMgmtEmissionsRepository manureMgmtEmissionsRepository;
    private final RiceCultivationEmissionsRepository riceCultivationEmissionsRepository;
    private final SyntheticFertilizerEmissionsRepository syntheticFertilizerEmissionsRepository;
    private final UreaEmissionsRepository ureaEmissionsRepository;

    @Override
    public Activity createStationaryActivity(CreateStationaryActivityDto activity) {
        try {
            Optional<Fuel> fuel = fuelRepository.findById(activity.getFuel());

            if (fuel.isEmpty()) {
                throw new IllegalArgumentException("Fuel is not recorded");
            }

            //Create FuelData
            FuelData fuelData = createFuelData(activity, fuel.get());

            //Create ActivityData
            ActivityData stationaryActivityData = new StationaryActivityData();
            stationaryActivityData.setActivityType(ActivityTypes.STATIONARY);
            stationaryActivityData.setFuelData(fuelData);
            stationaryActivityData = activityDataRepository.save(stationaryActivityData);

            //Create Activity
            Activity activity1 = new Activity();
            activity1.setSector(activity.getSector());
            activity1.setScope(Scopes.SCOPE_1);
            activity1.setRegion(regionRepository.findById(activity.getRegion()).get());
            activity1.setActivityData(stationaryActivityData);
            activity1.setActivityYear(activity.getActivityYear());
            activity1.setActivityData(stationaryActivityData);

            //calculate emissions
            stationaryEmissionCalculationService.calculateEmissions(fuel.get(), activity1, fuelData, activity.getFuelUnit(), activity.getFuelAmount());

            return activityRepository.save(activity1);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating stationary activity");
        }
    }

    @Override
    public void deleteActivity(UUID id) {
        activityRepository.deleteById(id);
    }

    @Override
    public Activity getActivityById(UUID id) {
        return activityRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Activity not found"));
    }

    @Override
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    @Override
    public Activity createTransportActivityByFuel(CreateTransportActivityByFuelDto activityDto) {
        // Validate fuel exists
        Optional<Fuel> fuel = fuelRepository.findById(activityDto.getFuel());
        if(fuel.isEmpty()){
            throw new IllegalArgumentException("Fuel is not recorded");
        }

        // Create FuelData
        FuelData fuelData = createFuelData(activityDto, fuel.get());

        // Create ActivityData
        TransportActivityData transportActivityData = new TransportActivityData();
        transportActivityData.setActivityType(ActivityTypes.TRANSPORT);
        transportActivityData.setFuelData(fuelData);
        transportActivityData.setModeOfTransport(activityDto.getTransportMode());
        transportActivityData.setTransportType(activityDto.getTransportType());
        transportActivityData = activityDataRepository.save(transportActivityData);

        // Create Activity
        Activity activity = new Activity();
        activity.setSector(activityDto.getSector());
        activity.setScope(activityDto.getScope());
        activity.setRegion(regionRepository.findById(activityDto.getRegion()).get());
        activity.setActivityData(transportActivityData);
        activity.setActivityYear(activityDto.getActivityYear());

        //find the emissions factors
        Optional<TransportFuelEmissionFactors> transportEmissionFactorsList = transportFuelEmissionFactorsService.findByFuelAndRegionGroupAndTransportTypeAndVehicleEngineType(fuel.get(), activityDto.getRegionGroup(), activityDto.getTransportType(), activityDto.getVehicleType());

        if(transportEmissionFactorsList.isEmpty()){
            throw new IllegalArgumentException("Transport Emission Factors not found for specified region");
        }

        // Calculate emissions
        transportEmissionCalculationService.calculateEmissionsByFuel(transportEmissionFactorsList.get(), fuel.get(), activity, fuelData,
             activityDto.getFuelUnit(), activityDto.getFuelAmount());

        return activityRepository.save(activity);
    }

    @Override
    public Activity createTransportActivityByVehicleData(CreateTransportActivityByVehicleDataDto activityDto) {
        // Validate fuel exists
        Optional<Fuel> fuel = fuelRepository.findById(activityDto.getFuel());
        if(fuel.isEmpty()){
            throw new IllegalArgumentException("Fuel is not recorded");
        }

        Optional<Vehicle> vehicle = vehicleRepository.findById(activityDto.getVehicle());
        if(vehicle.isEmpty()){
            throw new IllegalArgumentException("Vehicle is not recorded");
        }

        // Create Vehicle Data
        VehicleData vehicleData = createTransportVehicleData(activityDto,vehicle.get());

        //Create Fuel Data
        FuelData fuelData = createFuelData(activityDto, fuel.get());

        // Create ActivityData
        TransportActivityData transportActivityData = new TransportActivityData();
        transportActivityData.setActivityType(ActivityTypes.TRANSPORT);
        transportActivityData.setFuelData(fuelData);
        transportActivityData.setModeOfTransport(activityDto.getTransportMode());
        transportActivityData.setTransportType(activityDto.getTransportType());
        transportActivityData.setVehicleData(vehicleData);
        transportActivityData = activityDataRepository.save(transportActivityData);

        // Create Activity
        Activity activity = new Activity();
        activity.setSector(activityDto.getSector());
        activity.setScope(activityDto.getScope());
        activity.setRegion(regionRepository.findById(activityDto.getRegion()).get());
        activity.setActivityData(transportActivityData);
        activity.setActivityYear(activityDto.getActivityYear());

        // Calculate emissions
        if(activityDto.getMobileActivityDataType() == MobileActivityDataType.VEHICLE_DISTANCE) {
            transportEmissionCalculationService.calculateEmissionsByVehicleData(activity, vehicleData, fuel.get(), activityDto.getRegionGroup(), activityDto.getMobileActivityDataType());
        }
        else{
            Optional<TransportFuelEmissionFactors> transportEmissionFactorsList = transportFuelEmissionFactorsService.findByFuelAndRegionGroupAndTransportTypeAndVehicleEngineType(fuel.get(), activityDto.getRegionGroup(), activityDto.getTransportType(), activityDto.getVehicleType());

            if(transportEmissionFactorsList.isEmpty()){
                throw new IllegalArgumentException("Transport Emission Factors not found for specified region");
            }
            transportEmissionCalculationService.calculateEmissionsByFuel(transportEmissionFactorsList.get(), fuel.get(), activity, fuelData,
                    activityDto.getFuelUnit(), activityDto.getFuelAmount());
            transportEmissionCalculationService.calculateEmissionsByVehicleData(activity, vehicleData, fuel.get(), activityDto.getRegionGroup(), activityDto.getMobileActivityDataType());
        }

        return activityRepository.save(activity);
    }

    @Override
    public List<Activity> getStationaryActivities() {
        return activityRepository.findByActivityData_ActivityType(ActivityTypes.STATIONARY);
    }

    @Override
    public List<Activity> getTransportActivities() {
        return activityRepository.findByActivityData_ActivityType(ActivityTypes.TRANSPORT);
    }

    @Override
    public DashboardData getDashboardData() {
        try {
            List<Activity> activities = activityRepository.findAll();
            List<WasteDataAbstract> wasteActivities = wasteDataAbstractRepository.findAll();
            List<AquacultureEmissions> aquacultureEmissions = aquacultureEmissionsRepository.findAll();
            List<EntericFermentationEmissions> entericFermentationEmissions = entericFermentationEmissionsRepository.findAll();
            List<LimingEmissions> limingEmissions = limingEmissionsRepository.findAll();
            List<ManureMgmtEmissions> manureMgmtEmissions = manureMgmtEmissionsRepository.findAll();
            List<RiceCultivationEmissions> riceCultivationEmissions = riceCultivationEmissionsRepository.findAll();
            List<SyntheticFertilizerEmissions> syntheticFertilizerEmissions = syntheticFertilizerEmissionsRepository.findAll();
            List<UreaEmissions> ureaEmissions = ureaEmissionsRepository.findAll();
            return calculateDashboardData(activities, wasteActivities,
                    aquacultureEmissions, entericFermentationEmissions, limingEmissions,
                    manureMgmtEmissions, riceCultivationEmissions, syntheticFertilizerEmissions, ureaEmissions);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public DashboardData getDashboardData(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            List<Activity> activities = activityRepository.findByActivityYearBetween(startDate, endDate);
            List<WasteDataAbstract> wasteActivities = wasteDataAbstractRepository.findByActivityYearBetween(startDate, endDate);
            List<AquacultureEmissions> aquacultureEmissions = aquacultureEmissionsRepository.findByYearRange(startDate.getYear(), endDate.getYear());
            List<EntericFermentationEmissions> entericFermentationEmissions = entericFermentationEmissionsRepository.findByYearRange(startDate.getYear(), endDate.getYear());
            List<LimingEmissions> limingEmissions = limingEmissionsRepository.findByYearRange(startDate.getYear(), endDate.getYear());
            List<ManureMgmtEmissions> manureMgmtEmissions = manureMgmtEmissionsRepository.findByYearRange(startDate.getYear(), endDate.getYear());
            List<RiceCultivationEmissions> riceCultivationEmissions = riceCultivationEmissionsRepository.findByYearRange(startDate.getYear(), endDate.getYear());
            List<SyntheticFertilizerEmissions> syntheticFertilizerEmissions = syntheticFertilizerEmissionsRepository.findByYearRange(startDate.getYear(), endDate.getYear());
            List<UreaEmissions> ureaEmissions = ureaEmissionsRepository.findByYearRange(startDate.getYear(), endDate.getYear());
            DashboardData dashboardData = calculateDashboardData(activities, wasteActivities,
                    aquacultureEmissions, entericFermentationEmissions, limingEmissions,
                    manureMgmtEmissions, riceCultivationEmissions, syntheticFertilizerEmissions, ureaEmissions);
            dashboardData.setYear(startDate.getYear());
            dashboardData.setIsoDate(startDate.toString());
            return dashboardData;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<DashboardData> getDashboardGraphData(Integer year) {
        List<Activity> activities;
        if(year==0){
            activities = activityRepository.findAll();
        }
        else {
            LocalDateTime startDate = LocalDateTime.of(year, 1, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(year, 12, 31, 23, 59);
            activities = activityRepository.findByActivityYearBetween(startDate, endDate);
        }

        // Group activities by year and month
        Map<YearMonth, List<Activity>> activitiesByYearMonth = activities.stream()
                .collect(Collectors.groupingBy(activity ->
                        YearMonth.from(activity.getActivityYear())));
//        Map<YearMonth, List<WasteDataAbstract>> wasteDataByYearMonth = wasteDataAbstractRepository.findByActivityYearBetween(
//                LocalDateTime.of(year, 1, 1, 0, 0),
//                LocalDateTime.of(year, 12, 31, 23, 59)
//        ).stream().collect(Collectors.groupingBy(waste ->
//                YearMonth.from(waste.getActivityYear())));

        //Add waste emissions


        //Add Agriculture emissions

        //


        //Add


        // Create aggregated dashboard data for each month
        List<DashboardData> dashboardDataList = new ArrayList<>();

        for (Map.Entry<YearMonth, List<Activity>> entry : activitiesByYearMonth.entrySet()) {
            YearMonth yearMonth = entry.getKey();
            List<Activity> monthlyActivities = entry.getValue();

            DashboardData data = calculateDashboardActivityData(monthlyActivities);
            if(yearMonth.getMonth().getValue() == 12) {
                 // Increment year for December
            } else {
                data.setYear(yearMonth.getYear());
            }

            // Store period information (you may need to add a field to DashboardData class)
            data.setMonth(yearMonth.getMonth());
            data.setYear(yearMonth.getYear());// Assuming there's a period field
            dashboardDataList.add(data);
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        // Sort by year and month
        dashboardDataList.sort(Comparator.comparing(data ->
                YearMonth.of(data.getYear(), data.getMonth().getValue())
        ));
        return dashboardDataList;
    }

    @Override
    public List<Activity> getStationaryEmissionsFilteredData(Sectors sectors, LocalDate year, FuelTypes fuelTypes, UUID fuel) {
        // First get stationary activities
        List<Activity> activities = activityRepository.findByActivityData_ActivityType(ActivityTypes.STATIONARY);

        // Filter by sector if provided
        if (sectors != null) {
            activities = activities.stream()
                    .filter(activity -> activity.getSector() == sectors)
                    .collect(Collectors.toList());
        }

        // Filter by fuel ID if provided
        if (fuel != null) {
            activities = activities.stream()
                    .filter(activity -> {
                        FuelData fuelData = activity.getActivityData().getFuelData();
                        return fuelData != null && fuelData.getFuel().getId().equals(fuel);
                    })
                    .collect(Collectors.toList());
        }

        // Filter by fuel type if provided
        if (fuelTypes != null) {
            activities = activities.stream()
                    .filter(activity -> {
                        FuelData fuelData = activity.getActivityData().getFuelData();
                        return fuelData != null && fuelData.getFuel().getFuelTypes() == fuelTypes;
                    })
                    .collect(Collectors.toList());
        }

        // Filter by year if provided
        if (year != null) {
            LocalDateTime startDate = year.atStartOfDay();
            LocalDateTime endDate = year.plusYears(1).atStartOfDay();
            activities = activities.stream()
                    .filter(activity -> {
                        LocalDateTime activityDate = activity.getActivityYear();
                        return activityDate.isAfter(startDate) && activityDate.isBefore(endDate);
                    })
                    .collect(Collectors.toList());
        }
        return activities;
    }

    @Override
    public List<Activity> getTransportEmissionsFilteredData(TransportModes transportMode, UUID region, TransportType transportType, UUID fuel, FuelTypes fuelType, UUID vehicle, Scopes scope) {
        // First get transport activities
        List<Activity> activities = activityRepository.findByActivityData_ActivityType(ActivityTypes.TRANSPORT);

        // Filter by transport mode if provided
        if (transportMode != null) {
            activities = activities.stream()
                    .filter(activity -> activity.getActivityData() instanceof TransportActivityData)
                    .filter(activity -> ((TransportActivityData) activity.getActivityData()).getModeOfTransport() == transportMode)
                    .collect(Collectors.toList());
        }

        // Filter by region if provided
        if (region != null) {
            activities = activities.stream()
                    .filter(activity -> activity.getRegion().getId().equals(region))
                    .collect(Collectors.toList());
        }

        // Filter by vehicle type if provided
        if (vehicle != null) {
            activities = activities.stream()
                    .filter(activity -> activity.getActivityData() instanceof TransportActivityData)
                    .filter(activity -> ((TransportActivityData) activity.getActivityData()).getVehicleData().getVehicle().getId() == vehicle)
                    .collect(Collectors.toList());
        }

        // Filter by transport type if provided
        if (transportType != null) {
            activities = activities.stream()
                    .filter(activity -> activity.getActivityData() instanceof TransportActivityData)
                    .filter(activity -> ((TransportActivityData) activity.getActivityData()).getTransportType() == transportType)
                    .collect(Collectors.toList());
        }

        // Filter by fuel ID if provided
        if (fuel != null) {
            activities = activities.stream()
                    .filter(activity -> {
                        FuelData fuelData = activity.getActivityData().getFuelData();
                        return fuelData != null && fuelData.getFuel().getId().equals(fuel);
                    })
                    .collect(Collectors.toList());
        }

        // Filter by fuel type if provided
        if (fuelType != null) {
            activities = activities.stream()
                    .filter(activity -> {
                        FuelData fuelData = activity.getActivityData().getFuelData();
                        return fuelData != null && fuelData.getFuel().getFuelTypes() == fuelType;
                    })
                    .collect(Collectors.toList());
        }

        // Filter by scope if provided
        if (scope != null) {
            activities = activities.stream()
                    .filter(activity -> activity.getScope() == scope)
                    .collect(Collectors.toList());
        }
        return activities;
    }

    private DashboardData calculateDashboardData(List<Activity> activities, List<WasteDataAbstract> wasteData, List<AquacultureEmissions> aquacultureEmissions, List<EntericFermentationEmissions> entericFermentationEmissions, List<LimingEmissions> limingEmissions, List<ManureMgmtEmissions> manureMgmtEmissions, List<RiceCultivationEmissions> riceCultivationEmissions, List<SyntheticFertilizerEmissions> syntheticFertilizerEmissions, List<UreaEmissions> ureaEmissions) {
        DashboardData dashboardData = new DashboardData();
        for(Activity activity : activities){
            dashboardData.setTotalCH4Emissions(dashboardData.getTotalCH4Emissions() + activity.getCH4Emissions());
            dashboardData.setTotalN2OEmissions(dashboardData.getTotalN2OEmissions() + activity.getN2OEmissions());
            dashboardData.setTotalFossilCO2Emissions(dashboardData.getTotalFossilCO2Emissions() + activity.getFossilCO2Emissions());
            dashboardData.setTotalBioCO2Emissions(dashboardData.getTotalBioCO2Emissions() + activity.getBioCO2Emissions());
        }
        for (WasteDataAbstract waste : wasteData) {
            dashboardData.setTotalCH4Emissions(dashboardData.getTotalCH4Emissions() + waste.getCH4Emissions());
            dashboardData.setTotalN2OEmissions(dashboardData.getTotalN2OEmissions() + waste.getN2OEmissions());
            dashboardData.setTotalFossilCO2Emissions(dashboardData.getTotalFossilCO2Emissions() + waste.getFossilCO2Emissions());
            dashboardData.setTotalBioCO2Emissions(dashboardData.getTotalBioCO2Emissions() + waste.getBioCO2Emissions());
        }
        for (AquacultureEmissions aquacultureEmission : aquacultureEmissions) {
            dashboardData.setTotalN2OEmissions(dashboardData.getTotalN2OEmissions() + aquacultureEmission.getN2OEmissions());
        }
        for (EntericFermentationEmissions entericEmission : entericFermentationEmissions) {
            dashboardData.setTotalCH4Emissions(dashboardData.getTotalCH4Emissions() + entericEmission.getCH4Emissions());
        }
        for (LimingEmissions limingEmission : limingEmissions) {
            dashboardData.setTotalBioCO2Emissions(dashboardData.getTotalBioCO2Emissions() + limingEmission.getCO2Emissions());
        }
        for (ManureMgmtEmissions manureEmission : manureMgmtEmissions) {
            dashboardData.setTotalCH4Emissions(dashboardData.getTotalCH4Emissions() + manureEmission.getCH4Emissions());
            dashboardData.setTotalN2OEmissions(dashboardData.getTotalN2OEmissions() + manureEmission.getN2OEmissions());
        }
        for (RiceCultivationEmissions riceEmission : riceCultivationEmissions) {
            dashboardData.setTotalCH4Emissions(dashboardData.getTotalCH4Emissions() + riceEmission.getAnnualCH4Emissions());
        }
        for (SyntheticFertilizerEmissions syntheticFertilizerEmission : syntheticFertilizerEmissions) {
            dashboardData.setTotalN2OEmissions(dashboardData.getTotalN2OEmissions() + syntheticFertilizerEmission.getN2OEmissions());
        }
        for (UreaEmissions ureaEmission : ureaEmissions) {
            dashboardData.setTotalN2OEmissions(dashboardData.getTotalBioCO2Emissions() + ureaEmission.getCO2Emissions());
        }
        dashboardData.setTotalCO2EqEmissions(dashboardData.getTotalFossilCO2Emissions() + dashboardData.getTotalBioCO2Emissions() + dashboardData.getTotalCH4Emissions()*GWP.CH4.getValue() + dashboardData.getTotalN2OEmissions()*GWP.N2O.getValue());
        return dashboardData;
    }

    private DashboardData calculateDashboardActivityData(List<Activity> activities) {
        DashboardData dashboardData = new DashboardData();
        for(Activity activity : activities){
            dashboardData.setTotalCH4Emissions(dashboardData.getTotalCH4Emissions() + activity.getCH4Emissions());
            dashboardData.setTotalN2OEmissions(dashboardData.getTotalN2OEmissions() + activity.getN2OEmissions());
            dashboardData.setTotalFossilCO2Emissions(dashboardData.getTotalFossilCO2Emissions() + activity.getFossilCO2Emissions());
            dashboardData.setTotalBioCO2Emissions(dashboardData.getTotalBioCO2Emissions() + activity.getBioCO2Emissions());
        }
        dashboardData.setTotalCO2EqEmissions(dashboardData.getTotalFossilCO2Emissions() + dashboardData.getTotalBioCO2Emissions() + dashboardData.getTotalCH4Emissions()*GWP.CH4.getValue() + dashboardData.getTotalN2OEmissions()*GWP.N2O.getValue());
        return dashboardData;
    }

    //create FuelData
    private FuelData createFuelData(CreateStationaryActivityDto dto, Fuel fuel) {
        FuelData fuelData = new FuelData();
        fuelData.setFuel(fuel);
        fuelData.setFuelState(dto.getFuelState());
        fuelData.setMetric(dto.getMetric());
        fuelData.setAmount_in_SI_Unit(0.0);
        return fuelDataRepository.save(fuelData);
    }

    //Create VehicleData
    private VehicleData createTransportVehicleData(CreateTransportActivityByVehicleDataDto dto, Vehicle vehicle) {
        VehicleData vehicleData = new VehicleData();
        vehicleData.setVehicle(vehicle);
        vehicleData.setDistanceTravelled_m(dto.getDistanceUnit().toMeters(dto.getDistanceTravelled()));
        vehicleData.setPassengers(dto.getPassengers());
        vehicleData.setFreightWeight_Kg(dto.getFreightWeightUnit().toKilograms(dto.getFreightWeight()));
        return vehicleDataRepository.save(vehicleData);
    }
}