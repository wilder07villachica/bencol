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
import com.runicsoft.bencolapp.utils.exceptions.BusinessException;
import com.runicsoft.bencolapp.utils.exceptions.ResourceNotFoundException;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;
import com.runicsoft.bencolapp.ventas.dtos.request.DetalleVentaRequest;
import com.runicsoft.bencolapp.ventas.dtos.request.VentaRequest;
import com.runicsoft.bencolapp.ventas.dtos.response.VentaResponse;
import com.runicsoft.bencolapp.ventas.mapper.VentaMapper;
import com.runicsoft.bencolapp.ventas.models.DetalleVenta;
import com.runicsoft.bencolapp.ventas.models.Venta;
import com.runicsoft.bencolapp.ventas.repository.VentaRepository;
import com.runicsoft.bencolapp.ventas.utils.EstadoVenta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.runicsoft.bencolapp.utils.constants.MessageConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
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
    public PaginaResponse<VentaResponse> findAll(int pagina, int tamanio, String codigo, Long clienteId, EstadoVenta estado, LocalDate desde, LocalDate hasta) {
        validarPaginacion(pagina, tamanio);
        validarRangoFechas(desde, hasta);

        if (codigo != null && codigo.isBlank()) {
            codigo = null;
        }

        if (clienteId != null) {
            if (clienteId <= 0) {
                throw new IllegalArgumentException(ID_INVALIDO);
            }
            getCliente(clienteId);
        }

        LocalDateTime fechaInicio = desde != null ? desde.atStartOfDay() : null;
        LocalDateTime fechaFin = hasta != null ? hasta.plusDays(1).atStartOfDay() : null;

        Pageable pageable = PageRequest.of(
                pagina,
                tamanio,
                Sort.by("fechaCreacion").descending()
        );

        Page<Venta> ventas = ventaRepository.buscar(
                codigo,
                clienteId,
                estado,
                fechaInicio,
                fechaFin,
                pageable
        );

        Page<VentaResponse> responses = ventas.map(ventaMapper::convertirVentaDto);
        return PaginaResponse.from(responses);
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
                .orElseThrow(() -> new ResourceNotFoundException(VENTA_NO_ENCONTRADA));

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

        log.info(
                "Venta creada. id={}, codigo={}, clienteId={}, total={}, usuario={}",
                ventaGuardada.getId(),
                ventaGuardada.getCodigo(),
                cliente.getId(),
                ventaGuardada.getTotal(),
                SecurityUtils.getUsuarioActual()
        );

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
            throw new BusinessException(VENTA_YA_ANULADA);
        }

        validarCuentaParaAnulacion(venta);
        devolverInventarioVenta(venta);
        anularCuentaCobrar(venta);

        venta.setEstado(EstadoVenta.ANULADA);
        venta.setActualizadoPor(SecurityUtils.getUsuarioActual());

        Venta ventaActualizada = ventaRepository.save(venta);

        log.info(
                "Venta anulada. id={}, codigo={}, usuario={}",
                ventaActualizada.getId(),
                ventaActualizada.getCodigo(),
                SecurityUtils.getUsuarioActual()
        );

        return ventaMapper.convertirVentaDto(ventaActualizada);
    }

    // Metodos auxiliares
    private Venta getVenta(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(VENTA_NO_ENCONTRADA));
    }

    private Cliente getCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CLIENTE_NO_ENCONTRADO));
    }

    private Producto getProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCTO_NO_ENCONTRADO));
    }

    private BigDecimal obtenerPrecioProducto(Long clienteId, Producto producto) {
        return clientePrecioRepository.findByClienteIdAndProductoId(clienteId, producto.getId())
                .map(ClientePrecio::getPrecio)
                .orElse(producto.getPrecioBase());
    }

    private void validarClienteActivo(Cliente cliente) {
        if (cliente.getEstado() != EstadoGeneral.ACTIVO) {
            throw new BusinessException(CLIENTE_INACTIVO);
        }
    }

    private void validarProductoActivo(Producto producto) {
        if (producto.getEstado() != EstadoGeneral.ACTIVO) {
            throw new BusinessException(PRODUCTO_INACTIVO);
        }
    }

    private void validarProductosDuplicados(List<DetalleVentaRequest> detalles) {
        Set<Long> productosIds = new HashSet<>();

        for (DetalleVentaRequest detalle : detalles) {
            if (!productosIds.add(detalle.getProductoId())) {
                throw new BusinessException(PRODUCTO_DUPLICADO_VENTA);
            }
        }
    }

    private void validarCuentaParaAnulacion(Venta venta) {
        CuentaCobrar cuenta = cuentaCobrarRepository.findByVentaId(venta.getId())
                .orElseThrow(() -> new ResourceNotFoundException(CUENTA_COBRAR_NO_ENCONTRADA));

        if (cuenta.getMontoPagado().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException(VENTA_CON_PAGOS);
        }
    }

    private void anularCuentaCobrar(Venta venta) {
        CuentaCobrar cuenta = cuentaCobrarRepository.findByVentaId(venta.getId())
                .orElseThrow(() -> new ResourceNotFoundException(CUENTA_COBRAR_NO_ENCONTRADA));

        cuenta.setEstado(EstadoCuenta.ANULADA);
        cuentaCobrarRepository.save(cuenta);
    }

    private Integer calcularUnidadesFisicas(Integer cantidadPaquetes, Producto producto) {
        return cantidadPaquetes * producto.getUnidadesPorPaquete();
    }

    private void descontarInventarioVenta(Venta venta, List<DetalleVentaRequest> detalles) {
        for (DetalleVentaRequest detalleRequest : detalles) {
            Producto producto = getProducto(detalleRequest.getProductoId());

            Inventario inventario = inventarioRepository.findByProductoIdForUpdate(producto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(INVENTARIO_NO_ENCONTRADO));

            Integer cantidadUnidades = calcularUnidadesFisicas(detalleRequest.getCantidad(), producto);

            Integer stockAnterior = inventario.getStockActual();

            if (cantidadUnidades > stockAnterior) {
                throw new BusinessException(STOCK_INSUFICIENTE);
            }

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

            Inventario inventario = inventarioRepository.findByProductoIdForUpdate(producto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(INVENTARIO_NO_ENCONTRADO));

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
            throw new BusinessException(STOCK_SUPERA_MAXIMO);
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

    private void validarPaginacion(int pagina, int tamanio) {
        if (pagina < 0) {
            throw new IllegalArgumentException(PAGINA_INVALIDA);
        }

        if (tamanio <= 0 || tamanio > 100) {
            throw new IllegalArgumentException(TAMANIO_PAGINA_INVALIDO);
        }
    }

    private void validarRangoFechas(LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new IllegalArgumentException(RANGO_FECHAS_INVALIDO);
        }
    }
}