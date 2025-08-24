package com.navyn.emissionlog.modules.wasteEmissions.models;

import com.navyn.emissionlog.Enums.Waste.BioTreatedWasteConstants;
import com.navyn.emissionlog.modules.populationRecords.PopulationRecords;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "bio_treated_waste_data")
public class BiologicallyTreatedWasteData extends WasteDataAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    private PopulationRecords populationRecords;
    private Double totalMSW;
    private Double compostedWaste;

    public Double calculateCH4Emissions(){
        setTotalMSW(populationRecords.getPopulation()* BioTreatedWasteConstants.WASTE_PER_CAPITA.getValue());

        Double CWFraction = 0.0;
        if(populationRecords.getYear()>=2022){
            CWFraction = BioTreatedWasteConstants.COMPOSTED_WASTE_E.getValue();
        }
        else if(populationRecords.getYear()<2020 && populationRecords.getYear()>=2017){
            CWFraction = BioTreatedWasteConstants.COMPOSTED_WASTE_D.getValue();
        }
        else if(populationRecords.getYear()<2017 && populationRecords.getYear()>=2014){
            CWFraction = BioTreatedWasteConstants.COMPOSTED_WASTE_C.getValue();
        }
        else if(populationRecords.getYear()<2014 && populationRecords.getYear()>=2011){
            CWFraction = BioTreatedWasteConstants.COMPOSTED_WASTE_B.getValue();
        }
        else{
            CWFraction = BioTreatedWasteConstants.COMPOSTED_WASTE_A.getValue();
        }
        setCompostedWaste(totalMSW*CWFraction/100);
        return compostedWaste*BioTreatedWasteConstants.CH4_EF.getValue()/1000;
    }
    public Double calculateN2OEmissions(){
        return compostedWaste*BioTreatedWasteConstants.N2O_EF.getValue()/1000;
    }
}

