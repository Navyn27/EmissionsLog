package com.navyn.emissionlog.utils;

import com.navyn.emissionlog.modules.LandUseEmissions.Repositories.*;
import com.navyn.emissionlog.modules.LandUseEmissions.models.*;
import com.navyn.emissionlog.modules.activities.models.Activity;
import com.navyn.emissionlog.modules.activities.repositories.ActivityRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.AquacultureEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions.AnimalManureAndCompostEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions.SyntheticFertilizerEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.LimingEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.RiceCultivationEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.UreaEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.Livestock.EntericFermentationEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.AquacultureEmissionsRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.DirectLandEmissions.AnimalManureAndCompostEmissionsRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.DirectLandEmissions.SyntheticFertilizerEmissionsRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.LimingEmissionsRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.RiceCultivationEmissionsRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.UreaEmissionsRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.Livestock.EntericFermentationEmissionsRepository;
import com.navyn.emissionlog.modules.wasteEmissions.WasteDataRepository;
import com.navyn.emissionlog.modules.wasteEmissions.models.WasteDataAbstract;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FetchMethods {

    private final ActivityRepository activityRepository;
    private final WasteDataRepository wasteDataRepository;
    private final AquacultureEmissionsRepository aquacultureEmissionsRepository;
    private final EntericFermentationEmissionsRepository entericFermentationEmissionsRepository;
    private final LimingEmissionsRepository limingEmissionsRepository;
    private final AnimalManureAndCompostEmissionsRepository animalManureAndCompostEmissionsRepository;
    private final RiceCultivationEmissionsRepository riceCultivationEmissionsRepository;
    private final SyntheticFertilizerEmissionsRepository syntheticFertilizerEmissionsRepository;
    private final UreaEmissionsRepository ureaEmissionsRepository;
    private final BiomassGainRepository biomassGainRepository;
    private final DisturbanceBiomassLossRepository disturbanceBiomassLossRepository;
    private final FirewoodRemovalBiomassLossRepository firewoodRemovalBiomassLossRepository;
    private final HarvestedBiomassLossRepository harvestedBiomassLossRepository;
    private final RewettedMineralWetlandsRepository rewettedMineralWetlandsRepository;
    
    public List<Activity> fetchActivities(Integer startYear, Integer endYear) {
        if (startYear != null && endYear != null) {
            LocalDateTime start = LocalDateTime.of(startYear, 1, 1, 0, 0);
            LocalDateTime end = LocalDateTime.of(endYear, 12, 31, 23, 59);
            return activityRepository.findAllByActivityYearBetweenOrderByActivityYearDesc(start, end);
        }
        return activityRepository.findAll();
    }

    public List<WasteDataAbstract> fetchWasteData(Integer startYear, Integer endYear) {
        if (startYear != null && endYear != null) {
            LocalDateTime start = LocalDateTime.of(startYear, 1, 1, 0, 0);
            LocalDateTime end = LocalDateTime.of(endYear, 12, 31, 23, 59);
            return wasteDataRepository.findByActivityYearBetweenOrderByYearDesc(start, end);
        }
        return wasteDataRepository.findAll();
    }

    public List<AquacultureEmissions> fetchAquaculture(Integer startYear, Integer endYear) {
        if (startYear != null && endYear != null) {
            return aquacultureEmissionsRepository.findByYearRange(startYear, endYear);
        }
        return aquacultureEmissionsRepository.findAll();
    }

    public List<EntericFermentationEmissions> fetchEntericFermentation(Integer startYear, Integer endYear) {
        if (startYear != null && endYear != null) {
            return entericFermentationEmissionsRepository.findByYearRange(startYear, endYear);
        }
        return entericFermentationEmissionsRepository.findAll();
    }

    public List<LimingEmissions> fetchLiming(Integer startYear, Integer endYear) {
        if (startYear != null && endYear != null) {
            return limingEmissionsRepository.findByYearRange(startYear, endYear);
        }
        return limingEmissionsRepository.findAll();
    }

    public List<AnimalManureAndCompostEmissions> fetchManure(Integer startYear, Integer endYear) {
        if (startYear != null && endYear != null) {
            return animalManureAndCompostEmissionsRepository.findByYearRange(startYear, endYear);
        }
        return animalManureAndCompostEmissionsRepository.findAll();
    }

    public List<RiceCultivationEmissions> fetchRice(Integer startYear, Integer endYear) {
        if (startYear != null && endYear != null) {
            return riceCultivationEmissionsRepository.findByYearRange(startYear, endYear);
        }
        return riceCultivationEmissionsRepository.findAll();
    }

    public List<SyntheticFertilizerEmissions> fetchFertilizer(Integer startYear, Integer endYear) {
        if (startYear != null && endYear != null) {
            return syntheticFertilizerEmissionsRepository.findByYearRange(startYear, endYear);
        }
        return syntheticFertilizerEmissionsRepository.findAll();
    }

    public List<UreaEmissions> fetchUrea(Integer startYear, Integer endYear) {
        if (startYear != null && endYear != null) {
            return ureaEmissionsRepository.findByYearRange(startYear, endYear);
        }
        return ureaEmissionsRepository.findAll();
    }

    public List<BiomassGain> fetchBiomassGains(Integer startYear, Integer endYear) {
        List<BiomassGain> all = biomassGainRepository.findAll();
        if (startYear != null && endYear != null) {
            return all.stream()
                    .filter(bg -> bg.getYear() >= startYear && bg.getYear() <= endYear)
                    .toList();
        }
        return all;
    }

    public List<DisturbanceBiomassLoss> fetchDisturbanceLosses(Integer startYear, Integer endYear) {
        List<DisturbanceBiomassLoss> all = disturbanceBiomassLossRepository.findAll();
        if (startYear != null && endYear != null) {
            return all.stream()
                    .filter(d -> d.getYear() >= startYear && d.getYear() <= endYear)
                    .toList();
        }
        return all;
    }

    public List<FirewoodRemovalBiomassLoss> fetchFirewoodLosses(Integer startYear, Integer endYear) {
        List<FirewoodRemovalBiomassLoss> all = firewoodRemovalBiomassLossRepository.findAll();
        if (startYear != null && endYear != null) {
            return all.stream()
                    .filter(f -> f.getYear() >= startYear && f.getYear() <= endYear)
                    .toList();
        }
        return all;
    }

    public List<HarvestedBiomassLoss> fetchHarvestedLosses(Integer startYear, Integer endYear) {
        List<HarvestedBiomassLoss> all = harvestedBiomassLossRepository.findAll();
        if (startYear != null && endYear != null) {
            return all.stream()
                    .filter(h -> h.getYear() >= startYear && h.getYear() <= endYear)
                    .toList();
        }
        return all;
    }

    public List<RewettedMineralWetlands> fetchRewettedWetlands(Integer startYear, Integer endYear) {
        List<RewettedMineralWetlands> all = rewettedMineralWetlandsRepository.findAll();
        if (startYear != null && endYear != null) {
            return all.stream()
                    .filter(rw -> rw.getYear() >= startYear && rw.getYear() <= endYear)
                    .toList();
        }
        return all;
    }
}
