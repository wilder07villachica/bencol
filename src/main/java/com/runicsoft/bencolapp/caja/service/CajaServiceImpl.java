package com.runicsoft.bencolapp.caja.service;

import com.runicsoft.bencolapp.caja.dtos.request.CajaRequest;
import com.runicsoft.bencolapp.caja.dtos.request.CierreCajaRequest;
import com.runicsoft.bencolapp.caja.dtos.request.MovimientoCajaRequest;
import com.runicsoft.bencolapp.caja.dtos.response.CajaResponse;
import com.runicsoft.bencolapp.caja.dtos.response.MovimientoCajaResponse;
import com.runicsoft.bencolapp.caja.mapper.CajaMapper;
import com.runicsoft.bencolapp.caja.mapper.MovimientoCajaMapper;
import com.runicsoft.bencolapp.caja.models.Caja;
import com.runicsoft.bencolapp.caja.models.MovimientoCaja;
import com.runicsoft.bencolapp.caja.repository.CajaRepository;
import com.runicsoft.bencolapp.caja.repository.MovimientoCajaRepository;
import com.runicsoft.bencolapp.caja.utils.EstadoCaja;
import com.runicsoft.bencolapp.caja.utils.TipoMovimientoCaja;
import com.runicsoft.bencolapp.seguridad.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.runicsoft.bencolapp.utils.constants.MessageConstants.*;

@Service
@RequiredArgsConstructor
public class CajaServiceImpl implements CajaService {

    private final CajaRepository cajaRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;
    private final CajaMapper cajaMapper;
    private final MovimientoCajaMapper movimientoCajaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CajaResponse> findAll() {
        List<Caja> cajas = cajaRepository.findAll();
        return cajaMapper.convertirListaCajaDto(cajas);
    }

    @Override
    @Transactional(readOnly = true)
    public CajaResponse findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }
        Caja caja = getCaja(id);
        return cajaMapper.convertirCajaDto(caja);
    }

    @Override
    @Transactional(readOnly = true)
    public CajaResponse findCajaAbierta() {
        Caja caja = getCajaAbierta();
        return cajaMapper.convertirCajaDto(caja);
    }

    @Override
    @Transactional
    public CajaResponse abrirCaja(CajaRequest request) {
        if (cajaRepository.existsByEstado(EstadoCaja.ABIERTA)) {
            throw new IllegalArgumentException(CAJA_YA_ABIERTA);
        }

        Caja caja = new Caja();
        caja.setSaldoInicial(request.getSaldoInicial());
        caja.setTotalIngresos(BigDecimal.ZERO);
        caja.setTotalEgresos(BigDecimal.ZERO);
        caja.setSaldoActual(request.getSaldoInicial());
        caja.setEstado(EstadoCaja.ABIERTA);
        caja.setAbiertaPor(SecurityUtils.getUsuarioActual());

        Caja cajaGuardada = cajaRepository.save(caja);
        return cajaMapper.convertirCajaDto(cajaGuardada);
    }

    @Override
    @Transactional
    public MovimientoCajaResponse registrarMovimiento(MovimientoCajaRequest request) {
        Caja caja = getCaja(request.getCajaId());
        validarCajaAbierta(caja);

        if (request.getTipoMovimiento() == TipoMovimientoCaja.INGRESO) {
            actualizarIngresoCaja(caja, request.getMonto());
        } else if (request.getTipoMovimiento() == TipoMovimientoCaja.EGRESO) {
            validarSaldoCaja(caja, request.getMonto());
            actualizarEgresoCaja(caja, request.getMonto());
        } else {
            throw new IllegalArgumentException(TIPO_MOVIMIENTO_CAJA_INVALIDO);
        }

        MovimientoCaja movimiento = crearMovimientoCaja(
                caja,
                request.getTipoMovimiento(),
                request.getMonto(),
                request.getConcepto(),
                request.getReferencia()
        );

        cajaRepository.save(caja);
        return movimientoCajaMapper.convertirMovimientoDto(movimiento);
    }

    @Override
    @Transactional
    public CajaResponse cerrarCaja(Long id, CierreCajaRequest request) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }
        Caja caja = getCaja(id);
        validarCajaAbierta(caja);
        BigDecimal saldoEsperado = caja.getSaldoInicial().add(caja.getTotalIngresos()).subtract(caja.getTotalEgresos());
        BigDecimal saldoReal = request.getSaldoReal();
        BigDecimal diferencia = saldoReal.subtract(saldoEsperado);
        caja.setSaldoEsperado(saldoEsperado);
        caja.setSaldoReal(saldoReal);
        caja.setDiferencia(diferencia);
        caja.setEstado(EstadoCaja.CERRADA);
        caja.setFechaCierre(LocalDateTime.now());
        caja.setCerradaPor(SecurityUtils.getUsuarioActual());

        Caja cajaCerrada = cajaRepository.save(caja);
        return cajaMapper.convertirCajaDto(cajaCerrada);
    }


    @Override
    @Transactional
    public void registrarIngreso(BigDecimal monto, String concepto, String referencia) {
        Caja caja = getCajaAbierta();
        actualizarIngresoCaja(caja, monto);
        crearMovimientoCaja(caja, TipoMovimientoCaja.INGRESO, monto, concepto, referencia);
        cajaRepository.save(caja);
    }

    @Override
    @Transactional
    public void registrarEgreso(BigDecimal monto, String concepto, String referencia) {
        Caja caja = getCajaAbierta();
        validarSaldoCaja(caja, monto);
        actualizarEgresoCaja(caja, monto);
        crearMovimientoCaja(caja, TipoMovimientoCaja.EGRESO, monto, concepto, referencia);
        cajaRepository.save(caja);
    }

    // Métodos auxiliares
    private Caja getCaja(Long id) {
        return cajaRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(CAJA_NO_ENCONTRADA)
        );
    }

    private Caja getCajaAbierta() {
        return cajaRepository.findFirstByEstadoOrderByFechaAperturaDesc(EstadoCaja.ABIERTA)
                .orElseThrow(
                        () -> new IllegalArgumentException(CAJA_NO_ABIERTA)
                );
    }

    private void validarCajaAbierta(Caja caja) {
        if (caja.getEstado() != EstadoCaja.ABIERTA) {
            throw new IllegalArgumentException(CAJA_CERRADA);
        }
    }

    private void actualizarIngresoCaja(Caja caja, BigDecimal monto) {
        caja.setTotalIngresos(caja.getTotalIngresos().add(monto));
        caja.setSaldoActual(caja.getSaldoActual().add(monto));
    }

    private void actualizarEgresoCaja(Caja caja, BigDecimal monto) {
        caja.setTotalEgresos(caja.getTotalEgresos().add(monto));
        caja.setSaldoActual(caja.getSaldoActual().subtract(monto));
    }

    private MovimientoCaja crearMovimientoCaja(Caja caja, TipoMovimientoCaja tipoMovimiento, BigDecimal monto, String concepto, String referencia) {
        MovimientoCaja movimiento = new MovimientoCaja();

        movimiento.setCaja(caja);
        movimiento.setTipoMovimiento(tipoMovimiento);
        movimiento.setMonto(monto);
        movimiento.setConcepto(concepto);
        movimiento.setReferencia(referencia);

        MovimientoCaja movimientoGuardado = movimientoCajaRepository.save(movimiento);
        caja.getMovimientos().add(movimientoGuardado);
        return movimientoGuardado;
    }

    private void validarSaldoCaja(Caja caja, BigDecimal monto) {
        if (monto.compareTo(caja.getSaldoActual()) > 0) {
            throw new IllegalArgumentException(SALDO_CAJA_INSUFICIENTE);
        }
    }
}