package com.runicsoft.bencolapp.proveedores.service;

import com.runicsoft.bencolapp.proveedores.dtos.request.ProveedorRequest;
import com.runicsoft.bencolapp.proveedores.dtos.response.ProveedorResponse;
import com.runicsoft.bencolapp.proveedores.mapper.ProveedorMapper;
import com.runicsoft.bencolapp.proveedores.models.Proveedor;
import com.runicsoft.bencolapp.proveedores.repository.ProveedorRepository;
import com.runicsoft.bencolapp.utils.exceptions.ConflictException;
import com.runicsoft.bencolapp.utils.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.runicsoft.bencolapp.utils.constants.MessageConstants.*;

@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ProveedorMapper proveedorMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorResponse> findAll() {
        List<Proveedor> proveedores = proveedorRepository.findAll();
        return proveedorMapper.convertirListaProveedorDto(proveedores);
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorResponse findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        Proveedor proveedor = getProveedor(id);
        return proveedorMapper.convertirProveedorDto(proveedor);
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorResponse findByRuc(String ruc) {
        if (ruc == null || ruc.isBlank()) {
            throw new IllegalArgumentException(RUC_INVALIDO);
        }

        Proveedor proveedor = proveedorRepository.findByRuc(ruc)
                .orElseThrow(() -> new ResourceNotFoundException(PROVEEDOR_NO_ENCONTRADO));

        return proveedorMapper.convertirProveedorDto(proveedor);
    }

    @Override
    @Transactional
    public ProveedorResponse create(ProveedorRequest request) {
        if (proveedorRepository.existsByRuc(request.getRuc())) {
            throw new ConflictException(RUC_EXISTENTE);
        }

        Proveedor proveedor = proveedorMapper.convertirProveedorEntidad(request);
        Proveedor proveedorGuardado = proveedorRepository.save(proveedor);
        return proveedorMapper.convertirProveedorDto(proveedorGuardado);
    }

    @Override
    @Transactional
    public ProveedorResponse update(Long id, ProveedorRequest request) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        Proveedor proveedor = getProveedor(id);

        if (proveedorRepository.existsByRucAndIdNot(request.getRuc(), id)) {
            throw new ConflictException(RUC_EXISTENTE);
        }

        proveedorMapper.updateProveedor(request, proveedor);
        Proveedor proveedorActualizado = proveedorRepository.save(proveedor);
        return proveedorMapper.convertirProveedorDto(proveedorActualizado);
    }

    private Proveedor getProveedor(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PROVEEDOR_NO_ENCONTRADO));
    }
}