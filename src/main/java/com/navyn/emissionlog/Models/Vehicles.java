package com.navyn.emissionlog.Models;

import com.navyn.emissionlog.Enums.RegionGroup;
import com.navyn.emissionlog.Enums.VehicleType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.UUID;

@Entity
public class Vehicles {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private VehicleType vehicleType;
    private RegionGroup regionGroup;
}
