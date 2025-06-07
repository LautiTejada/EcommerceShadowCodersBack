package com.dresscode.api_dresscode.Auth;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "El email es obligatorio")
    String email;

    @NotBlank(message = "La contraseña es obligatoria")
    String password;
}
