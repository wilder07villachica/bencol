package com.runicsoft.bencolapp.empresa.service;

import com.runicsoft.bencolapp.empresa.dtos.request.EmpresaRequest;
import com.runicsoft.bencolapp.empresa.dtos.response.EmpresaResponse;
import com.runicsoft.bencolapp.empresa.mappers.EmpresaMapper;
import com.runicsoft.bencolapp.empresa.models.Empresa;
import com.runicsoft.bencolapp.empresa.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpresaServiceImpl implements EmpresaService{

    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper empresaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EmpresaResponse> listarEmpresas() {
        List<Empresa> listaEmpresas = empresaRepository.findAll();
        return empresaMapper.convertirListaEmpresaDto(listaEmpresas);
    }

    @Override
    @Transactional(readOnly = true)
    public EmpresaResponse buscarEmpresa(Long idEmpresa) {
        if (idEmpresa == null || idEmpresa <= 0) {
            throw new IllegalArgumentException("Identificador inválido");
        }
        Empresa empresa = empresaRepository.findById(idEmpresa).orElseThrow(
                () -> new IllegalArgumentException("Empresa no encontrada")
        );
        return empresaMapper.convertirEmpresaDto(empresa);
    }

    @Override
    @Transactional
    public EmpresaResponse registrarEmpresa(EmpresaRequest request) {
        if (empresaRepository.existsByRuc(request.getRuc())) {
            throw new IllegalArgumentException("Empresa con RUC: "  + request.getRuc() + "Ya existe.");
        }
        Empresa empresa = empresaMapper.convertirEmpresaEntidad(request);
        empresaRepository.save(empresa);
        return empresaMapper.convertirEmpresaDto(empresa);
    }
}
