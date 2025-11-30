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
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.Livestock.ManureManagementEmissionsRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.models.Livestock.ManureManagementEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.Livestock.ManureManagementEmissionsDto;

import com.navyn.emissionlog.utils.DashboardData;
import com.navyn.emissionlog.utils.Specifications.AgricultureSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.navyn.emissionlog.Enums.Agriculture.AFOLUConstants.OTHER_FOREST_CF;
import static com.navyn.emissionlog.utils.Specifications.AgricultureSpecifications.*;
import static java.util.stream.Collectors.groupingBy;

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
    private final ManureManagementEmissionsRepository manureManagementEmissionsRepository;

    @Override
    public List<AquacultureEmissions> getAllAquacultureEmissions(Integer year) {
        Specification<AquacultureEmissions> spec = Specification.where(hasYear(year));
        return aquacultureEmissionsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }

    @Override
    public List<EntericFermentationEmissions> getAllEntericFermentationEmissions(Integer year, LivestockSpecies species) {
        Specification<EntericFermentationEmissions> spec =
                Specification.<EntericFermentationEmissions>where(hasYear(year))
                        .and(hasSpecies(species));
        return entericFermentationEmissionsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }

    @Override
    public List<LimingEmissions> getAllLimingEmissions(Integer year, LimingMaterials limingMaterials) {
        Specification<LimingEmissions> spec = Specification.where(hasLimingMaterial(limingMaterials)).and(hasYear(year));
        return limingEmissionsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }

    @Override
    public List<AnimalManureAndCompostEmissions> getAllAnimalManureAndCompostEmissions(Integer year, OrganicAmendmentTypes amendmentType, LivestockSpecies species) {
        Specification<AnimalManureAndCompostEmissions> spec = Specification.where(hasAmendmentType(amendmentType))
                .and(hasSpecies(species))
                .and(hasYear(year));
        return animalManureAndCompostEmissionsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }

    @Override
    public List<RiceCultivationEmissions> getAllRiceCultivationEmissions(String riceEcosystem, WaterRegime waterRegime, Integer year) {
        Specification<RiceCultivationEmissions> spec = Specification.where(hasRiceEcosystem(riceEcosystem))
                .and(hasWaterRegime(waterRegime))
                .and(hasYear(year));
        return riceCultivationEmissionsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }

    @Override
    public List<SyntheticFertilizerEmissions> getAllSyntheticFertilizerEmissions(Integer year, CropTypes cropType, Fertilizers fertilizerType) {
        Specification<SyntheticFertilizerEmissions> spec = Specification.where(hasFertilizerType(fertilizerType))
                .and(hasYear(year))
                .and(hasCropType(cropType));
        return syntheticFertilizerEmissionsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }

    @Override
    public List<UreaEmissions> getAllUreaEmissions(String fertilizer, Integer year) {

        Specification<UreaEmissions> spec = Specification.where(hasFertilizerName(fertilizer))
                .and(hasYear(year));
        return ureaEmissionsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
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
        emissions.setAmendmentType(getAmendmentTypeByLivestockSpecies(emissionsDto.getSpecies()));
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
        return burningEmissionsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
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
        return cropResiduesEmissionsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
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
        return mineralSoilEmissionsRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "year"));
    }

    @Override
    public List<PastureExcretionEmissions> getAllPastureExcretionEmissions(Integer year, LivestockSpecies species, MMS mms) {
        Specification<PastureExcretionEmissions> spec = Specification.where(AgricultureSpecifications.<PastureExcretionEmissions>hasMMS(mms))
                .and(hasYear(year))
                .and(hasLivestockCategory(species));
        return pastureExcretionEmissionsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
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
        return atmosphericDepositionEmissionsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }

    @Override
    public List<LeachingAndRunoffEmissions> getAllLeachingAndRunoffEmissions(Integer year, LandUseCategory landUseCategory) {
        Specification<LeachingAndRunoffEmissions> spec = Specification.where(AgricultureSpecifications.<LeachingAndRunoffEmissions>hasLandUseCategory(landUseCategory))
                .and(hasYear(year));
        return leachingAndRunoffEmissionsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }

    @Override
    public List<LeachingEmissions> getAllLeachingEmissions(Integer year, MMS mms, LivestockSpecies species) {
        Specification<LeachingEmissions> spec = Specification.where(AgricultureSpecifications.<LeachingEmissions> hasYear(year))
                .and(hasMMS(mms))
                .and(hasLivestockCategory(species));
        return leachingEmissionsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }

    @Override
    public List<VolatilizationEmissions> getAllVolatilizationEmissions(Integer year, MMS mms, LivestockSpecies species) {
        Specification<VolatilizationEmissions> spec = Specification.where(AgricultureSpecifications.<VolatilizationEmissions>hasYear(year))
                .and(hasMMS(mms))
                .and(hasLivestockCategory(species));
        return volatilizationEmissionsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }

    private OrganicAmendmentTypes getAmendmentTypeByLivestockSpecies(LivestockSpecies species){
        switch (species) {
            case DAIRY_LACTATING_COWS:
                return OrganicAmendmentTypes.MANURE_LACTATING_COWS;
            case DAIRY_GROWING_COWS:
                return OrganicAmendmentTypes.MANURE_GROWING_COWS;
            case DAIRY_MATURE_COWS:
                return OrganicAmendmentTypes.MANURE_MATURE_COWS;
            case SWINE:
                return OrganicAmendmentTypes.MANURE_SWINE;
            case POULTRY:
                return OrganicAmendmentTypes.MANURE_POULTRY;
            case SHEEP:
                return OrganicAmendmentTypes.MANURE_SHEEP;
            case GOATS:
                return OrganicAmendmentTypes.MANURE_GOATS;
            default:
                return OrganicAmendmentTypes.MANURE_RABBITS;
        }
    }

    @Override
    public ManureManagementEmissions createManureManagementEmissions(ManureManagementEmissionsDto dto) {
        ManureManagementEmissions emission = new ManureManagementEmissions();
        
        // Map input fields
        emission.setYear(dto.getYear());
        emission.setSpecies(dto.getSpecies());
        emission.setManureManagementSystem(dto.getManureManagementSystem());
        emission.setAnimalPopulation(dto.getAnimalPopulation());
        emission.setAverageAnnualTemperature(dto.getAverageAnnualTemperature());
        emission.setAverageAnimalWeight(dto.getAverageAnimalWeight() != null ? 
            dto.getAverageAnimalWeight() : getDefaultWeight(dto.getSpecies()));
        
        // 1. Calculate Volatile Solids
        double vsRate = getVolatileSolidsRate(dto.getSpecies()); // kg VS/day/animal
        emission.setVolatileSolidsExcretion(vsRate);
        emission.setTotalVolatileSolids(vsRate * dto.getAnimalPopulation() * 365);
        
        // 2. Calculate Methane Emissions
        double mcf = getMethaneConversionFactor(dto.getManureManagementSystem(), 
                                                 dto.getAverageAnnualTemperature());
        emission.setMethaneConversionFactor(mcf);
        
        double ch4Emissions = emission.getTotalVolatileSolids() * 
                              ManureManagementConstants.BO_FACTOR.getValue() * 
                              mcf * 
                              ManureManagementConstants.CH4_DENSITY.getValue();
        emission.setCH4EmissionsFromManure(ch4Emissions);
        emission.setCH4_CO2Eq(ch4Emissions * GWP.CH4.getValue());
        
        // 3. Calculate Nitrogen Excretion
        double nExcretion = getNitrogenExcretionRate(dto.getSpecies()) * 
                            dto.getAnimalPopulation();
        emission.setNitrogenExcretion(nExcretion);
        emission.setNitrogenInManure(nExcretion); // Simplified - could have loss factors
        
        // 4. Calculate Direct N2O Emissions
        double directN2O = emission.getNitrogenInManure() * 
                           ManureManagementConstants.N2O_DIRECT_EF.getValue() * 
                           ManureManagementConstants.N2O_CONVERSION_FACTOR.getValue();
        emission.setDirectN2OEmissions(directN2O);
        emission.setDirectN2O_CO2Eq(directN2O * GWP.N2O.getValue());
        
        // 5. Calculate Indirect N2O - Volatilization
        double volatilizedN = emission.getNitrogenInManure() * 
                              ManureManagementConstants.FRAC_GASMS.getValue();
        emission.setVolatilizedNitrogen(volatilizedN);
        
        double indirectN2OVol = volatilizedN * 
                                ManureManagementConstants.N2O_VOLATILIZATION_EF.getValue() * 
                                ManureManagementConstants.N2O_CONVERSION_FACTOR.getValue();
        emission.setIndirectN2OFromVolatilization(indirectN2OVol);
        emission.setVolatilizationN2O_CO2Eq(indirectN2OVol * GWP.N2O.getValue());
        
        // 6. Calculate Indirect N2O - Leaching
        double leachedN = emission.getNitrogenInManure() * 
                          ManureManagementConstants.FRAC_LEACH.getValue();
        emission.setLeachedNitrogen(leachedN);
        
        double indirectN2OLeach = leachedN * 
                                  ManureManagementConstants.N2O_LEACHING_EF.getValue() * 
                                  ManureManagementConstants.N2O_CONVERSION_FACTOR.getValue();
        emission.setIndirectN2OFromLeaching(indirectN2OLeach);
        emission.setLeachingN2O_CO2Eq(indirectN2OLeach * GWP.N2O.getValue());
        
        // 7. Calculate Totals
        emission.setTotalN2OEmissions(directN2O + indirectN2OVol + indirectN2OLeach);
        emission.setTotalN2O_CO2Eq(emission.getDirectN2O_CO2Eq() + 
                                    emission.getVolatilizationN2O_CO2Eq() + 
                                    emission.getLeachingN2O_CO2Eq());
        emission.setTotalCO2EqEmissions(emission.getCH4_CO2Eq() + 
                                         emission.getTotalN2O_CO2Eq());
        
        return manureManagementEmissionsRepository.save(emission);
    }

    @Override
    public List<ManureManagementEmissions> getAllManureManagementEmissions(
            Integer year, ManureManagementLivestock species, ManureManagementSystem mms) {
        Specification<ManureManagementEmissions> spec = 
            Specification.<ManureManagementEmissions>where(hasYear(year))
                .and(hasManureManagementLivestock(species))
                .and(hasManureManagementSystem(mms));
        return manureManagementEmissionsRepository.findAll(
            spec, Sort.by(Sort.Direction.DESC, "year")
        );
    }
    
//    @Override
//    public AquacultureEmissions updateAquacultureEmissions(UUID id, AquacultureEmissionsDto dto) {
//        AquacultureEmissions emissions = aquacultureEmissionsRepository.findById(id)
//            .orElseThrow(() -> new RuntimeException("Aquaculture emissions not found with id: " + id));
//
//        emissions.setYear(dto.getYear());
//        emissions.setActivityDesc(dto.getActivityDesc());
//        emissions.setFishProduction(dto.getFishProduction());
//        emissions.setN2ONEmissions(dto.getFishProduction() * AFOLUConstants.FISH_N20_EF.getValue());
//        emissions.setN2OEmissions(dto.getFishProduction() * AFOLUConstants.FISH_N20_EF.getValue() * 44/28 * 1000000);
//        emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());
//
//        return aquacultureEmissionsRepository.save(emissions);
//    }
//
//    @Override
//    public EntericFermentationEmissions updateEntericFermentationEmissions(UUID id, EntericFermentationEmissionsDto dto) {
//        EntericFermentationEmissions emissions = entericFermentationEmissionsRepository.findById(id)
//            .orElseThrow(() -> new RuntimeException("Enteric Fermentation emissions not found with id: " + id));
//
//        emissions.setYear(dto.getYear());
//        emissions.setAnimalPopulation(dto.getAnimalPopulation());
//        emissions.setSpecies(dto.getSpecies());
//        emissions.setCH4Emissions(emissions.getAnimalPopulation() * emissions.getSpecies().getEntericFermentationCH4EF() / 1000);
//        emissions.setCO2EqEmissions(emissions.getCH4Emissions() * GWP.CH4.getValue());
//
//        return entericFermentationEmissionsRepository.save(emissions);
//    }
//
//    @Override
//    public LimingEmissions updateLimingEmissions(UUID id, LimingEmissionsDto dto) {
//        LimingEmissions emissions = limingEmissionsRepository.findById(id)
//            .orElseThrow(() -> new RuntimeException("Liming emissions not found with id: " + id));
//
//        emissions.setYear(dto.getYear());
//        emissions.setMaterial(dto.getMaterial());
//        emissions.setCaCO3Qty(dto.getCaCO3Qty());
//        emissions.setCO2Emissions(emissions.getMaterial().getLimingConstant() * AFOLUConstants.CONVERSION_FACTOR.getValue());
//
//        return limingEmissionsRepository.save(emissions);
//    }
//
//    @Override
//    public AnimalManureAndCompostEmissions updateAnimalManureAndCompostEmissions(UUID id, AnimalManureAndCompostEmissionsDto dto) {
//        AnimalManureAndCompostEmissions emissions = animalManureAndCompostEmissionsRepository.findById(id)
//            .orElseThrow(() -> new RuntimeException("Animal Manure and Compost emissions not found with id: " + id));
//
//        emissions.setYear(dto.getYear());
//        emissions.setLivestockSpecies(dto.getSpecies());
//        emissions.setAmendmentType(getAmendmentTypeByLivestockSpecies(dto.getSpecies()));
//        emissions.setPopulation(dto.getPopulation());
//        emissions.setTotalN(emissions.getLivestockSpecies().getManureNitrogenEF() * emissions.getPopulation());
//        emissions.setNAvailable(emissions.getTotalN() * emissions.getLivestockSpecies().getMeanLossesOfNinManureMMS());
//        emissions.setN2ONEmissions(emissions.getNAvailable() * emissions.getLivestockSpecies().getEFFOrgManureCompostAppliedInFields());
//        emissions.setN2OEmissions(emissions.getN2ONEmissions() * 44 / 28);
//        emissions.setCH4Emissions(emissions.getPopulation() * emissions.getLivestockSpecies().getManureManagementCH4EF());
//        emissions.setCO2EqEmissions((emissions.getN2OEmissions() * GWP.N2O.getValue()) + (emissions.getCH4Emissions() * GWP.CH4.getValue()));
//
//        return animalManureAndCompostEmissionsRepository.save(emissions);
//    }
//
//    @Override
//    public RiceCultivationEmissions updateRiceCultivationEmissions(UUID id, RiceCultivationEmissionsDto dto) {
//        RiceCultivationEmissions emissions = riceCultivationEmissionsRepository.findById(id)
//            .orElseThrow(() -> new RuntimeException("Rice Cultivation emissions not found with id: " + id));
//
//        emissions.setYear(dto.getYear());
//        emissions.setRiceEcosystem(dto.getRiceEcosystem());
//        emissions.setWaterRegime(dto.getWaterRegime());
//        emissions.setAreaUnderRice(dto.getAreaUnderRice());
//        emissions.setSeasonalEmissionFactor(dto.getSeasonalEmissionFactor());
//        emissions.setAnnualCH4Emissions(dto.getAreaUnderRice() * dto.getSeasonalEmissionFactor() * 365 / 1000);
//        emissions.setCO2EqEmissions(emissions.getAnnualCH4Emissions() * GWP.CH4.getValue());
//
//        return riceCultivationEmissionsRepository.save(emissions);
//    }
    
//    @Override
//    public SyntheticFertilizerEmissions updateSyntheticFertilizerEmissions(UUID id, SyntheticFertilizerEmissionsDto dto) {
//        SyntheticFertilizerEmissions emissions = syntheticFertilizerEmissionsRepository.findById(id)
//            .orElseThrow(() -> new RuntimeException("Synthetic Fertilizer emissions not found with id: " + id));
//
//        emissions.setYear(dto.getYear());
//        emissions.setCropType(dto.getCropType());
//        emissions.setFertilizerType(dto.getFertilizerType());
//        emissions.setFertilizerConsumption(dto.getFertilizerConsumption());
//        emissions.setN2ONEmissions(emissions.getFertilizerConsumption() * emissions.getFertilizerType().getNitrogenContentPercentage() * AFOLUConstants.SYNTHETIC_FERTILIZER_EF.getValue());
//        emissions.setN2OEmissions(emissions.getN2ONEmissions() * 44 / 28);
//        emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());
//
//        return syntheticFertilizerEmissionsRepository.save(emissions);
//    }
    
//    @Override
//    public UreaEmissions updateUreaEmissions(UUID id, UreaEmissionsDto dto) {
//        UreaEmissions emissions = ureaEmissionsRepository.findById(id)
//            .orElseThrow(() -> new RuntimeException("Urea emissions not found with id: " + id));
//
//        emissions.setYear(dto.getYear());
//        emissions.setFertilizerName(dto.getFertilizerName());
//        emissions.setUreaApplied(dto.getUreaApplied());
//        emissions.setCO2Emissions(emissions.getUreaApplied() * AFOLUConstants.UREA_EF.getValue() * AFOLUConstants.CONVERSION_FACTOR.getValue());
//
//        return ureaEmissionsRepository.save(emissions);
//    }
    
//    @Override
//    public BurningEmissions updateBurningEmissions(UUID id, BurningEmissionsDto dto) {
//        BurningEmissions emissions = burningEmissionsRepository.findById(id)
//            .orElseThrow(() -> new RuntimeException("Burning emissions not found with id: " + id));
//
//        emissions.setYear(dto.getYear());
//        emissions.setForestType(dto.getForestType());
//        emissions.setAreaBurned(dto.getAreaBurned());
//        emissions.setMassFuelAvailableForCombustion(dto.getAreaBurned() * dto.getForestType().getMassFuelAvailableForCombustion());
//        emissions.setCombustionFactor(dto.getForestType().getCombustionFactor());
//        emissions.setMassFuelActuallyCombust(emissions.getMassFuelAvailableForCombustion() * emissions.getCombustionFactor());
//        emissions.setEmissionFactorCO2(dto.getForestType().getCO2EmissionFactor());
//        emissions.setEmissionFactorCH4(dto.getForestType().getCH4EmissionFactor());
//        emissions.setEmissionFactorN2O(dto.getForestType().getN2OEmissionFactor());
//        emissions.setCO2Emissions(emissions.getMassFuelActuallyCombust() * emissions.getEmissionFactorCO2());
//        emissions.setCH4Emissions(emissions.getMassFuelActuallyCombust() * emissions.getEmissionFactorCH4() / 1000);
//        emissions.setN2OEmissions(emissions.getMassFuelActuallyCombust() * emissions.getEmissionFactorN2O() / 1000);
//        emissions.setCO2EqEmissions((emissions.getCH4Emissions() * GWP.CH4.getValue()) + (emissions.getN2OEmissions() * GWP.N2O.getValue()));
//
//        return burningEmissionsRepository.save(emissions);
//    }
    
//    @Override
//    public CropResiduesEmissions updateCropResidueEmissions(UUID id, CropResiduesEmissionsDto dto) {
//        CropResiduesEmissions emissions = cropResiduesEmissionsRepository.findById(id)
//            .orElseThrow(() -> new RuntimeException("Crop Residues emissions not found with id: " + id));
//
//        emissions.setYear(dto.getYear());
//        emissions.setCropType(dto.getCropType());
//        emissions.setLandUseCategory(dto.getLandUseCategory());
//        emissions.setAreaUnderCrop(dto.getAreaUnderCrop());
//        emissions.setCropYield(dto.getCropYield());
//        emissions.setResidueNitrogenContent(dto.getCropType().getResidueNitrogenContent());
//        emissions.setResidueToProductRatio(dto.getCropType().getResidueToProductRatio());
//        emissions.setSlopeOfLinearRelationship(dto.getCropType().getSlopeOfLinearRelationship());
//        emissions.setInterceptOfLinearRelationship(dto.getCropType().getInterceptOfLinearRelationship());
//        emissions.setDryMatterFraction(dto.getCropType().getDryMatterFraction());
//        emissions.setFractionOfResidueRemoved(dto.getCropType().getFractionOfResidueRemoved());
//        emissions.setFractionOfResidueBurned(dto.getCropType().getFractionOfResidueBurned());
//        emissions.setCombustionFactor(AFOLUConstants.COMBUSTION_FACTOR.getValue());
//        emissions.setResidueNContent((dto.getCropType().getSlopeOfLinearRelationship() * dto.getCropYield()) + dto.getCropType().getInterceptOfLinearRelationship());
//        emissions.setTotalResidueProduced(dto.getAreaUnderCrop() * dto.getCropYield() * dto.getCropType().getResidueToProductRatio() * dto.getCropType().getDryMatterFraction());
//        emissions.setNinResiduesReturnedToSoil(emissions.getResidueNContent() * (1 - dto.getCropType().getFractionOfResidueRemoved() - dto.getCropType().getFractionOfResidueBurned()));
//        emissions.setN2ONEmissions(emissions.getNinResiduesReturnedToSoil() * AFOLUConstants.CROP_RESIDUE_EF.getValue());
//        emissions.setN2OEmissions(emissions.getN2ONEmissions() * 44 / 28);
//        emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());
//
//        return cropResiduesEmissionsRepository.save(emissions);
//    }
    
//    @Override
//    public PastureExcretionEmissions updatePastureExcretionEmissions(UUID id, PastureExcretionsEmissionsDto dto) {
//        PastureExcretionEmissions emissions = pastureExcretionEmissionsRepository.findById(id)
//            .orElseThrow(() -> new RuntimeException("Pasture Excretion emissions not found with id: " + id));
//
//        emissions.setYear(dto.getYear());
//        emissions.setMms(dto.getMms());
//        emissions.setLivestockSpecies(dto.getSpecies());
//        emissions.setAnimalPopulation(dto.getAnimalPopulation());
//        emissions.setN2ONEmissions(emissions.getLivestockSpecies().getPastureExcretionN2ONEF() * emissions.getAnimalPopulation());
//        emissions.setN2OEmissions(emissions.getN2ONEmissions() * 44 / 28);
//        emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());
//
//        return pastureExcretionEmissionsRepository.save(emissions);
//    }
    
//    @Override
//    public MineralSoilEmissions updateMineralSoilEmissions(UUID id, MineralSoilEmissionsDto dto) {
//        MineralSoilEmissions emissions = mineralSoilEmissionsRepository.findById(id)
//            .orElseThrow(() -> new RuntimeException("Mineral Soil emissions not found with id: " + id));
//
//        emissions.setYear(dto.getYear());
//        emissions.setInitialLandUse(dto.getInitialLandUse());
//        emissions.setLandUseInReportingYear(dto.getLandUseInReportingYear());
//        emissions.setAreaUndergoingLandUseConversion(dto.getAreaUndergoingLandUseConversion());
//        emissions.setSoilOrganicCarbonStockInitialLandUse(dto.getSoilOrganicCarbonStockInitialLandUse());
//        emissions.setSoilOrganicCarbonStockConvertedLand(dto.getSoilOrganicCarbonStockConvertedLand());
//        emissions.setTimeToTransition(dto.getTimeToTransition());
//        emissions.setAnnualChangeInCarbonStocks((emissions.getSoilOrganicCarbonStockConvertedLand() - emissions.getSoilOrganicCarbonStockInitialLandUse()) / emissions.getTimeToTransition());
//        emissions.setAnnualEmissions(emissions.getAnnualChangeInCarbonStocks() * emissions.getAreaUndergoingLandUseConversion() * AFOLUConstants.CONVERSION_FACTOR.getValue());
//
//        return mineralSoilEmissionsRepository.save(emissions);
//    }
    
//    @Override
//    public VolatilizationEmissions updateVolatilizationEmissions(UUID id, VolatilizationEmissionsDto dto) {
//        VolatilizationEmissions emissions = volatilizationEmissionsRepository.findById(id)
//            .orElseThrow(() -> new RuntimeException("Volatilization emissions not found with id: " + id));
//
//        emissions.setYear(dto.getYear());
//        emissions.setMms(dto.getMms());
//        emissions.setLivestockSpecies(dto.getSpecies());
//        emissions.setAnimalPopulation(dto.getAnimalPopulation());
//        emissions.setNitrogenExcretedPerAnimal(emissions.getLivestockSpecies().getManureNitrogenEF());
//        emissions.setTotalNitrogenExcreted(emissions.getNitrogenExcretedPerAnimal() * emissions.getAnimalPopulation());
//        emissions.setFractionVolatilized(emissions.getMms().getVolatilizationFraction());
//        emissions.setVolatilizedNitrogen(emissions.getTotalNitrogenExcreted() * emissions.getFractionVolatilized());
//        emissions.setN2ONEmissions(emissions.getVolatilizedNitrogen() * AFOLUConstants.VOLATILIZATION_EF.getValue());
//        emissions.setN2OEmissions(emissions.getN2ONEmissions() * 44 / 28);
//        emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());
//
//        return volatilizationEmissionsRepository.save(emissions);
//    }
//
//    @Override
//    public LeachingEmissions updateLeachingEmissions(UUID id, LeachingEmissionsDto dto) {
//        LeachingEmissions emissions = leachingEmissionsRepository.findById(id)
//            .orElseThrow(() -> new RuntimeException("Leaching emissions not found with id: " + id));
//
//        emissions.setYear(dto.getYear());
//        emissions.setMms(dto.getMms());
//        emissions.setLivestockSpecies(dto.getSpecies());
//        emissions.setAnimalPopulation(dto.getAnimalPopulation());
//        emissions.setNitrogenExcretedPerAnimal(emissions.getLivestockSpecies().getManureNitrogenEF());
//        emissions.setTotalNitrogenExcreted(emissions.getNitrogenExcretedPerAnimal() * emissions.getAnimalPopulation());
//        emissions.setFractionLeached(emissions.getMms().getLeachingFraction());
//        emissions.setLeachedNitrogen(emissions.getTotalNitrogenExcreted() * emissions.getFractionLeached());
//        emissions.setN2ONEmissions(emissions.getLeachedNitrogen() * AFOLUConstants.LEACHING_EF.getValue());
//        emissions.setN2OEmissions(emissions.getN2ONEmissions() * 44 / 28);
//        emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());
//
//        return leachingEmissionsRepository.save(emissions);
//    }
//
//    @Override
//    public AtmosphericDepositionEmissions updateAtmosphericNDepositionEmissions(UUID id, AtmosphericDepositionEmissionsDto dto) {
//        AtmosphericDepositionEmissions emissions = atmosphericDepositionEmissionsRepository.findById(id)
//            .orElseThrow(() -> new RuntimeException("Atmospheric Deposition emissions not found with id: " + id));
//
//        emissions.setYear(dto.getYear());
//        emissions.setLandCategory(dto.getLandCategory());
//        emissions.setNitrogenDeposited(dto.getNitrogenDeposited());
//        emissions.setN2ONEmissions(emissions.getNitrogenDeposited() * AFOLUConstants.ATMOSPHERIC_DEPOSITION_EF.getValue());
//        emissions.setN2OEmissions(emissions.getN2ONEmissions() * 44 / 28);
//        emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());
//
//        return atmosphericDepositionEmissionsRepository.save(emissions);
//    }
//
//    @Override
//    public LeachingAndRunoffEmissions updateLeachingAndRunoffEmissions(UUID id, LeachingAndRunoffEmissionsDto dto) {
//        LeachingAndRunoffEmissions emissions = leachingAndRunoffEmissionsRepository.findById(id)
//            .orElseThrow(() -> new RuntimeException("Leaching and Runoff emissions not found with id: " + id));
//
//        emissions.setYear(dto.getYear());
//        emissions.setLandCategory(dto.getLandCategory());
//        emissions.setNitrogenLeached(dto.getNitrogenLeached());
//        emissions.setN2ONEmissions(emissions.getNitrogenLeached() * AFOLUConstants.LEACHING_AND_RUNOFF_EF.getValue());
//        emissions.setN2OEmissions(emissions.getN2ONEmissions() * 44 / 28);
//        emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());
//
//        return leachingAndRunoffEmissionsRepository.save(emissions);
//    }
    
//    @Override
//    public ManureManagementEmissions updateManureManagementEmissions(UUID id, ManureManagementEmissionsDto dto) {
//        ManureManagementEmissions emission = manureManagementEmissionsRepository.findById(id)
//            .orElseThrow(() -> new RuntimeException("Manure Management emissions not found with id: " + id));
//
//        // Map input fields
//        emission.setYear(dto.getYear());
//        emission.setSpecies(dto.getSpecies());
//        emission.setManureManagementSystem(dto.getManureManagementSystem());
//        emission.setAnimalPopulation(dto.getAnimalPopulation());
//        emission.setAverageAnnualTemperature(dto.getAverageAnnualTemperature());
//        emission.setAverageAnimalWeight(dto.getAverageAnimalWeight() != null ?
//            dto.getAverageAnimalWeight() : getDefaultWeight(dto.getSpecies()));
//
//        // 1. Calculate Volatile Solids
//        double vsRate = getVolatileSolidsRate(dto.getSpecies());
//        emission.setVolatileSolidsExcretion(vsRate);
//        emission.setTotalVolatileSolids(vsRate * dto.getAnimalPopulation() * 365);
//
//        // 2. Calculate Methane Emissions
//        double mcf = getMethaneConversionFactor(dto.getManureManagementSystem(),
//                                                 dto.getAverageAnnualTemperature());
//        emission.setMethaneConversionFactor(mcf);
//
//        double ch4Emissions = emission.getTotalVolatileSolids() *
//                              ManureManagementConstants.BO_FACTOR.getValue() *
//                              mcf *
//                              ManureManagementConstants.CH4_DENSITY.getValue();
//        emission.setCH4EmissionsFromManure(ch4Emissions);
//        emission.setCH4_CO2Eq(ch4Emissions * GWP.CH4.getValue());
//
//        // 3. Calculate Nitrogen Excretion
//        double nExcretion = getNitrogenExcretionRate(dto.getSpecies()) *
//                            dto.getAnimalPopulation();
//        emission.setNitrogenExcretion(nExcretion);
//        emission.setNitrogenInManure(nExcretion);
//
//        // 4. Calculate Direct N2O Emissions
//        double directN2O = emission.getNitrogenInManure() *
//                           ManureManagementConstants.N2O_DIRECT_EF.getValue() *
//                           ManureManagementConstants.N2O_CONVERSION_FACTOR.getValue();
//        emission.setDirectN2OEmissions(directN2O);
//        emission.setDirectN2O_CO2Eq(directN2O * GWP.N2O.getValue());
//
//        // 5. Calculate Indirect N2O - Volatilization
//        double volatilizedN = emission.getNitrogenInManure() *
//                              ManureManagementConstants.FRAC_GASMS.getValue();
//        emission.setVolatilizedNitrogen(volatilizedN);
//
//        double indirectN2OVol = volatilizedN *
//                                ManureManagementConstants.N2O_VOLATILIZATION_EF.getValue() *
//                                ManureManagementConstants.N2O_CONVERSION_FACTOR.getValue();
//        emission.setIndirectN2OFromVolatilization(indirectN2OVol);
//        emission.setVolatilizationN2O_CO2Eq(indirectN2OVol * GWP.N2O.getValue());
//
//        // 6. Calculate Indirect N2O - Leaching
//        double leachedN = emission.getNitrogenInManure() *
//                          ManureManagementConstants.FRAC_LEACH.getValue();
//        emission.setLeachedNitrogen(leachedN);
//
//        double indirectN2OLeach = leachedN *
//                                  ManureManagementConstants.N2O_LEACHING_EF.getValue() *
//                                  ManureManagementConstants.N2O_CONVERSION_FACTOR.getValue();
//        emission.setIndirectN2OFromLeaching(indirectN2OLeach);
//        emission.setLeachingN2O_CO2Eq(indirectN2OLeach * GWP.N2O.getValue());
//
//        // 7. Calculate Totals
//        emission.setTotalN2OEmissions(directN2O + indirectN2OVol + indirectN2OLeach);
//        emission.setTotalN2O_CO2Eq(emission.getDirectN2O_CO2Eq() +
//                                    emission.getVolatilizationN2O_CO2Eq() +
//                                    emission.getLeachingN2O_CO2Eq());
//        emission.setTotalCO2EqEmissions(emission.getCH4_CO2Eq() +
//                                         emission.getTotalN2O_CO2Eq());
//
//        return manureManagementEmissionsRepository.save(emission);
//    }
    
    // Helper methods for Manure Management
    private double getVolatileSolidsRate(ManureManagementLivestock species) {
        return switch (species) {
            case DAIRY_CATTLE -> ManureManagementConstants.VS_DAIRY_CATTLE.getValue();
            case NON_DAIRY_CATTLE -> ManureManagementConstants.VS_NON_DAIRY_CATTLE.getValue();
            case BUFFALO -> ManureManagementConstants.VS_BUFFALO.getValue();
            case SWINE -> ManureManagementConstants.VS_SWINE.getValue();
            case SHEEP -> ManureManagementConstants.VS_SHEEP.getValue();
            case GOATS -> ManureManagementConstants.VS_GOATS.getValue();
            case CAMELS -> ManureManagementConstants.VS_CAMELS.getValue();
            case HORSES -> ManureManagementConstants.VS_HORSES.getValue();
            case MULES_ASSES -> ManureManagementConstants.VS_MULES_ASSES.getValue();
            case POULTRY_CHICKEN, POULTRY_DUCKS, POULTRY_TURKEYS -> 
                ManureManagementConstants.VS_POULTRY.getValue();
        };
    }
    
    private double getMethaneConversionFactor(ManureManagementSystem mms, double temperature) {
        // Base MCF values - could add temperature adjustment logic here if needed
        return switch (mms) {
            case PASTURE_RANGE_PADDOCK -> ManureManagementConstants.MCF_PASTURE.getValue();
            case DAILY_SPREAD -> ManureManagementConstants.MCF_DAILY_SPREAD.getValue();
            case SOLID_STORAGE -> ManureManagementConstants.MCF_SOLID_STORAGE.getValue();
            case DRY_LOT -> ManureManagementConstants.MCF_DRY_LOT.getValue();
            case LIQUID_SLURRY -> ManureManagementConstants.MCF_LIQUID_SLURRY.getValue();
            case ANAEROBIC_LAGOON -> ManureManagementConstants.MCF_ANAEROBIC_LAGOON.getValue();
            case ANAEROBIC_DIGESTER -> ManureManagementConstants.MCF_ANAEROBIC_DIGESTER.getValue();
            case COMPOSTING_INTENSIVE -> ManureManagementConstants.MCF_COMPOSTING_INTENSIVE.getValue();
            case COMPOSTING_STATIC_PILE -> ManureManagementConstants.MCF_COMPOSTING_STATIC_PILE.getValue();
            case DEEP_BEDDING -> ManureManagementConstants.MCF_DEEP_BEDDING.getValue();
            case POULTRY_MANURE_WITH_LITTER -> ManureManagementConstants.MCF_POULTRY_WITH_LITTER.getValue();
            case POULTRY_MANURE_WITHOUT_LITTER -> ManureManagementConstants.MCF_POULTRY_WITHOUT_LITTER.getValue();
            case BURNED_FOR_FUEL -> ManureManagementConstants.MCF_BURNED.getValue();
        };
    }
    
    private double getNitrogenExcretionRate(ManureManagementLivestock species) {
        return switch (species) {
            case DAIRY_CATTLE -> ManureManagementConstants.N_EXCRETION_DAIRY_CATTLE.getValue();
            case NON_DAIRY_CATTLE -> ManureManagementConstants.N_EXCRETION_NON_DAIRY_CATTLE.getValue();
            case BUFFALO -> ManureManagementConstants.N_EXCRETION_BUFFALO.getValue();
            case SWINE -> ManureManagementConstants.N_EXCRETION_SWINE.getValue();
            case SHEEP -> ManureManagementConstants.N_EXCRETION_SHEEP.getValue();
            case GOATS -> ManureManagementConstants.N_EXCRETION_GOATS.getValue();
            case CAMELS -> ManureManagementConstants.N_EXCRETION_CAMELS.getValue();
            case HORSES -> ManureManagementConstants.N_EXCRETION_HORSES.getValue();
            case MULES_ASSES -> ManureManagementConstants.N_EXCRETION_MULES_ASSES.getValue();
            case POULTRY_CHICKEN, POULTRY_DUCKS, POULTRY_TURKEYS -> 
                ManureManagementConstants.N_EXCRETION_POULTRY.getValue();
        };
    }
    
    private double getDefaultWeight(ManureManagementLivestock species) {
        return switch (species) {
            case DAIRY_CATTLE -> ManureManagementConstants.WEIGHT_DAIRY_CATTLE.getValue();
            case NON_DAIRY_CATTLE -> ManureManagementConstants.WEIGHT_NON_DAIRY_CATTLE.getValue();
            case BUFFALO -> ManureManagementConstants.WEIGHT_BUFFALO.getValue();
            case SWINE -> ManureManagementConstants.WEIGHT_SWINE.getValue();
            case SHEEP -> ManureManagementConstants.WEIGHT_SHEEP.getValue();
            case GOATS -> ManureManagementConstants.WEIGHT_GOATS.getValue();
            case CAMELS -> ManureManagementConstants.WEIGHT_CAMELS.getValue();
            case HORSES -> ManureManagementConstants.WEIGHT_HORSES.getValue();
            case MULES_ASSES -> ManureManagementConstants.WEIGHT_MULES_ASSES.getValue();
            case POULTRY_CHICKEN, POULTRY_DUCKS, POULTRY_TURKEYS -> 
                ManureManagementConstants.WEIGHT_POULTRY.getValue();
        };
    }
    
    // ============= MINI DASHBOARDS =============
    
    @Override
    public DashboardData getAgricultureDashboardSummary(Integer startingYear, Integer endingYear) {
        // Fetch all 7 agriculture modules
        List<AquacultureEmissions> aquaculture = aquacultureEmissionsRepository.findAll();
        List<EntericFermentationEmissions> enteric = entericFermentationEmissionsRepository.findAll();
        List<LimingEmissions> liming = limingEmissionsRepository.findAll();
        List<AnimalManureAndCompostEmissions> manure = animalManureAndCompostEmissionsRepository.findAll();
        List<RiceCultivationEmissions> rice = riceCultivationEmissionsRepository.findAll();
        List<SyntheticFertilizerEmissions> fertilizer = syntheticFertilizerEmissionsRepository.findAll();
        List<UreaEmissions> urea = ureaEmissionsRepository.findAll();
        
        // Filter by year if specified
        if (startingYear != null && endingYear != null) {
            aquaculture = aquaculture.stream()
                .filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear)
                .toList();
            enteric = enteric.stream()
                .filter(e -> e.getYear() >= startingYear && e.getYear() <= endingYear)
                .toList();
            liming = liming.stream()
                .filter(l -> l.getYear() >= startingYear && l.getYear() <= endingYear)
                .toList();
            manure = manure.stream()
                .filter(m -> m.getYear() >= startingYear && m.getYear() <= endingYear)
                .toList();
            rice = rice.stream()
                .filter(r -> r.getYear() >= startingYear && r.getYear() <= endingYear)
                .toList();
            fertilizer = fertilizer.stream()
                .filter(f -> f.getYear() >= startingYear && f.getYear() <= endingYear)
                .toList();
            urea = urea.stream()
                .filter(u -> u.getYear() >= startingYear && u.getYear() <= endingYear)
                .toList();
        }
        
        return calculateAgricultureDashboardData(aquaculture, enteric, liming, manure, rice, fertilizer, urea);
    }
    
    @Override
    public List<DashboardData> getAgricultureDashboardGraph(Integer startingYear, Integer endingYear) {
        // Default to last 5 years if not specified
        if (startingYear == null || endingYear == null) {
            int currentYear = LocalDateTime.now().getYear();
            startingYear = currentYear - 4;
            endingYear = currentYear;
        }
        
        // Fetch all data
        List<AquacultureEmissions> aquaculture = aquacultureEmissionsRepository.findAll();
        List<EntericFermentationEmissions> enteric = entericFermentationEmissionsRepository.findAll();
        List<LimingEmissions> liming = limingEmissionsRepository.findAll();
        List<AnimalManureAndCompostEmissions> manure = animalManureAndCompostEmissionsRepository.findAll();
        List<RiceCultivationEmissions> rice = riceCultivationEmissionsRepository.findAll();
        List<SyntheticFertilizerEmissions> fertilizer = syntheticFertilizerEmissionsRepository.findAll();
        List<UreaEmissions> urea = ureaEmissionsRepository.findAll();
        
        // Filter by year range
        final int finalStartYear = startingYear;
        final int finalEndYear = endingYear;
        
        aquaculture = aquaculture.stream()
            .filter(a -> a.getYear() >= finalStartYear && a.getYear() <= finalEndYear)
            .toList();
        enteric = enteric.stream()
            .filter(e -> e.getYear() >= finalStartYear && e.getYear() <= finalEndYear)
            .toList();
        liming = liming.stream()
            .filter(l -> l.getYear() >= finalStartYear && l.getYear() <= finalEndYear)
            .toList();
        manure = manure.stream()
            .filter(m -> m.getYear() >= finalStartYear && m.getYear() <= finalEndYear)
            .toList();
        rice = rice.stream()
            .filter(r -> r.getYear() >= finalStartYear && r.getYear() <= finalEndYear)
            .toList();
        fertilizer = fertilizer.stream()
            .filter(f -> f.getYear() >= finalStartYear && f.getYear() <= finalEndYear)
            .toList();
        urea = urea.stream()
            .filter(u -> u.getYear() >= finalStartYear && u.getYear() <= finalEndYear)
            .toList();
        
        // Group by year
        Map<Integer, List<AquacultureEmissions>> aquacultureByYear = aquaculture.stream().collect(groupingBy(AquacultureEmissions::getYear));
        Map<Integer, List<EntericFermentationEmissions>> entericByYear = enteric.stream().collect(groupingBy(EntericFermentationEmissions::getYear));
        Map<Integer, List<LimingEmissions>> limingByYear = liming.stream().collect(groupingBy(LimingEmissions::getYear));
        Map<Integer, List<AnimalManureAndCompostEmissions>> manureByYear = manure.stream().collect(groupingBy(AnimalManureAndCompostEmissions::getYear));
        Map<Integer, List<RiceCultivationEmissions>> riceByYear = rice.stream().collect(groupingBy(RiceCultivationEmissions::getYear));
        Map<Integer, List<SyntheticFertilizerEmissions>> fertilizerByYear = fertilizer.stream().collect(groupingBy(SyntheticFertilizerEmissions::getYear));
        Map<Integer, List<UreaEmissions>> ureaByYear = urea.stream().collect(groupingBy(UreaEmissions::getYear));
        
        // Create dashboard data for each year
        List<DashboardData> dashboardDataList = new ArrayList<>();
        for (int year = startingYear; year <= endingYear; year++) {
            DashboardData data = calculateAgricultureDashboardData(
                aquacultureByYear.getOrDefault(year, List.of()),
                entericByYear.getOrDefault(year, List.of()),
                limingByYear.getOrDefault(year, List.of()),
                manureByYear.getOrDefault(year, List.of()),
                riceByYear.getOrDefault(year, List.of()),
                fertilizerByYear.getOrDefault(year, List.of()),
                ureaByYear.getOrDefault(year, List.of())
            );
            data.setStartingDate(LocalDateTime.of(year, 1, 1, 0, 0).toString());
            data.setEndingDate(LocalDateTime.of(year, 12, 31, 23, 59).toString());
            data.setYear(Year.of(year));
            dashboardDataList.add(data);
        }
        
        return dashboardDataList;
    }
    
    private DashboardData calculateAgricultureDashboardData(
            List<AquacultureEmissions> aquaculture,
            List<EntericFermentationEmissions> enteric,
            List<LimingEmissions> liming,
            List<AnimalManureAndCompostEmissions> manure,
            List<RiceCultivationEmissions> rice,
            List<SyntheticFertilizerEmissions> fertilizer,
            List<UreaEmissions> urea) {
        
        DashboardData data = new DashboardData();
        
        // Aquaculture: N2O only
        for (AquacultureEmissions a : aquaculture) {
            data.setTotalN2OEmissions(data.getTotalN2OEmissions() + a.getN2OEmissions());
        }
        
        // Enteric: CH4 only
        for (EntericFermentationEmissions e : enteric) {
            data.setTotalCH4Emissions(data.getTotalCH4Emissions() + e.getCH4Emissions());
        }
        
        // Liming: BioCO2 only
        for (LimingEmissions l : liming) {
            data.setTotalBioCO2Emissions(data.getTotalBioCO2Emissions() + l.getCO2Emissions());
        }
        
        // Manure: CH4 + N2O
        for (AnimalManureAndCompostEmissions m : manure) {
            data.setTotalCH4Emissions(data.getTotalCH4Emissions() + m.getCH4Emissions());
            data.setTotalN2OEmissions(data.getTotalN2OEmissions() + m.getN2OEmissions());
        }
        
        // Rice: CH4 only
        for (RiceCultivationEmissions r : rice) {
            data.setTotalCH4Emissions(data.getTotalCH4Emissions() + r.getAnnualCH4Emissions());
        }
        
        // Fertilizer: N2O only
        for (SyntheticFertilizerEmissions f : fertilizer) {
            data.setTotalN2OEmissions(data.getTotalN2OEmissions() + f.getN2OEmissions());
        }
        
        // Urea: BioCO2 only
        for (UreaEmissions u : urea) {
            data.setTotalBioCO2Emissions(data.getTotalBioCO2Emissions() + u.getCO2Emissions());
        }
        
        // Calculate CO2eq
        data.setTotalCO2EqEmissions(
            data.getTotalFossilCO2Emissions() + 
            data.getTotalBioCO2Emissions() + 
            data.getTotalCH4Emissions() * GWP.CH4.getValue() + 
            data.getTotalN2OEmissions() * GWP.N2O.getValue()
        );
        
        return data;
    }
}
