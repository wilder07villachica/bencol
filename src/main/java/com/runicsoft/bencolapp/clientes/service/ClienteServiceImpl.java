package com.runicsoft.bencolapp.clientes.service;

import com.runicsoft.bencolapp.clientes.dtos.request.ClienteRequest;
import com.runicsoft.bencolapp.clientes.dtos.response.ClienteResponse;
import com.runicsoft.bencolapp.clientes.mappers.ClienteMapper;
import com.runicsoft.bencolapp.clientes.models.Cliente;
import com.runicsoft.bencolapp.clientes.repository.ClienteRepository;
import com.runicsoft.bencolapp.clientes.utils.CategoriaCliente;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import com.runicsoft.bencolapp.utils.exceptions.ConflictException;
import com.runicsoft.bencolapp.utils.exceptions.ResourceNotFoundException;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.runicsoft.bencolapp.utils.constants.MessageConstants.*;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Override
    @Transactional(readOnly = true)
    public PaginaResponse<ClienteResponse> findAll(int pagina, int tamanio, String texto, EstadoGeneral estado, CategoriaCliente categoria) {
        validarPaginacion(pagina, tamanio);

        if (texto != null && texto.isBlank()) {
            texto = null;
        }

        Pageable pageable = PageRequest.of(
                pagina,
                tamanio,
                Sort.by("nombre").ascending()
        );

        Page<Cliente> clientes = clienteRepository.buscar(texto, estado, categoria, pageable);

        Page<ClienteResponse> responses = clientes.map(clienteMapper::convertirClienteDto);

        return PaginaResponse.from(responses);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }
        Cliente cliente = getCliente(id);
        return clienteMapper.convertirClienteDto(cliente);
    }

    @Override
    @Transactional
    public ClienteResponse create(ClienteRequest request) {
        if (clienteRepository.existsByTelefono(request.getTelefono())) {
            throw new ConflictException(TELEFONO_EXISTENTE);
        }
        if (clienteRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException(CORREO_EXISTENTE);
        }

        Cliente cliente = clienteMapper.convertirClienteEntidad(request);
        Cliente clienteGuardado = clienteRepository.save(cliente);
        return clienteMapper.convertirClienteDto(clienteGuardado);
    }

    @Override
    @Transactional
    public ClienteResponse update(Long id, ClienteRequest request) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        Cliente cliente = getCliente(id);

        if (clienteRepository.existsByTelefonoAndIdNot(request.getTelefono(), id)) {
            throw new ConflictException(TELEFONO_EXISTENTE);
        }
        if (clienteRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new ConflictException(CORREO_EXISTENTE);
        }

        clienteMapper.updateCliente(request, cliente);
        Cliente clienteActualizado = clienteRepository.save(cliente);
        return clienteMapper.convertirClienteDto(clienteActualizado);
    }

    // Metodos auxiliares
    private Cliente getCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CLIENTE_NO_ENCONTRADO));
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