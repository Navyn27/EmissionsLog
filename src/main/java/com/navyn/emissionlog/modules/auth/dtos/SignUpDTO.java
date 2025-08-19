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

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDTO {

    @jakarta.validation.constraints.NotNull(message = "The First name is mandatory")
    @Size(min = 2, max = 30, message = "The first name must be between 2 and 30 characters")
    private String firstName;

    @jakarta.validation.constraints.NotNull(message = "The Last name is mandatory")
    @Size(min = 2, max = 30, message = "The last name must be between 2 and 30 characters")
    private String lastName;

    @jakarta.validation.constraints.NotNull(message = "Value can't be empty; An email is required for every user")
    @Email(message = "The provided input is an invalid email")
    private String email;

    @Enumerated(EnumType.STRING)
    private Roles role;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
            message = "A valid password should be of length 8-20 characters with at least one uppercase character, lowercase character, a special symbol and a number"
    )
    private String password;

    @jakarta.validation.constraints.NotNull(message = "Value can't be empty; A password is required for every user")
    @Size(min=8, max=20, message = "A password should be at least 8 characters and 20 at most")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
            message = "A valid password should be of length 8-20 characters with at least one uppercase character, lowercase character, a special symbol and a number"
    )
    private String confirmPassword;

    @jakarta.validation.constraints.NotNull(message = "Value can't be empty; Phone number is mandatory for every user")
    @Pattern(
            regexp = "^\\+(2507[358]|25072)\\d{7}|\\*25079\\d{7}$\n",
            message = "A valid Rwandan registered phone number should be like:  +25078******* or +25073******* or *25079******* or +25072******* "
    )
    private String phoneNumber;
    private UUID record;
}
