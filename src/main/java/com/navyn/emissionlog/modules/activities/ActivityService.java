package com.navyn.emissionlog.modules.activities;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.Enums.Fuel.FuelTypes;
import com.navyn.emissionlog.Enums.Transport.TransportModes;
import com.navyn.emissionlog.Enums.Transport.TransportType;
import com.navyn.emissionlog.modules.activities.models.Activity;
import com.navyn.emissionlog.modules.activities.dtos.CreateTransportActivityByFuelDto;
import com.navyn.emissionlog.modules.activities.dtos.CreateTransportActivityByVehicleDataDto;
import com.navyn.emissionlog.modules.activities.dtos.CreateStationaryActivityDto;
import com.navyn.emissionlog.modules.activities.dtos.UpdateTransportActivityByFuelDto;
import com.navyn.emissionlog.modules.activities.dtos.UpdateTransportActivityByVehicleDataDto;
import com.navyn.emissionlog.modules.activities.dtos.UpdateStationaryActivityDto;
import com.navyn.emissionlog.utils.DashboardData;
import com.navyn.emissionlog.modules.activities.dtos.AppliedEmissionFactorsDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface ActivityService {
    Activity createStationaryActivity(CreateStationaryActivityDto activity);

    Activity updateStationaryActivity(UUID id, UpdateStationaryActivityDto activity);

    void deleteStationaryActivity(UUID id);

    void deleteActivity(UUID id);

    Activity getActivityById(UUID id);

    List<Activity> getAllActivities();

    Activity createTransportActivityByFuel(CreateTransportActivityByFuelDto activityDto);

    Activity createTransportActivityByVehicleData(CreateTransportActivityByVehicleDataDto activityDto);

    Activity updateTransportActivityByFuel(UUID id, UpdateTransportActivityByFuelDto activityDto);

    Activity updateTransportActivityByVehicleData(UUID id, UpdateTransportActivityByVehicleDataDto activityDto);

    void deleteTransportActivity(UUID id);

    List<Activity> getStationaryActivities(UUID region, Sectors sector, UUID fuel, FuelTypes fuelType, Integer year);

    List<Activity> getTransportActivities(TransportModes transportMode, UUID region, TransportType transportType, UUID fuel, FuelTypes fuelType, UUID vehicle, Scopes scope, Integer year);

    DashboardData getDashboardData();

    DashboardData getDashboardData(LocalDateTime startDate, LocalDateTime endDate);

    List<DashboardData> getDashboardGraphDataByMonth(Integer year);

    List<DashboardData> getDashboardGraphDataByYear(Integer startingYear, Integer endingYear);
    
    // Mini Dashboards for Transport and Stationary
    DashboardData getTransportDashboardSummary(Integer startingYear, Integer endingYear);
    
    List<DashboardData> getTransportDashboardGraph(Integer startingYear, Integer endingYear);
    
    DashboardData getStationaryDashboardSummary(Integer startingYear, Integer endingYear);
    
    List<DashboardData> getStationaryDashboardGraph(Integer startingYear, Integer endingYear);

    /**
     * Returns the emission factors applied for a transport activity (if resolvable).
     * Used for transparency and audit. Returns empty for non-transport or when factors cannot be resolved.
     */
    Optional<AppliedEmissionFactorsDto> getEmissionFactorsForActivity(UUID activityId);
}