/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/Main.kt
 * Capa / módulo ....: Composition Root · Monolito Modular
 * Función ..........: Ensambla y cablea todos los módulos, adaptadores e interfaces de los 4 diagramas
 * Requerimientos ...: RF-01 al RF-10
 * Calidad (RNF) ....: RNF-01 al RNF-08
 * Casos de uso .....: CU-01 al CU-12
 * Reglas de negocio : RN-01 al RN-09
 * Patrones .........: Composition Root, Dependency Injection, Observer, Strategy, Adapter, Unit of Work, Chain of Responsibility
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART)
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart

import pe.edu.upt.simacmart.captacion.application.service.ServicioCaptacion
import pe.edu.upt.simacmart.captacion.infrastructure.adapter.outbound.JdbcCaptacionRepository
import pe.edu.upt.simacmart.cuentas.application.service.ServicioCuentas
import pe.edu.upt.simacmart.cuentas.infrastructure.adapter.inbound.AuthController
import pe.edu.upt.simacmart.cuentas.infrastructure.adapter.outbound.PostgresCuentaRepository
import pe.edu.upt.simacmart.ingesta.application.service.ServicioIngesta
import pe.edu.upt.simacmart.ingesta.domain.service.CamposHandler
import pe.edu.upt.simacmart.ingesta.domain.service.FechaHandler
import pe.edu.upt.simacmart.ingesta.domain.service.RangosHandler
import pe.edu.upt.simacmart.ingesta.infrastructure.adapter.inbound.*
import pe.edu.upt.simacmart.ingesta.infrastructure.adapter.outbound.JdbcUnitOfWork
import pe.edu.upt.simacmart.ingesta.infrastructure.adapter.outbound.VerificarDispositivoAdapter
import pe.edu.upt.simacmart.monitoreo.application.service.ServicioMonitoreo
import pe.edu.upt.simacmart.monitoreo.infrastructure.adapter.inbound.DashboardHttpController
import pe.edu.upt.simacmart.prediccion.application.service.ServicioPrediccion
import pe.edu.upt.simacmart.prediccion.application.service.ServicioPrediccionMensual
import pe.edu.upt.simacmart.prediccion.infrastructure.adapter.inbound.PrediccionHttpController
import pe.edu.upt.simacmart.prediccion.infrastructure.adapter.outbound.*
import pe.edu.upt.simacmart.shared.infrastructure.db.DatabaseInitializer
import pe.edu.upt.simacmart.shared.infrastructure.db.ResilienteDataSource
import pe.edu.upt.simacmart.shared.infrastructure.http.ServidorHttpMonolito

fun main() {
    println("=================================================================")
    println(">>> INICIANDO SISTEMA MONOLITO MODULAR SIMACMART (UPT)")
    println(">>> Arquitectura Hexagonal + Patrones GoF (Strategy, Adapter, CoR, UoW)")
    println("=================================================================")

    // FASE 1: Inicialización de la Base de Datos Local / Remota
    DatabaseInitializer.inicializar()
    val dataSource = ResilienteDataSource

    // Servidor HTTP Compartido
    val puertoServidor = System.getenv("PORT")?.toIntOrNull() ?: 8080
    val servidor = ServidorHttpMonolito(port = puertoServidor)

    // =========================================================================
    // MÓDULO 1: Cuentas y Seguridad
    // =========================================================================
    val cuentaRepo = PostgresCuentaRepository()
    val servicioCuentas = ServicioCuentas(cuentaRepo)
    val authController = AuthController(servicioCuentas)
    authController.registrarRutas(servidor)

    // =========================================================================
    // MÓDULO 2: Captación Hídrica y Detección de Vaciados (Diagrama 2)
    // =========================================================================
    val captacionRepo = JdbcCaptacionRepository()
    val servicioCaptacion = ServicioCaptacion(captacionRepo)

    // =========================================================================
    // MÓDULO 3: Ingesta de Datos y Persistencia con Unit of Work (Diagramas 1 y 2)
    // =========================================================================
    // Cadena de Responsabilidad (Chain of Responsibility)
    val camposHandler = CamposHandler()
    val fechaHandler = FechaHandler()
    val rangosHandler = RangosHandler()
    camposHandler.establecerSiguiente(fechaHandler)
    fechaHandler.establecerSiguiente(rangosHandler)

    // Adaptadores de Persistencia
    val verificador = VerificarDispositivoAdapter(dataSource)
    val uow = JdbcUnitOfWork(dataSource)

    // Servicio de Ingesta (Application Service)
    val servicioIngesta = ServicioIngesta(
        validacion = camposHandler,
        verificador = verificador,
        uow = uow,
        captacion = servicioCaptacion
    )

    // Recepción MQTT con Fachada y Observer (Diagrama 1)
    val mqttFacade = MqttFacade()
    val balanzaAdapter = BalanzaAdapter()
    val receptorBalanza = ReceptorBalanza(balanzaAdapter, servicioIngesta, mqttFacade)
    mqttFacade.suscribir("simacmart/+/balanza", receptorBalanza)
    mqttFacade.suscribir("simacmart/v1/+/balanza", receptorBalanza)

    // Recepción Ecowitt (Diagrama 1)
    val ecowittAdapter = EcowittAdapter()
    val ecowittController = EcowittController(ecowittAdapter, servicioIngesta)

    // Controlador HTTP para Ingesta REST / Postman / Dispositivos HTTP
    val ingestaHttpController = IngestaHttpController(servicioIngesta, servicioCaptacion)
    ingestaHttpController.registrarRutas(servidor)

    // =========================================================================
    // MÓDULO 4: Monitoreo y Dashboard
    // =========================================================================
    val servicioMonitoreo = ServicioMonitoreo()
    val dashboardController = DashboardHttpController(servicioMonitoreo)
    dashboardController.registrarRutas(servidor)

    // =========================================================================
    // MÓDULO 5: Predicción Hídrica con Strategy y Adapter (Diagrama 3 - Hernández y Arce)
    // =========================================================================
    val almacenModelo = AlmacenModeloAdapter()
    val evaluadorArtefacto = EvaluadorArtefactoAdapter()
    val estrategiaML = PrediccionConModeloML(almacenModelo, evaluadorArtefacto)
    val preparadorEntrada = PreparadorEntradaAdapter()
    val prediccionRepo = JdbcPrediccionRepository(dataSource)

    val servicioPrediccionMensual = ServicioPrediccionMensual(
        estrategia = estrategiaML,
        preparadorEntrada = preparadorEntrada,
        prediccionRepository = prediccionRepo
    )

    val servicioPrediccionLegacy = ServicioPrediccion(PrediccionConModeloMLStrategy())
    val prediccionController = PrediccionHttpController(servicioPrediccionLegacy)
    prediccionController.registrarRutas(servidor)

    // Iniciar servidor HTTP en el puerto configurado
    servidor.iniciar()
}
