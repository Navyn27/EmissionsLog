package com.navyn.emissionlog.Repositories.Agriculture;

import com.navyn.emissionlog.Models.Agriculture.SyntheticFertilizerEmissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SyntheticFertilizerEmissionsRepository extends JpaRepository<SyntheticFertilizerEmissions, UUID> {
}
