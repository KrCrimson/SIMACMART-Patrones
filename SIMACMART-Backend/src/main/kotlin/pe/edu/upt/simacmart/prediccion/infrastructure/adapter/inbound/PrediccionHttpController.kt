package pe.edu.upt.simacmart.prediccion.infrastructure.adapter.inbound

import pe.edu.upt.simacmart.prediccion.application.service.ServicioPrediccion
import pe.edu.upt.simacmart.shared.infrastructure.http.JsonUtils
import pe.edu.upt.simacmart.shared.infrastructure.http.ServidorHttpMonolito

class PrediccionHttpController(
    private val servicioPrediccion: ServicioPrediccion
) {
    fun registrarRutas(servidor: ServidorHttpMonolito) {
        // Endpoint Consulta de Estimacion Hidrica (UC-10, UC-11)
        servidor.registrarRuta("/api/v1/prediccion/estimacion") { exchange ->
            if (exchange.requestMethod.equals("GET", ignoreCase = true)) {
                try {
                    val query = exchange.requestURI.query.orEmpty()
                    val params = query.split("&").associate {
                        val parts = it.split("=")
                        if (parts.size == 2) parts[0] to parts[1] else "" to ""
                    }

                    val idUnidad = params["idUnidad"]?.toIntOrNull() ?: 1
                    val estrategia = params["estrategia"] ?: "ML"

                    val resultado = servicioPrediccion.estimarConEstrategia(idUnidad, estrategia)
                    val json = JsonUtils.gson.toJson(resultado)
                    ServidorHttpMonolito.responderJson(exchange, 200, json)
                } catch (e: Exception) {
                    ServidorHttpMonolito.responderJson(exchange, 400, "{\"exito\":false,\"error\":\"" + (e.message ?: "Error desconocido") + "\"}")
                }
            } else {
                ServidorHttpMonolito.responderJson(exchange, 405, "{\"error\":\"Metodo no permitido\"}")
            }
        }

        // Endpoint Listado de Modelos Registrados
        servidor.registrarRuta("/api/v1/prediccion/modelos") { exchange ->
            if (exchange.requestMethod.equals("GET", ignoreCase = true)) {
                try {
                    val modelos = servicioPrediccion.listarModelosDisponibles()
                    val json = JsonUtils.gson.toJson(modelos)
                    ServidorHttpMonolito.responderJson(exchange, 200, json)
                } catch (e: Exception) {
                    ServidorHttpMonolito.responderJson(exchange, 500, "{\"error\":\"" + (e.message ?: "Error al listar modelos") + "\"}")
                }
            } else {
                ServidorHttpMonolito.responderJson(exchange, 405, "{\"error\":\"Metodo no permitido\"}")
            }
        }
    }
}
