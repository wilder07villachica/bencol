package com.runicsoft.bencolapp.caja.service;

import com.runicsoft.bencolapp.caja.dtos.request.CajaRequest;
import com.runicsoft.bencolapp.caja.dtos.request.CierreCajaRequest;
import com.runicsoft.bencolapp.caja.dtos.request.MovimientoCajaRequest;
import com.runicsoft.bencolapp.caja.dtos.response.CajaResponse;
import com.runicsoft.bencolapp.caja.dtos.response.MovimientoCajaResponse;

import java.math.BigDecimal;
import java.util.List;

public interface CajaService {
    List<CajaResponse> findAll();
    CajaResponse findById(Long id);
    CajaResponse findCajaAbierta();
    CajaResponse abrirCaja(CajaRequest request);
    MovimientoCajaResponse registrarMovimiento(MovimientoCajaRequest request);
    CajaResponse cerrarCaja(Long id, CierreCajaRequest request);

    void registrarIngreso(BigDecimal monto, String concepto, String referencia);
    void registrarEgreso(BigDecimal monto, String concepto, String referencia);
}