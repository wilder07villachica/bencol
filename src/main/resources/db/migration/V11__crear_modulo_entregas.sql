ALTER TABLE usuarios
    MODIFY COLUMN rol ENUM(
    'ADMIN',
    'ALMACEN',
    'CAJA',
    'COBRANZAS',
    'COMPRAS',
    'VENTAS',
    'REPARTIDOR'
    ) NOT NULL;

CREATE TABLE entregas
(
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    venta_id            BIGINT       NOT NULL,
    repartidor_id       BIGINT NULL,

    direccion           VARCHAR(255) NOT NULL,

    estado              ENUM(
        'PENDIENTE',
        'ENTREGADO',
        'REPROGRAMADO',
        'CANCELADO'
    ) NOT NULL,

    observaciones       VARCHAR(1000) NULL,

    fecha_creacion      DATETIME(6) NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_programada    DATETIME(6) NULL,
    fecha_entrega       DATETIME(6) NULL,

    version             BIGINT       NOT NULL DEFAULT 0,

    PRIMARY KEY (id),

    UNIQUE KEY uk_entrega_venta (venta_id),

    KEY                 idx_entrega_responsable_estado (
        repartidor_id,
        estado
    ),

    CONSTRAINT fk_entrega_venta
        FOREIGN KEY (venta_id)
            REFERENCES ventas (id),

    CONSTRAINT fk_entrega_responsable
        FOREIGN KEY (repartidor_id)
            REFERENCES usuarios (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE historial_entregas
(
    id              BIGINT      NOT NULL AUTO_INCREMENT,
    entrega_id      BIGINT      NOT NULL,

    accion          VARCHAR(30) NOT NULL,
    estado_anterior VARCHAR(20) NULL,
    estado_nuevo    VARCHAR(20) NOT NULL,

    observaciones   VARCHAR(1000) NULL,
    usuario         VARCHAR(50) NOT NULL,
    fecha           DATETIME(6) NOT NULL,

    PRIMARY KEY (id),

    KEY             idx_historial_entrega (
        entrega_id,
        id
    ),

    CONSTRAINT fk_historial_entrega
        FOREIGN KEY (entrega_id)
            REFERENCES entregas (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;