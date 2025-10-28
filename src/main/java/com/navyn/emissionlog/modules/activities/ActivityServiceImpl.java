package com.navyn.emissionlog.modules.activities;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.Enums.Fuel.FuelTypes;
import com.navyn.emissionlog.Enums.Transport.MobileActivityDataType;
import com.navyn.emissionlog.Enums.Transport.TransportModes;
import com.navyn.emissionlog.Enums.Transport.TransportType;
import com.navyn.emissionlog.modules.activities.models.*;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.*;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions.SyntheticFertilizerEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.Livestock.EntericFermentationEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions.AnimalManureAndCompostEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.AquacultureEmissionsRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.DirectLandEmissions.AnimalManureAndCompostEmissionsRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.DirectLandEmissions.SyntheticFertilizerEmissionsRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.LimingEmissionsRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.RiceCultivationEmissionsRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.UreaEmissionsRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.Livestock.EntericFermentationEmissionsRepository;
import com.navyn.emissionlog.modules.vehicles.VehicleRepository;
import com.navyn.emissionlog.modules.wasteEmissions.models.WasteDataAbstract;
import com.navyn.emissionlog.modules.activities.dtos.*;
import com.navyn.emissionlog.modules.fuel.Fuel;
import com.navyn.emissionlog.modules.fuel.FuelData;
import com.navyn.emissionlog.modules.transportEmissions.models.TransportFuelEmissionFactors;
import com.navyn.emissionlog.utils.DashboardData;
import com.navyn.emissionlog.modules.stationaryEmissions.serviceImpls.StationaryEmissionCalculationServiceImpl;
import com.navyn.emissionlog.modules.transportEmissions.serviceImpls.TransportEmissionCalculationServiceImpl;
import com.navyn.emissionlog.modules.transportEmissions.services.TransportFuelEmissionFactorsService;
import com.navyn.emissionlog.modules.vehicles.Vehicle;
import com.navyn.emissionlog.utils.Specifications.ActivitySpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.navyn.emissionlog.modules.activities.repositories.ActivityRepository;
import com.navyn.emissionlog.modules.activities.repositories.ActivityDataRepository;
import com.navyn.emissionlog.modules.regions.RegionRepository;
import com.navyn.emissionlog.modules.fuel.repositories.FuelRepository;
import com.navyn.emissionlog.modules.wasteEmissions.WasteDataRepository;
import com.navyn.emissionlog.modules.fuel.repositories.FuelDataRepository;
import com.navyn.emissionlog.modules.vehicles.VehicleDataRepository;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;

import static com.navyn.emissionlog.utils.Specifications.ActivitySpecifications.*;
import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final StationaryEmissionCalculationServiceImpl stationaryEmissionCalculationService;
    private final TransportEmissionCalculationServiceImpl transportEmissionCalculationService;
    private final TransportFuelEmissionFactorsService transportFuelEmissionFactorsService;
    private final FuelRepository fuelRepository;
    private final ActivityRepository activityRepository;
    private final ActivityDataRepository activityDataRepository;
    private final RegionRepository regionRepository;
    private final VehicleRepository vehicleRepository;
    private final WasteDataRepository wasteDataAbstractRepository;
    private final AquacultureEmissionsRepository aquacultureEmissionsRepository;
    private final EntericFermentationEmissionsRepository entericFermentationEmissionsRepository;
    private final LimingEmissionsRepository limingEmissionsRepository;
    private final RiceCultivationEmissionsRepository riceCultivationEmissionsRepository;
    private final SyntheticFertilizerEmissionsRepository syntheticFertilizerEmissionsRepository;
    private final UreaEmissionsRepository ureaEmissionsRepository;
    private final FuelDataRepository fuelDataRepository;
    private final VehicleDataRepository vehicleDataRepository;
    private final AnimalManureAndCompostEmissionsRepository animalManureAndCompostEmissionsRepository;

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

        //find the emissions factors with wildcard support for ANY values
        Optional<TransportFuelEmissionFactors> transportEmissionFactorsList = transportFuelEmissionFactorsService.findBestMatchWithWildcardSupport(fuel.get(), activityDto.getRegionGroup(), activityDto.getTransportType(), activityDto.getVehicleType());

        if(transportEmissionFactorsList.isEmpty()){
            throw new IllegalArgumentException("Transport Emission Factors not found for specified fuel, region, transport type, and vehicle type combination");
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
            // Use flexible wildcard-aware matching to support ANY values
            Optional<TransportFuelEmissionFactors> transportEmissionFactorsList = transportFuelEmissionFactorsService.findBestMatchWithWildcardSupport(fuel.get(), activityDto.getRegionGroup(), activityDto.getTransportType(), activityDto.getVehicleType());

            if(transportEmissionFactorsList.isEmpty()){
                throw new IllegalArgumentException("Transport Emission Factors not found for specified fuel, region, transport type, and vehicle type combination");
            }
            transportEmissionCalculationService.calculateEmissionsByFuel(transportEmissionFactorsList.get(), fuel.get(), activity, fuelData,
                    activityDto.getFuelUnit(), activityDto.getFuelAmount());
            transportEmissionCalculationService.calculateEmissionsByVehicleData(activity, vehicleData, fuel.get(), activityDto.getRegionGroup(), activityDto.getMobileActivityDataType());
        }

        return activityRepository.save(activity);
    }

    @Override
    public List<Activity> getStationaryActivities(UUID region, Sectors sector, UUID fuel, FuelTypes fuelType, Integer year) {

        Specification<Activity> spec = Specification
                .where(isStationaryActivity())
                .and(hasSector(sector))
                .and(hasFuel(fuel))
                .and(hasFuelType(fuelType))
                .and(hasYear(year))
                .and(hasRegion(region));

        return activityRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "activityYear"));
    }

    @Override
    public List<Activity> getTransportActivities(TransportModes transportMode, UUID region, TransportType transportType, UUID fuel, FuelTypes fuelType, UUID vehicle, Scopes scope, Integer year) {

        Specification<Activity> spec = Specification
                .where(isTransportActivity())
                .and(hasTransportMode(transportMode))
                .and(hasTransportType(transportType))
                .and(hasYear(year))
                .and(ActivitySpecifications.hasRegion(region))
                .and(ActivitySpecifications.hasFuel(fuel))
                .and(ActivitySpecifications.hasFuelType(fuelType))
                .and(hasVehicle(vehicle))
                .and(hasScope(scope));

        return activityRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "activityYear"));
    }

    @Override
    public DashboardData getDashboardData() {
        try {
            List<Activity> activities = activityRepository.findAll();
            List<WasteDataAbstract> wasteActivities = wasteDataAbstractRepository.findAll();
            List<AquacultureEmissions> aquacultureEmissions = aquacultureEmissionsRepository.findAll();
            List<EntericFermentationEmissions> entericFermentationEmissions = entericFermentationEmissionsRepository.findAll();
            List<LimingEmissions> limingEmissions = limingEmissionsRepository.findAll();
            List<AnimalManureAndCompostEmissions> animalManureAndCompostEmissions = animalManureAndCompostEmissionsRepository.findAll();
            List<RiceCultivationEmissions> riceCultivationEmissions = riceCultivationEmissionsRepository.findAll();
            List<SyntheticFertilizerEmissions> syntheticFertilizerEmissions = syntheticFertilizerEmissionsRepository.findAll();
            List<UreaEmissions> ureaEmissions = ureaEmissionsRepository.findAll();
            return calculateDashboardData(activities, wasteActivities,
                    aquacultureEmissions, entericFermentationEmissions, limingEmissions,
                    animalManureAndCompostEmissions, riceCultivationEmissions, syntheticFertilizerEmissions, ureaEmissions);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public DashboardData getDashboardData(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            List<Activity> activities = activityRepository.findAllByActivityYearBetweenOrderByActivityYearDesc(startDate, endDate);
            List<WasteDataAbstract> wasteActivities = wasteDataAbstractRepository.findByActivityYearBetweenOrderByYearDesc(startDate, endDate);
            List<AquacultureEmissions> aquacultureEmissions = aquacultureEmissionsRepository.findByYearRange(startDate.getYear(), endDate.getYear());
            List<EntericFermentationEmissions> entericFermentationEmissions = entericFermentationEmissionsRepository.findByYearRange(startDate.getYear(), endDate.getYear());
            List<LimingEmissions> limingEmissions = limingEmissionsRepository.findByYearRange(startDate.getYear(), endDate.getYear());
            List<AnimalManureAndCompostEmissions> animalManureAndCompostEmissions = animalManureAndCompostEmissionsRepository.findByYearRange(startDate.getYear(), endDate.getYear());
            List<RiceCultivationEmissions> riceCultivationEmissions = riceCultivationEmissionsRepository.findByYearRange(startDate.getYear(), endDate.getYear());
            List<SyntheticFertilizerEmissions> syntheticFertilizerEmissions = syntheticFertilizerEmissionsRepository.findByYearRange(startDate.getYear(), endDate.getYear());
            List<UreaEmissions> ureaEmissions = ureaEmissionsRepository.findByYearRange(startDate.getYear(), endDate.getYear());
            DashboardData dashboardData = calculateDashboardData(activities, wasteActivities,
                    aquacultureEmissions, entericFermentationEmissions, limingEmissions,
                    animalManureAndCompostEmissions, riceCultivationEmissions, syntheticFertilizerEmissions, ureaEmissions);
            dashboardData.setStartingDate(startDate.toString());
            dashboardData.setEndingDate(endDate.toString());
            return dashboardData;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<DashboardData> getDashboardGraphDataByYear(Integer startingYear, Integer endingYear) {
        List<Activity> activities;
        List<WasteDataAbstract> wasteActivities;

        if(startingYear == null || endingYear == null) {
            activities = activityRepository.findAll();
            wasteActivities = wasteDataAbstractRepository.findAll();
        }
        else {
            LocalDateTime startDate = LocalDateTime.of(startingYear, 1, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(endingYear, 12, 31, 23, 59);
            activities = activityRepository.findAllByActivityYearBetweenOrderByActivityYearDesc(startDate, endDate);
            wasteActivities = wasteDataAbstractRepository.findByActivityYearBetweenOrderByYearDesc(startDate, endDate);
        }

        // Group activities by year
        Map<Integer, List<Activity>> groupedActivities = activities.stream().collect(groupingBy(Activity::getYear));
        Map<Integer, List<WasteDataAbstract>> groupedWasteActivities = wasteActivities.stream().collect(groupingBy(WasteDataAbstract::getYear));

        // Create aggregated dashboard data for each month
        List<DashboardData> dashboardDataList = new ArrayList<>();
        for (int year = startingYear; year <= endingYear; year++) {

            List<Activity> activityList = groupedActivities.getOrDefault(year, List.of());
            List<WasteDataAbstract> wasteList = groupedWasteActivities.getOrDefault(year, List.of());

            DashboardData data = generateDashboardGraphTime_DataPoint(activityList, wasteList);
            data.setStartingDate(LocalDateTime.of(year, 1, 1, 0, 0).toString());
            data.setEndingDate(LocalDateTime.of(year, 12, 31 , 23, 59).toString());
            data.setYear(Year.of(year));
            dashboardDataList.add(data);
        }

        return dashboardDataList;
    }

    @Override
    public List<DashboardData> getDashboardGraphDataByMonth(Integer year) {

        LocalDateTime startDate = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(year, 12, 31, 23, 59);

        List<Activity> activities = activityRepository.findAllByActivityYearBetweenOrderByActivityYearDesc(startDate, endDate);
        List<WasteDataAbstract> wasteData = wasteDataAbstractRepository.findByActivityYearBetweenOrderByYearDesc(startDate, endDate);

        // Group activities by year and month
        Map<YearMonth, List<Activity>> activitiesByYearMonth = activities.stream()
                .collect(groupingBy(activity ->
                        YearMonth.from(activity.getActivityYear())));

        //Waste data
        Map<YearMonth, List<WasteDataAbstract>> wasteDataByYearMonth = wasteData.stream()
                .collect(groupingBy(waste -> YearMonth.from(waste.getActivityYear())));

        // Create aggregated dashboard data for each month
        List<DashboardData> dashboardDataList = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            YearMonth ym = YearMonth.of(year, month);

            List<Activity> activityList = activitiesByYearMonth.getOrDefault(ym, List.of());
            List<WasteDataAbstract> wasteList = wasteDataByYearMonth.getOrDefault(ym, List.of());

            DashboardData data = generateDashboardGraphTime_DataPoint(activityList, wasteList);
            data.setStartingDate(LocalDateTime.of(year, month, 1, 0, 0).toString());
            data.setEndingDate(LocalDateTime.of(year, month, ym.lengthOfMonth() , 23, 59).toString());
            data.setMonth(ym.getMonth());
            dashboardDataList.add(data);
        }

        return dashboardDataList;
    }


    private DashboardData generateDashboardGraphTime_DataPoint(List<Activity> activities, List<WasteDataAbstract> wasteData) {
        DashboardData activityDashboardData = calculateDashboardActivityData(activities);
        DashboardData wasteDashboardData  = calculateWasteDashboardData(wasteData);

        activityDashboardData.setTotalCH4Emissions(activityDashboardData.getTotalCH4Emissions() + wasteDashboardData.getTotalCH4Emissions());
        activityDashboardData.setTotalN2OEmissions(activityDashboardData.getTotalN2OEmissions() + wasteDashboardData.getTotalN2OEmissions());
        activityDashboardData.setTotalFossilCO2Emissions(activityDashboardData.getTotalFossilCO2Emissions() + wasteDashboardData.getTotalFossilCO2Emissions());
        activityDashboardData.setTotalBioCO2Emissions(activityDashboardData.getTotalBioCO2Emissions() + wasteDashboardData.getTotalBioCO2Emissions());
        activityDashboardData.setTotalCO2EqEmissions(activityDashboardData.getTotalCO2EqEmissions() + wasteDashboardData.getTotalCO2EqEmissions());
        return activityDashboardData;
    }

    private DashboardData calculateDashboardData(List<Activity> activities, List<WasteDataAbstract> wasteData, List<AquacultureEmissions> aquacultureEmissions, List<EntericFermentationEmissions> entericFermentationEmissions, List<LimingEmissions> limingEmissions, List<AnimalManureAndCompostEmissions> animalManureAndCompostEmissions, List<RiceCultivationEmissions> riceCultivationEmissions, List<SyntheticFertilizerEmissions> syntheticFertilizerEmissions, List<UreaEmissions> ureaEmissions) {
        DashboardData dashboardData = new DashboardData();
        for(Activity activity : activities){
            dashboardData.setTotalCH4Emissions(dashboardData.getTotalCH4Emissions() + activity.getCH4Emissions());
            dashboardData.setTotalN2OEmissions(dashboardData.getTotalN2OEmissions() + activity.getN2OEmissions());
            dashboardData.setTotalFossilCO2Emissions(dashboardData.getTotalFossilCO2Emissions() + activity.getFossilCO2Emissions());
            dashboardData.setTotalBioCO2Emissions(dashboardData.getTotalBioCO2Emissions() + activity.getBioCO2Emissions());
            dashboardData.setTotalCO2EqEmissions(dashboardData.getTotalCO2EqEmissions() + activity.getCO2EqEmissions());
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
        for (AnimalManureAndCompostEmissions manureEmission : animalManureAndCompostEmissions) {
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

        if(activities == null) {
            return dashboardData;
        }

        for(Activity activity : activities){
            dashboardData.setTotalCH4Emissions(dashboardData.getTotalCH4Emissions() + activity.getCH4Emissions());
            dashboardData.setTotalN2OEmissions(dashboardData.getTotalN2OEmissions() + activity.getN2OEmissions());
            dashboardData.setTotalFossilCO2Emissions(dashboardData.getTotalFossilCO2Emissions() + activity.getFossilCO2Emissions());
            dashboardData.setTotalBioCO2Emissions(dashboardData.getTotalBioCO2Emissions() + activity.getBioCO2Emissions());
        }
        dashboardData.setTotalCO2EqEmissions(dashboardData.getTotalFossilCO2Emissions() + dashboardData.getTotalBioCO2Emissions() + dashboardData.getTotalCH4Emissions()*GWP.CH4.getValue() + dashboardData.getTotalN2OEmissions()*GWP.N2O.getValue());
        return dashboardData;
    }

    private DashboardData calculateWasteDashboardData(List<WasteDataAbstract> wasteActivities){
        DashboardData dashboardData = new DashboardData();

        if(wasteActivities == null || wasteActivities.isEmpty()) {
            return dashboardData;
        }

        for(WasteDataAbstract waste : wasteActivities) {
            dashboardData.setTotalCH4Emissions(dashboardData.getTotalCH4Emissions() + waste.getCH4Emissions());
            dashboardData.setTotalN2OEmissions(dashboardData.getTotalN2OEmissions() + waste.getN2OEmissions());
            dashboardData.setTotalFossilCO2Emissions(dashboardData.getTotalFossilCO2Emissions() + waste.getFossilCO2Emissions());
            dashboardData.setTotalBioCO2Emissions(dashboardData.getTotalBioCO2Emissions() + waste.getBioCO2Emissions());
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