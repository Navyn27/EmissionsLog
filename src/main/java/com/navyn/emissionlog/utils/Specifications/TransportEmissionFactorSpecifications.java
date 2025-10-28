package com.navyn.emissionlog.utils.Specifications;

import com.navyn.emissionlog.Enums.Transport.RegionGroup;
import com.navyn.emissionlog.Enums.Transport.TransportType;
import com.navyn.emissionlog.Enums.Transport.VehicleEngineType;
import com.navyn.emissionlog.modules.fuel.Fuel;
import com.navyn.emissionlog.modules.transportEmissions.models.TransportFuelEmissionFactors;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specifications for flexible Transport Emission Factor matching.
 * Handles "ANY" wildcard values to ensure proper factor retrieval.
 */
public class TransportEmissionFactorSpecifications {

    /**
     * Creates a flexible specification that matches emission factors with wildcard support.
     * Handles "ANY" values in TransportType and VehicleEngineType as wildcards.
     * 
     * @param fuel The fuel to match (required, exact match)
     * @param regionGroup The region group to match (required, exact match)
     * @param transportType The transport type to match (supports ANY wildcard)
     * @param vehicleEngineType The vehicle engine type to match (supports ANY wildcard)
     * @return Specification that matches flexibly with wildcard support
     */
    public static Specification<TransportFuelEmissionFactors> matchesFlexibly(
            Fuel fuel,
            RegionGroup regionGroup,
            TransportType transportType,
            VehicleEngineType vehicleEngineType) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Fuel and Region must match exactly (no wildcards)
            if (fuel != null) {
                predicates.add(criteriaBuilder.equal(root.get("fuel"), fuel));
            }
            
            if (regionGroup != null) {
                predicates.add(criteriaBuilder.equal(root.get("regionGroup"), regionGroup));
            }

            // TransportType: match exactly OR is ANY (wildcard support)
            if (transportType != null) {
                Predicate transportPredicate = criteriaBuilder.or(
                    criteriaBuilder.equal(root.get("transportType"), transportType),
                    criteriaBuilder.equal(root.get("transportType"), TransportType.ANY)
                );
                predicates.add(transportPredicate);
            }

            // VehicleEngineType: match exactly OR is ANY (wildcard support)
            if (vehicleEngineType != null) {
                Predicate enginePredicate = criteriaBuilder.or(
                    criteriaBuilder.equal(root.get("vehicleEngineType"), vehicleEngineType),
                    criteriaBuilder.equal(root.get("vehicleEngineType"), VehicleEngineType.ANY)
                );
                predicates.add(enginePredicate);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Creates a specification for filtering by fuel only.
     * 
     * @param fuel The fuel to filter by
     * @return Specification for fuel filtering
     */
    public static Specification<TransportFuelEmissionFactors> hasFuel(Fuel fuel) {
        return (root, query, criteriaBuilder) -> {
            if (fuel == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("fuel"), fuel);
        };
    }

    /**
     * Creates a specification for filtering by region group.
     * 
     * @param regionGroup The region group to filter by
     * @return Specification for region filtering
     */
    public static Specification<TransportFuelEmissionFactors> hasRegionGroup(RegionGroup regionGroup) {
        return (root, query, criteriaBuilder) -> {
            if (regionGroup == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("regionGroup"), regionGroup);
        };
    }

    /**
     * Creates a specification for filtering by transport type (including ANY).
     * 
     * @param transportType The transport type to filter by
     * @return Specification for transport type filtering with ANY support
     */
    public static Specification<TransportFuelEmissionFactors> hasTransportTypeOrAny(TransportType transportType) {
        return (root, query, criteriaBuilder) -> {
            if (transportType == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.or(
                criteriaBuilder.equal(root.get("transportType"), transportType),
                criteriaBuilder.equal(root.get("transportType"), TransportType.ANY)
            );
        };
    }

    /**
     * Creates a specification for filtering by vehicle engine type (including ANY).
     * 
     * @param vehicleEngineType The vehicle engine type to filter by
     * @return Specification for engine type filtering with ANY support
     */
    public static Specification<TransportFuelEmissionFactors> hasVehicleEngineTypeOrAny(VehicleEngineType vehicleEngineType) {
        return (root, query, criteriaBuilder) -> {
            if (vehicleEngineType == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.or(
                criteriaBuilder.equal(root.get("vehicleEngineType"), vehicleEngineType),
                criteriaBuilder.equal(root.get("vehicleEngineType"), VehicleEngineType.ANY)
            );
        };
    }
}
