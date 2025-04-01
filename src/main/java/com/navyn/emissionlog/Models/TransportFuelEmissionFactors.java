package com.navyn.emissionlog.Models;

import com.navyn.emissionlog.Enums.VehicleEngineType;
import com.navyn.emissionlog.Enums.RegionGroup;
import com.navyn.emissionlog.Enums.TransportType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransportFuelEmissionFactors {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private RegionGroup regionGroup;

    private VehicleEngineType vehicleEngineType;
    private TransportType transportType;

    @ManyToOne
    private Fuel fuel;

    private Double FossilCO2EmissionFactor;
    private Double BiogenicCO2EmissionFactor;
    private Double CH4EmissionFactor;
    private Double N2OEmissionFactor;
}
