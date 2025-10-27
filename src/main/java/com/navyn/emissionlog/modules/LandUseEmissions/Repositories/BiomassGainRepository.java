package com.navyn.emissionlog.modules.LandUseEmissions.Repositories;

import com.navyn.emissionlog.modules.LandUseEmissions.models.BiomassGain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BiomassGainRepository extends JpaRepository<BiomassGain, UUID>, JpaSpecificationExecutor<BiomassGain> {
    List<BiomassGain> findAllByOrderByYearDesc();
}
