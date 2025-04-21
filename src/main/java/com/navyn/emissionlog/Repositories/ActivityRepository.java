package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.Enums.ActivityTypes;
import com.navyn.emissionlog.Models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    List<Activity> findByActivityData_ActivityType(ActivityTypes activityType);
}
