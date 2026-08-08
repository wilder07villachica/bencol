package com.runicsoft.bencolapp.reportes.dtos.response;

import lombok.Data;

@Data
public class DashboardResponse {
    private ResumenVentasResponse ventas;
    private ResumenFinanzasResponse finanzas;
    private ResumenCajaResponse caja;
    private ResumenComprasResponse compras;
    private ResumenInventarioResponse inventario;
}