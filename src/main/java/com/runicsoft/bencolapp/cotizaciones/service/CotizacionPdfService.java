package com.runicsoft.bencolapp.cotizaciones.service;

public interface CotizacionPdfService {
    byte[] generarPdf(Long cotizacionId);
}