package com.navyn.emissionlog.Services;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.Models.Activity;
import com.navyn.emissionlog.Models.Region;
import com.navyn.emissionlog.Payload.Requests.Activity.CreateTransportActivityByFuelDto;
import com.navyn.emissionlog.Payload.Requests.Activity.CreateTransportActivityByVehicleDataDto;
import com.navyn.emissionlog.Payload.Requests.Activity.CreateStationaryActivityDto;
import com.navyn.emissionlog.Payload.Responses.DashboardData;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    List<Activity> getTransportActivities();

    DashboardData getDashboardData();

    DashboardData getDashboardData(LocalDateTime startDate, LocalDateTime endDate);

    List<DashboardData> getDashboardGraphDataByMonth(Integer year);

    List<DashboardData> getDashboardGraphDataByYear(Integer startingYear, Integer endingYear);
}