package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.repository;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.enums.EStoveType;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoveMitigationRepository extends JpaRepository<StoveMitigation, UUID> {
    
    @Query("SELECT sm FROM StoveMitigation sm LEFT JOIN FETCH sm.projectIntervention")
    @Override
    List<StoveMitigation> findAll();
    
    @Query("SELECT sm FROM StoveMitigation sm LEFT JOIN FETCH sm.projectIntervention WHERE sm.year = :year")
    List<StoveMitigation> findAllByYear(@Param("year") int year);
    
    @Query("SELECT sm FROM StoveMitigation sm LEFT JOIN FETCH sm.projectIntervention WHERE sm.stoveType = :stoveType")
    List<StoveMitigation> findAllByStoveType(@Param("stoveType") EStoveType stoveType);
    
    @Query("SELECT sm FROM StoveMitigation sm LEFT JOIN FETCH sm.projectIntervention WHERE sm.year = :year AND sm.stoveType = :stoveType")
    List<StoveMitigation> findAllByYearAndStoveType(@Param("year") int year, @Param("stoveType") EStoveType stoveType);
    
    @Query("SELECT sm FROM StoveMitigation sm LEFT JOIN FETCH sm.projectIntervention WHERE sm.id = :id")
    Optional<StoveMitigation> findByIdWithIntervention(@Param("id") UUID id);
}

