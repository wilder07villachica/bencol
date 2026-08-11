package com.runicsoft.bencolapp.finanzas.service;

import com.runicsoft.bencolapp.caja.service.CajaService;
import com.runicsoft.bencolapp.compras.models.Compra;
import com.runicsoft.bencolapp.compras.repository.CompraRepository;
import com.runicsoft.bencolapp.finanzas.dtos.request.CuentaPagarRequest;
import com.runicsoft.bencolapp.finanzas.dtos.request.PagoProveedorRequest;
import com.runicsoft.bencolapp.finanzas.dtos.response.CuentaPagarResponse;
import com.runicsoft.bencolapp.finanzas.dtos.response.DeudaProveedorResponse;
import com.runicsoft.bencolapp.finanzas.dtos.response.ResumenCuentasPagarResponse;
import com.runicsoft.bencolapp.finanzas.mapper.CuentaPagarMapper;
import com.runicsoft.bencolapp.finanzas.models.CuentaPagar;
import com.runicsoft.bencolapp.finanzas.models.PagoProveedor;
import com.runicsoft.bencolapp.finanzas.repository.CuentaPagarRepository;
import com.runicsoft.bencolapp.finanzas.repository.PagoProveedorRepository;
import com.runicsoft.bencolapp.finanzas.utils.EstadoCuentaPagar;
import com.runicsoft.bencolapp.proveedores.models.Proveedor;
import com.runicsoft.bencolapp.proveedores.repository.ProveedorRepository;
import com.runicsoft.bencolapp.seguridad.utils.SecurityUtils;
import com.runicsoft.bencolapp.utils.exceptions.BusinessException;
import com.runicsoft.bencolapp.utils.exceptions.ConflictException;
import com.runicsoft.bencolapp.utils.exceptions.ResourceNotFoundException;
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
    private final ProveedorRepository proveedorRepository;

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
                .orElseThrow(() -> new ResourceNotFoundException(CUENTA_PAGAR_NO_ENCONTRADA));

        return cuentaPagarMapper.convertirCuentaPagarDto(cuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaPagarResponse> findByProveedorId(Long proveedorId) {
        if (proveedorId == null || proveedorId <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new ResourceNotFoundException(PROVEEDOR_NO_ENCONTRADO));

        List<CuentaPagar> cuentas = cuentaPagarRepository.findByCompraProveedorId(proveedor.getId());
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
            throw new ConflictException(CUENTA_PAGAR_EXISTENTE);
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
        pago.setRegistradoPor(SecurityUtils.getUsuarioActual());

        PagoProveedor pagoGuardado = pagoProveedorRepository.save(pago);
        cuenta.getPagos().add(pagoGuardado);

        CuentaPagar cuentaActualizada = actualizarCuentaDespuesPago(cuenta, request.getMonto());

        cajaService.registrarEgreso(
                pagoGuardado.getMonto(),
                "Pago a proveedor " + cuenta.getCompra().getProveedor().getRazonSocial(),
                pagoGuardado.getReferencia()
        );

        return cuentaPagarMapper.convertirCuentaPagarDto(cuentaActualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public DeudaProveedorResponse obtenerDeudaProveedor(Long proveedorId) {
        if (proveedorId == null || proveedorId <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new ResourceNotFoundException(PROVEEDOR_NO_ENCONTRADO));

        List<CuentaPagar> cuentas = cuentaPagarRepository.findByCompraProveedorId(proveedorId);

        List<CuentaPagar> cuentasConDeuda = cuentas.stream()
                .filter(cuenta ->
                        cuenta.getEstado() == EstadoCuentaPagar.PENDIENTE ||
                                cuenta.getEstado() == EstadoCuentaPagar.PARCIAL)
                .toList();

        BigDecimal deudaTotal = cuentasConDeuda.stream()
                .map(CuentaPagar::getSaldoPendiente)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        DeudaProveedorResponse response = new DeudaProveedorResponse();
        response.setProveedorId(proveedor.getId());
        response.setRazonSocialProveedor(proveedor.getRazonSocial());
        response.setDeudaTotal(deudaTotal);
        response.setCantidadCuentasPendientes(cuentasConDeuda.size());
        response.setCuentas(cuentaPagarMapper.convertirListaCuentaPagarDto(cuentasConDeuda));

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenCuentasPagarResponse obtenerResumenCuentasPagar() {
        List<CuentaPagar> cuentas = cuentaPagarRepository.findAll();

        BigDecimal totalComprado = cuentas.stream()
                .filter(cuenta -> cuenta.getEstado() != EstadoCuentaPagar.ANULADA)
                .map(CuentaPagar::getMontoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPagado = cuentas.stream()
                .filter(cuenta -> cuenta.getEstado() != EstadoCuentaPagar.ANULADA)
                .map(CuentaPagar::getMontoPagado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPorPagar = cuentas.stream()
                .filter(cuenta ->
                        cuenta.getEstado() == EstadoCuentaPagar.PENDIENTE ||
                                cuenta.getEstado() == EstadoCuentaPagar.PARCIAL)
                .map(CuentaPagar::getSaldoPendiente)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int pendientes = 0;
        int parciales = 0;
        int pagadas = 0;
        int anuladas = 0;

        for (CuentaPagar cuenta : cuentas) {
            switch (cuenta.getEstado()) {
                case PENDIENTE -> pendientes++;
                case PARCIAL -> parciales++;
                case PAGADA -> pagadas++;
                case ANULADA -> anuladas++;
            }
        }

        ResumenCuentasPagarResponse response = new ResumenCuentasPagarResponse();
        response.setTotalComprado(totalComprado);
        response.setTotalPagado(totalPagado);
        response.setTotalPorPagar(totalPorPagar);
        response.setCantidadCuentas(cuentas.size());
        response.setCantidadPendientes(pendientes);
        response.setCantidadParciales(parciales);
        response.setCantidadPagadas(pagadas);
        response.setCantidadAnuladas(anuladas);

        return response;
    }

    private CuentaPagar getCuentaPagar(Long id) {
        return cuentaPagarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CUENTA_PAGAR_NO_ENCONTRADA));
    }

    private Compra getCompra(Long id) {
        return compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(COMPRA_NO_ENCONTRADA));
    }

    private void validarCuentaParaPago(CuentaPagar cuenta) {
        if (cuenta.getEstado() == EstadoCuentaPagar.ANULADA) {
            throw new BusinessException(CUENTA_PAGAR_ANULADA);
        }

        if (cuenta.getEstado() == EstadoCuentaPagar.PAGADA) {
            throw new BusinessException(CUENTA_PAGAR_PAGADA);
        }
    }

    private void validarMontoPago(CuentaPagar cuenta, BigDecimal monto) {
        if (monto.compareTo(cuenta.getSaldoPendiente()) > 0) {
            throw new BusinessException(PAGO_SUPERA_SALDO);
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