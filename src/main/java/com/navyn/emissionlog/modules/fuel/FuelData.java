package com.navyn.emissionlog.modules.fuel;

import com.navyn.emissionlog.Enums.Fuel.FuelStates;
import com.navyn.emissionlog.Enums.Metrics.Metrics;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class FuelData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    private Fuel fuel;

    private FuelStates fuelState;

    private Metrics metric;

    private Double amount_in_SI_Unit;
}
