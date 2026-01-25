package com.navyn.emissionlog.modules.LandUseEmissions.Repositories;

import com.navyn.emissionlog.modules.LandUseEmissions.models.HarvestedBiomassLoss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HarvestedBiomassLossRepository extends JpaRepository<HarvestedBiomassLoss, UUID>, JpaSpecificationExecutor<HarvestedBiomassLoss> {
    List<HarvestedBiomassLoss> findAllByOrderByYearDesc();

    List<HarvestedBiomassLoss> findByYearBetweenOrderByYearDesc(Integer startYear, Integer endYear);

    default List<HarvestedBiomassLoss> findByYearRange(Integer startYear, Integer endYear) {
        return findByYearBetweenOrderByYearDesc(startYear, endYear);
    }
}
