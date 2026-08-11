package com.runicsoft.bencolapp.caja.service;

import com.runicsoft.bencolapp.caja.dtos.request.CajaRequest;
import com.runicsoft.bencolapp.caja.dtos.request.CierreCajaRequest;
import com.runicsoft.bencolapp.caja.dtos.request.MovimientoCajaRequest;
import com.runicsoft.bencolapp.caja.dtos.response.CajaResponse;
import com.runicsoft.bencolapp.caja.dtos.response.MovimientoCajaResponse;
import com.runicsoft.bencolapp.caja.utils.EstadoCaja;
import com.runicsoft.bencolapp.caja.utils.TipoMovimientoCaja;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface CajaService {
    PaginaResponse<CajaResponse> findAll(
            int pagina,
            int tamanio,
            EstadoCaja estado,
            LocalDate desde,
            LocalDate hasta
    );
    CajaResponse findById(Long id);
    CajaResponse findCajaAbierta();
    CajaResponse abrirCaja(CajaRequest request);
    MovimientoCajaResponse registrarMovimiento(MovimientoCajaRequest request);
    CajaResponse cerrarCaja(Long id, CierreCajaRequest request);

    void registrarIngreso(BigDecimal monto, String concepto, String referencia);
    void registrarEgreso(BigDecimal monto, String concepto, String referencia);

    PaginaResponse<MovimientoCajaResponse> findMovimientos(
            int pagina,
            int tamanio,
            Long cajaId,
            TipoMovimientoCaja tipoMovimiento,
            LocalDate desde,
            LocalDate hasta
    );
}