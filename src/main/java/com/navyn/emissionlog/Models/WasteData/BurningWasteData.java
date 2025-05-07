package com.navyn.emissionlog.Models.WasteData;

import com.navyn.emissionlog.Enums.BurntWasteConstants;
import com.navyn.emissionlog.Enums.WasteType;
import com.navyn.emissionlog.Models.PopulationRecords;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "burning_waste_data")
public class BurningWasteData extends WasteDataAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    private PopulationRecords populationRecords;

    private Double totalWaste;
    private Double totalBurntWaste;
    private Double openBurntWaste;


    final Double daysInYear = 365.0;

    public Double calculateCH4Emissions(){
        setTotalWaste(populationRecords.getPopulation()* BurntWasteConstants.WASTE_PER_CAPITA.getValue()*daysInYear);
        setTotalBurntWaste(totalWaste*BurntWasteConstants.FRACTION_OF_POP_BURNING_WASTE.getValue());
        setOpenBurntWaste(totalBurntWaste*BurntWasteConstants.FRACTION_OF_WASTE_OPEN_BURNT.getValue());
        return openBurntWaste*BurntWasteConstants.CH4_EF.getValue()*0.43/1000000;
    }
    public Double calculateCO2Emissions(){
        return openBurntWaste*BurntWasteConstants.CO2_EF.getValue();
    }
    public Double calculateN2OEmissions(){
        return openBurntWaste*BurntWasteConstants.N2O_EF.getValue()*0.57/1000000;
    }

}
