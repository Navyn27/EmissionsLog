package com.navyn.emissionlog.Repositories.Agriculture;

import com.navyn.emissionlog.Models.Agriculture.ManureMgmtEmissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ManureMgmtEmissionsRepository extends JpaRepository<ManureMgmtEmissions, UUID> {
    @Query("SELECT mme FROM ManureMgmtEmissions mme WHERE mme.year BETWEEN :startYear AND :endYear")
    List<ManureMgmtEmissions> findByYearRange(@Param("startYear") int startYear, @Param("endYear") int endYear);
}
