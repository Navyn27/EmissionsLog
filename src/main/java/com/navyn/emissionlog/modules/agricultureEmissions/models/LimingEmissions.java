package com.navyn.emissionlog.modules.agricultureEmissions.models;

import com.navyn.emissionlog.Enums.LimingMaterials;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "liming")
public class LimingEmissions extends AgricultureAbstractClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private LimingMaterials material;

    private double CaCO3Qty;
    private double CO2Emissions;
}

