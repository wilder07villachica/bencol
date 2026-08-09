package com.runicsoft.bencolapp.reportes.service;

import com.runicsoft.bencolapp.caja.models.MovimientoCaja;
import com.runicsoft.bencolapp.caja.repository.CajaRepository;
import com.runicsoft.bencolapp.caja.repository.MovimientoCajaRepository;
import com.runicsoft.bencolapp.caja.utils.EstadoCaja;
import com.runicsoft.bencolapp.caja.utils.TipoMovimientoCaja;
import com.runicsoft.bencolapp.compras.models.Compra;
import com.runicsoft.bencolapp.compras.models.DetalleCompra;
import com.runicsoft.bencolapp.compras.repository.CompraRepository;
import com.runicsoft.bencolapp.compras.repository.DetalleCompraRepository;
import com.runicsoft.bencolapp.compras.utils.EstadoCompra;
import com.runicsoft.bencolapp.finanzas.models.CuentaCobrar;
import com.runicsoft.bencolapp.finanzas.models.CuentaPagar;
import com.runicsoft.bencolapp.finanzas.models.Pago;
import com.runicsoft.bencolapp.finanzas.models.PagoProveedor;
import com.runicsoft.bencolapp.finanzas.repository.CuentaCobrarRepository;
import com.runicsoft.bencolapp.finanzas.repository.CuentaPagarRepository;
import com.runicsoft.bencolapp.finanzas.repository.PagoProveedorRepository;
import com.runicsoft.bencolapp.finanzas.repository.PagoRepository;
import com.runicsoft.bencolapp.finanzas.utils.EstadoCuenta;
import com.runicsoft.bencolapp.finanzas.utils.EstadoCuentaPagar;
import com.runicsoft.bencolapp.inventario.models.Inventario;
import com.runicsoft.bencolapp.inventario.repository.InventarioRepository;
import com.runicsoft.bencolapp.reportes.dtos.response.*;
import com.runicsoft.bencolapp.ventas.models.DetalleVenta;
import com.runicsoft.bencolapp.ventas.models.Venta;
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

        LocalDateTime fechaInicio = desde.atStartOfDay();

        LocalDateTime fechaFin = hasta.plusDays(1).atStartOfDay();
        List<Venta> ventas = ventaRepository
                .findByFechaCreacionGreaterThanEqualAndFechaCreacionLessThan(fechaInicio, fechaFin)
                .stream()
                .filter(venta ->
                        venta.getEstado() != EstadoVenta.ANULADA)
                .toList();

        Map<Long, ProductoMasVendidoResponse> productos = new HashMap<>();

        for (Venta venta : ventas) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                Long productoId = detalle.getProducto().getId();

                ProductoMasVendidoResponse reporte = productos.computeIfAbsent(
                        productoId, id -> {
                            ProductoMasVendidoResponse nuevo = new ProductoMasVendidoResponse();
                            nuevo.setProductoId(detalle.getProducto().getId());
                            nuevo.setCodigoProducto(detalle.getProducto().getCodigo());
                            nuevo.setDescripcionProducto(detalle.getProducto().getDescripcion());
                            nuevo.setCantidadVendida(0L);
                            nuevo.setUnidadesFisicasVendidas(0L);
                            nuevo.setTotalVendido(BigDecimal.ZERO);
                            return nuevo;
                        }
                );

                long cantidadPaquetes = detalle.getCantidad().longValue();
                long unidadesFisicas = cantidadPaquetes * detalle.getProducto().getUnidadesPorPaquete();
                reporte.setCantidadVendida(reporte.getCantidadVendida() + cantidadPaquetes);
                reporte.setUnidadesFisicasVendidas(reporte.getUnidadesFisicasVendidas() + unidadesFisicas);
                reporte.setTotalVendido(reporte.getTotalVendido().add(detalle.getSubtotal()));
            }
        }

        return productos.values()
                .stream()
                .sorted(
                        Comparator.comparing(ProductoMasVendidoResponse::getCantidadVendida)
                                .reversed())
                .limit(limite)
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

        LocalDateTime fechaInicio = desde.atStartOfDay();
        LocalDateTime fechaFin = hasta.plusDays(1).atStartOfDay();
        List<Venta> ventas = ventaRepository
                .findByFechaCreacionGreaterThanEqualAndFechaCreacionLessThan(fechaInicio, fechaFin)
                .stream()
                .filter(venta -> venta.getEstado() != EstadoVenta.ANULADA)
                .toList();

        Map<YearMonth, List<Venta>> ventasPorMes = ventas.stream()
                .collect(
                        Collectors.groupingBy(
                                venta ->
                                        YearMonth.from(venta.getFechaCreacion())
                        )
                );

        List<VentaPorMesResponse> resultado = new ArrayList<>();

        for (YearMonth periodo : generarMeses(desde, hasta)) {
            List<Venta> ventasMes = ventasPorMes.getOrDefault(periodo, List.of());

            BigDecimal totalVendido = ventasMes.stream()
                    .map(Venta::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            VentaPorMesResponse response = new VentaPorMesResponse();
            response.setAnio(periodo.getYear());
            response.setMes(periodo.getMonthValue());
            response.setCantidadVentas((long) ventasMes.size());
            response.setTotalVendido(totalVendido);
            resultado.add(response);
        }
        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngresoPorMesResponse> obtenerIngresosPorMes(LocalDate desde, LocalDate hasta) {
        validarFechas(desde, hasta);

        LocalDateTime fechaInicio = desde.atStartOfDay();
        LocalDateTime fechaFin = hasta.plusDays(1).atStartOfDay();
        List<Pago> pagos = pagoRepository
                .findByFechaPagoGreaterThanEqualAndFechaPagoLessThan(fechaInicio, fechaFin);

        Map<YearMonth, List<Pago>> pagosPorMes =
                pagos.stream()
                        .collect(
                                Collectors.groupingBy(
                                        pago ->
                                                YearMonth.from(pago.getFechaPago())
                                )
                        );

        List<IngresoPorMesResponse> resultado = new ArrayList<>();

        for (YearMonth periodo : generarMeses(desde, hasta)) {
            List<Pago> pagosMes =
                    pagosPorMes.getOrDefault(periodo, List.of());

            BigDecimal totalIngresos =
                    pagosMes.stream()
                            .map(Pago::getMonto)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

            IngresoPorMesResponse response = new IngresoPorMesResponse();
            response.setAnio(periodo.getYear());
            response.setMes(periodo.getMonthValue());
            response.setTotalIngresos(totalIngresos);
            resultado.add(response);
        }
        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EgresoPorMesResponse> obtenerEgresosPorMes(LocalDate desde, LocalDate hasta) {
        validarFechas(desde, hasta);

        LocalDateTime fechaInicio = desde.atStartOfDay();
        LocalDateTime fechaFin = hasta.plusDays(1).atStartOfDay();

        List<MovimientoCaja> movimientos = movimientoCajaRepository
                .findByFechaMovimientoGreaterThanEqualAndFechaMovimientoLessThan(fechaInicio, fechaFin)
                .stream()
                .filter(movimiento -> movimiento.getTipoMovimiento() == TipoMovimientoCaja.EGRESO)
                .toList();

        Map<YearMonth, List<MovimientoCaja>> egresosPorMes = movimientos.stream()
                .collect(
                        Collectors.groupingBy(
                                movimiento ->
                                        YearMonth.from(movimiento.getFechaMovimiento())
                        )
                );

        List<EgresoPorMesResponse> resultado = new ArrayList<>();

        for (YearMonth periodo : generarMeses(desde, hasta)) {
            List<MovimientoCaja> movimientosMes = egresosPorMes.getOrDefault(periodo, List.of());

            BigDecimal totalEgresos = movimientosMes.stream()
                    .map(MovimientoCaja::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            EgresoPorMesResponse response = new EgresoPorMesResponse();
            response.setAnio(periodo.getYear());
            response.setMes(periodo.getMonthValue());
            response.setTotalEgresos(totalEgresos);
            resultado.add(response);
        }
        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlujoNetoPorMesResponse> obtenerFlujoNetoPorMes(LocalDate desde, LocalDate hasta) {
        validarFechas(desde, hasta);

        LocalDateTime fechaInicio = desde.atStartOfDay();
        LocalDateTime fechaFin = hasta.plusDays(1).atStartOfDay();
        List<MovimientoCaja> movimientos = movimientoCajaRepository
                .findByFechaMovimientoGreaterThanEqualAndFechaMovimientoLessThan(fechaInicio, fechaFin);

        Map<YearMonth, List<MovimientoCaja>> movimientosPorMes = movimientos.stream()
                .collect(
                        Collectors.groupingBy(
                                movimiento ->
                                        YearMonth.from(movimiento.getFechaMovimiento())
                        )
                );

        List<FlujoNetoPorMesResponse> resultado = new ArrayList<>();

        for (YearMonth periodo : generarMeses(desde, hasta)) {
            List<MovimientoCaja> movimientosMes = movimientosPorMes.getOrDefault(periodo, List.of());

            BigDecimal ingresos = movimientosMes.stream()
                    .filter(movimiento -> movimiento.getTipoMovimiento() == TipoMovimientoCaja.INGRESO)
                    .map(MovimientoCaja::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal egresos = movimientosMes.stream()
                    .filter(movimiento -> movimiento.getTipoMovimiento() == TipoMovimientoCaja.EGRESO)
                    .map(MovimientoCaja::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            FlujoNetoPorMesResponse response = new FlujoNetoPorMesResponse();
            response.setAnio(periodo.getYear());
            response.setMes(periodo.getMonthValue());
            response.setTotalIngresos(ingresos);
            response.setTotalEgresos(egresos);
            response.setFlujoNeto(ingresos.subtract(egresos));
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
        List<Venta> ventas = ventaRepository
                .findByFechaCreacionGreaterThanEqualAndFechaCreacionLessThan(desde, hasta);
        List<Venta> ventasValidas = ventas.stream()
                .filter(venta ->
                        venta.getEstado() != EstadoVenta.ANULADA)
                .toList();

        BigDecimal totalVendido =
                ventasValidas.stream().map(Venta::getTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        long cantidadVentas = ventasValidas.size();

        BigDecimal ticketPromedio = cantidadVentas > 0 ? totalVendido.divide(BigDecimal.valueOf(cantidadVentas), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        ResumenVentasResponse response = new ResumenVentasResponse();

        response.setTotalVendido(totalVendido);
        response.setCantidadVentas(cantidadVentas);
        response.setTicketPromedio(ticketPromedio);

        return response;
    }

    private ResumenFinanzasResponse obtenerResumenFinanzas(LocalDateTime desde, LocalDateTime hasta) {
        List<Pago> pagosClientes = pagoRepository
                .findByFechaPagoGreaterThanEqualAndFechaPagoLessThan(desde, hasta);

        List<PagoProveedor> pagosProveedores = pagoProveedorRepository
                .findByFechaPagoGreaterThanEqualAndFechaPagoLessThan(desde, hasta);

        BigDecimal totalCobrado = pagosClientes.stream()
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPagadoProveedores = pagosProveedores.stream()
                .map(PagoProveedor::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<CuentaCobrar> cuentasCobrar = cuentaCobrarRepository.findAll();
        List<CuentaPagar> cuentasPagar = cuentaPagarRepository.findAll();

        BigDecimal totalPorCobrar = cuentasCobrar.stream()
                .filter(cuenta ->
                        cuenta.getEstado() == EstadoCuenta.PENDIENTE || cuenta.getEstado() == EstadoCuenta.PARCIAL)
                .map(CuentaCobrar::getSaldoPendiente)
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
        List<Compra> compras = compraRepository
                .findByFechaCreacionGreaterThanEqualAndFechaCreacionLessThan(desde, hasta);

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

    private void validarFechas(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new IllegalArgumentException("Las fechas desde y hasta son obligatorias.");
        }

        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la fecha final.");
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
        return ventaRepository.findByFechaCreacionGreaterThanEqualAndFechaCreacionLessThan(inicio, fin)
                .stream()
                .filter(venta ->
                        venta.getEstado() != EstadoVenta.ANULADA
                )
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularTotalIngresos(LocalDate desde, LocalDate hasta) {
        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.plusDays(1).atStartOfDay();

        return movimientoCajaRepository.findByFechaMovimientoGreaterThanEqualAndFechaMovimientoLessThan(inicio, fin)
                .stream()
                .filter(movimiento ->
                        movimiento.getTipoMovimiento() == TipoMovimientoCaja.INGRESO)
                .map(MovimientoCaja::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularTotalEgresos(LocalDate desde, LocalDate hasta) {
        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.plusDays(1).atStartOfDay();

        return movimientoCajaRepository.findByFechaMovimientoGreaterThanEqualAndFechaMovimientoLessThan(inicio, fin)
                .stream()
                .filter(movimiento ->
                        movimiento.getTipoMovimiento() == TipoMovimientoCaja.EGRESO)
                .map(MovimientoCaja::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
}