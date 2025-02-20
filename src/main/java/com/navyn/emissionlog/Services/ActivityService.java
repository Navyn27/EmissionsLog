package com.navyn.emissionlog.Services;

import com.navyn.emissionlog.Models.Activity;
import com.navyn.emissionlog.Payload.Requests.CreateActivityDto;

import java.util.List;
import java.util.UUID;

public interface ActivityService {
    Activity createActivity(CreateActivityDto activity);

    Activity updateActivity(UUID id, Activity activity);

    void deleteActivity(UUID id);

    Activity getActivityById(UUID id);

    List<Activity> getAllActivities();
}