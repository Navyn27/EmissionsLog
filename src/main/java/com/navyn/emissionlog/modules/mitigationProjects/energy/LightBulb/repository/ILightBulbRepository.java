package com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.repository;

import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.model.LightBulb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ILightBulbRepository extends JpaRepository<LightBulb, UUID> {
    
    @Query("SELECT lb FROM LightBulb lb LEFT JOIN FETCH lb.projectIntervention WHERE lb.year = :year")
    List<LightBulb> findAllByYear(@Param("year") int year);

    @Query("SELECT lb FROM LightBulb lb LEFT JOIN FETCH lb.projectIntervention WHERE lb.year = :year")
    Optional<LightBulb> findByYear(@Param("year") int year);
    
    @Query("SELECT lb FROM LightBulb lb LEFT JOIN FETCH lb.projectIntervention")
    @Override
    List<LightBulb> findAll();
    
    @Query("SELECT lb FROM LightBulb lb LEFT JOIN FETCH lb.projectIntervention WHERE lb.id = :id")
    Optional<LightBulb> findByIdWithIntervention(@Param("id") UUID id);
}
