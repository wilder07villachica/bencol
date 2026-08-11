package com.runicsoft.bencolapp.finanzas.service;

import com.runicsoft.bencolapp.finanzas.dtos.request.CuentaCobrarRequest;
import com.runicsoft.bencolapp.finanzas.dtos.request.PagoRequest;
import com.runicsoft.bencolapp.finanzas.dtos.response.CuentaCobrarResponse;
import com.runicsoft.bencolapp.finanzas.dtos.response.DeudaClienteResponse;
import com.runicsoft.bencolapp.finanzas.dtos.response.ResumenFinancieroResponse;
import com.runicsoft.bencolapp.finanzas.utils.EstadoCuenta;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;

import java.time.LocalDate;
import java.util.List;

public interface CuentaCobrarService {
    PaginaResponse<CuentaCobrarResponse> findAll(int pagina, int tamanio, Long clienteId, EstadoCuenta estado, LocalDate desde, LocalDate hasta);
    CuentaCobrarResponse findById(Long id);
    CuentaCobrarResponse findByVentaId(Long ventaId);
    List<CuentaCobrarResponse> findByEstado(EstadoCuenta estado);
    List<CuentaCobrarResponse> findByClienteId(Long clienteId);
    CuentaCobrarResponse create(CuentaCobrarRequest request);
    CuentaCobrarResponse registrarPago(PagoRequest request);

    DeudaClienteResponse obtenerDeudaCliente(Long clienteId);
    ResumenFinancieroResponse obtenerResumenFinanciero();
}