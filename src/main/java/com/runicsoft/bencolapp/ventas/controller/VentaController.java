package com.runicsoft.bencolapp.ventas.controller;

import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;
import com.runicsoft.bencolapp.ventas.dtos.request.VentaRequest;
import com.runicsoft.bencolapp.ventas.dtos.response.VentaResponse;
import com.runicsoft.bencolapp.ventas.service.VentaService;
import com.runicsoft.bencolapp.ventas.utils.EstadoVenta;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/bencol.agua/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @GetMapping
    public ResponseEntity<PaginaResponse<VentaResponse>> findAll(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio,
            @RequestParam(required = false) String codigo,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) EstadoVenta estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        return ResponseEntity.ok(ventaService.findAll(pagina, tamanio, codigo, clienteId, estado, desde, hasta));
    }

    @GetMapping("/{idVenta}")
    public ResponseEntity<VentaResponse> findById(@PathVariable Long idVenta) {
        return ResponseEntity.ok(ventaService.findById(idVenta));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<VentaResponse> findByCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(ventaService.findByCodigo(codigo));
    }

    @PostMapping
    public ResponseEntity<VentaResponse> create(@Valid @RequestBody VentaRequest request) {
        VentaResponse response = ventaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{idVenta}/anular")
    public ResponseEntity<VentaResponse> anularVenta(@PathVariable Long idVenta) {
        VentaResponse response = ventaService.anularVenta(idVenta);
        return ResponseEntity.ok(response);
    }
}