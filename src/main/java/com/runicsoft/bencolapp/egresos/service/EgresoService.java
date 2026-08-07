package com.runicsoft.bencolapp.egresos.service;

import com.runicsoft.bencolapp.egresos.dtos.request.EgresoRequest;
import com.runicsoft.bencolapp.egresos.dtos.response.EgresoResponse;
import com.runicsoft.bencolapp.egresos.utils.CategoriaEgreso;

import java.util.List;

public interface EgresoService {
    List<EgresoResponse> findAll();
    EgresoResponse findById(Long id);
    List<EgresoResponse> findByCategoria(CategoriaEgreso categoria);
    EgresoResponse create(EgresoRequest request);
}