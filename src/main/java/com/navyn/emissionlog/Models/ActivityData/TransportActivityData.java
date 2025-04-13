package com.navyn.emissionlog.Models.ActivityData;

import com.navyn.emissionlog.Enums.TransportModes;
import com.navyn.emissionlog.Enums.TransportType;
import com.navyn.emissionlog.Models.Fuel;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class TransportActivityData extends ActivityData{
    private TransportModes modeOfTransport;

    private TransportType transportType;

    @OneToOne
    private VehicleData vehicleData;
}
