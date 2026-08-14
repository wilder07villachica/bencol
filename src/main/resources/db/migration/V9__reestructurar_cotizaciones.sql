ALTER TABLE cotizaciones
DROP
COLUMN imagen_nombre,
DROP
COLUMN imagen_tipo,
DROP
COLUMN imagen_ruta,
ADD COLUMN porcentaje_impuesto DECIMAL(5,2) NOT NULL DEFAULT 0.00,
ADD COLUMN monto_impuesto DECIMAL(12,2) NOT NULL DEFAULT 0.00,
ADD COLUMN condiciones_pago VARCHAR(500) NULL,
ADD COLUMN plazo_entrega VARCHAR(255) NULL,
MODIFY COLUMN observacion VARCHAR(1000) NULL;

ALTER TABLE detalles_cotizaciones
DROP
COLUMN modalidad_envase,
ADD COLUMN frecuencia_abastecimiento VARCHAR(255) NULL,
ADD COLUMN descripcion_adicional VARCHAR(500) NULL,
ADD COLUMN imagen_nombre VARCHAR(255) NULL,
ADD COLUMN imagen_tipo VARCHAR(100) NULL,
ADD COLUMN imagen_ruta VARCHAR(500) NULL;