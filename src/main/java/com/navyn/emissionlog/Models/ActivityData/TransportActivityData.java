package com.navyn.emissionlog.Models.ActivityData;

import com.navyn.emissionlog.Enums.TransportModes;
import com.navyn.emissionlog.Models.Fuel;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

import java.util.List;

@Entity
public class TransportActivityData extends ActivityData{
    private TransportModes modeOfTransport;

    @ManyToOne
    private List<Fuel> fuel;

    @OneToOne
    private VehicleData vehicleData;
}
