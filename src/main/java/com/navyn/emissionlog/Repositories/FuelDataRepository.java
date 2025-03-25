package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.Models.ActivityData.FuelData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FuelDataRepository extends JpaRepository<FuelData, UUID> {
}
