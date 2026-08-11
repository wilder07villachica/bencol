package com.runicsoft.bencolapp.egresos.service;

import com.runicsoft.bencolapp.egresos.dtos.request.EgresoRequest;
import com.runicsoft.bencolapp.egresos.dtos.response.EgresoResponse;
import com.runicsoft.bencolapp.egresos.utils.CategoriaEgreso;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;

import java.time.LocalDate;
import java.util.List;

public interface EgresoService {
    PaginaResponse<EgresoResponse> findAll(int pagina, int tamanio, CategoriaEgreso categoria, LocalDate desde, LocalDate hasta);
    EgresoResponse findById(Long id);
    List<EgresoResponse> findByCategoria(CategoriaEgreso categoria);
    EgresoResponse create(EgresoRequest request);
}