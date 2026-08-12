package com.runicsoft.bencolapp.envases.service;

import com.runicsoft.bencolapp.clientes.models.Cliente;
import com.runicsoft.bencolapp.clientes.repository.ClienteRepository;
import com.runicsoft.bencolapp.envases.dtos.request.MovimientoEnvaseRequest;
import com.runicsoft.bencolapp.envases.dtos.response.CuentaEnvasesClienteResponse;
import com.runicsoft.bencolapp.envases.dtos.response.MovimientoEnvaseResponse;
import com.runicsoft.bencolapp.envases.mapper.CuentaEnvasesClienteMapper;
import com.runicsoft.bencolapp.envases.mapper.MovimientoEnvaseMapper;
import com.runicsoft.bencolapp.envases.models.CuentaEnvasesCliente;
import com.runicsoft.bencolapp.envases.models.MovimientoEnvase;
import com.runicsoft.bencolapp.envases.repository.CuentaEnvasesClienteRepository;
import com.runicsoft.bencolapp.envases.repository.MovimientoEnvaseRepository;
import com.runicsoft.bencolapp.envases.utils.TipoMovimientoEnvase;
import com.runicsoft.bencolapp.productos.models.Producto;
import com.runicsoft.bencolapp.productos.repository.ProductoRepository;
import com.runicsoft.bencolapp.productos.utils.ProductoCategoria;
import com.runicsoft.bencolapp.seguridad.utils.SecurityUtils;
import com.runicsoft.bencolapp.utils.constants.MessageConstants;
import com.runicsoft.bencolapp.utils.exceptions.BusinessException;
import com.runicsoft.bencolapp.utils.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnvaseServiceImpl implements EnvaseService {

    private final CuentaEnvasesClienteRepository cuentaEnvasesClienteRepository;
    private final MovimientoEnvaseRepository movimientoEnvaseRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final CuentaEnvasesClienteMapper cuentaEnvasesClienteMapper;
    private final MovimientoEnvaseMapper movimientoEnvaseMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CuentaEnvasesClienteResponse> findAll() {
        List<CuentaEnvasesCliente> cuentas = cuentaEnvasesClienteRepository.findAll();
        return cuentaEnvasesClienteMapper.convertirListaCuentaDto(cuentas);
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaEnvasesClienteResponse findByClienteAndProducto(Long clienteId, Long productoId) {
        validarId(clienteId);
        validarId(productoId);

        CuentaEnvasesCliente cuenta = cuentaEnvasesClienteRepository
                .findByClienteIdAndProductoId(clienteId, productoId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.CUENTA_ENVASE_NO_ENCONTRADA));

        return cuentaEnvasesClienteMapper.convertirCuentaDto(cuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaEnvasesClienteResponse> findByClienteId(Long clienteId) {
        validarId(clienteId);

        getCliente(clienteId);

        List<CuentaEnvasesCliente> cuentas = cuentaEnvasesClienteRepository.findByClienteId(clienteId);
        return cuentaEnvasesClienteMapper.convertirListaCuentaDto(cuentas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoEnvaseResponse> findMovimientosByCuentaId(Long cuentaId) {
        validarId(cuentaId);

        if (!cuentaEnvasesClienteRepository.existsById(cuentaId)) {
            throw new ResourceNotFoundException(MessageConstants.CUENTA_ENVASE_NO_ENCONTRADA);
        }

        List<MovimientoEnvase> movimientos = movimientoEnvaseRepository
                .findByCuentaEnvaseIdOrderByFechaMovimientoDesc(cuentaId);

        return movimientoEnvaseMapper.convertirListaMovimientoDto(movimientos);
    }

    @Override
    @Transactional
    public CuentaEnvasesClienteResponse registrarMovimiento(MovimientoEnvaseRequest request) {
        Cliente cliente = getCliente(request.getClienteId());
        Producto producto = getProducto(request.getProductoId());

        validarProductoEnvase(producto);
        validarTipoMovimiento(request.getTipoMovimiento());

        CuentaEnvasesCliente cuenta = obtenerOCrearCuenta(cliente, producto);

        switch (request.getTipoMovimiento()) {
            case COMPRA -> registrarCompra(cuenta, request.getCantidad());
            case PRESTAMO -> registrarPrestamo(cuenta, request.getCantidad());
            case DEVOLUCION -> registrarDevolucion(cuenta, request.getCantidad());
            case INTERCAMBIO -> registrarIntercambio(cuenta, request.getCantidad());
            case CONVERSION_COMPRA -> registrarConversionCompra(cuenta, request.getCantidad());
            case AJUSTE -> throw new BusinessException(MessageConstants.AJUSTE_ENVASE_NO_PERMITIDO);
        }

        CuentaEnvasesCliente cuentaActualizada = cuentaEnvasesClienteRepository.save(cuenta);

        registrarHistorial(
                cuentaActualizada,
                request.getTipoMovimiento(),
                request.getCantidad(),
                request.getReferencia()
        );

        return cuentaEnvasesClienteMapper.convertirCuentaDto(cuentaActualizada);
    }

    private Cliente getCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.CLIENTE_NO_ENCONTRADO));
    }

    private Producto getProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.PRODUCTO_NO_ENCONTRADO));
    }

    private CuentaEnvasesCliente obtenerOCrearCuenta(Cliente cliente, Producto producto) {
        return cuentaEnvasesClienteRepository
                .findByClienteIdAndProductoIdForUpdate(cliente.getId(), producto.getId())
                .orElseGet(() -> {
                    CuentaEnvasesCliente cuenta = new CuentaEnvasesCliente();
                    cuenta.setCliente(cliente);
                    cuenta.setProducto(producto);
                    cuenta.setCantidadPropios(0);
                    cuenta.setCantidadPrestados(0);
                    return cuentaEnvasesClienteRepository.save(cuenta);
                });
    }

    private void registrarCompra(CuentaEnvasesCliente cuenta, Integer cantidad) {
        cuenta.setCantidadPropios(
                cuenta.getCantidadPropios() + cantidad
        );
    }

    private void registrarPrestamo(CuentaEnvasesCliente cuenta, Integer cantidad) {
        cuenta.setCantidadPrestados(
                cuenta.getCantidadPrestados() + cantidad
        );
    }

    private void registrarDevolucion(CuentaEnvasesCliente cuenta, Integer cantidad) {
        if (cantidad > cuenta.getCantidadPrestados()) {
            throw new BusinessException(MessageConstants.DEVOLUCION_ENVASE_SUPERA_PRESTADOS);
        }

        cuenta.setCantidadPrestados(
                cuenta.getCantidadPrestados() - cantidad
        );
    }

    private void registrarIntercambio(CuentaEnvasesCliente cuenta, Integer cantidad) {
        Integer cantidadDisponible = cuenta.getCantidadPropios()
                + cuenta.getCantidadPrestados();

        if (cantidad > cantidadDisponible) {
            throw new BusinessException(MessageConstants.INTERCAMBIO_ENVASE_SUPERA_DISPONIBLES);
        }
    }

    private void registrarConversionCompra(CuentaEnvasesCliente cuenta, Integer cantidad) {
        if (cantidad > cuenta.getCantidadPrestados()) {
            throw new BusinessException(MessageConstants.CONVERSION_ENVASE_SUPERA_PRESTADOS);
        }

        cuenta.setCantidadPrestados(
                cuenta.getCantidadPrestados() - cantidad
        );

        cuenta.setCantidadPropios(
                cuenta.getCantidadPropios() + cantidad
        );
    }

    private void registrarHistorial(
            CuentaEnvasesCliente cuenta,
            TipoMovimientoEnvase tipoMovimiento,
            Integer cantidad,
            String referencia
    ) {
        MovimientoEnvase movimiento = new MovimientoEnvase();

        movimiento.setCuentaEnvase(cuenta);
        movimiento.setTipoMovimiento(tipoMovimiento);
        movimiento.setCantidad(cantidad);
        movimiento.setReferencia(referencia);
        movimiento.setRegistradoPor(SecurityUtils.getUsuarioActual());

        movimientoEnvaseRepository.save(movimiento);
    }

    private void validarProductoEnvase(Producto producto) {
        if (producto.getCategoria() != ProductoCategoria.BIDON) {
            throw new BusinessException(MessageConstants.PRODUCTO_NO_ES_ENVASE);
        }
    }

    private void validarTipoMovimiento(TipoMovimientoEnvase tipoMovimiento) {
        if (tipoMovimiento == null) {
            throw new IllegalArgumentException(
                    "El tipo de movimiento de envase es obligatorio."
            );
        }
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(MessageConstants.ID_INVALIDO);
        }
    }
}