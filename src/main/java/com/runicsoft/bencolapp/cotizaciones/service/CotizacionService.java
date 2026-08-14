package com.runicsoft.bencolapp.cotizaciones.service;

import com.runicsoft.bencolapp.cotizaciones.dtos.request.CotizacionRequest;
import com.runicsoft.bencolapp.cotizaciones.dtos.response.CotizacionResponse;
import com.runicsoft.bencolapp.cotizaciones.utils.EstadoCotizacion;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;

import java.time.LocalDate;

public interface CotizacionService {

    PaginaResponse<CotizacionResponse> findAll(
            int pagina,
            int tamanio,
            String codigo,
            Long clienteId,
            EstadoCotizacion estado,
            LocalDate desde,
            LocalDate hasta
    );

    CotizacionResponse findById(Long id);

    CotizacionResponse findByCodigo(String codigo);

    CotizacionResponse create(CotizacionRequest request);

    CotizacionResponse update(Long id, CotizacionRequest request);

    CotizacionResponse emitir(Long id);

    CotizacionResponse anular(Long id);
}