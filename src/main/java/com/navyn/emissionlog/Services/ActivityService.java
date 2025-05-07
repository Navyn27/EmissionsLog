package com.navyn.emissionlog.Services;

import com.navyn.emissionlog.Models.Activity;
import com.navyn.emissionlog.Payload.Requests.Activity.CreateTransportActivityByFuelDto;
import com.navyn.emissionlog.Payload.Requests.Activity.CreateTransportActivityByVehicleDataDto;
import com.navyn.emissionlog.Payload.Requests.Activity.CreateStationaryActivityDto;
import com.navyn.emissionlog.Payload.Responses.DashboardData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ActivityService {
    Activity createStationaryActivity(CreateStationaryActivityDto activity);

    void deleteActivity(UUID id);

    Activity getActivityById(UUID id);

    List<Activity> getAllActivities();

    Activity createTransportActivityByFuel(CreateTransportActivityByFuelDto activityDto);

    Activity createTransportActivityByVehicleData(CreateTransportActivityByVehicleDataDto activityDto);

    List<Activity> getStationaryActivities();

    List<Activity> getTransportActivities();

    DashboardData getDashboardData();

    DashboardData getDashboardData(LocalDateTime startDate, LocalDateTime endDate);

    List<DashboardData> getDashboardGraphData(Integer year);
}