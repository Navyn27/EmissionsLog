package com.navyn.emissionlog.Payload.Requests;

import com.navyn.emissionlog.Enums.Countries;
import lombok.Data;

@Data
public class CreateRegionDto {
    public Countries countries;
    public String province;
    public String city;
}
