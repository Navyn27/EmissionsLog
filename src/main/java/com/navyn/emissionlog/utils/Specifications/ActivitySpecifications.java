package com.navyn.emissionlog.utils.Specifications;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.modules.activities.models.Activity;
import com.navyn.emissionlog.modules.activities.models.TransportActivityData;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class ActivitySpecifications {

    public static Specification<Activity> isTransportActivity() {
        return (root, query, cb) ->
                cb.equal(root.get("activityData").get("activityType"), ActivityTypes.TRANSPORT);
    }

    public static Specification<Activity> hasTransportMode(TransportModes mode) {
        return (root, query, cb) -> {
            if (mode == null) return cb.conjunction(); // no filter
            var transportData = cb.treat(root.get("activityData"), TransportActivityData.class);
            return cb.equal(transportData.get("modeOfTransport"), mode);
        };
    }

    public static Specification<Activity> hasTransportType(TransportType transportType) {
        return (root, query, cb) -> {
            if (transportType == null) return cb.conjunction(); // no filter
            var transportData = cb.treat(root.get("activityData"), TransportActivityData.class);
            return cb.equal(transportData.get("transportType"), transportType);
        };
    }
    public static Specification<Activity> hasRegion(UUID regionId) {
        return (root, query, cb) -> {
            if (regionId == null) return cb.conjunction();
            return cb.equal(root.get("region").get("id"), regionId);
        };
    }

    public static Specification<Activity> hasFuelType(FuelTypes fuelType) {
        return (root, query, cb) -> {
            if (fuelType == null) return cb.conjunction();
            return cb.equal(root.get("activityData").get("fuelData").get("fuel").get("type"), fuelType);
        };
    }

    public static Specification<Activity> hasFuel(UUID fuelId) {
        return (root, query, cb) -> {
            if (fuelId == null) return cb.conjunction();
            return cb.equal(root.get("activityData").get("fuel").get("id"), fuelId);
        };
    }

    public static Specification<Activity> hasVehicle(UUID vehicleId) {
        return (root, query, cb) -> {
            if (vehicleId == null) return cb.conjunction();
            return cb.equal(root.get("activityData").get("vehicleData").get("id"), vehicleId);
        };
    }

    public static Specification<Activity> hasScope(Scopes scope) {
        return (root, query, cb) -> {
            if (scope == null) return cb.conjunction();
            return cb.equal(root.get("scope"), scope);
        };
    }

    public static Specification<Activity> hasYear(Integer year) {
        return (root, query, cb) -> {
            if (year == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("activityYear").get("year"), year);
        };
    }

    public static Specification<Activity> isStationaryActivity() {
        return (root,query, cb) ->
                cb.equal(root.get("activityData").get("activityType"), ActivityTypes.STATIONARY);
    }

    public static Specification<Activity> hasSector(Sectors sector) {
        return (root, query, cb) -> {
            if (sector == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("sector"), sector);
        };
    }
}