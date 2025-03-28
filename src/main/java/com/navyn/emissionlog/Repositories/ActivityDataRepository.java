package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.Models.ActivityData.ActivityData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ActivityDataRepository extends JpaRepository<ActivityData, UUID> {
}
