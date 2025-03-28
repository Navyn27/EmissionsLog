package com.navyn.emissionlog.Services;

import com.navyn.emissionlog.Models.Activity;
import com.navyn.emissionlog.Payload.Requests.CreateStationaryActivityDto;

import java.util.List;
import java.util.UUID;

public interface ActivityService {
    Activity createStationaryActivity(CreateStationaryActivityDto activity);

//    Activity updateActivity(UUID id, Activity activity);

    void deleteActivity(UUID id);

    Activity getActivityById(UUID id);

    List<Activity> getAllActivities();
}