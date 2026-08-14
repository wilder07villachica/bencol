package com.runicsoft.bencolapp.envases.service;

import com.runicsoft.bencolapp.clientes.models.Cliente;
import com.runicsoft.bencolapp.clientes.repository.ClienteRepository;
import com.runicsoft.bencolapp.envases.dtos.request.MovimientoEnvaseRequest;
import com.runicsoft.bencolapp.envases.dtos.request.SaldoInicialEnvaseRequest;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnvaseServiceImpl implements EnvaseService {

    private final CuentaEnvasesClienteRepository cuentaEnvasesClienteRepository;
    private final MovimientoEnvaseRepository movimientoEnvaseRepository;

    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    private final CuentaEnvasesClienteMapper cuentaEnvasesClienteMapper;
    private final MovimientoEnvaseMapper movimientoEnvaseMapper;

    // CONSULTAS
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

        CuentaEnvasesCliente cuenta =
                cuentaEnvasesClienteRepository
                        .findByClienteIdAndProductoId(
                                clienteId,
                                productoId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        MessageConstants.CUENTA_ENVASE_NO_ENCONTRADA
                                )
                        );

        return cuentaEnvasesClienteMapper.convertirCuentaDto(cuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaEnvasesClienteResponse> findByClienteId(Long clienteId) {
        validarId(clienteId);

        getCliente(clienteId);

        List<CuentaEnvasesCliente> cuentas =
                cuentaEnvasesClienteRepository.findByClienteId(
                        clienteId
                );

        return cuentaEnvasesClienteMapper.convertirListaCuentaDto(cuentas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoEnvaseResponse> findMovimientosByCuentaId(Long cuentaId) {
        validarId(cuentaId);

        if (!cuentaEnvasesClienteRepository.existsById(cuentaId)) {
            throw new ResourceNotFoundException(
                    MessageConstants.CUENTA_ENVASE_NO_ENCONTRADA
            );
        }

        List<MovimientoEnvase> movimientos =
                movimientoEnvaseRepository
                        .findByCuentaEnvaseIdOrderByFechaMovimientoDesc(
                                cuentaId
                        );

        return movimientoEnvaseMapper.convertirListaMovimientoDto(
                movimientos
        );
    }

    // MOVIMIENTOS MANUALES DESDE EL MÓDULO ENVASES
    @Override
    @Transactional
    public CuentaEnvasesClienteResponse registrarMovimiento(MovimientoEnvaseRequest request) {
        Cliente cliente = getCliente(request.getClienteId());
        Producto producto = getProducto(request.getProductoId());
        validarProductoEnvase(producto);
        validarTipoMovimiento(request.getTipoMovimiento());

        CuentaEnvasesCliente cuenta =
                obtenerOCrearCuenta(
                        cliente,
                        producto
                );

        switch (request.getTipoMovimiento()) {
            case COMPRA -> registrarCompra(cuenta, request.getCantidad());
            case PRESTAMO -> registrarPrestamo(cuenta, request.getCantidad());
            case DEVOLUCION -> registrarDevolucion(cuenta, request.getCantidad());
            case INTERCAMBIO -> registrarIntercambio(cuenta, request.getCantidad());
            case CONVERSION_COMPRA -> registrarConversionCompra(cuenta, request.getCantidad());
            case AJUSTE -> throw new BusinessException(MessageConstants.AJUSTE_ENVASE_NO_PERMITIDO);
        }

        CuentaEnvasesCliente cuentaActualizada =
                cuentaEnvasesClienteRepository.save(cuenta);

        registrarHistorial(
                cuentaActualizada,
                request.getTipoMovimiento(),
                request.getCantidad(),
                request.getReferencia()
        );

        return cuentaEnvasesClienteMapper.convertirCuentaDto(
                cuentaActualizada
        );
    }

    // REVERSIÓN DE VENTA
    @Override
    @Transactional
    public void revertirMovimientoVenta(Long clienteId, Long productoId, TipoMovimientoEnvase tipoMovimiento, Integer cantidad, String referencia) {
        validarId(clienteId);
        validarId(productoId);

        if (tipoMovimiento == null) {
            throw new IllegalArgumentException(
                    "El tipo de movimiento de envase es obligatorio."
            );
        }

        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de envases debe ser mayor a cero."
            );
        }

        CuentaEnvasesCliente cuenta =
                cuentaEnvasesClienteRepository
                        .findByClienteIdAndProductoIdForUpdate(
                                clienteId,
                                productoId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        MessageConstants.CUENTA_ENVASE_NO_ENCONTRADA
                                )
                        );

        switch (tipoMovimiento) {

            case INTERCAMBIO -> {
            }

            case PRESTAMO -> {

                if (cantidad > cuenta.getCantidadPrestados()) {
                    throw new BusinessException(
                            "No es posible revertir el préstamo porque " +
                                    "el saldo de envases prestados es insuficiente."
                    );
                }

                cuenta.setCantidadPrestados(
                        cuenta.getCantidadPrestados() - cantidad
                );
            }

            case COMPRA -> {

                if (cantidad > cuenta.getCantidadPropios()) {
                    throw new BusinessException(
                            "No es posible revertir la compra porque " +
                                    "el saldo de envases propios es insuficiente."
                    );
                }

                cuenta.setCantidadPropios(
                        cuenta.getCantidadPropios() - cantidad
                );
            }

            default -> throw new BusinessException(
                    "El movimiento de envase no puede revertirse desde una venta."
            );
        }

        CuentaEnvasesCliente cuentaActualizada = cuentaEnvasesClienteRepository.save(cuenta);

        registrarHistorial(
                cuentaActualizada,
                TipoMovimientoEnvase.AJUSTE,
                cantidad,
                referencia
        );
    }

    // SALDO INICIAL
    @Override
    @Transactional
    public CuentaEnvasesClienteResponse registrarSaldoInicial(SaldoInicialEnvaseRequest request) {
        Cliente cliente = getCliente(request.getClienteId());
        Producto producto = getProducto(request.getProductoId());

        validarProductoEnvase(producto);

        if (request.getCantidadPropios() == 0 &&
                request.getCantidadPrestados() == 0) {

            throw new BusinessException(
                    "El saldo inicial debe contener al menos un envase."
            );
        }

        Optional<CuentaEnvasesCliente> cuentaExistente =
                cuentaEnvasesClienteRepository
                        .findByClienteIdAndProductoIdForUpdate(
                                cliente.getId(),
                                producto.getId()
                        );

        if (cuentaExistente.isPresent()) {
            throw new BusinessException(
                    "El cliente ya tiene una cuenta de envases para este producto."
            );
        }

        CuentaEnvasesCliente cuenta = new CuentaEnvasesCliente();

        cuenta.setCliente(cliente);
        cuenta.setProducto(producto);
        cuenta.setCantidadPropios(request.getCantidadPropios());
        cuenta.setCantidadPrestados(request.getCantidadPrestados());

        CuentaEnvasesCliente cuentaGuardada = cuentaEnvasesClienteRepository.save(cuenta);

        Integer cantidadTotal =
                request.getCantidadPropios()
                        + request.getCantidadPrestados();

        registrarHistorial(
                cuentaGuardada,
                TipoMovimientoEnvase.AJUSTE,
                cantidadTotal,
                request.getReferencia() != null &&
                        !request.getReferencia().isBlank()
                        ? request.getReferencia()
                        : "Carga inicial de envases"
        );

        return cuentaEnvasesClienteMapper.convertirCuentaDto(
                cuentaGuardada
        );
    }

    // CLIENTE / PRODUCTO / CUENTA
    private Cliente getCliente(Long id) {
        return clienteRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                MessageConstants.CLIENTE_NO_ENCONTRADO
                        )
                );
    }

    private Producto getProducto(Long id) {
        return productoRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                MessageConstants.PRODUCTO_NO_ENCONTRADO
                        )
                );
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

    // OPERACIONES SOBRE CUENTA DE ENVASES
    private void registrarCompra(CuentaEnvasesCliente cuenta, Integer cantidad) {
        cuenta.setCantidadPropios(
                cuenta.getCantidadPropios()
                        + cantidad
        );
    }

    private void registrarPrestamo(CuentaEnvasesCliente cuenta, Integer cantidad) {
        cuenta.setCantidadPrestados(
                cuenta.getCantidadPrestados()
                        + cantidad
        );
    }

    private void registrarDevolucion(CuentaEnvasesCliente cuenta, Integer cantidad) {
        if (cantidad > cuenta.getCantidadPrestados()) {
            throw new BusinessException(MessageConstants.DEVOLUCION_ENVASE_SUPERA_PRESTADOS);
        }

        cuenta.setCantidadPrestados(
                cuenta.getCantidadPrestados()
                        - cantidad
        );
    }

    private void registrarIntercambio(CuentaEnvasesCliente cuenta, Integer cantidad) {
        Integer cantidadDisponible =
                cuenta.getCantidadPropios()
                        + cuenta.getCantidadPrestados();

        if (cantidad > cantidadDisponible) {

            throw new BusinessException(
                    MessageConstants.INTERCAMBIO_ENVASE_SUPERA_DISPONIBLES
            );
        }
    }

    private void registrarConversionCompra(CuentaEnvasesCliente cuenta, Integer cantidad) {
        if (cantidad > cuenta.getCantidadPrestados()) {
            throw new BusinessException(
                    MessageConstants.CONVERSION_ENVASE_SUPERA_PRESTADOS
            );
        }

        cuenta.setCantidadPrestados(
                cuenta.getCantidadPrestados()
                        - cantidad
        );

        cuenta.setCantidadPropios(
                cuenta.getCantidadPropios()
                        + cantidad
        );
    }

    // HISTORIAL ENVASES
    private void registrarHistorial(CuentaEnvasesCliente cuenta, TipoMovimientoEnvase tipoMovimiento, Integer cantidad, String referencia) {
        MovimientoEnvase movimiento = new MovimientoEnvase();
        movimiento.setCuentaEnvase(cuenta);
        movimiento.setTipoMovimiento(tipoMovimiento);
        movimiento.setCantidad(cantidad);
        movimiento.setReferencia(referencia);
        movimiento.setRegistradoPor(SecurityUtils.getUsuarioActual());
        movimientoEnvaseRepository.save(movimiento);
    }

    // VALIDACIONES
    private void validarProductoEnvase(Producto producto) {
        if (producto.getCategoria() !=
                ProductoCategoria.BIDON) {

            throw new BusinessException(
                    MessageConstants.PRODUCTO_NO_ES_ENVASE
            );
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

            throw new IllegalArgumentException(
                    MessageConstants.ID_INVALIDO
            );
        }
    }
}