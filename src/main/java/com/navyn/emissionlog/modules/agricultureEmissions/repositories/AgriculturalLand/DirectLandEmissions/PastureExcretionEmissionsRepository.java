package com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.DirectLandEmissions;

import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions.PastureExcretionEmissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PastureExcretionEmissionsRepository extends JpaRepository<PastureExcretionEmissions, UUID>, JpaSpecificationExecutor<PastureExcretionEmissions> {
    List<PastureExcretionEmissions> findAllByOrderByYearDesc();
}
