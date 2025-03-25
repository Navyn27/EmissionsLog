package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.Models.ActivityData.ActivityData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActivityDataRepository extends JpaRepository<ActivityData, UUID> {
}
