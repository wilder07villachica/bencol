package com.runicsoft.bencolapp.inventario.controller;

import com.runicsoft.bencolapp.inventario.dtos.request.InventarioRequest;
import com.runicsoft.bencolapp.inventario.dtos.request.MovimientoInventarioRequest;
import com.runicsoft.bencolapp.inventario.dtos.response.InventarioResponse;
import com.runicsoft.bencolapp.inventario.dtos.response.MovimientoInventarioResponse;
import com.runicsoft.bencolapp.inventario.service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bencol.agua/inventarios")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping
    public ResponseEntity<List<InventarioResponse>> findAll() {
        return ResponseEntity.ok(inventarioService.findAll());
    }

    @GetMapping("/{idInventario}")
    public ResponseEntity<InventarioResponse> findById(@PathVariable Long idInventario) {
        return ResponseEntity.ok(
                inventarioService.findById(idInventario)
        );
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<InventarioResponse> findByProductoId(@PathVariable Long productoId) {
        return ResponseEntity.ok(
                inventarioService.findByProductoId(productoId)
        );
    }

    @PostMapping
    public ResponseEntity<InventarioResponse> create(@Valid @RequestBody InventarioRequest request) {
        InventarioResponse response = inventarioService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/movimientos")
    public ResponseEntity<MovimientoInventarioResponse> registrarMovimiento(@Valid @RequestBody MovimientoInventarioRequest request) {
        MovimientoInventarioResponse response = inventarioService.registrarMovimiento(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/producto/{productoId}/movimientos")
    public ResponseEntity<List<MovimientoInventarioResponse>>
    findMovimientosByProductoId(@PathVariable Long productoId) {
        return ResponseEntity.ok(
                inventarioService.findMovimientosByProductoId(productoId)
        );
    }
}