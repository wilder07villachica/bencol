package com.runicsoft.bencolapp.empresa.service;

import com.runicsoft.bencolapp.empresa.dtos.request.EmpresaRequest;
import com.runicsoft.bencolapp.empresa.dtos.response.EmpresaResponse;

import java.util.List;

public interface EmpresaService {
    List<EmpresaResponse> listarEmpresas();
    EmpresaResponse buscarEmpresa(Long idEmpresa);
    EmpresaResponse registrarEmpresa(EmpresaRequest request);
}
