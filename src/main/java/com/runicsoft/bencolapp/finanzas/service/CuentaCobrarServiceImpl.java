package com.runicsoft.bencolapp.finanzas.service;

import com.runicsoft.bencolapp.caja.service.CajaService;
import com.runicsoft.bencolapp.clientes.models.Cliente;
import com.runicsoft.bencolapp.clientes.repository.ClienteRepository;
import com.runicsoft.bencolapp.finanzas.dtos.request.CuentaCobrarRequest;
import com.runicsoft.bencolapp.finanzas.dtos.request.PagoRequest;
import com.runicsoft.bencolapp.finanzas.dtos.response.CuentaCobrarResponse;
import com.runicsoft.bencolapp.finanzas.dtos.response.DeudaClienteResponse;
import com.runicsoft.bencolapp.finanzas.dtos.response.ResumenFinancieroResponse;
import com.runicsoft.bencolapp.finanzas.mapper.CuentaCobrarMapper;
import com.runicsoft.bencolapp.finanzas.models.CuentaCobrar;
import com.runicsoft.bencolapp.finanzas.models.Pago;
import com.runicsoft.bencolapp.finanzas.repository.CuentaCobrarRepository;
import com.runicsoft.bencolapp.finanzas.repository.PagoRepository;
import com.runicsoft.bencolapp.finanzas.utils.EstadoCuenta;
import com.runicsoft.bencolapp.seguridad.utils.SecurityUtils;
import com.runicsoft.bencolapp.utils.exceptions.BusinessException;
import com.runicsoft.bencolapp.utils.exceptions.ConflictException;
import com.runicsoft.bencolapp.utils.exceptions.ResourceNotFoundException;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;
import com.runicsoft.bencolapp.ventas.models.Venta;
import com.runicsoft.bencolapp.ventas.repository.VentaRepository;
import com.runicsoft.bencolapp.ventas.utils.EstadoVenta;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.runicsoft.bencolapp.utils.constants.MessageConstants.*;

@Service
@RequiredArgsConstructor
public class CuentaCobrarServiceImpl implements CuentaCobrarService {

    private final CuentaCobrarRepository cuentaCobrarRepository;
    private final PagoRepository pagoRepository;
    private final VentaRepository ventaRepository;
    private final CuentaCobrarMapper cuentaCobrarMapper;
    private final ClienteRepository clienteRepository;
    private final CajaService cajaService;

    @Override
    @Transactional(readOnly = true)
    public PaginaResponse<CuentaCobrarResponse> findAll(int pagina, int tamanio, Long clienteId, EstadoCuenta estado, LocalDate desde, LocalDate hasta) {
        validarPaginacion(pagina, tamanio);
        validarRangoFechas(desde, hasta);

        if (clienteId != null) {
            if (clienteId <= 0) {
                throw new IllegalArgumentException(ID_INVALIDO);
            }

            getCliente(clienteId);
        }

        LocalDateTime fechaInicio =
                desde != null ? desde.atStartOfDay() : null;

        LocalDateTime fechaFin =
                hasta != null ? hasta.plusDays(1).atStartOfDay() : null;

        Pageable pageable = PageRequest.of(
                pagina,
                tamanio,
                Sort.by("venta.fechaCreacion").descending()
        );

        Page<CuentaCobrar> cuentas = cuentaCobrarRepository.buscar(
                clienteId,
                estado,
                fechaInicio,
                fechaFin,
                pageable
        );

        Page<CuentaCobrarResponse> responses =
                cuentas.map(cuentaCobrarMapper::convertirCuentaDto);

        return PaginaResponse.from(responses);
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
                .orElseThrow(() -> new ResourceNotFoundException(CUENTA_COBRAR_NO_ENCONTRADA));

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

        Cliente cliente = getCliente(clienteId);

        List<CuentaCobrar> cuentas =
                cuentaCobrarRepository.findByVentaClienteId(cliente.getId());

        return cuentaCobrarMapper.convertirListaCuentaDto(cuentas);
    }

    @Override
    @Transactional
    public CuentaCobrarResponse create(CuentaCobrarRequest request) {
        Venta venta = getVenta(request.getVentaId());
        validarVentaParaCuenta(venta);

        if (cuentaCobrarRepository.existsByVentaId(venta.getId())) {
            throw new ConflictException(CUENTA_COBRAR_EXISTENTE);
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
        pago.setRegistradoPor(SecurityUtils.getUsuarioActual());

        Pago pagoGuardado = pagoRepository.save(pago);
        cuenta.getPagos().add(pagoGuardado);

        CuentaCobrar cuentaActualizada = actualizarCuentaDespuesPago(cuenta, request.getMonto());

        cajaService.registrarIngreso(
                pagoGuardado.getMonto(),
                "Pago de venta " + cuenta.getVenta().getCodigo(),
                pagoGuardado.getReferencia()
        );

        return cuentaCobrarMapper.convertirCuentaDto(cuentaActualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public DeudaClienteResponse obtenerDeudaCliente(Long clienteId) {
        if (clienteId == null || clienteId <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException(CLIENTE_NO_ENCONTRADO));

        List<CuentaCobrar> cuentas = cuentaCobrarRepository.findByVentaClienteId(clienteId);

        List<CuentaCobrar> cuentasConDeuda = cuentas.stream()
                .filter(cuenta ->
                        cuenta.getEstado() == EstadoCuenta.PENDIENTE ||
                                cuenta.getEstado() == EstadoCuenta.PARCIAL)
                .toList();

        BigDecimal deudaTotal = cuentasConDeuda.stream()
                .map(CuentaCobrar::getSaldoPendiente)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        DeudaClienteResponse response = new DeudaClienteResponse();
        response.setClienteId(cliente.getId());
        response.setNombreCliente(cliente.getNombre());
        response.setDeudaTotal(deudaTotal);
        response.setCantidadCuentasPendientes(cuentasConDeuda.size());
        response.setCuentas(cuentaCobrarMapper.convertirListaCuentaDto(cuentasConDeuda));

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenFinancieroResponse obtenerResumenFinanciero() {
        List<CuentaCobrar> cuentas = cuentaCobrarRepository.findAll();

        BigDecimal totalVendido = cuentas.stream()
                .filter(cuenta -> cuenta.getEstado() != EstadoCuenta.ANULADA)
                .map(CuentaCobrar::getMontoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCobrado = cuentas.stream()
                .filter(cuenta -> cuenta.getEstado() != EstadoCuenta.ANULADA)
                .map(CuentaCobrar::getMontoPagado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPorCobrar = cuentas.stream()
                .filter(cuenta ->
                        cuenta.getEstado() == EstadoCuenta.PENDIENTE ||
                                cuenta.getEstado() == EstadoCuenta.PARCIAL)
                .map(CuentaCobrar::getSaldoPendiente)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int pendientes = 0;
        int parciales = 0;
        int pagadas = 0;
        int anuladas = 0;

        for (CuentaCobrar cuenta : cuentas) {
            switch (cuenta.getEstado()) {
                case PENDIENTE -> pendientes++;
                case PARCIAL -> parciales++;
                case PAGADA -> pagadas++;
                case ANULADA -> anuladas++;
            }
        }

        ResumenFinancieroResponse response = new ResumenFinancieroResponse();
        response.setTotalVendido(totalVendido);
        response.setTotalCobrado(totalCobrado);
        response.setTotalPorCobrar(totalPorCobrar);
        response.setCantidadCuentas(cuentas.size());
        response.setCantidadPendientes(pendientes);
        response.setCantidadParciales(parciales);
        response.setCantidadPagadas(pagadas);
        response.setCantidadAnuladas(anuladas);

        return response;
    }

    // Metodos auxiliares
    private Cliente getCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(CLIENTE_NO_ENCONTRADO)
                );
    }

    private CuentaCobrar getCuentaCobrar(Long id) {
        return cuentaCobrarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CUENTA_COBRAR_NO_ENCONTRADA));
    }

    private Venta getVenta(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(VENTA_NO_ENCONTRADA));
    }

    private void validarVentaParaCuenta(Venta venta) {
        if (venta.getEstado() == EstadoVenta.ANULADA) {
            throw new BusinessException(VENTA_ANULADA_CUENTA);
        }
    }

    private void validarCuentaParaPago(CuentaCobrar cuenta) {
        if (cuenta.getEstado() == EstadoCuenta.ANULADA) {
            throw new BusinessException(CUENTA_COBRAR_ANULADA);
        }

        if (cuenta.getEstado() == EstadoCuenta.PAGADA) {
            throw new BusinessException(CUENTA_COBRAR_PAGADA);
        }
    }

    private void validarMontoPago(CuentaCobrar cuenta, BigDecimal monto) {
        if (monto.compareTo(cuenta.getSaldoPendiente()) > 0) {
            throw new BusinessException(PAGO_SUPERA_SALDO);
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