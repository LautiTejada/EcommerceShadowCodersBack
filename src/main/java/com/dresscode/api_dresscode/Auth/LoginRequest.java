package com.dresscode.api_dresscode.Auth;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "El username es obligatorio")
    String username;

    @NotBlank(message = "La contraseña es obligatoria")
    String password;
}
