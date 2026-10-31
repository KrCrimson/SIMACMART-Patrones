# SIMACMART — Sistema Inteligente y Modular de Atrapanieblas

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-purple.svg?logo=kotlin)](https://kotlinlang.org/)
[![JVM](https://img.shields.io/badge/JVM-17%20%7C%2021-red.svg?logo=openjdk)](https://openjdk.org/)
[![Architecture](https://img.shields.io/badge/Architecture-Modular%20Monolith%20%2B%20Hexagonal-blue.svg)](#-arquitectura-del-sistema)
[![Design Patterns](https://img.shields.io/badge/Patterns-Strategy%20%7C%20Adapter%20%7C%20Repository-orange.svg)](#-patrones-de-diseño-implementados)
[![Frontend](https://img.shields.io/badge/Frontend-Vanilla%20ES6%20SPA-yellow.svg)](#-frontend-spa-desacoplado)
[![Knowledge Graph](https://img.shields.io/badge/Knowledge%20Graph-graphify%20enabled-green.svg)](#-grafo-de-conocimiento-graphify)

> **Curso:** Patrones de Software  
> **Institución:** Universidad Privada de Tacna — Escuela Profesional de Ingeniería de Sistemas  
> **Caso de Estudio:** Captación, Monitoreo y Acondicionamiento de Agua Atmosférica en Atrapanieblas (Pachía, Tacna).

---

## 📖 Descripción General

**SIMACMART** es una solución integral orientada a la ingeniería de software moderna para telemetría IoT, cálculo hidrológico y analítica predictiva en sistemas de atrapanieblas. Diseñado bajo las restricciones académicas y requerimientos de producción:
* **Backend de Alto Rendimiento:** Implementado en **Kotlin puro** sobre el servidor HTTP nativo de la JVM (`com.sun.net.httpserver.HttpServer`), **sin depender de Spring Boot ni frameworks invasivos**.
* **Frontend Desacoplado:** SPA interactiva en **JavaScript Vanilla (ES6)** con visualización de series temporales en **Chart.js** y modo de contingencia/demo automático.
* **Resiliencia en Borde:** Arquitectura de almacenamiento híbrido (**PostgreSQL** central en la nube + búfer local en **SQLite**) con el patrón *Store-and-Forward*.
* **Inteligencia Artificial Desacoplada:** Predicción de captación hídrica mediante el patrón **Strategy**, permitiendo alternar entre modelos de Machine Learning y medias históricas sin alterar el núcleo de negocio.

---

## 🏛️ Arquitectura del Sistema

El sistema implementa un **Monolito Modular con Arquitectura Hexagonal (Puertos y Adaptadores)** en cada módulo, asegurando independencia de frameworks, alta cohesión y bajo acoplamiento:

```
SIMACMART-Patrones/
├── SIMACMART-Backend/               # Núcleo modular en Kotlin
│   ├── src/main/kotlin/pe/edu/upt/simacmart/
│   │   ├── Main.kt                  # Orquestador y ensamblador de dependencias
│   │   ├── shared/                  # Kernel compartido (HTTP nativo, DB config, Result<T>)
│   │   ├── cuentas/                 # Bounded Context: Autenticación, Roles y BCrypt
│   │   ├── ingesta/                 # Bounded Context: Telemetría IoT (Balanza + Estación Ecowitt)
│   │   ├── captacion/               # Bounded Context: Arqueo hídrico y eventos de vaciado (RN-03)
│   │   ├── monitoreo/               # Bounded Context: KPIs, telemetría y endpoints de Dashboard
│   │   └── prediccion/              # Bounded Context: Motor de Machine Learning (Patrón Strategy)
│   ├── Dockerfile                   # Empaquetado de contenedor multi-stage
│   ├── docker-compose.yml           # Orquestación con PostgreSQL 16 y volumen persistente
│   └── render.yaml                  # Especificación de despliegue cloud en Render
│
├── SIMACMART-Frontend/              # Dashboard SPA desacoplado
│   ├── index.html                   # Shell interactivo con vistas dinámicas
│   ├── css/                         # Sistema de diseño hídrico (Glassmorphism & Dark Mode)
│   └── js/                          # Arquitectura modular ES6 (API client, charts, store)
│
└── graphify-out/                    # Grafo de conocimiento y topología del código
    ├── graph.html                   # Visualizador interactivo 2D del sistema
    ├── graph.json                   # Estructura del grafo en formato GraphRAG
    └── GRAPH_REPORT.md              # Reporte de cohesión, god nodes y comunidades
```

---

## 🧩 Patrones de Diseño Implementados

### 1. Patrón Strategy (Módulo de Predicción)
Permite intercambiar dinámicamente el algoritmo de predicción climática/hídrica en tiempo de ejecución:
* **Puerto de Salida (Strategy Interface):** `EstrategiaPrediccionPort`
* **Estrategia Concreta 1:** `PrediccionConModeloMLStrategy` (consumo de regresores e inferencia de series temporales).
* **Estrategia Concreta 2:** `PromedioHistoricoStrategy` (fallback heurístico basado en registros estacionales).
* **Contexto:** `ServicioPrediccion` (ejecuta la estimación sin conocer los detalles de cálculo interno).

### 2. Patrón Store-and-Forward / Repository Resiliente (Módulo de Ingesta)
Diseñado para la geografía de Pachía donde la conectividad 4G suele experimentar microcortes:
* **Adaptador de Persistencia:** `IngestaPersistenceAdapter`
* **Flujo Operativo:** Intenta persistir primariamente en **PostgreSQL**. Si se detecta un corte de red o timeout, conmuta automáticamente a un búfer local transaccional en **SQLite**, sincronizando los lotes al restablecerse el enlace.

### 3. Patrón Ports & Adapters (Hexagonal Architecture)
* **Puertos de Entrada (Inbound Ports / Use Cases):** Interfaces como `CalcularCaptacionUseCase`, `GenerarPrediccionUseCase` y `LoginUseCase` que exponen la intención del negocio.
* **Adaptadores de Entrada (Inbound Adapters):** Controladores HTTP ligeros (`AuthController`, `IngestaHttpController`, `DashboardHttpController`) que traducen DTOs hacia los casos de uso.
* **Puertos de Salida (Outbound Ports):** Contratos de persistencia (`CaptacionRepositoryPort`, `CuentaRepositoryPort`).
* **Adaptadores de Salida (Outbound Adapters):** Implementaciones JDBC desacopladas (`PostgresCuentaRepository`, `CaptacionRepositoryAdapter`).

---

## 🌐 Módulos del Sistema (Bounded Contexts)

| Módulo | Casos de Uso | Responsabilidad Central |
| :--- | :--- | :--- |
| **Cuentas** | UC-13, UC-14, UC-17 | Gestión de usuarios, perfiles de campo y contraseñas hasheadas con BCrypt. |
| **Ingesta IoT** | UC-01 al UC-04 | Recepción de tramas HTTP/REST de balanzas de precisión ESP32 y estaciones meteorológicas Ecowitt. |
| **Captación** | UC-05, Regla RN-03 | Detección de caídas de peso (vaciados de tina) y agregación de litros recolectados por periodo. |
| **Monitoreo** | UC-06 al UC-09 | Cálculo de KPIs en tiempo real, niveles de niebla, series temporales y exportación CSV. |
| **Predicción** | UC-10 al UC-12 | Pronóstico de litros captables a 30 días mediante el patrón Strategy. |

---

## 🚀 Guía de Instalación y Ejecución

### Prerrequisitos
* **Java Development Kit (JDK):** Versión 17 o 21.
* **Docker & Docker Compose** (Opcional, para ejecución en contenedores).
* **Navegador Web Moderno** (Chrome, Edge, Firefox).

---

### Opción A: Ejecución con Docker Compose (Recomendada)
Inicia PostgreSQL y el backend Kotlin con un solo comando:
```bash
cd SIMACMART-Backend
docker-compose up --build -d
```
* **Backend API:** `http://localhost:8080`
* **PostgreSQL:** `localhost:5432` (Base de datos: `simacmart_db`)

---

### Opción B: Ejecución Local en Terminal

#### 1. Iniciar el Backend (Kotlin)
```bash
cd SIMACMART-Backend
# En Windows:
.\gradlew.bat run
# En Linux / macOS:
./gradlew run
```
El servidor HTTP nativo escuchará en el puerto `8080`.

#### 2. Iniciar el Frontend (Vanilla SPA)
Dado que utiliza módulos nativos de JavaScript (`type="module"`), debe servirse por HTTP:
```bash
cd SIMACMART-Frontend
# Con Python (sin dependencias):
python -m http.server 3000
```
Abre en tu navegador: 👉 **`http://localhost:3000`**

* **Indicador de Conexión:**
  * 🟢 **"Backend Conectado (Live)":** Comunicándose con la API en tiempo real.
  * 🟡 **"Modo Local / Demo":** Si el backend está apagado, el cliente presenta automáticamente telemetría sintética sin bloquear la UI.

---

## 🧠 Grafo de Conocimiento (graphify)

Este repositorio ha sido analizado y mapeado con la tecnología **graphify**, generando un grafo estructurado de relaciones arquitectónicas y dependencias:

* **Topología:** 253 nodos · 532 aristas · 14 comunidades temáticas.
* **Extracción:** 99% estructurado (AST determinista) + 1% semántico.
* **Componentes Principales (God Nodes):**
  1. `main()` — Ensamblador de dependencias y contenedor IoC manual.
  2. `EventoVaciado` — Entidad crítica de dominio para el arqueo hídrico.
  3. `Cuenta` — Entidad raíz de autenticación y seguridad.
  4. `DatabaseConfig` — Punto de articulación de persistencia dual.
  5. `ServicioPrediccion` — Contexto del patrón Strategy.

### Visualización del Grafo:
Abre el archivo interactivo generado en tu navegador:
```
graphify-out/graph.html
```
O consulta el reporte detallado en:
```
graphify-out/GRAPH_REPORT.md
```

---

## 👥 Equipo de Desarrollo

* **Universidad Privada de Tacna**  
* **Facultad de Ingeniería — Escuela Profesional de Ingeniería de Sistemas**  
* **Asignatura:** Patrones de Software  
