package com.runicsoft.bencolapp.empresa.controller;

import com.runicsoft.bencolapp.empresa.dtos.request.EmpresaRequest;
import com.runicsoft.bencolapp.empresa.dtos.response.EmpresaResponse;
import com.runicsoft.bencolapp.empresa.service.EmpresaService;
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
    public ResponseEntity<List<EmpresaResponse>> listarEmpresas() {
        return ResponseEntity.ok(empresaService.listarEmpresas());
    }

    @GetMapping("/{idEmpresa}")
    public ResponseEntity<EmpresaResponse> buscarEmpresa(@PathVariable Long idEmpresa) {
        return ResponseEntity.ok(empresaService.buscarEmpresa(idEmpresa));
    }

    @PostMapping
    public ResponseEntity<EmpresaResponse> registrarEmpresa(@RequestBody EmpresaRequest request) {
        EmpresaResponse response = empresaService.registrarEmpresa(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
