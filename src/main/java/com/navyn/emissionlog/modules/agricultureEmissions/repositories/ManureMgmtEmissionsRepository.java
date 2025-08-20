package com.navyn.emissionlog.modules.agricultureEmissions.repositories;

import com.navyn.emissionlog.modules.agricultureEmissions.models.ManureMgmtEmissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ManureMgmtEmissionsRepository extends JpaRepository<ManureMgmtEmissions, UUID>, JpaSpecificationExecutor<ManureMgmtEmissions> {
    @Query(value = "SELECT * FROM manure_mgmt_emissions WHERE year BETWEEN :startYear AND :endYear", nativeQuery = true)
    List<ManureMgmtEmissions> findByYearRange(@Param("startYear") int startYear, @Param("endYear") int endYear);
}