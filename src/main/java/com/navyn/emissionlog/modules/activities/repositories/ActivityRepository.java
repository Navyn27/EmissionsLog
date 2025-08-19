package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.Enums.ActivityTypes;
import com.navyn.emissionlog.Enums.Sectors;
import com.navyn.emissionlog.modules.activities.models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    List<Activity> findByActivityData_ActivityType(ActivityTypes activityType);
    List<Activity> findByActivityYearBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Activity> findByRegion_IdAndActivityData_ActivityType(UUID region, ActivityTypes activityTypes);
    List<Activity> findBySector(Sectors sector);
}