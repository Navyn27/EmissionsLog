package com.navyn.emissionlog.Utils;

import com.navyn.emissionlog.Models.EICVReport;
import com.navyn.emissionlog.Models.PopulationRecords;
import com.navyn.emissionlog.Repositories.PopulationRecordsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.HashMap;

public class CalculationUtils {

    HashMap<Integer, EICVReport> interpolatedEICVReport = null;

    public HashMap<Integer, PopulationRecords> getInterpolatedPopulations(PopulationRecords startingPopulation, PopulationRecords endingPopulation) {
        HashMap<Integer, PopulationRecords> interpolatedPopulation = new HashMap<>();

        int underlyingYears = endingPopulation.getYear() -  startingPopulation.getYear();
        int startingYear = startingPopulation.getYear();

        Double populationGrowthRate = Math.log((double) endingPopulation.getPopulation() /startingPopulation.getPopulation())/underlyingYears;
        Double numberOfHouseholdRate = Math.log((double) endingPopulation.getNumberOfKigaliHouseholds() /startingPopulation.getNumberOfKigaliHouseholds())/underlyingYears;
        Double GDPMillionsRate = Math.log(endingPopulation.getGDPMillions().doubleValue()/startingPopulation.getGDPMillions().doubleValue())/underlyingYears;
        Double GDPPerCapitaRate = Math.log(endingPopulation.getGDPPerCapita().doubleValue()/startingPopulation.getGDPPerCapita().doubleValue())/underlyingYears;
        Double kigaliGDPRate = Math.log(endingPopulation.getKigaliGDP().doubleValue()/startingPopulation.getKigaliGDP().doubleValue())/underlyingYears;

        PopulationRecords workingPopulation = startingPopulation;

        for(int i=0;i<=underlyingYears;i++){
            PopulationRecords newPopulationRecord = new PopulationRecords();
            newPopulationRecord.setYear(startingYear+i);
            newPopulationRecord.setPopulation(exponentialOperation(Double.valueOf(workingPopulation.getPopulation()),populationGrowthRate).longValue());
            newPopulationRecord.setCountry(workingPopulation.getCountry());
            newPopulationRecord.setNumberOfKigaliHouseholds(exponentialOperation((double) workingPopulation.getNumberOfKigaliHouseholds(),numberOfHouseholdRate).intValue());
            newPopulationRecord.setGDPMillions(BigDecimal.valueOf(exponentialOperation(workingPopulation.getGDPMillions().doubleValue(),GDPMillionsRate)));
            newPopulationRecord.setGDPPerCapita(BigDecimal.valueOf(exponentialOperation(workingPopulation.getGDPPerCapita().doubleValue(),GDPPerCapitaRate)));
            newPopulationRecord.setKigaliGDP(BigDecimal.valueOf(exponentialOperation(workingPopulation.getKigaliGDP().doubleValue(),kigaliGDPRate)));
            newPopulationRecord.setAnnualGrowth(populationGrowthRate);
            workingPopulation = newPopulationRecord;
            interpolatedPopulation.put(startingYear+i, workingPopulation);
        }
        return interpolatedPopulation;
    }

//    public HashMap<Integer, EICVReport> getInterpolatedEICVReports(Integer startingYear, Integer endingYear) {}
//
//    public PopulationRecords extrapolatePopulationRecords(){}
//
//    public EICVReport extrapolateEICVReport(){}

    private Double exponentialOperation(Double multiplicand, Double rate){
        return multiplicand*(Math.pow(Math.E,rate));
    }

}
