-- ============================================================================
-- SIMACMART: Datos Semilla Iniciales (Pachía, Tacna)
-- ============================================================================

-- 1. Unidades / Nodos de Atrapanieblas
INSERT INTO unidades (id_unidad, nombre, localidad, latitud, longitud, capacidad_tina_kg, activo)
VALUES 
  (1, 'Nodo Pachía Central #01', 'Pachía (Tacna) - Sector Calientes', -17.894200, -70.152300, 50.00, true),
  (2, 'Nodo Pachía Experimental #02', 'Pachía (Tacna) - Cerro Blanco', -17.881000, -70.141500, 50.00, true)
ON CONFLICT (id_unidad) DO NOTHING;

-- 2. Dispositivos Asociados
-- Hashes BCrypt correspondientes a 'clave_balanza_pachia_1' y 'clave_ecowitt_pachia_1'
INSERT INTO dispositivos (id_dispositivo, id_unidad, codigo, tipo, clave_hash, activo)
VALUES
  (1, 1, 'BAL-ESP32-01', 'BALANZA', '$2a$10$wE4i.7i5Q5L1WnZqK6Yw7e4nQyN7f8w4P5q9G6u1L7n5m4K3j2i1h', true),
  (2, 1, 'ECOWITT-GW1100', 'ESTACION', '$2a$10$wE4i.7i5Q5L1WnZqK6Yw7e4nQyN7f8w4P5q9G6u1L7n5m4K3j2i1h', true),
  (3, 2, 'BAL-ESP32-02', 'BALANZA', '$2a$10$wE4i.7i5Q5L1WnZqK6Yw7e4nQyN7f8w4P5q9G6u1L7n5m4K3j2i1h', true),
  (4, 2, 'ECOWITT-WS69', 'ESTACION', '$2a$10$wE4i.7i5Q5L1WnZqK6Yw7e4nQyN7f8w4P5q9G6u1L7n5m4K3j2i1h', true)
ON CONFLICT (codigo) DO NOTHING;

-- 3. Cuentas de Usuario Iniciales
-- Contraseña 'admin123' y 'operador123'
INSERT INTO cuentas (id_cuenta, usuario, nombre_completo, correo, password_hash, rol, activo)
VALUES
  (1, 'admin', 'Administrador del Sistema SIMACMART', 'admin@simacmart.upt.pe', '$2a$10$C4FuAitSuT0xysyR0LDi5.LhAqI5LMC9xv1.dfGFDIAt61rsH8Rwe', 'ADMINISTRADOR', true),
  (2, 'operador', 'Operador de Campo Pachía', 'operador@simacmart.upt.pe', '$2a$10$D152X61f1jZw2uoekxV/HOao3f/TS78XCCNl6DVsKkfX6nLveQmN6', 'OPERADOR', true),
  (3, 'investigador', 'Investigador de Recursos Hídricos UPT', 'investigador@simacmart.upt.pe', '$2a$10$Te4MuDwogflEJvSFEpkEruofsAARR8g8JXFC2CWkByej2zy8IT3ri', 'INVESTIGADOR', true)
ON CONFLICT (usuario) DO NOTHING;

-- 4. Modelo de Machine Learning Base
INSERT INTO modelos_ml (id_modelo, algoritmo, version, fecha_entrenamiento, error_medio_litros, activo)
VALUES
  (1, 'RandomForestRegressor (Niebla Orográfica)', 'v1.4-pachia', CURRENT_TIMESTAMP, 1.250, true)
ON CONFLICT (id_modelo) DO NOTHING;
