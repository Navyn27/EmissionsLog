package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.repository;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveMitigationYear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface StoveMitigationYearRepository extends JpaRepository<StoveMitigationYear, UUID> {
    List<StoveMitigationYear> findByStoveTypeId(UUID stoveTypeId);
}

