package com.runicsoft.bencolapp.finanzas.service;

import com.runicsoft.bencolapp.caja.service.CajaService;
import com.runicsoft.bencolapp.compras.models.Compra;
import com.runicsoft.bencolapp.compras.repository.CompraRepository;
import com.runicsoft.bencolapp.finanzas.dtos.request.CuentaPagarRequest;
import com.runicsoft.bencolapp.finanzas.dtos.request.PagoProveedorRequest;
import com.runicsoft.bencolapp.finanzas.dtos.response.CuentaPagarResponse;
import com.runicsoft.bencolapp.finanzas.mapper.CuentaPagarMapper;
import com.runicsoft.bencolapp.finanzas.models.CuentaPagar;
import com.runicsoft.bencolapp.finanzas.models.PagoProveedor;
import com.runicsoft.bencolapp.finanzas.repository.CuentaPagarRepository;
import com.runicsoft.bencolapp.finanzas.repository.PagoProveedorRepository;
import com.runicsoft.bencolapp.finanzas.utils.EstadoCuentaPagar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.runicsoft.bencolapp.utils.constants.MessageConstants.*;

@Service
@RequiredArgsConstructor
public class CuentaPagarServiceImpl implements CuentaPagarService {

    private final CuentaPagarRepository cuentaPagarRepository;
    private final PagoProveedorRepository pagoProveedorRepository;
    private final CompraRepository compraRepository;
    private final CuentaPagarMapper cuentaPagarMapper;
    private final CajaService cajaService;

    @Override
    @Transactional(readOnly = true)
    public List<CuentaPagarResponse> findAll() {
        List<CuentaPagar> cuentas = cuentaPagarRepository.findAll();
        return cuentaPagarMapper.convertirListaCuentaPagarDto(cuentas);
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaPagarResponse findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }
        CuentaPagar cuenta = getCuentaPagar(id);
        return cuentaPagarMapper.convertirCuentaPagarDto(cuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaPagarResponse findByCompraId(Long compraId) {
        if (compraId == null || compraId <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }
        CuentaPagar cuenta = cuentaPagarRepository.findByCompraId(compraId)
                .orElseThrow(
                        () -> new IllegalArgumentException(CUENTA_PAGAR_NO_ENCONTRADA)
                );
        return cuentaPagarMapper.convertirCuentaPagarDto(cuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaPagarResponse> findByProveedorId(Long proveedorId) {
        if (proveedorId == null || proveedorId <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        List<CuentaPagar> cuentas = cuentaPagarRepository.findByCompraProveedorId(proveedorId);
        return cuentaPagarMapper.convertirListaCuentaPagarDto(cuentas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaPagarResponse> findByEstado(EstadoCuentaPagar estado) {
        if (estado == null) {
            throw new IllegalArgumentException(ESTADO_CUENTA_INVALIDO);
        }
        List<CuentaPagar> cuentas = cuentaPagarRepository.findByEstado(estado);
        return cuentaPagarMapper.convertirListaCuentaPagarDto(cuentas);
    }

    @Override
    @Transactional
    public CuentaPagarResponse create(CuentaPagarRequest request) {
        Compra compra = getCompra(request.getCompraId());

        if (cuentaPagarRepository.existsByCompraId(compra.getId())) {
            throw new IllegalArgumentException(CUENTA_PAGAR_EXISTENTE);
        }
        CuentaPagar cuenta = new CuentaPagar();
        cuenta.setCompra(compra);
        cuenta.setMontoTotal(compra.getTotal());
        cuenta.setMontoPagado(BigDecimal.ZERO);
        cuenta.setSaldoPendiente(compra.getTotal());
        cuenta.setEstado(EstadoCuentaPagar.PENDIENTE);

        CuentaPagar cuentaGuardada = cuentaPagarRepository.save(cuenta);
        return cuentaPagarMapper.convertirCuentaPagarDto(cuentaGuardada);
    }

    @Override
    @Transactional
    public CuentaPagarResponse registrarPago(PagoProveedorRequest request) {
        CuentaPagar cuenta = getCuentaPagar(request.getCuentaPagarId());
        validarCuentaParaPago(cuenta);
        validarMontoPago(cuenta, request.getMonto());

        PagoProveedor pago = new PagoProveedor();

        pago.setCuentaPagar(cuenta);
        pago.setMonto(request.getMonto());
        pago.setMetodoPago(request.getMetodoPago());
        pago.setReferencia(request.getReferencia());

        PagoProveedor pagoGuardado = pagoProveedorRepository.save(pago);
        cuenta.getPagos().add(pagoGuardado);
        CuentaPagar cuentaActualizada = actualizarCuentaDespuesPago(cuenta, request.getMonto());
        cajaService.registrarEgreso(
                pagoGuardado.getMonto(),
                "Pago a proveedor "
                        + cuenta.getCompra()
                        .getProveedor()
                        .getRazonSocial(),
                pagoGuardado.getReferencia()
        );
        return cuentaPagarMapper.convertirCuentaPagarDto(cuentaActualizada);
    }

    // Métodos auxiliares
    private CuentaPagar getCuentaPagar(Long id) {
        return cuentaPagarRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(CUENTA_PAGAR_NO_ENCONTRADA)
                );
    }

    private Compra getCompra(Long id) {
        return compraRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(COMPRA_NO_ENCONTRADA)
                );
    }

    private void validarCuentaParaPago(CuentaPagar cuenta) {
        if (cuenta.getEstado() == EstadoCuentaPagar.ANULADA) {
            throw new IllegalArgumentException(CUENTA_PAGAR_ANULADA);
        }

        if (cuenta.getEstado() == EstadoCuentaPagar.PAGADA) {
            throw new IllegalArgumentException(CUENTA_PAGAR_PAGADA);
        }
    }

    private void validarMontoPago(CuentaPagar cuenta, BigDecimal monto) {
        if (monto.compareTo(cuenta.getSaldoPendiente()) > 0) {
            throw new IllegalArgumentException(PAGO_SUPERA_SALDO);
        }
    }

    private CuentaPagar actualizarCuentaDespuesPago(CuentaPagar cuenta, BigDecimal montoPago) {
        BigDecimal nuevoMontoPagado = cuenta.getMontoPagado().add(montoPago);
        BigDecimal nuevoSaldo = cuenta.getMontoTotal().subtract(nuevoMontoPagado);
        cuenta.setMontoPagado(nuevoMontoPagado);
        cuenta.setSaldoPendiente(nuevoSaldo);
        actualizarEstadoCuenta(cuenta);
        return cuentaPagarRepository.save(cuenta);
    }

    private void actualizarEstadoCuenta(CuentaPagar cuenta) {
        if (cuenta.getSaldoPendiente().compareTo(BigDecimal.ZERO) == 0) {
            cuenta.setEstado(EstadoCuentaPagar.PAGADA);
            return;
        }

        if (cuenta.getMontoPagado().compareTo(BigDecimal.ZERO) > 0) {
            cuenta.setEstado(EstadoCuentaPagar.PARCIAL);
            return;
        }
        cuenta.setEstado(EstadoCuentaPagar.PENDIENTE);
    }
}