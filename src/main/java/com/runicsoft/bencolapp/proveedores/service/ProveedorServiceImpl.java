package com.runicsoft.bencolapp.proveedores.service;

import com.runicsoft.bencolapp.proveedores.dtos.request.ProveedorRequest;
import com.runicsoft.bencolapp.proveedores.dtos.response.ProveedorResponse;
import com.runicsoft.bencolapp.proveedores.mapper.ProveedorMapper;
import com.runicsoft.bencolapp.proveedores.models.Proveedor;
import com.runicsoft.bencolapp.proveedores.repository.ProveedorRepository;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import com.runicsoft.bencolapp.utils.exceptions.ConflictException;
import com.runicsoft.bencolapp.utils.exceptions.ResourceNotFoundException;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public PaginaResponse<ProveedorResponse> findAll(
            int pagina,
            int tamanio,
            String texto,
            EstadoGeneral estado
    ) {
        validarPaginacion(pagina, tamanio);

        if (texto != null && texto.isBlank()) {
            texto = null;
        }

        Pageable pageable = PageRequest.of(
                pagina,
                tamanio,
                Sort.by("razonSocial").ascending()
        );

        Page<Proveedor> proveedores = proveedorRepository.buscar(
                texto,
                estado,
                pageable
        );

        Page<ProveedorResponse> responses = proveedores.map(proveedorMapper::convertirProveedorDto);
        return PaginaResponse.from(responses);
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

    private void validarPaginacion(int pagina, int tamanio) {
        if (pagina < 0) {
            throw new IllegalArgumentException(PAGINA_INVALIDA);
        }

        if (tamanio <= 0 || tamanio > 100) {
            throw new IllegalArgumentException(TAMANIO_PAGINA_INVALIDO);
        }
    }
}