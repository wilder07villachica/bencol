package com.runicsoft.bencolapp.compras.service;

import com.runicsoft.bencolapp.compras.dtos.request.CompraRequest;
import com.runicsoft.bencolapp.compras.dtos.request.DetalleCompraRequest;
import com.runicsoft.bencolapp.compras.dtos.response.CompraResponse;
import com.runicsoft.bencolapp.compras.mapper.CompraMapper;
import com.runicsoft.bencolapp.compras.models.Compra;
import com.runicsoft.bencolapp.compras.models.DetalleCompra;
import com.runicsoft.bencolapp.compras.repository.CompraRepository;
import com.runicsoft.bencolapp.finanzas.models.CuentaPagar;
import com.runicsoft.bencolapp.finanzas.repository.CuentaPagarRepository;
import com.runicsoft.bencolapp.finanzas.utils.EstadoCuentaPagar;
import com.runicsoft.bencolapp.inventario.models.Inventario;
import com.runicsoft.bencolapp.inventario.models.MovimientoInventario;
import com.runicsoft.bencolapp.inventario.repository.InventarioRepository;
import com.runicsoft.bencolapp.inventario.repository.MovimientoInventarioRepository;
import com.runicsoft.bencolapp.inventario.utils.TipoMovimientoInventario;
import com.runicsoft.bencolapp.productos.models.Producto;
import com.runicsoft.bencolapp.productos.repository.ProductoRepository;
import com.runicsoft.bencolapp.proveedores.models.Proveedor;
import com.runicsoft.bencolapp.proveedores.repository.ProveedorRepository;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.runicsoft.bencolapp.utils.constants.MessageConstants.*;

@Service
@RequiredArgsConstructor
public class CompraServiceImpl implements CompraService {

    private final CompraRepository compraRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;

    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;

    private final CompraMapper compraMapper;
    private final CuentaPagarRepository cuentaPagarRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CompraResponse> findAll() {
        List<Compra> compras = compraRepository.findAll();
        return compraMapper.convertirListaCompraDto(compras);
    }

    @Override
    @Transactional(readOnly = true)
    public CompraResponse findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }
        Compra compra = getCompra(id);
        return compraMapper.convertirCompraDto(compra);
    }

    @Override
    @Transactional(readOnly = true)
    public CompraResponse findByCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(CODIGO_INVALIDO);
        }

        Compra compra = compraRepository.findByCodigo(codigo)
                .orElseThrow(
                        () -> new IllegalArgumentException(COMPRA_NO_ENCONTRADA)
                );
        return compraMapper.convertirCompraDto(compra);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompraResponse> findByProveedorId(Long proveedorId) {
        if (proveedorId == null || proveedorId <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }
        getProveedor(proveedorId);
        List<Compra> compras = compraRepository.findByProveedorId(proveedorId);
        return compraMapper.convertirListaCompraDto(compras);
    }

    @Override
    @Transactional
    public CompraResponse create(CompraRequest request) {
        Proveedor proveedor = getProveedor(request.getProveedorId());
        validarProveedorActivo(proveedor);
        validarProductosDuplicados(request.getDetalles());
        Compra compra = new Compra();
        compra.setCodigo(generarCodigoCompra());
        compra.setProveedor(proveedor);
        List<DetalleCompra> detalles = new ArrayList<>();

        BigDecimal subtotalCompra = BigDecimal.ZERO;

        for (DetalleCompraRequest detalleRequest : request.getDetalles()) {
            Producto producto = getProducto(detalleRequest.getProductoId());
            validarProductoActivo(producto);
            BigDecimal subtotalDetalle = detalleRequest.getCostoUnitario()
                    .multiply(
                            BigDecimal.valueOf(detalleRequest.getCantidad())
                    );

            DetalleCompra detalle = new DetalleCompra();
            detalle.setCompra(compra);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleRequest.getCantidad());
            detalle.setCostoUnitario(detalleRequest.getCostoUnitario());
            detalle.setSubtotal(subtotalDetalle);
            detalles.add(detalle);
            subtotalCompra = subtotalCompra.add(subtotalDetalle);
        }

        compra.setDetalles(detalles);
        compra.setSubtotal(subtotalCompra);
        compra.setTotal(subtotalCompra);

        Compra compraGuardada = compraRepository.save(compra);
        ingresarInventarioCompra(compraGuardada);
        crearCuentaPagar(compraGuardada);
        return compraMapper.convertirCompraDto(compraGuardada);
    }

    // Métodos auxiliares
    private Compra getCompra(Long id) {
        return compraRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(COMPRA_NO_ENCONTRADA)
                );
    }

    private Proveedor getProveedor(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(PROVEEDOR_NO_ENCONTRADO)
                );
    }

    private Producto getProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(PRODUCTO_NO_ENCONTRADO)
                );
    }

    private void validarProveedorActivo(Proveedor proveedor) {
        if (proveedor.getEstado() != EstadoGeneral.ACTIVO) {
            throw new IllegalArgumentException(PROVEEDOR_INACTIVO);
        }
    }

    private void validarProductoActivo(Producto producto) {
        if (producto.getEstado() != EstadoGeneral.ACTIVO) {
            throw new IllegalArgumentException(PRODUCTO_INACTIVO);
        }
    }

    private void validarProductosDuplicados(List<DetalleCompraRequest> detalles) {
        Set<Long> productosIds = new HashSet<>();
        for (DetalleCompraRequest detalle : detalles) {
            if (!productosIds.add(detalle.getProductoId())) {
                throw new IllegalArgumentException(PRODUCTO_DUPLICADO_COMPRA);
            }
        }
    }

    private String generarCodigoCompra() {
        return "COM-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();
    }

    private void ingresarInventarioCompra(Compra compra) {
        for (DetalleCompra detalle : compra.getDetalles()) {
            Producto producto = detalle.getProducto();
            Inventario inventario = inventarioRepository
                    .findByProductoId(producto.getId()).orElseThrow(
                            () -> new IllegalArgumentException(INVENTARIO_NO_ENCONTRADO)
                    );
            Integer stockAnterior = inventario.getStockActual();
            Integer stockNuevo = stockAnterior + detalle.getCantidad();
            validarStockMaximo(inventario, stockNuevo);
            inventario.setStockActual(stockNuevo);
            inventarioRepository.save(inventario);
            registrarMovimientoCompra(
                    compra,
                    inventario,
                    detalle.getCantidad(),
                    stockAnterior,
                    stockNuevo
            );
        }
    }

    private void registrarMovimientoCompra(Compra compra, Inventario inventario, Integer cantidad, Integer stockAnterior, Integer stockNuevo) {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(inventario.getProducto());
        movimiento.setTipoMovimiento(TipoMovimientoInventario.ENTRADA);
        movimiento.setCantidad(cantidad);
        movimiento.setStockAnterior(stockAnterior);
        movimiento.setStockNuevo(stockNuevo);
        movimiento.setReferencia("Compra " + compra.getCodigo());
        movimientoInventarioRepository.save(movimiento);
    }

    private void crearCuentaPagar(Compra compra) {
        CuentaPagar cuenta = new CuentaPagar();
        cuenta.setCompra(compra);
        cuenta.setMontoTotal(compra.getTotal());
        cuenta.setMontoPagado(BigDecimal.ZERO);
        cuenta.setSaldoPendiente(compra.getTotal());
        cuenta.setEstado(EstadoCuentaPagar.PENDIENTE);
        cuentaPagarRepository.save(cuenta);
    }

    private void validarStockMaximo(Inventario inventario, Integer stockNuevo) {
        if (inventario.getStockMaximo() != null && stockNuevo > inventario.getStockMaximo()) {
            throw new IllegalArgumentException(STOCK_SUPERA_MAXIMO);
        }
    }
}