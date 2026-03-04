package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.repository;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.models.AddingStrawMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddingStrawMitigationRepository extends JpaRepository<AddingStrawMitigation, UUID>,
        JpaSpecificationExecutor<AddingStrawMitigation> {

    Optional<AddingStrawMitigation> findByYear(Integer year);

    // Find by year range for dashboard filtering
    List<AddingStrawMitigation> findByYearBetweenOrderByYearDesc(Integer startYear, Integer endYear);

    default List<AddingStrawMitigation> findByYearRange(Integer startYear, Integer endYear) {
        return findByYearBetweenOrderByYearDesc(startYear, endYear);
    }
}
