package com.runicsoft.bencolapp.reportes.service;

import com.runicsoft.bencolapp.caja.models.MovimientoCaja;
import com.runicsoft.bencolapp.caja.repository.CajaRepository;
import com.runicsoft.bencolapp.caja.repository.MovimientoCajaRepository;
import com.runicsoft.bencolapp.caja.utils.EstadoCaja;
import com.runicsoft.bencolapp.caja.utils.TipoMovimientoCaja;
import com.runicsoft.bencolapp.compras.models.DetalleCompra;
import com.runicsoft.bencolapp.compras.repository.CompraRepository;
import com.runicsoft.bencolapp.compras.repository.DetalleCompraRepository;
import com.runicsoft.bencolapp.compras.utils.EstadoCompra;
import com.runicsoft.bencolapp.finanzas.models.CuentaCobrar;
import com.runicsoft.bencolapp.finanzas.repository.CuentaCobrarRepository;
import com.runicsoft.bencolapp.finanzas.repository.CuentaPagarRepository;
import com.runicsoft.bencolapp.finanzas.repository.PagoProveedorRepository;
import com.runicsoft.bencolapp.finanzas.repository.PagoRepository;
import com.runicsoft.bencolapp.finanzas.utils.EstadoCuenta;
import com.runicsoft.bencolapp.finanzas.utils.EstadoCuentaPagar;
import com.runicsoft.bencolapp.inventario.models.Inventario;
import com.runicsoft.bencolapp.inventario.repository.InventarioRepository;
import com.runicsoft.bencolapp.reportes.dtos.response.*;
import com.runicsoft.bencolapp.utils.constants.MessageConstants;
import com.runicsoft.bencolapp.ventas.models.DetalleVenta;
import com.runicsoft.bencolapp.ventas.models.Venta;
import com.runicsoft.bencolapp.ventas.repository.DetalleVentaRepository;
import com.runicsoft.bencolapp.ventas.repository.VentaRepository;
import com.runicsoft.bencolapp.ventas.utils.EstadoVenta;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final VentaRepository ventaRepository;
    private final CuentaCobrarRepository cuentaCobrarRepository;
    private final CuentaPagarRepository cuentaPagarRepository;
    private final CajaRepository cajaRepository;
    private final CompraRepository compraRepository;
    private final InventarioRepository inventarioRepository;

    private final PagoRepository pagoRepository;
    private final PagoProveedorRepository pagoProveedorRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;

    private final DetalleCompraRepository detalleCompraRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    private static @NonNull Map<Long, ClienteDeudaResponse> getLongClienteDeudaResponseMap(List<CuentaCobrar> cuentas) {
        Map<Long, ClienteDeudaResponse> clientes = new HashMap<>();

        for (CuentaCobrar cuenta : cuentas) {
            Long clienteId = cuenta.getVenta()
                    .getCliente()
                    .getId();

            ClienteDeudaResponse response = clientes.computeIfAbsent(clienteId, id -> {
                ClienteDeudaResponse nuevo = new ClienteDeudaResponse();
                nuevo.setClienteId(clienteId);
                nuevo.setNombreCliente(cuenta.getVenta().getCliente().getNombre());
                nuevo.setDeudaTotal(BigDecimal.ZERO);
                nuevo.setCantidadCuentasPendientes(0L);
                return nuevo;
            });

            response.setDeudaTotal(response.getDeudaTotal().add(cuenta.getSaldoPendiente()));
            response.setCantidadCuentasPendientes(response.getCantidadCuentasPendientes() + 1);
        }
        return clientes;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse obtenerDashboard(LocalDate desde, LocalDate hasta) {
        validarFechas(desde, hasta);
        LocalDateTime fechaInicio = desde.atStartOfDay();
        LocalDateTime fechaFin = hasta.plusDays(1).atStartOfDay();
        DashboardResponse response = new DashboardResponse();
        response.setVentas(obtenerResumenVentas(fechaInicio, fechaFin));
        response.setFinanzas(obtenerResumenFinanzas(fechaInicio, fechaFin));
        response.setCaja(obtenerResumenCaja(fechaInicio, fechaFin));
        response.setCompras(obtenerResumenCompras(fechaInicio, fechaFin));
        response.setInventario(obtenerResumenInventario());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoMasVendidoResponse> obtenerProductosMasVendidos(LocalDate desde, LocalDate hasta, Integer limite) {
        validarFechas(desde, hasta);

        if (limite == null || limite <= 0) {
            limite = 10;
        }

        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.plusDays(1).atStartOfDay();

        List<Object[]> resultados = detalleVentaRepository.findProductosMasVendidos(inicio, fin, EstadoVenta.ANULADA);

        return resultados.stream()
                .limit(limite)
                .map(fila -> {
                    ProductoMasVendidoResponse response = new ProductoMasVendidoResponse();
                    response.setProductoId(((Number) fila[0]).longValue());
                    response.setCodigoProducto((String) fila[1]);
                    response.setDescripcionProducto((String) fila[2]);
                    response.setCantidadVendida(((Number) fila[3]).longValue());
                    response.setUnidadesFisicasVendidas(((Number) fila[4]).longValue());
                    response.setTotalVendido((BigDecimal) fila[5]);
                    return response;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaPorDiaResponse> obtenerVentasPorDia(LocalDate desde, LocalDate hasta) {
        validarFechas(desde, hasta);
        LocalDateTime fechaInicio = desde.atStartOfDay();
        LocalDateTime fechaFin = hasta.plusDays(1).atStartOfDay();
        List<Venta> ventas = ventaRepository
                .findByFechaCreacionGreaterThanEqualAndFechaCreacionLessThan(fechaInicio, fechaFin)
                .stream()
                .filter(venta ->
                        venta.getEstado() != EstadoVenta.ANULADA)
                .toList();

        Map<LocalDate, List<Venta>> ventasPorFecha = ventas.stream()
                .collect(Collectors.groupingBy(
                        venta ->
                                venta.getFechaCreacion().toLocalDate())
                );

        return ventasPorFecha.entrySet()
                .stream()
                .map(entry -> {
                    LocalDate fecha = entry.getKey();
                    List<Venta> ventasDia = entry.getValue();
                    BigDecimal totalVendido = ventasDia.stream()
                            .map(Venta::getTotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    VentaPorDiaResponse response = new VentaPorDiaResponse();
                    response.setFecha(fecha);
                    response.setCantidadVentas((long) ventasDia.size());
                    response.setTotalVendido(totalVendido);
                    return response;
                })
                .sorted(Comparator.comparing(VentaPorDiaResponse::getFecha))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDeudaResponse> obtenerClientesConMayorDeuda(Integer limite) {

        if (limite == null || limite <= 0) {
            limite = 10;
        }

        List<CuentaCobrar> cuentas = cuentaCobrarRepository.findAll()
                .stream()
                .filter(cuenta ->
                        cuenta.getEstado() == EstadoCuenta.PENDIENTE || cuenta.getEstado() == EstadoCuenta.PARCIAL)
                .toList();

        Map<Long, ClienteDeudaResponse> clientes = getLongClienteDeudaResponseMap(cuentas);

        return clientes.values()
                .stream()
                .sorted(
                        Comparator.comparing(
                                ClienteDeudaResponse::getDeudaTotal
                        ).reversed())
                .limit(limite)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoStockBajoResponse> obtenerProductosStockBajo() {
        List<Inventario> inventarios = inventarioRepository.findAll();
        return inventarios.stream()
                .filter(inventario ->
                        inventario.getStockActual() <= inventario.getStockMinimo()
                )
                .map(inventario -> {
                    ProductoStockBajoResponse response = new ProductoStockBajoResponse();
                    response.setProductoId(inventario.getProducto().getId());
                    response.setCodigoProducto(inventario.getProducto().getCodigo());
                    response.setDescripcionProducto(inventario.getProducto().getDescripcion());
                    response.setStockActual(inventario.getStockActual());
                    response.setStockMinimo(inventario.getStockMinimo());
                    response.setDiferenciaStock(inventario.getStockActual() - inventario.getStockMinimo());
                    return response;
                })
                .sorted(
                        Comparator.comparing(ProductoStockBajoResponse::getDiferenciaStock)
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaPorMesResponse> obtenerVentasPorMes(LocalDate desde, LocalDate hasta) {
        validarFechas(desde, hasta);
        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.plusDays(1).atStartOfDay();
        List<Object[]> resultados = ventaRepository.findVentasAgrupadasPorMes(inicio, fin, EstadoVenta.ANULADA);

        Map<YearMonth, VentaPorMesResponse> mapa = new HashMap<>();
        for (Object[] fila : resultados) {
            int anio = ((Number) fila[0]).intValue();
            int mes = ((Number) fila[1]).intValue();
            Long cantidad = ((Number) fila[2]).longValue();
            BigDecimal total = (BigDecimal) fila[3];

            VentaPorMesResponse response = new VentaPorMesResponse();

            response.setAnio(anio);
            response.setMes(mes);
            response.setCantidadVentas(cantidad);
            response.setTotalVendido(total);

            mapa.put(YearMonth.of(anio, mes), response);
        }

        List<VentaPorMesResponse> resultado = new ArrayList<>();

        for (YearMonth periodo : generarMeses(desde, hasta)) {
            VentaPorMesResponse response = mapa.get(periodo);
            if (response == null) {
                response = new VentaPorMesResponse();
                response.setAnio(periodo.getYear());
                response.setMes(periodo.getMonthValue());
                response.setCantidadVentas(0L);
                response.setTotalVendido(BigDecimal.ZERO);
            }
            resultado.add(response);
        }

        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngresoPorMesResponse> obtenerIngresosPorMes(LocalDate desde, LocalDate hasta) {
        validarFechas(desde, hasta);
        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.plusDays(1).atStartOfDay();

        List<Object[]> resultados = pagoRepository.findIngresosAgrupadosPorMes(inicio, fin);
        Map<YearMonth, BigDecimal> mapa = new HashMap<>();

        for (Object[] fila : resultados) {
            int anio = ((Number) fila[0]).intValue();
            int mes = ((Number) fila[1]).intValue();
            BigDecimal total = (BigDecimal) fila[2];
            mapa.put(YearMonth.of(anio, mes), total);
        }

        List<IngresoPorMesResponse> resultado = new ArrayList<>();

        for (YearMonth periodo : generarMeses(desde, hasta)) {
            IngresoPorMesResponse response = new IngresoPorMesResponse();
            response.setAnio(periodo.getYear());
            response.setMes(periodo.getMonthValue());
            response.setTotalIngresos(mapa.getOrDefault(periodo, BigDecimal.ZERO));
            resultado.add(response);
        }

        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EgresoPorMesResponse> obtenerEgresosPorMes(LocalDate desde, LocalDate hasta) {
        validarFechas(desde, hasta);
        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.plusDays(1).atStartOfDay();

        List<Object[]> resultados = movimientoCajaRepository.findMovimientosAgrupadosPorMes(inicio, fin, TipoMovimientoCaja.EGRESO);
        Map<YearMonth, BigDecimal> mapa = new HashMap<>();

        for (Object[] fila : resultados) {
            int anio = ((Number) fila[0]).intValue();
            int mes = ((Number) fila[1]).intValue();
            BigDecimal total = (BigDecimal) fila[2];
            mapa.put(YearMonth.of(anio, mes), total);
        }

        List<EgresoPorMesResponse> resultado = new ArrayList<>();

        for (YearMonth periodo : generarMeses(desde, hasta)) {
            EgresoPorMesResponse response = new EgresoPorMesResponse();
            response.setAnio(periodo.getYear());
            response.setMes(periodo.getMonthValue());
            response.setTotalEgresos(mapa.getOrDefault(periodo, BigDecimal.ZERO));
            resultado.add(response);
        }

        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlujoNetoPorMesResponse> obtenerFlujoNetoPorMes(LocalDate desde, LocalDate hasta) {
        validarFechas(desde, hasta);
        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.plusDays(1).atStartOfDay();

        List<Object[]> ingresos = movimientoCajaRepository.findMovimientosAgrupadosPorMes(inicio, fin, TipoMovimientoCaja.INGRESO);
        List<Object[]> egresos = movimientoCajaRepository.findMovimientosAgrupadosPorMes(inicio, fin, TipoMovimientoCaja.EGRESO);

        Map<YearMonth, BigDecimal> mapaIngresos = convertirResultadosMensuales(ingresos);
        Map<YearMonth, BigDecimal> mapaEgresos = convertirResultadosMensuales(egresos);

        List<FlujoNetoPorMesResponse> resultado = new ArrayList<>();

        for (YearMonth periodo : generarMeses(desde, hasta)) {
            BigDecimal totalIngresos = mapaIngresos.getOrDefault(periodo, BigDecimal.ZERO);
            BigDecimal totalEgresos = mapaEgresos.getOrDefault(periodo, BigDecimal.ZERO);

            FlujoNetoPorMesResponse response = new FlujoNetoPorMesResponse();
            response.setAnio(periodo.getYear());
            response.setMes(periodo.getMonthValue());
            response.setTotalIngresos(totalIngresos);
            response.setTotalEgresos(totalEgresos);
            response.setFlujoNeto(totalIngresos.subtract(totalEgresos));
            resultado.add(response);
        }

        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public TendenciasDashboardResponse obtenerTendencias(LocalDate desde, LocalDate hasta) {
        validarFechas(desde, hasta);

        LocalDate[] periodoAnterior = calcularPeriodoAnterior(desde, hasta);
        LocalDate desdeAnterior = periodoAnterior[0];
        LocalDate hastaAnterior = periodoAnterior[1];
        BigDecimal ventasActuales = calcularTotalVentas(desde, hasta);
        BigDecimal ventasAnteriores = calcularTotalVentas(desdeAnterior, hastaAnterior);
        BigDecimal ingresosActuales = calcularTotalIngresos(desde, hasta);
        BigDecimal ingresosAnteriores = calcularTotalIngresos(desdeAnterior, hastaAnterior);
        BigDecimal egresosActuales = calcularTotalEgresos(desde, hasta);
        BigDecimal egresosAnteriores = calcularTotalEgresos(desdeAnterior, hastaAnterior);
        BigDecimal flujoActual = ingresosActuales.subtract(egresosActuales);
        BigDecimal flujoAnterior = ingresosAnteriores.subtract(egresosAnteriores);

        TendenciasDashboardResponse response = new TendenciasDashboardResponse();
        response.setVentas(crearComparacion(ventasActuales, ventasAnteriores));
        response.setIngresos(crearComparacion(ingresosActuales, ingresosAnteriores));
        response.setEgresos(crearComparacion(egresosActuales, egresosAnteriores));
        response.setFlujoNeto(crearComparacion(flujoActual, flujoAnterior));

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentabilidadProductoResponse> obtenerRentabilidadProductos(LocalDate desde, LocalDate hasta) {
        validarFechas(desde, hasta);

        LocalDateTime fechaInicio = desde.atStartOfDay();
        LocalDateTime fechaFin = hasta.plusDays(1).atStartOfDay();
        List<Venta> ventas = ventaRepository
                .findByFechaCreacionGreaterThanEqualAndFechaCreacionLessThan(fechaInicio, fechaFin)
                .stream()
                .filter(venta -> venta.getEstado() != EstadoVenta.ANULADA)
                .toList();

        Map<Long, RentabilidadProductoResponse> productos = new HashMap<>();

        for (Venta venta : ventas) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                Long productoId = detalle.getProducto().getId();
                RentabilidadProductoResponse reporte = productos.computeIfAbsent(
                        productoId,
                        id -> {
                            RentabilidadProductoResponse nuevo = new RentabilidadProductoResponse();
                            nuevo.setProductoId(detalle.getProducto().getId());
                            nuevo.setCodigoProducto(detalle.getProducto().getCodigo());
                            nuevo.setDescripcionProducto(detalle.getProducto().getDescripcion());
                            nuevo.setCantidadVendida(0L);
                            nuevo.setUnidadesFisicasVendidas(0L);
                            nuevo.setTotalVentas(BigDecimal.ZERO);
                            return nuevo;
                        });

                long cantidadComercial = detalle.getCantidad().longValue();

                long unidadesFisicas = cantidadComercial * detalle.getProducto().getUnidadesPorPaquete();
                reporte.setCantidadVendida(reporte.getCantidadVendida() + cantidadComercial);
                reporte.setUnidadesFisicasVendidas(reporte.getUnidadesFisicasVendidas() + unidadesFisicas);
                reporte.setTotalVentas(reporte.getTotalVentas().add(detalle.getSubtotal()));
            }
        }

        for (RentabilidadProductoResponse reporte : productos.values()) {
            BigDecimal costoPromedio = calcularCostoPromedioProducto(reporte.getProductoId());
            BigDecimal costoEstimado = costoPromedio.multiply(BigDecimal.valueOf(reporte.getUnidadesFisicasVendidas()));
            BigDecimal margen = reporte.getTotalVentas().subtract(costoEstimado);
            BigDecimal margenPorcentual = BigDecimal.ZERO;
            if (reporte.getTotalVentas().compareTo(BigDecimal.ZERO) > 0) {
                margenPorcentual = margen
                        .divide(reporte.getTotalVentas(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
            }

            reporte.setCostoPromedioUnitario(costoPromedio);
            reporte.setCostoEstimado(costoEstimado.setScale(2, RoundingMode.HALF_UP));
            reporte.setMargenEstimado(margen.setScale(2, RoundingMode.HALF_UP));
            reporte.setMargenPorcentual(margenPorcentual.setScale(2, RoundingMode.HALF_UP));
        }

        return productos.values()
                .stream()
                .sorted(
                        Comparator.comparing(RentabilidadProductoResponse::getMargenEstimado)
                                .reversed()
                )
                .toList();
    }

    // métodos auxiliares...
    private ResumenVentasResponse obtenerResumenVentas(LocalDateTime desde, LocalDateTime hasta) {
        BigDecimal totalVendido = ventaRepository.sumTotalVentas(desde, hasta, EstadoVenta.ANULADA);
        Long cantidadVentas = ventaRepository.countVentas(desde, hasta, EstadoVenta.ANULADA);
        BigDecimal ticketPromedio = cantidadVentas > 0 ? totalVendido.divide(BigDecimal.valueOf(cantidadVentas), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        ResumenVentasResponse response = new ResumenVentasResponse();
        response.setTotalVendido(totalVendido);
        response.setCantidadVentas(cantidadVentas);
        response.setTicketPromedio(ticketPromedio);
        return response;
    }

    private ResumenFinanzasResponse obtenerResumenFinanzas(LocalDateTime desde, LocalDateTime hasta) {
        BigDecimal totalCobrado = pagoRepository.sumPagosPeriodo(desde, hasta);
        BigDecimal totalPagadoProveedores = pagoProveedorRepository.sumPagosPeriodo(desde, hasta);
        BigDecimal totalPorCobrar = cuentaCobrarRepository.sumSaldoPendienteByEstados(List.of(EstadoCuenta.PENDIENTE, EstadoCuenta.PARCIAL));
        BigDecimal totalPorPagar = cuentaPagarRepository.sumSaldoPendienteByEstados(List.of(EstadoCuentaPagar.PENDIENTE, EstadoCuentaPagar.PARCIAL));
        ResumenFinanzasResponse response = new ResumenFinanzasResponse();
        response.setTotalCobrado(totalCobrado);
        response.setTotalPorCobrar(totalPorCobrar);
        response.setTotalPagadoProveedores(totalPagadoProveedores);
        response.setTotalPorPagar(totalPorPagar);
        return response;
    }

    private ResumenCajaResponse obtenerResumenCaja(LocalDateTime desde, LocalDateTime hasta) {
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

        List<MovimientoCaja> movimientos = movimientoCajaRepository
                .findByFechaMovimientoGreaterThanEqualAndFechaMovimientoLessThan(desde, hasta);

        BigDecimal ingresosPeriodo = movimientos.stream()
                .filter(movimiento ->
                        movimiento.getTipoMovimiento() == TipoMovimientoCaja.INGRESO)
                .map(MovimientoCaja::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal egresosPeriodo = movimientos.stream()
                .filter(movimiento ->
                        movimiento.getTipoMovimiento() == TipoMovimientoCaja.EGRESO)
                .map(MovimientoCaja::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        response.setIngresosPeriodo(ingresosPeriodo);
        response.setEgresosPeriodo(egresosPeriodo);
        return response;
    }

    private ResumenComprasResponse obtenerResumenCompras(LocalDateTime desde, LocalDateTime hasta) {
        BigDecimal totalComprado = compraRepository.sumTotalCompras(desde, hasta, EstadoCompra.ANULADA);
        Long cantidadCompras = compraRepository.countCompras(desde, hasta, EstadoCompra.ANULADA);
        ResumenComprasResponse response = new ResumenComprasResponse();
        response.setTotalComprado(totalComprado);
        response.setCantidadCompras(cantidadCompras);
        return response;
    }

    private ResumenInventarioResponse obtenerResumenInventario() {
        ResumenInventarioResponse response = new ResumenInventarioResponse();
        response.setCantidadProductosInventario(inventarioRepository.countInventarios());
        response.setProductosStockBajo(inventarioRepository.countStockBajo());
        return response;
    }

    private void validarFechas(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new IllegalArgumentException(MessageConstants.FECHAS_OBLIGATORIAS);
        }

        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException(MessageConstants.RANGO_FECHAS_INVALIDO);
        }
    }

    private List<YearMonth> generarMeses(LocalDate desde, LocalDate hasta) {
        List<YearMonth> meses = new ArrayList<>();

        YearMonth actual = YearMonth.from(desde);
        YearMonth ultimo = YearMonth.from(hasta);

        while (!actual.isAfter(ultimo)) {
            meses.add(actual);
            actual = actual.plusMonths(1);
        }
        return meses;
    }

    private LocalDate[] calcularPeriodoAnterior(LocalDate desde, LocalDate hasta) {
        long dias = ChronoUnit.DAYS.between(desde, hasta) + 1;
        LocalDate hastaAnterior = desde.minusDays(1);
        LocalDate desdeAnterior = hastaAnterior.minusDays(dias - 1);
        return new LocalDate[]{desdeAnterior, hastaAnterior};
    }

    private BigDecimal calcularTotalVentas(LocalDate desde, LocalDate hasta) {
        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.plusDays(1).atStartOfDay();
        return ventaRepository.sumTotalVentas(inicio, fin, EstadoVenta.ANULADA);
    }

    private BigDecimal calcularTotalIngresos(LocalDate desde, LocalDate hasta) {
        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.plusDays(1).atStartOfDay();
        return movimientoCajaRepository.sumMontoPeriodoByTipo(inicio, fin, TipoMovimientoCaja.INGRESO);
    }

    private BigDecimal calcularTotalEgresos(LocalDate desde, LocalDate hasta) {
        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.plusDays(1).atStartOfDay();
        return movimientoCajaRepository.sumMontoPeriodoByTipo(inicio, fin, TipoMovimientoCaja.EGRESO);
    }

    private ComparacionPeriodoResponse crearComparacion(BigDecimal actual, BigDecimal anterior) {
        ComparacionPeriodoResponse response = new ComparacionPeriodoResponse();
        BigDecimal diferencia = actual.subtract(anterior);
        BigDecimal porcentaje = BigDecimal.ZERO;

        if (anterior.compareTo(BigDecimal.ZERO) != 0) {
            porcentaje = diferencia.divide(anterior, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        response.setValorActual(actual);
        response.setValorAnterior(anterior);
        response.setVariacionAbsoluta(diferencia);
        response.setVariacionPorcentual(porcentaje);

        return response;
    }

    private BigDecimal calcularCostoPromedioProducto(Long productoId) {
        List<DetalleCompra> detalles = detalleCompraRepository
                .findByProductoId(productoId)
                .stream()
                .filter(detalle -> detalle.getCompra().getEstado() != EstadoCompra.ANULADA)
                .toList();

        if (detalles.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal costoTotal = detalles.stream()
                .map(DetalleCompra::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long unidadesCompradas = detalles.stream()
                .mapToLong(
                        detalle -> detalle.getCantidad().longValue())
                .sum();

        if (unidadesCompradas == 0) {
            return BigDecimal.ZERO;
        }

        return costoTotal.divide(BigDecimal.valueOf(unidadesCompradas), 4, RoundingMode.HALF_UP);
    }

    private Map<YearMonth, BigDecimal> convertirResultadosMensuales(List<Object[]> resultados) {
        Map<YearMonth, BigDecimal> mapa = new HashMap<>();

        for (Object[] fila : resultados) {
            int anio = ((Number) fila[0]).intValue();
            int mes = ((Number) fila[1]).intValue();
            BigDecimal total = (BigDecimal) fila[2];
            mapa.put(YearMonth.of(anio, mes), total);
        }

        return mapa;
    }
}