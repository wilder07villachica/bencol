package com.runicsoft.bencolapp.egresos.controller;

import com.runicsoft.bencolapp.egresos.dtos.request.EgresoRequest;
import com.runicsoft.bencolapp.egresos.dtos.response.EgresoResponse;
import com.runicsoft.bencolapp.egresos.service.EgresoService;
import com.runicsoft.bencolapp.egresos.utils.CategoriaEgreso;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/bencol.agua/egresos")
@RequiredArgsConstructor
public class EgresoController {

    private final EgresoService egresoService;

    @GetMapping
    public ResponseEntity<PaginaResponse<EgresoResponse>> findAll(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio,
            @RequestParam(required = false) CategoriaEgreso categoria,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate desde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate hasta
    ) {
        return ResponseEntity.ok(egresoService.findAll(pagina, tamanio, categoria, desde, hasta));
    }

    @GetMapping("/{idEgreso}")
    public ResponseEntity<EgresoResponse> findById(@PathVariable Long idEgreso) {
        return ResponseEntity.ok(egresoService.findById(idEgreso));
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<EgresoResponse>> findByCategoria(@PathVariable CategoriaEgreso categoria) {
        return ResponseEntity.ok(egresoService.findByCategoria(categoria));
    }

    @PostMapping
    public ResponseEntity<EgresoResponse> create(@Valid @RequestBody EgresoRequest request) {
        EgresoResponse response = egresoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}