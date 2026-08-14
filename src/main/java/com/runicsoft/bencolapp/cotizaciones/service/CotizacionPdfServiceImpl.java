package com.runicsoft.bencolapp.cotizaciones.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.runicsoft.bencolapp.cotizaciones.models.Cotizacion;
import com.runicsoft.bencolapp.cotizaciones.models.DetalleCotizacion;
import com.runicsoft.bencolapp.cotizaciones.repository.CotizacionRepository;
import com.runicsoft.bencolapp.utils.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CotizacionPdfServiceImpl implements CotizacionPdfService {

    private final CotizacionRepository cotizacionRepository;
    private final TemplateEngine templateEngine;

    @Override
    @Transactional(readOnly = true)
    public byte[] generarPdf(Long cotizacionId) {
        if (cotizacionId == null || cotizacionId <= 0) {
            throw new IllegalArgumentException("La referencia de la cotización no es válida.");
        }

        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La cotización no fue encontrada."
                ));

        try {
            Context context = new Context();

            context.setVariable("cotizacion", cotizacion);
            context.setVariable("logoEmpresa", convertirLogoEmpresa(cotizacion));
            context.setVariable("imagenesDetalles", convertirImagenesDetalles(cotizacion));

            String html = templateEngine.process(
                    "cotizaciones/cotizacion",
                    context
            );

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new IllegalStateException(
                    "No fue posible generar el PDF de la cotización.",
                    e
            );
        }
    }

    private String convertirLogoEmpresa(Cotizacion cotizacion) {
        if (cotizacion.getEmpresa().getLogoRuta() == null ||
                cotizacion.getEmpresa().getLogoRuta().isBlank()) {
            return null;
        }

        return convertirArchivoBase64(
                cotizacion.getEmpresa().getLogoRuta(),
                cotizacion.getEmpresa().getLogoTipo()
        );
    }

    private Map<Long, String> convertirImagenesDetalles(Cotizacion cotizacion) {
        Map<Long, String> imagenes = new HashMap<>();

        for (DetalleCotizacion detalle : cotizacion.getDetalles()) {
            if (detalle.getImagenRuta() == null ||
                    detalle.getImagenRuta().isBlank()) {
                continue;
            }

            String imagen = convertirArchivoBase64(
                    detalle.getImagenRuta(),
                    detalle.getImagenTipo()
            );

            if (imagen != null) {
                imagenes.put(detalle.getId(), imagen);
            }
        }

        return imagenes;
    }

    private String convertirArchivoBase64(String rutaArchivo, String tipoContenido) {
        try {
            Path ruta = Paths.get(rutaArchivo).normalize();

            if (!Files.exists(ruta) || !Files.isReadable(ruta)) {
                return null;
            }

            byte[] bytes = Files.readAllBytes(ruta);
            String base64 = Base64.getEncoder().encodeToString(bytes);

            String tipo = tipoContenido != null && !tipoContenido.isBlank()
                    ? tipoContenido
                    : "image/png";

            return "data:" + tipo + ";base64," + base64;

        } catch (IOException e) {
            return null;
        }
    }
}