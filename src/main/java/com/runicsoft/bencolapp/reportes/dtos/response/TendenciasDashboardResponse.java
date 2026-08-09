package com.runicsoft.bencolapp.reportes.dtos.response;

import lombok.Data;

@Data
public class TendenciasDashboardResponse {
    private ComparacionPeriodoResponse ventas;
    private ComparacionPeriodoResponse ingresos;
    private ComparacionPeriodoResponse egresos;
    private ComparacionPeriodoResponse flujoNeto;
}