package com.runicsoft.bencolapp.proveedores.dtos.response;

import com.runicsoft.bencolapp.utils.EstadoGeneral;
import lombok.Data;

@Data
public class ProveedorResponse {
    private Long id;
    private String razonSocial;
    private String ruc;
    private String contacto;
    private String telefono;
    private String email;
    private String direccion;
    private EstadoGeneral estado;
}