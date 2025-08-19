package com.navyn.emissionlog.modules.agricultureEmissions.repositories;

import com.navyn.emissionlog.modules.agricultureEmissions.models.AquacultureEmissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AquacultureEmissionsRepository extends JpaRepository<AquacultureEmissions, UUID> {
    @Query("SELECT ae FROM AquacultureEmissions ae WHERE ae.year BETWEEN :startYear AND :endYear")
    List<AquacultureEmissions> findByYearRange(@Param("startYear") int startYear, @Param("endYear") int endYear);
}
