CREATE TABLE cotizaciones
(
    id                  BIGINT         NOT NULL AUTO_INCREMENT,
    codigo              VARCHAR(20)    NOT NULL,
    empresa_id          BIGINT         NOT NULL,
    cliente_id          BIGINT         NOT NULL,
    subtotal            DECIMAL(12, 2) NOT NULL,
    total               DECIMAL(12, 2) NOT NULL,
    estado              VARCHAR(255)   NOT NULL,
    fecha_vencimiento   DATE NULL,
    observacion         VARCHAR(500) NULL,
    imagen_nombre       VARCHAR(255) NULL,
    imagen_tipo         VARCHAR(100) NULL,
    imagen_ruta         VARCHAR(500) NULL,
    creado_por          VARCHAR(50) NULL,
    actualizado_por     VARCHAR(50) NULL,
    fecha_creacion      DATETIME(6) NOT NULL,
    fecha_actualizacion DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_cotizaciones_codigo UNIQUE (codigo),
    CONSTRAINT fk_cotizaciones_empresa
        FOREIGN KEY (empresa_id)
            REFERENCES empresas (id),
    CONSTRAINT fk_cotizaciones_cliente
        FOREIGN KEY (cliente_id)
            REFERENCES clients (id)
);

CREATE TABLE detalles_cotizaciones
(
    id               BIGINT         NOT NULL AUTO_INCREMENT,
    cotizacion_id    BIGINT         NOT NULL,
    producto_id      BIGINT         NOT NULL,
    cantidad         INT            NOT NULL,
    modalidad_envase VARCHAR(255) NULL,
    precio_unitario  DECIMAL(12, 2) NOT NULL,
    subtotal         DECIMAL(12, 2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_detalles_cotizaciones_cotizacion
        FOREIGN KEY (cotizacion_id)
            REFERENCES cotizaciones (id),
    CONSTRAINT fk_detalles_cotizaciones_producto
        FOREIGN KEY (producto_id)
            REFERENCES productos (id)
);