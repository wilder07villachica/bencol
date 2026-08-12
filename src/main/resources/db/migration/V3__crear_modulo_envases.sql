-- BENCOL APP
-- V3 - Módulo de control de envases retornables

-- CUENTA DE ENVASES POR CLIENTE
CREATE TABLE cuentas_envases_clientes
(
    id                  BIGINT NOT NULL AUTO_INCREMENT,
    cliente_id          BIGINT NOT NULL,
    producto_id         BIGINT NOT NULL,
    cantidad_propios    INT    NOT NULL DEFAULT 0,
    cantidad_prestados  INT    NOT NULL DEFAULT 0,
    fecha_creacion      DATETIME(6) NOT NULL,
    fecha_actualizacion DATETIME(6) DEFAULT NULL,

    PRIMARY KEY (id),

    UNIQUE KEY uk_cuenta_envase_cliente_producto (
        cliente_id,
        producto_id
        ),

    KEY                 idx_cuenta_envase_cliente (
        cliente_id
    ),

    KEY                 idx_cuenta_envase_producto (
        producto_id
    ),

    CONSTRAINT fk_cuenta_envase_cliente
        FOREIGN KEY (cliente_id)
            REFERENCES clients (id),

    CONSTRAINT fk_cuenta_envase_producto
        FOREIGN KEY (producto_id)
            REFERENCES productos (id),

    CONSTRAINT chk_cantidad_propios_no_negativa
        CHECK (cantidad_propios >= 0),

    CONSTRAINT chk_cantidad_prestados_no_negativa
        CHECK (cantidad_prestados >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- MOVIMIENTOS DE ENVASES
CREATE TABLE movimientos_envases
(
    id               BIGINT NOT NULL AUTO_INCREMENT,
    cuenta_envase_id BIGINT NOT NULL,
    tipo_movimiento  ENUM(
        'COMPRA',
        'PRESTAMO',
        'DEVOLUCION',
        'CONVERSION_COMPRA',
        'AJUSTE'
    ) NOT NULL,
    cantidad         INT    NOT NULL,
    referencia       VARCHAR(255) DEFAULT NULL,
    fecha_movimiento DATETIME(6) NOT NULL,
    registrado_por   VARCHAR(50)  DEFAULT NULL,

    PRIMARY KEY (id),

    KEY              idx_movimiento_envase_cuenta (
        cuenta_envase_id
    ),

    KEY              idx_movimiento_envase_fecha (
        fecha_movimiento
    ),

    KEY              idx_movimiento_envase_tipo_fecha (
        tipo_movimiento,
        fecha_movimiento
    ),

    CONSTRAINT fk_movimiento_envase_cuenta
        FOREIGN KEY (cuenta_envase_id)
            REFERENCES cuentas_envases_clientes (id),

    CONSTRAINT chk_movimiento_envase_cantidad_positiva
        CHECK (cantidad > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;