package com.runicsoft.bencolapp.finanzas.service;

import com.runicsoft.bencolapp.finanzas.dtos.request.CuentaCobrarRequest;
import com.runicsoft.bencolapp.finanzas.dtos.request.PagoRequest;
import com.runicsoft.bencolapp.finanzas.dtos.response.CuentaCobrarResponse;
import com.runicsoft.bencolapp.finanzas.dtos.response.DeudaClienteResponse;
import com.runicsoft.bencolapp.finanzas.dtos.response.ResumenFinancieroResponse;
import com.runicsoft.bencolapp.finanzas.utils.EstadoCuenta;

import java.util.List;

public interface CuentaCobrarService {
    List<CuentaCobrarResponse> findAll();
    CuentaCobrarResponse findById(Long id);
    CuentaCobrarResponse findByVentaId(Long ventaId);
    List<CuentaCobrarResponse> findByEstado(EstadoCuenta estado);
    List<CuentaCobrarResponse> findByClienteId(Long clienteId);
    CuentaCobrarResponse create(CuentaCobrarRequest request);
    CuentaCobrarResponse registrarPago(PagoRequest request);

    DeudaClienteResponse obtenerDeudaCliente(Long clienteId);
    ResumenFinancieroResponse obtenerResumenFinanciero();
}