package com.runicsoft.bencolapp.ventas.service;

import com.runicsoft.bencolapp.clientes.models.Cliente;
import com.runicsoft.bencolapp.clientes.repository.ClienteRepository;
import com.runicsoft.bencolapp.precios_clientes.models.ClientePrecio;
import com.runicsoft.bencolapp.precios_clientes.repository.ClientePrecioRepository;
import com.runicsoft.bencolapp.productos.models.Producto;
import com.runicsoft.bencolapp.productos.repository.ProductoRepository;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import com.runicsoft.bencolapp.ventas.dtos.request.DetalleVentaRequest;
import com.runicsoft.bencolapp.ventas.dtos.request.VentaRequest;
import com.runicsoft.bencolapp.ventas.dtos.response.VentaResponse;
import com.runicsoft.bencolapp.ventas.mapper.VentaMapper;
import com.runicsoft.bencolapp.ventas.models.DetalleVenta;
import com.runicsoft.bencolapp.ventas.models.Venta;
import com.runicsoft.bencolapp.ventas.repository.VentaRepository;
import com.runicsoft.bencolapp.ventas.utils.EstadoVenta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static com.runicsoft.bencolapp.utils.constants.MessageConstants.*;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final ClientePrecioRepository clientePrecioRepository;
    private final VentaMapper ventaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponse> findAll() {
        List<Venta> ventas = ventaRepository.findAll();
        return ventaMapper.convertirListaVentaDto(ventas);
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponse findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        Venta venta = getVenta(id);
        return ventaMapper.convertirVentaDto(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponse findByCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(CODIGO_INVALIDO);
        }

        Venta venta = ventaRepository.findByCodigo(codigo)
                .orElseThrow(
                        () -> new IllegalArgumentException(VENTA_NO_ENCONTRADA)
                );

        return ventaMapper.convertirVentaDto(venta);
    }

    @Override
    @Transactional
    public VentaResponse create(VentaRequest request) {
        Cliente cliente = getCliente(request.getClienteId());
        validarClienteActivo(cliente);
        validarProductosDuplicados(request.getDetalles());

        Venta venta = new Venta();

        venta.setCodigo(generarCodigoVenta());
        venta.setCliente(cliente);

        List<DetalleVenta> detalles = new ArrayList<>();
        BigDecimal subtotalVenta = BigDecimal.ZERO;

        for (DetalleVentaRequest detalleRequest : request.getDetalles()) {
            Producto producto = getProducto(detalleRequest.getProductoId());
            validarProductoActivo(producto);

            BigDecimal precioUnitario = obtenerPrecioProducto(
                    cliente.getId(),
                    producto
            );

            BigDecimal subtotalDetalle = precioUnitario.multiply(
                    BigDecimal.valueOf(detalleRequest.getCantidad())
            );

            DetalleVenta detalle = new DetalleVenta();

            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleRequest.getCantidad());
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setSubtotal(subtotalDetalle);

            detalles.add(detalle);
            subtotalVenta = subtotalVenta.add(subtotalDetalle);
        }

        venta.setDetalles(detalles);
        venta.setSubtotal(subtotalVenta);
        venta.setTotal(subtotalVenta);

        Venta ventaGuardada = ventaRepository.save(venta);
        return ventaMapper.convertirVentaDto(ventaGuardada);
    }

    @Override
    public VentaResponse anularVenta(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        Venta venta = getVenta(id);

        if (venta.getEstado() == EstadoVenta.ANULADA) {
            throw new IllegalArgumentException(VENTA_YA_ANULADA);
        }

        venta.setEstado(EstadoVenta.ANULADA);
        Venta ventaActualizada = ventaRepository.save(venta);
        return ventaMapper.convertirVentaDto(ventaActualizada);
    }

    // Métodos auxiliares
    private Venta getVenta(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(VENTA_NO_ENCONTRADA)
                );
    }

    private Cliente getCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(CLIENTE_NO_ENCONTRADO)
                );
    }

    private Producto getProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(PRODUCTO_NO_ENCONTRADO)
                );
    }

    private BigDecimal obtenerPrecioProducto(Long clienteId, Producto producto) {
        return clientePrecioRepository
                .findByClienteIdAndProductoId(
                        clienteId,
                        producto.getId()
                )
                .map(ClientePrecio::getPrecio)
                .orElse(producto.getPrecioBase());
    }

    private void validarClienteActivo(Cliente cliente) {
        if (cliente.getEstado() != EstadoGeneral.ACTIVO) {
            throw new IllegalArgumentException(CLIENTE_INACTIVO);
        }
    }

    private void validarProductoActivo(Producto producto) {
        if (producto.getEstado() != EstadoGeneral.ACTIVO) {
            throw new IllegalArgumentException(PRODUCTO_INACTIVO);
        }
    }

    private void validarProductosDuplicados(List<DetalleVentaRequest> detalles) {
        Set<Long> productosIds = new HashSet<>();
        for (DetalleVentaRequest detalle : detalles) {
            if (!productosIds.add(detalle.getProductoId())) {
                throw new IllegalArgumentException(PRODUCTO_DUPLICADO_VENTA);
            }
        }
    }

    private String generarCodigoVenta() {
        return "VEN-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}