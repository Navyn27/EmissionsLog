package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.Models.ActivityData.FuelData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface FuelDataRepository extends JpaRepository<FuelData, UUID> {
}
