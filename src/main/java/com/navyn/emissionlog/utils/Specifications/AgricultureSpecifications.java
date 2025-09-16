package com.navyn.emissionlog.utils.Specifications;

import com.navyn.emissionlog.Enums.Agriculture.*;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.*;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions.*;
import jakarta.validation.constraints.Past;
import org.springframework.data.jpa.domain.Specification;

public class AgricultureSpecifications {
    public static <T>Specification<T> hasYear(Integer year) {
        return (root, query, cb) -> {
            if (year == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("year"), year);
        };
    }

    public static <T> Specification<T> hasSpecies(LivestockSpecies species) {
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

    public static Specification<UreaEmissions> hasFertilizerName(String fertilizerType) {
        return (root, query, cb) -> {
            if (fertilizerType == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("fertilizerName"), fertilizerType);
        };
    }

    public static<T> Specification<T> hasCropType(CropTypes cropType) {
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

    public static Specification<AnimalManureAndCompostEmissions> hasAmendmentType(OrganicAmendmentTypes amendmentType) {
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

    public static Specification<CropResiduesEmissions> hasLandUseCategory_CropResidue(LandUseCategory landUseCategory) {
        return (root, query, cb) -> {
            if (landUseCategory == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("landUseCategory"), landUseCategory);
        };
    }

    public static<T> Specification<T> hasCropResiduesCropType(CropResiduesCropType cropType) {
        return (root, query, cb) -> {
            if (cropType == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("burningAgentType"), cropType);
        };
    }

    public static Specification<PastureExcretionEmissions> hasMMS(MMS mms) {
        return (root, query, cb) -> {
            if (mms == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("mms"), mms);
        };
    }

    public static Specification<PastureExcretionEmissions> hasLivestockCategory(LivestockSpecies species) {
        return (root, query, cb) -> {
            if (species == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("livestockSpecies"), species);
        };
    }

    public static Specification<MineralSoilEmissions> hasInitialLandUse(LandUseCategory initialLandUse) {
        return (root, query, cb) -> {
            if (initialLandUse == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("initialLandUse"), initialLandUse);
        };
    }

    public static Specification<MineralSoilEmissions> hasLandUseInReportingYear(LandUseCategory landUseInReportingYear) {
        return (root, query, cb) -> {
            if (landUseInReportingYear == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("landUseInReportingYear"), landUseInReportingYear);
        };
    }
}
