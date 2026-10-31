-- ============================================================================
-- SIMACMART: Esquema Relacional Local Offline (SQLite)
-- Version: 1.2 (Soporte Store-and-Forward y Ejecución Autónoma)
-- ============================================================================

CREATE TABLE IF NOT EXISTS unidades (
    id_unidad INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    localidad TEXT NOT NULL,
    latitud REAL NOT NULL,
    longitud REAL NOT NULL,
    capacidad_tina_kg REAL DEFAULT 50.00,
    activo INTEGER DEFAULT 1,
    creado_en TEXT DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS dispositivos (
    id_dispositivo INTEGER PRIMARY KEY AUTOINCREMENT,
    id_unidad INTEGER NOT NULL,
    codigo TEXT UNIQUE NOT NULL,
    tipo TEXT NOT NULL CHECK (tipo IN ('BALANZA', 'ESTACION')),
    clave_hash TEXT NOT NULL,
    activo INTEGER DEFAULT 1,
    creado_en TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_unidad) REFERENCES unidades(id_unidad) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS lecturas_balanza (
    id_lectura INTEGER PRIMARY KEY AUTOINCREMENT,
    id_dispositivo INTEGER NOT NULL,
    id_unidad INTEGER NOT NULL,
    secuencia INTEGER NOT NULL,
    fecha_hora TEXT NOT NULL,
    peso_kg REAL NOT NULL,
    valida INTEGER DEFAULT 1,
    recibido_en TEXT DEFAULT CURRENT_TIMESTAMP,
    sincronizado INTEGER DEFAULT 1,
    UNIQUE (id_dispositivo, secuencia),
    FOREIGN KEY (id_dispositivo) REFERENCES dispositivos(id_dispositivo),
    FOREIGN KEY (id_unidad) REFERENCES unidades(id_unidad)
);

CREATE TABLE IF NOT EXISTS lecturas_estacion (
    id_lectura INTEGER PRIMARY KEY AUTOINCREMENT,
    id_dispositivo INTEGER NOT NULL,
    id_unidad INTEGER NOT NULL,
    fecha_hora TEXT NOT NULL,
    temperatura_c REAL,
    humedad_pct REAL,
    viento_ms REAL,
    radiacion_wm2 REAL,
    lluvia_mm REAL,
    valida INTEGER DEFAULT 1,
    recibido_en TEXT DEFAULT CURRENT_TIMESTAMP,
    sincronizado INTEGER DEFAULT 1,
    FOREIGN KEY (id_dispositivo) REFERENCES dispositivos(id_dispositivo),
    FOREIGN KEY (id_unidad) REFERENCES unidades(id_unidad)
);

CREATE TABLE IF NOT EXISTS eventos (
    id_evento INTEGER PRIMARY KEY AUTOINCREMENT,
    id_unidad INTEGER NOT NULL,
    tipo TEXT NOT NULL DEFAULT 'vaciado',
    fecha_hora TEXT NOT NULL,
    peso_antes_kg REAL NOT NULL,
    peso_despues_kg REAL NOT NULL,
    volumen_litros REAL NOT NULL,
    descripcion TEXT,
    FOREIGN KEY (id_unidad) REFERENCES unidades(id_unidad)
);

CREATE TABLE IF NOT EXISTS captacion_agua (
    id_captacion INTEGER PRIMARY KEY AUTOINCREMENT,
    id_unidad INTEGER NOT NULL,
    inicio_periodo TEXT NOT NULL,
    tipo_periodo TEXT NOT NULL CHECK (tipo_periodo IN ('HORA', 'DIA', 'MES')),
    litros_captados REAL NOT NULL,
    calculado_en TEXT DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (id_unidad, inicio_periodo, tipo_periodo),
    FOREIGN KEY (id_unidad) REFERENCES unidades(id_unidad)
);

CREATE TABLE IF NOT EXISTS pronostico (
    id_pronostico INTEGER PRIMARY KEY AUTOINCREMENT,
    id_unidad INTEGER NOT NULL,
    fecha_hora TEXT NOT NULL,
    temperatura_c REAL,
    humedad_pct REAL,
    viento_ms REAL,
    radiacion_wm2 REAL,
    fuente TEXT DEFAULT 'Open-Meteo',
    FOREIGN KEY (id_unidad) REFERENCES unidades(id_unidad)
);

CREATE TABLE IF NOT EXISTS modelos_ml (
    id_modelo INTEGER PRIMARY KEY AUTOINCREMENT,
    algoritmo TEXT NOT NULL,
    version TEXT NOT NULL,
    fecha_entrenamiento TEXT NOT NULL,
    error_medio_litros REAL,
    activo INTEGER DEFAULT 1
);

CREATE TABLE IF NOT EXISTS predicciones (
    id_prediccion INTEGER PRIMARY KEY AUTOINCREMENT,
    id_unidad INTEGER NOT NULL,
    id_modelo INTEGER,
    fecha_inicio TEXT NOT NULL,
    litros_estimados REAL NOT NULL,
    confianza_pct REAL,
    estrategia TEXT NOT NULL,
    generado_en TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_unidad) REFERENCES unidades(id_unidad),
    FOREIGN KEY (id_modelo) REFERENCES modelos_ml(id_modelo)
);

CREATE TABLE IF NOT EXISTS cuentas (
    id_cuenta INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario TEXT UNIQUE NOT NULL,
    nombre_completo TEXT NOT NULL,
    correo TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    rol TEXT NOT NULL CHECK (rol IN ('ADMINISTRADOR', 'OPERADOR', 'INVESTIGADOR')),
    activo INTEGER DEFAULT 1,
    creado_en TEXT DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_balanza_unidad_fecha ON lecturas_balanza(id_unidad, fecha_hora DESC);
CREATE INDEX IF NOT EXISTS idx_estacion_unidad_fecha ON lecturas_estacion(id_unidad, fecha_hora DESC);
CREATE INDEX IF NOT EXISTS idx_captacion_unidad_fecha ON captacion_agua(id_unidad, inicio_periodo DESC);
