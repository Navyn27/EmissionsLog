package com.navyn.emissionlog.modules.agricultureEmissions;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.Enums.Agriculture.*;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.*;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.Livestock.EntericFermentationEmissionsDto;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.Livestock.ManureMgmtEmissionsDto;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.*;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions.SyntheticFertilizerEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.Livestock.EntericFermentationEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions.AnimalManureAndCompostEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.*;
import com.navyn.emissionlog.utils.Specifications.AgricultureSpecifications;
import com.navyn.emissionlog.utils.Specifications.WasteSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

import static com.navyn.emissionlog.Enums.Agriculture.AFOLUConstants.OTHER_FOREST_CF;
import static com.navyn.emissionlog.utils.Specifications.AgricultureSpecifications.*;

@Service
@RequiredArgsConstructor
public class AgricultureEmissionsServiceImpl implements AgricultureEmissionsService {

    private final AquacultureEmissionsRepository aquacultureEmissionsRepository;
    private final EntericFermentationEmissionsRepository entericFermentationEmissionsRepository;
    private final LimingEmissionsRepository limingEmissionsRepository;
    private final ManureMgmtEmissionsRepository manureMgmtEmissionsRepository;
    private final RiceCultivationEmissionsRepository riceCultivationEmissionsRepository;
    private final SyntheticFertilizerEmissionsRepository syntheticFertilizerEmissionsRepository;
    private final UreaEmissionsRepository ureaEmissionsRepository;
    private final BurningEmissionsRepository burningEmissionsRepository;


    @Override
    public List<AquacultureEmissions> getAllAquacultureEmissions(Integer year) {
        Specification<AquacultureEmissions> spec = Specification.where(hasYear(year));
        return aquacultureEmissionsRepository.findAll(spec);
    }

    @Override
    public List<EntericFermentationEmissions> getAllEntericFermentationEmissions(Integer year, LivestockSpecies species) {
        Specification<EntericFermentationEmissions> spec =
                Specification.<EntericFermentationEmissions>where(hasYear(year))
                        .and(hasSpecies(species));
        return entericFermentationEmissionsRepository.findAll(spec);
    }

    @Override
    public List<LimingEmissions> getAllLimingEmissions(Integer year, LimingMaterials limingMaterials) {
        Specification<LimingEmissions> spec = Specification.where(hasLimingMaterial(limingMaterials)).and(hasYear(year));
        return limingEmissionsRepository.findAll(spec);
    }

    @Override
    public List<AnimalManureAndCompostEmissions> getAllManureMgmtEmissions(Integer year, OrganicAmendmentTypes amendmentType, LivestockSpecies species) {
        Specification<AnimalManureAndCompostEmissions> spec = Specification.where(AgricultureSpecifications.hasAmendmentType(amendmentType))
                .and(hasSpecies(species))
                .and(AgricultureSpecifications.hasYear(year));
        return manureMgmtEmissionsRepository.findAll(spec);
    }

    @Override
    public List<RiceCultivationEmissions> getAllRiceCultivationEmissions(String riceEcosystem, WaterRegime waterRegime, Integer year) {
        Specification<RiceCultivationEmissions> spec = Specification.where(AgricultureSpecifications.hasRiceEcosystem(riceEcosystem))
                .and(AgricultureSpecifications.hasWaterRegime(waterRegime))
                .and(AgricultureSpecifications.hasYear(year));
        return riceCultivationEmissionsRepository.findAll(spec);
    }

    @Override
    public List<SyntheticFertilizerEmissions> getAllSyntheticFertilizerEmissions(Integer year, CropTypes cropType, Fertilizers fertilizerType) {
        Specification<SyntheticFertilizerEmissions> spec = Specification.where(AgricultureSpecifications.hasCropType(cropType))
                .and(AgricultureSpecifications.hasYear(year))
                .and(AgricultureSpecifications.hasFertilizerType(fertilizerType));
        return syntheticFertilizerEmissionsRepository.findAll(spec);
    }

    @Override
    public List<UreaEmissions> getAllUreaEmissions(String fertilizer, Integer year) {

        Specification<UreaEmissions> spec = Specification.where(AgricultureSpecifications.hasFertilizerName(fertilizer))
                .and(AgricultureSpecifications.hasYear(year));
        return ureaEmissionsRepository.findAll(spec);
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
            emissions.setCO2Emissions(emissions.getCaCO3Qty()* AFOLUConstants.LIMESTONE.getValue()*AFOLUConstants.CONVERSION_FACTOR.getValue());
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
    public AnimalManureAndCompostEmissions createManureMgmtEmissions(ManureMgmtEmissionsDto emissionsDto) {
        AnimalManureAndCompostEmissions emissions = new AnimalManureAndCompostEmissions();
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

    @Override
    public BurningEmissions createBurningEmissions(BurningEmissionsDto burningEmissionsDto) {
        BurningEmissions emissions = new BurningEmissions();
        emissions.setYear(burningEmissionsDto.getYear());
        emissions.setBurningAgentType(burningEmissionsDto.getBurningAgentType());
        emissions.setBurntArea(burningEmissionsDto.getBurntArea());
        emissions.setFuelMassAvailable(burningEmissionsDto.getFuelMassUnit().toKilograms(burningEmissionsDto.getFuelMassAvailable()));
        emissions.setFireType(burningEmissionsDto.getFireType());

        Double combustionFactor = burningEmissionsDto.getIsEucalyptusForest() ? AFOLUConstants.EUCALYPTUS_FOREST_CF.getValue(): OTHER_FOREST_CF.getValue() ;
        emissions.setFuelMassConsumed(emissions.getFuelMassAvailable() * combustionFactor);
        
        //Emissions Calculation
        switch (burningEmissionsDto.getBurningAgentType()){
            case SAVANNA_AND_GRASSLAND -> {
                emissions.setCO2Emissions(emissions.getFuelMassConsumed() * AFOLUConstants.SAVANNA_AND_GRASSLAND_CO2.getValue());
                emissions.setCH4Emissions(emissions.getFuelMassConsumed() * AFOLUConstants.SAVANNA_AND_GRASSLAND_CH4.getValue());
                emissions.setN2OEmissions(emissions.getFuelMassConsumed() * AFOLUConstants.SAVANNA_AND_GRASSLAND_N2O.getValue());
            }
            case AGRICULTURAL_RESIDUES -> {
                emissions.setCO2Emissions(emissions.getFuelMassConsumed() * AFOLUConstants.AGRICULTURAL_RESIDUES_CO2.getValue());
                emissions.setCH4Emissions(emissions.getFuelMassConsumed() * AFOLUConstants.AGRICULTURAL_RESIDUES_CH4.getValue());
                emissions.setN2OEmissions(emissions.getFuelMassConsumed() * AFOLUConstants.AGRICULTURAL_RESIDUES_N2O.getValue());
            }
            case FOREST -> {
                emissions.setCO2Emissions(emissions.getFuelMassConsumed() * AFOLUConstants.FOREST_CO2.getValue());
                emissions.setCH4Emissions(emissions.getFuelMassConsumed() * AFOLUConstants.FOREST_CH4.getValue());
                emissions.setN2OEmissions(emissions.getFuelMassConsumed() * AFOLUConstants.FOREST_N2O.getValue());
            }
            case BIOFUEL_BURNING -> {
                emissions.setCO2Emissions(emissions.getFuelMassConsumed() * AFOLUConstants.BIOFUEL_BURNING_CO2.getValue());
                emissions.setCH4Emissions(emissions.getFuelMassConsumed() * AFOLUConstants.BIOFUEL_BURNING_CH4.getValue());
                emissions.setN2OEmissions(emissions.getFuelMassConsumed() * AFOLUConstants.BIOFUEL_BURNING_N2O.getValue());
            }
            default -> throw new IllegalArgumentException("Invalid burning agent type: " + burningEmissionsDto.getBurningAgentType());
        }

        emissions.setCO2EqEmissions(emissions.getCO2Emissions() + (emissions.getCH4Emissions() * GWP.CH4.getValue()) + (emissions.getN2OEmissions() * GWP.N2O.getValue()));
        return burningEmissionsRepository.save(emissions);
    }

    @Override
    public List<BurningEmissions> getAllBurningEmissions(Integer year, BurningAgentType forestType) {
        Specification<BurningEmissions> spec = Specification.where(AgricultureSpecifications.hasBurningAgentType(forestType))
                .and(AgricultureSpecifications.hasYear(year));
        return burningEmissionsRepository.findAll(spec);
    }

    private Double getEntericEFBySpeciesType(LivestockSpecies species){
        return switch (species) {
            case DAIRY_GROWING_COWS -> AFOLUConstants.ENTERIC_DAIRY_GROWING_COWS_CH4_EF.getValue();
            case DAIRY_LACTATING_COWS -> AFOLUConstants.ENTERIC_DAIRY_LACTATING_COWS_CH4_EF.getValue();
            case DAIRY_MATURE_COWS -> AFOLUConstants.ENTERIC_DAIRY_MATURE_COWS_CH4_EF.getValue();
            case SHEEP -> AFOLUConstants.ENTERIC_SHEEP_CH4_EF.getValue();
            case GOATS -> AFOLUConstants.ENTERIC_GOATS_CH4_EF.getValue();
            case SWINE -> AFOLUConstants.ENTERIC_SWINE_CH4_EF.getValue();
            default -> 0.0;
        };
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
        return switch (species) {
            case DAIRY_GROWING_COWS -> AFOLUConstants.MANURE_DAIRY_GROWING_COWS_CH4_EF.getValue();
            case DAIRY_LACTATING_COWS -> AFOLUConstants.MANURE_DAIRY_LACTATING_COWS_CH4_EF.getValue();
            case DAIRY_MATURE_COWS -> AFOLUConstants.MANURE_DAIRY_MATURE_COWS_CH4_EF.getValue();
            case SHEEP -> AFOLUConstants.MANURE_SHEEP_CH4_EF.getValue();
            case GOATS -> AFOLUConstants.MANURE_GOATS_CH4_EF.getValue();
            case SWINE -> AFOLUConstants.MANURE_SWINE_CH4_EF.getValue();
            case POULTRY -> AFOLUConstants.MANURE_POULTRY_CH4_EF.getValue();
            case RABBITS -> AFOLUConstants.MANURE_RABBITS_CH4_EF.getValue();
            default -> 0.0;
        };
    }

}
