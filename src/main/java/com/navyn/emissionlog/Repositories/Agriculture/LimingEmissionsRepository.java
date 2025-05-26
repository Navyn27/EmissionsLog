package com.navyn.emissionlog.Repositories.Agriculture;

import com.navyn.emissionlog.Models.Agriculture.LimingEmissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LimingEmissionsRepository extends JpaRepository<LimingEmissions, UUID> {
}
