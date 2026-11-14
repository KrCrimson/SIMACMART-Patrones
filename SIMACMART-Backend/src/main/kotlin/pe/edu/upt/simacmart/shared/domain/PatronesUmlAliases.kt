/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/shared/domain/PatronesUmlAliases.kt
 * Capa / módulo ....: Shared · Dominio Transversal
 * Función ..........: Mapa maestro de equivalencias literales 1:1 entre Diagramas UML del SAD y Clases Kotlin
 * Requerimientos ...: RF-01 al RF-10
 * Calidad (RNF) ....: RNF-02 (Mantenibilidad arquitectónica)
 * Patrones .........: GoF & GRASP (Alineación con diagramas UML)
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART)
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.shared.domain

// --- DIAGRAMA 1: INGESTA DE DATOS ---
typealias PuertoTransporteMensajes = pe.edu.upt.simacmart.ingesta.application.port.transport.TransporteMensajes
typealias PuertoObservadorMensaje = pe.edu.upt.simacmart.ingesta.application.port.transport.ObservadorMensaje
typealias FachadaMqtt = pe.edu.upt.simacmart.ingesta.infrastructure.adapter.inbound.MqttFacade
typealias ObservadorReceptorBalanza = pe.edu.upt.simacmart.ingesta.infrastructure.adapter.inbound.ReceptorBalanza
typealias AdaptadorBalanza = pe.edu.upt.simacmart.ingesta.infrastructure.adapter.inbound.BalanzaAdapter
typealias AdaptadorEcowitt = pe.edu.upt.simacmart.ingesta.infrastructure.adapter.inbound.EcowittAdapter
typealias ControladorEcowitt = pe.edu.upt.simacmart.ingesta.infrastructure.adapter.inbound.EcowittController
typealias PuertoIngestarLectura = pe.edu.upt.simacmart.ingesta.application.port.inbound.IngestarLectura
typealias GestorServicioIngesta = pe.edu.upt.simacmart.ingesta.application.service.ServicioIngesta
typealias CadenaValidacion = pe.edu.upt.simacmart.ingesta.domain.service.ValidacionHandler
typealias PuertoVerificarDispositivo = pe.edu.upt.simacmart.ingesta.application.port.outbound.VerificarDispositivo

// --- DIAGRAMA 2: PERSISTENCIA CON REPOSITORY Y UNIT OF WORK ---
typealias PuertoUnitOfWork = pe.edu.upt.simacmart.ingesta.application.port.outbound.UnitOfWork
typealias PuertoLecturaRepository = pe.edu.upt.simacmart.ingesta.application.port.outbound.LecturaRepository
typealias PuertoDispositivoRepository = pe.edu.upt.simacmart.ingesta.application.port.outbound.DispositivoRepository
typealias AdaptadorJdbcUnitOfWork = pe.edu.upt.simacmart.ingesta.infrastructure.adapter.outbound.JdbcUnitOfWork
typealias AdaptadorJdbcLecturaRepo = pe.edu.upt.simacmart.ingesta.infrastructure.adapter.outbound.JdbcLecturaRepository
typealias AdaptadorJdbcDispositivoRepo = pe.edu.upt.simacmart.ingesta.infrastructure.adapter.outbound.JdbcDispositivoRepository
typealias AdaptadorVerificarDispositivo = pe.edu.upt.simacmart.ingesta.infrastructure.adapter.outbound.VerificarDispositivoAdapter

// --- DIAGRAMA 3: PREDICCIÓN CON STRATEGY Y ADAPTER (HERNÁNDEZ Y ARCE) ---
typealias ContextoServicioPrediccionMensual = pe.edu.upt.simacmart.prediccion.application.service.ServicioPrediccionMensual
typealias PuertoEstrategiaPrediccion = pe.edu.upt.simacmart.prediccion.application.port.outbound.EstrategiaPrediccion
typealias EstrategiaConcretaML = pe.edu.upt.simacmart.prediccion.infrastructure.adapter.outbound.PrediccionConModeloML
typealias EstrategiaConcretaHistorico = pe.edu.upt.simacmart.prediccion.infrastructure.adapter.outbound.PromedioHistoricoMensual
typealias PuertoPrepararEntrada = pe.edu.upt.simacmart.prediccion.application.port.outbound.PrepararEntradaPrediccion
typealias PuertoObtenerModeloActivo = pe.edu.upt.simacmart.prediccion.application.port.outbound.ObtenerModeloActivo
typealias PuertoEvaluarModelo = pe.edu.upt.simacmart.prediccion.application.port.outbound.EvaluarModelo
typealias PuertoPrediccionRepository = pe.edu.upt.simacmart.prediccion.application.port.outbound.PrediccionRepository
typealias AdaptadorAlmacenModelo = pe.edu.upt.simacmart.prediccion.infrastructure.adapter.outbound.AlmacenModeloAdapter
typealias AdaptadorEvaluadorArtefacto = pe.edu.upt.simacmart.prediccion.infrastructure.adapter.outbound.EvaluadorArtefactoAdapter
typealias AdaptadorPreparadorEntrada = pe.edu.upt.simacmart.prediccion.infrastructure.adapter.outbound.PreparadorEntradaAdapter
typealias AdaptadorJdbcPrediccionRepo = pe.edu.upt.simacmart.prediccion.infrastructure.adapter.outbound.JdbcPrediccionRepository


// --- DIAGRAMA 4: DASHBOARD: ADAPTER Y OBSERVER ---
typealias PuertoAutenticarSesion = pe.edu.upt.simacmart.cuentas.application.port.inbound.AutenticarSesion
typealias PuertoConsultarMonitoreo = pe.edu.upt.simacmart.monitoreo.application.port.inbound.ConsultarMonitoreo
typealias GestorServicioConsultaMonitoreo = pe.edu.upt.simacmart.monitoreo.application.service.ServicioConsultaMonitoreo
typealias PuertoConsultaMonitoreoQuery = pe.edu.upt.simacmart.monitoreo.application.port.outbound.ConsultaMonitoreoQuery
typealias PuertoConsultaActividadDispositivos = pe.edu.upt.simacmart.monitoreo.application.port.outbound.ConsultaActividadDispositivos
typealias PuertoSupervisarDispositivos = pe.edu.upt.simacmart.monitoreo.application.port.inbound.SupervisarDispositivos
typealias SujetoSupervisorDispositivos = pe.edu.upt.simacmart.monitoreo.application.service.SupervisorDispositivos
typealias ObservadorNotificacionesMonitoreo = pe.edu.upt.simacmart.monitoreo.application.port.outbound.ObservadorMonitoreo
typealias AdaptadorDashboardHttp = pe.edu.upt.simacmart.monitoreo.infrastructure.adapter.inbound.DashboardHttpAdapter
typealias AdaptadorDashboardNotificaciones = pe.edu.upt.simacmart.monitoreo.infrastructure.adapter.inbound.DashboardNotificacionesAdapter
typealias GestorPlanificadorSupervision = pe.edu.upt.simacmart.monitoreo.infrastructure.adapter.inbound.PlanificadorSupervision
typealias AdaptadorJdbcConsultaMonitoreo = pe.edu.upt.simacmart.monitoreo.infrastructure.adapter.outbound.JdbcConsultaMonitoreoQuery
typealias AdaptadorJdbcActividadDispositivos = pe.edu.upt.simacmart.monitoreo.infrastructure.adapter.outbound.JdbcActividadDispositivos
