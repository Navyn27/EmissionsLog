package com.navyn.emissionlog.Models.WasteData;

import com.navyn.emissionlog.Enums.WasteWaterConstants;
import com.navyn.emissionlog.Models.EICVReport;
import com.navyn.emissionlog.Models.PopulationRecords;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class WasteWaterData extends WasteDataAbstract {
    @ManyToOne
    private PopulationRecords populationRecords;

    private Double populationWithFlushToilets;

    private Double populationWithLatrines;

    private Double populationWithNoToiletFacitilies;

    private Double populationWithOtherToiletFacilities;

    @ManyToOne
    private EICVReport EICVReport;

    private Double TOWFlushToilets;

    private Double FlushToiletsCH4;

    private Double TOWLatrines;

    private Double LatrinesCH4;

    public Double calculateCH4Emissions() {

        setPopulationWithFlushToilets(populationRecords.getPopulation() * EICVReport.getFlushToilet()/100);
        setPopulationWithLatrines(populationRecords.getPopulation() * (EICVReport.getProtectedLatrines() +EICVReport.getUnprotectedLatrines())/100);
        setPopulationWithOtherToiletFacilities(populationRecords.getPopulation() * EICVReport.getOthers()/100);
        setPopulationWithNoToiletFacitilies(populationRecords.getPopulation() * EICVReport.getNoToiletFacilities()/100);
        setTOWFlushToilets(0.0);

        Double flushToiletCH4 = populationWithFlushToilets * WasteWaterConstants.FLUSH_TOILET_MCF.getValue() * WasteWaterConstants.FLUSH_TOILET_EF.getValue();

        return 0.0;
    }

    public Double calculateN2OEmissions() {
        return 0.0;
    }

    private Double calculateNH4Emissions(){
        return 0.0;
    }
}
