package com.navyn.emissionlog.utils.Specifications;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.modules.agricultureEmissions.models.*;
import org.springframework.data.jpa.domain.Specification;

public class AgricultureSpecifications {

    //year
    public static Specification<AquacultureEmissions> hasYear_Aquaculture(Integer year) {
        return (root, query, cb) -> {
            if (year == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("year"), year);
        };
    }

    public static Specification<EntericFermentationEmissions> hasYear_EntericEmissions(Integer year) {
        return (root, query, cb) -> {
            if (year == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("year"), year);
        };
    }

    public static Specification<LimingEmissions> hasYear_LimingEmissions(Integer year) {
        return (root, query, cb) -> {
            if (year == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("year"), year);
        };
    }

    public static Specification<ManureMgmtEmissions> hasYear_ManureMgmt(Integer year) {
        return (root, query, cb) -> {
            if (year == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("year"), year);
        };
    }

    public static Specification<RiceCultivationEmissions> hasYear_RiceCultivation(Integer year) {
        return (root, query, cb) -> {
            if (year == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("year"), year);
        };
    }

    public static Specification<SyntheticFertilizerEmissions> hasYear_SyntheticFertilizer(Integer year) {
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
}
