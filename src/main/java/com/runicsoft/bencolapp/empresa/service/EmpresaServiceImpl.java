package com.runicsoft.bencolapp.empresa.service;

import com.runicsoft.bencolapp.empresa.dtos.request.EmpresaRequest;
import com.runicsoft.bencolapp.empresa.dtos.response.EmpresaResponse;
import com.runicsoft.bencolapp.empresa.mappers.EmpresaMapper;
import com.runicsoft.bencolapp.empresa.models.Empresa;
import com.runicsoft.bencolapp.empresa.repository.EmpresaRepository;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import com.runicsoft.bencolapp.utils.exceptions.BusinessException;
import com.runicsoft.bencolapp.utils.exceptions.ConflictException;
import com.runicsoft.bencolapp.utils.exceptions.ResourceNotFoundException;
import com.runicsoft.bencolapp.utils.storage.StorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import static com.runicsoft.bencolapp.utils.constants.MessageConstants.*;

@Service
@RequiredArgsConstructor
public class EmpresaServiceImpl implements EmpresaService {

    private final StorageProperties storageProperties;
    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper empresaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EmpresaResponse> findAll() {
        List<Empresa> listaEmpresas = empresaRepository.findAll();
        return empresaMapper.convertirListaEmpresaDto(listaEmpresas);
    }

    @Override
    @Transactional(readOnly = true)
    public EmpresaResponse findById(Long idEmpresa) {
        if (idEmpresa == null || idEmpresa <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        Empresa empresa = getEmpresa(idEmpresa);
        return empresaMapper.convertirEmpresaDto(empresa);
    }

    @Override
    @Transactional(readOnly = true)
    public EmpresaResponse findActiva() {
        Empresa empresa = empresaRepository
                .findFirstByEstadoOrderByIdAsc(EstadoGeneral.ACTIVO)
                .orElseThrow(() -> new ResourceNotFoundException(EMPRESA_NO_ENCONTRADA));

        return empresaMapper.convertirEmpresaDto(empresa);
    }

    @Override
    @Transactional
    public EmpresaResponse create(EmpresaRequest request) {
        if (empresaRepository.existsByRuc(request.getRuc())) {
            throw new ConflictException(EMPRESA_EXISTENTE);
        }

        Empresa empresa = empresaMapper.convertirEmpresaEntidad(request);
        Empresa empresaGuardada = empresaRepository.save(empresa);
        return empresaMapper.convertirEmpresaDto(empresaGuardada);
    }

    @Override
    @Transactional
    public EmpresaResponse update(Long idEmpresa, EmpresaRequest request) {
        if (idEmpresa == null || idEmpresa <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        Empresa empresa = getEmpresa(idEmpresa);

        if (empresaRepository.existsByRucAndIdNot(request.getRuc(), idEmpresa)) {
            throw new ConflictException(EMPRESA_EXISTENTE);
        }

        empresaMapper.updateEmpresa(request, empresa);

        Empresa empresaActualizada = empresaRepository.save(empresa);
        return empresaMapper.convertirEmpresaDto(empresaActualizada);
    }

    @Override
    @Transactional
    public EmpresaResponse subirLogo(Long idEmpresa, MultipartFile archivo) {
        if (idEmpresa == null || idEmpresa <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        Empresa empresa = getEmpresa(idEmpresa);

        validarLogo(archivo);

        try {
            Path directorioLogos = getDirectorioLogos();

            Files.createDirectories(directorioLogos);

            eliminarLogoAnterior(empresa);

            String extension = obtenerExtension(archivo.getOriginalFilename());

            String nombreArchivo = "empresa-" + empresa.getId() + "-" + UUID.randomUUID() + extension;

            Path rutaArchivo = directorioLogos
                    .resolve(nombreArchivo)
                    .normalize();

            Files.copy(
                    archivo.getInputStream(),
                    rutaArchivo,
                    StandardCopyOption.REPLACE_EXISTING
            );

            empresa.setLogoNombre(archivo.getOriginalFilename());
            empresa.setLogoTipo(archivo.getContentType());
            empresa.setLogoRuta(rutaArchivo.toString());

            Empresa empresaActualizada = empresaRepository.save(empresa);
            return empresaMapper.convertirEmpresaDto(empresaActualizada);

        } catch (IOException e) {
            throw new BusinessException("No fue posible guardar el logo de la empresa.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Resource obtenerLogo(Long idEmpresa) {
        if (idEmpresa == null || idEmpresa <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        Empresa empresa = getEmpresa(idEmpresa);

        if (empresa.getLogoRuta() == null || empresa.getLogoRuta().isBlank()) {
            throw new ResourceNotFoundException("La empresa no tiene un logo registrado.");
        }

        try {
            Path ruta = Paths.get(empresa.getLogoRuta())
                    .toAbsolutePath()
                    .normalize();

            Resource recurso = new UrlResource(ruta.toUri());

            if (!recurso.exists() || !recurso.isReadable()) {
                throw new ResourceNotFoundException("El logo de la empresa no fue encontrado.");
            }

            return recurso;

        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("El logo de la empresa no fue encontrado.");
        }
    }

    @Override
    @Transactional
    public EmpresaResponse eliminarLogo(Long idEmpresa) {
        if (idEmpresa == null || idEmpresa <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        Empresa empresa = getEmpresa(idEmpresa);

        if (empresa.getLogoRuta() == null || empresa.getLogoRuta().isBlank()) {
            throw new BusinessException("La empresa no tiene un logo registrado.");
        }

        eliminarLogoAnterior(empresa);

        empresa.setLogoNombre(null);
        empresa.setLogoTipo(null);
        empresa.setLogoRuta(null);

        Empresa empresaActualizada = empresaRepository.save(empresa);
        return empresaMapper.convertirEmpresaDto(empresaActualizada);
    }

    private Empresa getEmpresa(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(EMPRESA_NO_ENCONTRADA));
    }

    private void validarLogo(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new BusinessException("El archivo del logo es obligatorio.");
        }

        String tipo = archivo.getContentType();

        if (tipo == null ||
                (!tipo.equals("image/png") &&
                        !tipo.equals("image/jpeg") &&
                        !tipo.equals("image/webp"))) {

            throw new BusinessException("El logo debe ser una imagen PNG, JPG, JPEG o WEBP.");
        }

        long tamanioMaximo = 5L * 1024L * 1024L;

        if (archivo.getSize() > tamanioMaximo) {
            throw new BusinessException("El logo no debe superar los 5 MB.");
        }
    }

    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isBlank()) {
            return "";
        }

        int indice = nombreArchivo.lastIndexOf('.');

        if (indice == -1) {
            return "";
        }

        return nombreArchivo.substring(indice);
    }

    private void eliminarLogoAnterior(Empresa empresa) {
        if (empresa.getLogoRuta() == null || empresa.getLogoRuta().isBlank()) {
            return;
        }

        try {
            Files.deleteIfExists(
                    Paths.get(empresa.getLogoRuta())
                            .toAbsolutePath()
                            .normalize()
            );
        } catch (IOException ignored) {
        }
    }

    private Path getDirectorioLogos() {
        return Paths.get(
                storageProperties.getRoot(),
                "empresa",
                "logos"
        ).toAbsolutePath().normalize();
    }
}