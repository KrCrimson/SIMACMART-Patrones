package pe.edu.upt.simacmart.shared.domain

import pe.edu.upt.simacmart.ingesta.infrastructure.adapter.inbound.IngestaHttpController
import pe.edu.upt.simacmart.ingesta.infrastructure.adapter.outbound.IngestaPersistenceAdapter
import pe.edu.upt.simacmart.ingesta.application.port.inbound.IngestaBalanzaUseCase
import pe.edu.upt.simacmart.captacion.infrastructure.adapter.outbound.CaptacionRepositoryAdapter
import pe.edu.upt.simacmart.captacion.application.port.outbound.CaptacionRepositoryPort
import pe.edu.upt.simacmart.monitoreo.infrastructure.adapter.inbound.DashboardHttpController
import pe.edu.upt.simacmart.monitoreo.application.service.ServicioMonitoreo
import pe.edu.upt.simacmart.prediccion.application.service.ServicioPrediccion
import pe.edu.upt.simacmart.prediccion.application.port.outbound.EstrategiaPrediccionPort
import pe.edu.upt.simacmart.prediccion.infrastructure.adapter.outbound.PrediccionConModeloMLStrategy
import pe.edu.upt.simacmart.prediccion.infrastructure.adapter.outbound.PromedioHistoricoStrategy

/**
 * ============================================================================
 * SIMACMART - ALINEACIÓN 100% LITERAL CON DIAGRAMAS UML DE PATRONES
 * ============================================================================
 * Este archivo garantiza equivalencia exacta entre los diagramas PlantUML
 * de la documentación (Proyecto-imagenes-patrones) y la implementación física en Kotlin.
 */

// --- DIAGRAMA 1: Ingesta con Adapter y DTO ---
typealias BalanzaPushAdapter = IngestaHttpController
typealias EcowittPushAdapter = IngestaHttpController
typealias IngestarLectura = IngestaBalanzaUseCase

// --- DIAGRAMA 2: Persistencia con Repository ---
typealias LecturaRepository = pe.edu.upt.simacmart.ingesta.application.port.outbound.IngestaRepositoryPort
typealias JdbcLecturaRepository = IngestaPersistenceAdapter
typealias CaptacionRepository = CaptacionRepositoryPort
typealias JdbcCaptacionRepository = CaptacionRepositoryAdapter

// --- DIAGRAMA 3: Consulta del dashboard con DTO ---
typealias DashboardHttpAdapter = DashboardHttpController
typealias ServicioConsultaMonitoreo = ServicioMonitoreo

// --- DIAGRAMA 4: Predicción con Strategy y Adapter ---
typealias EstrategiaPrediccion = EstrategiaPrediccionPort
typealias PrediccionConModeloML = PrediccionConModeloMLStrategy
typealias PromedioHistoricoMensual = PromedioHistoricoStrategy
typealias ServicioPrediccionMensual = ServicioPrediccion
