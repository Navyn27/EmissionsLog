package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.repository;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.models.AddingStrawParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddingStrawParameterRepository extends JpaRepository<AddingStrawParameter, UUID> {

    /**
     * Finds the latest active AddingStrawParameter ordered by creation date
     * (most recent first)
     * 
     * @return Optional containing the latest active parameter, or empty if none
     *         found
     */
    Optional<AddingStrawParameter> findFirstByIsActiveTrueOrderByCreatedAtDesc();
}

