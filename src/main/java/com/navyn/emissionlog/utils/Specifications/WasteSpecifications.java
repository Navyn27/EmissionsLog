package com.navyn.emissionlog.utils.Specifications;

import com.navyn.emissionlog.Enums.SolidWasteType;
import com.navyn.emissionlog.Enums.WasteType;
import com.navyn.emissionlog.modules.wasteEmissions.models.SolidWasteData;
import com.navyn.emissionlog.modules.wasteEmissions.models.WasteDataAbstract;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class WasteSpecifications {

    public static Specification<WasteDataAbstract> hasWasteType(WasteType wasteType) {
        return (root,query, cb) -> {
            if(wasteType == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("wasteType"), wasteType);
        };
    }

    public static Specification<WasteDataAbstract> hasRegion(UUID regionId) {
        return (root, query, cb) -> {
            if (regionId == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("region").get("id"), regionId);
        };
    }

    public static Specification<WasteDataAbstract> hasYear(Integer year) {
        return (root, query, cb) -> {
            if (year == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("activityYear").get("year"), year);
        };
    }

    public static Specification<WasteDataAbstract> hasEicvReport(UUID eicvReportId) {
        return (root, query, cb) -> {
            if (eicvReportId == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("eicvReport").get("id"), eicvReportId);
        };
    }

    public static Specification<SolidWasteData> hasSolidWasteType(SolidWasteType solidWasteType) {
        return (root, query, cb) -> {
            if (solidWasteType == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("solidWasteType"), solidWasteType);
        };
    }

    public static Specification<SolidWasteData> hasRegion_solidWaste(UUID regionId) {
        return (root, query, cb) -> {
            if (regionId == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("region").get("id"), regionId);
        };
    }

    public static Specification<SolidWasteData> hasYear_solidWaste(Integer year) {
        return (root, query, cb) -> {
            if (year == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("activityYear").get("year"), year);
        };
    }

}
