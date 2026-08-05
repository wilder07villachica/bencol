package com.runicsoft.bencolapp.empresa.service;

import com.runicsoft.bencolapp.empresa.dtos.request.EmpresaRequest;
import com.runicsoft.bencolapp.empresa.dtos.response.EmpresaResponse;

import java.util.List;

public interface EmpresaService {
    List<EmpresaResponse> findAll();
    EmpresaResponse findById(Long idEmpresa);
    EmpresaResponse create(EmpresaRequest request);
}
