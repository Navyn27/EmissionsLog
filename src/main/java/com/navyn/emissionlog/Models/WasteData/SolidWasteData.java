package com.navyn.emissionlog.Models.WasteData;

import com.navyn.emissionlog.Enums.SolidWasteType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "solid_waste_data")
public class SolidWasteData extends WasteDataAbstract {

    final Double MCF = 0.8;
    final Double conv = (double) (16/12);

    @Enumerated(EnumType.STRING)
    private SolidWasteType solidWasteType;

    private Double amountDeposited = 0.0;

    private Double methaneRecovery = 0.0;

    private Double DDOCmDeposited = 0.0;

    private Double DDOCmUnreacted = 0.0;

    private Double DDOCmDecomposedDpYear = 0.0;

    private Double DDOCmAccumulated = 0.0;

    private Double DDOCmDecomposed = 0.0;

    public Double calculateCH4Emissions(Double DDOCmAccumulatedLastYear) {

        //Abbreviated as D in Excel: Decomposable DOC (DDOCm) deposited
        setDDOCmDeposited(amountDeposited* solidWasteType.getDOC()* solidWasteType.getDOCF()*MCF);

        //Abbreviated as B in Excel: DDOCm not reacted. Deposition year
        setDDOCmUnreacted(DDOCmDeposited* solidWasteType.getEXP2());

        //Abbreviated as C in Excel: DDOCm decomposed. Deposition year
        setDDOCmDecomposedDpYear(DDOCmDeposited*(1-solidWasteType.getEXP2()));

        //Abbreviated as D in Excel: DDOCm accumulated in SWDS end of year
        setDDOCmAccumulated(DDOCmUnreacted + (DDOCmAccumulatedLastYear* solidWasteType.getEXP1()));

        setDDOCmDecomposed(DDOCmDecomposedDpYear + DDOCmAccumulatedLastYear * (1 - solidWasteType.getEXP1()));

        return DDOCmDecomposed* solidWasteType.getF()*conv;
    }
}
