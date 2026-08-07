package com.runicsoft.bencolapp.caja.service;

import com.runicsoft.bencolapp.caja.dtos.request.CajaRequest;
import com.runicsoft.bencolapp.caja.dtos.request.MovimientoCajaRequest;
import com.runicsoft.bencolapp.caja.dtos.response.CajaResponse;
import com.runicsoft.bencolapp.caja.dtos.response.MovimientoCajaResponse;

import java.util.List;

public interface CajaService {
    List<CajaResponse> findAll();
    CajaResponse findById(Long id);
    CajaResponse findCajaAbierta();
    CajaResponse abrirCaja(CajaRequest request);
    MovimientoCajaResponse registrarMovimiento(MovimientoCajaRequest request);
    CajaResponse cerrarCaja(Long id);
}