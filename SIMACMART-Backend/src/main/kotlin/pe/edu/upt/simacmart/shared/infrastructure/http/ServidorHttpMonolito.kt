package pe.edu.upt.simacmart.shared.infrastructure.http

import com.sun.net.httpserver.HttpServer
import com.sun.net.httpserver.HttpExchange
import java.net.InetSocketAddress

/**
 * Servidor HTTP central del Monolito Modular.
 * Expone un unico punto de red donde cada modulo registra sus rutas (endpoints).
 * Cumple la restriccion academica de no usar frameworks de negocio como Spring Boot.
 * Incluye soporte completo de CORS y preflight OPTIONS para el Frontend desacoplado.
 */
class ServidorHttpMonolito(private val port: Int = 8080) {
    private val server: HttpServer = HttpServer.create(InetSocketAddress(port), 0)

    fun registrarRuta(path: String, handler: (HttpExchange) -> Unit) {
        server.createContext(path) { exchange ->
            // Encabezados CORS globales para permitir comunicacion con el frontend desacoplado
            exchange.responseHeaders.set("Access-Control-Allow-Origin", "*")
            exchange.responseHeaders.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
            exchange.responseHeaders.set("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With")

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
        fun responderJson(exchange: HttpExchange, codigo: Int, json: String) {
            val bytes = json.toByteArray(Charsets.UTF_8)
            exchange.responseHeaders.set("Content-Type", "application/json; charset=UTF-8")
            exchange.responseHeaders.set("Access-Control-Allow-Origin", "*")
            exchange.responseHeaders.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
            exchange.responseHeaders.set("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With")
            exchange.sendResponseHeaders(codigo, bytes.size.toLong())
            exchange.responseBody.use { it.write(bytes) }
        }
    }
}
