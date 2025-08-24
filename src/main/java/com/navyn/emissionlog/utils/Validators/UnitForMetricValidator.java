package com.navyn.emissionlog.utils.Validators;

import com.navyn.emissionlog.Enums.Metrics.EnergyUnits;
import com.navyn.emissionlog.Enums.Metrics.MassUnits;
import com.navyn.emissionlog.Enums.Metrics.Metrics;
import com.navyn.emissionlog.Enums.Metrics.VolumeUnits;
import com.navyn.emissionlog.modules.activities.dtos.CreateStationaryActivityDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UnitForMetricValidator implements ConstraintValidator<ValidUnitForMetric, CreateStationaryActivityDto> {

    @Override
    public boolean isValid(CreateStationaryActivityDto activity, ConstraintValidatorContext context) {
        if (activity == null) {
            return true;
        }

        Metrics metrics = activity.getMetric();
        String unit = activity.getFuelUnit();

        switch (metrics) {
            case MASS:
                try {
                    MassUnits.valueOf(unit);
                } catch (IllegalArgumentException e) {
                    return false;
                }
                break;
            case VOLUME:
                try {
                    VolumeUnits.valueOf(unit);
                } catch (IllegalArgumentException e) {
                    return false;
                }
                break;
            case ENERGY:
                try {
                    EnergyUnits.valueOf(unit);
                } catch (IllegalArgumentException e) {
                    return false;
                }
                break;
            default:
                return false;
        }
        return true;
    }
}