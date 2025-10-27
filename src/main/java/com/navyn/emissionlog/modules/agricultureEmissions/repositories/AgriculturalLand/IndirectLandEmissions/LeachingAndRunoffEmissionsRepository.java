package com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.IndirectLandEmissions;

import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.IndirectLandEmissions.LeachingAndRunoffEmissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeachingAndRunoffEmissionsRepository extends JpaRepository<LeachingAndRunoffEmissions, UUID>, JpaSpecificationExecutor<LeachingAndRunoffEmissions> {
    List<LeachingAndRunoffEmissions> findAllByOrderByYearDesc();
}
