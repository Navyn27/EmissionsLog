package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.swap;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SwapRepository extends JpaRepository<Swap, UUID> {

    Optional<Swap> findByNameIgnoreCase(String name);
}
