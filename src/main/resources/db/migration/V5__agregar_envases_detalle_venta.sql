ALTER TABLE detalles_ventas
    ADD COLUMN envases_devueltos INT DEFAULT NULL AFTER cantidad,
ADD COLUMN modalidad_envase ENUM(
    'COMPRA',
    'PRESTAMO',
    'DEVOLUCION',
    'INTERCAMBIO',
    'CONVERSION_COMPRA',
    'AJUSTE'
) DEFAULT NULL AFTER envases_devueltos;

ALTER TABLE detalles_ventas
    ADD CONSTRAINT chk_envases_devueltos_no_negativo
        CHECK (envases_devueltos IS NULL OR envases_devueltos >= 0);