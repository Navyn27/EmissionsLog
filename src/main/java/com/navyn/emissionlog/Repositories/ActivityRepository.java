package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.Models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {
}
