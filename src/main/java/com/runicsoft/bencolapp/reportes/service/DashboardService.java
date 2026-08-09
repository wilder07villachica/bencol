package com.runicsoft.bencolapp.reportes.service;

import com.runicsoft.bencolapp.reportes.dtos.response.*;

import java.time.LocalDate;
import java.util.List;

public interface DashboardService {
    DashboardResponse obtenerDashboard(LocalDate desde, LocalDate hasta);

    List<ProductoMasVendidoResponse> obtenerProductosMasVendidos(LocalDate desde, LocalDate hasta, Integer limite);
    List<VentaPorDiaResponse> obtenerVentasPorDia(LocalDate desde, LocalDate hasta);

    List<ClienteDeudaResponse> obtenerClientesConMayorDeuda(Integer limite);
    List<ProductoStockBajoResponse> obtenerProductosStockBajo();

    List<VentaPorMesResponse> obtenerVentasPorMes(LocalDate desde, LocalDate hasta);
    List<IngresoPorMesResponse> obtenerIngresosPorMes(LocalDate desde, LocalDate hasta);

    List<EgresoPorMesResponse> obtenerEgresosPorMes(LocalDate desde, LocalDate hasta);
    List<FlujoNetoPorMesResponse> obtenerFlujoNetoPorMes(LocalDate desde, LocalDate hasta);

    TendenciasDashboardResponse obtenerTendencias(LocalDate desde, LocalDate hasta);

    List<RentabilidadProductoResponse> obtenerRentabilidadProductos(LocalDate desde, LocalDate hasta);
}