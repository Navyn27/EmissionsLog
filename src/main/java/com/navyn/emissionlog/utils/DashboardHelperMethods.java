package com.navyn.emissionlog.utils;

import com.navyn.emissionlog.Enums.GWP;
import com.navyn.emissionlog.modules.LandUseEmissions.models.*;
import com.navyn.emissionlog.modules.activities.models.Activity;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.AquacultureEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions.AnimalManureAndCompostEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions.SyntheticFertilizerEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.LimingEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.RiceCultivationEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.UreaEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.Livestock.EntericFermentationEmissions;
import com.navyn.emissionlog.modules.wasteEmissions.models.WasteDataAbstract;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DashboardHelperMethods {
    public static void aggregateActivityEmissions(DashboardData data, List<Activity> activities) {
        for (Activity activity : activities) {
            if (activity.getCH4Emissions() != null) {
                data.setTotalCH4Emissions(data.getTotalCH4Emissions() + activity.getCH4Emissions());
            }
            if (activity.getN2OEmissions() != null) {
                data.setTotalN2OEmissions(data.getTotalN2OEmissions() + activity.getN2OEmissions());
            }
            if (activity.getFossilCO2Emissions() != null) {
                data.setTotalFossilCO2Emissions(data.getTotalFossilCO2Emissions() + activity.getFossilCO2Emissions());
            }
            if (activity.getBioCO2Emissions() != null) {
                data.setTotalBioCO2Emissions(data.getTotalBioCO2Emissions() + activity.getBioCO2Emissions());
            }
        }
    }

    public static void aggregateWasteEmissions(DashboardData data, List<WasteDataAbstract> wasteData) {
        for (WasteDataAbstract waste : wasteData) {
            if (waste.getCH4Emissions() != null) {
                data.setTotalCH4Emissions(data.getTotalCH4Emissions() + waste.getCH4Emissions());
            }
            if (waste.getN2OEmissions() != null) {
                data.setTotalN2OEmissions(data.getTotalN2OEmissions() + waste.getN2OEmissions());
            }
            if (waste.getFossilCO2Emissions() != null) {
                data.setTotalFossilCO2Emissions(data.getTotalFossilCO2Emissions() + waste.getFossilCO2Emissions());
            }
            if (waste.getBioCO2Emissions() != null) {
                data.setTotalBioCO2Emissions(data.getTotalBioCO2Emissions() + waste.getBioCO2Emissions());
            }
        }
    }

    public static void aggregateAgricultureEmissions(DashboardData data,
                                               List<AquacultureEmissions> aquaculture,
                                               List<EntericFermentationEmissions> enteric,
                                               List<LimingEmissions> liming,
                                               List<AnimalManureAndCompostEmissions> manure,
                                               List<RiceCultivationEmissions> rice,
                                               List<SyntheticFertilizerEmissions> fertilizer,
                                               List<UreaEmissions> urea) {

        // Aquaculture - N2O only (double primitive, always present)
        for (AquacultureEmissions a : aquaculture) {
            data.setTotalN2OEmissions(data.getTotalN2OEmissions() + a.getN2OEmissions());
        }

        // Enteric Fermentation - CH4 only (double primitive)
        for (EntericFermentationEmissions e : enteric) {
            data.setTotalCH4Emissions(data.getTotalCH4Emissions() + e.getCH4Emissions());
        }

        // Liming - CO2 (bio) (double primitive)
        for (LimingEmissions l : liming) {
            data.setTotalBioCO2Emissions(data.getTotalBioCO2Emissions() + l.getCO2Emissions());
        }

        // Manure - CH4 and N2O (double primitives)
        for (AnimalManureAndCompostEmissions m : manure) {
            data.setTotalCH4Emissions(data.getTotalCH4Emissions() + m.getCH4Emissions());
            data.setTotalN2OEmissions(data.getTotalN2OEmissions() + m.getN2OEmissions());
        }

        // Rice - CH4 only (double primitive)
        for (RiceCultivationEmissions r : rice) {
            data.setTotalCH4Emissions(data.getTotalCH4Emissions() + r.getAnnualCH4Emissions());
        }

        // Fertilizer - N2O only (double primitive)
        for (SyntheticFertilizerEmissions f : fertilizer) {
            data.setTotalN2OEmissions(data.getTotalN2OEmissions() + f.getN2OEmissions());
        }

        // Urea - CO2 (bio) (double primitive)
        for (UreaEmissions u : urea) {
            data.setTotalBioCO2Emissions(data.getTotalBioCO2Emissions() + u.getCO2Emissions());
        }
    }

    public static void aggregateLandUseEmissions(DashboardData data,
                                           List<BiomassGain> biomassGains,
                                           List<DisturbanceBiomassLoss> disturbanceLosses,
                                           List<FirewoodRemovalBiomassLoss> firewoodLosses,
                                           List<HarvestedBiomassLoss> harvestedLosses,
                                           List<RewettedMineralWetlands> rewettedWetlands) {

        double landUseTotal = 0.0;

        // Biomass Gains (negative emissions - sequestration, double primitive)
        for (BiomassGain bg : biomassGains) {
            landUseTotal -= bg.getCO2EqOfBiomassCarbonGained(); // Subtract because it's sequestration
        }

        // Biomass Losses (positive emissions, double primitives)
        for (DisturbanceBiomassLoss dl : disturbanceLosses) {
            landUseTotal += dl.getCO2EqOfBiomassCarbonLoss();
        }

        for (FirewoodRemovalBiomassLoss fl : firewoodLosses) {
            landUseTotal += fl.getCO2EqOfBiomassCarbonLoss();
        }

        for (HarvestedBiomassLoss hl : harvestedLosses) {
            landUseTotal += hl.getCO2EqOfBiomassCarbonLoss();
        }

        // Rewetted Wetlands (negative emissions from CH4 reduction, double primitive)
        for (RewettedMineralWetlands rw : rewettedWetlands) {
            landUseTotal -= rw.getCO2EqEmissions(); // Rewetting reduces emissions
        }

        data.setTotalLandUseEmissions(landUseTotal);
    }

    public static void calculateCO2Equivalent(DashboardData data) {
        double co2Eq = 0.0;

        // CO2 (1:1 ratio)
        if (data.getTotalFossilCO2Emissions() != null) {
            co2Eq += data.getTotalFossilCO2Emissions();
        }
        if (data.getTotalBioCO2Emissions() != null) {
            co2Eq += data.getTotalBioCO2Emissions();
        }

        // CH4 (GWP = 25)
        if (data.getTotalCH4Emissions() != null) {
            co2Eq += data.getTotalCH4Emissions() * GWP.CH4.getValue();
        }

        // N2O (GWP = 298)
        if (data.getTotalN2OEmissions() != null) {
            co2Eq += data.getTotalN2OEmissions() * GWP.N2O.getValue();
        }

        // Add land use emissions
        if (data.getTotalLandUseEmissions() != null) {
            co2Eq += data.getTotalLandUseEmissions();
        }

        data.setTotalCO2EqEmissions(co2Eq);
    }
}
