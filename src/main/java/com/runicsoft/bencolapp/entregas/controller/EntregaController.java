package com.runicsoft.bencolapp.entregas.controller;

import com.runicsoft.bencolapp.entregas.dtos.request.ActualizarEstadoEntregaRequest;
import com.runicsoft.bencolapp.entregas.dtos.request.AsignarEntregaRequest;
import com.runicsoft.bencolapp.entregas.dtos.request.ObservacionEntregaRequest;
import com.runicsoft.bencolapp.entregas.dtos.response.EntregaResponse;
import com.runicsoft.bencolapp.entregas.dtos.response.HistorialEntregaResponse;
import com.runicsoft.bencolapp.entregas.dtos.response.ResponsableEntregaResponse;
import com.runicsoft.bencolapp.entregas.service.EntregaService;
import com.runicsoft.bencolapp.entregas.utils.EstadoEntrega;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bencol.agua/entregas")
@RequiredArgsConstructor
public class EntregaController {

    private final EntregaService entregaService;

    @GetMapping
    public ResponseEntity<PaginaResponse<EntregaResponse>> listar(@RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "20") int tamanio, @RequestParam(required = false) EstadoEntrega estado) {
        return ResponseEntity.ok(
                entregaService.listar(pagina, tamanio, estado)
        );
    }

    @GetMapping("/responsables")
    public ResponseEntity<PaginaResponse<ResponsableEntregaResponse>> responsables(@RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "20") int tamanio) {
        return ResponseEntity.ok(
                entregaService.responsables(pagina, tamanio)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntregaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(
                entregaService.obtener(id)
        );
    }

    @PatchMapping("/{id}/asignacion")
    public ResponseEntity<EntregaResponse> asignar(@PathVariable Long id, @Valid @RequestBody AsignarEntregaRequest request) {
        return ResponseEntity.ok(
                entregaService.asignar(id, request)
        );
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<EntregaResponse> actualizarEstado(@PathVariable Long id, @Valid @RequestBody ActualizarEstadoEntregaRequest request) {
        return ResponseEntity.ok(
                entregaService.actualizarEstado(id, request)
        );
    }

    @PostMapping("/{id}/observaciones")
    public ResponseEntity<EntregaResponse> registrarObservacion(@PathVariable Long id, @Valid @RequestBody ObservacionEntregaRequest request) {
        return ResponseEntity.ok(
                entregaService.registrarObservacion(id, request)
        );
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<PaginaResponse<HistorialEntregaResponse>>
    historial(@PathVariable Long id, @RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "20") int tamanio) {
        return ResponseEntity.ok(
                entregaService.historial(id, pagina, tamanio)
        );
    }
}