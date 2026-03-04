package com.navyn.emissionlog.modules.LandUseEmissions.Repositories;

import com.navyn.emissionlog.modules.LandUseEmissions.models.FirewoodRemovalBiomassLoss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FirewoodRemovalBiomassLossRepository extends JpaRepository<FirewoodRemovalBiomassLoss, UUID>, JpaSpecificationExecutor<FirewoodRemovalBiomassLoss> {
    List<FirewoodRemovalBiomassLoss> findAllByOrderByYearDesc();

    List<FirewoodRemovalBiomassLoss> findByYearBetweenOrderByYearDesc(Integer startYear, Integer endYear);

    default List<FirewoodRemovalBiomassLoss> findByYearRange(Integer startYear, Integer endYear) {
        return findByYearBetweenOrderByYearDesc(startYear, endYear);
    }
}
