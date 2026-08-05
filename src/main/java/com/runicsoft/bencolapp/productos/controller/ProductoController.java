package com.runicsoft.bencolapp.productos.controller;

import com.runicsoft.bencolapp.productos.dtos.request.ProductoRequest;
import com.runicsoft.bencolapp.productos.dtos.response.ProductoResponse;
import com.runicsoft.bencolapp.productos.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bencol.agua/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> findAll() {
        return ResponseEntity.ok(productoService.findAll());
    }

    @GetMapping("/{idProducto}")
    public ResponseEntity<ProductoResponse> findById(@PathVariable Long idProducto) {
        return ResponseEntity.ok(productoService.findById(idProducto));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<ProductoResponse> findByCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(productoService.findByCodigo(codigo));
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> create(@Valid @RequestBody ProductoRequest request) {
        ProductoResponse response = productoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{idProducto}")
    public ResponseEntity<ProductoResponse> update(@PathVariable Long idProducto, @Valid @RequestBody ProductoRequest request) {
        ProductoResponse response = productoService.update(idProducto, request);
        return ResponseEntity.ok(response);
    }
}