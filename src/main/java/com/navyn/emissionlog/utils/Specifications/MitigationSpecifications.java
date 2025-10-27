package com.navyn.emissionlog.utils.Specifications;

import com.navyn.emissionlog.Enums.Mitigation.WetlandTreeCategory;
import org.springframework.data.jpa.domain.Specification;

public class MitigationSpecifications {
    
    public static <T> Specification<T> hasYear(Integer year) {
        return (root, query, cb) -> {
            if (year == null) return cb.conjunction();
            return cb.equal(root.get("year"), year);
        };
    }
    
    public static <T> Specification<T> hasWetlandTreeCategory(WetlandTreeCategory category) {
        return (root, query, cb) -> {
            if (category == null) return cb.conjunction();
            return cb.equal(root.get("treeCategory"), category);
        };
    }
}
