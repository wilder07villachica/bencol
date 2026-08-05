package com.runicsoft.bencolapp.clientes.service;

import com.runicsoft.bencolapp.clientes.dtos.request.ClienteRequest;
import com.runicsoft.bencolapp.clientes.dtos.response.ClienteResponse;
import com.runicsoft.bencolapp.clientes.mappers.ClienteMapper;
import com.runicsoft.bencolapp.clientes.models.Cliente;
import com.runicsoft.bencolapp.clientes.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.runicsoft.bencolapp.utils.constants.MessageConstants.*;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> findAll() {
        List<Cliente> clientes = clienteRepository.findAll();
        return clienteMapper.convertirListaClienteDto(clientes);
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
            throw new IllegalArgumentException(TELEFONO_EXISTENTE);
        }
        if (clienteRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(CORREO_EXISTENTE);
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
            throw new IllegalArgumentException(TELEFONO_EXISTENTE);
        }
        if (clienteRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new IllegalArgumentException(CORREO_EXISTENTE);
        }

        clienteMapper.updateCliente(request, cliente);
        Cliente clienteActualizado = clienteRepository.save(cliente);
        return clienteMapper.convertirClienteDto(clienteActualizado);
    }

    // Métodos auxiliares
    private Cliente getCliente(Long id) {
        return clienteRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(CLIENTE_NO_ENCONTRADO)
        );
    }
}
