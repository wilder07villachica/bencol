package com.runicsoft.bencolapp.clientes.dtos.request;

import com.runicsoft.bencolapp.clientes.utils.CategoriaCliente;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import lombok.Data;

@Data
public class ClienteRequest {
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private CategoriaCliente categoria;
    private EstadoGeneral estado;
}
