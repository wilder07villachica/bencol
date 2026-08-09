package com.runicsoft.bencolapp.reportes.controller;

import com.runicsoft.bencolapp.reportes.dtos.response.*;
import com.runicsoft.bencolapp.reportes.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/bencol.agua/reportes")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> obtenerDashboard(@RequestParam LocalDate desde, @RequestParam LocalDate hasta) {
        return ResponseEntity.ok(dashboardService.obtenerDashboard(desde, hasta));
    }

    @GetMapping("/productos-mas-vendidos")
    public ResponseEntity<List<ProductoMasVendidoResponse>> obtenerProductosMasVendidos(@RequestParam LocalDate desde, @RequestParam LocalDate hasta, @RequestParam(defaultValue = "10") Integer limite) {
        return ResponseEntity.ok(dashboardService.obtenerProductosMasVendidos(desde, hasta, limite));
    }

    @GetMapping("/ventas-por-dia")
    public ResponseEntity<List<VentaPorDiaResponse>> obtenerVentasPorDia(@RequestParam LocalDate desde, @RequestParam LocalDate hasta) {
        return ResponseEntity.ok(dashboardService.obtenerVentasPorDia(desde, hasta));
    }

    @GetMapping("/clientes-mayor-deuda")
    public ResponseEntity<List<ClienteDeudaResponse>>
    obtenerClientesConMayorDeuda(@RequestParam(defaultValue = "10") Integer limite) {
        return ResponseEntity.ok(dashboardService.obtenerClientesConMayorDeuda(limite));
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ProductoStockBajoResponse>> obtenerProductosStockBajo() {
        return ResponseEntity.ok(dashboardService.obtenerProductosStockBajo());
    }

    @GetMapping("/ventas-por-mes")
    public ResponseEntity<List<VentaPorMesResponse>> obtenerVentasPorMes(@RequestParam LocalDate desde, @RequestParam LocalDate hasta) {
        return ResponseEntity.ok(dashboardService.obtenerVentasPorMes(desde, hasta));
    }

    @GetMapping("/ingresos-por-mes")
    public ResponseEntity<List<IngresoPorMesResponse>> obtenerIngresosPorMes(@RequestParam LocalDate desde, @RequestParam LocalDate hasta) {
        return ResponseEntity.ok(dashboardService.obtenerIngresosPorMes(desde, hasta));
    }

    @GetMapping("/egresos-por-mes")
    public ResponseEntity<List<EgresoPorMesResponse>> obtenerEgresosPorMes(@RequestParam LocalDate desde, @RequestParam LocalDate hasta) {
        return ResponseEntity.ok(dashboardService.obtenerEgresosPorMes(desde, hasta));
    }

    @GetMapping("/flujo-neto-por-mes")
    public ResponseEntity<List<FlujoNetoPorMesResponse>> obtenerFlujoNetoPorMes(@RequestParam LocalDate desde, @RequestParam LocalDate hasta) {
        return ResponseEntity.ok(dashboardService.obtenerFlujoNetoPorMes(desde, hasta));
    }

    @GetMapping("/tendencias")
    public ResponseEntity<TendenciasDashboardResponse> obtenerTendencias(@RequestParam LocalDate desde, @RequestParam LocalDate hasta) {
        return ResponseEntity.ok(dashboardService.obtenerTendencias(desde, hasta));
    }

    @GetMapping("/rentabilidad-productos")
    public ResponseEntity<List<RentabilidadProductoResponse>> obtenerRentabilidadProductos(@RequestParam LocalDate desde, @RequestParam LocalDate hasta) {
        return ResponseEntity.ok(dashboardService.obtenerRentabilidadProductos(desde, hasta));
    }
}