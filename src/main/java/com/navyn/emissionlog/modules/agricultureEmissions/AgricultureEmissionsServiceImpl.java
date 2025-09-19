package com.navyn.emissionlog.modules.agricultureEmissions;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.Enums.Agriculture.*;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.*;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.DirectLandEmissions.*;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.IndirectLandEmissions.AtmosphericDepositionEmissionsDto;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.IndirectLandEmissions.LeachingAndRunoffEmissionsDto;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.IndirectManureEmissions.LeachingEmissionsDto;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.IndirectManureEmissions.VolatilizationEmissionsDto;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.Livestock.EntericFermentationEmissionsDto;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.*;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions.*;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.IndirectLandEmissions.AtmosphericDepositionEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.IndirectLandEmissions.LeachingAndRunoffEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.IndirectManureEmissions.LeachingEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.IndirectManureEmissions.VolatilizationEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.Livestock.EntericFermentationEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.*;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.DirectLandEmissions.*;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.IndirectLandEmissions.AtmosphericDepositionEmissionsRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.IndirectLandEmissions.LeachingAndRunoffEmissionsRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.IndirectManureEmissions.LeachingEmissionsRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.IndirectManureEmissions.VolatilizationEmissionsRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.Livestock.EntericFermentationEmissionsRepository;

import com.navyn.emissionlog.utils.Specifications.AgricultureSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.navyn.emissionlog.Enums.Agriculture.AFOLUConstants.OTHER_FOREST_CF;
import static com.navyn.emissionlog.utils.Specifications.AgricultureSpecifications.*;

@Service
@RequiredArgsConstructor
public class AgricultureEmissionsServiceImpl implements AgricultureEmissionsService {

    private final AquacultureEmissionsRepository aquacultureEmissionsRepository;
    private final EntericFermentationEmissionsRepository entericFermentationEmissionsRepository;
    private final LimingEmissionsRepository limingEmissionsRepository;
    private final AnimalManureAndCompostEmissionsRepository animalManureAndCompostEmissionsRepository;
    private final RiceCultivationEmissionsRepository riceCultivationEmissionsRepository;
    private final SyntheticFertilizerEmissionsRepository syntheticFertilizerEmissionsRepository;
    private final UreaEmissionsRepository ureaEmissionsRepository;
    private final BurningEmissionsRepository burningEmissionsRepository;
    private final CropResiduesEmissionsRepository cropResiduesEmissionsRepository;
    private final PastureExcretionEmissionsRepository pastureExcretionEmissionsRepository;
    private final MineralSoilEmissionsRepository mineralSoilEmissionsRepository;
    private final VolatilizationEmissionsRepository volatilizationEmissionsRepository;
    private final AtmosphericDepositionEmissionsRepository atmosphericDepositionEmissionsRepository;
    private final LeachingEmissionsRepository leachingEmissionsRepository;
    private final LeachingAndRunoffEmissionsRepository leachingAndRunoffEmissionsRepository;

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
    public List<AnimalManureAndCompostEmissions> getAllAnimalManureAndCompostEmissions(Integer year, OrganicAmendmentTypes amendmentType, LivestockSpecies species) {
        Specification<AnimalManureAndCompostEmissions> spec = Specification.where(hasAmendmentType(amendmentType))
                .and(hasSpecies(species))
                .and(hasYear(year));
        return animalManureAndCompostEmissionsRepository.findAll(spec);
    }

    @Override
    public List<RiceCultivationEmissions> getAllRiceCultivationEmissions(String riceEcosystem, WaterRegime waterRegime, Integer year) {
        Specification<RiceCultivationEmissions> spec = Specification.where(hasRiceEcosystem(riceEcosystem))
                .and(hasWaterRegime(waterRegime))
                .and(hasYear(year));
        return riceCultivationEmissionsRepository.findAll(spec);
    }

    @Override
    public List<SyntheticFertilizerEmissions> getAllSyntheticFertilizerEmissions(Integer year, CropTypes cropType, Fertilizers fertilizerType) {
        Specification<SyntheticFertilizerEmissions> spec = Specification.where(hasFertilizerType(fertilizerType))
                .and(hasYear(year))
                .and(hasCropType(cropType));
        return syntheticFertilizerEmissionsRepository.findAll(spec);
    }

    @Override
    public List<UreaEmissions> getAllUreaEmissions(String fertilizer, Integer year) {

        Specification<UreaEmissions> spec = Specification.where(hasFertilizerName(fertilizer))
                .and(hasYear(year));
        return ureaEmissionsRepository.findAll(spec);
    }

    @Override
    public AquacultureEmissions createAquacultureEmissions(AquacultureEmissionsDto emissionsDto) {
        AquacultureEmissions emissions = new AquacultureEmissions();
        emissions.setYear(emissionsDto.getYear());
        emissions.setActivityDesc(emissionsDto.getActivityDesc());
        emissions.setFishProduction(emissionsDto.getFishProduction());
        emissions.setN2ONEmissions(emissionsDto.getFishProduction()* AFOLUConstants.FISH_N20_EF.getValue());
        emissions.setN2OEmissions(emissionsDto.getFishProduction()* AFOLUConstants.FISH_N20_EF.getValue() *44/28*1000000);
        emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());
        return aquacultureEmissionsRepository.save(emissions);
    }

    @Override
    public EntericFermentationEmissions createEntericFermentationEmissions(EntericFermentationEmissionsDto emissionsDto) {
        EntericFermentationEmissions emissions = new EntericFermentationEmissions();
        emissions.setYear(emissionsDto.getYear());
        emissions.setAnimalPopulation(emissionsDto.getAnimalPopulation());
        emissions.setSpecies(emissionsDto.getSpecies());
        emissions.setCH4Emissions(emissions.getAnimalPopulation()*emissions.getSpecies().getEntericFermentationCH4EF()/1000);
        emissions.setCO2EqEmissions(emissions.getCH4Emissions()* GWP.CH4.getValue());
        return entericFermentationEmissionsRepository.save(emissions);
    }

    @Override
    public LimingEmissions createLimingEmissions(LimingEmissionsDto emissionsDto) {
        LimingEmissions emissions = new LimingEmissions();
        emissions.setYear(emissionsDto.getYear());
        emissions.setMaterial(emissionsDto.getMaterial());
        emissions.setCaCO3Qty(emissionsDto.getCaCO3Qty());
        emissions.setCO2Emissions(emissions.getMaterial().getLimingConstant()*AFOLUConstants.CONVERSION_FACTOR.getValue());
        return limingEmissionsRepository.save(emissions);
    }

    @Override
    public AnimalManureAndCompostEmissions createAnimalManureAndCompostEmissions(AnimalManureAndCompostEmissionsDto emissionsDto) {
        AnimalManureAndCompostEmissions emissions = new AnimalManureAndCompostEmissions();
        emissions.setYear(emissionsDto.getYear());
        emissions.setLivestockSpecies(emissionsDto.getSpecies());
        emissions.setAmendmentType(emissionsDto.getAmendmentType());
        emissions.setPopulation(emissionsDto.getPopulation());
        emissions.setTotalN(emissions.getLivestockSpecies().getManureNitrogenEF()* emissions.getPopulation());
        emissions.setNAvailable(emissions.getTotalN()*emissions.getLivestockSpecies().getMeanLossesOfNinManureMMS());
        emissions.setN2ONEmissions(emissions.getNAvailable()*emissions.getLivestockSpecies().getEFFOrgManureCompostAppliedInFields());
        emissions.setN2OEmissions(emissions.getN2ONEmissions()* 44 / 28);
        emissions.setCH4Emissions(emissions.getPopulation()*emissions.getLivestockSpecies().getManureManagementCH4EF());
        emissions.setCO2EqEmissions(emissions.getN2OEmissions()*265/1000000 + emissions.getCH4Emissions() * GWP.CH4.getValue());
        return animalManureAndCompostEmissionsRepository.save(emissions);
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
        emissions.setNAmount(emissions.getQtyApplied() * emissions.getFertType().getNContent());
        emissions.setN2ONEmissions(emissions.getNAmount()*emissions.getFertType().getNContent());
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

        //Emissions Calculations
        emissions.setCO2Emissions(emissions.getFuelMassAvailable() * emissions.getBurningAgentType().getCO2EF());
        emissions.setCH4Emissions(emissions.getFuelMassAvailable() * emissions.getBurningAgentType().getCH4EF());
        emissions.setN2OEmissions(emissions.getFuelMassAvailable() * emissions.getBurningAgentType().getN2OEF());

        emissions.setCO2EqEmissions(emissions.getCO2Emissions() + (emissions.getCH4Emissions() * GWP.CH4.getValue()) + (emissions.getN2OEmissions() * GWP.N2O.getValue()));
        return burningEmissionsRepository.save(emissions);
    }

    @Override
    public List<BurningEmissions> getAllBurningEmissions(Integer year, BurningAgentType forestType) {
        Specification<BurningEmissions> spec = Specification.where(hasBurningAgentType(forestType))
                .and(hasYear(year));
        return burningEmissionsRepository.findAll(spec);
    }

    @Override
    public CropResiduesEmissions createCropResidueEmissions(CropResiduesEmissionsDto cropResidueEmissionsDto) {
        CropResiduesEmissions emissions = new CropResiduesEmissions();
        emissions.setYear(cropResidueEmissionsDto.getYear());
        emissions.setLandUseCategory(cropResidueEmissionsDto.getLandUseCategory());
        emissions.setCropType(cropResidueEmissionsDto.getCropType());
        emissions.setTotalAreaHarvested(cropResidueEmissionsDto.getTotalAreaHarvested());
        emissions.setHarvestedFreshCropYield(cropResidueEmissionsDto.getHarvestedFreshCropYield());
        emissions.setHarvestedDMYield(emissions.getTotalAreaHarvested() * emissions.getHarvestedFreshCropYield() * cropResidueEmissionsDto.getCropType().getDMFraction());
        emissions.setAGResiduesDryMatter(cropResidueEmissionsDto.getAGResiduesDryMatter());
        emissions.setRatioOfAGResiduesDMToHarvestedYield(emissions.getAGResiduesDryMatter() * 1000 / emissions.getHarvestedDMYield());
        emissions.setRatioOfBelowGroundResiduesToHarvestedYield(cropResidueEmissionsDto.getCropType().getRatioOfBGRToAGBiomass() * (emissions.getAGResiduesDryMatter()*1000 + emissions.getHarvestedDMYield())/emissions.getHarvestedDMYield());
        emissions.setNInCropResiduesReturned(cropResidueEmissionsDto.getNInCropResiduesReturned());
        emissions.setN2ONEmissions(emissions.getNInCropResiduesReturned() * AFOLUConstants.N_CROP_RESIDUES_EF.getValue());
        emissions.setN2OEmissions(emissions.getN2ONEmissions() * 44 / 28);
        emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());
        return cropResiduesEmissionsRepository.save(emissions);
    }

    @Override
    public List<CropResiduesEmissions> getAllCropResidueEmissions(Integer year, CropResiduesCropType cropType, LandUseCategory landUseCategory) {
        Specification<CropResiduesEmissions> spec = Specification.where(hasCropResiduesCropType(cropType))
                .and(hasLandUseCategory(landUseCategory))
                .and(hasYear(year));
        return cropResiduesEmissionsRepository.findAll(spec);
    }

    @Override
    public PastureExcretionEmissions createPastureExcretionEmissions(PastureExcretionsEmissionsDto pastureExcretionEmissionsDto) {
        PastureExcretionEmissions emissions = new PastureExcretionEmissions();
        emissions.setYear(pastureExcretionEmissionsDto.getYear());
        emissions.setLivestockSpecies(pastureExcretionEmissionsDto.getLivestockSpecies());
        emissions.setMMS(pastureExcretionEmissionsDto.getMms());
        emissions.setAnimalPopulation(pastureExcretionEmissionsDto.getAnimalPopulation());
        emissions.setTotalNExcretionDeposited(emissions.getAnimalPopulation() * emissions.getLivestockSpecies().getAnnualNExcretion() * emissions.getLivestockSpecies().getFractionOfManureDepositedOnPasture());
        emissions.setN20NEmissions(emissions.getTotalNExcretionDeposited() * emissions.getLivestockSpecies().getNEFManureDepositedOnPasture());
        emissions.setN2OEmissions(emissions.getN20NEmissions() * 44 / 28);
        emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());
        return pastureExcretionEmissionsRepository.save(emissions);
    }

    @Override
    public MineralSoilEmissions createMineralSoilEmissions(MineralSoilEmissionsDto mineralSoilEmissionsDto) {
        MineralSoilEmissions emissions = new MineralSoilEmissions();
        emissions.setYear(mineralSoilEmissionsDto.getYear());
        emissions.setInitialLandUse(mineralSoilEmissionsDto.getInitialLandUse());
        emissions.setLandUseInReportingYear(mineralSoilEmissionsDto.getLandUseInReportingYear());
        emissions.setAvLossOfSoilC(mineralSoilEmissionsDto.getAvLossOfSoilC());
        emissions.setNMineralisedInMineralSoil(emissions.getAvLossOfSoilC() * 1/emissions.getLandUseInReportingYear().getCNRatioOfSoilOrganicMatter()* 1000);
        emissions.setN20NEmissions(emissions.getNMineralisedInMineralSoil() * emissions.getLandUseInReportingYear().getEFNMineralised());
        emissions.setN2OEmissions(emissions.getN20NEmissions() * 44 / 28);
        emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());
        return mineralSoilEmissionsRepository.save(emissions);
    }

    @Override
    public List<MineralSoilEmissions> getAllMineralSoilEmissions(Integer year, LandUseCategory initialLandUse, LandUseCategory landUseInReportingYear) {
        Specification<MineralSoilEmissions> specification = Specification.where(hasInitialLandUse(initialLandUse))
                .and(hasLandUseInReportingYear(landUseInReportingYear))
                .and(hasYear(year));
        return mineralSoilEmissionsRepository.findAll(specification);
    }

    @Override
    public List<PastureExcretionEmissions> getAllPastureExcretionEmissions(Integer year, LivestockSpecies species, MMS mms) {
        Specification<PastureExcretionEmissions> spec = Specification.where(AgricultureSpecifications.<PastureExcretionEmissions>hasMMS(mms))
                .and(hasYear(year))
                .and(hasLivestockCategory(species));
        return pastureExcretionEmissionsRepository.findAll(spec);
    }

    @Override
    public VolatilizationEmissions createVolatilizationEmissions(VolatilizationEmissionsDto volatilizationEmissionsDto) {
        VolatilizationEmissions emissions = new VolatilizationEmissions();
        emissions.setYear(volatilizationEmissionsDto.getYear());
        emissions.setMMS(volatilizationEmissionsDto.getMms());
        emissions.setLivestockSpecies(volatilizationEmissionsDto.getLivestockSpecies());
        emissions.setAnimalPopulation(volatilizationEmissionsDto.getAnimalPopulation());
        emissions.setTotalNExcretionForMMS(emissions.getAnimalPopulation() * emissions.getLivestockSpecies().getExcretionRate());
        emissions.setManureVolatilizationNLoss(emissions.getTotalNExcretionForMMS() * emissions.getMMS().getFractionOfManureNThatVolatilizes());
        emissions.setIndirectVolatilizationN2OEmissionsFromVolatilization(emissions.getManureVolatilizationNLoss()* AFOLUConstants.EF_N2O_AtmoNDeposition.getValue()* 44/28);
        emissions.setCO2EqEmissions(emissions.getIndirectVolatilizationN2OEmissionsFromVolatilization()* GWP.N2O.getValue());
        return volatilizationEmissionsRepository.save(emissions);
    }

    @Override
    public LeachingEmissions createLeachingEmissions(LeachingEmissionsDto leachingEmissionsDto) {
        LeachingEmissions emissions = new LeachingEmissions();
        emissions.setYear(leachingEmissionsDto.getYear());
        emissions.setMMS(leachingEmissionsDto.getMms());
        emissions.setLivestockSpecies(leachingEmissionsDto.getLivestockSpecies());
        emissions.setTotalNExcretionForMMS(leachingEmissionsDto.getMMSExcretionAmount());
        emissions.setManureNLossDueToLeachingAndRunoff(emissions.getTotalNExcretionForMMS() *  emissions.getMMS().getFractionOfManureNThatLeaches());
        return leachingEmissionsRepository.save(emissions);
    }

    @Override
    public AtmosphericDepositionEmissions createAtmosphericNDepositionEmissions(AtmosphericDepositionEmissionsDto atmosphericNDepositionEmissionsDto) {
        AtmosphericDepositionEmissions emissions = new AtmosphericDepositionEmissions();
        emissions.setYear(atmosphericNDepositionEmissionsDto.getYear());
        emissions.setLandUseCategory(atmosphericNDepositionEmissionsDto.getLandUseCategory());
        emissions.setSyntheticNVolatilized(atmosphericNDepositionEmissionsDto.getSyntheticNThatVolatilizes());
        emissions.setOrganicNAdditions(atmosphericNDepositionEmissionsDto.getOrganicNSoilAdditions());
        emissions.setExcretionsDepositedByGrazingAnimals(atmosphericNDepositionEmissionsDto.getExcretionsDepositedByGrazingAnimals());
        emissions.setAnnualN2ONFromAtmosphericDeposition((emissions.getSyntheticNVolatilized() + (emissions.getOrganicNAdditions() + emissions.getExcretionsDepositedByGrazingAnimals()) * AFOLUConstants.FRACTION_OF_APPLIED_ORGANIC_N_EXCRETIONS_THAT_VOLATILIZES.getValue()) * AFOLUConstants.EF_N2O_AtmoNDeposition.getValue());
        emissions.setN2OEmissions(emissions.getAnnualN2ONFromAtmosphericDeposition() * 44 / 28);
        emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());
        return atmosphericDepositionEmissionsRepository.save(emissions);
    }

    @Override
    public LeachingAndRunoffEmissions createLeachingAndRunoffEmissions(LeachingAndRunoffEmissionsDto leachingAndRunoffEmissionsDto) {
        LeachingAndRunoffEmissions emissions = new LeachingAndRunoffEmissions();
        LeachingAndRunoffEmissions runoffEmissions = new LeachingAndRunoffEmissions();
        emissions.setYear(leachingAndRunoffEmissionsDto.getYear());
        emissions.setLandUseCategory(leachingAndRunoffEmissionsDto.getLandUseCategory());
        emissions.setSyntheticNAppliedToSoil(leachingAndRunoffEmissionsDto.getSyntheticNApplied());
        emissions.setOrganicAdditionsAppliedToSoil(leachingAndRunoffEmissionsDto.getOrganicSoilAdditions());
        emissions.setExcretionsDepositedByGrazingAnimals(leachingAndRunoffEmissionsDto.getExcretionsDepositedByGrazingAnimals());
        emissions.setNInCropResidues(leachingAndRunoffEmissionsDto.getNInCropResidues());
        emissions.setNMineralizedInMineralSoils(leachingAndRunoffEmissionsDto.getNMineralizedInMineralSoils());
        emissions.setN2ONProducedFromLeachingAndRunoff((emissions.getSyntheticNAppliedToSoil()
                + emissions.getOrganicAdditionsAppliedToSoil()
                + emissions.getExcretionsDepositedByGrazingAnimals()
                + emissions.getNInCropResidues()
                + emissions.getNMineralizedInMineralSoils()) * emissions.getLandUseCategory().getNFractionAddedToSoilPostLeaching() * emissions.getLandUseCategory().getEF_N2O_LeachAndRunoffNSoilAdditive());
        emissions.setN2OEmissions(emissions.getN2ONProducedFromLeachingAndRunoff() * 44 / 28);
        emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());
        return leachingAndRunoffEmissionsRepository.save(emissions);
    }

    @Override
    public List<AtmosphericDepositionEmissions> getAllAtmosphericNDepositionEmissions(Integer year, LandUseCategory landUseCategory) {
        Specification<AtmosphericDepositionEmissions> spec = Specification.where(AgricultureSpecifications.<AtmosphericDepositionEmissions> hasYear(year))
                .and(hasLandUseCategory(landUseCategory));
        return atmosphericDepositionEmissionsRepository.findAll(spec);
    }

    @Override
    public List<LeachingAndRunoffEmissions> getAllLeachingAndRunoffEmissions(Integer year, LandUseCategory landUseCategory) {
        Specification<LeachingAndRunoffEmissions> spec = Specification.where(AgricultureSpecifications.<LeachingAndRunoffEmissions>hasLandUseCategory(landUseCategory))
                .and(hasYear(year));
        return leachingAndRunoffEmissionsRepository.findAll(spec);
    }

    @Override
    public List<LeachingEmissions> getAllLeachingEmissions(Integer year, MMS mms, LivestockSpecies species) {
        Specification<LeachingEmissions> spec = Specification.where(AgricultureSpecifications.<LeachingEmissions> hasYear(year))
                .and(hasMMS(mms))
                .and(hasLivestockCategory(species));
        return leachingEmissionsRepository.findAll(spec);
    }

    @Override
    public List<VolatilizationEmissions> getAllVolatilizationEmissions(Integer year, MMS mms, LivestockSpecies species) {
        Specification<VolatilizationEmissions> spec = Specification.where(AgricultureSpecifications.<VolatilizationEmissions>hasYear(year))
                .and(hasMMS(mms))
                .and(hasLivestockCategory(species));
        return volatilizationEmissionsRepository.findAll(spec);
    }
}
