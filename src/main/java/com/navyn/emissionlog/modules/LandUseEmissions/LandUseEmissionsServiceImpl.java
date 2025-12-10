package com.navyn.emissionlog.modules.LandUseEmissions;

import com.navyn.emissionlog.Enums.GWP;
import com.navyn.emissionlog.Enums.LandUse.LandCategory;
import com.navyn.emissionlog.Enums.LandUse.LandUseConstants;
import com.navyn.emissionlog.modules.LandUseEmissions.Dtos.*;
import com.navyn.emissionlog.modules.LandUseEmissions.Repositories.*;
import com.navyn.emissionlog.modules.LandUseEmissions.models.*;
import com.navyn.emissionlog.utils.DashboardData;
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
import jakarta.persistence.EntityNotFoundException;

import static com.navyn.emissionlog.utils.Specifications.LandUseEmissionsSpecification.*;
import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
public class LandUseEmissionsServiceImpl implements LandUseEmissionsService {

        private final BiomassGainRepository biomassGainRepository;
        private final DisturbanceBiomassLossRepository disturbanceBiomassLossRepository;
        private final FirewoodRemovalBiomassLossRepository firewoodRemovalBiomassLossRepository;
        private final HarvestedBiomassLossRepository harvestedBiomassLossRepository;
        private final RewettedMineralWetlandsRepository rewettedMineralWetlandsRepository;

        @Override
        public BiomassGain createBiomassGain(BiomassGainDto biomassGainDto) {

                double total_AGB_BGB = LandUseConstants.AVG_ANNUAL_ABG_BIOMASS_GROWTH.getValue()
                                * (1.0 + LandUseConstants.RATIO_BGB_AGB.getValue());

                BiomassGain biomassGain = new BiomassGain();
                biomassGain.setYear(biomassGainDto.getYear());
                biomassGain.setLandCategory(biomassGainDto.getLandCategory());
                biomassGain.setForestArea(biomassGainDto.getForestArea());
                biomassGain.setTotalBiomassGrowth(total_AGB_BGB + biomassGainDto.getForestArea());
                biomassGain.setIncreaseOfBiomassCarbon(
                                (total_AGB_BGB + biomassGainDto.getForestArea())
                                                * LandUseConstants.C_FRACT_DRY_MATTER.getValue());
                biomassGain.setCO2EqOfBiomassCarbonGained(
                                biomassGain.getIncreaseOfBiomassCarbon() * LandUseConstants.C_TO_CO2_FACTOR.getValue());
                return biomassGainRepository.save(biomassGain);
        }

        @Override
        public List<BiomassGain> getAllBiomassGain(Integer year, LandCategory landCategory) {
                Specification<BiomassGain> spec = Specification.<BiomassGain>where(hasYear(year))
                                .and(hasLandCategory(landCategory));
                return biomassGainRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
        }

        @Override
        public BiomassGain updateBiomassGain(UUID id, BiomassGainDto biomassGainDto) {
                BiomassGain biomassGain = biomassGainRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Biomass Gain not found with id: " + id));

                double total_AGB_BGB = LandUseConstants.AVG_ANNUAL_ABG_BIOMASS_GROWTH.getValue()
                                * (1.0 + LandUseConstants.RATIO_BGB_AGB.getValue());

                biomassGain.setYear(biomassGainDto.getYear());
                biomassGain.setLandCategory(biomassGainDto.getLandCategory());
                biomassGain.setForestArea(biomassGainDto.getForestArea());
                biomassGain.setTotalBiomassGrowth(total_AGB_BGB + biomassGainDto.getForestArea());
                biomassGain.setIncreaseOfBiomassCarbon(
                                (total_AGB_BGB + biomassGainDto.getForestArea())
                                                * LandUseConstants.C_FRACT_DRY_MATTER.getValue());
                biomassGain.setCO2EqOfBiomassCarbonGained(
                                biomassGain.getIncreaseOfBiomassCarbon() * LandUseConstants.C_TO_CO2_FACTOR.getValue());

                return biomassGainRepository.save(biomassGain);
        }

        @Override
        public void deleteBiomassGain(UUID id) {
                if (!biomassGainRepository.existsById(id)) {
                        throw new EntityNotFoundException("Biomass Gain not found with id: " + id);
                }
                biomassGainRepository.deleteById(id);
        }

        @Override
        public DisturbanceBiomassLoss createDisturbanceBiomassLoss(
                        DisturbanceBiomassLossDto disturbanceBiomassLossDto) {
                DisturbanceBiomassLoss disturbanceBiomassLoss = new DisturbanceBiomassLoss();
                disturbanceBiomassLoss.setYear(disturbanceBiomassLossDto.getYear());
                disturbanceBiomassLoss.setLandCategory(disturbanceBiomassLossDto.getLandCategory());
                disturbanceBiomassLoss.setForestArea(disturbanceBiomassLossDto.getAffectedForestArea());
                disturbanceBiomassLoss
                                .setAreaAffectedByDisturbance(disturbanceBiomassLossDto.getAreaAffectedByDisturbance());

                // Calculate lossOfBiomassCarbon
                disturbanceBiomassLoss.setLossOfBiomassCarbon(disturbanceBiomassLossDto.getAreaAffectedByDisturbance()
                                * LandUseConstants.ABG_BIOMASS_STOCK.getValue()
                                * (LandUseConstants.RATIO_BGB_AGB.getValue() + 1)
                                * LandUseConstants.C_FRACT_DRY_MATTER.getValue()
                                * LandUseConstants.FRACT_BIOMASS_LOST_DISTURBANCE.getValue());
                disturbanceBiomassLoss.setCO2EqOfBiomassCarbonLoss(
                                disturbanceBiomassLoss.getLossOfBiomassCarbon()
                                                * LandUseConstants.C_TO_CO2_FACTOR.getValue());

                return disturbanceBiomassLossRepository.save(disturbanceBiomassLoss);
        }

        @Override
        public List<DisturbanceBiomassLoss> getAllDisturbanceBiomassLoss(Integer year, LandCategory landCategory) {
                Specification<DisturbanceBiomassLoss> spec = Specification.<DisturbanceBiomassLoss>where(hasYear(year))
                                .and(hasLandCategory(landCategory));
                return disturbanceBiomassLossRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
        }

        @Override
        public DisturbanceBiomassLoss updateDisturbanceBiomassLoss(UUID id,
                        DisturbanceBiomassLossDto disturbanceBiomassLossDto) {
                DisturbanceBiomassLoss disturbanceBiomassLoss = disturbanceBiomassLossRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Disturbance Biomass Loss not found with id: " + id));

                disturbanceBiomassLoss.setYear(disturbanceBiomassLossDto.getYear());
                disturbanceBiomassLoss.setLandCategory(disturbanceBiomassLossDto.getLandCategory());
                disturbanceBiomassLoss.setForestArea(disturbanceBiomassLossDto.getAffectedForestArea());
                disturbanceBiomassLoss
                                .setAreaAffectedByDisturbance(disturbanceBiomassLossDto.getAreaAffectedByDisturbance());

                // Recalculate lossOfBiomassCarbon
                disturbanceBiomassLoss.setLossOfBiomassCarbon(disturbanceBiomassLossDto.getAreaAffectedByDisturbance()
                                * LandUseConstants.ABG_BIOMASS_STOCK.getValue()
                                * (LandUseConstants.RATIO_BGB_AGB.getValue() + 1)
                                * LandUseConstants.C_FRACT_DRY_MATTER.getValue()
                                * LandUseConstants.FRACT_BIOMASS_LOST_DISTURBANCE.getValue());
                disturbanceBiomassLoss.setCO2EqOfBiomassCarbonLoss(
                                disturbanceBiomassLoss.getLossOfBiomassCarbon()
                                                * LandUseConstants.C_TO_CO2_FACTOR.getValue());

                return disturbanceBiomassLossRepository.save(disturbanceBiomassLoss);
        }

        @Override
        public void deleteDisturbanceBiomassLoss(UUID id) {
                if (!disturbanceBiomassLossRepository.existsById(id)) {
                        throw new EntityNotFoundException("Disturbance Biomass Loss not found with id: " + id);
                }
                disturbanceBiomassLossRepository.deleteById(id);
        }

        @Override
        public FirewoodRemovalBiomassLoss createFirewoodRemovalBiomassLoss(
                        FirewoodRemovalBiomassLossDto firewoodRemovalBiomassLossDto) {

                double total_AGB_BGB = LandUseConstants.BIOMASS_CONVERSION_EXPANSION_FACTOR.getValue()
                                * (1.0 + LandUseConstants.RATIO_BGB_AGB.getValue());

                FirewoodRemovalBiomassLoss firewoodRemovalBiomassLoss = new FirewoodRemovalBiomassLoss();
                firewoodRemovalBiomassLoss.setYear(firewoodRemovalBiomassLossDto.getYear());
                firewoodRemovalBiomassLoss.setLandCategory(firewoodRemovalBiomassLossDto.getLandCategory());
                firewoodRemovalBiomassLoss
                                .setRemovedFirewoodAmount(firewoodRemovalBiomassLossDto.getRemovedFirewoodAmount());

                // Calculate lossOfBiomassCarbon
                firewoodRemovalBiomassLoss
                                .setTotalBiomass(total_AGB_BGB
                                                * firewoodRemovalBiomassLossDto.getRemovedFirewoodAmount());
                firewoodRemovalBiomassLoss.setLossOfBiomassCarbon(
                                firewoodRemovalBiomassLoss.getTotalBiomass()
                                                * LandUseConstants.C_FRACT_DRY_MATTER.getValue());
                firewoodRemovalBiomassLoss.setCO2EqOfBiomassCarbonLoss(
                                firewoodRemovalBiomassLoss.getLossOfBiomassCarbon()
                                                * LandUseConstants.C_TO_CO2_FACTOR.getValue());
                return firewoodRemovalBiomassLossRepository.save(firewoodRemovalBiomassLoss);
        }

        @Override
        public List<FirewoodRemovalBiomassLoss> getAllFirewoodRemovalBiomassLoss(Integer year,
                        LandCategory landCategory) {
                Specification<FirewoodRemovalBiomassLoss> spec = Specification
                                .<FirewoodRemovalBiomassLoss>where(hasYear(year))
                                .and(hasLandCategory(landCategory));
                return firewoodRemovalBiomassLossRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
        }

        @Override
        public FirewoodRemovalBiomassLoss updateFirewoodRemovalBiomassLoss(UUID id,
                        FirewoodRemovalBiomassLossDto firewoodRemovalBiomassLossDto) {
                FirewoodRemovalBiomassLoss firewoodRemovalBiomassLoss = firewoodRemovalBiomassLossRepository
                                .findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Firewood Removal Biomass Loss not found with id: " + id));

                double total_AGB_BGB = LandUseConstants.BIOMASS_CONVERSION_EXPANSION_FACTOR.getValue()
                                * (1.0 + LandUseConstants.RATIO_BGB_AGB.getValue());

                firewoodRemovalBiomassLoss.setYear(firewoodRemovalBiomassLossDto.getYear());
                firewoodRemovalBiomassLoss.setLandCategory(firewoodRemovalBiomassLossDto.getLandCategory());
                firewoodRemovalBiomassLoss
                                .setRemovedFirewoodAmount(firewoodRemovalBiomassLossDto.getRemovedFirewoodAmount());

                // Recalculate lossOfBiomassCarbon
                firewoodRemovalBiomassLoss
                                .setTotalBiomass(total_AGB_BGB
                                                * firewoodRemovalBiomassLossDto.getRemovedFirewoodAmount());
                firewoodRemovalBiomassLoss.setLossOfBiomassCarbon(
                                firewoodRemovalBiomassLoss.getTotalBiomass()
                                                * LandUseConstants.C_FRACT_DRY_MATTER.getValue());
                firewoodRemovalBiomassLoss.setCO2EqOfBiomassCarbonLoss(
                                firewoodRemovalBiomassLoss.getLossOfBiomassCarbon()
                                                * LandUseConstants.C_TO_CO2_FACTOR.getValue());

                return firewoodRemovalBiomassLossRepository.save(firewoodRemovalBiomassLoss);
        }

        @Override
        public void deleteFirewoodRemovalBiomassLoss(UUID id) {
                if (!firewoodRemovalBiomassLossRepository.existsById(id)) {
                        throw new EntityNotFoundException("Firewood Removal Biomass Loss not found with id: " + id);
                }
                firewoodRemovalBiomassLossRepository.deleteById(id);
        }

        @Override
        public HarvestedBiomassLoss createHarvestedBiomassLoss(HarvestedBiomassLossDto harvestedBiomassLossDto) {

                double total_AGB_BGB = LandUseConstants.BIOMASS_CONVERSION_EXPANSION_FACTOR.getValue()
                                * (1.0 + LandUseConstants.RATIO_BGB_AGB.getValue());

                HarvestedBiomassLoss harvestedBiomassLoss = new HarvestedBiomassLoss();
                harvestedBiomassLoss.setYear(harvestedBiomassLossDto.getYear());
                harvestedBiomassLoss.setLandCategory(harvestedBiomassLossDto.getLandCategory());
                harvestedBiomassLoss.setHarvestedWood(harvestedBiomassLossDto.getHarvestedWood());

                // Calculate lossOfBiomassCarbon
                harvestedBiomassLoss.setTotalBiomass(total_AGB_BGB * harvestedBiomassLossDto.getHarvestedWood());
                harvestedBiomassLoss.setLossOfBiomassCarbon(harvestedBiomassLoss.getTotalBiomass()
                                * LandUseConstants.C_FRACT_DRY_MATTER.getValue());
                harvestedBiomassLoss.setCO2EqOfBiomassCarbonLoss(harvestedBiomassLoss.getLossOfBiomassCarbon()
                                * LandUseConstants.C_TO_CO2_FACTOR.getValue());

                System.out.println("harvestedWood:" + harvestedBiomassLoss.getHarvestedWood());
                System.out.println(harvestedBiomassLossDto);
                System.out.println(harvestedBiomassLoss);

                return harvestedBiomassLossRepository.save(harvestedBiomassLoss);
        }

        @Override
        public List<HarvestedBiomassLoss> getAllHarvestedBiomassLoss(Integer year, LandCategory landCategory) {
                Specification<HarvestedBiomassLoss> spec = Specification.<HarvestedBiomassLoss>where(hasYear(year))
                                .and(hasLandCategory(landCategory));
                return harvestedBiomassLossRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
        }

        @Override
        public HarvestedBiomassLoss updateHarvestedBiomassLoss(UUID id,
                        HarvestedBiomassLossDto harvestedBiomassLossDto) {
                HarvestedBiomassLoss harvestedBiomassLoss = harvestedBiomassLossRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Harvested Biomass Loss not found with id: " + id));

                double total_AGB_BGB = LandUseConstants.BIOMASS_CONVERSION_EXPANSION_FACTOR.getValue()
                                * (1.0 + LandUseConstants.RATIO_BGB_AGB.getValue());

                harvestedBiomassLoss.setYear(harvestedBiomassLossDto.getYear());
                harvestedBiomassLoss.setLandCategory(harvestedBiomassLossDto.getLandCategory());
                harvestedBiomassLoss.setHarvestedWood(harvestedBiomassLossDto.getHarvestedWood());

                // Recalculate lossOfBiomassCarbon
                harvestedBiomassLoss.setTotalBiomass(total_AGB_BGB * harvestedBiomassLossDto.getHarvestedWood());
                harvestedBiomassLoss.setLossOfBiomassCarbon(
                                harvestedBiomassLoss.getTotalBiomass()
                                                * LandUseConstants.C_FRACT_DRY_MATTER.getValue());
                harvestedBiomassLoss.setCO2EqOfBiomassCarbonLoss(
                                harvestedBiomassLoss.getLossOfBiomassCarbon()
                                                * LandUseConstants.C_TO_CO2_FACTOR.getValue());

                return harvestedBiomassLossRepository.save(harvestedBiomassLoss);
        }

        @Override
        public void deleteHarvestedBiomassLoss(UUID id) {
                if (!harvestedBiomassLossRepository.existsById(id)) {
                        throw new EntityNotFoundException("Harvested Biomass Loss not found with id: " + id);
                }
                harvestedBiomassLossRepository.deleteById(id);
        }

        @Override
        public RewettedMineralWetlands createRewettedMineralWetlands(
                        RewettedMineralWetlandsDto rewettedMineralWetlandsDto) {
                RewettedMineralWetlands rewettedMineralWetlands = new RewettedMineralWetlands();
                rewettedMineralWetlands.setYear(rewettedMineralWetlandsDto.getYear());
                rewettedMineralWetlands
                                .setAreaOfRewettedWetlands(rewettedMineralWetlandsDto.getAreaOfRewettedWetlands());

                // Calculate CH4Emissions and CO2EqEmissions
                rewettedMineralWetlands.setCH4Emissions(rewettedMineralWetlandsDto.getAreaOfRewettedWetlands()
                                * LandUseConstants.CH4_EF_REWETTED_LAND.getValue() / 1000000);
                rewettedMineralWetlands
                                .setCO2EqEmissions(rewettedMineralWetlands.getCH4Emissions() * GWP.CH4.getValue());
                return rewettedMineralWetlandsRepository.save(rewettedMineralWetlands);
        }

        @Override
        public List<RewettedMineralWetlands> getAllRewettedMineralWetlands(Integer year) {
                Specification<RewettedMineralWetlands> spec = Specification
                                .<RewettedMineralWetlands>where(hasYear(year));
                return rewettedMineralWetlandsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
        }

        @Override
        public RewettedMineralWetlands updateRewettedMineralWetlands(UUID id,
                        RewettedMineralWetlandsDto rewettedMineralWetlandsDto) {
                RewettedMineralWetlands rewettedMineralWetlands = rewettedMineralWetlandsRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Rewetted Mineral Wetlands not found with id: " + id));

                rewettedMineralWetlands.setYear(rewettedMineralWetlandsDto.getYear());
                rewettedMineralWetlands
                                .setAreaOfRewettedWetlands(rewettedMineralWetlandsDto.getAreaOfRewettedWetlands());

                // Recalculate CH4Emissions and CO2EqEmissions
                rewettedMineralWetlands.setCH4Emissions(rewettedMineralWetlandsDto.getAreaOfRewettedWetlands()
                                * LandUseConstants.CH4_EF_REWETTED_LAND.getValue() / 1000000);
                rewettedMineralWetlands
                                .setCO2EqEmissions(rewettedMineralWetlands.getCH4Emissions() * GWP.CH4.getValue());

                return rewettedMineralWetlandsRepository.save(rewettedMineralWetlands);
        }

        @Override
        public void deleteRewettedMineralWetlands(UUID id) {
                if (!rewettedMineralWetlandsRepository.existsById(id)) {
                        throw new EntityNotFoundException("Rewetted Mineral Wetlands not found with id: " + id);
                }
                rewettedMineralWetlandsRepository.deleteById(id);
        }

        // ============= MINI DASHBOARDS =============

        @Override
        public DashboardData getLandUseDashboardSummary(Integer startingYear, Integer endingYear) {
                // Fetch all 5 land use modules
                List<BiomassGain> gains = biomassGainRepository.findAll();
                List<DisturbanceBiomassLoss> disturbanceLosses = disturbanceBiomassLossRepository.findAll();
                List<FirewoodRemovalBiomassLoss> firewoodLosses = firewoodRemovalBiomassLossRepository.findAll();
                List<HarvestedBiomassLoss> harvestedLosses = harvestedBiomassLossRepository.findAll();
                List<RewettedMineralWetlands> wetlands = rewettedMineralWetlandsRepository.findAll();

                // Filter by year if specified
                if (startingYear != null && endingYear != null) {
                        gains = gains.stream()
                                        .filter(g -> g.getYear() >= startingYear && g.getYear() <= endingYear)
                                        .toList();
                        disturbanceLosses = disturbanceLosses.stream()
                                        .filter(d -> d.getYear() >= startingYear && d.getYear() <= endingYear)
                                        .toList();
                        firewoodLosses = firewoodLosses.stream()
                                        .filter(f -> f.getYear() >= startingYear && f.getYear() <= endingYear)
                                        .toList();
                        harvestedLosses = harvestedLosses.stream()
                                        .filter(h -> h.getYear() >= startingYear && h.getYear() <= endingYear)
                                        .toList();
                        wetlands = wetlands.stream()
                                        .filter(w -> w.getYear() >= startingYear && w.getYear() <= endingYear)
                                        .toList();
                }

                return calculateLandUseDashboardData(gains, disturbanceLosses, firewoodLosses, harvestedLosses,
                                wetlands);
        }

        @Override
        public List<DashboardData> getLandUseDashboardGraph(Integer startingYear, Integer endingYear) {
                // Default to last 5 years if not specified
                if (startingYear == null || endingYear == null) {
                        int currentYear = LocalDateTime.now().getYear();
                        startingYear = currentYear - 4;
                        endingYear = currentYear;
                }

                // Fetch all data
                List<BiomassGain> gains = biomassGainRepository.findAll();
                List<DisturbanceBiomassLoss> disturbanceLosses = disturbanceBiomassLossRepository.findAll();
                List<FirewoodRemovalBiomassLoss> firewoodLosses = firewoodRemovalBiomassLossRepository.findAll();
                List<HarvestedBiomassLoss> harvestedLosses = harvestedBiomassLossRepository.findAll();
                List<RewettedMineralWetlands> wetlands = rewettedMineralWetlandsRepository.findAll();

                // Filter by year range
                final int finalStartYear = startingYear;
                final int finalEndYear = endingYear;

                gains = gains.stream()
                                .filter(g -> g.getYear() >= finalStartYear && g.getYear() <= finalEndYear)
                                .toList();
                disturbanceLosses = disturbanceLosses.stream()
                                .filter(d -> d.getYear() >= finalStartYear && d.getYear() <= finalEndYear)
                                .toList();
                firewoodLosses = firewoodLosses.stream()
                                .filter(f -> f.getYear() >= finalStartYear && f.getYear() <= finalEndYear)
                                .toList();
                harvestedLosses = harvestedLosses.stream()
                                .filter(h -> h.getYear() >= finalStartYear && h.getYear() <= finalEndYear)
                                .toList();
                wetlands = wetlands.stream()
                                .filter(w -> w.getYear() >= finalStartYear && w.getYear() <= finalEndYear)
                                .toList();

                // Group by year
                Map<Integer, List<BiomassGain>> gainsByYear = gains.stream().collect(groupingBy(BiomassGain::getYear));
                Map<Integer, List<DisturbanceBiomassLoss>> disturbanceByYear = disturbanceLosses.stream()
                                .collect(groupingBy(DisturbanceBiomassLoss::getYear));
                Map<Integer, List<FirewoodRemovalBiomassLoss>> firewoodByYear = firewoodLosses.stream()
                                .collect(groupingBy(FirewoodRemovalBiomassLoss::getYear));
                Map<Integer, List<HarvestedBiomassLoss>> harvestedByYear = harvestedLosses.stream()
                                .collect(groupingBy(HarvestedBiomassLoss::getYear));
                Map<Integer, List<RewettedMineralWetlands>> wetlandsByYear = wetlands.stream()
                                .collect(groupingBy(RewettedMineralWetlands::getYear));

                // Create dashboard data for each year
                List<DashboardData> dashboardDataList = new ArrayList<>();
                for (int year = startingYear; year <= endingYear; year++) {
                        DashboardData data = calculateLandUseDashboardData(
                                        gainsByYear.getOrDefault(year, List.of()),
                                        disturbanceByYear.getOrDefault(year, List.of()),
                                        firewoodByYear.getOrDefault(year, List.of()),
                                        harvestedByYear.getOrDefault(year, List.of()),
                                        wetlandsByYear.getOrDefault(year, List.of()));
                        data.setStartingDate(LocalDateTime.of(year, 1, 1, 0, 0).toString());
                        data.setEndingDate(LocalDateTime.of(year, 12, 31, 23, 59).toString());
                        data.setYear(Year.of(year));
                        dashboardDataList.add(data);
                }

                return dashboardDataList;
        }

        private DashboardData calculateLandUseDashboardData(
                        List<BiomassGain> gains,
                        List<DisturbanceBiomassLoss> disturbanceLosses,
                        List<FirewoodRemovalBiomassLoss> firewoodLosses,
                        List<HarvestedBiomassLoss> harvestedLosses,
                        List<RewettedMineralWetlands> wetlands) {

                DashboardData data = new DashboardData();
                Double landUseTotal = 0.0;

                // BiomassGain (carbon removal - subtract)
                for (BiomassGain gain : gains) {
                        landUseTotal -= gain.getCO2EqOfBiomassCarbonGained();
                }

                // Biomass Losses (positive emissions - add)
                for (DisturbanceBiomassLoss loss : disturbanceLosses) {
                        landUseTotal += loss.getCO2EqOfBiomassCarbonLoss();
                }
                for (FirewoodRemovalBiomassLoss loss : firewoodLosses) {
                        landUseTotal += loss.getCO2EqOfBiomassCarbonLoss();
                }
                for (HarvestedBiomassLoss loss : harvestedLosses) {
                        landUseTotal += loss.getCO2EqOfBiomassCarbonLoss();
                }

                // Rewetted Wetlands (positive emissions + CH4)
                for (RewettedMineralWetlands wetland : wetlands) {
                        landUseTotal += wetland.getCO2EqEmissions();
                        data.setTotalCH4Emissions(data.getTotalCH4Emissions() + wetland.getCH4Emissions());
                }

                data.setTotalLandUseEmissions(landUseTotal);

                // Calculate total CO2eq (land use + CH4 from wetlands)
                data.setTotalCO2EqEmissions(
                                landUseTotal +
                                                data.getTotalCH4Emissions() * GWP.CH4.getValue());

                return data;
        }
}
