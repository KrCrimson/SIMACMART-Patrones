# SIMACMART — Sistema Inteligente y Modular de Atrapanieblas

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-purple.svg?logo=kotlin)](https://kotlinlang.org/)
[![JVM](https://img.shields.io/badge/JVM-17%20%7C%2021-red.svg?logo=openjdk)](https://openjdk.org/)
[![Architecture](https://img.shields.io/badge/Architecture-Modular%20Monolith%20%2B%20Hexagonal-blue.svg)](#-arquitectura-del-sistema)
[![Design Patterns](https://img.shields.io/badge/Patterns-Strategy%20%7C%20Adapter%20%7C%20Chain%20of%20Resp%20%7C%20Unit%20of%20Work-orange.svg)](#-patrones-de-diseño-implementados)
[![Frontend](https://img.shields.io/badge/Frontend-Vanilla%20ES6%20SPA-yellow.svg)](#-frontend-spa-desacoplado)
[![Build Status](https://img.shields.io/badge/Tests-11%2F11%20Passing-brightgreen.svg)](#-pruebas-automatizadas)

> **Curso:** Patrones de Software  
> **Institución:** Universidad Privada de Tacna — Escuela Profesional de Ingeniería de Sistemas  
> **Docente:** Ing. Alberto Flor Rodríguez  
> **Autores:** Hernández y Arce  
> **Caso de Estudio:** Captación, Monitoreo y Acondicionamiento de Agua Atmosférica en Atrapanieblas (Pachía, Tacna).

---

## 📖 Descripción General

**SIMACMART** es una solución de ingeniería de software para telemetría IoT, cálculo hidrológico y analítica predictiva en sistemas de atrapanieblas. Diseñado bajo las mejores prácticas arquitectónicas de bajo acoplamiento y alta cohesión:
* **Backend de Alto Rendimiento:** Implementado en **Kotlin puro** sobre el servidor HTTP nativo de la JVM (`com.sun.net.httpserver.HttpServer`), sin frameworks invasivos.
* **Frontend Desacoplado:** SPA interactiva en **JavaScript Vanilla (ES6)** con visualización de series temporales en **Chart.js** y cliente API resiliente.
* **Resiliencia en Persistencia:** Soporte dual (PostgreSQL central en la nube + búfer local en SQLite) con transaccionalidad mediante **Unit of Work**.
* **Inteligencia Artificial Desacoplada:** Predicción de captación hídrica mediante el patrón **Strategy**, permitiendo alternar entre modelos de Machine Learning (ONNX) y medias históricas sin alterar el núcleo de negocio.

---

## 🏛️ Arquitectura del Sistema

El sistema implementa un **Monolito Modular con Arquitectura Hexagonal (Puertos y Adaptadores)** en cada módulo:

```
SIMACMART-Patrones/
├── SIMACMART-Backend/               # Núcleo modular en Kotlin
│   ├── src/main/kotlin/pe/edu/upt/simacmart/
│   │   ├── Main.kt                  # Composition Root e inyección de dependencias
│   │   ├── ingesta/                 # Bounded Context: Telemetría IoT (Balanza + Estación Ecowitt)
│   │   │   ├── application/         # Puertos Inbound/Outbound y ServicioIngesta
│   │   │   ├── domain/              # Entidad Lectura y Cadena de Responsabilidad (Validación)
│   │   │   └── infrastructure/      # MqttFacade, EcowittController y JdbcUnitOfWork
│   │   ├── prediccion/              # Bounded Context: Predicción Hídrica (Patrón Strategy + Adapter)
│   │   │   ├── application/         # ServicioPrediccionMensual y Puertos Hexagonales
│   │   │   ├── domain/              # EntradaPrediccion, Estimacion, ModeloVersionado
│   │   │   └── infrastructure/      # PrediccionConModeloML, PromedioHistorico, Repositorios
│   │   ├── captacion/               # Bounded Context: Dinámica hidrológica y eventos de vaciado (RN-03)
│   │   ├── monitoreo/               # Bounded Context: KPIs, telemetría y endpoints de Dashboard
│   │   ├── cuentas/                 # Bounded Context: Autenticación, Roles y BCrypt
│   │   └── shared/                  # Servidor HTTP nativo, configuración JDBC y ResilientDataSource
│   └── src/test/kotlin/             # Pruebas unitarias de integración
│
├── SIMACMART-Frontend/              # Dashboard SPA desacoplado
│   ├── index.html                   # Shell interactivo con vistas dinámicas
│   ├── css/                         # Sistema de diseño hídrico (Glassmorphism & Dark Mode)
│   └── js/                          # Arquitectura modular ES6 (API client, charts, store)
│
├── GUIA_INSTALACION_LOCAL.md        # Guía paso a paso de despliegue local
└── README.md                        # Documentación general y arquitectura
```

---

## 🧩 Patrones de Diseño Implementados (Alineación con Diagramas UML)

### 1. Ingesta de Datos: Chain of Responsibility, Observer, Facade y Adapter
* **Cadena de Responsabilidad:** Valida sucesivamente campos obligatorios (`CamposHandler`), coherencia temporal (`FechaHandler`) y rangos físicos de pesaje y clima (`RangosHandler`).
* **Fachada y Observador:** `MqttFacade` centraliza las suscripciones de red, notificando al observador `ReceptorBalanza`.
* **Adaptadores:** `BalanzaAdapter` y `EcowittAdapter` traducen payloads heterogéneos hacia `DatosLectura`.

### 2. Persistencia: Unit of Work y Repository
* **Unit of Work:** `JdbcUnitOfWork` asegura atomicidad transaccional: adquiere la conexión del pool, inicializa `JdbcLecturaRepository` y `JdbcDispositivoRepository`, confirma la transacción y revierte ante error.
* **Idempotencia:** Inserción condicionada a la restricción `UNIQUE(dispositivoId, claveOrigen)` sin pérdida de lecturas anómalas.

### 3. Predicción Hídrica: Strategy y Adapter (Hernández y Arce)
* **Contexto:** `ServicioPrediccionMensual` orquesta la recolección de entradas climáticas y delega el cálculo a la estrategia activa.
* **Estrategia ML:** `PrediccionConModeloML` se desacopla del motor físico mediante los puertos `ObtenerModeloActivo` y `EvaluarModelo`.
* **Estrategia Histórica:** `PromedioHistoricoMensual` actúa como línea base estacional de alta disponibilidad.

### 4. Dashboard de Monitoreo: Adapter y Observer (Supervisión y Alertas)
* **Controlador HTTP (Adapter):** `DashboardHttpAdapter` expone consultas de resumen diario, historial y comparación, autenticando sesión con `AutenticarSesion` (ServicioCuentas) y retornando 401 si expira.
* **Notificaciones en Tiempo Real (Adapter & Observer):** `DashboardNotificacionesAdapter` implementa `ObservadorMonitoreo` emitiendo eventos Server-Sent Events (SSE) hacia el frontend con canal de latido (Heartbeat cada 20s).
* **Supervisión Activa de Dispositivos (Subject):** `SupervisorDispositivos` actúa como sujeto observable; junto con `PlanificadorSupervision` (tick cada 30s), vigila que ningún dispositivo supere los 10 minutos de silencio (2 × intervalo esperado de 5 min), emitiendo la alerta `SIN_REPORTE`.

---

## 🚀 Despliegue Local Rápido

Para instrucciones detalladas paso a paso en Windows, Linux y macOS, consulta la **[Guía de Instalación Local](GUIA_INSTALACION_LOCAL.md)**.

### Resumen de Ejecución:

#### 1. Iniciar el Backend (Puerto 8080):
```bash
cd SIMACMART-Backend
# En Windows:
.\gradlew.bat run
# En Linux/Mac:
./gradlew run
```

#### 2. Iniciar el Frontend (Puerto 3000):
```bash
cd SIMACMART-Frontend
python -m http.server 3000
```
Abre en tu navegador: 👉 **`http://localhost:3000`**

---

## 🧪 Pruebas Automatizadas

Para compilar y verificar todas las pruebas unitarias:
```bash
cd SIMACMART-Backend
.\gradlew.bat test
```
* **Estado:** `BUILD SUCCESSFUL` (11 pruebas completadas al 100%).

---

## 👥 Equipo de Trabajo

* **Universidad Privada de Tacna**
* **Facultad de Ingeniería — Escuela Profesional de Ingeniería de Sistemas**
* **Asignatura:** Patrones de Software
* **Docente:** Ing. Alberto Flor Rodríguez
* **Integrantes:** Hernández y Arce
