package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.Models.Fuel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FuelRepository extends JpaRepository<Fuel, UUID> {
}
