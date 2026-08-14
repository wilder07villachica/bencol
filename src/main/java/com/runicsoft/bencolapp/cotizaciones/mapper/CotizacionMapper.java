package com.runicsoft.bencolapp.cotizaciones.mapper;

import com.runicsoft.bencolapp.cotizaciones.dtos.response.CotizacionResponse;
import com.runicsoft.bencolapp.cotizaciones.dtos.response.DetalleCotizacionResponse;
import com.runicsoft.bencolapp.cotizaciones.models.Cotizacion;
import com.runicsoft.bencolapp.cotizaciones.models.DetalleCotizacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CotizacionMapper {

    @Mapping(target = "empresaId", source = "empresa.id")
    @Mapping(target = "rucEmpresa", source = "empresa.ruc")
    @Mapping(target = "razonSocialEmpresa", source = "empresa.razonSocial")
    @Mapping(target = "nombreComercialEmpresa", source = "empresa.nombreComercial")
    @Mapping(target = "clienteId", source = "cliente.id")
    @Mapping(target = "nombreCliente", source = "cliente.nombre")
    @Mapping(target = "tieneImagen", expression = "java(cotizacion.getImagenRuta() != null && !cotizacion.getImagenRuta().isBlank())")
    CotizacionResponse convertirCotizacionDto(Cotizacion cotizacion);

    List<CotizacionResponse> convertirListaCotizacionDto(List<Cotizacion> cotizaciones);

    @Mapping(target = "productoId", source = "producto.id")
    @Mapping(target = "codigoProducto", source = "producto.codigo")
    @Mapping(target = "descripcionProducto", source = "producto.descripcion")
    DetalleCotizacionResponse convertirDetalleDto(DetalleCotizacion detalle);

    List<DetalleCotizacionResponse> convertirListaDetalleDto(List<DetalleCotizacion> detalles);
}