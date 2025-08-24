package com.navyn.emissionlog.modules.wasteEmissions.models;

import com.navyn.emissionlog.modules.populationRecords.PopulationRecords;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.navyn.emissionlog.Enums.Waste.GeneralWasteWaterConstants.*;
import static com.navyn.emissionlog.Enums.Waste.IndustrialWasteWaterConstants.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "industrial_waste_water_data")
public class IndustrialWasteWaterData extends WasteDataAbstract {
    private Double sugarProductionAmount = 0.0;
    private Double beerProductionAmount = 0.0;
    private Double dairyProductionAmount = 0.0;
    private Double meatAndPoultryProductionAmount = 0.0;

    private Double sugarN2OEmissions = 0.0;
    private Double bearN2OEmissions = 0.0;
    private Double dairyN2OEmissions= 0.0;
    private Double meatAndPoultryN2OEmissions = 0.0;

    private Double sugarCH4Emissions = 0.0;
    private Double bearCH4Emissions = 0.0;
    private Double dairyCH4Emissions = 0.0;
    private Double meatAndPoultryCH4Emissions = 0.0;

    private Double sugarTOW = 0.0;
    private Double bearTOW = 0.0;
    private Double dairyTOW = 0.0;
    private Double meatTOW = 0.0;

    @ManyToOne
    private PopulationRecords populationRecords;

    public Double calculateN2OEmissions() {

        Double generalConstantMultiplier =  PROTEIN_EXCRETION_PER_CAPITA.getValue()*F_NPR.getValue()*F_NON_CON.getValue()*F_IND_COM.getValue()*EF_EFFLUENT.getValue();

        setSugarN2OEmissions((sugarProductionAmount*WASTE_WATER_GENERATED.getSugarValue()*PROTEIN_EXCRETION_PER_CAPITA.getValue()*generalConstantMultiplier*44/28)/1000.0);
        setBearN2OEmissions((beerProductionAmount*WASTE_WATER_GENERATED.getBeerValue()*PROTEIN_EXCRETION_PER_CAPITA.getValue()*generalConstantMultiplier*44/28)/1000.0);
        setDairyN2OEmissions((dairyProductionAmount*WASTE_WATER_GENERATED.getDairyProductsValue()*PROTEIN_EXCRETION_PER_CAPITA.getValue()*generalConstantMultiplier*44/28)/1000.0);
        setMeatAndPoultryN2OEmissions((meatAndPoultryProductionAmount*WASTE_WATER_GENERATED.getMeatAndPoultryValue()*PROTEIN_EXCRETION_PER_CAPITA.getValue()*generalConstantMultiplier*44/28)/1000.0);
        return sugarN2OEmissions + bearN2OEmissions + dairyN2OEmissions + meatAndPoultryN2OEmissions;
    }

    public Double calculateCH4Emissions() {

        Double generalConstantMultiplier = INDUSTRIAL_WW_BO.getSugarValue()*INDUSTRIAL_WW_MCF.getSugarValue();

        //calculate all the TOW
        setSugarTOW(sugarProductionAmount*CODi.getSugarValue()* WASTE_WATER_GENERATED.getSugarValue());
        setBearTOW(beerProductionAmount*CODi.getBeerValue()* WASTE_WATER_GENERATED.getBeerValue());
        setDairyTOW(dairyProductionAmount*CODi.getDairyProductsValue()* WASTE_WATER_GENERATED.getDairyProductsValue());
        setMeatTOW(meatAndPoultryProductionAmount*CODi.getMeatAndPoultryValue()* WASTE_WATER_GENERATED.getMeatAndPoultryValue());

        //Calculate the emissions
        setSugarCH4Emissions(sugarTOW*generalConstantMultiplier);
        setBearCH4Emissions(bearTOW*generalConstantMultiplier);
        setDairyCH4Emissions(dairyTOW*generalConstantMultiplier);
        setMeatAndPoultryCH4Emissions(meatTOW*generalConstantMultiplier);

        return sugarCH4Emissions + bearCH4Emissions + dairyCH4Emissions + meatAndPoultryCH4Emissions;
    }
}
