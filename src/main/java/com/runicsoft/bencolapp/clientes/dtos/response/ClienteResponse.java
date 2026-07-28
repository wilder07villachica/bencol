package com.runicsoft.bencolapp.clientes.dtos.response;

import com.runicsoft.bencolapp.clientes.utils.CategoriaCliente;
import lombok.Data;

@Data
public class ClienteResponse {
    private Long id;
    private String nombre;
    private String direccion;
    private CategoriaCliente categoria;
}
