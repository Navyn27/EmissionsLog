package com.navyn.emissionlog.modules.auth.dtos;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {

    @jakarta.validation.constraints.NotNull(message = "Value can't be empty; An email is required for every user")
    @Email(message = "The provided input is an invalid email")
    private String email;

    @jakarta.validation.constraints.NotNull(message = "Value can't be empty; An email is required for every user")
    private String password;
}
