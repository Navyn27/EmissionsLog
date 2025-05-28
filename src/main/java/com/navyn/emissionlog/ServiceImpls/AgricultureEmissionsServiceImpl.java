package com.navyn.emissionlog.ServiceImpls;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.Models.Agriculture.*;
import com.navyn.emissionlog.Payload.Requests.Agriculture.*;
import com.navyn.emissionlog.Repositories.Agriculture.*;
import com.navyn.emissionlog.Services.AgricultureEmissionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class AgricultureEmissionsServiceImpl implements AgricultureEmissionsService {

    @Autowired
    private AquacultureEmissionsRepository aquacultureEmissionsRepository;

    @Autowired
    private EntericFermentationEmissionsRepository entericFermentationEmissionsRepository;

    @Autowired
    private LimingEmissionsRepository limingEmissionsRepository;

    @Autowired
    private ManureMgmtEmissionsRepository manureMgmtEmissionsRepository;

    @Autowired
    private RiceCultivationEmissionsRepository riceCultivationEmissionsRepository;

    @Autowired
    private SyntheticFertilizerEmissionsRepository syntheticFertilizerEmissionsRepository;

    @Autowired
    private UreaEmissionsRepository ureaEmissionsRepository;


    @Override
    public List<AquacultureEmissions> getAllAquacultureEmissions() {
        return aquacultureEmissionsRepository.findAll();
    }

    @Override
    public List<EntericFermentationEmissions> getAllEntericFermentationEmissions() {
        return entericFermentationEmissionsRepository.findAll();
    }

    @Override
    public List<LimingEmissions> getAllLimingEmissions(){
        return limingEmissionsRepository.findAll();
    }

    @Override
    public List<ManureMgmtEmissions> getAllManureMgmtEmissions() {
        return manureMgmtEmissionsRepository.findAll();
    }

    @Override
    public List<RiceCultivationEmissions> getAllRiceCultivationEmissions() {
        return riceCultivationEmissionsRepository.findAll();
    }

    @Override
    public List<SyntheticFertilizerEmissions> getAllSyntheticFertilizerEmissions() {
        return syntheticFertilizerEmissionsRepository.findAll();
    }

    @Override
    public List<UreaEmissions> getAllUreaEmissions() {
        return ureaEmissionsRepository.findAll();
    }

    @Override
    public AquacultureEmissions createAquacultureEmissions(AquacultureEmissionsDto emissionsDto) {
        AquacultureEmissions emissions = new AquacultureEmissions();
        emissions.setYear(emissionsDto.getYear());
        emissions.setActivityDesc(emissionsDto.getActivityDesc());
        emissions.setFishProduction(emissionsDto.getFishProduction());
        double fishN20EmissionsFactors = 0.0169;
        emissions.setN2ONEmissions(emissionsDto.getFishProduction()* fishN20EmissionsFactors);
        emissions.setN2OEmissions(emissionsDto.getFishProduction()* fishN20EmissionsFactors *44/28*1000000);
        emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());
        return aquacultureEmissionsRepository.save(emissions);
    }

    @Override
    public EntericFermentationEmissions createEntericFermentationEmissions(EntericFermentationEmissionsDto emissionsDto) {
        EntericFermentationEmissions emissions = new EntericFermentationEmissions();
        emissions.setYear(emissionsDto.getYear());
        emissions.setAnimalPopulation(emissionsDto.getAnimalPopulation());
        emissions.setSpecies(emissionsDto.getSpecies());
        emissions.setCH4Emissions(emissions.getAnimalPopulation()*getEntericEFBySpeciesType(emissionsDto.getSpecies())/1000);
        emissions.setCO2EqEmissions(emissions.getCH4Emissions()* GWP.CH4.getValue());
        return entericFermentationEmissionsRepository.save(emissions);
    }

    @Override
    public LimingEmissions createLimingEmissions(LimingEmissionsDto emissionsDto) {
        LimingEmissions emissions = new LimingEmissions();
        emissions.setYear(emissionsDto.getYear());
        emissions.setMaterial(emissionsDto.getMaterial());
        emissions.setCaCO3Qty(emissionsDto.getCaCO3Qty());
        if(emissions.getMaterial() == LimingMaterials.LIMESTONE) {
            emissions.setCO2Emissions(emissions.getCaCO3Qty()*AFOLUConstants.LIMESTONE.getValue()*AFOLUConstants.CONVERSION_FACTOR.getValue());
        }
        else if(emissions.getMaterial() == LimingMaterials.DOLOMITE) {
            emissions.setCO2Emissions(emissions.getCaCO3Qty()*AFOLUConstants.DOLOMITE.getValue()*AFOLUConstants.CONVERSION_FACTOR.getValue());
        }
        else{
            throw new IllegalArgumentException("Invalid liming material: " + emissions.getMaterial());
        }
        return limingEmissionsRepository.save(emissions);
    }

    @Override
    public ManureMgmtEmissions createManureMgmtEmissions(ManureMgmtEmissionsDto emissionsDto) {
        ManureMgmtEmissions emissions = new ManureMgmtEmissions();
        HashMap<String, Double> efs = getNEFBySpecieType(emissionsDto.getSpecies());
        emissions.setYear(emissionsDto.getYear());
        emissions.setSpecies(emissionsDto.getSpecies());
        emissions.setAmendmentType(emissionsDto.getAmendmentType());
        emissions.setPopulation(emissionsDto.getPopulation());
        emissions.setTotalN(efs.get("NEF")* emissions.getPopulation());
        emissions.setNAvailable(emissions.getTotalN()*efs.get("meanLosses"));
        emissions.setN2ONEmissions(emissions.getNAvailable()*efs.get("compostManureEF"));
        emissions.setN2OEmissions(emissions.getN2ONEmissions()* 44 / 28);
        emissions.setCH4Emissions(emissions.getPopulation()*getCH4EFBySpeciesType(emissionsDto.getSpecies()));
        emissions.setCO2EqEmissions(emissions.getN2OEmissions()*265/1000000 + emissions.getCH4Emissions() * GWP.CH4.getValue());
        return manureMgmtEmissionsRepository.save(emissions);
    }

    @Override
    public RiceCultivationEmissions createRiceCultivationEmissions(RiceCultivationEmissionsDto emissionsDto) {
        RiceCultivationEmissions emissions = new RiceCultivationEmissions();
        emissions.setYear(emissionsDto.getYear());
        emissions.setCultivationPeriod(emissionsDto.getCultivationPeriod());
        emissions.setHarvestedArea(emissionsDto.getHarvestedArea());
        emissions.setRiceEcosystem(emissionsDto.getRiceEcosystem());
        emissions.setWaterRegime(emissionsDto.getWaterRegime());
        //Efi=Efc*SFw*SFp*SfoA*SFs,r
        emissions.setAdjDailyEFEmissions(AFOLUConstants.EFC.getValue() * emissionsDto.getWaterRegime().getValue()* AFOLUConstants.SFP.getValue() * AFOLUConstants.SFOA.getValue() * AFOLUConstants.SFSR.getValue());
        emissions.setAnnualCH4Emissions(emissionsDto.getHarvestedArea()* emissionsDto.getCultivationPeriod()*emissions.getAdjDailyEFEmissions()/1000000);
        emissions.setCO2EqEmissions(emissions.getAnnualCH4Emissions()* GWP.CH4.getValue());
        return riceCultivationEmissionsRepository.save(emissions);
    }

    @Override
    public SyntheticFertilizerEmissions createSyntheticFertilizerEmissions(SyntheticFertilizerEmissionsDto emissionsDto) {
        SyntheticFertilizerEmissions emissions = new SyntheticFertilizerEmissions();
        emissions.setYear(emissionsDto.getYear());
        emissions.setCropType(emissionsDto.getCropType());
        emissions.setFertType(emissionsDto.getFertType());
        emissions.setQtyApplied(emissionsDto.getQtyApplied());

        if (emissions.getFertType() == Fertilizers.UREA) {
            emissions.setNAmount(emissions.getQtyApplied() * AFOLUConstants.N_CONTENT_UREA.getValue());
        } else if (emissions.getFertType() == Fertilizers.NPK) {
            emissions.setNAmount(emissions.getQtyApplied() * AFOLUConstants.N_CONTENT_NPK.getValue());
        } else {
            throw new IllegalArgumentException("Invalid fertilizer type: " + emissions.getFertType());
        }

        if(emissions.getCropType() == CropTypes.ANNUAL_CROPS_ON_HILLS) {
            emissions.setN2ONEmissions(emissions.getNAmount() * AFOLUConstants.ANNUAL_CROPS_ON_HILLS_N2O_EF.getValue());
        } else if (emissions.getCropType() == CropTypes.FLOODED_RICE) {
            emissions.setN2ONEmissions(emissions.getNAmount() * AFOLUConstants.FLOODED_RICE_N2O_EF.getValue());
        } else {
            throw new IllegalArgumentException("Invalid crop type: " + emissions.getCropType());
        }
        emissions.setN2OEmissions(emissions.getN2ONEmissions() * 44/28);
        emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());
        return syntheticFertilizerEmissionsRepository.save(emissions);
    }

    @Override
    public UreaEmissions createUreaEmissions(UreaEmissionsDto emissionsDto) {
        UreaEmissions emissions = new UreaEmissions();
        emissions.setYear(emissionsDto.getYear());
        emissions.setFertilizerName(emissionsDto.getFertilizerName());
        emissions.setQty(emissionsDto.getQty());
        emissions.setCO2Emissions(emissions.getQty() * AFOLUConstants.UREA_EMISSION_FACTOR.getValue() * AFOLUConstants.CONVERSION_FACTOR.getValue());
        return ureaEmissionsRepository.save(emissions);
    }

    private Double getEntericEFBySpeciesType(LivestockSpecies species){
        switch(species){
            case DAIRY_GROWING_COWS:
                return AFOLUConstants.ENTERIC_DAIRY_GROWING_COWS_CH4_EF.getValue();
            case DAIRY_LACTATING_COWS:
                return AFOLUConstants.ENTERIC_DAIRY_LACTATING_COWS_CH4_EF.getValue();
            case DAIRY_MATURE_COWS:
                return AFOLUConstants.ENTERIC_DAIRY_MATURE_COWS_CH4_EF.getValue();
            case SHEEP:
                return AFOLUConstants.ENTERIC_SHEEP_CH4_EF.getValue();
            case GOATS:
                return AFOLUConstants.ENTERIC_GOATS_CH4_EF.getValue();
            case SWINE:
                return AFOLUConstants.ENTERIC_SWINE_CH4_EF.getValue();
            default:
                return 0.0;
        }
    }

    private HashMap<String, Double> getNEFBySpecieType(LivestockSpecies species){

        HashMap<String, Double> efs = new HashMap<>();
        switch(species){
            case DAIRY_GROWING_COWS:
                efs.put("NEF",AFOLUConstants.MANURE_DAIRY_GROWING_COWS_N_EF.getValue());
                efs.put("meanLosses", AFOLUConstants.MANURE_DAIRY_GROWING_COWS_N_LOST.getValue());
                efs.put("compostManureEF", AFOLUConstants.MANURE_DAIRY_GROWING_COWS_EF_COMPOST_MANURE.getValue());
                break;
            case DAIRY_LACTATING_COWS:
                efs.put("NEF",AFOLUConstants.MANURE_DAIRY_LACTATING_COWS_N_EF.getValue());
                efs.put("meanLosses", AFOLUConstants.MANURE_DAIRY_LACTATING_COWS_N_LOST.getValue());
                efs.put("compostManureEF", AFOLUConstants.MANURE_DAIRY_LACTATING_COWS_EF_COMPOST_MANURE.getValue());
                break;
            case DAIRY_MATURE_COWS:
                efs.put("NEF",AFOLUConstants.MANURE_DAIRY_MATURE_COWS_N_EF.getValue());
                efs.put("meanLosses", AFOLUConstants.MANURE_DAIRY_MATURE_COWS_N_LOST.getValue());
                efs.put("compostManureEF", AFOLUConstants.MANURE_DAIRY_MATURE_COWS_EF_COMPOST_MANURE.getValue());
                break;
            case SHEEP:
                efs.put("NEF",AFOLUConstants.MANURE_SHEEP_N_EF.getValue());
                efs.put("meanLosses", AFOLUConstants.MANURE_SHEEP_N_LOST.getValue());
                efs.put("compostManureEF", AFOLUConstants.MANURE_SHEEP_EF_COMPOST_MANURE.getValue());
                break;
            case GOATS:
                efs.put("NEF",AFOLUConstants.MANURE_GOATS_N_EF.getValue());
                efs.put("meanLosses", AFOLUConstants.MANURE_GOATS_N_LOST.getValue());
                efs.put("compostManureEF", AFOLUConstants.MANURE_GOATS_EF_COMPOST_MANURE.getValue());
                break;
            case SWINE:
                efs.put("NEF",AFOLUConstants.MANURE_SWINE_N_EF.getValue());
                efs.put("meanLosses", AFOLUConstants.MANURE_SWINE_N_LOST.getValue());
                efs.put("compostManureEF", AFOLUConstants.MANURE_SWINE_EF_COMPOST_MANURE.getValue());
                break;
            case POULTRY:
                efs.put("NEF",AFOLUConstants.MANURE_POULTRY_N_EF.getValue());
                efs.put("meanLosses", AFOLUConstants.MANURE_POULTRY_N_LOST.getValue());
                efs.put("compostManureEF", AFOLUConstants.MANURE_POULTRY_EF_COMPOST_MANURE.getValue());
                break;
            case RABBITS:
                efs.put("NEF",AFOLUConstants.MANURE_RABBITS_N_EF.getValue());
                efs.put("meanLosses", AFOLUConstants.MANURE_RABBITS_N_LOST.getValue());
                efs.put("compostManureEF", AFOLUConstants.MANURE_RABBITS_EF_COMPOST_MANURE.getValue());
                break;
            default:
                throw new IllegalArgumentException("Invalid livestock species: " + species);
        }
        return efs;
    }

    private Double getCH4EFBySpeciesType(LivestockSpecies species) {
        switch (species) {
            case DAIRY_GROWING_COWS:
                return AFOLUConstants.MANURE_DAIRY_GROWING_COWS_CH4_EF.getValue();
            case DAIRY_LACTATING_COWS:
                return AFOLUConstants.MANURE_DAIRY_LACTATING_COWS_CH4_EF.getValue();
            case DAIRY_MATURE_COWS:
                return AFOLUConstants.MANURE_DAIRY_MATURE_COWS_CH4_EF.getValue();
            case SHEEP:
                return AFOLUConstants.MANURE_SHEEP_CH4_EF.getValue();
            case GOATS:
                return AFOLUConstants.MANURE_GOATS_CH4_EF.getValue();
            case SWINE:
                return AFOLUConstants.MANURE_SWINE_CH4_EF.getValue();
            case POULTRY:
                return AFOLUConstants.MANURE_POULTRY_CH4_EF.getValue();
            case RABBITS:
                return AFOLUConstants.MANURE_RABBITS_CH4_EF.getValue();
            default:
                return 0.0;
        }
    }

}
