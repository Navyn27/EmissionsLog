package com.navyn.emissionlog.modules.wasteEmissions.models;


import com.navyn.emissionlog.Enums.Waste.IncinerationConstants;
import com.navyn.emissionlog.modules.populationRecords.PopulationRecords;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "incineration_waste_data")
@Data
public class IncinerationWasteData extends WasteDataAbstract{

    @ManyToOne
    private PopulationRecords populationRecords;

    private Double totalWaste = 0.0;

    private Double totalIncineratedWaste = 0.0;

    final Double daysInYear = 365.0;

    public Double calculateCH4Emissions() {
        setTotalWaste(populationRecords.getPopulation()* IncinerationConstants.WASTE_PER_CAPITA.getValue()*daysInYear/1000);
        setTotalIncineratedWaste(totalWaste*IncinerationConstants.INCINERATION_FRACTION.getValue());
        return totalIncineratedWaste*IncinerationConstants.CH4_EF.getValue()*IncinerationConstants.DRY_MATTER_CONTENT.getValue()/1000000;
    }

    public Double calculateN2OEmissions() {
        return totalIncineratedWaste*IncinerationConstants.N2O_EF.getValue()*IncinerationConstants.DRY_MATTER_CONTENT.getValue()/1000000;
    }

    public Double calculateCO2Emissions() {
        return totalIncineratedWaste*
                IncinerationConstants.CO2_EF.getValue()*
                IncinerationConstants.DRY_MATTER_CONTENT.getValue()*
                IncinerationConstants.FRACTION_OF_DRY_MATTER_CARBON.getValue()*
                IncinerationConstants.FRACTION_OF_FOSSIL_CARBON_IN_TOTAL_CARBON.getValue()*
                IncinerationConstants.OXIDATION_FACTOR.getValue();
    }
}
