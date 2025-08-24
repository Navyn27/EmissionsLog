package com.navyn.emissionlog.modules.activities.models;

import com.navyn.emissionlog.Enums.Transport.TransportModes;
import com.navyn.emissionlog.Enums.Transport.TransportType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class TransportActivityData extends ActivityData {
    private TransportModes modeOfTransport;

    private TransportType transportType;

    @OneToOne
    private VehicleData vehicleData;
}
