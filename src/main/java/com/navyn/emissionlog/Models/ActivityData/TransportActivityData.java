package com.navyn.emissionlog.Models.ActivityData;

import com.navyn.emissionlog.Enums.TransportModes;
import com.navyn.emissionlog.Models.Fuel;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

import java.util.List;

@Entity
public class TransportActivityData extends ActivityData{
    private TransportModes modeOfTransport;

    @OneToOne
    private VehicleData vehicleData;
}
