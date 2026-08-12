package com.runicsoft.bencolapp.envases.service;

import com.runicsoft.bencolapp.envases.dtos.request.MovimientoEnvaseRequest;
import com.runicsoft.bencolapp.envases.dtos.response.CuentaEnvasesClienteResponse;
import com.runicsoft.bencolapp.envases.dtos.response.MovimientoEnvaseResponse;

import java.util.List;

public interface EnvaseService {

    List<CuentaEnvasesClienteResponse> findAll();

    CuentaEnvasesClienteResponse findByClienteAndProducto(Long clienteId, Long productoId);

    List<CuentaEnvasesClienteResponse> findByClienteId(Long clienteId);

    List<MovimientoEnvaseResponse> findMovimientosByCuentaId(Long cuentaId);

    CuentaEnvasesClienteResponse registrarMovimiento(MovimientoEnvaseRequest request);
}