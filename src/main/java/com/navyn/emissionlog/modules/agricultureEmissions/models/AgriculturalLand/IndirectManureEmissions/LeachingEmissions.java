package com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.IndirectManureEmissions;

import com.navyn.emissionlog.Enums.Agriculture.LivestockSpecies;
import com.navyn.emissionlog.Enums.Agriculture.MMS;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgricultureAbstractClass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Getter
@Table(name = "leaching_emission", uniqueConstraints = @UniqueConstraint(columnNames = { "year", "species" }))
@AttributeOverride(name = "year", column = @Column(name = "year", nullable = false, unique = false))
public class LeachingEmissions extends AgricultureAbstractClass {

    @Enumerated(EnumType.STRING)
    private MMS MMS;
    @Column(name = "species")
    private LivestockSpecies livestockSpecies;
    @Column(name = "animal_population")
    private double numberOfAnimals;
    @Column(name = "total_n_excretion_for_mms")
    private double totalNExcretionForMMS;
    @Column(name = "manure_n_loss_due_to_leaching_and_runoff")
    private double manureNLossDueToLeachingAndRunoff;
    @Column(name = "n2o_n_from_leaching")
    private double n2ONFromLeaching;
    @Column(name = "indirect_n2o_emissions_from_leaching")
    private double indirectN2OEmissionsFromLeaching;
    @Column(name = "co2_eq_emissions")
    private double CO2EqEmissions;
}
