package com.navyn.emissionlog.Models.ActivityData;

import com.navyn.emissionlog.Enums.ActivityTypes;
import org.hibernate.validator.constraints.UUID;

import java.math.BigDecimal;

public abstract class ActivityData {
    private UUID id;
    private Double amount_SI_Unit;
    private ActivityTypes activityType;
}

//
//@ManyToOne
//@JoinColumn(name = "emission_factors_list", nullable = false)
//private Fuel fuel;
//
//
//private Double fuelAmount;
//
//@Enumerated(EnumType.STRING)
//private FuelState fuelState;
//
//@Enumerated(EnumType.STRING)
//private Metric metric;