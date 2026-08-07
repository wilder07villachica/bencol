package com.runicsoft.bencolapp.compras.controller;

import com.runicsoft.bencolapp.compras.dtos.request.CompraRequest;
import com.runicsoft.bencolapp.compras.dtos.response.CompraResponse;
import com.runicsoft.bencolapp.compras.service.CompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bencol.agua/compras")
@RequiredArgsConstructor
public class CompraController {

    private final CompraService compraService;

    @GetMapping
    public ResponseEntity<List<CompraResponse>> findAll() {
        return ResponseEntity.ok(compraService.findAll());
    }

    @GetMapping("/{idCompra}")
    public ResponseEntity<CompraResponse> findById(@PathVariable Long idCompra) {
        return ResponseEntity.ok(compraService.findById(idCompra));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<CompraResponse> findByCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(compraService.findByCodigo(codigo));
    }

    @GetMapping("/proveedor/{proveedorId}")
    public ResponseEntity<List<CompraResponse>> findByProveedorId(@PathVariable Long proveedorId) {
        return ResponseEntity.ok(compraService.findByProveedorId(proveedorId));
    }

    @PostMapping
    public ResponseEntity<CompraResponse> create(@Valid @RequestBody CompraRequest request) {
        CompraResponse response = compraService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}