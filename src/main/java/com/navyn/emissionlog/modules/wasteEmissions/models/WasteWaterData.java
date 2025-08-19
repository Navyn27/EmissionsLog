package com.navyn.emissionlog.modules.wasteEmissions.models;

import com.navyn.emissionlog.Enums.GeneralWasteWaterConstants;
import com.navyn.emissionlog.Enums.WasteWaterConstants;
import com.navyn.emissionlog.modules.eicvReports.EICVReport;
import com.navyn.emissionlog.modules.populationRecords.PopulationRecords;
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

    private Double flushToiletsCH4;

    private Double TOWLatrines;

    private Double latrinesCH4;

    private Double latrinesN2O;

    private Double flushToiletsN2O;

    public Double calculateCH4Emissions() {

        setPopulationWithFlushToilets(populationRecords.getPopulation() * EICVReport.getFlushToilet()/100);
        setPopulationWithLatrines(populationRecords.getPopulation() * (EICVReport.getProtectedLatrines() +EICVReport.getUnprotectedLatrines())/100);
        setPopulationWithOtherToiletFacilities(populationRecords.getPopulation() * EICVReport.getOthers()/100);
        setPopulationWithNoToiletFacitilies(populationRecords.getPopulation() * EICVReport.getNoToiletFacilities()/100);
        setTOWFlushToilets(populationWithFlushToilets * WasteWaterConstants.BOD.getValue());

        Double flushToiletCH4 = TOWFlushToilets* WasteWaterConstants.BO.getValue() *WasteWaterConstants.FLUSH_TOILET_MCF.getValue();
        setFlushToiletsCH4(flushToiletCH4);

        setTOWLatrines(populationWithLatrines * WasteWaterConstants.BOD.getValue());
        Double latrineCH4 = TOWLatrines * WasteWaterConstants.BO.getValue() * WasteWaterConstants.LATRINES_MCF.getValue();
        setLatrinesCH4(latrineCH4);
        return flushToiletCH4+latrineCH4;
    }

    public Double calculateN2OEmissions() {
        Double flushToiletNitrogen = populationWithFlushToilets*WasteWaterConstants.PROTEIN_EXCRETION.getValue()* GeneralWasteWaterConstants.F_NPR.getValue()*GeneralWasteWaterConstants.F_NON_CON.getValue()*GeneralWasteWaterConstants.F_IND_COM.getValue();
        setFlushToiletsN2O(flushToiletNitrogen * GeneralWasteWaterConstants.EF_EFFLUENT.getValue()*44/28);

        Double latrineNitrogen = populationWithLatrines*WasteWaterConstants.PROTEIN_EXCRETION.getValue()* GeneralWasteWaterConstants.F_NPR.getValue()*GeneralWasteWaterConstants.F_NON_CON.getValue()*GeneralWasteWaterConstants.F_IND_COM.getValue();
        setLatrinesN2O(latrineNitrogen * GeneralWasteWaterConstants.EF_EFFLUENT.getValue()*44/28);

        return flushToiletsN2O + latrinesN2O;
    }
}
