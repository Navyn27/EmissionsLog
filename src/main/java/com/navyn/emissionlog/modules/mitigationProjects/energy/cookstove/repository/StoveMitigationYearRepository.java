package com.navyn.emissionlog.modules.mitigationProjects.Energy.cookstove.repository;

import com.navyn.emissionlog.modules.mitigationProjects.Energy.cookstove.models.StoveMitigationYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoveMitigationYearRepository extends JpaRepository<StoveMitigationYear, UUID> {

    List<StoveMitigationYear> findByStoveTypeId(UUID stoveTypeId);

    List<StoveMitigationYear> findByYear(int year);

    Optional<StoveMitigationYear> findTopByStoveTypeIdAndYearLessThanOrderByYearDesc(UUID stoveTypeId, int year);

    @Query("select coalesce(sum(s.avoidedEmissions), 0) from StoveMitigationYear s where s.year = :year")
    double sumAvoidedEmissionsByYear(@Param("year") int year);
}

