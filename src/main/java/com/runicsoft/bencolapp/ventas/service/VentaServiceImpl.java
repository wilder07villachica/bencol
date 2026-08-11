package com.runicsoft.bencolapp.ventas.service;

import com.runicsoft.bencolapp.clientes.models.Cliente;
import com.runicsoft.bencolapp.clientes.repository.ClienteRepository;
import com.runicsoft.bencolapp.finanzas.models.CuentaCobrar;
import com.runicsoft.bencolapp.finanzas.repository.CuentaCobrarRepository;
import com.runicsoft.bencolapp.finanzas.utils.EstadoCuenta;
import com.runicsoft.bencolapp.inventario.models.Inventario;
import com.runicsoft.bencolapp.inventario.models.MovimientoInventario;
import com.runicsoft.bencolapp.inventario.repository.InventarioRepository;
import com.runicsoft.bencolapp.inventario.repository.MovimientoInventarioRepository;
import com.runicsoft.bencolapp.inventario.utils.TipoMovimientoInventario;
import com.runicsoft.bencolapp.precios_clientes.models.ClientePrecio;
import com.runicsoft.bencolapp.precios_clientes.repository.ClientePrecioRepository;
import com.runicsoft.bencolapp.productos.models.Producto;
import com.runicsoft.bencolapp.productos.repository.ProductoRepository;
import com.runicsoft.bencolapp.seguridad.utils.SecurityUtils;
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

    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final CuentaCobrarRepository cuentaCobrarRepository;

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
        validarStockVenta(request);

        Venta venta = new Venta();

        venta.setCodigo(generarCodigoVenta());
        venta.setCliente(cliente);

        List<DetalleVenta> detalles = new ArrayList<>();
        BigDecimal subtotalVenta = BigDecimal.ZERO;

        for (DetalleVentaRequest detalleRequest : request.getDetalles()) {
            Producto producto = getProducto(detalleRequest.getProductoId());
            validarProductoActivo(producto);
            BigDecimal precioUnitario = obtenerPrecioProducto(cliente.getId(), producto);
            BigDecimal subtotalDetalle = precioUnitario.multiply(BigDecimal.valueOf(detalleRequest.getCantidad()));

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
        venta.setCreadoPor(SecurityUtils.getUsuarioActual());

        Venta ventaGuardada = ventaRepository.save(venta);
        descontarInventarioVenta(ventaGuardada, request.getDetalles());
        crearCuentaCobrar(ventaGuardada);
        return ventaMapper.convertirVentaDto(ventaGuardada);
    }

    @Override
    @Transactional
    public VentaResponse anularVenta(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        Venta venta = getVenta(id);

        if (venta.getEstado() == EstadoVenta.ANULADA) {
            throw new IllegalArgumentException(VENTA_YA_ANULADA);
        }
        validarCuentaParaAnulacion(venta);
        devolverInventarioVenta(venta);
        anularCuentaCobrar(venta);
        venta.setEstado(EstadoVenta.ANULADA);
        venta.setActualizadoPor(SecurityUtils.getUsuarioActual());

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

    private void validarStockVenta(VentaRequest request) {
        for (DetalleVentaRequest detalle : request.getDetalles()) {
            Producto producto = getProducto(detalle.getProductoId());
            Inventario inventario = inventarioRepository
                    .findByProductoId(producto.getId())
                    .orElseThrow(() -> new IllegalArgumentException(INVENTARIO_NO_ENCONTRADO));

            Integer cantidadUnidades = calcularUnidadesFisicas(detalle.getCantidad(), producto);
            if (cantidadUnidades > inventario.getStockActual()) {
                throw new IllegalArgumentException(STOCK_INSUFICIENTE);
            }
        }
    }

    private void validarCuentaParaAnulacion(Venta venta) {
        CuentaCobrar cuenta = cuentaCobrarRepository
                .findByVentaId(venta.getId()).orElseThrow(
                        () -> new IllegalArgumentException(CUENTA_COBRAR_NO_ENCONTRADA)
                );
        if (cuenta.getMontoPagado().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException(VENTA_CON_PAGOS);
        }
    }

    private void anularCuentaCobrar(Venta venta) {
        CuentaCobrar cuenta = cuentaCobrarRepository
                .findByVentaId(venta.getId()).orElseThrow(
                        () -> new IllegalArgumentException(CUENTA_COBRAR_NO_ENCONTRADA)
                );

        cuenta.setEstado(EstadoCuenta.ANULADA);
        cuentaCobrarRepository.save(cuenta);
    }

    private Integer calcularUnidadesFisicas(Integer cantidadPaquetes, Producto producto) {
        return cantidadPaquetes * producto.getUnidadesPorPaquete();
    }

    private void descontarInventarioVenta(Venta venta, List<DetalleVentaRequest> detalles) {
        for (DetalleVentaRequest detalleRequest : detalles) {
            Producto producto = getProducto(detalleRequest.getProductoId());
            Inventario inventario = inventarioRepository.findByProductoId(producto.getId())
                    .orElseThrow(() -> new IllegalArgumentException(INVENTARIO_NO_ENCONTRADO));

            Integer cantidadUnidades = calcularUnidadesFisicas(detalleRequest.getCantidad(), producto);
            Integer stockAnterior = inventario.getStockActual();
            Integer stockNuevo = stockAnterior - cantidadUnidades;

            inventario.setStockActual(stockNuevo);
            inventarioRepository.save(inventario);
            registrarMovimientoVenta(venta, inventario, cantidadUnidades, stockAnterior, stockNuevo);
        }
    }

    private void registrarMovimientoVenta(Venta venta, Inventario inventario, Integer cantidad, Integer stockAnterior, Integer stockNuevo) {
        MovimientoInventario movimiento = new MovimientoInventario();

        movimiento.setProducto(inventario.getProducto());
        movimiento.setTipoMovimiento(TipoMovimientoInventario.SALIDA);
        movimiento.setCantidad(cantidad);
        movimiento.setStockAnterior(stockAnterior);
        movimiento.setStockNuevo(stockNuevo);
        movimiento.setReferencia("Venta " + venta.getCodigo());
        movimiento.setRegistradoPor(SecurityUtils.getUsuarioActual());

        movimientoInventarioRepository.save(movimiento);
    }

    private void devolverInventarioVenta(Venta venta) {
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            Inventario inventario = inventarioRepository.findByProductoId(producto.getId())
                    .orElseThrow(() -> new IllegalArgumentException(INVENTARIO_NO_ENCONTRADO));

            Integer cantidadUnidades = calcularUnidadesFisicas(detalle.getCantidad(), producto);
            Integer stockAnterior = inventario.getStockActual();
            Integer stockNuevo = stockAnterior + cantidadUnidades;

            validarStockMaximo(inventario, stockNuevo);
            inventario.setStockActual(stockNuevo);
            inventarioRepository.save(inventario);
            registrarMovimientoAnulacion(venta, inventario, cantidadUnidades, stockAnterior, stockNuevo);
        }
    }

    private void registrarMovimientoAnulacion(Venta venta, Inventario inventario, Integer cantidad, Integer stockAnterior, Integer stockNuevo) {
        MovimientoInventario movimiento = new MovimientoInventario();

        movimiento.setProducto(inventario.getProducto());
        movimiento.setTipoMovimiento(TipoMovimientoInventario.ENTRADA);
        movimiento.setCantidad(cantidad);
        movimiento.setStockAnterior(stockAnterior);
        movimiento.setStockNuevo(stockNuevo);
        movimiento.setReferencia("Anulación venta " + venta.getCodigo());
        movimiento.setRegistradoPor(SecurityUtils.getUsuarioActual());

        movimientoInventarioRepository.save(movimiento);
    }

    private void validarStockMaximo(Inventario inventario, Integer stockNuevo) {
        if (inventario.getStockMaximo() != null && stockNuevo > inventario.getStockMaximo()) {
            throw new IllegalArgumentException(STOCK_SUPERA_MAXIMO);
        }
    }

    private void crearCuentaCobrar(Venta venta) {
        CuentaCobrar cuenta = new CuentaCobrar();
        cuenta.setVenta(venta);
        cuenta.setMontoTotal(venta.getTotal());
        cuenta.setMontoPagado(BigDecimal.ZERO);
        cuenta.setSaldoPendiente(venta.getTotal());
        cuenta.setEstado(EstadoCuenta.PENDIENTE);
        cuentaCobrarRepository.save(cuenta);
    }

    private String generarCodigoVenta() {
        return "VEN-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}