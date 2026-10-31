# SIMACMART — Backend Core (Kotlin)
## Monolito Modular con Arquitectura Hexagonal (Modular Monolith + Ports & Adapters)

Este proyecto implementa el núcleo del sistema **SIMACMART** (*Sistema Inteligente y Modular de Atrapanieblas para la Captación, Monitoreo y Acondicionamiento de Agua Atmosférica en Tacna*), desarrollado para el curso de **Patrones de Software** en la Universidad Privada de Tacna.

---

### 1. ¿Por qué Monolito Modular + Arquitectura Hexagonal?

Un **Monolito Modular** divide el sistema en **módulos de negocio delimitados (*Bounded Contexts*)** dentro de una misma base de código y un único proceso de ejecución (sin la complejidad ni latencia de red de los microservicios).

A su vez, **dentro de cada módulo se aplica rigurosamente la Arquitectura Hexagonal (Puertos y Adaptadores)**:
- **Alta Cohesión:** Cada módulo es dueño de su modelo de dominio, sus reglas y sus datos.
- **Bajo Acoplamiento:** Los módulos se comunican entre sí únicamente a través de sus **Puertos de Entrada (*Use Cases*)** o contratos compartidos en `shared`, nunca tocando las tablas ni clases internas de otro módulo.
- **Correspondencia con la Documentación del Proyecto:** Esta estructura replica exactamente la organización modular documentada en `5.3.4_Diagrama_de_Clases.md`, `SIMACMART_Diagramas_Actividades_Modulos` y `SIMACMART_Diagramas_Secuencia_Modulos`.

---

### 2. Mapa Arquitectónico del Proyecto

```
pe.edu.upt.simacmart/
├── Main.kt                                   <- Ensamblaje central del Monolito Modular
│
├── shared/                                   <- KERNEL COMPARTIDO
│   ├── domain/                               <- Tipos y resultados comunes (Resultado<T>)
│   └── infrastructure/
│       ├── db/DatabaseConfig.kt              <- Conexiones centralizadas (Postgres y SQLite)
│       └── http/ServidorHttpMonolito.kt      <- Servidor HTTP nativo de la JVM (Sin Spring Boot)
│
├── cuentas/                                  <- MÓDULO 1: CUENTAS Y SEGURIDAD (UC-13, 14, 17)
│   ├── domain/model/                         <- Cuenta, Rol, Unidad (Nodo de campo)
│   ├── application/port/
│   │   ├── inbound/                          <- LoginUseCase, CrearCuentaUseCase
│   │   └── outbound/                         <- CuentaRepositoryPort
│   ├── application/service/                  <- ServicioCuentas (Cifrado con BCrypt)
│   └── infrastructure/adapter/
│       ├── inbound/                          <- AuthController (/api/auth/*)
│       └── outbound/                         <- PostgresCuentaRepository
│
├── ingesta/                                  <- MÓDULO 2: INGESTA IoT (UC-01 al UC-04)
│   ├── domain/model/                         <- LecturaBalanza, LecturaEstacion, Dispositivo
│   ├── application/port/
│   │   ├── inbound/                          <- IngestaBalanzaUseCase, IngestaEcowittUseCase
│   │   └── outbound/                         <- IngestaRepositoryPort
│   ├── application/service/                  <- ServicioIngesta (Validación de anomalías)
│   └── infrastructure/adapter/
│       ├── inbound/                          <- IngestaHttpController (/api/ingesta/*)
│       └── outbound/                         <- IngestaPersistenceAdapter (Store-and-Forward: Postgres + SQLite)
│
├── captacion/                                <- MÓDULO 3: CAPTACIÓN Y CÁLCULO (UC-05, Regla RN-03)
│   ├── domain/model/                         <- CaptacionPeriodo, EventoVaciado
│   ├── application/port/
│   │   ├── inbound/                          <- CalcularCaptacionUseCase
│   │   └── outbound/                         <- CaptacionRepositoryPort
│   ├── application/service/                  <- ServicioCaptacion (Detección de vaciados de tina)
│   └── infrastructure/adapter/
│       └── outbound/                         <- CaptacionRepositoryAdapter
│
├── monitoreo/                                <- MÓDULO 4: MONITOREO & DASHBOARD (UC-06 al UC-09)
│   ├── application/port/inbound/             <- ConsultarDashboardUseCase
│   ├── application/service/                  <- ServicioMonitoreo
│   └── infrastructure/adapter/inbound/       <- DashboardHttpController (/api/dashboard/*)
│
└── prediccion/                               <- MÓDULO 5: PREDICCIÓN & ML (UC-10 al UC-12)
    ├── domain/model/                         <- PrediccionMensual, ModeloML
    ├── application/port/
    │   ├── inbound/                          <- GenerarPrediccionUseCase
    │   └── outbound/                         <- EstrategiaPrediccionPort (PATRÓN STRATEGY)
    ├── application/service/                  <- ServicioPrediccion (Contexto del Strategy)
    └── infrastructure/adapter/outbound/      <- PrediccionConModeloMLStrategy (Python) y PromedioHistoricoStrategy
```

---

### 3. Patrones de Diseño Integrados en el Monolito

1. **Patrón Strategy (Módulo Predicción):**
   - Interfaz: `prediccion.application.port.outbound.EstrategiaPrediccionPort`
   - Estrategias concretas: `PrediccionConModeloMLStrategy` y `PromedioHistoricoStrategy`.
   - Permite al monolito intercambiar en caliente el motor predictivo de Machine Learning sin recompilar ni alterar el dominio.
2. **Patrón Store-and-Forward con Repository (Módulo Ingesta):**
   - Adaptador: `ingesta.infrastructure.adapter.outbound.IngestaPersistenceAdapter`
   - Intenta guardar directamente en la base central **PostgreSQL**. Si la red 4G en Pachía falla, realiza un *fallback* automático hacia el búfer local en **SQLite**, garantizando cero pérdida de lecturas.
3. **Patrón Adapter / Ports (Todos los Módulos):**
   - Transforma peticiones HTTP externas (Ecowitt Push, sensores ESP32, dashboard) en comandos limpios de la capa de aplicación.
4. **Cumplimiento de la Restricción Docente:**
   - Implementado en **Kotlin puro**.
   - Servidor HTTP nativo de la JVM (`com.sun.net.httpserver.HttpServer`), **sin Spring Boot ni frameworks pesados**.
