package com.navyn.emissionlog.Models.ActivityData;

import com.navyn.emissionlog.Models.Vehicle;
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
