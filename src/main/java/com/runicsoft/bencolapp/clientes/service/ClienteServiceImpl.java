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

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> listarClientes() {
        List<Cliente> clientes = clienteRepository.findAll();
        return clienteMapper.convertirListaClienteDto(clientes);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse buscarClientePorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Valor inválido");
        }
        Cliente cliente = clienteRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Cliente no encontrado")
        );
        return clienteMapper.convertirClienteDto(cliente);
    }

    @Override
    @Transactional
    public ClienteResponse registrarCliente(ClienteRequest request) {
        Cliente cliente = clienteMapper.convertirClienteEntidad(request);
        clienteRepository.save(cliente);
        return clienteMapper.convertirClienteDto(cliente);
    }

    @Override
    @Transactional
    public ClienteResponse actualizarCliente(Long id, ClienteRequest request) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Valor inválido");
        }
        Cliente cliente = clienteRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Cliente no encontrado")
        );
        cliente.setNombre(request.getNombre());
        cliente.setDireccion(request.getDireccion());
        cliente.setTelefono(request.getTelefono());
        cliente.setEmail(request.getEmail());
        cliente.setCategoria(request.getCategoria());
        cliente.setEstado(request.getEstado());
        clienteRepository.save(cliente);
        return clienteMapper.convertirClienteDto(cliente);
    }
}
