package com.navyn.emissionlog.modules.agricultureEmissions.models;

import com.navyn.emissionlog.Enums.CropTypes;
import com.navyn.emissionlog.Enums.Fertilizers;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "synthetic_fertilizer_emissions")
public class SyntheticFertilizerEmissions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true)
    private int year;

    @Enumerated(EnumType.STRING)
    private CropTypes cropType;

    @Enumerated(EnumType.STRING)
    private Fertilizers fertType;

    private double qtyApplied;
    private double NAmount;
    private double N2ONEmissions;
    private double N2OEmissions;
    private double CO2EqEmissions;
}
