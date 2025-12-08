package com.navyn.emissionlog.modules.wasteEmissions.models;

import com.navyn.emissionlog.Enums.Waste.BioTreatedWasteConstants;
import com.navyn.emissionlog.modules.populationRecords.PopulationRecords;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "bio_treated_waste_data")
public class BiologicallyTreatedWasteData extends WasteDataAbstract {

    @ManyToOne
    private PopulationRecords populationRecords;
    private Double totalMSW;
    private Double compostedWaste;

    public Double calculateCH4Emissions() {
        // Null safety check
        if (populationRecords == null) {
            throw new IllegalStateException("Population records cannot be null for biological waste calculation");
        }

        setTotalMSW(populationRecords.getPopulation() * BioTreatedWasteConstants.WASTE_PER_CAPITA.getValue());

        Double CWFraction = 0.0;
        int year = populationRecords.getYear();

        // Fixed year ranges - no gaps
        if (year >= 2022) {
            CWFraction = BioTreatedWasteConstants.COMPOSTED_WASTE_E.getValue();
        } else if (year >= 2017 && year < 2022) {
            // Covers 2017-2021 (including 2020 and 2021)
            CWFraction = BioTreatedWasteConstants.COMPOSTED_WASTE_D.getValue();
        } else if (year >= 2014 && year < 2017) {
            CWFraction = BioTreatedWasteConstants.COMPOSTED_WASTE_C.getValue();
        } else if (year >= 2011 && year < 2014) {
            CWFraction = BioTreatedWasteConstants.COMPOSTED_WASTE_B.getValue();
        } else {
            // For years < 2011 (1990-2010)
            CWFraction = BioTreatedWasteConstants.COMPOSTED_WASTE_A.getValue();
        }
        setCompostedWaste(totalMSW * CWFraction / 100);
        return compostedWaste * BioTreatedWasteConstants.CH4_EF.getValue() / 1000;
    }

    public Double calculateN2OEmissions() {
        return compostedWaste * BioTreatedWasteConstants.N2O_EF.getValue() / 1000;
    }
}
