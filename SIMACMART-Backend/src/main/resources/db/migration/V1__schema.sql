-- ============================================================================
-- SIMACMART: Esquema Relacional de Base de Datos (PostgreSQL)
-- Version: 1.2 (Alineado con Diccionario de Datos v1.2 y Monolito Modular)
-- ============================================================================

CREATE TABLE IF NOT EXISTS unidades (
    id_unidad SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    localidad VARCHAR(150) NOT NULL,
    latitud NUMERIC(9,6) NOT NULL,
    longitud NUMERIC(9,6) NOT NULL,
    capacidad_tina_kg NUMERIC(6,2) DEFAULT 50.00,
    activo BOOLEAN DEFAULT TRUE,
    creado_en TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS dispositivos (
    id_dispositivo SERIAL PRIMARY KEY,
    id_unidad INT NOT NULL REFERENCES unidades(id_unidad) ON DELETE CASCADE,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('BALANZA', 'ESTACION')),
    clave_hash VARCHAR(255) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    creado_en TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS lecturas_balanza (
    id_lectura BIGSERIAL PRIMARY KEY,
    id_dispositivo INT NOT NULL REFERENCES dispositivos(id_dispositivo),
    id_unidad INT NOT NULL REFERENCES unidades(id_unidad),
    secuencia BIGINT NOT NULL,
    fecha_hora TIMESTAMPTZ NOT NULL,
    peso_kg NUMERIC(6,3) NOT NULL,
    valida BOOLEAN DEFAULT TRUE,
    recibido_en TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    sincronizado BOOLEAN DEFAULT TRUE,
    CONSTRAINT uq_balanza_seq UNIQUE (id_dispositivo, secuencia)
);

CREATE TABLE IF NOT EXISTS lecturas_estacion (
    id_lectura BIGSERIAL PRIMARY KEY,
    id_dispositivo INT NOT NULL REFERENCES dispositivos(id_dispositivo),
    id_unidad INT NOT NULL REFERENCES unidades(id_unidad),
    fecha_hora TIMESTAMPTZ NOT NULL,
    temperatura_c NUMERIC(5,2),
    humedad_pct NUMERIC(5,2),
    viento_ms NUMERIC(5,2),
    radiacion_wm2 NUMERIC(6,2),
    lluvia_mm NUMERIC(6,2),
    valida BOOLEAN DEFAULT TRUE,
    recibido_en TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    sincronizado BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS eventos (
    id_evento SERIAL PRIMARY KEY,
    id_unidad INT NOT NULL REFERENCES unidades(id_unidad),
    tipo VARCHAR(30) NOT NULL DEFAULT 'vaciado',
    fecha_hora TIMESTAMPTZ NOT NULL,
    peso_antes_kg NUMERIC(6,3) NOT NULL,
    peso_despues_kg NUMERIC(6,3) NOT NULL,
    volumen_litros NUMERIC(6,2) NOT NULL,
    descripcion TEXT
);

CREATE TABLE IF NOT EXISTS captacion_agua (
    id_captacion SERIAL PRIMARY KEY,
    id_unidad INT NOT NULL REFERENCES unidades(id_unidad),
    inicio_periodo TIMESTAMPTZ NOT NULL,
    tipo_periodo VARCHAR(10) NOT NULL CHECK (tipo_periodo IN ('HORA', 'DIA', 'MES')),
    litros_captados NUMERIC(8,2) NOT NULL,
    calculado_en TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_captacion_periodo UNIQUE (id_unidad, inicio_periodo, tipo_periodo)
);

CREATE TABLE IF NOT EXISTS pronostico (
    id_pronostico SERIAL PRIMARY KEY,
    id_unidad INT NOT NULL REFERENCES unidades(id_unidad),
    fecha_hora TIMESTAMPTZ NOT NULL,
    temperatura_c NUMERIC(5,2),
    humedad_pct NUMERIC(5,2),
    viento_ms NUMERIC(5,2),
    radiacion_wm2 NUMERIC(6,2),
    fuente VARCHAR(50) DEFAULT 'Open-Meteo'
);

CREATE TABLE IF NOT EXISTS modelos_ml (
    id_modelo SERIAL PRIMARY KEY,
    algoritmo VARCHAR(100) NOT NULL,
    version VARCHAR(20) NOT NULL,
    fecha_entrenamiento TIMESTAMPTZ NOT NULL,
    error_medio_litros NUMERIC(6,3),
    activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS predicciones (
    id_prediccion SERIAL PRIMARY KEY,
    id_unidad INT NOT NULL REFERENCES unidades(id_unidad),
    id_modelo INT REFERENCES modelos_ml(id_modelo),
    fecha_inicio TIMESTAMPTZ NOT NULL,
    litros_estimados NUMERIC(8,2) NOT NULL,
    confianza_pct NUMERIC(5,2),
    estrategia VARCHAR(50) NOT NULL,
    generado_en TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cuentas (
    id_cuenta SERIAL PRIMARY KEY,
    usuario VARCHAR(50) UNIQUE NOT NULL,
    nombre_completo VARCHAR(120) NOT NULL,
    correo VARCHAR(120) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('ADMINISTRADOR', 'OPERADOR', 'INVESTIGADOR')),
    activo BOOLEAN DEFAULT TRUE,
    creado_en TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_balanza_unidad_fecha ON lecturas_balanza(id_unidad, fecha_hora DESC);
CREATE INDEX IF NOT EXISTS idx_estacion_unidad_fecha ON lecturas_estacion(id_unidad, fecha_hora DESC);
CREATE INDEX IF NOT EXISTS idx_captacion_unidad_fecha ON captacion_agua(id_unidad, inicio_periodo DESC);
