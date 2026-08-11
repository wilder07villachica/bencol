package com.runicsoft.bencolapp.finanzas.service;

import com.runicsoft.bencolapp.finanzas.dtos.request.CuentaPagarRequest;
import com.runicsoft.bencolapp.finanzas.dtos.request.PagoProveedorRequest;
import com.runicsoft.bencolapp.finanzas.dtos.response.CuentaPagarResponse;
import com.runicsoft.bencolapp.finanzas.dtos.response.DeudaProveedorResponse;
import com.runicsoft.bencolapp.finanzas.dtos.response.ResumenCuentasPagarResponse;
import com.runicsoft.bencolapp.finanzas.utils.EstadoCuentaPagar;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;

import java.time.LocalDate;
import java.util.List;

public interface CuentaPagarService {
    PaginaResponse<CuentaPagarResponse> findAll(int pagina, int tamanio, Long proveedorId, EstadoCuentaPagar estado, LocalDate desde, LocalDate hasta);
    CuentaPagarResponse findById(Long id);
    CuentaPagarResponse findByCompraId(Long compraId);
    List<CuentaPagarResponse> findByProveedorId(Long proveedorId);
    List<CuentaPagarResponse> findByEstado(EstadoCuentaPagar estado);
    CuentaPagarResponse create(CuentaPagarRequest request);
    CuentaPagarResponse registrarPago(PagoProveedorRequest request);

    DeudaProveedorResponse obtenerDeudaProveedor(Long proveedorId);
    ResumenCuentasPagarResponse obtenerResumenCuentasPagar();
}