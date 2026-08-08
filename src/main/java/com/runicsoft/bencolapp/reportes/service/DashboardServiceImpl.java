package com.runicsoft.bencolapp.reportes.service;

import com.runicsoft.bencolapp.caja.repository.CajaRepository;
import com.runicsoft.bencolapp.caja.utils.EstadoCaja;
import com.runicsoft.bencolapp.compras.models.Compra;
import com.runicsoft.bencolapp.compras.repository.CompraRepository;
import com.runicsoft.bencolapp.compras.utils.EstadoCompra;
import com.runicsoft.bencolapp.finanzas.models.CuentaCobrar;
import com.runicsoft.bencolapp.finanzas.models.CuentaPagar;
import com.runicsoft.bencolapp.finanzas.repository.CuentaCobrarRepository;
import com.runicsoft.bencolapp.finanzas.repository.CuentaPagarRepository;
import com.runicsoft.bencolapp.finanzas.utils.EstadoCuenta;
import com.runicsoft.bencolapp.finanzas.utils.EstadoCuentaPagar;
import com.runicsoft.bencolapp.inventario.models.Inventario;
import com.runicsoft.bencolapp.inventario.repository.InventarioRepository;
import com.runicsoft.bencolapp.reportes.dtos.response.*;
import com.runicsoft.bencolapp.ventas.models.Venta;
import com.runicsoft.bencolapp.ventas.repository.VentaRepository;
import com.runicsoft.bencolapp.ventas.utils.EstadoVenta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final VentaRepository ventaRepository;
    private final CuentaCobrarRepository cuentaCobrarRepository;
    private final CuentaPagarRepository cuentaPagarRepository;
    private final CajaRepository cajaRepository;
    private final CompraRepository compraRepository;
    private final InventarioRepository inventarioRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse obtenerDashboard() {
        DashboardResponse response = new DashboardResponse();
        response.setVentas(obtenerResumenVentas());
        response.setFinanzas(obtenerResumenFinanzas());
        response.setCaja(obtenerResumenCaja());
        response.setCompras(obtenerResumenCompras());
        response.setInventario(obtenerResumenInventario());
        return response;
    }

    // métodos auxiliares...
    private ResumenVentasResponse obtenerResumenVentas() {
        List<Venta> ventas = ventaRepository.findAll();
        List<Venta> ventasValidas = ventas.stream()
                .filter(venta ->
                                venta.getEstado() != EstadoVenta.ANULADA
                ).toList();

        BigDecimal totalVendido = ventasValidas.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long cantidadVentas = ventasValidas.size();

        BigDecimal ticketPromedio = cantidadVentas > 0 ? totalVendido
                .divide(BigDecimal.valueOf(cantidadVentas), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        ResumenVentasResponse response = new ResumenVentasResponse();

        response.setTotalVendido(totalVendido);
        response.setCantidadVentas(cantidadVentas);
        response.setTicketPromedio(ticketPromedio);
        return response;
    }

    private ResumenFinanzasResponse obtenerResumenFinanzas() {
        List<CuentaCobrar> cuentasCobrar = cuentaCobrarRepository.findAll();
        List<CuentaPagar> cuentasPagar = cuentaPagarRepository.findAll();
        BigDecimal totalCobrado = cuentasCobrar.stream()
                .filter(cuenta ->
                                cuenta.getEstado() != EstadoCuenta.ANULADA)
                .map(CuentaCobrar::getMontoPagado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPorCobrar = cuentasCobrar.stream()
                .filter(cuenta ->
                                cuenta.getEstado() == EstadoCuenta.PENDIENTE || cuenta.getEstado() == EstadoCuenta.PARCIAL)
                .map(CuentaCobrar::getSaldoPendiente)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPagadoProveedores = cuentasPagar.stream()
                .filter(cuenta ->
                                cuenta.getEstado() != EstadoCuentaPagar.ANULADA)
                .map(CuentaPagar::getMontoPagado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPorPagar = cuentasPagar.stream()
                .filter(cuenta ->
                                cuenta.getEstado() == EstadoCuentaPagar.PENDIENTE || cuenta.getEstado() == EstadoCuentaPagar.PARCIAL)
                .map(CuentaPagar::getSaldoPendiente)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ResumenFinanzasResponse response = new ResumenFinanzasResponse();

        response.setTotalCobrado(totalCobrado);
        response.setTotalPorCobrar(totalPorCobrar);
        response.setTotalPagadoProveedores(totalPagadoProveedores);
        response.setTotalPorPagar(totalPorPagar);
        return response;
    }

    private ResumenCajaResponse obtenerResumenCaja() {
        ResumenCajaResponse response = new ResumenCajaResponse();

        cajaRepository.findFirstByEstadoOrderByFechaAperturaDesc(EstadoCaja.ABIERTA)
                .ifPresent(caja -> {
                    response.setCajaId(caja.getId());
                    response.setEstado(caja.getEstado());
                    response.setSaldoInicial(caja.getSaldoInicial());
                    response.setTotalIngresos(caja.getTotalIngresos());
                    response.setTotalEgresos(caja.getTotalEgresos());
                    response.setSaldoActual(caja.getSaldoActual());
                });
        return response;
    }

    private ResumenComprasResponse obtenerResumenCompras() {
        List<Compra> compras = compraRepository.findAll();
        List<Compra> comprasValidas = compras.stream()
                .filter(compra ->
                                compra.getEstado() != EstadoCompra.ANULADA)
                .toList();

        BigDecimal totalComprado = comprasValidas.stream()
                .map(Compra::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ResumenComprasResponse response = new ResumenComprasResponse();
        response.setTotalComprado(totalComprado);
        response.setCantidadCompras((long) comprasValidas.size());
        return response;
    }

    private ResumenInventarioResponse obtenerResumenInventario() {
        List<Inventario> inventarios = inventarioRepository.findAll();
        long productosStockBajo = inventarios.stream()
                .filter(inventario ->
                                inventario.getStockActual() <= inventario.getStockMinimo())
                .count();

        ResumenInventarioResponse response = new ResumenInventarioResponse();
        response.setCantidadProductosInventario((long) inventarios.size());
        response.setProductosStockBajo(productosStockBajo);
        return response;
    }
}