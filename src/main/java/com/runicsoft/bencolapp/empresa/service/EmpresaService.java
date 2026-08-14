package com.runicsoft.bencolapp.empresa.service;

import com.runicsoft.bencolapp.empresa.dtos.request.EmpresaRequest;
import com.runicsoft.bencolapp.empresa.dtos.response.EmpresaResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmpresaService {
    List<EmpresaResponse> findAll();
    EmpresaResponse findById(Long idEmpresa);
    EmpresaResponse findActiva();
    EmpresaResponse create(EmpresaRequest request);
    EmpresaResponse update(Long idEmpresa, EmpresaRequest request);
    EmpresaResponse subirLogo(Long idEmpresa, MultipartFile archivo);
    Resource obtenerLogo(Long idEmpresa);
    EmpresaResponse eliminarLogo(Long idEmpresa);
}