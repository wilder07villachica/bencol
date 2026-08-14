package com.runicsoft.bencolapp.cotizaciones.mapper;

import com.runicsoft.bencolapp.cotizaciones.dtos.response.CotizacionResponse;
import com.runicsoft.bencolapp.cotizaciones.dtos.response.DetalleCotizacionResponse;
import com.runicsoft.bencolapp.cotizaciones.models.Cotizacion;
import com.runicsoft.bencolapp.cotizaciones.models.DetalleCotizacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CotizacionMapper {

    @Mapping(target = "empresaId", source = "empresa.id")
    @Mapping(target = "rucEmpresa", source = "empresa.ruc")
    @Mapping(target = "razonSocialEmpresa", source = "empresa.razonSocial")
    @Mapping(target = "nombreComercialEmpresa", source = "empresa.nombreComercial")
    @Mapping(target = "clienteId", source = "cliente.id")
    @Mapping(target = "nombreCliente", source = "cliente.nombre")
    CotizacionResponse convertirCotizacionDto(Cotizacion cotizacion);

    @Mapping(target = "productoId", source = "producto.id")
    @Mapping(target = "codigoProducto", source = "producto.codigo")
    @Mapping(target = "descripcionProducto", source = "producto.descripcion")
    @Mapping(target = "categoriaProducto", expression = "java(detalle.getProducto().getCategoria() != null ? detalle.getProducto().getCategoria().name() : null)")
    @Mapping(target = "contenidoProducto", source = "producto.contenido")
    @Mapping(target = "unidadMedidaProducto", expression = "java(detalle.getProducto().getUnidadMedida() != null ? detalle.getProducto().getUnidadMedida().name() : null)")
    @Mapping(target = "unidadesPorPaquete", source = "producto.unidadesPorPaquete")
    @Mapping(target = "tieneImagen", expression = "java(detalle.getImagenRuta() != null && !detalle.getImagenRuta().isBlank())")
    DetalleCotizacionResponse convertirDetalleDto(DetalleCotizacion detalle);
}