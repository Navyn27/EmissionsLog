package com.navyn.emissionlog.Models.ActivityData;

import com.navyn.emissionlog.Enums.FuelStates;
import com.navyn.emissionlog.Enums.Metrics;
import jakarta.persistence.OneToOne;
import java.util.UUID;

public class FuelData {

    @OneToOne
    private UUID fuel;

    private FuelStates fuelStates;

    private Metrics metrics;
}
