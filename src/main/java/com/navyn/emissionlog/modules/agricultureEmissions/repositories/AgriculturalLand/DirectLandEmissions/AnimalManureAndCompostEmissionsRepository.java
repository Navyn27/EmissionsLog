package com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.DirectLandEmissions;

import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions.AnimalManureAndCompostEmissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnimalManureAndCompostEmissionsRepository extends JpaRepository<AnimalManureAndCompostEmissions, UUID>, JpaSpecificationExecutor<AnimalManureAndCompostEmissions> {
    @Query(value = "SELECT * FROM manure_mgmt_emissions WHERE year BETWEEN :startYear AND :endYear", nativeQuery = true)
    List<AnimalManureAndCompostEmissions> findByYearRange(@Param("startYear") int startYear, @Param("endYear") int endYear);
}