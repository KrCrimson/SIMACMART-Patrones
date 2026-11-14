# GUÍA DE INSTALACIÓN Y DESPLIEGUE LOCAL — SIMACMART

Esta guía detalla los pasos para clonar, configurar, compilar y ejecutar el sistema **SIMACMART** (Backend Kotlin + Frontend SPA) en cualquier computadora (Windows, Linux o macOS) de manera 100% local, autónoma y sin dependencias externas obligatorias.

---

## 1. Prerrequisitos del Sistema

Para ejecutar SIMACMART de forma local solo necesitas:

| Software | Versión Recomendada | Comando para verificar |
| :--- | :--- | :--- |
| **Java JDK** | OpenJDK 17 o 21 (LTS) | `java -version` |
| **Python** o **Node.js** | Python 3.8+ o Node.js 18+ | `python --version` o `node -v` |
| **Git** | Cualquier versión reciente | `git --version` |

> [!NOTE]
> **No es necesario instalar Gradle ni PostgreSQL**:
> * El proyecto incluye el ejecutable `gradlew` / `gradlew.bat` que descarga automáticamente la versión exacta de Gradle.
> * El sistema cuenta con **persistencia híbrida resiliente**: si no hay PostgreSQL corriendo en el puerto 5432, el backend conmuta automáticamente a una base de datos local SQLite (`data/simacmart.db`), precargando datos semilla demostrativos sin fallar.

---

## 2. Paso a Paso: Despliegue en 3 Pasos

```
                     ┌───────────────────────────────┐
                     │         NAVEGADOR WEB         │
                     │    http://localhost:3000      │
                     └───────────────┬───────────────┘
                                     │ HTTP / REST
                                     ▼
                     ┌───────────────────────────────┐
                     │    BACKEND KOTLIN (PUERTO 8080)│
                     │    com.sun.net.httpserver     │
                     └───────────────┬───────────────┘
                                     │ JDBC
                                     ▼
                     ┌───────────────────────────────┐
                     │   PERSISTENCIA RESILIENTE     │
                     │  PostgreSQL (Cloud) o SQLite  │
                     └───────────────────────────────┘
```

### Paso 1: Clonar el Repositorio

Abre tu terminal (PowerShell, CMD, Bash o Zsh) y ejecuta:

```bash
git clone https://github.com/tu-usuario/SIMACMART-Patrones.git
cd SIMACMART-Patrones
```

---

### Paso 2: Compilar y Ejecutar el Backend (Kotlin)

El backend de SIMACMART corre sobre el servidor HTTP nativo de la JVM, optimizado y sin Spring Boot.

#### En Windows (PowerShell / CMD):
```powershell
cd SIMACMART-Backend
.\gradlew.bat run
```

#### En Linux / macOS (Bash / Terminal):
```bash
cd SIMACMART-Backend
chmod +x gradlew
./gradlew run
```

#### Salida esperada en consola:
```text
=================================================================
>>> INICIANDO SISTEMA MONOLITO MODULAR SIMACMART (UPT)
>>> Arquitectura Hexagonal + Patrones GoF (Strategy, Adapter, CoR, UoW)
=================================================================
>>> [DatabaseConfig] Conexión establecida con éxito.
>>> [DatabaseInitializer] Tablas y datos semilla verificados.
>>> [ServidorHttpMonolito] Servidor iniciado en http://localhost:8080
```

> [!TIP]
> **Ejecutar Pruebas Automatizadas**:
> Para verificar que todos los patrones y componentes pasan las pruebas unitarias:
> ```powershell
> .\gradlew.bat test
> ```
> Resultado: `BUILD SUCCESSFUL` (11 tests completados con éxito).

---

### Paso 3: Iniciar el Frontend (Dashboard SPA)

Abre **otra ventana de terminal** y navega a la carpeta del Frontend:

```bash
cd SIMACMART-Patrones/SIMACMART-Frontend
```

Dado que el frontend utiliza módulos modernos ES6 (`import`/`export`), debe servirse mediante un servidor HTTP local. Elige cualquiera de estas opciones:

#### Opción A: Con Python (Cero instalaciones adicionales)
```bash
python -m http.server 3000
```

#### Opción B: Con Node.js / npx
```bash
npx serve -l 3000
```

#### Opción C: Con extensión Live Server (VS Code)
Haz clic derecho en `SIMACMART-Frontend/index.html` y selecciona **"Open with Live Server"**.

---

## 3. Acceso al Sistema

Abre tu navegador (Chrome, Edge o Firefox) e ingresa a:

👉 **`http://localhost:3000`**

### Verificación de Conectividad en la UI:
* En la esquina inferior izquierda del sidebar verás:
  * 🟢 **"Backend Conectado"** (El frontend está consumiendo la API de `http://localhost:8080`).
* En la barra superior:
  * El reloj del sistema y el selector de nodo activo: **Nodo Pachía #01**.
* En el menú lateral podrás explorar:
  1. **Dashboard en Vivo**: Telemetría en tiempo real (humedad, temperatura, litros captados).
  2. **Historial y Filtros**: Gráficos de captación y variables climáticas orográficas.
  3. **Eventos de Vaciado**: Registro de caídas de peso en la tina (Regla de negocio `RN-03`).
  4. **Predicción Hídrica**: Ejecución y conmutación del patrón **Strategy** (ML vs Histórico).
  5. **Gestión de Nodos**: Estado de balanzas ESP32 y estaciones Ecowitt.

---

## 4. Endpoints Principales del Backend (Verificación Rápida)

Puedes probar los endpoints directamente en tu navegador o con herramientas como `curl` o Postman:

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `http://localhost:8080/api/v1/monitoreo/dashboard?idUnidad=1` | KPIs de captación, clima y estado en tiempo real |
| `GET` | `http://localhost:8080/api/v1/prediccion/estimacion?idUnidad=1&estrategia=ML` | Inferencia del modelo de Machine Learning |
| `GET` | `http://localhost:8080/api/v1/prediccion/estimacion?idUnidad=1&estrategia=HISTORICO` | Cálculo estacional de línea base |
| `GET` | `http://localhost:8080/api/v1/prediccion/modelos` | Manifiesto de modelos ONNX activos |
| `POST` | `http://localhost:8080/api/v1/ingesta/balanza` | Ingesta REST de telemetría de pesaje |

### Ejemplo de Prueba con `curl` (Ingesta de Pesaje):
```bash
curl -X POST http://localhost:8080/api/v1/ingesta/balanza \
  -H "Content-Type: application/json" \
  -d "{\"dispositivoCodigo\":\"BAL-ESP32-01\",\"secuencia\":101,\"pesoKg\":28.4,\"bateriaV\":3.92,\"apiKey\":\"secret-token\"}"
```

Respuesta:
```json
{"exito":true,"mensaje":"Lectura aceptada y persistida exitosamente."}
```

### Ejemplo de Prueba (Dashboard y Monitoreo - Diagrama 4):
```bash
# Consultar Resumen Diario de Captación y Estado
curl -X GET http://localhost:8080/api/v1/monitoreo/resumen-dia \
  -H "Authorization: Bearer sesion-activa-demo"

# Escuchar Stream de Alertas en Tiempo Real (Server-Sent Events)
curl -N -X GET http://localhost:8080/api/v1/monitoreo/stream \
  -H "Authorization: Bearer sesion-activa-demo"
```

---

## 5. Solución de Problemas Comunes

### 1. El puerto 8080 está ocupado
Si tienes otro servicio (como Tomcat o Jenkins) usando el puerto 8080:
* En Windows:
  ```powershell
  $env:PORT="9090"; .\gradlew.bat run
  ```
* En Linux/Mac:
  ```bash
  PORT=9090 ./gradlew run
  ```
* Luego en `SIMACMART-Frontend/js/config.js` cambia `API_BASE_URL: 'http://localhost:9090/api/v1'`.

### 2. Error de permisos en Linux/Mac
Si `gradlew` no tiene permisos de ejecución:
```bash
chmod +x gradlew
```

---

## 6. Estructura de Carpetas del Proyecto

```
SIMACMART-Patrones/
├── SIMACMART-Backend/               # Servidor Kotlin + Arquitectura Hexagonal + Patrones GoF
│   ├── src/main/kotlin/             # Código fuente con trazabilidad institucional UPT
│   │   └── pe/edu/upt/simacmart/
│   │       ├── ingesta/             # Chain of Responsibility, MqttFacade, Unit of Work
│   │       ├── prediccion/          # Strategy (ML / Histórico), Model Adapters, Repository
│   │       ├── captacion/           # Lógica hidrológica y eventos de vaciado (RN-03)
│   │       ├── monitoreo/           # Agregaciones de telemetría para dashboard
│   │       ├── cuentas/             # Autenticación y control de acceso
│   │       └── shared/              # Servidor HTTP nativo y persistencia híbrida
│   └── src/test/kotlin/             # Batería de pruebas unitarias
│
├── SIMACMART-Frontend/              # SPA desacoplada en HTML5, CSS3 y JavaScript ES6
│   ├── index.html                   # Interfaz interactiva
│   ├── css/                         # Temas, layout y componentes
│   └── js/                          # Clientes API, charts y gestor de estado
│
├── GUIA_INSTALACION_LOCAL.md        # Esta guía de despliegue
├── README.md                        # Documentación general y arquitectura
└── .gitignore                       # Filtro de archivos privados y compilados
```
