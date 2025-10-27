package com.navyn.emissionlog.modules.activities.repositories;

import com.navyn.emissionlog.Enums.ActivityTypes;
import com.navyn.emissionlog.Enums.Sectors;
import com.navyn.emissionlog.modules.activities.models.Activity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID>, JpaSpecificationExecutor<Activity> {
    List<Activity> findByActivityData_ActivityTypeOrderByActivityYearDesc(ActivityTypes activityType);

    List<Activity> findAllByActivityYearBetweenOrderByActivityYearDesc(LocalDateTime startDate, LocalDateTime endDate);

    List<Activity> findAllByRegion_IdAndActivityData_ActivityTypeOrderByActivityYearDesc(UUID region, ActivityTypes activityTypes);
    List<Activity> findAllBySectorOrderByActivityYearDesc(Sectors sector);
}