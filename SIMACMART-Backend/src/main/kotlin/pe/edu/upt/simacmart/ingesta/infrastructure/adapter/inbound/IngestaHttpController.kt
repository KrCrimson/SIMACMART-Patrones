package pe.edu.upt.simacmart.ingesta.infrastructure.adapter.inbound

import com.google.gson.Gson
import com.google.gson.JsonObject
import pe.edu.upt.simacmart.captacion.application.service.ServicioCaptacion
import pe.edu.upt.simacmart.ingesta.application.port.inbound.IngestaBalanzaDto
import pe.edu.upt.simacmart.ingesta.application.port.inbound.IngestaEcowittDto
import pe.edu.upt.simacmart.ingesta.application.service.ServicioIngesta
import pe.edu.upt.simacmart.shared.infrastructure.http.ServidorHttpMonolito
import java.io.InputStreamReader
import java.math.BigDecimal
import java.time.Instant

class IngestaHttpController(
    private val servicioIngesta: ServicioIngesta,
    private val servicioCaptacion: ServicioCaptacion? = null
) {
    private val gson = Gson()

    fun registrarRutas(servidor: ServidorHttpMonolito) {
        // Receptor Balanza ESP32 (UC-01, UC-04, UC-05)
        servidor.registrarRuta("/api/v1/ingesta/balanza") { exchange ->
            if (exchange.requestMethod.equals("POST", ignoreCase = true)) {
                try {
                    val reader = InputStreamReader(exchange.requestBody, Charsets.UTF_8)
                    val body = gson.fromJson(reader, JsonObject::class.java)

                    val apiKey = body.get("apiKey")?.asString.orEmpty()
                    val dispositivo = body.get("dispositivoCodigo")?.asString ?: "BAL-ESP32-01"
                    val secuencia = body.get("secuencia")?.asLong ?: System.currentTimeMillis()
                    val peso = BigDecimal(body.get("pesoKg")?.asString ?: "0.0")
                    val bateria = body.get("bateriaV")?.asString?.let { BigDecimal(it) }

                    // 1. Evaluacion de dinamica hidrica y vaciados ANTES de sobreescribir ultimo peso
                    val vaciadoDetectado = servicioCaptacion?.procesarNuevaLectura(1, peso, Instant.now())

                    // 2. Almacenamiento de la lectura en lecturas_balanza
                    val res = servicioIngesta.recibirLectura(
                        IngestaBalanzaDto(apiKey, dispositivo, secuencia, peso, bateria)
                    )

                    val resJson = if (vaciadoDetectado != null) {
                        """{"exito":${res.exito},"mensaje":"${res.mensaje}","eventoVaciado":{"id":${vaciadoDetectado.idEvento},"litros":${vaciadoDetectado.volumenLitros},"antes":${vaciadoDetectado.pesoAntesKg},"despues":${vaciadoDetectado.pesoDespuesKg}}}"""
                    } else {
                        """{"exito":${res.exito},"mensaje":"${res.mensaje}"}"""
                    }

                    ServidorHttpMonolito.responderJson(exchange, if (res.exito) 200 else 400, resJson)
                } catch (e: Exception) {
                    ServidorHttpMonolito.responderJson(exchange, 400, """{"exito":false,"error":"${e.message}"}""")
                }
            } else {
                ServidorHttpMonolito.responderJson(exchange, 405, """{"error":"Metodo no permitido"}""")
            }
        }

        // Receptor Estacion Meteorologica Ecowitt (UC-02)
        servidor.registrarRuta("/api/v1/ingesta/ecowitt") { exchange ->
            if (exchange.requestMethod.equals("POST", ignoreCase = true)) {
                try {
                    val reader = InputStreamReader(exchange.requestBody, Charsets.UTF_8)
                    val body = gson.fromJson(reader, JsonObject::class.java)

                    val apiKey = body.get("apiKey")?.asString.orEmpty()
                    val dispositivo = body.get("dispositivoCodigo")?.asString ?: "ECOWITT-GW1100"
                    val temp = body.get("tempC")?.asString?.let { BigDecimal(it) }
                    val hum = body.get("humedadPct")?.asString?.let { BigDecimal(it) }
                    val viento = body.get("vientoMs")?.asString?.let { BigDecimal(it) }
                    val lluvia = body.get("lluviaMm")?.asString?.let { BigDecimal(it) }

                    val res = servicioIngesta.recibirClima(
                        IngestaEcowittDto(apiKey, dispositivo, temp, hum, viento, lluvia)
                    )

                    val json = """{"exito":${res.exito},"mensaje":"${res.mensaje}"}"""
                    ServidorHttpMonolito.responderJson(exchange, if (res.exito) 200 else 400, json)
                } catch (e: Exception) {
                    ServidorHttpMonolito.responderJson(exchange, 400, """{"exito":false,"error":"${e.message}"}""")
                }
            } else {
                ServidorHttpMonolito.responderJson(exchange, 405, """{"error":"Metodo no permitido"}""")
            }
        }
    }
}
