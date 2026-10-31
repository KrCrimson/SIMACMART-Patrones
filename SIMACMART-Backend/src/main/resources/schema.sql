-- ====================================================================
-- SIMACMART v1.2 — SCRIPT DDL DE BASE DE DATOS (POSTGRESQL)
-- Universidad Privada de Tacna — Patrones de Software 2026
-- ====================================================================

-- 1. Unidades Atrapanieblas (Nodos de Campo)
CREATE TABLE IF NOT EXISTS unidades (
    id_unidad SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    localidad VARCHAR(100) NOT NULL,
    latitud NUMERIC(9,6) NOT NULL,
    longitud NUMERIC(9,6) NOT NULL,
    capacidad_tina_kg NUMERIC(6,2),
    fecha_instalacion DATE DEFAULT CURRENT_DATE
);

-- 2. Dispositivos Asociados (Balanza, Estación Ecowitt)
CREATE TABLE IF NOT EXISTS dispositivos (
    id_dispositivo SERIAL PRIMARY KEY,
    id_unidad INT NOT NULL REFERENCES unidades(id_unidad) ON DELETE CASCADE,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('estacion', 'balanza')),
    nombre VARCHAR(100) NOT NULL,
    api_key_hash VARCHAR(255) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    intervalo_esperado_seg INT NOT NULL DEFAULT 300,
    ultima_lectura_ts TIMESTAMPTZ,
    fecha_registro TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. Lecturas de Estación Meteorológica (Ecowitt)
CREATE TABLE IF NOT EXISTS lecturas_estacion (
    id_lectura BIGSERIAL PRIMARY KEY,
    id_dispositivo INT NOT NULL REFERENCES dispositivos(id_dispositivo),
    ts TIMESTAMPTZ NOT NULL,
    recibido_ts TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    temperatura_c NUMERIC(4,1),
    humedad_pct NUMERIC(4,1),
    viento_ms NUMERIC(4,1),
    radiacion_wm2 NUMERIC(6,1),
    lluvia_mm NUMERIC(5,1),
    valida BOOLEAN NOT NULL DEFAULT TRUE,
    motivo_invalida VARCHAR(100)
);

-- 4. Lecturas de Balanza Digital (Celdas HX711)
CREATE TABLE IF NOT EXISTS lecturas_balanza (
    id_lectura BIGSERIAL PRIMARY KEY,
    id_dispositivo INT NOT NULL REFERENCES dispositivos(id_dispositivo),
    seq BIGINT NOT NULL,
    ts TIMESTAMPTZ NOT NULL,
    recibido_ts TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    peso_kg NUMERIC(7,3),
    muestras SMALLINT,
    dispersion_kg NUMERIC(6,3),
    bateria_v NUMERIC(4,2),
    valida BOOLEAN NOT NULL DEFAULT TRUE,
    motivo_invalida VARCHAR(100),
    CONSTRAINT uq_dispositivo_seq UNIQUE (id_dispositivo, seq)
);

-- 5. Eventos de la Unidad (Vaciados de Tina, Mantenimiento)
CREATE TABLE IF NOT EXISTS eventos (
    id_evento SERIAL PRIMARY KEY,
    id_unidad INT NOT NULL REFERENCES unidades(id_unidad),
    tipo VARCHAR(20) NOT NULL DEFAULT 'vaciado',
    ts TIMESTAMPTZ NOT NULL,
    peso_antes_kg NUMERIC(7,3),
    peso_despues_kg NUMERIC(7,3),
    nota VARCHAR(255)
);

-- 6. Captación Agregada por Período (Hora / Día)
CREATE TABLE IF NOT EXISTS captacion_periodo (
    id_captacion SERIAL PRIMARY KEY,
    id_unidad INT NOT NULL REFERENCES unidades(id_unidad),
    tipo_periodo VARCHAR(10) NOT NULL CHECK (tipo_periodo IN ('HORA', 'DIA')),
    inicio_periodo TIMESTAMPTZ NOT NULL,
    litros_captados NUMERIC(8,3) NOT NULL,
    calculado_ts TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_unidad_periodo UNIQUE (id_unidad, tipo_periodo, inicio_periodo)
);

-- 7. Pronósticos Climáticos de APIs Externas
CREATE TABLE IF NOT EXISTS pronosticos (
    id_pronostico SERIAL PRIMARY KEY,
    id_unidad INT NOT NULL REFERENCES unidades(id_unidad),
    fecha_objetivo DATE NOT NULL,
    humedad_media NUMERIC(4,1),
    temperatura_media NUMERIC(4,1),
    viento_medio NUMERIC(4,1),
    fuente VARCHAR(50) DEFAULT 'Open-Meteo',
    consultado_ts TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 8. Modelos de Machine Learning Versionados
CREATE TABLE IF NOT EXISTS modelos_ml (
    id_modelo SERIAL PRIMARY KEY,
    algoritmo VARCHAR(50) NOT NULL,
    version_etiqueta VARCHAR(20) NOT NULL,
    fecha_entrenamiento TIMESTAMPTZ NOT NULL,
    mae_litros NUMERIC(6,3),
    r2_score NUMERIC(5,4),
    activo BOOLEAN NOT NULL DEFAULT FALSE,
    ruta_artefacto VARCHAR(255) NOT NULL
);

-- 9. Predicciones Mensuales de Captación
CREATE TABLE IF NOT EXISTS predicciones (
    id_prediccion SERIAL PRIMARY KEY,
    id_unidad INT NOT NULL REFERENCES unidades(id_unidad),
    id_modelo INT REFERENCES modelos_ml(id_modelo),
    mes_objetivo VARCHAR(7) NOT NULL, -- 'YYYY-MM'
    litros_predichos NUMERIC(8,2) NOT NULL,
    litros_reales NUMERIC(8,2),
    error_pct NUMERIC(5,2),
    fecha_generacion TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 10. Cuentas y Control de Acceso (UC-13, UC-14, UC-17)
CREATE TABLE IF NOT EXISTS cuentas (
    id_cuenta SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('ADMINISTRADOR', 'USUARIO')),
    nombre_completo VARCHAR(100) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Datos iniciales de prueba
INSERT INTO unidades (nombre, localidad, latitud, longitud, capacidad_tina_kg)
VALUES ('Atrapanieblas Piloto 01', 'Pachía - Sector Alto', -17.892300, -70.154200, 200.0)
ON CONFLICT DO NOTHING;

INSERT INTO cuentas (username, password_hash, rol, nombre_completo)
VALUES ('admin', '$2a$10$wT0Xq5g.yqI4WJz3.F8Hjei1rA7P/0dE5sM9fF8g6h5j4k3l2m1n.', 'ADMINISTRADOR', 'Administrador Principal')
ON CONFLICT DO NOTHING;
