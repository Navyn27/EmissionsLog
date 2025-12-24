package com.navyn.emissionlog.modules.agricultureEmissions.models.Livestock;

import com.navyn.emissionlog.Enums.Agriculture.ManureManagementLivestock;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgricultureAbstractClass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "manure_management_emission", uniqueConstraints = @UniqueConstraint(columnNames = { "year", "species" }))
@AttributeOverride(name = "year", column = @Column(name = "year", nullable = false, unique = false))
public class ManureManagementEmissions extends AgricultureAbstractClass {

    // INPUT FIELDS
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ManureManagementLivestock species;

    @Column(nullable = false)
    private double animalPopulation;

    // CALCULATED FIELDS - CH4
    @Column(nullable = false)
    private double ch4Tonnes; // tonnes CH4/year

    @Column(nullable = false)
    private double ch4Co2eq; // tonnes CO2eq

    // CALCULATED FIELDS - N2O
    @Column(nullable = false)
    private double n2oTonnes; // tonnes N2O/year

    @Column(nullable = false)
    private double n2oCo2eq; // tonnes CO2eq

    // TOTAL
    @Column(nullable = false)
    private double totalCo2eq; // tonnes CO2eq (CH4 + N2O)
}
