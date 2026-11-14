/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/shared/infrastructure/http/ServidorHttpMonolito.kt
 * Capa / módulo ....: Shared / Infraestructura HTTP · Kernel Transversal
 * Función ..........: Servidor HTTP nativo de la JVM (com.sun.net.httpserver) con enrutador, CORS y handlers por defecto
 * Requerimientos ...: RF-01 al RF-10
 * Calidad (RNF) ....: RNF-01 (Concurrencia), RNF-02 (Mantenibilidad sin frameworks pesados)
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART)
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.shared.infrastructure.http

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress

class ServidorHttpMonolito(private val port: Int = 8080) {
    private val server: HttpServer = HttpServer.create(InetSocketAddress(port), 0)

    init {
        // Enrutador raíz para verificación de salud, welcome y favicon sin 404
        server.createContext("/") { exchange ->
            configurarCors(exchange)

            if (exchange.requestMethod.equals("OPTIONS", ignoreCase = true)) {
                exchange.sendResponseHeaders(204, -1)
                exchange.close()
                return@createContext
            }

            val path = exchange.requestURI.path
            when (path) {
                "/", "/health", "/api", "/api/v1" -> {
                    val json = """
                        {
                            "sistema": "SIMACMART Backend - Monolito Modular",
                            "estado": "ACTIVO",
                            "version": "1.0.0-PROD",
                            "arquitectura": "Hexagonal + Patrones GoF (Strategy, Adapter, CoR, UoW)",
                            "institucion": "Universidad Privada de Tacna",
                            "docente": "Ing. Alberto Flor Rodriguez",
                            "autores": "Hernandez y Arce",
                            "endpoints": [
                                "/api/v1/dashboard/resumen?idUnidad=1",
                                "/api/v1/prediccion/estimacion?idUnidad=1&estrategia=ML",
                                "/api/v1/prediccion/modelos",
                                "/api/v1/ingesta/balanza",
                                "/api/v1/ingesta/ecowitt",
                                "/api/v1/captacion/vaciados?idUnidad=1"
                            ]
                        }
                    """.trimIndent()
                    responderJson(exchange, 200, json)
                }
                "/favicon.ico" -> {
                    exchange.sendResponseHeaders(204, -1)
                    exchange.close()
                }
                else -> {
                    responderJson(exchange, 404, """{"error":"Ruta no encontrada","path":"$path"}""")
                }
            }
        }
    }

    fun registrarRuta(path: String, handler: (HttpExchange) -> Unit) {
        server.createContext(path) { exchange ->
            configurarCors(exchange)

            if (exchange.requestMethod.equals("OPTIONS", ignoreCase = true)) {
                exchange.sendResponseHeaders(204, -1)
                exchange.close()
                return@createContext
            }

            handler(exchange)
        }
    }

    fun iniciar() {
        server.executor = null
        server.start()
        println(">>> Monolito Modular SIMACMART escuchando en http://localhost:$port (Servidor nativo JVM)")
    }

    fun detener() {
        server.stop(0)
    }

    companion object {
        fun configurarCors(exchange: HttpExchange) {
            exchange.responseHeaders.set("Access-Control-Allow-Origin", "*")
            exchange.responseHeaders.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
            exchange.responseHeaders.set("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With")
        }

        fun responderJson(exchange: HttpExchange, codigo: Int, json: String) {
            val bytes = json.toByteArray(Charsets.UTF_8)
            configurarCors(exchange)
            exchange.responseHeaders.set("Content-Type", "application/json; charset=UTF-8")
            exchange.sendResponseHeaders(codigo, bytes.size.toLong())
            exchange.responseBody.use { it.write(bytes) }
        }
    }
}
