package com.runicsoft.bencolapp.ventas.service;

import com.runicsoft.bencolapp.clientes.models.Cliente;
import com.runicsoft.bencolapp.clientes.repository.ClienteRepository;
import com.runicsoft.bencolapp.envases.dtos.request.MovimientoEnvaseRequest;
import com.runicsoft.bencolapp.envases.service.EnvaseService;
import com.runicsoft.bencolapp.envases.utils.TipoMovimientoEnvase;
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
import com.runicsoft.bencolapp.productos.utils.ProductoCategoria;
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

    private final EnvaseService envaseService;

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

            BigDecimal precioUnitario = obtenerPrecioProducto(
                    cliente.getId(),
                    producto,
                    detalleRequest
            );
            BigDecimal subtotalDetalle = precioUnitario.multiply(BigDecimal.valueOf(detalleRequest.getCantidad()));

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleRequest.getCantidad());
            detalle.setEnvasesDevueltos(detalleRequest.getEnvasesDevueltos());
            detalle.setModalidadEnvase(detalleRequest.getModalidadEnvase());
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
        procesarEnvasesVenta(ventaGuardada, request.getDetalles());
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
        revertirEnvasesVenta(venta);
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

    private BigDecimal obtenerPrecioProducto(Long clienteId, Producto producto, DetalleVentaRequest detalle) {
        /*
         * COMPRA DE BIDÓN
         *
         * Por defecto se utiliza precioBase.
         *
         * Ejemplo:
         * precioBase = 20
         *
         * Excepcionalmente ADMIN puede establecer
         * un precio menor para esa venta.
         */
        if (producto.getCategoria() == ProductoCategoria.BIDON && detalle.getModalidadEnvase() == TipoMovimientoEnvase.COMPRA) {
            BigDecimal precioBase = producto.getPrecioBase();

            if (detalle.getPrecioManual() == null) {
                return precioBase;
            }

            validarPrecioManualCompra(detalle.getPrecioManual(), precioBase);
            return detalle.getPrecioManual();
        }

        /*
         * Fuera de una COMPRA de bidones no permitimos que
         * este campo altere silenciosamente el precio.
         */
        if (detalle.getPrecioManual() != null) {
            throw new BusinessException("El precio manual solo puede utilizarse en la compra de bidones.");
        }

        /*
         * RECARGA / INTERCAMBIO / PRÉSTAMO
         *
         * Precio especial del cliente si existe.
         * De lo contrario precioBase.
         */
        return clientePrecioRepository
                .findByClienteIdAndProductoId(clienteId, producto.getId())
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

            if (producto.getCategoria() == ProductoCategoria.BIDON) {
                descontarInventarioBidon(
                        venta,
                        producto,
                        detalleRequest
                );
                continue;
            }

            Inventario inventario = inventarioRepository
                    .findByProductoIdForUpdate(producto.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(INVENTARIO_NO_ENCONTRADO)
                    );

            Integer cantidadUnidades = calcularUnidadesFisicas(
                    detalleRequest.getCantidad(),
                    producto
            );

            Integer stockAnterior = inventario.getStockActual();

            if (cantidadUnidades > stockAnterior) {
                throw new BusinessException(STOCK_INSUFICIENTE);
            }

            Integer stockNuevo = stockAnterior - cantidadUnidades;

            inventario.setStockActual(stockNuevo);
            inventarioRepository.save(inventario);

            registrarMovimientoVenta(
                    venta,
                    inventario,
                    cantidadUnidades,
                    stockAnterior,
                    stockNuevo
            );
        }
    }

    private void descontarInventarioBidon(Venta venta, Producto producto, DetalleVentaRequest detalle) {
        if (detalle.getModalidadEnvase() == null) {
            throw new BusinessException(
                    "La modalidad de envase es obligatoria para productos BIDON."
            );
        }

        Integer envasesDevueltos = detalle.getEnvasesDevueltos() != null
                ? detalle.getEnvasesDevueltos()
                : 0;

        validarEnvasesVenta(detalle, envasesDevueltos);

        if (detalle.getModalidadEnvase() == TipoMovimientoEnvase.INTERCAMBIO) {
            return;
        }

        if (detalle.getModalidadEnvase() != TipoMovimientoEnvase.PRESTAMO &&
                detalle.getModalidadEnvase() != TipoMovimientoEnvase.COMPRA) {

            throw new BusinessException(
                    "Modalidad de envase inválida para la venta."
            );
        }

        Inventario inventario = inventarioRepository
                .findByProductoIdForUpdate(producto.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(INVENTARIO_NO_ENCONTRADO)
                );

        Integer stockAnterior = inventario.getStockActual();
        Integer cantidad = detalle.getCantidad();

        if (cantidad > stockAnterior) {
            throw new BusinessException(STOCK_INSUFICIENTE);
        }

        Integer stockNuevo = stockAnterior - cantidad;

        inventario.setStockActual(stockNuevo);
        inventarioRepository.save(inventario);

        registrarMovimientoVenta(
                venta,
                inventario,
                cantidad,
                stockAnterior,
                stockNuevo
        );
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

            if (producto.getCategoria() == ProductoCategoria.BIDON) {
                devolverInventarioBidon(venta, detalle);
                continue;
            }

            Inventario inventario = inventarioRepository
                    .findByProductoIdForUpdate(producto.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(INVENTARIO_NO_ENCONTRADO)
                    );

            Integer cantidadUnidades = calcularUnidadesFisicas(
                    detalle.getCantidad(),
                    producto
            );

            Integer stockAnterior = inventario.getStockActual();
            Integer stockNuevo = stockAnterior + cantidadUnidades;

            validarStockMaximo(inventario, stockNuevo);

            inventario.setStockActual(stockNuevo);
            inventarioRepository.save(inventario);

            registrarMovimientoAnulacion(
                    venta,
                    inventario,
                    cantidadUnidades,
                    stockAnterior,
                    stockNuevo
            );
        }
    }

    private void devolverInventarioBidon(Venta venta, DetalleVenta detalle) {
        /*
         * Un INTERCAMBIO nunca redujo el inventario,
         * por lo tanto tampoco tenemos nada que devolver.
         */
        if (detalle.getModalidadEnvase() == TipoMovimientoEnvase.INTERCAMBIO) {
            return;
        }

        if (detalle.getModalidadEnvase() != TipoMovimientoEnvase.PRESTAMO &&
                detalle.getModalidadEnvase() != TipoMovimientoEnvase.COMPRA) {
            return;
        }

        Inventario inventario = inventarioRepository
                .findByProductoIdForUpdate(detalle.getProducto().getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(INVENTARIO_NO_ENCONTRADO)
                );

        Integer stockAnterior = inventario.getStockActual();
        Integer stockNuevo = stockAnterior + detalle.getCantidad();

        validarStockMaximo(inventario, stockNuevo);

        inventario.setStockActual(stockNuevo);
        inventarioRepository.save(inventario);

        registrarMovimientoAnulacion(
                venta,
                inventario,
                detalle.getCantidad(),
                stockAnterior,
                stockNuevo
        );
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

    private void procesarEnvasesVenta(
            Venta venta,
            List<DetalleVentaRequest> detalles
    ) {
        for (DetalleVentaRequest detalle : detalles) {
            Producto producto = getProducto(detalle.getProductoId());

            if (producto.getCategoria() != ProductoCategoria.BIDON) {
                continue;
            }

            MovimientoEnvaseRequest movimiento = new MovimientoEnvaseRequest();
            movimiento.setClienteId(venta.getCliente().getId());
            movimiento.setProductoId(producto.getId());
            movimiento.setTipoMovimiento(detalle.getModalidadEnvase());
            movimiento.setCantidad(detalle.getCantidad());
            movimiento.setReferencia("Venta " + venta.getCodigo());

            envaseService.registrarMovimiento(movimiento);
        }
    }

    private void validarEnvasesVenta(DetalleVentaRequest detalle, Integer envasesDevueltos) {
        if (envasesDevueltos < 0) {
            throw new BusinessException("La cantidad de envases devueltos no puede ser negativa.");
        }

        if (envasesDevueltos > detalle.getCantidad()) {
            throw new BusinessException("Los envases devueltos no pueden superar la cantidad vendida.");
        }

        if (detalle.getModalidadEnvase() == TipoMovimientoEnvase.INTERCAMBIO &&
                !envasesDevueltos.equals(detalle.getCantidad())) {
            throw new BusinessException(
                    "En un intercambio, el cliente debe devolver la misma cantidad de envases que recibe."
            );
        }

        if ((detalle.getModalidadEnvase() == TipoMovimientoEnvase.PRESTAMO ||
                detalle.getModalidadEnvase() == TipoMovimientoEnvase.COMPRA) &&
                envasesDevueltos > 0) {
            throw new BusinessException(
                    "Un préstamo o compra de envases no debe registrar envases devueltos."
            );
        }

        if (detalle.getModalidadEnvase() == TipoMovimientoEnvase.DEVOLUCION ||
                detalle.getModalidadEnvase() == TipoMovimientoEnvase.CONVERSION_COMPRA ||
                detalle.getModalidadEnvase() == TipoMovimientoEnvase.AJUSTE) {
            throw new BusinessException(
                    "Esta modalidad de envase no puede registrarse directamente desde una venta."
            );
        }
    }

    private void revertirEnvasesVenta(Venta venta) {
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();

            if (producto.getCategoria() != ProductoCategoria.BIDON) {
                continue;
            }

            if (detalle.getModalidadEnvase() == null) {
                continue;
            }

            envaseService.revertirMovimientoVenta(
                    venta.getCliente().getId(),
                    producto.getId(),
                    detalle.getModalidadEnvase(),
                    detalle.getCantidad(),
                    "Anulación venta " + venta.getCodigo()
            );
        }
    }

    private void validarPrecioManualCompra(BigDecimal precioManual, BigDecimal precioBase) {
        if (!SecurityUtils.esAdmin()) {
            throw new BusinessException(
                    "Solo un administrador puede modificar el precio de compra de un bidón."
            );
        }

        if (precioManual.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(
                    "El precio de compra debe ser mayor a cero."
            );
        }

        if (precioManual.compareTo(precioBase) > 0) {
            throw new BusinessException(
                    "El precio manual no puede superar el precio base del bidón."
            );
        }
    }
}