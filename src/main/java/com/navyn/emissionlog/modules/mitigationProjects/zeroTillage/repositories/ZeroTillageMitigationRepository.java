package com.navyn.emissionlog.modules.mitigationProjects.zeroTillage.repositories;

import com.navyn.emissionlog.modules.mitigationProjects.zeroTillage.models.ZeroTillageMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ZeroTillageMitigationRepository 
        extends JpaRepository<ZeroTillageMitigation, UUID>,
                JpaSpecificationExecutor<ZeroTillageMitigation> {
    
    Optional<ZeroTillageMitigation> findByYear(Integer year);
}
