package com.navyn.emissionlog.Models.ActivityData;

import com.navyn.emissionlog.Models.Vehicles;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class VehicleData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    private Vehicles vehicle;
    private Double distanceTravelled_Km;
    private Integer passengers;
    private Double freightWeight_Kg;
}
