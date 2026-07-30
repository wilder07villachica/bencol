package com.runicsoft.bencolapp.productos.controller;

import com.runicsoft.bencolapp.productos.dtos.request.ProductoRequest;
import com.runicsoft.bencolapp.productos.dtos.response.ProductoResponse;
import com.runicsoft.bencolapp.productos.service.ProductoService;
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
    public ResponseEntity<List<ProductoResponse>> listarProductos() {
        return ResponseEntity.ok(productoService.listarProductos());
    }

    @GetMapping("/{idProducto}")
    public ResponseEntity<ProductoResponse> buscarProductoPorId(@PathVariable Long idProducto) {
        return ResponseEntity.ok(productoService.buscarProductoPorId(idProducto));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<ProductoResponse> buscarProductoPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(productoService.buscarProductoPorCodigo(codigo));
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> registrarProducto(@RequestBody ProductoRequest request) {
        ProductoResponse response = productoService.registrarProducto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{idProducto}")
    public ResponseEntity<ProductoResponse> actualizarProducto(@PathVariable Long idProducto, @RequestBody ProductoRequest request) {
        ProductoResponse response = productoService.actualizarProducto(idProducto, request);
        return ResponseEntity.ok(response);
    }
}