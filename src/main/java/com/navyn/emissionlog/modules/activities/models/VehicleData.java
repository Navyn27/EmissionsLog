package com.navyn.emissionlog.modules.activities.models;

import com.navyn.emissionlog.modules.vehicles.Vehicle;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class VehicleData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    private Vehicle vehicle;
    private Double distanceTravelled_m;
    private Integer passengers;
    private Double freightWeight_Kg;
}
