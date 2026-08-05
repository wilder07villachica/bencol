package com.runicsoft.bencolapp.clientes.dtos.request;

import com.runicsoft.bencolapp.clientes.utils.CategoriaCliente;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClienteRequest {

    @NotBlank(message = "El nombre del cliente es obligatorio.")
    @Size(max = 50, message = "El nombre no debe exceder los 50 caracteres.")
    private String nombre;

    @NotBlank(message = "La dirección es obligatoria.")
    @Size(max = 200, message = "La dirección no debe exceder los 200 caracteres.")
    private String direccion;

    @NotBlank(message = "El teléfono es obligatorio.")
    @Pattern(regexp = "\\d{9}", message = "El teléfono debe tener 9 dígitos.")
    private String telefono;

    @NotBlank(message = "El correo electrónico es obligatorio.")
    @Email(message = "El correo electrónico no es válido.")
    private String email;

    private CategoriaCliente categoria;
    private EstadoGeneral estado;
}
