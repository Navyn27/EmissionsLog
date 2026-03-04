package com.navyn.emissionlog.modules.activities.repositories;

import com.navyn.emissionlog.Enums.ActivityTypes;
import com.navyn.emissionlog.Enums.Sectors;
import com.navyn.emissionlog.modules.activities.models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Check if an activity already exists for the same sector, region, year, fuel and activity type
     * to prevent duplicate entries (e.g. same sector, fuel, year for one region).
     */
    @Query("SELECT COUNT(a) > 0 FROM Activity a WHERE a.sector = :sector AND a.region.id = :regionId AND YEAR(a.activityYear) = :year AND a.activityData.fuelData.fuel.id = :fuelId AND a.activityData.activityType = :activityType")
    boolean existsBySectorAndRegionAndYearAndFuelAndActivityType(
            @Param("sector") Sectors sector,
            @Param("regionId") UUID regionId,
            @Param("year") int year,
            @Param("fuelId") UUID fuelId,
            @Param("activityType") ActivityTypes activityType
    );
}