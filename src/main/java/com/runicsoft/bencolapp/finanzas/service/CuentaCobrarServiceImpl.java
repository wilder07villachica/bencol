package com.runicsoft.bencolapp.finanzas.service;

import com.runicsoft.bencolapp.finanzas.dtos.request.CuentaCobrarRequest;
import com.runicsoft.bencolapp.finanzas.dtos.request.PagoRequest;
import com.runicsoft.bencolapp.finanzas.dtos.response.CuentaCobrarResponse;
import com.runicsoft.bencolapp.finanzas.mapper.CuentaCobrarMapper;
import com.runicsoft.bencolapp.finanzas.mapper.PagoMapper;
import com.runicsoft.bencolapp.finanzas.models.CuentaCobrar;
import com.runicsoft.bencolapp.finanzas.models.Pago;
import com.runicsoft.bencolapp.finanzas.repository.CuentaCobrarRepository;
import com.runicsoft.bencolapp.finanzas.repository.PagoRepository;
import com.runicsoft.bencolapp.finanzas.utils.EstadoCuenta;
import com.runicsoft.bencolapp.ventas.models.Venta;
import com.runicsoft.bencolapp.ventas.repository.VentaRepository;
import com.runicsoft.bencolapp.ventas.utils.EstadoVenta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.runicsoft.bencolapp.utils.constants.MessageConstants.*;

@Service
@RequiredArgsConstructor
public class CuentaCobrarServiceImpl implements CuentaCobrarService {

    private final CuentaCobrarRepository cuentaCobrarRepository;
    private final PagoRepository pagoRepository;
    private final VentaRepository ventaRepository;
    private final CuentaCobrarMapper cuentaCobrarMapper;
    private final PagoMapper pagoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CuentaCobrarResponse> findAll() {
        List<CuentaCobrar> cuentas = cuentaCobrarRepository.findAll();
        return cuentaCobrarMapper.convertirListaCuentaDto(cuentas);
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaCobrarResponse findById(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        CuentaCobrar cuenta = getCuentaCobrar(id);
        return cuentaCobrarMapper.convertirCuentaDto(cuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaCobrarResponse findByVentaId(Long ventaId) {
        if (ventaId == null || ventaId <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        CuentaCobrar cuenta = cuentaCobrarRepository.findByVentaId(ventaId)
                .orElseThrow(() -> new IllegalArgumentException(CUENTA_COBRAR_NO_ENCONTRADA));
        return cuentaCobrarMapper.convertirCuentaDto(cuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaCobrarResponse> findByEstado(EstadoCuenta estado) {
        if (estado == null) {
            throw new IllegalArgumentException(ESTADO_CUENTA_INVALIDO);
        }

        List<CuentaCobrar> cuentas = cuentaCobrarRepository.findByEstado(estado);
        return cuentaCobrarMapper.convertirListaCuentaDto(cuentas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaCobrarResponse> findByClienteId(Long clienteId) {
        if (clienteId == null || clienteId <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        List<CuentaCobrar> cuentas = cuentaCobrarRepository.findByVentaClienteId(clienteId);
        return cuentaCobrarMapper.convertirListaCuentaDto(cuentas);
    }

    @Override
    @Transactional
    public CuentaCobrarResponse create(CuentaCobrarRequest request) {
        Venta venta = getVenta(request.getVentaId());
        validarVentaParaCuenta(venta);

        if (cuentaCobrarRepository.existsByVentaId(venta.getId())) {
            throw new IllegalArgumentException(CUENTA_COBRAR_EXISTENTE);
        }

        CuentaCobrar cuenta = new CuentaCobrar();

        cuenta.setVenta(venta);
        cuenta.setMontoTotal(venta.getTotal());
        cuenta.setMontoPagado(BigDecimal.ZERO);
        cuenta.setSaldoPendiente(venta.getTotal());
        cuenta.setEstado(EstadoCuenta.PENDIENTE);

        CuentaCobrar cuentaGuardada = cuentaCobrarRepository.save(cuenta);
        return cuentaCobrarMapper.convertirCuentaDto(cuentaGuardada);
    }

    @Override
    @Transactional
    public CuentaCobrarResponse registrarPago(PagoRequest request) {
        CuentaCobrar cuenta = getCuentaCobrar(request.getCuentaCobrarId());
        validarCuentaParaPago(cuenta);
        validarMontoPago(cuenta, request.getMonto());

        Pago pago = new Pago();

        pago.setCuentaCobrar(cuenta);
        pago.setMonto(request.getMonto());
        pago.setMetodoPago(request.getMetodoPago());
        pago.setReferencia(request.getReferencia());

        Pago pagoGuardado = pagoRepository.save(pago);
        cuenta.getPagos().add(pagoGuardado);

        CuentaCobrar cuentaActualizada = actualizarCuentaDespuesPago(cuenta, request.getMonto());
        return cuentaCobrarMapper.convertirCuentaDto(cuentaActualizada);
    }

    // Métodos auxiliares
    private CuentaCobrar getCuentaCobrar(Long id) {
        return cuentaCobrarRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(CUENTA_COBRAR_NO_ENCONTRADA));
    }

    private Venta getVenta(Long id) {
        return ventaRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(VENTA_NO_ENCONTRADA));
    }

    private void validarVentaParaCuenta(Venta venta) {
        if (venta.getEstado() == EstadoVenta.ANULADA) {
            throw new IllegalArgumentException(VENTA_ANULADA_CUENTA);
        }
    }

    private void validarCuentaParaPago(CuentaCobrar cuenta) {
        if (cuenta.getEstado() == EstadoCuenta.ANULADA) {
            throw new IllegalArgumentException(CUENTA_COBRAR_ANULADA);
        }

        if (cuenta.getEstado() == EstadoCuenta.PAGADA) {
            throw new IllegalArgumentException(CUENTA_COBRAR_PAGADA);
        }
    }

    private void validarMontoPago(CuentaCobrar cuenta, BigDecimal monto) {
        if (monto.compareTo(cuenta.getSaldoPendiente()) > 0) {
            throw new IllegalArgumentException(PAGO_SUPERA_SALDO);
        }
    }

    private CuentaCobrar actualizarCuentaDespuesPago(CuentaCobrar cuenta, BigDecimal montoPago) {
        BigDecimal nuevoMontoPagado = cuenta.getMontoPagado().add(montoPago);
        BigDecimal nuevoSaldo = cuenta.getMontoTotal().subtract(nuevoMontoPagado);
        cuenta.setMontoPagado(nuevoMontoPagado);
        cuenta.setSaldoPendiente(nuevoSaldo);
        actualizarEstadoCuenta(cuenta);
        return cuentaCobrarRepository.save(cuenta);
    }

    private void actualizarEstadoCuenta(CuentaCobrar cuenta) {
        if (cuenta.getSaldoPendiente().compareTo(BigDecimal.ZERO) == 0) {
            cuenta.setEstado(EstadoCuenta.PAGADA);
            return;
        }

        if (cuenta.getMontoPagado().compareTo(BigDecimal.ZERO) > 0) {
            cuenta.setEstado(EstadoCuenta.PARCIAL);
            return;
        }

        cuenta.setEstado(EstadoCuenta.PENDIENTE);
    }
}