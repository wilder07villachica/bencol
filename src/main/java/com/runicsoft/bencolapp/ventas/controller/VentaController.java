package com.runicsoft.bencolapp.ventas.controller;

import com.runicsoft.bencolapp.ventas.dtos.request.VentaRequest;
import com.runicsoft.bencolapp.ventas.dtos.response.VentaResponse;
import com.runicsoft.bencolapp.ventas.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bencol.agua/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @GetMapping
    public ResponseEntity<List<VentaResponse>> findAll() {
        return ResponseEntity.ok(ventaService.findAll());
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