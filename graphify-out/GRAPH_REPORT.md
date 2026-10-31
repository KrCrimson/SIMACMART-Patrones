# Graph Report - SIMACMART-Patrones  (2026-10-31)

## Corpus Check
- Corpus is ~14,306 words - fits in a single context window. You may not need a graph.

## Summary
- 253 nodes · 532 edges · 14 communities (9 shown, 5 thin omitted)
- Extraction: 99% EXTRACTED · 1% INFERRED · 0% AMBIGUOUS · INFERRED: 4 edges (avg confidence: 0.88)
- Token cost: 1,200 input · 800 output

## Community Hubs (Navigation)
- Captacion y Reglas de Calculo
- Servidor HTTP y Middleware Nativo
- Cuentas, Autenticacion y Seguridad
- Frontend SPA y Cliente API
- Persistencia y Repositorios de Captacion
- Prediccion ML y Patron Strategy
- Ingesta IoT y Puertos de Sensores
- Arquitectura Hexagonal y Despliegue Cloud
- Migraciones e Inicializacion de BD
- Scripts Gradle Wrapper
- Scripts de Arranque Contenedor
- Proyecto Raiz SIMACMART

## God Nodes (most connected - your core abstractions)
1. `main()` - 16 edges
2. `EventoVaciado` - 16 edges
3. `Cuenta` - 16 edges
4. `DatabaseConfig` - 16 edges
5. `ServidorHttpMonolito` - 16 edges
6. `ServicioPrediccion` - 14 edges
7. `ServicioCaptacion` - 13 edges
8. `CaptacionRepositoryPort` - 12 edges
9. `ServicioCuentas` - 11 edges
10. `PrediccionConModeloMLStrategy` - 11 edges

## Surprising Connections (you probably didn't know these)
- `SIMACMART Frontend Vanilla Dashboard` --references--> `SIMACMART Modular Monolith Architecture`  [EXTRACTED]
  SIMACMART-Frontend/README.md → SIMACMART-Backend/README.md
- `SIMACMART Render Cloud Spec` --semantically_similar_to--> `SIMACMART Docker Compose Deployment`  [INFERRED] [semantically similar]
  SIMACMART-Backend/render.yaml → SIMACMART-Backend/docker-compose.yml
- `main()` --calls--> `ServicioCaptacion`  [EXTRACTED]
  SIMACMART-Backend/src/main/kotlin/pe/edu/upt/simacmart/Main.kt → SIMACMART-Backend/src/main/kotlin/pe/edu/upt/simacmart/captacion/application/service/ServicioCaptacion.kt
- `main()` --calls--> `CaptacionRepositoryAdapter`  [EXTRACTED]
  SIMACMART-Backend/src/main/kotlin/pe/edu/upt/simacmart/Main.kt → SIMACMART-Backend/src/main/kotlin/pe/edu/upt/simacmart/captacion/infrastructure/adapter/outbound/CaptacionRepositoryAdapter.kt
- `main()` --calls--> `ServicioCuentas`  [EXTRACTED]
  SIMACMART-Backend/src/main/kotlin/pe/edu/upt/simacmart/Main.kt → SIMACMART-Backend/src/main/kotlin/pe/edu/upt/simacmart/cuentas/application/service/ServicioCuentas.kt

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **SIMACMART Complete Architectural Ecosystem** — simacmart_backend_readme_modular_monolith, simacmart_frontend_readme_dashboard, concept_hexagonal_architecture, concept_strategy_pattern_prediccion, concept_store_and_forward_iot [INFERRED 0.95]

## Communities (14 total, 5 thin omitted)

### Community 0 - "Captacion y Reglas de Calculo"
Cohesion: 0.08
Nodes (17): bigdecimal, instant, localdate, roundingmode, CalcularCaptacionUseCase, CaptacionRepositoryPort, ServicioCaptacion, CaptacionPeriodo (+9 more)

### Community 1 - "Servidor HTTP y Middleware Nativo"
Cohesion: 0.10
Nodes (22): Gson, HttpExchange, HttpServer, inetsocketaddress, inputstreamreader, jsonobject, AuthController, IngestaBalanzaDto (+14 more)

### Community 2 - "Cuentas, Autenticacion y Seguridad"
Cohesion: 0.12
Nodes (16): bcrypt, CrearCuentaComando, CrearCuentaUseCase, LoginComando, LoginUseCase, CuentaRepositoryPort, ServicioCuentas, Cuenta (+8 more)

### Community 3 - "Frontend SPA y Cliente API"
Cohesion: 0.11
Nodes (21): ApiClient, fetchDashboardData(), lastSyncTimestamp, setupActions(), setupLiveClock(), setupModals(), setupNavigation(), startPolling() (+13 more)

### Community 4 - "Persistencia y Repositorios de Captacion"
Cohesion: 0.12
Nodes (10): drivermanager, file, CaptacionRepositoryAdapter, ConsultarDashboardUseCase, DashboardResumenDto, DatabaseConfig, Connection, TipoMotor (+2 more)

### Community 5 - "Prediccion ML y Patron Strategy"
Cohesion: 0.16
Nodes (10): datetimeformatter, GenerarPrediccionUseCase, EstrategiaPrediccionPort, ServicioPrediccion, EstimacionHidricaResponse, ModeloMLInfo, ProyeccionDiariaDto, PrediccionConModeloMLStrategy (+2 more)

### Community 6 - "Ingesta IoT y Puertos de Sensores"
Cohesion: 0.15
Nodes (9): java, IngestaRepositoryPort, Dispositivo, LecturaBalanza, LecturaEstacion, TipoDispositivo, BALANZA, ESTACION (+1 more)

### Community 7 - "Arquitectura Hexagonal y Despliegue Cloud"
Cohesion: 0.25
Nodes (8): Hexagonal Architecture (Ports and Adapters), Store-and-Forward Pattern (PostgreSQL + SQLite Buffer), Strategy Pattern (ML Prediction), SIMACMART Docker Compose Deployment, SIMACMART Modular Monolith Architecture, SIMACMART Render Cloud Spec, SIMACMART Frontend SPA Shell, SIMACMART Frontend Vanilla Dashboard

### Community 9 - "Scripts Gradle Wrapper"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

## Knowledge Gaps
- **18 isolated node(s):** `HORA`, `DIA`, `MES`, `ADMINISTRADOR`, `OPERADOR` (+13 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 43 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **5 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `DatabaseConfig` connect `Persistencia y Repositorios de Captacion` to `Captacion y Reglas de Calculo`, `Servidor HTTP y Middleware Nativo`, `Cuentas, Autenticacion y Seguridad`, `Prediccion ML y Patron Strategy`, `Ingesta IoT y Puertos de Sensores`?**
  _High betweenness centrality (0.113) - this node is a cross-community bridge._
- **Why does `main()` connect `Servidor HTTP y Middleware Nativo` to `Captacion y Reglas de Calculo`, `Cuentas, Autenticacion y Seguridad`, `Persistencia y Repositorios de Captacion`, `Prediccion ML y Patron Strategy`, `Migraciones e Inicializacion de BD`?**
  _High betweenness centrality (0.080) - this node is a cross-community bridge._
- **What connects `HORA`, `DIA`, `MES` to the rest of the system?**
  _18 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Captacion y Reglas de Calculo` be split into smaller, more focused modules?**
  _Cohesion score 0.08484848484848485 - nodes in this community are weakly interconnected._
- **Should `Servidor HTTP y Middleware Nativo` be split into smaller, more focused modules?**
  _Cohesion score 0.09513742071881606 - nodes in this community are weakly interconnected._
- **Should `Cuentas, Autenticacion y Seguridad` be split into smaller, more focused modules?**
  _Cohesion score 0.12063492063492064 - nodes in this community are weakly interconnected._
- **Should `Frontend SPA y Cliente API` be split into smaller, more focused modules?**
  _Cohesion score 0.11174242424242424 - nodes in this community are weakly interconnected._