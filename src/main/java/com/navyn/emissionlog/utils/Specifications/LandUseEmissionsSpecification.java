package com.navyn.emissionlog.utils.Specifications;

import com.navyn.emissionlog.Enums.LandUse.LandCategory;
import org.springframework.data.jpa.domain.Specification;

public class LandUseEmissionsSpecification {
    
    public static <T> Specification<T> hasYear(Integer year) {
        return (root, query, cb) -> {
            if (year == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("year"), year);
        };
    }
    
    public static <T> Specification<T> hasLandCategory(LandCategory landCategory) {
        return (root, query, cb) -> {
            if (landCategory == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("landCategory"), landCategory);
        };
    }
}
