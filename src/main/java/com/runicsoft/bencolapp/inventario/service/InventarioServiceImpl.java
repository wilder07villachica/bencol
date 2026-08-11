package com.runicsoft.bencolapp.inventario.service;

import com.runicsoft.bencolapp.inventario.dtos.request.InventarioRequest;
import com.runicsoft.bencolapp.inventario.dtos.request.MovimientoInventarioRequest;
import com.runicsoft.bencolapp.inventario.dtos.response.InventarioResponse;
import com.runicsoft.bencolapp.inventario.dtos.response.MovimientoInventarioResponse;
import com.runicsoft.bencolapp.inventario.mapper.InventarioMapper;
import com.runicsoft.bencolapp.inventario.mapper.MovimientoInventarioMapper;
import com.runicsoft.bencolapp.inventario.models.Inventario;
import com.runicsoft.bencolapp.inventario.models.MovimientoInventario;
import com.runicsoft.bencolapp.inventario.repository.InventarioRepository;
import com.runicsoft.bencolapp.inventario.repository.MovimientoInventarioRepository;
import com.runicsoft.bencolapp.inventario.utils.TipoMovimientoInventario;
import com.runicsoft.bencolapp.productos.models.Producto;
import com.runicsoft.bencolapp.productos.repository.ProductoRepository;
import com.runicsoft.bencolapp.seguridad.utils.SecurityUtils;
import com.runicsoft.bencolapp.utils.exceptions.BusinessException;
import com.runicsoft.bencolapp.utils.exceptions.ConflictException;
import com.runicsoft.bencolapp.utils.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.runicsoft.bencolapp.utils.constants.MessageConstants.*;

@Service
@RequiredArgsConstructor
public class InventarioServiceImpl implements InventarioService {

    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final ProductoRepository productoRepository;
    private final InventarioMapper inventarioMapper;
    private final MovimientoInventarioMapper movimientoInventarioMapper;

    @Override
    @Transactional(readOnly = true)
    public List<InventarioResponse> findAll() {
        List<Inventario> inventarios = inventarioRepository.findAll();
        return inventarioMapper.convertirListaInventarioDto(inventarios);
    }

    @Override
    @Transactional(readOnly = true)
    public InventarioResponse findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        Inventario inventario = getInventario(id);
        return inventarioMapper.convertirInventarioDto(inventario);
    }

    @Override
    @Transactional(readOnly = true)
    public InventarioResponse findByProductoId(Long productoId) {
        if (productoId == null || productoId <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        Inventario inventario = getInventarioByProducto(productoId);
        return inventarioMapper.convertirInventarioDto(inventario);
    }

    @Override
    @Transactional
    public InventarioResponse create(InventarioRequest request) {
        Producto producto = getProducto(request.getProductoId());
        validarInventarioExistente(producto.getId());

        validarLimitesStock(
                request.getStockActual(),
                request.getStockMinimo(),
                request.getStockMaximo()
        );

        Inventario inventario = new Inventario();
        inventario.setProducto(producto);
        inventario.setStockActual(request.getStockActual());
        inventario.setStockMinimo(request.getStockMinimo());
        inventario.setStockMaximo(request.getStockMaximo());

        Inventario inventarioGuardado = inventarioRepository.save(inventario);
        return inventarioMapper.convertirInventarioDto(inventarioGuardado);
    }

    @Override
    @Transactional
    public MovimientoInventarioResponse registrarMovimiento(MovimientoInventarioRequest request) {
        Producto producto = getProducto(request.getProductoId());
        Inventario inventario = getInventarioByProducto(producto.getId());
        Integer stockAnterior = inventario.getStockActual();

        Integer stockNuevo = calcularStockNuevo(
                inventario,
                request.getTipoMovimiento(),
                request.getCantidad()
        );

        inventario.setStockActual(stockNuevo);
        inventarioRepository.save(inventario);

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setTipoMovimiento(request.getTipoMovimiento());
        movimiento.setCantidad(request.getCantidad());
        movimiento.setStockAnterior(stockAnterior);
        movimiento.setStockNuevo(stockNuevo);
        movimiento.setReferencia(request.getReferencia());
        movimiento.setRegistradoPor(SecurityUtils.getUsuarioActual());

        MovimientoInventario movimientoGuardado = movimientoInventarioRepository.save(movimiento);
        return movimientoInventarioMapper.convertirMovimientoDto(movimientoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventarioResponse> findMovimientosByProductoId(Long productoId) {
        if (productoId == null || productoId <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        getProducto(productoId);

        List<MovimientoInventario> movimientos = movimientoInventarioRepository.findByProductoId(productoId);
        return movimientoInventarioMapper.convertirListaMovimientoDto(movimientos);
    }

    // Metodos auxiliares
    private Inventario getInventario(Long id) {
        return inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(INVENTARIO_NO_ENCONTRADO));
    }

    private Inventario getInventarioByProducto(Long productoId) {
        return inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new ResourceNotFoundException(INVENTARIO_NO_ENCONTRADO));
    }

    private Producto getProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCTO_NO_ENCONTRADO));
    }

    private void validarInventarioExistente(Long productoId) {
        if (inventarioRepository.existsByProductoId(productoId)) {
            throw new ConflictException(INVENTARIO_PRODUCTO_EXISTENTE);
        }
    }

    private void validarLimitesStock(Integer stockActual, Integer stockMinimo, Integer stockMaximo) {
        if (stockMaximo != null && stockMinimo != null && stockMaximo < stockMinimo) {
            throw new BusinessException(STOCK_MAXIMO_INVALIDO);
        }

        if (stockMaximo != null && stockActual > stockMaximo) {
            throw new BusinessException(STOCK_SUPERA_MAXIMO);
        }
    }

    private Integer calcularStockNuevo(Inventario inventario, TipoMovimientoInventario tipoMovimiento, Integer cantidad) {
        Integer stockActual = inventario.getStockActual();

        if (tipoMovimiento == TipoMovimientoInventario.ENTRADA) {
            Integer stockNuevo = stockActual + cantidad;

            if (inventario.getStockMaximo() != null && stockNuevo > inventario.getStockMaximo()) {
                throw new BusinessException(STOCK_SUPERA_MAXIMO);
            }

            return stockNuevo;
        }

        if (tipoMovimiento == TipoMovimientoInventario.SALIDA) {
            if (cantidad > stockActual) {
                throw new BusinessException(STOCK_INSUFICIENTE);
            }

            return stockActual - cantidad;
        }

        throw new IllegalArgumentException(TIPO_MOVIMIENTO_INVALIDO);
    }
}