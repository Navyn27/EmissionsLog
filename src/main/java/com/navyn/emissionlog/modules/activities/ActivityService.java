package com.navyn.emissionlog.modules.activities;

import com.navyn.emissionlog.Enums.FuelTypes;
import com.navyn.emissionlog.Enums.Scopes;
import com.navyn.emissionlog.Enums.TransportModes;
import com.navyn.emissionlog.Enums.TransportType;
import com.navyn.emissionlog.modules.activities.models.Activity;
import com.navyn.emissionlog.modules.activities.dtos.CreateTransportActivityByFuelDto;
import com.navyn.emissionlog.modules.activities.dtos.CreateTransportActivityByVehicleDataDto;
import com.navyn.emissionlog.modules.activities.dtos.CreateStationaryActivityDto;
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

    List<Activity> getStationaryActivities();

    List<Activity> getTransportActivities(TransportModes transportMode, UUID region, TransportType transportType, UUID fuel, FuelTypes fuelType, UUID vehicle, Scopes scope);

    DashboardData getDashboardData();

    DashboardData getDashboardData(LocalDateTime startDate, LocalDateTime endDate);

    List<DashboardData> getDashboardGraphDataByMonth(Integer year);

    List<DashboardData> getDashboardGraphDataByYear(Integer startingYear, Integer endingYear);
}