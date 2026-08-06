package com.runicsoft.bencolapp.utils.constants;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class MessageConstants {

    // IDS
    public static final String ID_INVALIDO = "Valor Inválido.";

    // EMPRESA
    public static final String EMPRESA_NO_ENCONTRADA = "Empresa no encontrada.";
    public static final String EMPRESA_EXISTENTE = "La empresa ya existe.";

    // CLIENTE
    public static final String CLIENTE_NO_ENCONTRADO = "Cliente no encontrado.";
    public static final String TELEFONO_EXISTENTE = "El teléfono ya esta registrdo.";
    public static final String CORREO_EXISTENTE = "El correo ya esta registrado.";
    public static final String CLIENTE_INACTIVO = "El Cliente seleccionado se encuentra Inactivo.";

    // PRODUCTO
    public static final String PRODUCTO_NO_ENCONTRADO = "Producto no encontrado.";
    public static final String CODIGO_INVALIDO = "Código inválido.";
    public static final String CODIGO_EXISTENTE = "El código ya existe.";
    public static final String PRODUCTO_INACTIVO = "El Producto seleccionado se encuentra Inactivo.";

    // PRECIOS DE CLIENTES
    public static final String PRECIO_CLIENTE_NO_ENCONTRADO = "Precio del cliente no encontrado.";
    public static final String PRECIO_CLIENTE_EXISTENTE = "Ya existe un registro para ese Cliente y Producto.";

    // VENTA Y DETALLES
    public static final String VENTA_NO_ENCONTRADA =  "La venta solicitada no fue encontrada.";
    public static final String PRODUCTO_DUPLICADO_VENTA = "No se puede registrar el mismo producto más de una vez en la venta.";
    public static final String VENTA_YA_ANULADA = "La venta ya se encuentra anulada.";

    // INVENTARIO
    public static final String INVENTARIO_NO_ENCONTRADO = "El inventario solicitado no fue encontrado.";
    public static final String INVENTARIO_PRODUCTO_EXISTENTE = "El producto ya tiene un inventario registrado.";
    public static final String STOCK_INSUFICIENTE = "No existe stock suficiente para realizar la salida.";
    public static final String STOCK_MAXIMO_INVALIDO = "El stock máximo no puede ser menor que el stock mínimo.";
    public static final String STOCK_SUPERA_MAXIMO = "La operación supera el stock máximo permitido.";
    public static final String TIPO_MOVIMIENTO_INVALIDO = "El tipo de movimiento de inventario no es válido.";
}