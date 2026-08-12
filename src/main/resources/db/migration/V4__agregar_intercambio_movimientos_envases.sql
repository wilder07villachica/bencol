ALTER TABLE movimientos_envases
    MODIFY COLUMN tipo_movimiento ENUM(
    'COMPRA',
    'PRESTAMO',
    'DEVOLUCION',
    'INTERCAMBIO',
    'CONVERSION_COMPRA',
    'AJUSTE'
    ) NOT NULL;