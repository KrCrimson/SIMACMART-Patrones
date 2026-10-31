package pe.edu.upt.simacmart

import pe.edu.upt.simacmart.captacion.application.service.ServicioCaptacion
import pe.edu.upt.simacmart.captacion.infrastructure.adapter.outbound.CaptacionRepositoryAdapter
import pe.edu.upt.simacmart.cuentas.application.service.ServicioCuentas
import pe.edu.upt.simacmart.cuentas.infrastructure.adapter.inbound.AuthController
import pe.edu.upt.simacmart.cuentas.infrastructure.adapter.outbound.PostgresCuentaRepository
import pe.edu.upt.simacmart.ingesta.application.service.ServicioIngesta
import pe.edu.upt.simacmart.ingesta.infrastructure.adapter.inbound.IngestaHttpController
import pe.edu.upt.simacmart.ingesta.infrastructure.adapter.outbound.IngestaPersistenceAdapter
import pe.edu.upt.simacmart.monitoreo.application.service.ServicioMonitoreo
import pe.edu.upt.simacmart.monitoreo.infrastructure.adapter.inbound.DashboardHttpController
import pe.edu.upt.simacmart.prediccion.application.service.ServicioPrediccion
import pe.edu.upt.simacmart.prediccion.infrastructure.adapter.inbound.PrediccionHttpController
import pe.edu.upt.simacmart.prediccion.infrastructure.adapter.outbound.PrediccionConModeloMLStrategy
import pe.edu.upt.simacmart.shared.infrastructure.db.DatabaseConfig
import pe.edu.upt.simacmart.shared.infrastructure.db.DatabaseInitializer
import pe.edu.upt.simacmart.shared.infrastructure.http.ServidorHttpMonolito

/**
 * Punto de entrada principal (Composition Root) del Monolito Modular SIMACMART.
 * Ensambla de forma pura los 5 modulos de negocio y arranca el servidor HTTP nativo.
 */
fun main() {
    println("=================================================================")
    println(">>> INICIANDO SISTEMA MONOLITO MODULAR SIMACMART (UPT)")
    println("=================================================================")

    // FASE 1: Inicializacion y comprobacion de Persistencia
    DatabaseInitializer.inicializar()

    // Servidor HTTP Compartido
    val servidor = ServidorHttpMonolito(port = 8080)

    // Modulo 1: Cuentas y Seguridad (Fase 2)
    val cuentaRepo = PostgresCuentaRepository()
    val servicioCuentas = ServicioCuentas(cuentaRepo)
    val authController = AuthController(servicioCuentas)
    authController.registrarRutas(servidor)

    // Modulo 3: Captacion y Vaciados de Tina (Fase 4)
    val captacionRepo = CaptacionRepositoryAdapter()
    val servicioCaptacion = ServicioCaptacion(captacionRepo)

    // Modulo 2: Ingesta IoT Balanza y Ecowitt (Fase 3 + Fase 4)
    val ingestaRepo = IngestaPersistenceAdapter()
    val servicioIngesta = ServicioIngesta(ingestaRepo)
    val ingestaController = IngestaHttpController(servicioIngesta, servicioCaptacion)
    ingestaController.registrarRutas(servidor)

    // Modulo 4: Monitoreo y Dashboard (Fase 5)
    val servicioMonitoreo = ServicioMonitoreo()
    val dashboardController = DashboardHttpController(servicioMonitoreo, servicioCaptacion)
    dashboardController.registrarRutas(servidor)

    // Modulo 5: Prediccion Hidrica (Fase 6 - Patron Strategy GoF)
    val estrategiaML = PrediccionConModeloMLStrategy()
    val servicioPrediccion = ServicioPrediccion(estrategiaML)
    val prediccionController = PrediccionHttpController(servicioPrediccion)
    prediccionController.registrarRutas(servidor)

    // Arranque del Servidor
    servidor.iniciar()
    println(">>> Backend SIMACMART listo para recibir solicitudes de sensores y Frontend.")
    println("=================================================================")
}
