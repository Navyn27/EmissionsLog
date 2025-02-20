package com.navyn.emissionlog.Payload.Requests;

import com.navyn.emissionlog.Enums.Country;
import lombok.Data;

@Data
public class CreateRegionDto {
    public Country country;
    public String province;
    public String city;
}
