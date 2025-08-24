package com.navyn.emissionlog.utils.Specifications;

import com.navyn.emissionlog.Enums.Agriculture.*;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.*;
import com.navyn.emissionlog.modules.agricultureEmissions.models.Livestock.EntericFermentationEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.Livestock.ManureMgmtEmissions;
import org.springframework.data.jpa.domain.Specification;

public class AgricultureSpecifications {
    public static <T>Specification<T> hasYear(Integer year) {
        return (root, query, cb) -> {
            if (year == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("year"), year);
        };
    }

    public static Specification<UreaEmissions> hasYear_UreaEmissions(Integer year) {
        return (root, query, cb) -> {
            if (year == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("year"), year);
        };
    }

    public static Specification<EntericFermentationEmissions> hasSpecies_Enteric(LivestockSpecies species) {
        return (root, query, cb) -> {
            if (species == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("species"), species);
        };
    }

    public static Specification<ManureMgmtEmissions> hasSpecies_Manure(LivestockSpecies species) {
        return (root, query, cb) -> {
            if (species == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("species"), species);
        };
    }

    public static Specification<LimingEmissions> hasLimingMaterial(LimingMaterials material){
        return (root, query, cb) -> {
            if(material == null) return cb.conjunction();
            return cb.equal(root.get("material"), material);
        };
    }

    public static Specification<SyntheticFertilizerEmissions> hasFertilizerType(Fertilizers fertilizerType) {
        return (root, query, cb) -> {
            if (fertilizerType == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("fertType"), fertilizerType);
        };
    }

    public static Specification<UreaEmissions> hasFertilizerName_Urea(String fertilizerType) {
        return (root, query, cb) -> {
            if (fertilizerType == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("fertilizerName"), fertilizerType);
        };
    }

    public static Specification<SyntheticFertilizerEmissions> hasCropType(CropTypes cropType) {
        return (root, query, cb) -> {
            if (cropType == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("cropType"), cropType);
        };
    }

    public static Specification<RiceCultivationEmissions> hasRiceEcosystem(String riceEcosystem) {
        return (root, query, cb) -> {
            if (riceEcosystem == null || riceEcosystem.isEmpty()) return cb.conjunction(); // no filter
            return cb.equal(root.get("riceEcosystem"), riceEcosystem);
        };
    }

    public static Specification<RiceCultivationEmissions> hasWaterRegime(WaterRegime waterRegime) {
        return (root, query, cb) -> {
            if (waterRegime == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("waterRegime"), waterRegime);
        };
    }

    public static Specification<ManureMgmtEmissions> hasAmendmentType(OrganicAmendmentTypes amendmentType) {
        return (root, query, cb) -> {
            if (amendmentType == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("amendmentType"), amendmentType);
        };
    }

    public static Specification<BurningEmissions> hasBurningAgentType(BurningAgentType burningAgentType) {
        return (root, query, cb) -> {
            if (burningAgentType == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("burningAgentType"), burningAgentType);
        };
    }
}
