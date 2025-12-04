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
import com.navyn.emissionlog.utils.DashboardData;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public interface ActivityService {
    Activity createStationaryActivity(CreateStationaryActivityDto activity);

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
}