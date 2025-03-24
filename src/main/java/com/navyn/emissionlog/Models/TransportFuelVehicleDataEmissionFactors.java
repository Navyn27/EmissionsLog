package com.navyn.emissionlog.Models;

import com.navyn.emissionlog.Enums.RegionGroup;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransportFuelVehicleDataEmissionFactors {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    private Vehicles vehicle;

    @ManyToOne
    private Fuel fuel;

    private String vehicleYear;
    private String size;
    private String weightLaden;

    @Enumerated(EnumType.STRING)
    private RegionGroup regionGroup;

    private Double CO2EmissionFactor;
    private Double CH4EmissionFactor;
    private Double N2OEmissionFactor;

}
