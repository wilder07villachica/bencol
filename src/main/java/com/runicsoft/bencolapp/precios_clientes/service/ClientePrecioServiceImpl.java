package com.runicsoft.bencolapp.precios_clientes.service;

import com.runicsoft.bencolapp.clientes.models.Cliente;
import com.runicsoft.bencolapp.clientes.repository.ClienteRepository;
import com.runicsoft.bencolapp.precios_clientes.dtos.request.ClientePrecioRequest;
import com.runicsoft.bencolapp.precios_clientes.dtos.response.ClientePrecioResponse;
import com.runicsoft.bencolapp.precios_clientes.mapper.ClientePrecioMapper;
import com.runicsoft.bencolapp.precios_clientes.models.ClientePrecio;
import com.runicsoft.bencolapp.precios_clientes.repository.ClientePrecioRepository;
import com.runicsoft.bencolapp.productos.models.Producto;
import com.runicsoft.bencolapp.productos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientePrecioServiceImpl implements ClientePrecioService{

    private final ClientePrecioRepository clientePrecioRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final ClientePrecioMapper clientePrecioMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ClientePrecioResponse> listarPreciosClientes() {
        List<ClientePrecio> precios = clientePrecioRepository.findAll();
        return clientePrecioMapper.convertirListaPrecioDto(precios);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientePrecioResponse buscarPrecioPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Valor inválido");
        }
        ClientePrecio clientePrecio = clientePrecioRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Precio no encontrado.")
        );
        return clientePrecioMapper.convertirDtoEntidad(clientePrecio);
    }

    @Override
    @Transactional
    public ClientePrecioResponse registrarNuevoPrecio(ClientePrecioRequest request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId()).orElseThrow(
                () -> new IllegalArgumentException("Cliente no encontrado")
        );
        Producto producto = productoRepository.findById(request.getProductoId()).orElseThrow(
                () -> new IllegalArgumentException("Producto no encontrado")
        );
        ClientePrecio precio = clientePrecioRepository.findByClientIdAndProductId(
                request.getClienteId(),
                request.getProductoId()
        ).orElse(new ClientePrecio());
        precio.setCliente(cliente);
        precio.setProducto(producto);
        precio.setPrecio(request.getPrecio());
        precio.setFechaActualizacion(LocalDateTime.now());

        ClientePrecio precioNuevo = clientePrecioRepository.save(precio);
        return clientePrecioMapper.convertirDtoEntidad(precioNuevo);
    }

    @Override
    @Transactional
    public ClientePrecioResponse actualizarInformacion(Long id, ClientePrecioRequest request) {
        ClientePrecio precio = clientePrecioRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Precio no encontrado")
        );
        Cliente cliente = clienteRepository.findById(request.getClienteId()).orElseThrow(
                () -> new IllegalArgumentException("Cliente no encontrado")
        );
        Producto producto = productoRepository.findById(request.getProductoId()).orElseThrow(
                () -> new IllegalArgumentException("Producto no encontrado")
        );
        precio.setCliente(cliente);
        precio.setProducto(producto);
        precio.setPrecio(request.getPrecio());
        precio.setFechaActualizacion(LocalDateTime.now());
        ClientePrecio actualizado = clientePrecioRepository.save(precio);
        return clientePrecioMapper.convertirDtoEntidad(actualizado);
    }

    @Override
    @Transactional
    public void deleteClientePrecio(Long id) {
        ClientePrecio precio = clientePrecioRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Precio no encontrado")
        );
        clientePrecioRepository.delete(precio);
    }
}
