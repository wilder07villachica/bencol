-- ============================================================
-- BENCOL APP
-- V1 - Estructura inicial de la base de datos
-- ============================================================

-- =========================
-- TABLAS INDEPENDIENTES
-- =========================

CREATE TABLE empresas
(
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    estado           ENUM('ACTIVO','INACTIVO') DEFAULT NULL,
    nombre_comercial VARCHAR(225) DEFAULT NULL,
    razon_social     VARCHAR(225) NOT NULL,
    ruc              VARCHAR(11)  NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_empresas_ruc (ruc)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE clients
(
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    categoria ENUM(
        'CONSUMIDOR_FINAL',
        'CORPORATIVO',
        'DISTRIBUIDOR'
    ) DEFAULT NULL,
    direccion VARCHAR(255) NOT NULL,
    email     VARCHAR(255) NOT NULL,
    estado    ENUM('ACTIVO','INACTIVO') DEFAULT NULL,
    nombre    VARCHAR(255) NOT NULL,
    telefono  VARCHAR(255) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_clients_email (email),
    UNIQUE KEY uk_clients_telefono (telefono)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE productos
(
    id                   BIGINT         NOT NULL AUTO_INCREMENT,
    categoria            ENUM(
        'BIDON',
        'BOTELLA',
        'PAQUETE'
    ) NOT NULL,
    codigo               VARCHAR(20)    NOT NULL,
    contenido            DECIMAL(10, 2) NOT NULL,
    descripcion          VARCHAR(255)   NOT NULL,
    estado               ENUM('ACTIVO','INACTIVO') NOT NULL,
    precio_base          DECIMAL(10, 2) NOT NULL,
    unidad_medida        ENUM(
        'LITRO',
        'MILILITRO'
    ) NOT NULL,
    unidades_por_paquete INT            NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_productos_codigo (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE proveedores
(
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    contacto     VARCHAR(100) DEFAULT NULL,
    direccion    VARCHAR(255) DEFAULT NULL,
    email        VARCHAR(100) DEFAULT NULL,
    estado       ENUM('ACTIVO','INACTIVO') NOT NULL,
    razon_social VARCHAR(100) NOT NULL,
    ruc          VARCHAR(11)  NOT NULL,
    telefono     VARCHAR(20)  DEFAULT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_proveedores_ruc (ruc)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE usuarios
(
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    email               VARCHAR(100) NOT NULL,
    estado              ENUM('ACTIVO','INACTIVO') NOT NULL,
    fecha_actualizacion DATETIME(6) DEFAULT NULL,
    fecha_creacion      DATETIME(6) NOT NULL,
    password            VARCHAR(255) NOT NULL,
    rol                 ENUM(
        'ADMIN',
        'ALMACEN',
        'CAJA',
        'COBRANZAS',
        'COMPRAS',
        'VENTAS'
    ) NOT NULL,
    username            VARCHAR(50)  NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_usuarios_username (username),
    UNIQUE KEY uk_usuarios_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE cajas
(
    id             BIGINT         NOT NULL AUTO_INCREMENT,
    diferencia     DECIMAL(12, 2) DEFAULT NULL,
    estado         ENUM('ABIERTA','CERRADA') NOT NULL,
    fecha_apertura DATETIME(6) NOT NULL,
    fecha_cierre   DATETIME(6) DEFAULT NULL,
    saldo_actual   DECIMAL(12, 2) NOT NULL,
    saldo_esperado DECIMAL(12, 2) DEFAULT NULL,
    saldo_inicial  DECIMAL(12, 2) NOT NULL,
    saldo_real     DECIMAL(12, 2) DEFAULT NULL,
    total_egresos  DECIMAL(12, 2) NOT NULL,
    total_ingresos DECIMAL(12, 2) NOT NULL,
    abierta_por    VARCHAR(50)    DEFAULT NULL,
    cerrada_por    VARCHAR(50)    DEFAULT NULL,

    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE egresos
(
    id             BIGINT         NOT NULL AUTO_INCREMENT,
    categoria      ENUM(
        'COMBUSTIBLE',
        'INSUMOS',
        'MANTENIMIENTO',
        'MOVILIDAD',
        'OTRO',
        'PERSONAL',
        'SERVICIOS'
    ) NOT NULL,
    concepto       VARCHAR(255)   NOT NULL,
    fecha_egreso   DATETIME(6) NOT NULL,
    monto          DECIMAL(12, 2) NOT NULL,
    referencia     VARCHAR(100) DEFAULT NULL,
    registrado_por VARCHAR(50)  DEFAULT NULL,

    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =========================
-- PRODUCTOS / INVENTARIO
-- =========================

CREATE TABLE inventarios
(
    id                  BIGINT NOT NULL AUTO_INCREMENT,
    fecha_actualizacion DATETIME(6) DEFAULT NULL,
    fecha_creacion      DATETIME(6) NOT NULL,
    stock_actual        INT    NOT NULL,
    stock_maximo        INT DEFAULT NULL,
    stock_minimo        INT    NOT NULL,
    producto_id         BIGINT NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_inventarios_producto (producto_id),

    CONSTRAINT fk_inventarios_producto
        FOREIGN KEY (producto_id)
            REFERENCES productos (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE movimientos_inventario
(
    id              BIGINT NOT NULL AUTO_INCREMENT,
    cantidad        INT    NOT NULL,
    fecha_creacion  DATETIME(6) NOT NULL,
    referencia      VARCHAR(255) DEFAULT NULL,
    stock_anterior  INT    NOT NULL,
    stock_nuevo     INT    NOT NULL,
    tipo_movimiento ENUM('ENTRADA','SALIDA') NOT NULL,
    producto_id     BIGINT NOT NULL,
    registrado_por  VARCHAR(50)  DEFAULT NULL,

    PRIMARY KEY (id),

    KEY             idx_movimientos_inventario_producto (producto_id),

    CONSTRAINT fk_movimientos_inventario_producto
        FOREIGN KEY (producto_id)
            REFERENCES productos (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE clientes_precios
(
    id                  BIGINT         NOT NULL AUTO_INCREMENT,
    fecha_actualizacion DATETIME(6) DEFAULT NULL,
    fecha_creacion      DATETIME(6) NOT NULL,
    precio              DECIMAL(10, 2) NOT NULL,
    cliente_id          BIGINT         NOT NULL,
    producto_id         BIGINT         NOT NULL,

    PRIMARY KEY (id),

    UNIQUE KEY uk_cliente_producto (
        cliente_id,
        producto_id
        ),

    KEY                 idx_clientes_precios_producto (producto_id),

    CONSTRAINT fk_clientes_precios_cliente
        FOREIGN KEY (cliente_id)
            REFERENCES clients (id),

    CONSTRAINT fk_clientes_precios_producto
        FOREIGN KEY (producto_id)
            REFERENCES productos (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =========================
-- VENTAS
-- =========================

CREATE TABLE ventas
(
    id                  BIGINT         NOT NULL AUTO_INCREMENT,
    codigo              VARCHAR(20)    NOT NULL,
    estado              ENUM('ANULADA','EMITIDA') NOT NULL,
    fecha_actualizacion DATETIME(6) DEFAULT NULL,
    fecha_creacion      DATETIME(6) NOT NULL,
    subtotal            DECIMAL(10, 2) NOT NULL,
    total               DECIMAL(10, 2) NOT NULL,
    cliente_id          BIGINT         NOT NULL,
    actualizado_por     VARCHAR(50) DEFAULT NULL,
    creado_por          VARCHAR(50) DEFAULT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_ventas_codigo (codigo),

    KEY                 idx_ventas_cliente (cliente_id),

    CONSTRAINT fk_ventas_cliente
        FOREIGN KEY (cliente_id)
            REFERENCES clients (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE detalles_ventas
(
    id              BIGINT         NOT NULL AUTO_INCREMENT,
    cantidad        INT            NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    subtotal        DECIMAL(10, 2) NOT NULL,
    producto_id     BIGINT         NOT NULL,
    venta_id        BIGINT         NOT NULL,

    PRIMARY KEY (id),

    KEY             idx_detalles_ventas_producto (producto_id),
    KEY             idx_detalles_ventas_venta (venta_id),

    CONSTRAINT fk_detalles_ventas_producto
        FOREIGN KEY (producto_id)
            REFERENCES productos (id),

    CONSTRAINT fk_detalles_ventas_venta
        FOREIGN KEY (venta_id)
            REFERENCES ventas (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE cuentas_cobrar
(
    id                  BIGINT         NOT NULL AUTO_INCREMENT,
    estado              ENUM(
        'ANULADA',
        'PAGADA',
        'PARCIAL',
        'PENDIENTE'
    ) NOT NULL,
    fecha_actualizacion DATETIME(6) DEFAULT NULL,
    fecha_creacion      DATETIME(6) NOT NULL,
    monto_pagado        DECIMAL(12, 2) NOT NULL,
    monto_total         DECIMAL(12, 2) NOT NULL,
    saldo_pendiente     DECIMAL(12, 2) NOT NULL,
    venta_id            BIGINT         NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_cuentas_cobrar_venta (venta_id),

    CONSTRAINT fk_cuentas_cobrar_venta
        FOREIGN KEY (venta_id)
            REFERENCES ventas (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE pagos
(
    id               BIGINT         NOT NULL AUTO_INCREMENT,
    fecha_pago       DATETIME(6) NOT NULL,
    metodo_pago      ENUM(
        'EFECTIVO',
        'OTRO',
        'PLIN',
        'TRANSFERENCIA',
        'YAPE'
    ) NOT NULL,
    monto            DECIMAL(12, 2) NOT NULL,
    referencia       VARCHAR(100) DEFAULT NULL,
    cuenta_cobrar_id BIGINT         NOT NULL,
    registrado_por   VARCHAR(50)  DEFAULT NULL,

    PRIMARY KEY (id),

    KEY              idx_pagos_cuenta_cobrar (cuenta_cobrar_id),

    CONSTRAINT fk_pagos_cuenta_cobrar
        FOREIGN KEY (cuenta_cobrar_id)
            REFERENCES cuentas_cobrar (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =========================
-- COMPRAS
-- =========================

CREATE TABLE compras
(
    id                  BIGINT         NOT NULL AUTO_INCREMENT,
    codigo              VARCHAR(20)    NOT NULL,
    estado              ENUM('ACTIVO','INACTIVO') NOT NULL,
    fecha_actualizacion DATETIME(6) DEFAULT NULL,
    fecha_creacion      DATETIME(6) NOT NULL,
    subtotal            DECIMAL(12, 2) NOT NULL,
    total               DECIMAL(12, 2) NOT NULL,
    proveedor_id        BIGINT         NOT NULL,
    actualizado_por     VARCHAR(50) DEFAULT NULL,
    creado_por          VARCHAR(50) DEFAULT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_compras_codigo (codigo),

    KEY                 idx_compras_proveedor (proveedor_id),

    CONSTRAINT fk_compras_proveedor
        FOREIGN KEY (proveedor_id)
            REFERENCES proveedores (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE detalles_compras
(
    id             BIGINT         NOT NULL AUTO_INCREMENT,
    cantidad       INT            NOT NULL,
    costo_unitario DECIMAL(12, 2) NOT NULL,
    subtotal       DECIMAL(12, 2) NOT NULL,
    compra_id      BIGINT         NOT NULL,
    producto_id    BIGINT         NOT NULL,

    PRIMARY KEY (id),

    KEY            idx_detalles_compras_compra (compra_id),
    KEY            idx_detalles_compras_producto (producto_id),

    CONSTRAINT fk_detalles_compras_compra
        FOREIGN KEY (compra_id)
            REFERENCES compras (id),

    CONSTRAINT fk_detalles_compras_producto
        FOREIGN KEY (producto_id)
            REFERENCES productos (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE cuentas_pagar
(
    id                  BIGINT         NOT NULL AUTO_INCREMENT,
    estado              ENUM(
        'ANULADA',
        'PAGADA',
        'PARCIAL',
        'PENDIENTE'
    ) NOT NULL,
    fecha_actualizacion DATETIME(6) DEFAULT NULL,
    fecha_creacion      DATETIME(6) NOT NULL,
    monto_pagado        DECIMAL(12, 2) NOT NULL,
    monto_total         DECIMAL(12, 2) NOT NULL,
    saldo_pendiente     DECIMAL(12, 2) NOT NULL,
    compra_id           BIGINT         NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_cuentas_pagar_compra (compra_id),

    CONSTRAINT fk_cuentas_pagar_compra
        FOREIGN KEY (compra_id)
            REFERENCES compras (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE pagos_proveedores
(
    id              BIGINT         NOT NULL AUTO_INCREMENT,
    fecha_pago      DATETIME(6) NOT NULL,
    metodo_pago     ENUM(
        'EFECTIVO',
        'OTRO',
        'PLIN',
        'TRANSFERENCIA',
        'YAPE'
    ) NOT NULL,
    monto           DECIMAL(12, 2) NOT NULL,
    referencia      VARCHAR(100) DEFAULT NULL,
    cuenta_pagar_id BIGINT         NOT NULL,
    registrado_por  VARCHAR(50)  DEFAULT NULL,

    PRIMARY KEY (id),

    KEY             idx_pagos_proveedores_cuenta (cuenta_pagar_id),

    CONSTRAINT fk_pagos_proveedores_cuenta
        FOREIGN KEY (cuenta_pagar_id)
            REFERENCES cuentas_pagar (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =========================
-- CAJA
-- =========================

CREATE TABLE movimientos_caja
(
    id               BIGINT         NOT NULL AUTO_INCREMENT,
    concepto         VARCHAR(255)   NOT NULL,
    fecha_movimiento DATETIME(6) NOT NULL,
    monto            DECIMAL(12, 2) NOT NULL,
    referencia       VARCHAR(100) DEFAULT NULL,
    tipo_movimiento  ENUM(
        'EGRESO',
        'INGRESO'
    ) NOT NULL,
    caja_id          BIGINT         NOT NULL,
    registrado_por   VARCHAR(50)  DEFAULT NULL,

    PRIMARY KEY (id),

    KEY              idx_movimientos_caja_caja (caja_id),

    CONSTRAINT fk_movimientos_caja_caja
        FOREIGN KEY (caja_id)
            REFERENCES cajas (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;