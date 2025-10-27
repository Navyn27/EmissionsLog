package com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.DirectLandEmissions;

import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions.MineralSoilEmissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MineralSoilEmissionsRepository extends JpaRepository<MineralSoilEmissions, UUID>, JpaSpecificationExecutor<MineralSoilEmissions> {
    List<MineralSoilEmissions> findAllByOrderByYearDesc();
}
