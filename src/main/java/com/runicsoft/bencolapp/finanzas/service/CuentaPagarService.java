package com.runicsoft.bencolapp.finanzas.service;

import com.runicsoft.bencolapp.finanzas.dtos.request.CuentaPagarRequest;
import com.runicsoft.bencolapp.finanzas.dtos.request.PagoProveedorRequest;
import com.runicsoft.bencolapp.finanzas.dtos.response.CuentaPagarResponse;
import com.runicsoft.bencolapp.finanzas.utils.EstadoCuentaPagar;

import java.util.List;

public interface CuentaPagarService {
    List<CuentaPagarResponse> findAll();
    CuentaPagarResponse findById(Long id);
    CuentaPagarResponse findByCompraId(Long compraId);
    List<CuentaPagarResponse> findByProveedorId(Long proveedorId);
    List<CuentaPagarResponse> findByEstado(EstadoCuentaPagar estado);
    CuentaPagarResponse create(CuentaPagarRequest request);
    CuentaPagarResponse registrarPago(PagoProveedorRequest request);
}