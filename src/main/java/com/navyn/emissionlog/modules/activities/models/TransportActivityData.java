package com.navyn.emissionlog.modules.activities.models;

import com.navyn.emissionlog.Enums.Transport.RegionGroup;
import com.navyn.emissionlog.Enums.Transport.TransportModes;
import com.navyn.emissionlog.Enums.Transport.TransportType;
import com.navyn.emissionlog.Enums.Transport.VehicleEngineType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class TransportActivityData extends ActivityData {
    private TransportModes modeOfTransport;

    private TransportType transportType;

    @Enumerated(EnumType.STRING)
    private RegionGroup regionGroup;

    @Enumerated(EnumType.STRING)
    private VehicleEngineType vehicleEngineType;

    @OneToOne
    private VehicleData vehicleData;
}
