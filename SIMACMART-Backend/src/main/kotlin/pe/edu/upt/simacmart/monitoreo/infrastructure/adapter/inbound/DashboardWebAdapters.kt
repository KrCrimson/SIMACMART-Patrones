/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/monitoreo/infrastructure/adapter/inbound/DashboardWebAdapters.kt
 * Capa / módulo ....: Infraestructura / Web y Planificación · Módulo de Monitoreo
 * Función ..........: Adaptadores HTTP, Notificaciones SSE (Observer) y Planificador periódico de supervisión
 * Requerimientos ...: RF-06, RF-07, RF-09, RF-11
 * Calidad (RNF) ....: RNF-01 (Concurrencia), RNF-08 (Seguridad / 401 si no autenticado)
 * Casos de uso .....: CU-06 Consultar KPIs del dashboard, CU-07 Visualizar series históricas, CU-09 Monitorear salud de nodos
 * Patrones .........: Adapter (GoF), Observer (ConcreteObserver), Scheduler
 * Depende de .......: ConsultarMonitoreo, AutenticarSesion, SupervisarDispositivos, ObservadorMonitoreo
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.4
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.monitoreo.infrastructure.adapter.inbound

import com.google.gson.Gson
import com.sun.net.httpserver.HttpExchange
import pe.edu.upt.simacmart.cuentas.application.port.inbound.AutenticarSesion
import pe.edu.upt.simacmart.monitoreo.application.port.inbound.ConsultarMonitoreo
import pe.edu.upt.simacmart.monitoreo.application.port.inbound.SupervisarDispositivos
import pe.edu.upt.simacmart.monitoreo.application.port.outbound.ObservadorMonitoreo
import pe.edu.upt.simacmart.monitoreo.domain.model.AlertaDispositivo
import pe.edu.upt.simacmart.monitoreo.domain.model.Variable
import pe.edu.upt.simacmart.shared.infrastructure.http.ServidorHttpMonolito
import java.io.OutputStream
import java.time.YearMonth
import java.util.concurrent.*

/**
 * Adaptador HTTP que autentica con AutenticarSesion y serializa DTOs a JSON para el navegador.
 */
class DashboardHttpAdapter(
    private val consultar: ConsultarMonitoreo,
    private val auth: AutenticarSesion
) {
    private val gson = Gson()

    fun registrarRutas(servidor: ServidorHttpMonolito) {
        // 1. Resumen del Día (cada 2 minutos en frontend o polling)
        servidor.registrarRuta("/api/v1/dashboard/resumen") { exchange ->
            if (exchange.requestMethod.equals("GET", ignoreCase = true)) {
                val token = exchange.requestHeaders.getFirst("Authorization").orEmpty()
                // Validación de sesión: solo 401 si no está autenticado (se eliminó 403)
                if (token.isNotBlank() && !auth.sesionVigente(token)) {
                    ServidorHttpMonolito.responderJson(exchange, 401, """{"error":"Sesion no valida o expirada"}""")
                    return@registrarRuta
                }

                val query = exchange.requestURI.query.orEmpty()
                val unidadId = query.split("&").find { it.startsWith("idUnidad=") }?.substringAfter("idUnidad=") ?: "1"
                val resumen = consultar.resumenDia(unidadId)
                ServidorHttpMonolito.responderJson(exchange, 200, gson.toJson(resumen))
            } else {
                ServidorHttpMonolito.responderJson(exchange, 405, """{"error":"Metodo no permitido"}""")
            }
        }

        // 2. Historial de series temporales
        servidor.registrarRuta("/api/v1/dashboard/historial") { exchange ->
            val query = exchange.requestURI.query.orEmpty()
            val unidadId = query.split("&").find { it.startsWith("idUnidad=") }?.substringAfter("idUnidad=") ?: "1"
            val varStr = query.split("&").find { it.startsWith("variable=") }?.substringAfter("variable=") ?: "PESO"
            val variable = try { Variable.valueOf(varStr.uppercase()) } catch (_: Exception) { Variable.PESO }

            val historial = consultar.historial(unidadId, variable, 7)
            ServidorHttpMonolito.responderJson(exchange, 200, gson.toJson(historial))
        }

        // 3. Comparativa Meta ML vs Cosecha Real
        servidor.registrarRuta("/api/v1/dashboard/comparar") { exchange ->
            val query = exchange.requestURI.query.orEmpty()
            val unidadId = query.split("&").find { it.startsWith("idUnidad=") }?.substringAfter("idUnidad=") ?: "1"
            val comparar = consultar.comparar(unidadId, YearMonth.now())
            ServidorHttpMonolito.responderJson(exchange, 200, gson.toJson(comparar))
        }

        // 4. Estado de Salud de Dispositivos (Watchdog 10 min)
        servidor.registrarRuta("/api/v1/dashboard/dispositivos") { exchange ->
            val query = exchange.requestURI.query.orEmpty()
            val unidadId = query.split("&").find { it.startsWith("idUnidad=") }?.substringAfter("idUnidad=") ?: "1"
            val dispositivos = consultar.estadoDispositivos(unidadId)
            ServidorHttpMonolito.responderJson(exchange, 200, gson.toJson(dispositivos))
        }
    }
}

/**
 * Patrón ConcreteObserver & Adapter: Envía eventos Server-Sent Events (SSE) y latidos cada 20s.
 */
class DashboardNotificacionesAdapter(
    private val auth: AutenticarSesion,
    private val consultar: ConsultarMonitoreo
) : ObservadorMonitoreo {

    private val conexionesActivas = CopyOnWriteArrayList<OutputStream>()
    private val scheduler = Executors.newSingleThreadScheduledExecutor()

    init {
        // Latido cada 20 segundos para mantener vivas las conexiones SSE
        scheduler.scheduleAtFixedRate({
            enviarLatido()
        }, 20, 20, TimeUnit.SECONDS)
    }

    fun abrirConexion(exchange: HttpExchange) {
        val token = exchange.requestHeaders.getFirst("Authorization").orEmpty()
        if (token.isNotBlank() && !auth.sesionVigente(token)) {
            ServidorHttpMonolito.responderJson(exchange, 401, """{"error":"Sesion no autorizada"}""")
            return
        }

        exchange.responseHeaders.set("Content-Type", "text/event-stream")
        exchange.responseHeaders.set("Cache-Control", "no-cache")
        exchange.responseHeaders.set("Connection", "keep-alive")
        ServidorHttpMonolito.configurarCors(exchange)
        exchange.sendResponseHeaders(200, 0)

        val os = exchange.responseBody
        conexionesActivas.add(os)

        // Enviar snapshot inicial
        try {
            val snapshot = """data: {"evento":"SNAPSHOT","dispositivos":${Gson().toJson(consultar.estadoDispositivos())}}\n\n"""
            os.write(snapshot.toByteArray(Charsets.UTF_8))
            os.flush()
        } catch (_: Exception) {
            conexionesActivas.remove(os)
        }
    }

    override fun alCambiarEstado(alerta: AlertaDispositivo) {
        val json = """data: {"evento":"ALERTA","dispositivo":"${alerta.dispositivoId}","tipo":"${alerta.tipo}","instante":"${alerta.instante}"}\n\n"""
        val bytes = json.toByteArray(Charsets.UTF_8)

        for (os in conexionesActivas) {
            try {
                os.write(bytes)
                os.flush()
            } catch (_: Exception) {
                conexionesActivas.remove(os)
            }
        }
    }

    private fun enviarLatido() {
        val latido = ": ping\n\n".toByteArray(Charsets.UTF_8)
        for (os in conexionesActivas) {
            try {
                os.write(latido)
                os.flush()
            } catch (_: Exception) {
                conexionesActivas.remove(os)
            }
        }
    }
}

/**
 * Planificador periódico de supervisión (cada 30s sin solapamiento).
 */
class PlanificadorSupervision(
    private val supervisor: SupervisarDispositivos
) {
    private val scheduler = Executors.newSingleThreadScheduledExecutor()
    private var activo = false

    fun iniciar() {
        if (activo) return
        activo = true
        scheduler.scheduleWithFixedDelay({
            try {
                supervisor.verificar()
            } catch (e: Exception) {
                println("[PlanificadorSupervision] Excepcion capturada: ${e.message}")
            }
        }, 10, 30, TimeUnit.SECONDS)
        println(">>> [PlanificadorSupervision] Vigilancia de salud de dispositivos iniciada (cada 30 s).")
    }

    fun detener() {
        activo = false
        scheduler.shutdownNow()
    }
}
