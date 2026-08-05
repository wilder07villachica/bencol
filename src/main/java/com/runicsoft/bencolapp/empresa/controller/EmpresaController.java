package com.runicsoft.bencolapp.empresa.controller;

import com.runicsoft.bencolapp.empresa.dtos.request.EmpresaRequest;
import com.runicsoft.bencolapp.empresa.dtos.response.EmpresaResponse;
import com.runicsoft.bencolapp.empresa.service.EmpresaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bencol.agua/empresa")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService empresaService;

    @GetMapping
    public ResponseEntity<List<EmpresaResponse>> findAll() {
        return ResponseEntity.ok(empresaService.findAll());
    }

    @GetMapping("/{idEmpresa}")
    public ResponseEntity<EmpresaResponse> findById(@PathVariable Long idEmpresa) {
        return ResponseEntity.ok(empresaService.findById(idEmpresa));
    }

    @PostMapping
    public ResponseEntity<EmpresaResponse> create(@Valid @RequestBody EmpresaRequest request) {
        EmpresaResponse response = empresaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
