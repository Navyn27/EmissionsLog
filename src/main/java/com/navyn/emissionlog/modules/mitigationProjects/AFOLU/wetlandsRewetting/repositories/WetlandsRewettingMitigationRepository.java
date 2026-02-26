package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.repositories;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.models.WetlandsRewettingMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WetlandsRewettingMitigationRepository extends JpaRepository<WetlandsRewettingMitigation, UUID>,
        JpaSpecificationExecutor<WetlandsRewettingMitigation> {
}
