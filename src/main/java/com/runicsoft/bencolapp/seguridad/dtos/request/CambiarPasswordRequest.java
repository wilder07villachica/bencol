package com.runicsoft.bencolapp.seguridad.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CambiarPasswordRequest {

    @NotBlank(message = "La nueva contraseña es obligatoria.")
    @Size(min = 8, max = 100, message = "La contraseña debe tener como mínimo 8 caracteres.")
    private String nuevaPassword;
}