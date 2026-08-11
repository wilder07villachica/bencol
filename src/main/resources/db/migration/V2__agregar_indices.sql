-- BENCOL APP
-- V2 - Índices para consultas, filtros y reportes

-- CLIENTES
CREATE INDEX idx_clients_estado_categoria
    ON clients (estado, categoria);

CREATE INDEX idx_clients_nombre
    ON clients (nombre);

-- PRODUCTOS
CREATE INDEX idx_productos_estado_categoria
    ON productos (estado, categoria);

CREATE INDEX idx_productos_descripcion
    ON productos (descripcion);


-- VENTAS
CREATE INDEX idx_ventas_fecha_creacion
    ON ventas (fecha_creacion);

CREATE INDEX idx_ventas_estado_fecha
    ON ventas (estado, fecha_creacion);

CREATE INDEX idx_ventas_cliente_fecha
    ON ventas (cliente_id, fecha_creacion);


-- CUENTAS POR COBRAR
CREATE INDEX idx_cuentas_cobrar_estado
    ON cuentas_cobrar (estado);

CREATE INDEX idx_cuentas_cobrar_estado_saldo
    ON cuentas_cobrar (estado, saldo_pendiente);

-- PAGOS DE CLIENTES
CREATE INDEX idx_pagos_fecha
    ON pagos (fecha_pago);

-- PROVEEDORES
CREATE INDEX idx_proveedores_estado
    ON proveedores (estado);

CREATE INDEX idx_proveedores_razon_social
    ON proveedores (razon_social);

-- COMPRAS
CREATE INDEX idx_compras_fecha_creacion
    ON compras (fecha_creacion);

CREATE INDEX idx_compras_estado_fecha
    ON compras (estado, fecha_creacion);

CREATE INDEX idx_compras_proveedor_fecha
    ON compras (proveedor_id, fecha_creacion);

-- CUENTAS POR PAGAR
CREATE INDEX idx_cuentas_pagar_estado
    ON cuentas_pagar (estado);

CREATE INDEX idx_cuentas_pagar_estado_saldo
    ON cuentas_pagar (estado, saldo_pendiente);

-- PAGOS A PROVEEDORES
CREATE INDEX idx_pagos_proveedores_fecha
    ON pagos_proveedores (fecha_pago);

-- EGRESOS
CREATE INDEX idx_egresos_fecha
    ON egresos (fecha_egreso);

CREATE INDEX idx_egresos_categoria_fecha
    ON egresos (categoria, fecha_egreso);

-- INVENTARIO
CREATE INDEX idx_inventarios_stock
    ON inventarios (stock_actual, stock_minimo);

-- MOVIMIENTOS INVENTARIO
CREATE INDEX idx_movimientos_inventario_fecha
    ON movimientos_inventario (fecha_creacion);

CREATE INDEX idx_movimientos_inventario_producto_fecha
    ON movimientos_inventario (producto_id, fecha_creacion);

CREATE INDEX idx_movimientos_inventario_tipo_fecha
    ON movimientos_inventario (tipo_movimiento, fecha_creacion);

-- CAJAS
CREATE INDEX idx_cajas_fecha_apertura
    ON cajas (fecha_apertura);

CREATE INDEX idx_cajas_estado_fecha
    ON cajas (estado, fecha_apertura);


-- MOVIMIENTOS DE CAJA
CREATE INDEX idx_movimientos_caja_fecha
    ON movimientos_caja (fecha_movimiento);

CREATE INDEX idx_movimientos_caja_caja_fecha
    ON movimientos_caja (caja_id, fecha_movimiento);

CREATE INDEX idx_movimientos_caja_tipo_fecha
    ON movimientos_caja (tipo_movimiento, fecha_movimiento);

-- USUARIOS
CREATE INDEX idx_usuarios_estado_rol
    ON usuarios (estado, rol);

CREATE INDEX idx_usuarios_username_estado
    ON usuarios (username, estado);