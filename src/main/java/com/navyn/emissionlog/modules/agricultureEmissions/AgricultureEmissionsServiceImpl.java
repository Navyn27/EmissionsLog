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
import com.navyn.emissionlog.Enums.Agriculture.ManureManagementEmissionFactors;

import com.navyn.emissionlog.utils.DashboardData;
import com.navyn.emissionlog.utils.ExcelReader;
import com.navyn.emissionlog.utils.Specifications.AgricultureSpecifications;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import java.util.Arrays;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        public List<EntericFermentationEmissions> getAllEntericFermentationEmissions(Integer year,
                        LivestockSpecies species) {
                Specification<EntericFermentationEmissions> spec = Specification
                                .<EntericFermentationEmissions>where(hasYear(year))
                                .and(hasSpecies(species));
                return entericFermentationEmissionsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
        }

        @Override
        public List<LimingEmissions> getAllLimingEmissions(Integer year, LimingMaterials limingMaterials) {
                Specification<LimingEmissions> spec = Specification.where(hasLimingMaterial(limingMaterials))
                                .and(hasYear(year));
                return limingEmissionsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
        }

        @Override
        public List<AnimalManureAndCompostEmissions> getAllAnimalManureAndCompostEmissions(Integer year,
                        OrganicAmendmentTypes amendmentType, LivestockSpecies species) {
                Specification<AnimalManureAndCompostEmissions> spec = Specification
                                .where(hasAmendmentType(amendmentType))
                                .and(hasSpecies(species))
                                .and(hasYear(year));
                return animalManureAndCompostEmissionsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
        }

        @Override
        public List<RiceCultivationEmissions> getAllRiceCultivationEmissions(String riceEcosystem,
                        WaterRegime waterRegime,
                        Integer year) {
                Specification<RiceCultivationEmissions> spec = Specification.where(hasRiceEcosystem(riceEcosystem))
                                .and(hasWaterRegime(waterRegime))
                                .and(hasYear(year));
                return riceCultivationEmissionsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
        }

        @Override
        public List<SyntheticFertilizerEmissions> getAllSyntheticFertilizerEmissions(Integer year, CropTypes cropType,
                        Fertilizers fertilizerType) {
                Specification<SyntheticFertilizerEmissions> spec = Specification
                                .where(hasFertilizerType(fertilizerType))
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
                emissions.setN2ONEmissions(emissionsDto.getFishProduction() * AFOLUConstants.FISH_N20_EF.getValue());
                emissions.setN2OEmissions(
                                emissionsDto.getFishProduction() * AFOLUConstants.FISH_N20_EF.getValue() * 44 / 28
                                                * 1000000);
                emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());
                return aquacultureEmissionsRepository.save(emissions);
        }

        @Override
        public EntericFermentationEmissions createEntericFermentationEmissions(
                        EntericFermentationEmissionsDto emissionsDto) {
                EntericFermentationEmissions emissions = new EntericFermentationEmissions();
                emissions.setYear(emissionsDto.getYear());
                emissions.setAnimalPopulation(emissionsDto.getAnimalPopulation());
                emissions.setSpecies(emissionsDto.getSpecies());
                emissions.setCH4Emissions(
                                emissions.getAnimalPopulation() * emissions.getSpecies().getEntericFermentationCH4EF()
                                                / 1000);
                emissions.setCO2EqEmissions(emissions.getCH4Emissions() * GWP.CH4.getValue());
                return entericFermentationEmissionsRepository.save(emissions);
        }

        @Override
        public LimingEmissions createLimingEmissions(LimingEmissionsDto emissionsDto) {
                LimingEmissions emissions = new LimingEmissions();
                emissions.setYear(emissionsDto.getYear());
                emissions.setMaterial(emissionsDto.getMaterial());
                emissions.setCaCO3Qty(emissionsDto.getCaCO3Qty());
                emissions.setCO2Emissions(
                                emissions.getMaterial().getLimingConstant()
                                                * AFOLUConstants.CONVERSION_FACTOR.getValue());
                return limingEmissionsRepository.save(emissions);
        }

        @Override
        public AnimalManureAndCompostEmissions createAnimalManureAndCompostEmissions(
                        AnimalManureAndCompostEmissionsDto emissionsDto) {
                AnimalManureAndCompostEmissions emissions = new AnimalManureAndCompostEmissions();
                emissions.setYear(emissionsDto.getYear());
                emissions.setLivestockSpecies(emissionsDto.getSpecies());
                emissions.setAmendmentType(getAmendmentTypeByLivestockSpecies(emissionsDto.getSpecies()));
                emissions.setPopulation(emissionsDto.getPopulation());
                emissions.setTotalN(emissions.getLivestockSpecies().getManureNitrogenEF() * emissions.getPopulation());
                emissions.setNAvailable(
                                emissions.getTotalN() * emissions.getLivestockSpecies().getMeanLossesOfNinManureMMS());
                emissions.setN2ONEmissions(
                                emissions.getNAvailable() * emissions.getLivestockSpecies()
                                                .getEFFOrgManureCompostAppliedInFields());
                emissions.setN2OEmissions(emissions.getN2ONEmissions() * 44 / 28);
                emissions.setCH4Emissions(
                                emissions.getPopulation() * emissions.getLivestockSpecies().getManureManagementCH4EF());
                emissions.setCO2EqEmissions(
                                emissions.getN2OEmissions() * 265 / 1000000
                                                + emissions.getCH4Emissions() * GWP.CH4.getValue());
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

                // Efi=Efc*SFw*SFp*SfoA*SFs,r
                emissions.setAdjDailyEFEmissions(
                                AFOLUConstants.EFC.getValue() * emissionsDto.getWaterRegime().getValue()
                                                * AFOLUConstants.SFP.getValue() * AFOLUConstants.SFOA.getValue()
                                                * AFOLUConstants.SFSR.getValue());
                emissions.setAnnualCH4Emissions(emissionsDto.getHarvestedArea() * emissionsDto.getCultivationPeriod()
                                * emissions.getAdjDailyEFEmissions() / 1000000);
                emissions.setCO2EqEmissions(emissions.getAnnualCH4Emissions() * GWP.CH4.getValue());
                return riceCultivationEmissionsRepository.save(emissions);
        }

        @Override
        public SyntheticFertilizerEmissions createSyntheticFertilizerEmissions(
                        SyntheticFertilizerEmissionsDto emissionsDto) {
                SyntheticFertilizerEmissions emissions = new SyntheticFertilizerEmissions();
                emissions.setYear(emissionsDto.getYear());
                emissions.setCropType(emissionsDto.getCropType());
                emissions.setFertType(emissionsDto.getFertType());
                emissions.setQtyApplied(emissionsDto.getQtyApplied());
                emissions.setNAmount(emissions.getQtyApplied() * emissions.getFertType().getNContent());
                emissions.setN2ONEmissions(emissions.getNAmount() * emissions.getFertType().getNContent());
                emissions.setN2OEmissions(emissions.getN2ONEmissions() * 44 / 28);
                emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());
                return syntheticFertilizerEmissionsRepository.save(emissions);
        }

        @Override
        public UreaEmissions createUreaEmissions(UreaEmissionsDto emissionsDto) {
                UreaEmissions emissions = new UreaEmissions();
                emissions.setYear(emissionsDto.getYear());
                emissions.setFertilizerName(emissionsDto.getFertilizerName());
                emissions.setQty(emissionsDto.getQty());
                emissions.setCO2Emissions(emissions.getQty() * AFOLUConstants.UREA_EMISSION_FACTOR.getValue()
                                * AFOLUConstants.CONVERSION_FACTOR.getValue());
                return ureaEmissionsRepository.save(emissions);
        }

        @Override
        public BurningEmissions createBurningEmissions(BurningEmissionsDto burningEmissionsDto) {
                BurningEmissions emissions = new BurningEmissions();
                emissions.setYear(burningEmissionsDto.getYear());
                emissions.setBurningAgentType(burningEmissionsDto.getBurningAgentType());
                emissions.setBurntArea(burningEmissionsDto.getBurntArea());
                emissions.setFuelMassAvailable(
                                burningEmissionsDto.getFuelMassUnit()
                                                .toKilograms(burningEmissionsDto.getFuelMassAvailable()));
                emissions.setFireType(burningEmissionsDto.getFireType());

                Double combustionFactor = burningEmissionsDto.getIsEucalyptusForest()
                                ? AFOLUConstants.EUCALYPTUS_FOREST_CF.getValue()
                                : OTHER_FOREST_CF.getValue();
                emissions.setFuelMassConsumed(emissions.getFuelMassAvailable() * combustionFactor);

                // Emissions Calculations
                emissions.setCO2Emissions(
                                emissions.getFuelMassAvailable() * emissions.getBurningAgentType().getCO2EF());
                emissions.setCH4Emissions(
                                emissions.getFuelMassAvailable() * emissions.getBurningAgentType().getCH4EF());
                emissions.setN2OEmissions(
                                emissions.getFuelMassAvailable() * emissions.getBurningAgentType().getN2OEF());

                emissions.setCO2EqEmissions(
                                emissions.getCO2Emissions() + (emissions.getCH4Emissions() * GWP.CH4.getValue())
                                                + (emissions.getN2OEmissions() * GWP.N2O.getValue()));
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
                emissions.setHarvestedDMYield(emissions.getTotalAreaHarvested() * emissions.getHarvestedFreshCropYield()
                                * cropResidueEmissionsDto.getCropType().getDMFraction());
                emissions.setAGResiduesDryMatter(cropResidueEmissionsDto.getAGResiduesDryMatter());
                emissions.setRatioOfAGResiduesDMToHarvestedYield(
                                emissions.getAGResiduesDryMatter() * 1000 / emissions.getHarvestedDMYield());
                emissions.setRatioOfBelowGroundResiduesToHarvestedYield(
                                cropResidueEmissionsDto.getCropType().getRatioOfBGRToAGBiomass()
                                                * (emissions.getAGResiduesDryMatter() * 1000
                                                                + emissions.getHarvestedDMYield())
                                                / emissions.getHarvestedDMYield());
                emissions.setNInCropResiduesReturned(cropResidueEmissionsDto.getNInCropResiduesReturned());
                emissions.setN2ONEmissions(
                                emissions.getNInCropResiduesReturned() * AFOLUConstants.N_CROP_RESIDUES_EF.getValue());
                emissions.setN2OEmissions(emissions.getN2ONEmissions() * 44 / 28);
                emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());
                return cropResiduesEmissionsRepository.save(emissions);
        }

        @Override
        public List<CropResiduesEmissions> getAllCropResidueEmissions(Integer year, CropResiduesCropType cropType,
                        LandUseCategory landUseCategory) {
                Specification<CropResiduesEmissions> spec = Specification.where(hasCropResiduesCropType(cropType))
                                .and(hasLandUseCategory(landUseCategory))
                                .and(hasYear(year));
                return cropResiduesEmissionsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
        }

        @Override
        public PastureExcretionEmissions createPastureExcretionEmissions(
                        PastureExcretionsEmissionsDto pastureExcretionEmissionsDto) {
                PastureExcretionEmissions emissions = new PastureExcretionEmissions();
                emissions.setYear(pastureExcretionEmissionsDto.getYear());
                emissions.setLivestockSpecies(pastureExcretionEmissionsDto.getLivestockSpecies());
                emissions.setMMS(pastureExcretionEmissionsDto.getMms());
                emissions.setAnimalPopulation(pastureExcretionEmissionsDto.getAnimalPopulation());
                emissions.setTotalNExcretionDeposited(
                                emissions.getAnimalPopulation() * emissions.getLivestockSpecies().getAnnualNExcretion()
                                                * emissions.getLivestockSpecies()
                                                                .getFractionOfManureDepositedOnPasture());
                emissions.setN20NEmissions(emissions.getTotalNExcretionDeposited()
                                * emissions.getLivestockSpecies().getNEFManureDepositedOnPasture());
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
                emissions.setNMineralisedInMineralSoil(emissions.getAvLossOfSoilC() * 1
                                / emissions.getLandUseInReportingYear().getCNRatioOfSoilOrganicMatter() * 1000);
                emissions.setN20NEmissions(
                                emissions.getNMineralisedInMineralSoil()
                                                * emissions.getLandUseInReportingYear().getEFNMineralised());
                emissions.setN2OEmissions(emissions.getN20NEmissions() * 44 / 28);
                emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());
                return mineralSoilEmissionsRepository.save(emissions);
        }

        @Override
        public List<MineralSoilEmissions> getAllMineralSoilEmissions(Integer year, LandUseCategory initialLandUse,
                        LandUseCategory landUseInReportingYear) {
                Specification<MineralSoilEmissions> specification = Specification
                                .where(hasInitialLandUse(initialLandUse))
                                .and(hasLandUseInReportingYear(landUseInReportingYear))
                                .and(hasYear(year));
                return mineralSoilEmissionsRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "year"));
        }

        @Override
        public List<PastureExcretionEmissions> getAllPastureExcretionEmissions(Integer year, LivestockSpecies species,
                        MMS mms) {
                Specification<PastureExcretionEmissions> spec = Specification
                                .where(AgricultureSpecifications.<PastureExcretionEmissions>hasMMS(mms))
                                .and(hasYear(year))
                                .and(hasLivestockCategory(species));
                return pastureExcretionEmissionsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
        }

        @Override
        public VolatilizationEmissions createVolatilizationEmissions(
                        VolatilizationEmissionsDto volatilizationEmissionsDto) {
                VolatilizationEmissions emissions = new VolatilizationEmissions();
                emissions.setYear(volatilizationEmissionsDto.getYear());
                emissions.setMMS(volatilizationEmissionsDto.getMms());
                emissions.setLivestockSpecies(volatilizationEmissionsDto.getLivestockSpecies());
                emissions.setAnimalPopulation(volatilizationEmissionsDto.getAnimalPopulation());
                emissions.setTotalNExcretionForMMS(
                                emissions.getAnimalPopulation() * emissions.getLivestockSpecies().getExcretionRate());
                emissions.setManureVolatilizationNLoss(emissions.getTotalNExcretionForMMS()
                                * emissions.getMMS().getFractionOfManureNThatVolatilizes());
                emissions.setIndirectVolatilizationN2OEmissionsFromVolatilization(
                                emissions.getManureVolatilizationNLoss()
                                                * AFOLUConstants.EF_N2O_AtmoNDeposition.getValue() * 44 / 28);
                emissions.setCO2EqEmissions(emissions.getIndirectVolatilizationN2OEmissionsFromVolatilization()
                                * GWP.N2O.getValue());
                return volatilizationEmissionsRepository.save(emissions);
        }

        @Override
        public LeachingEmissions createLeachingEmissions(LeachingEmissionsDto leachingEmissionsDto) {
                LeachingEmissions emissions = new LeachingEmissions();
                emissions.setYear(leachingEmissionsDto.getYear());
                emissions.setMMS(leachingEmissionsDto.getMms());
                emissions.setLivestockSpecies(leachingEmissionsDto.getLivestockSpecies());
                emissions.setNumberOfAnimals(leachingEmissionsDto.getNumberOfAnimals());

                // Step 1: Calculate Total N excretion for MMS = Number of animals × N excretion
                // rate
                double nExcretionRate = leachingEmissionsDto.getLivestockSpecies().getExcretionRate();
                emissions.setTotalNExcretionForMMS(leachingEmissionsDto.getNumberOfAnimals() * nExcretionRate);

                // Step 2: Calculate N loss due to leaching = Total N excretion × Frac(leachMS)
                emissions.setManureNLossDueToLeachingAndRunoff(emissions.getTotalNExcretionForMMS()
                                * emissions.getMMS().getFractionOfManureNThatVolatilizes());

                // Step 3: Calculate N2O-N from leaching = N loss due to leaching × EF_leaching
                // (0.0075)
                emissions.setN2ONFromLeaching(emissions.getManureNLossDueToLeachingAndRunoff()
                                * ManureManagementConstants.N2O_LEACHING_EF.getValue());

                // Step 4: Calculate N2O emissions = N2O-N × (44/28)
                emissions.setIndirectN2OEmissionsFromLeaching(emissions.getN2ONFromLeaching()
                                * ManureManagementConstants.N2O_CONVERSION_FACTOR.getValue());

                // Step 5: Calculate CO2Eq emissions = N2O × GWP_N2O (298)
                emissions.setCO2EqEmissions(emissions.getIndirectN2OEmissionsFromLeaching() * GWP.N2O.getValue());

                return leachingEmissionsRepository.save(emissions);
        }

        @Override
        public AtmosphericDepositionEmissions createAtmosphericNDepositionEmissions(
                        AtmosphericDepositionEmissionsDto atmosphericNDepositionEmissionsDto) {
                AtmosphericDepositionEmissions emissions = new AtmosphericDepositionEmissions();
                emissions.setYear(atmosphericNDepositionEmissionsDto.getYear());
                emissions.setLandUseCategory(atmosphericNDepositionEmissionsDto.getLandUseCategory());
                emissions.setSyntheticNVolatilized(atmosphericNDepositionEmissionsDto.getSyntheticNThatVolatilizes());
                emissions.setOrganicNAdditions(atmosphericNDepositionEmissionsDto.getOrganicNSoilAdditions());
                emissions.setExcretionsDepositedByGrazingAnimals(
                                atmosphericNDepositionEmissionsDto.getExcretionsDepositedByGrazingAnimals());
                emissions.setAnnualN2ONFromAtmosphericDeposition((emissions.getSyntheticNVolatilized()
                                + (emissions.getOrganicNAdditions()
                                                + emissions.getExcretionsDepositedByGrazingAnimals())
                                                * AFOLUConstants.FRACTION_OF_APPLIED_ORGANIC_N_EXCRETIONS_THAT_VOLATILIZES
                                                                .getValue())
                                * AFOLUConstants.EF_N2O_AtmoNDeposition.getValue());
                emissions.setN2OEmissions(emissions.getAnnualN2ONFromAtmosphericDeposition() * 44 / 28);
                emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());
                return atmosphericDepositionEmissionsRepository.save(emissions);
        }

        @Override
        public LeachingAndRunoffEmissions createLeachingAndRunoffEmissions(
                        LeachingAndRunoffEmissionsDto leachingAndRunoffEmissionsDto) {
                LeachingAndRunoffEmissions emissions = new LeachingAndRunoffEmissions();
                LeachingAndRunoffEmissions runoffEmissions = new LeachingAndRunoffEmissions();
                emissions.setYear(leachingAndRunoffEmissionsDto.getYear());
                emissions.setLandUseCategory(leachingAndRunoffEmissionsDto.getLandUseCategory());
                emissions.setSyntheticNAppliedToSoil(leachingAndRunoffEmissionsDto.getSyntheticNApplied());
                emissions.setOrganicAdditionsAppliedToSoil(leachingAndRunoffEmissionsDto.getOrganicSoilAdditions());
                emissions.setExcretionsDepositedByGrazingAnimals(
                                leachingAndRunoffEmissionsDto.getExcretionsDepositedByGrazingAnimals());
                emissions.setNInCropResidues(leachingAndRunoffEmissionsDto.getNInCropResidues());
                emissions.setNMineralizedInMineralSoils(leachingAndRunoffEmissionsDto.getNMineralizedInMineralSoils());
                emissions.setN2ONProducedFromLeachingAndRunoff((emissions.getSyntheticNAppliedToSoil()
                                + emissions.getOrganicAdditionsAppliedToSoil()
                                + emissions.getExcretionsDepositedByGrazingAnimals()
                                + emissions.getNInCropResidues()
                                + emissions.getNMineralizedInMineralSoils())
                                * emissions.getLandUseCategory().getNFractionAddedToSoilPostLeaching()
                                * emissions.getLandUseCategory().getEF_N2O_LeachAndRunoffNSoilAdditive());
                emissions.setN2OEmissions(emissions.getN2ONProducedFromLeachingAndRunoff() * 44 / 28);
                emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());
                return leachingAndRunoffEmissionsRepository.save(emissions);
        }

        @Override
        public List<AtmosphericDepositionEmissions> getAllAtmosphericNDepositionEmissions(Integer year,
                        LandUseCategory landUseCategory) {
                Specification<AtmosphericDepositionEmissions> spec = Specification
                                .where(AgricultureSpecifications.<AtmosphericDepositionEmissions>hasYear(year))
                                .and(hasLandUseCategory(landUseCategory));
                return atmosphericDepositionEmissionsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
        }

        @Override
        public List<LeachingAndRunoffEmissions> getAllLeachingAndRunoffEmissions(Integer year,
                        LandUseCategory landUseCategory) {
                Specification<LeachingAndRunoffEmissions> spec = Specification
                                .where(AgricultureSpecifications
                                                .<LeachingAndRunoffEmissions>hasLandUseCategory(landUseCategory))
                                .and(hasYear(year));
                return leachingAndRunoffEmissionsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
        }

        @Override
        public List<LeachingEmissions> getAllLeachingEmissions(Integer year, MMS mms, LivestockSpecies species) {
                Specification<LeachingEmissions> spec = Specification
                                .where(AgricultureSpecifications.<LeachingEmissions>hasYear(year))
                                .and(hasMMS(mms))
                                .and(hasLivestockCategory(species));
                return leachingEmissionsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
        }

        @Override
        public List<VolatilizationEmissions> getAllVolatilizationEmissions(Integer year, MMS mms,
                        LivestockSpecies species) {
                Specification<VolatilizationEmissions> spec = Specification
                                .where(AgricultureSpecifications.<VolatilizationEmissions>hasYear(year))
                                .and(hasMMS(mms))
                                .and(hasLivestockCategory(species));
                return volatilizationEmissionsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
        }

        private OrganicAmendmentTypes getAmendmentTypeByLivestockSpecies(LivestockSpecies species) {
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

                // Set input fields
                emission.setYear(dto.getYear());
                emission.setSpecies(dto.getSpecies());
                emission.setAnimalPopulation(dto.getAnimalPopulation());

                // Calculate emissions using Excel methodology
                calculateEmissions(emission);

                return manureManagementEmissionsRepository.save(emission);
        }

        @Override
        public byte[] generateManureManagementExcelTemplate() {
                try (Workbook workbook = new XSSFWorkbook();
                                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                        Sheet sheet = workbook.createSheet("Manure Management Emissions");

                        // Create title style
                        XSSFCellStyle titleStyle = (XSSFCellStyle) workbook.createCellStyle();
                        Font titleFont = workbook.createFont();
                        titleFont.setBold(true);
                        titleFont.setFontHeightInPoints((short) 18);
                        titleFont.setColor(IndexedColors.WHITE.getIndex());
                        titleFont.setFontName("Calibri");
                        titleStyle.setFont(titleFont);
                        titleStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
                        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        titleStyle.setAlignment(HorizontalAlignment.CENTER);
                        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                        titleStyle.setBorderTop(BorderStyle.MEDIUM);
                        titleStyle.setBorderBottom(BorderStyle.MEDIUM);
                        titleStyle.setBorderLeft(BorderStyle.MEDIUM);
                        titleStyle.setBorderRight(BorderStyle.MEDIUM);

                        // Create header style
                        XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
                        Font headerFont = workbook.createFont();
                        headerFont.setBold(true);
                        headerFont.setFontHeightInPoints((short) 11);
                        headerFont.setColor(IndexedColors.WHITE.getIndex());
                        headerFont.setFontName("Calibri");
                        headerStyle.setFont(headerFont);
                        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
                        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        headerStyle.setAlignment(HorizontalAlignment.CENTER);
                        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                        headerStyle.setBorderTop(BorderStyle.THIN);
                        headerStyle.setBorderBottom(BorderStyle.THIN);
                        headerStyle.setBorderLeft(BorderStyle.THIN);
                        headerStyle.setBorderRight(BorderStyle.THIN);
                        headerStyle.setWrapText(true);

                        // Create data style
                        XSSFCellStyle dataStyle = (XSSFCellStyle) workbook.createCellStyle();
                        Font dataFont = workbook.createFont();
                        dataFont.setFontName("Calibri");
                        dataFont.setFontHeightInPoints((short) 10);
                        dataStyle.setFont(dataFont);
                        dataStyle.setBorderBottom(BorderStyle.THIN);
                        dataStyle.setBorderTop(BorderStyle.THIN);
                        dataStyle.setBorderLeft(BorderStyle.THIN);
                        dataStyle.setBorderRight(BorderStyle.THIN);
                        dataStyle.setAlignment(HorizontalAlignment.LEFT);
                        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                        dataStyle.setWrapText(true);

                        // Create alternate data style
                        XSSFCellStyle alternateDataStyle = (XSSFCellStyle) workbook.createCellStyle();
                        Font altDataFont = workbook.createFont();
                        altDataFont.setFontName("Calibri");
                        altDataFont.setFontHeightInPoints((short) 10);
                        alternateDataStyle.setFont(altDataFont);
                        alternateDataStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                        alternateDataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        alternateDataStyle.setBorderBottom(BorderStyle.THIN);
                        alternateDataStyle.setBorderTop(BorderStyle.THIN);
                        alternateDataStyle.setBorderLeft(BorderStyle.THIN);
                        alternateDataStyle.setBorderRight(BorderStyle.THIN);
                        alternateDataStyle.setAlignment(HorizontalAlignment.LEFT);
                        alternateDataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                        alternateDataStyle.setWrapText(true);

                        // Create number style
                        XSSFCellStyle numberStyle = (XSSFCellStyle) workbook.createCellStyle();
                        Font numFont = workbook.createFont();
                        numFont.setFontName("Calibri");
                        numFont.setFontHeightInPoints((short) 10);
                        numberStyle.setFont(numFont);
                        DataFormat dataFormat = workbook.createDataFormat();
                        numberStyle.setDataFormat(dataFormat.getFormat("#,##0.00"));
                        numberStyle.setBorderBottom(BorderStyle.THIN);
                        numberStyle.setBorderTop(BorderStyle.THIN);
                        numberStyle.setBorderLeft(BorderStyle.THIN);
                        numberStyle.setBorderRight(BorderStyle.THIN);
                        numberStyle.setAlignment(HorizontalAlignment.RIGHT);
                        numberStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                        // Create year style (centered number)
                        XSSFCellStyle yearStyle = (XSSFCellStyle) workbook.createCellStyle();
                        yearStyle.cloneStyleFrom(dataStyle);
                        yearStyle.setAlignment(HorizontalAlignment.CENTER);

                        int rowIdx = 0;

                        // Title row
                        Row titleRow = sheet.createRow(rowIdx++);
                        titleRow.setHeightInPoints(30);
                        Cell titleCell = titleRow.createCell(0);
                        titleCell.setCellValue("Manure Management Emissions Template");
                        titleCell.setCellStyle(titleStyle);
                        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

                        rowIdx++; // Blank row

                        // Create header row
                        Row headerRow = sheet.createRow(rowIdx++);
                        headerRow.setHeightInPoints(22);
                        String[] headers = {
                                        "Year",
                                        "Species",
                                        "Animal Population"
                        };

                        for (int i = 0; i < headers.length; i++) {
                                Cell cell = headerRow.createCell(i);
                                cell.setCellValue(headers[i]);
                                cell.setCellStyle(headerStyle);
                        }

                        // Get all ManureManagementLivestock enum values for dropdown
                        String[] speciesNames = Arrays.stream(ManureManagementLivestock.values())
                                        .map(Enum::name)
                                        .toArray(String[]::new);

                        // Create data validation helper
                        DataValidationHelper validationHelper = sheet.getDataValidationHelper();

                        // Data validation for Species column (Column B, index 1)
                        if (speciesNames.length > 0) {
                                CellRangeAddressList speciesList = new CellRangeAddressList(3, 1000, 1, 1);
                                DataValidationConstraint speciesConstraint = validationHelper
                                                .createExplicitListConstraint(speciesNames);
                                DataValidation speciesValidation = validationHelper.createValidation(speciesConstraint,
                                                speciesList);
                                speciesValidation.setShowErrorBox(true);
                                speciesValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
                                speciesValidation.createErrorBox("Invalid Species",
                                                "Please select a valid species from the dropdown list.");
                                speciesValidation.setShowPromptBox(true);
                                speciesValidation.createPromptBox("Species",
                                                "Select a species from the dropdown list.");
                                sheet.addValidationData(speciesValidation);
                        }

                        // Create example data rows
                        Object[] exampleData1 = {
                                        2024,
                                        "DAIRY_COWS_LACTATING",
                                        1500.0
                        };

                        Object[] exampleData2 = {
                                        2025,
                                        "SHEEP",
                                        2500.0
                        };

                        // First example row
                        Row exampleRow1 = sheet.createRow(rowIdx++);
                        exampleRow1.setHeightInPoints(18);
                        for (int i = 0; i < exampleData1.length; i++) {
                                Cell cell = exampleRow1.createCell(i);
                                if (i == 0) { // Year
                                        cell.setCellStyle(yearStyle);
                                        cell.setCellValue(((Number) exampleData1[i]).intValue());
                                } else if (i == 2) { // Animal Population (number)
                                        cell.setCellStyle(numberStyle);
                                        cell.setCellValue(((Number) exampleData1[i]).doubleValue());
                                } else { // Species (text)
                                        cell.setCellStyle(dataStyle);
                                        cell.setCellValue((String) exampleData1[i]);
                                }
                        }

                        // Second example row with alternate style
                        Row exampleRow2 = sheet.createRow(rowIdx++);
                        exampleRow2.setHeightInPoints(18);
                        for (int i = 0; i < exampleData2.length; i++) {
                                Cell cell = exampleRow2.createCell(i);
                                if (i == 0) { // Year
                                        CellStyle altYearStyle = workbook.createCellStyle();
                                        altYearStyle.cloneStyleFrom(alternateDataStyle);
                                        altYearStyle.setAlignment(HorizontalAlignment.CENTER);
                                        cell.setCellStyle(altYearStyle);
                                        cell.setCellValue(((Number) exampleData2[i]).intValue());
                                } else if (i == 2) { // Animal Population (number)
                                        CellStyle altNumStyle = workbook.createCellStyle();
                                        altNumStyle.cloneStyleFrom(numberStyle);
                                        altNumStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                                        altNumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                                        cell.setCellStyle(altNumStyle);
                                        cell.setCellValue(((Number) exampleData2[i]).doubleValue());
                                } else { // Species (text)
                                        cell.setCellStyle(alternateDataStyle);
                                        cell.setCellValue((String) exampleData2[i]);
                                }
                        }

                        // Auto-size columns with limits
                        for (int i = 0; i < headers.length; i++) {
                                sheet.autoSizeColumn(i);
                                int currentWidth = sheet.getColumnWidth(i);
                                int minWidth = 3000;
                                int maxWidth = 20000;
                                if (currentWidth < minWidth) {
                                        sheet.setColumnWidth(i, minWidth);
                                } else if (currentWidth > maxWidth) {
                                        sheet.setColumnWidth(i, maxWidth);
                                }
                        }

                        workbook.write(out);
                        return out.toByteArray();
                } catch (IOException e) {
                        throw new RuntimeException("Error generating Excel template", e);
                }
        }

        @Override
        @Transactional
        public Map<String, Object> createManureManagementEmissionsFromExcel(MultipartFile file) {
                List<ManureManagementEmissions> savedRecords = new ArrayList<>();
                List<Map<String, Object>> skippedRows = new ArrayList<>();
                Set<String> processedYearSpecies = new HashSet<>(); // Track duplicates in file
                int totalProcessed = 0;

                try {
                        List<ManureManagementEmissionsDto> dtos = ExcelReader.readExcel(
                                        file.getInputStream(),
                                        ManureManagementEmissionsDto.class,
                                        ExcelType.MANURE_MANAGEMENT_EMISSIONS);

                        for (int i = 0; i < dtos.size(); i++) {
                                ManureManagementEmissionsDto dto = dtos.get(i);
                                totalProcessed++;
                                int rowNumber = i + 1; // Excel row number (1-based, accounting for header row)
                                int excelRowNumber = rowNumber + 2; // +2 for title row and blank row

                                // Validate required fields
                                List<String> missingFields = new ArrayList<>();
                                if (dto.getYear() == null || dto.getYear() == 0) {
                                        missingFields.add("Year");
                                }
                                if (dto.getSpecies() == null) {
                                        missingFields.add("Species");
                                }
                                if (dto.getAnimalPopulation() == null || dto.getAnimalPopulation() <= 0) {
                                        missingFields.add("Animal Population");
                                }

                                if (!missingFields.isEmpty()) {
                                        Map<String, Object> skipInfo = new HashMap<>();
                                        skipInfo.put("row", excelRowNumber);
                                        skipInfo.put("year", dto.getYear() != null && dto.getYear() > 0 ? dto.getYear() : "N/A");
                                        skipInfo.put("species", dto.getSpecies() != null ? dto.getSpecies().name() : "N/A");
                                        skipInfo.put("reason", "Missing required fields: " + String.join(", ", missingFields));
                                        skippedRows.add(skipInfo);
                                        continue; // Skip this row
                                }

                                // Validate year (must be >= 1900)
                                if (dto.getYear() < 1900 || dto.getYear() > 2100) {
                                        Map<String, Object> skipInfo = new HashMap<>();
                                        skipInfo.put("row", excelRowNumber);
                                        skipInfo.put("year", dto.getYear());
                                        skipInfo.put("species", dto.getSpecies().name());
                                        skipInfo.put("reason", "Year must be between 1900 and 2100");
                                        skippedRows.add(skipInfo);
                                        continue; // Skip this row
                                }

                                // Check for duplicate in same file
                                String yearSpeciesKey = dto.getYear() + "_" + dto.getSpecies().name();
                                if (processedYearSpecies.contains(yearSpeciesKey)) {
                                        Map<String, Object> skipInfo = new HashMap<>();
                                        skipInfo.put("row", excelRowNumber);
                                        skipInfo.put("year", dto.getYear());
                                        skipInfo.put("species", dto.getSpecies().name());
                                        skipInfo.put("reason", "Duplicate year and species combination in the same file");
                                        skippedRows.add(skipInfo);
                                        continue; // Skip this row
                                }
                                processedYearSpecies.add(yearSpeciesKey);

                                // Check if record with same year AND species already exists
                                if (manureManagementEmissionsRepository.findByYearAndSpecies(dto.getYear(), dto.getSpecies()).isPresent()) {
                                        Map<String, Object> skipInfo = new HashMap<>();
                                        skipInfo.put("row", excelRowNumber);
                                        skipInfo.put("year", dto.getYear());
                                        skipInfo.put("species", dto.getSpecies().name());
                                        skipInfo.put("reason", "Record with this year and species combination already exists");
                                        skippedRows.add(skipInfo);
                                        continue; // Skip this row
                                }

                                // Create the record using existing create method
                                try {
                                        ManureManagementEmissions saved = createManureManagementEmissions(dto);
                                        savedRecords.add(saved);
                                } catch (RuntimeException e) {
                                        String errorMessage = e.getMessage();
                                        Map<String, Object> skipInfo = new HashMap<>();
                                        skipInfo.put("row", excelRowNumber);
                                        skipInfo.put("year", dto.getYear());
                                        skipInfo.put("species", dto.getSpecies().name());
                                        skipInfo.put("reason", errorMessage != null ? errorMessage : "Error creating record");
                                        skippedRows.add(skipInfo);
                                        continue; // Skip this row
                                }
                        }

                        // Calculate total skipped count
                        int totalSkipped = skippedRows.size();

                        Map<String, Object> result = new HashMap<>();
                        result.put("saved", savedRecords);
                        result.put("savedCount", savedRecords.size());
                        result.put("skippedCount", totalSkipped);
                        result.put("skippedRows", skippedRows);
                        result.put("totalProcessed", totalProcessed);

                        return result;
                } catch (IOException e) {
                        // Re-throw IOException with user-friendly message
                        String message = e.getMessage();
                        if (message != null) {
                                throw new RuntimeException(message, e);
                        } else {
                                throw new RuntimeException(
                                                "Incorrect template. Please download the correct template and try again.",
                                                e);
                        }
                } catch (NullPointerException e) {
                        // Handle null pointer exceptions with clear message
                        throw new RuntimeException(
                                        "Missing required fields. Please fill in all required fields in your Excel file.", e);
                } catch (Exception e) {
                        String errorMsg = e.getMessage();
                        if (errorMsg != null) {
                                throw new RuntimeException(errorMsg, e);
                        }
                        throw new RuntimeException("Error processing Excel file. Please check your file and try again.", e);
                }
        }

        @Override
        public List<ManureManagementEmissions> getAllManureManagementEmissions(
                        Integer year, ManureManagementLivestock species) {
                Specification<ManureManagementEmissions> spec = Specification.<ManureManagementEmissions>where(null);

                if (year != null) {
                        spec = spec.and(hasYear(year));
                }
                if (species != null) {
                        spec = spec.and(hasManureManagementLivestock(species));
                }

                return manureManagementEmissionsRepository.findAll(
                                spec, Sort.by(Sort.Direction.DESC, "year"));
        }

        @Override
        public ManureManagementEmissions getManureManagementEmissionsById(UUID id) {
                return manureManagementEmissionsRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Manure Management emissions not found with id: " + id));
        }

        @Override
        public AquacultureEmissions updateAquacultureEmissions(UUID id, AquacultureEmissionsDto dto) {
                AquacultureEmissions emissions = aquacultureEmissionsRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Aquaculture emissions not found with id: " + id));

                emissions.setYear(dto.getYear());
                emissions.setActivityDesc(dto.getActivityDesc());
                emissions.setFishProduction(dto.getFishProduction());
                emissions.setN2ONEmissions(dto.getFishProduction() * AFOLUConstants.FISH_N20_EF.getValue());
                emissions.setN2OEmissions(
                                dto.getFishProduction() * AFOLUConstants.FISH_N20_EF.getValue() * 44 / 28 * 1000000);
                emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());

                return aquacultureEmissionsRepository.save(emissions);
        }

        @Override
        public EntericFermentationEmissions updateEntericFermentationEmissions(UUID id,
                        EntericFermentationEmissionsDto dto) {
                EntericFermentationEmissions emissions = entericFermentationEmissionsRepository.findById(id)
                                .orElseThrow(
                                                () -> new EntityNotFoundException(
                                                                "Enteric Fermentation emissions not found with id: "
                                                                                + id));

                emissions.setYear(dto.getYear());
                emissions.setAnimalPopulation(dto.getAnimalPopulation());
                emissions.setSpecies(dto.getSpecies());
                emissions.setCH4Emissions(
                                emissions.getAnimalPopulation() * emissions.getSpecies().getEntericFermentationCH4EF()
                                                / 1000);
                emissions.setCO2EqEmissions(emissions.getCH4Emissions() * GWP.CH4.getValue());

                return entericFermentationEmissionsRepository.save(emissions);
        }

        @Override
        public byte[] generateEntericFermentationExcelTemplate() {
                try (Workbook workbook = new XSSFWorkbook();
                                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                        Sheet sheet = workbook.createSheet("Enteric Fermentation Emissions");

                        // Create title style
                        XSSFCellStyle titleStyle = (XSSFCellStyle) workbook.createCellStyle();
                        Font titleFont = workbook.createFont();
                        titleFont.setBold(true);
                        titleFont.setFontHeightInPoints((short) 18);
                        titleFont.setColor(IndexedColors.WHITE.getIndex());
                        titleFont.setFontName("Calibri");
                        titleStyle.setFont(titleFont);
                        titleStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
                        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        titleStyle.setAlignment(HorizontalAlignment.CENTER);
                        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                        titleStyle.setBorderTop(BorderStyle.MEDIUM);
                        titleStyle.setBorderBottom(BorderStyle.MEDIUM);
                        titleStyle.setBorderLeft(BorderStyle.MEDIUM);
                        titleStyle.setBorderRight(BorderStyle.MEDIUM);

                        // Create header style
                        XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
                        Font headerFont = workbook.createFont();
                        headerFont.setBold(true);
                        headerFont.setFontHeightInPoints((short) 11);
                        headerFont.setColor(IndexedColors.WHITE.getIndex());
                        headerFont.setFontName("Calibri");
                        headerStyle.setFont(headerFont);
                        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
                        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        headerStyle.setAlignment(HorizontalAlignment.CENTER);
                        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                        headerStyle.setBorderTop(BorderStyle.THIN);
                        headerStyle.setBorderBottom(BorderStyle.THIN);
                        headerStyle.setBorderLeft(BorderStyle.THIN);
                        headerStyle.setBorderRight(BorderStyle.THIN);
                        headerStyle.setWrapText(true);

                        // Create data style
                        XSSFCellStyle dataStyle = (XSSFCellStyle) workbook.createCellStyle();
                        Font dataFont = workbook.createFont();
                        dataFont.setFontName("Calibri");
                        dataFont.setFontHeightInPoints((short) 10);
                        dataStyle.setFont(dataFont);
                        dataStyle.setBorderBottom(BorderStyle.THIN);
                        dataStyle.setBorderTop(BorderStyle.THIN);
                        dataStyle.setBorderLeft(BorderStyle.THIN);
                        dataStyle.setBorderRight(BorderStyle.THIN);
                        dataStyle.setAlignment(HorizontalAlignment.LEFT);
                        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                        dataStyle.setWrapText(true);

                        // Create alternate data style
                        XSSFCellStyle alternateDataStyle = (XSSFCellStyle) workbook.createCellStyle();
                        Font altDataFont = workbook.createFont();
                        altDataFont.setFontName("Calibri");
                        altDataFont.setFontHeightInPoints((short) 10);
                        alternateDataStyle.setFont(altDataFont);
                        alternateDataStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                        alternateDataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        alternateDataStyle.setBorderBottom(BorderStyle.THIN);
                        alternateDataStyle.setBorderTop(BorderStyle.THIN);
                        alternateDataStyle.setBorderLeft(BorderStyle.THIN);
                        alternateDataStyle.setBorderRight(BorderStyle.THIN);
                        alternateDataStyle.setAlignment(HorizontalAlignment.LEFT);
                        alternateDataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                        alternateDataStyle.setWrapText(true);

                        // Create number style
                        XSSFCellStyle numberStyle = (XSSFCellStyle) workbook.createCellStyle();
                        Font numFont = workbook.createFont();
                        numFont.setFontName("Calibri");
                        numFont.setFontHeightInPoints((short) 10);
                        numberStyle.setFont(numFont);
                        DataFormat dataFormat = workbook.createDataFormat();
                        numberStyle.setDataFormat(dataFormat.getFormat("#,##0.00"));
                        numberStyle.setBorderBottom(BorderStyle.THIN);
                        numberStyle.setBorderTop(BorderStyle.THIN);
                        numberStyle.setBorderLeft(BorderStyle.THIN);
                        numberStyle.setBorderRight(BorderStyle.THIN);
                        numberStyle.setAlignment(HorizontalAlignment.RIGHT);
                        numberStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                        // Create year style (centered number)
                        XSSFCellStyle yearStyle = (XSSFCellStyle) workbook.createCellStyle();
                        yearStyle.cloneStyleFrom(dataStyle);
                        yearStyle.setAlignment(HorizontalAlignment.CENTER);

                        int rowIdx = 0;

                        // Title row
                        Row titleRow = sheet.createRow(rowIdx++);
                        titleRow.setHeightInPoints(30);
                        Cell titleCell = titleRow.createCell(0);
                        titleCell.setCellValue("Enteric Fermentation Emissions Template");
                        titleCell.setCellStyle(titleStyle);
                        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

                        rowIdx++; // Blank row

                        // Create header row
                        Row headerRow = sheet.createRow(rowIdx++);
                        headerRow.setHeightInPoints(22);
                        String[] headers = {
                                        "Year",
                                        "Species",
                                        "Animal Population"
                        };

                        for (int i = 0; i < headers.length; i++) {
                                Cell cell = headerRow.createCell(i);
                                cell.setCellValue(headers[i]);
                                cell.setCellStyle(headerStyle);
                        }

                        // Get all LivestockSpecies enum values for dropdown
                        String[] speciesNames = Arrays.stream(LivestockSpecies.values())
                                        .map(Enum::name)
                                        .toArray(String[]::new);

                        // Create data validation helper
                        DataValidationHelper validationHelper = sheet.getDataValidationHelper();

                        // Data validation for Species column (Column B, index 1)
                        if (speciesNames.length > 0) {
                                CellRangeAddressList speciesList = new CellRangeAddressList(3, 1000, 1, 1);
                                DataValidationConstraint speciesConstraint = validationHelper
                                                .createExplicitListConstraint(speciesNames);
                                DataValidation speciesValidation = validationHelper.createValidation(speciesConstraint,
                                                speciesList);
                                speciesValidation.setShowErrorBox(true);
                                speciesValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
                                speciesValidation.createErrorBox("Invalid Species",
                                                "Please select a valid species from the dropdown list.");
                                speciesValidation.setShowPromptBox(true);
                                speciesValidation.createPromptBox("Species",
                                                "Select a species from the dropdown list.");
                                sheet.addValidationData(speciesValidation);
                        }

                        // Create example data rows
                        Object[] exampleData1 = {
                                        2024,
                                        "DAIRY_LACTATING_COWS",
                                        1500.0
                        };

                        Object[] exampleData2 = {
                                        2025,
                                        "SHEEP",
                                        2500.0
                        };

                        // First example row
                        Row exampleRow1 = sheet.createRow(rowIdx++);
                        exampleRow1.setHeightInPoints(18);
                        for (int i = 0; i < exampleData1.length; i++) {
                                Cell cell = exampleRow1.createCell(i);
                                if (i == 0) { // Year
                                        cell.setCellStyle(yearStyle);
                                        cell.setCellValue(((Number) exampleData1[i]).intValue());
                                } else if (i == 2) { // Animal Population (number)
                                        cell.setCellStyle(numberStyle);
                                        cell.setCellValue(((Number) exampleData1[i]).doubleValue());
                                } else { // Species (text)
                                        cell.setCellStyle(dataStyle);
                                        cell.setCellValue((String) exampleData1[i]);
                                }
                        }

                        // Second example row with alternate style
                        Row exampleRow2 = sheet.createRow(rowIdx++);
                        exampleRow2.setHeightInPoints(18);
                        for (int i = 0; i < exampleData2.length; i++) {
                                Cell cell = exampleRow2.createCell(i);
                                if (i == 0) { // Year
                                        CellStyle altYearStyle = workbook.createCellStyle();
                                        altYearStyle.cloneStyleFrom(alternateDataStyle);
                                        altYearStyle.setAlignment(HorizontalAlignment.CENTER);
                                        cell.setCellStyle(altYearStyle);
                                        cell.setCellValue(((Number) exampleData2[i]).intValue());
                                } else if (i == 2) { // Animal Population (number)
                                        CellStyle altNumStyle = workbook.createCellStyle();
                                        altNumStyle.cloneStyleFrom(numberStyle);
                                        altNumStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                                        altNumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                                        cell.setCellStyle(altNumStyle);
                                        cell.setCellValue(((Number) exampleData2[i]).doubleValue());
                                } else { // Species (text)
                                        cell.setCellStyle(alternateDataStyle);
                                        cell.setCellValue((String) exampleData2[i]);
                                }
                        }

                        // Auto-size columns with limits
                        for (int i = 0; i < headers.length; i++) {
                                sheet.autoSizeColumn(i);
                                int currentWidth = sheet.getColumnWidth(i);
                                int minWidth = 3000;
                                int maxWidth = 20000;
                                if (currentWidth < minWidth) {
                                        sheet.setColumnWidth(i, minWidth);
                                } else if (currentWidth > maxWidth) {
                                        sheet.setColumnWidth(i, maxWidth);
                                }
                        }

                        workbook.write(out);
                        return out.toByteArray();
                } catch (IOException e) {
                        throw new RuntimeException("Error generating Excel template", e);
                }
        }

        @Override
        @Transactional
        public Map<String, Object> createEntericFermentationEmissionsFromExcel(MultipartFile file) {
                List<EntericFermentationEmissions> savedRecords = new ArrayList<>();
                List<Map<String, Object>> skippedRows = new ArrayList<>();
                Set<String> processedYearSpecies = new HashSet<>(); // Track duplicates in file
                int totalProcessed = 0;

                try {
                        List<EntericFermentationEmissionsDto> dtos = ExcelReader.readExcel(
                                        file.getInputStream(),
                                        EntericFermentationEmissionsDto.class,
                                        ExcelType.ENTERIC_FERMENTATION_EMISSIONS);

                        for (int i = 0; i < dtos.size(); i++) {
                                EntericFermentationEmissionsDto dto = dtos.get(i);
                                totalProcessed++;
                                int rowNumber = i + 1; // Excel row number (1-based, accounting for header row)
                                int excelRowNumber = rowNumber + 2; // +2 for title row and blank row

                                // Validate required fields
                                List<String> missingFields = new ArrayList<>();
                                if (dto.getYear() == 0) {
                                        missingFields.add("Year");
                                }
                                if (dto.getSpecies() == null) {
                                        missingFields.add("Species");
                                }
                                if (dto.getAnimalPopulation() <= 0) {
                                        missingFields.add("Animal Population");
                                }

                                if (!missingFields.isEmpty()) {
                                        Map<String, Object> skipInfo = new HashMap<>();
                                        skipInfo.put("row", excelRowNumber);
                                        skipInfo.put("year", dto.getYear() > 0 ? dto.getYear() : "N/A");
                                        skipInfo.put("species", dto.getSpecies() != null ? dto.getSpecies().name() : "N/A");
                                        skipInfo.put("reason", "Missing required fields: " + String.join(", ", missingFields));
                                        skippedRows.add(skipInfo);
                                        continue; // Skip this row
                                }

                                // Validate year (must be >= 1900)
                                if (dto.getYear() < 1900 || dto.getYear() > 2100) {
                                        Map<String, Object> skipInfo = new HashMap<>();
                                        skipInfo.put("row", excelRowNumber);
                                        skipInfo.put("year", dto.getYear());
                                        skipInfo.put("species", dto.getSpecies().name());
                                        skipInfo.put("reason", "Year must be between 1900 and 2100");
                                        skippedRows.add(skipInfo);
                                        continue; // Skip this row
                                }

                                // Check for duplicate in same file
                                String yearSpeciesKey = dto.getYear() + "_" + dto.getSpecies().name();
                                if (processedYearSpecies.contains(yearSpeciesKey)) {
                                        Map<String, Object> skipInfo = new HashMap<>();
                                        skipInfo.put("row", excelRowNumber);
                                        skipInfo.put("year", dto.getYear());
                                        skipInfo.put("species", dto.getSpecies().name());
                                        skipInfo.put("reason", "Duplicate year and species combination in the same file");
                                        skippedRows.add(skipInfo);
                                        continue; // Skip this row
                                }
                                processedYearSpecies.add(yearSpeciesKey);

                                // Check if record with same year AND species already exists
                                if (entericFermentationEmissionsRepository.findByYearAndSpecies(dto.getYear(), dto.getSpecies()).isPresent()) {
                                        Map<String, Object> skipInfo = new HashMap<>();
                                        skipInfo.put("row", excelRowNumber);
                                        skipInfo.put("year", dto.getYear());
                                        skipInfo.put("species", dto.getSpecies().name());
                                        skipInfo.put("reason", "Record with this year and species combination already exists");
                                        skippedRows.add(skipInfo);
                                        continue; // Skip this row
                                }

                                // Create the record using existing create method
                                try {
                                        EntericFermentationEmissions saved = createEntericFermentationEmissions(dto);
                                        savedRecords.add(saved);
                                } catch (RuntimeException e) {
                                        String errorMessage = e.getMessage();
                                        Map<String, Object> skipInfo = new HashMap<>();
                                        skipInfo.put("row", excelRowNumber);
                                        skipInfo.put("year", dto.getYear());
                                        skipInfo.put("species", dto.getSpecies().name());
                                        skipInfo.put("reason", errorMessage != null ? errorMessage : "Error creating record");
                                        skippedRows.add(skipInfo);
                                        continue; // Skip this row
                                }
                        }

                        // Calculate total skipped count
                        int totalSkipped = skippedRows.size();

                        Map<String, Object> result = new HashMap<>();
                        result.put("saved", savedRecords);
                        result.put("savedCount", savedRecords.size());
                        result.put("skippedCount", totalSkipped);
                        result.put("skippedRows", skippedRows);
                        result.put("totalProcessed", totalProcessed);

                        return result;
                } catch (IOException e) {
                        // Re-throw IOException with user-friendly message
                        String message = e.getMessage();
                        if (message != null) {
                                throw new RuntimeException(message, e);
                        } else {
                                throw new RuntimeException(
                                                "Incorrect template. Please download the correct template and try again.",
                                                e);
                        }
                } catch (NullPointerException e) {
                        // Handle null pointer exceptions with clear message
                        throw new RuntimeException(
                                        "Missing required fields. Please fill in all required fields in your Excel file.", e);
                } catch (Exception e) {
                        String errorMsg = e.getMessage();
                        if (errorMsg != null) {
                                throw new RuntimeException(errorMsg, e);
                        }
                        throw new RuntimeException("Error processing Excel file. Please check your file and try again.", e);
                }
        }

        @Override
        public LimingEmissions updateLimingEmissions(UUID id, LimingEmissionsDto dto) {
                LimingEmissions emissions = limingEmissionsRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Liming emissions not found with id: " + id));

                emissions.setYear(dto.getYear());
                emissions.setMaterial(dto.getMaterial());
                emissions.setCaCO3Qty(dto.getCaCO3Qty());
                emissions.setCO2Emissions(
                                emissions.getMaterial().getLimingConstant()
                                                * AFOLUConstants.CONVERSION_FACTOR.getValue());

                return limingEmissionsRepository.save(emissions);
        }

        @Override
        public AnimalManureAndCompostEmissions updateAnimalManureAndCompostEmissions(UUID id,
                        AnimalManureAndCompostEmissionsDto dto) {
                AnimalManureAndCompostEmissions emissions = animalManureAndCompostEmissionsRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Animal Manure and Compost emissions not found with id: " + id));

                emissions.setYear(dto.getYear());
                emissions.setLivestockSpecies(dto.getSpecies());
                emissions.setAmendmentType(getAmendmentTypeByLivestockSpecies(dto.getSpecies()));
                emissions.setPopulation(dto.getPopulation());
                emissions.setTotalN(emissions.getLivestockSpecies().getManureNitrogenEF() * emissions.getPopulation());
                emissions.setNAvailable(
                                emissions.getTotalN() * emissions.getLivestockSpecies().getMeanLossesOfNinManureMMS());
                emissions.setN2ONEmissions(
                                emissions.getNAvailable() * emissions.getLivestockSpecies()
                                                .getEFFOrgManureCompostAppliedInFields());
                emissions.setN2OEmissions(emissions.getN2ONEmissions() * 44 / 28);
                emissions.setCH4Emissions(
                                emissions.getPopulation() * emissions.getLivestockSpecies().getManureManagementCH4EF());
                emissions.setCO2EqEmissions((emissions.getN2OEmissions() * GWP.N2O.getValue())
                                + (emissions.getCH4Emissions() * GWP.CH4.getValue()));

                return animalManureAndCompostEmissionsRepository.save(emissions);
        }

        @Override
        public RiceCultivationEmissions updateRiceCultivationEmissions(UUID id, RiceCultivationEmissionsDto dto) {
                RiceCultivationEmissions emissions = riceCultivationEmissionsRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Rice Cultivation emissions not found with id: " + id));

                emissions.setYear(dto.getYear());
                emissions.setRiceEcosystem(dto.getRiceEcosystem());
                emissions.setWaterRegime(dto.getWaterRegime());
                emissions.setHarvestedArea(dto.getHarvestedArea());
                emissions.setCultivationPeriod(dto.getCultivationPeriod());
                emissions.setAdjDailyEFEmissions(AFOLUConstants.EFC.getValue() * dto.getWaterRegime().getValue()
                                * AFOLUConstants.SFP.getValue() * AFOLUConstants.SFOA.getValue()
                                * AFOLUConstants.SFSR.getValue());
                emissions.setAnnualCH4Emissions(
                                dto.getHarvestedArea() * dto.getCultivationPeriod() * emissions.getAdjDailyEFEmissions()
                                                / 1000000);
                emissions.setCO2EqEmissions(emissions.getAnnualCH4Emissions() * GWP.CH4.getValue());

                return riceCultivationEmissionsRepository.save(emissions);
        }

        @Override
        public SyntheticFertilizerEmissions updateSyntheticFertilizerEmissions(UUID id,
                        SyntheticFertilizerEmissionsDto dto) {
                SyntheticFertilizerEmissions emissions = syntheticFertilizerEmissionsRepository.findById(id)
                                .orElseThrow(
                                                () -> new EntityNotFoundException(
                                                                "Synthetic Fertilizer emissions not found with id: "
                                                                                + id));

                emissions.setYear(dto.getYear());
                emissions.setCropType(dto.getCropType());
                emissions.setFertType(dto.getFertType());
                emissions.setQtyApplied(dto.getQtyApplied());
                emissions.setNAmount(emissions.getQtyApplied() * emissions.getFertType().getNContent());
                emissions.setN2ONEmissions(emissions.getNAmount() * emissions.getFertType().getNContent());
                emissions.setN2OEmissions(emissions.getN2ONEmissions() * 44 / 28);
                emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());

                return syntheticFertilizerEmissionsRepository.save(emissions);
        }

        @Override
        public UreaEmissions updateUreaEmissions(UUID id, UreaEmissionsDto dto) {
                UreaEmissions emissions = ureaEmissionsRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Urea emissions not found with id: " + id));

                emissions.setYear(dto.getYear());
                emissions.setFertilizerName(dto.getFertilizerName());
                emissions.setQty(dto.getQty());
                emissions.setCO2Emissions(emissions.getQty() * AFOLUConstants.UREA_EMISSION_FACTOR.getValue()
                                * AFOLUConstants.CONVERSION_FACTOR.getValue());

                return ureaEmissionsRepository.save(emissions);
        }

        @Override
        public BurningEmissions updateBurningEmissions(UUID id, BurningEmissionsDto dto) {
                BurningEmissions emissions = burningEmissionsRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Burning emissions not found with id: " + id));

                emissions.setYear(dto.getYear());
                emissions.setBurningAgentType(dto.getBurningAgentType());
                emissions.setBurntArea(dto.getBurntArea());
                emissions.setFuelMassAvailable(dto.getFuelMassUnit().toKilograms(dto.getFuelMassAvailable()));
                emissions.setFireType(dto.getFireType());

                Double combustionFactor = dto.getIsEucalyptusForest() ? AFOLUConstants.EUCALYPTUS_FOREST_CF.getValue()
                                : OTHER_FOREST_CF.getValue();
                emissions.setFuelMassConsumed(emissions.getFuelMassAvailable() * combustionFactor);

                // Emissions Calculations
                emissions.setCO2Emissions(
                                emissions.getFuelMassAvailable() * emissions.getBurningAgentType().getCO2EF());
                emissions.setCH4Emissions(
                                emissions.getFuelMassAvailable() * emissions.getBurningAgentType().getCH4EF());
                emissions.setN2OEmissions(
                                emissions.getFuelMassAvailable() * emissions.getBurningAgentType().getN2OEF());

                emissions.setCO2EqEmissions(
                                emissions.getCO2Emissions() + (emissions.getCH4Emissions() * GWP.CH4.getValue())
                                                + (emissions.getN2OEmissions() * GWP.N2O.getValue()));

                return burningEmissionsRepository.save(emissions);
        }

        @Override
        public CropResiduesEmissions updateCropResidueEmissions(UUID id, CropResiduesEmissionsDto dto) {
                CropResiduesEmissions emissions = cropResiduesEmissionsRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Crop Residues emissions not found with id: " + id));

                emissions.setYear(dto.getYear());
                emissions.setCropType(dto.getCropType());
                emissions.setLandUseCategory(dto.getLandUseCategory());
                emissions.setTotalAreaHarvested(dto.getTotalAreaHarvested());
                emissions.setHarvestedFreshCropYield(dto.getHarvestedFreshCropYield());
                emissions.setHarvestedDMYield(emissions.getTotalAreaHarvested() * emissions.getHarvestedFreshCropYield()
                                * dto.getCropType().getDMFraction());
                emissions.setAGResiduesDryMatter(dto.getAGResiduesDryMatter());
                emissions.setRatioOfAGResiduesDMToHarvestedYield(
                                emissions.getAGResiduesDryMatter() * 1000 / emissions.getHarvestedDMYield());
                emissions.setRatioOfBelowGroundResiduesToHarvestedYield(dto.getCropType().getRatioOfBGRToAGBiomass()
                                * (emissions.getAGResiduesDryMatter() * 1000 + emissions.getHarvestedDMYield())
                                / emissions.getHarvestedDMYield());
                emissions.setNInCropResiduesReturned(dto.getNInCropResiduesReturned());
                emissions.setN2ONEmissions(
                                emissions.getNInCropResiduesReturned() * AFOLUConstants.N_CROP_RESIDUES_EF.getValue());
                emissions.setN2OEmissions(emissions.getN2ONEmissions() * 44 / 28);
                emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());

                return cropResiduesEmissionsRepository.save(emissions);
        }

        @Override
        public PastureExcretionEmissions updatePastureExcretionEmissions(UUID id, PastureExcretionsEmissionsDto dto) {
                PastureExcretionEmissions emissions = pastureExcretionEmissionsRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Pasture Excretion emissions not found with id: " + id));

                emissions.setYear(dto.getYear());
                emissions.setMMS(dto.getMms());
                emissions.setLivestockSpecies(dto.getLivestockSpecies());
                emissions.setAnimalPopulation(dto.getAnimalPopulation());
                emissions.setTotalNExcretionDeposited(
                                emissions.getAnimalPopulation() * emissions.getLivestockSpecies().getAnnualNExcretion()
                                                * emissions.getLivestockSpecies()
                                                                .getFractionOfManureDepositedOnPasture());
                emissions.setN20NEmissions(emissions.getTotalNExcretionDeposited()
                                * emissions.getLivestockSpecies().getNEFManureDepositedOnPasture());
                emissions.setN2OEmissions(emissions.getN20NEmissions() * 44 / 28);
                emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());

                return pastureExcretionEmissionsRepository.save(emissions);
        }

        @Override
        public MineralSoilEmissions updateMineralSoilEmissions(UUID id, MineralSoilEmissionsDto dto) {
                MineralSoilEmissions emissions = mineralSoilEmissionsRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Mineral Soil emissions not found with id: " + id));

                emissions.setYear(dto.getYear());
                emissions.setInitialLandUse(dto.getInitialLandUse());
                emissions.setLandUseInReportingYear(dto.getLandUseInReportingYear());
                emissions.setAvLossOfSoilC(dto.getAvLossOfSoilC());
                emissions.setNMineralisedInMineralSoil(emissions.getAvLossOfSoilC() * 1
                                / emissions.getLandUseInReportingYear().getCNRatioOfSoilOrganicMatter() * 1000);
                emissions.setN20NEmissions(
                                emissions.getNMineralisedInMineralSoil()
                                                * emissions.getLandUseInReportingYear().getEFNMineralised());
                emissions.setN2OEmissions(emissions.getN20NEmissions() * 44 / 28);
                emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());

                return mineralSoilEmissionsRepository.save(emissions);
        }

        @Override
        public VolatilizationEmissions updateVolatilizationEmissions(UUID id, VolatilizationEmissionsDto dto) {
                VolatilizationEmissions emissions = volatilizationEmissionsRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Volatilization emissions not found with id: " + id));

                emissions.setYear(dto.getYear());
                emissions.setMMS(dto.getMms());
                emissions.setLivestockSpecies(dto.getLivestockSpecies());
                emissions.setAnimalPopulation(dto.getAnimalPopulation());
                emissions.setTotalNExcretionForMMS(
                                emissions.getAnimalPopulation() * emissions.getLivestockSpecies().getExcretionRate());
                emissions.setManureVolatilizationNLoss(
                                emissions.getTotalNExcretionForMMS()
                                                * emissions.getMMS().getFractionOfManureNThatVolatilizes());
                emissions.setIndirectVolatilizationN2OEmissionsFromVolatilization(
                                emissions.getManureVolatilizationNLoss()
                                                * AFOLUConstants.EF_N2O_AtmoNDeposition.getValue() * 44 / 28);
                emissions.setCO2EqEmissions(
                                emissions.getIndirectVolatilizationN2OEmissionsFromVolatilization()
                                                * GWP.N2O.getValue());

                return volatilizationEmissionsRepository.save(emissions);
        }

        @Override
        public LeachingEmissions updateLeachingEmissions(UUID id, LeachingEmissionsDto dto) {
                LeachingEmissions emissions = leachingEmissionsRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Leaching emissions not found with id: " + id));

                emissions.setYear(dto.getYear());
                emissions.setMMS(dto.getMms());
                emissions.setLivestockSpecies(dto.getLivestockSpecies());
                emissions.setNumberOfAnimals(dto.getNumberOfAnimals());

                // Step 1: Calculate Total N excretion for MMS = Number of animals × N excretion
                // rate
                double nExcretionRate = dto.getLivestockSpecies().getExcretionRate();
                emissions.setTotalNExcretionForMMS(dto.getNumberOfAnimals() * nExcretionRate);

                // Step 2: Calculate N loss due to leaching = Total N excretion × Frac(leachMS)
                emissions.setManureNLossDueToLeachingAndRunoff(emissions.getTotalNExcretionForMMS()
                                * emissions.getMMS().getFractionOfManureNThatVolatilizes());

                // Step 3: Calculate N2O-N from leaching = N loss due to leaching × EF_leaching
                // (0.0075)
                emissions.setN2ONFromLeaching(emissions.getManureNLossDueToLeachingAndRunoff()
                                * ManureManagementConstants.N2O_LEACHING_EF.getValue());

                // Step 4: Calculate N2O emissions = N2O-N × (44/28)
                emissions.setIndirectN2OEmissionsFromLeaching(emissions.getN2ONFromLeaching()
                                * ManureManagementConstants.N2O_CONVERSION_FACTOR.getValue());

                // Step 5: Calculate CO2Eq emissions = N2O × GWP_N2O (298)
                emissions.setCO2EqEmissions(emissions.getIndirectN2OEmissionsFromLeaching() * GWP.N2O.getValue());

                return leachingEmissionsRepository.save(emissions);
        }

        @Override
        public AtmosphericDepositionEmissions updateAtmosphericNDepositionEmissions(UUID id,
                        AtmosphericDepositionEmissionsDto dto) {
                AtmosphericDepositionEmissions emissions = atmosphericDepositionEmissionsRepository.findById(id)
                                .orElseThrow(
                                                () -> new EntityNotFoundException(
                                                                "Atmospheric Deposition emissions not found with id: "
                                                                                + id));

                emissions.setYear(dto.getYear());
                emissions.setLandUseCategory(dto.getLandUseCategory());
                emissions.setSyntheticNVolatilized(dto.getSyntheticNThatVolatilizes());
                emissions.setOrganicNAdditions(dto.getOrganicNSoilAdditions());
                emissions.setExcretionsDepositedByGrazingAnimals(dto.getExcretionsDepositedByGrazingAnimals());
                emissions.setAnnualN2ONFromAtmosphericDeposition((emissions.getSyntheticNVolatilized()
                                + (emissions.getOrganicNAdditions()
                                                + emissions.getExcretionsDepositedByGrazingAnimals())
                                                * AFOLUConstants.FRACTION_OF_APPLIED_ORGANIC_N_EXCRETIONS_THAT_VOLATILIZES
                                                                .getValue())
                                * AFOLUConstants.EF_N2O_AtmoNDeposition.getValue());
                emissions.setN2OEmissions(emissions.getAnnualN2ONFromAtmosphericDeposition() * 44 / 28);
                emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());

                return atmosphericDepositionEmissionsRepository.save(emissions);
        }

        @Override
        public LeachingAndRunoffEmissions updateLeachingAndRunoffEmissions(UUID id, LeachingAndRunoffEmissionsDto dto) {
                LeachingAndRunoffEmissions emissions = leachingAndRunoffEmissionsRepository.findById(id)
                                .orElseThrow(
                                                () -> new EntityNotFoundException(
                                                                "Leaching and Runoff emissions not found with id: "
                                                                                + id));

                emissions.setYear(dto.getYear());
                emissions.setLandUseCategory(dto.getLandUseCategory());
                emissions.setSyntheticNAppliedToSoil(dto.getSyntheticNApplied());
                emissions.setOrganicAdditionsAppliedToSoil(dto.getOrganicSoilAdditions());
                emissions.setExcretionsDepositedByGrazingAnimals(dto.getExcretionsDepositedByGrazingAnimals());
                emissions.setNInCropResidues(dto.getNInCropResidues());
                emissions.setNMineralizedInMineralSoils(dto.getNMineralizedInMineralSoils());
                emissions.setN2ONProducedFromLeachingAndRunoff((emissions.getSyntheticNAppliedToSoil()
                                + emissions.getOrganicAdditionsAppliedToSoil()
                                + emissions.getExcretionsDepositedByGrazingAnimals()
                                + emissions.getNInCropResidues()
                                + emissions.getNMineralizedInMineralSoils())
                                * emissions.getLandUseCategory().getNFractionAddedToSoilPostLeaching()
                                * emissions.getLandUseCategory().getEF_N2O_LeachAndRunoffNSoilAdditive());
                emissions.setN2OEmissions(emissions.getN2ONProducedFromLeachingAndRunoff() * 44 / 28);
                emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());

                return leachingAndRunoffEmissionsRepository.save(emissions);
        }

        @Override
        public ManureManagementEmissions updateManureManagementEmissions(UUID id, ManureManagementEmissionsDto dto) {
                ManureManagementEmissions emission = manureManagementEmissionsRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Manure Management emissions not found with id: " + id));

                // Update input fields
                emission.setYear(dto.getYear());
                emission.setSpecies(dto.getSpecies());
                emission.setAnimalPopulation(dto.getAnimalPopulation());

                // Recalculate emissions
                calculateEmissions(emission);

                return manureManagementEmissionsRepository.save(emission);
        }

        @Override
        public void deleteAquacultureEmissions(UUID id) {
                if (!aquacultureEmissionsRepository.existsById(id)) {
                        throw new EntityNotFoundException("Aquaculture emissions not found with id: " + id);
                }
                aquacultureEmissionsRepository.deleteById(id);
        }

        @Override
        public void deleteEntericFermentationEmissions(UUID id) {
                if (!entericFermentationEmissionsRepository.existsById(id)) {
                        throw new EntityNotFoundException("Enteric Fermentation emissions not found with id: " + id);
                }
                entericFermentationEmissionsRepository.deleteById(id);
        }

        @Override
        public void deleteLimingEmissions(UUID id) {
                if (!limingEmissionsRepository.existsById(id)) {
                        throw new EntityNotFoundException("Liming emissions not found with id: " + id);
                }
                limingEmissionsRepository.deleteById(id);
        }

        @Override
        public void deleteAnimalManureAndCompostEmissions(UUID id) {
                if (!animalManureAndCompostEmissionsRepository.existsById(id)) {
                        throw new EntityNotFoundException(
                                        "Animal Manure and Compost emissions not found with id: " + id);
                }
                animalManureAndCompostEmissionsRepository.deleteById(id);
        }

        @Override
        public void deleteRiceCultivationEmissions(UUID id) {
                if (!riceCultivationEmissionsRepository.existsById(id)) {
                        throw new EntityNotFoundException("Rice Cultivation emissions not found with id: " + id);
                }
                riceCultivationEmissionsRepository.deleteById(id);
        }

        @Override
        public void deleteSyntheticFertilizerEmissions(UUID id) {
                if (!syntheticFertilizerEmissionsRepository.existsById(id)) {
                        throw new EntityNotFoundException("Synthetic Fertilizer emissions not found with id: " + id);
                }
                syntheticFertilizerEmissionsRepository.deleteById(id);
        }

        @Override
        public void deleteUreaEmissions(UUID id) {
                if (!ureaEmissionsRepository.existsById(id)) {
                        throw new EntityNotFoundException("Urea emissions not found with id: " + id);
                }
                ureaEmissionsRepository.deleteById(id);
        }

        @Override
        public void deleteBurningEmissions(UUID id) {
                if (!burningEmissionsRepository.existsById(id)) {
                        throw new EntityNotFoundException("Burning emissions not found with id: " + id);
                }
                burningEmissionsRepository.deleteById(id);
        }

        @Override
        public void deleteCropResidueEmissions(UUID id) {
                if (!cropResiduesEmissionsRepository.existsById(id)) {
                        throw new EntityNotFoundException("Crop Residues emissions not found with id: " + id);
                }
                cropResiduesEmissionsRepository.deleteById(id);
        }

        @Override
        public void deletePastureExcretionEmissions(UUID id) {
                if (!pastureExcretionEmissionsRepository.existsById(id)) {
                        throw new EntityNotFoundException("Pasture Excretion emissions not found with id: " + id);
                }
                pastureExcretionEmissionsRepository.deleteById(id);
        }

        @Override
        public void deleteMineralSoilEmissions(UUID id) {
                if (!mineralSoilEmissionsRepository.existsById(id)) {
                        throw new EntityNotFoundException("Mineral Soil emissions not found with id: " + id);
                }
                mineralSoilEmissionsRepository.deleteById(id);
        }

        @Override
        public void deleteVolatilizationEmissions(UUID id) {
                if (!volatilizationEmissionsRepository.existsById(id)) {
                        throw new EntityNotFoundException("Volatilization emissions not found with id: " + id);
                }
                volatilizationEmissionsRepository.deleteById(id);
        }

        @Override
        public void deleteLeachingEmissions(UUID id) {
                if (!leachingEmissionsRepository.existsById(id)) {
                        throw new EntityNotFoundException("Leaching emissions not found with id: " + id);
                }
                leachingEmissionsRepository.deleteById(id);
        }

        @Override
        public void deleteAtmosphericNDepositionEmissions(UUID id) {
                if (!atmosphericDepositionEmissionsRepository.existsById(id)) {
                        throw new EntityNotFoundException("Atmospheric Deposition emissions not found with id: " + id);
                }
                atmosphericDepositionEmissionsRepository.deleteById(id);
        }

        @Override
        public void deleteLeachingAndRunoffEmissions(UUID id) {
                if (!leachingAndRunoffEmissionsRepository.existsById(id)) {
                        throw new EntityNotFoundException("Leaching and Runoff emissions not found with id: " + id);
                }
                leachingAndRunoffEmissionsRepository.deleteById(id);
        }

        @Override
        public void deleteManureManagementEmissions(UUID id) {
                if (!manureManagementEmissionsRepository.existsById(id)) {
                        throw new EntityNotFoundException("Manure Management emissions not found with id: " + id);
                }
                manureManagementEmissionsRepository.deleteById(id);
        }

        // Calculation helper method - Excel methodology
        private void calculateEmissions(ManureManagementEmissions emission) {
                ManureManagementLivestock species = emission.getSpecies();
                double population = emission.getAnimalPopulation();

                // Get CH4 emission factor (kg CH4/animal/year)
                double ch4EF = getCH4EmissionFactor(species);

                // Calculate CH4 (tonnes/year) = Population × EF × 10^-3
                double ch4Tonnes = (population * ch4EF) / 1000.0;
                emission.setCh4Tonnes(ch4Tonnes);

                // Calculate CH4 CO2eq (tonnes) = CH4 × 28
                double ch4Co2eq = ch4Tonnes * ManureManagementEmissionFactors.CH4_TO_CO2EQ;
                emission.setCh4Co2eq(ch4Co2eq);

                // Get N2O emission factor (kg N2O/animal/year)
                double n2oEF = getN2OEmissionFactor(species);

                // Calculate N2O (tonnes/year) = Population × EF × 10^-3
                double n2oTonnes = (population * n2oEF) / 1000.0;
                emission.setN2oTonnes(n2oTonnes);

                // Calculate N2O CO2eq (tonnes) = N2O × 28
                double n2oCo2eq = n2oTonnes * ManureManagementEmissionFactors.N2O_TO_CO2EQ;
                emission.setN2oCo2eq(n2oCo2eq);

                // Total CO2eq = CH4_CO2eq + N2O_CO2eq
                double totalCo2eq = ch4Co2eq + n2oCo2eq;
                emission.setTotalCo2eq(totalCo2eq);
        }

        // Helper methods for emission factors
        private double getCH4EmissionFactor(ManureManagementLivestock species) {
                return switch (species) {
                        case DAIRY_COWS_LACTATING ->
                                ManureManagementEmissionFactors.CH4_DAIRY_COWS_LACTATING.getValue();
                        case DAIRY_COWS_OTHER_MATURE ->
                                ManureManagementEmissionFactors.CH4_DAIRY_COWS_OTHER_MATURE.getValue();
                        case DAIRY_COWS_GROWING -> ManureManagementEmissionFactors.CH4_DAIRY_COWS_GROWING.getValue();
                        case SHEEP -> ManureManagementEmissionFactors.CH4_SHEEP.getValue();
                        case GOATS -> ManureManagementEmissionFactors.CH4_GOATS.getValue();
                        case SWINE -> ManureManagementEmissionFactors.CH4_SWINE.getValue();
                        case POULTRY -> ManureManagementEmissionFactors.CH4_POULTRY.getValue();
                        case RABBITS -> ManureManagementEmissionFactors.CH4_RABBITS.getValue();
                };
        }

        private double getN2OEmissionFactor(ManureManagementLivestock species) {
                return switch (species) {
                        case DAIRY_COWS_LACTATING ->
                                ManureManagementEmissionFactors.N2O_DAIRY_COWS_LACTATING.getValue();
                        case DAIRY_COWS_OTHER_MATURE ->
                                ManureManagementEmissionFactors.N2O_DAIRY_COWS_OTHER_MATURE.getValue();
                        case DAIRY_COWS_GROWING -> ManureManagementEmissionFactors.N2O_DAIRY_COWS_GROWING.getValue();
                        case SHEEP -> ManureManagementEmissionFactors.N2O_SHEEP.getValue();
                        case GOATS -> ManureManagementEmissionFactors.N2O_GOATS.getValue();
                        case SWINE -> ManureManagementEmissionFactors.N2O_SWINE.getValue();
                        case POULTRY -> ManureManagementEmissionFactors.N2O_POULTRY.getValue();
                        case RABBITS -> ManureManagementEmissionFactors.N2O_RABBITS.getValue();
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
                Map<Integer, List<AquacultureEmissions>> aquacultureByYear = aquaculture.stream()
                                .collect(groupingBy(AquacultureEmissions::getYear));
                Map<Integer, List<EntericFermentationEmissions>> entericByYear = enteric.stream()
                                .collect(groupingBy(EntericFermentationEmissions::getYear));
                Map<Integer, List<LimingEmissions>> limingByYear = liming.stream()
                                .collect(groupingBy(LimingEmissions::getYear));
                Map<Integer, List<AnimalManureAndCompostEmissions>> manureByYear = manure.stream()
                                .collect(groupingBy(AnimalManureAndCompostEmissions::getYear));
                Map<Integer, List<RiceCultivationEmissions>> riceByYear = rice.stream()
                                .collect(groupingBy(RiceCultivationEmissions::getYear));
                Map<Integer, List<SyntheticFertilizerEmissions>> fertilizerByYear = fertilizer.stream()
                                .collect(groupingBy(SyntheticFertilizerEmissions::getYear));
                Map<Integer, List<UreaEmissions>> ureaByYear = urea.stream()
                                .collect(groupingBy(UreaEmissions::getYear));

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
                                        ureaByYear.getOrDefault(year, List.of()));
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
                                                data.getTotalN2OEmissions() * GWP.N2O.getValue());

                return data;
        }
}
