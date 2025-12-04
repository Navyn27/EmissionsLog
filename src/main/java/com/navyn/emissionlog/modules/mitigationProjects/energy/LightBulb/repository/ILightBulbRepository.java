package com.navyn.emissionlog.modules.mitigationProjects.Energy.LightBulb.repository;

import com.navyn.emissionlog.modules.mitigationProjects.Energy.LightBulb.model.LightBulb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface ILightBulbRepository extends JpaRepository<LightBulb, UUID> {
}
