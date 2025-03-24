package com.navyn.emissionlog.Validators;

import com.navyn.emissionlog.Enums.EnergyUnits;
import com.navyn.emissionlog.Enums.MassUnits;
import com.navyn.emissionlog.Enums.Metrics;
import com.navyn.emissionlog.Enums.VolumeUnits;
import com.navyn.emissionlog.Payload.Requests.CreateActivityDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UnitForMetricValidator implements ConstraintValidator<ValidUnitForMetric, CreateActivityDto> {

    @Override
    public boolean isValid(CreateActivityDto activity, ConstraintValidatorContext context) {
        if (activity == null) {
            return true;
        }

        Metrics metrics = activity.getMetrics();
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