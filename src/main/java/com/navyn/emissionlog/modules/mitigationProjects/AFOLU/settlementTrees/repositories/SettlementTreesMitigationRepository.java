package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.repositories;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.models.SettlementTreesMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SettlementTreesMitigationRepository 
        extends JpaRepository<SettlementTreesMitigation, UUID>,
                JpaSpecificationExecutor<SettlementTreesMitigation> {
    
    Optional<SettlementTreesMitigation> findByYear(Integer year);
}
