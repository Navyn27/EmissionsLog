package com.navyn.emissionlog.modules.agricultureEmissions.repositories.Livestock;

import com.navyn.emissionlog.modules.agricultureEmissions.models.Livestock.ManureManagementEmissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ManureManagementEmissionsRepository 
        extends JpaRepository<ManureManagementEmissions, UUID>, 
                JpaSpecificationExecutor<ManureManagementEmissions> {
    
    @Query("SELECT m FROM ManureManagementEmissions m WHERE m.year BETWEEN :startYear AND :endYear ORDER BY m.year DESC")
    List<ManureManagementEmissions> findByYearRange(@Param("startYear") int startYear, 
                                                      @Param("endYear") int endYear);
    
    Optional<ManureManagementEmissions> findByYearAndSpecies(int year, com.navyn.emissionlog.Enums.Agriculture.ManureManagementLivestock species);
}
