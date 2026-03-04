package com.navyn.emissionlog.modules.auth.dtos;

import com.navyn.emissionlog.Enums.Roles;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO {

    @Size(min = 2, max = 30, message = "The first name must be between 2 and 30 characters")
    private String firstName;

    @Size(min = 2, max = 30, message = "The last name must be between 2 and 30 characters")
    private String lastName;

    @Email(message = "The provided input is an invalid email")
    private String email;

    @Enumerated(EnumType.STRING)
    private Roles role;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
            message = "A valid password should be of length 8-20 characters with at least one uppercase character, lowercase character, a special symbol and a number"
    )
    private String password;

    @Pattern(
            regexp = "^\\+(2507[358]|25072)\\d{7}|\\*25079\\d{7}$\n",
            message = "A valid Rwandan registered phone number should be like:  +25078******* or +25073******* or *25079******* or +25072******* "
    )
    private String phoneNumber;
}
