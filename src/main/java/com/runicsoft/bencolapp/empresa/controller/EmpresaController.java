package com.runicsoft.bencolapp.empresa.controller;

import com.runicsoft.bencolapp.empresa.dtos.request.EmpresaRequest;
import com.runicsoft.bencolapp.empresa.dtos.response.EmpresaResponse;
import com.runicsoft.bencolapp.empresa.service.EmpresaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/activa")
    public ResponseEntity<EmpresaResponse> findActiva() {
        return ResponseEntity.ok(empresaService.findActiva());
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

    @PutMapping("/{idEmpresa}")
    public ResponseEntity<EmpresaResponse> update(@PathVariable Long idEmpresa, @Valid @RequestBody EmpresaRequest request) {
        return ResponseEntity.ok(
                empresaService.update(idEmpresa, request)
        );
    }

    @PostMapping(value = "/{idEmpresa}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmpresaResponse> subirLogo(@PathVariable Long idEmpresa, @RequestPart("archivo") MultipartFile archivo) {
        return ResponseEntity.ok(
                empresaService.subirLogo(
                        idEmpresa,
                        archivo
                )
        );
    }

    @GetMapping("/{idEmpresa}/logo")
    public ResponseEntity<Resource> obtenerLogo(@PathVariable Long idEmpresa) {
        EmpresaResponse empresa = empresaService.findById(idEmpresa);

        Resource recurso = empresaService.obtenerLogo(idEmpresa);

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;

        if (empresa.getLogoTipo() != null) {
            mediaType = MediaType.parseMediaType(empresa.getLogoTipo());
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(recurso);
    }

    @DeleteMapping("/{idEmpresa}/logo")
    public ResponseEntity<EmpresaResponse> eliminarLogo(@PathVariable Long idEmpresa) {
        return ResponseEntity.ok(
                empresaService.eliminarLogo(idEmpresa)
        );
    }
}