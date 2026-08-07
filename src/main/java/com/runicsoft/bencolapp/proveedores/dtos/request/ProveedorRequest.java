package com.runicsoft.bencolapp.proveedores.dtos.request;

import com.runicsoft.bencolapp.utils.EstadoGeneral;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProveedorRequest {

    @NotBlank(message = "La razón social es obligatoria.")
    @Size(max = 100, message = "La razón social no debe exceder los 100 caracteres.")
    private String razonSocial;

    @NotBlank(message = "El RUC es obligatorio.")
    @Pattern(regexp = "\\d{11}", message = "El RUC debe tener 11 dígitos.")
    private String ruc;

    @Size(max = 100)
    private String contacto;

    @Size(max = 20)
    private String telefono;

    @Email(message = "El correo electrónico no es válido.")
    private String email;

    @Size(max = 255)
    private String direccion;

    private EstadoGeneral estado;
}