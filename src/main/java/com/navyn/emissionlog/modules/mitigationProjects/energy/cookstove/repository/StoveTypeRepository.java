
package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.repository;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StoveTypeRepository extends JpaRepository<StoveType, UUID> {
}
