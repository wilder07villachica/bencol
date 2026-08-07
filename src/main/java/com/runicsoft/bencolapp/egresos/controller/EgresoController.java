package com.runicsoft.bencolapp.egresos.controller;

import com.runicsoft.bencolapp.egresos.dtos.request.EgresoRequest;
import com.runicsoft.bencolapp.egresos.dtos.response.EgresoResponse;
import com.runicsoft.bencolapp.egresos.service.EgresoService;
import com.runicsoft.bencolapp.egresos.utils.CategoriaEgreso;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bencol.agua/egresos")
@RequiredArgsConstructor
public class EgresoController {

    private final EgresoService egresoService;

    @GetMapping
    public ResponseEntity<List<EgresoResponse>> findAll() {
        return ResponseEntity.ok(egresoService.findAll());
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