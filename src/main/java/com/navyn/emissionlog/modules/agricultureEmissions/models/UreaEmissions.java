package com.navyn.emissionlog.modules.agricultureEmissions.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "urea_emissions")
public class UreaEmissions extends  AgricultureAbstractClass {

    private String fertilizerName;
    private double qty;
    private double CO2Emissions;
}