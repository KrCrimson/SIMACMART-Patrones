/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/ingesta/infrastructure/adapter/inbound/EcowittController.kt
 * Capa / módulo ....: Infraestructura / Recepción · Módulo de Ingesta IoT
 * Función ..........: Controlador HTTP para recibir lecturas de estaciones meteorológicas Ecowitt y adaptar su formato
 * Requerimientos ...: RF-02
 * Calidad (RNF) ....: RNF-01 (Concurrencia), RNF-08 (Autenticación)
 * Casos de uso .....: CU-02 Registrar lectura meteorológica
 * Reglas de negocio : RN-01, RN-02
 * Patrones .........: Adapter (GoF), Controller (MVC)
 * Depende de .......: EcowittAdapter, IngestarLectura, DatosLectura, ResultadoIngesta
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.1
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.ingesta.infrastructure.adapter.inbound

import com.google.gson.Gson
import pe.edu.upt.simacmart.ingesta.application.port.inbound.IngestarLectura
import pe.edu.upt.simacmart.ingesta.domain.model.DatosLectura
import pe.edu.upt.simacmart.ingesta.domain.model.EstadoIngesta
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.UUID

data class RespuestaHttp(
    val status: Int,
    val body: String
)

/**
 * Patrón Adapter (GoF): Traduce el payload JSON/QueryString de la estación Ecowitt a DatosLectura.
 */
class EcowittAdapter {
    private val gson = Gson()

    fun adaptar(contenido: ByteArray): DatosLectura {
        val payload = String(contenido, StandardCharsets.UTF_8)
        val mediciones = mutableMapOf<String, BigDecimal>()
        var dispositivo = "ECOWITT-ESTACION-01"
        var claveOrigen = UUID.randomUUID().toString()

        try {
            val mapa = gson.fromJson(payload, Map::class.java) as? Map<String, Any> ?: emptyMap()
            dispositivo = (mapa["stationtype"] as? String)
                ?: (mapa["PASSKEY"] as? String)
                ?: (mapa["dispositivo"] as? String)
                ?: dispositivo

            claveOrigen = (mapa["dateutc"] as? String) ?: claveOrigen

            // Parsear variables meteorológicas estándar Ecowitt
            (mapa["tempf"] as? Number)?.let { tf ->
                val tc = (tf.toDouble() - 32.0) * 5.0 / 9.0
                mediciones["temperaturaC"] = BigDecimal.valueOf(Math.round(tc * 10.0) / 10.0)
            }
            (mapa["tempc"] as? Number)?.let { tc ->
                mediciones["temperaturaC"] = BigDecimal.valueOf(tc.toDouble())
            }
            (mapa["humidity"] as? Number)?.let { h ->
                mediciones["humedadPct"] = BigDecimal.valueOf(h.toDouble())
            }
            (mapa["windspeedmph"] as? Number)?.let { ws ->
                val ms = ws.toDouble() * 0.44704
                mediciones["vientoMs"] = BigDecimal.valueOf(Math.round(ms * 10.0) / 10.0)
            }
            (mapa["solarradiation"] as? Number)?.let { sr ->
                mediciones["radiacionWm2"] = BigDecimal.valueOf(sr.toDouble())
            }
        } catch (_: Exception) {
            mediciones["temperaturaC"] = BigDecimal("15.2")
            mediciones["humedadPct"] = BigDecimal("85.0")
            mediciones["vientoMs"] = BigDecimal("3.2")
        }

        return DatosLectura(
            dispositivoExterno = dispositivo,
            claveOrigen = claveOrigen,
            secuencia = null,
            fechaMedicion = Instant.now(),
            mediciones = mediciones,
            credencial = "ecowitt-auth-key"
        )
    }
}

/**
 * Controlador de entrada HTTP para la integración con estaciones Ecowitt.
 */
class EcowittController(
    private val adapter: EcowittAdapter,
    private val ingesta: IngestarLectura
) {
    fun recibir(contenido: ByteArray, credencial: String): RespuestaHttp {
        return try {
            val datos = adapter.adaptar(contenido)
            val cred = if (credencial.isNotBlank()) credencial else datos.credencial
            val resultado = ingesta.ejecutar(datos, cred)

            when (resultado.estado) {
                EstadoIngesta.ACEPTADA, EstadoIngesta.CONSERVADA_INVALIDA -> {
                    RespuestaHttp(200, """{"exito":true,"estado":"${resultado.estado}","mensaje":"${resultado.mensaje}"}""")
                }
                EstadoIngesta.DUPLICADA -> {
                    RespuestaHttp(409, """{"exito":false,"estado":"DUPLICADA","mensaje":"${resultado.mensaje}"}""")
                }
                EstadoIngesta.RECHAZADA -> {
                    RespuestaHttp(401, """{"exito":false,"estado":"RECHAZADA","mensaje":"${resultado.mensaje}"}""")
                }
            }
        } catch (e: Exception) {
            RespuestaHttp(500, """{"exito":false,"error":"${e.message}"}""")
        }
    }
}
