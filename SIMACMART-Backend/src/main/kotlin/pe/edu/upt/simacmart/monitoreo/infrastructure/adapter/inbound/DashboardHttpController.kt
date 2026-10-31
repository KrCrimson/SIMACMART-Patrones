package pe.edu.upt.simacmart.monitoreo.infrastructure.adapter.inbound

import pe.edu.upt.simacmart.captacion.application.service.ServicioCaptacion
import pe.edu.upt.simacmart.monitoreo.application.service.ServicioMonitoreo
import pe.edu.upt.simacmart.shared.infrastructure.http.JsonUtils
import pe.edu.upt.simacmart.shared.infrastructure.http.ServidorHttpMonolito

class DashboardHttpController(
    private val servicioMonitoreo: ServicioMonitoreo,
    private val servicioCaptacion: ServicioCaptacion? = null
) {
    fun registrarRutas(servidor: ServidorHttpMonolito) {
        // Resumen Dashboard en Tiempo Real (UC-06)
        val resumenHandler: (com.sun.net.httpserver.HttpExchange) -> Unit = { exchange ->
            if (exchange.requestMethod.equals("GET", ignoreCase = true)) {
                val query = exchange.requestURI.query.orEmpty()
                val idUnidad = query.split("&")
                    .find { it.startsWith("idUnidad=") }
                    ?.substringAfter("idUnidad=")
                    ?.toIntOrNull() ?: 1

                val resumen = servicioMonitoreo.obtenerResumen(idUnidad)
                val json = JsonUtils.gson.toJson(resumen)
                ServidorHttpMonolito.responderJson(exchange, 200, json)
            } else {
                ServidorHttpMonolito.responderJson(exchange, 405, "{\"error\":\"Metodo no permitido\"}")
            }
        }

        servidor.registrarRuta("/api/v1/dashboard/resumen", resumenHandler)
        servidor.registrarRuta("/api/dashboard/resumen", resumenHandler)

        // Eventos de Vaciado de Tina (UC-04, UC-05)
        servidor.registrarRuta("/api/v1/captacion/vaciados") { exchange ->
            if (exchange.requestMethod.equals("GET", ignoreCase = true)) {
                val query = exchange.requestURI.query.orEmpty()
                val idUnidad = query.split("&")
                    .find { it.startsWith("idUnidad=") }
                    ?.substringAfter("idUnidad=")
                    ?.toIntOrNull() ?: 1

                val vaciados = servicioCaptacion?.obtenerEventosRecientes(idUnidad, 10) ?: emptyList()
                val json = JsonUtils.gson.toJson(vaciados)
                ServidorHttpMonolito.responderJson(exchange, 200, json)
            } else {
                ServidorHttpMonolito.responderJson(exchange, 405, "{\"error\":\"Metodo no permitido\"}")
            }
        }
    }
}
