package com.runicsoft.bencolapp.entregas.service;

import com.runicsoft.bencolapp.entregas.dtos.request.ActualizarEstadoEntregaRequest;
import com.runicsoft.bencolapp.entregas.dtos.request.AsignarEntregaRequest;
import com.runicsoft.bencolapp.entregas.dtos.request.ObservacionEntregaRequest;
import com.runicsoft.bencolapp.entregas.dtos.response.EntregaResponse;
import com.runicsoft.bencolapp.entregas.dtos.response.HistorialEntregaResponse;
import com.runicsoft.bencolapp.entregas.dtos.response.ResponsableEntregaResponse;
import com.runicsoft.bencolapp.entregas.utils.EstadoEntrega;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;
import com.runicsoft.bencolapp.ventas.models.Venta;

public interface EntregaService {

    void crearParaVenta(Venta venta);
    void alAnularVenta(Venta venta);
    PaginaResponse<EntregaResponse> listar(int pagina, int tamanio, EstadoEntrega estado);
    EntregaResponse obtener(Long id);
    PaginaResponse<ResponsableEntregaResponse> responsables(int pagina, int tamanio);
    EntregaResponse asignar(Long id, AsignarEntregaRequest request);
    EntregaResponse actualizarEstado(Long id, ActualizarEstadoEntregaRequest request);
    EntregaResponse registrarObservacion(Long id, ObservacionEntregaRequest request);
    PaginaResponse<HistorialEntregaResponse> historial(Long id, int pagina, int tamanio);
}