package com.navyn.emissionlog.modules.agricultureEmissions.models.Livestock;

import com.navyn.emissionlog.Enums.Agriculture.ManureManagementLivestock;
import com.navyn.emissionlog.Enums.Agriculture.ManureManagementSystem;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgricultureAbstractClass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "manure_management_emissions")
public class ManureManagementEmissions extends AgricultureAbstractClass {
    
    // INPUT FIELDS
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ManureManagementLivestock species;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ManureManagementSystem manureManagementSystem;
    
    @Column(nullable = false)
    private double animalPopulation;
    
    @Column(nullable = false)
    private double averageAnnualTemperature; // in Celsius
    
    private double averageAnimalWeight; // kg (optional, default by species)
    
    // CALCULATED FIELDS - Volatile Solids
    private double volatileSolidsExcretion; // kg VS/day/animal
    private double totalVolatileSolids; // kg VS/year
    
    // CALCULATED FIELDS - Methane
    private double methaneConversionFactor; // MCF (decimal)
    private double CH4EmissionsFromManure; // kg CH4/year
    private double CH4_CO2Eq; // kg CO2eq
    
    // CALCULATED FIELDS - Nitrogen
    private double nitrogenExcretion; // kg N/year
    private double nitrogenInManure; // kg N/year
    
    // CALCULATED FIELDS - Direct N2O
    private double directN2OEmissions; // kg N2O/year
    private double directN2O_CO2Eq; // kg CO2eq
    
    // CALCULATED FIELDS - Indirect N2O (Volatilization)
    private double volatilizedNitrogen; // kg N volatilized
    private double indirectN2OFromVolatilization; // kg N2O/year
    private double volatilizationN2O_CO2Eq; // kg CO2eq
    
    // CALCULATED FIELDS - Indirect N2O (Leaching)
    private double leachedNitrogen; // kg N leached
    private double indirectN2OFromLeaching; // kg N2O/year
    private double leachingN2O_CO2Eq; // kg CO2eq
    
    // TOTAL EMISSIONS
    private double totalN2OEmissions; // kg N2O/year
    private double totalN2O_CO2Eq; // kg CO2eq
    private double totalCO2EqEmissions; // kg CO2eq (CH4 + all N2O)
}
