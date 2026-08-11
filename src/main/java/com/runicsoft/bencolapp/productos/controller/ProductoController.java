package com.runicsoft.bencolapp.productos.controller;

import com.runicsoft.bencolapp.productos.dtos.request.ProductoRequest;
import com.runicsoft.bencolapp.productos.dtos.response.ProductoResponse;
import com.runicsoft.bencolapp.productos.service.ProductoService;
import com.runicsoft.bencolapp.productos.utils.ProductoCategoria;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;
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
    public ResponseEntity<PaginaResponse<ProductoResponse>> findAll(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio,
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) EstadoGeneral estado,
            @RequestParam(required = false) ProductoCategoria categoria
    ) {
        return ResponseEntity.ok(productoService.findAll(pagina, tamanio, texto, estado, categoria));
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