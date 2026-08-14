package com.runicsoft.bencolapp.empresa.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmpresaRequest {

    @NotBlank(message = "El RUC es un campo obligatorio.")
    @Pattern(regexp = "\\d{11}", message = "El RUC debe tener 11 dígitos.")
    private String ruc;

    @NotBlank(message = "La razón social es un campo obligatorio.")
    @Size(max = 225, message = "La razón social no debe exceder los 225 carácteres.")
    private String razonSocial;

    @Size(max = 225, message = "El nombre comercial de la empresa no debe exceder los 225 carácteres.")
    private String nombreComercial;

    @Size(max = 255, message = "La dirección no debe exceder los 255 carácteres.")
    private String direccion;

    @Size(max = 20, message = "El teléfono no debe exceder los 20 carácteres.")
    private String telefono;

    @Email(message = "El correo electrónico no es válido.")
    @Size(max = 100, message = "El correo electrónico no debe exceder los 100 carácteres.")
    private String email;
}