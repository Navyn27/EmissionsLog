package com.navyn.emissionlog.modules.auth.dtos;

import com.navyn.emissionlog.modules.workspace.Workspace;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginResponse {
    private String jwtToken;
    private String tokenType = "Bearer";
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private GrantedAuthority role;
    private Workspace record;
}
