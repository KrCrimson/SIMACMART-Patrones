/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/ingesta/infrastructure/external/FuentesExternas.kt
 * Capa / módulo ....: Fuentes Externas · Módulo de Ingesta IoT
 * Función ..........: Simulación y cliente de subsistemas de campo (Balanza HX711 y Estación Ecowitt)
 * Requerimientos ...: RF-01, RF-02
 * Calidad (RNF) ....: RNF-03 (Store-and-forward en nodo físico de campo)
 * Casos de uso .....: CU-01 Registrar pesaje de balanza, CU-02 Registrar lectura meteorológica
 * Reglas de negocio : RN-02 (Reenvío ante falta de acuse y cola de cuarentena ante rechazo permanente)
 * Patrones .........: External Subsystem
 * Depende de .......: TransporteMensajes, EcowittController
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.1
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.ingesta.infrastructure.external

import pe.edu.upt.simacmart.ingesta.application.port.transport.TransporteMensajes
import pe.edu.upt.simacmart.ingesta.infrastructure.adapter.inbound.EcowittController
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Representa el firmware de la Balanza electrónica instalada en el atrapanieblas (HX711 + ESP32).
 * Implementa Store-and-Forward resiliente diferenciando fallos temporales de permanentes.
 */
class Balanza(
    private val dispositivoCodigo: String = "BAL-PACHIA-01",
    private val transporte: TransporteMensajes? = null
) {
    private val colaPendientes = ConcurrentLinkedQueue<ByteArray>()
    private val colaCuarentena = ConcurrentLinkedQueue<ByteArray>()
    private var secuenciaActual = 0L

    fun capturarYEncolar() {
        secuenciaActual++
        val payload = """
            {
                "dispositivo": "$dispositivoCodigo",
                "secuencia": $secuenciaActual,
                "pesoKg": 42.5,
                "bateriaV": 3.95,
                "credencial": "balanza-token-xyz"
            }
        """.trimIndent().toByteArray(StandardCharsets.UTF_8)

        colaPendientes.add(payload)
        transporte?.publicar("simacmart/v1/$dispositivoCodigo/balanza", payload)
    }

    fun reenviarPendientes() {
        for (item in colaPendientes) {
            transporte?.publicar("simacmart/v1/$dispositivoCodigo/balanza", item)
        }
    }

    /**
     * Procesa el acuse del servidor distinguiendo confirmaciones de rechazos permanentes:
     * - ACEPTADA / DUPLICADA / CONSERVADA_INVALIDA: La lectura se libera de la cola activa.
     * - RECHAZADA: Error permanente (credencial o formato corrupto). Se envía a cuarentena
     *   sin reintento para no saturar la red ni bloquear la cola.
     */
    fun procesarAcuse(contenido: ByteArray) {
        val respuesta = String(contenido, StandardCharsets.UTF_8)
        if (respuesta.contains("ACEPTADA") || respuesta.contains("DUPLICADA") || respuesta.contains("CONSERVADA_INVALIDA")) {
            colaPendientes.poll()
            println("[Balanza-$dispositivoCodigo] Acuse confirmado: lectura liberada de cola local.")
        } else if (respuesta.contains("RECHAZADA")) {
            val rechazada = colaPendientes.poll()
            if (rechazada != null) {
                colaCuarentena.add(rechazada)
                println("[Balanza-$dispositivoCodigo] Error permanente: mensaje movido a cuarentena sin reintento.")
            }
        }
    }

    fun cantidadPendientes(): Int = colaPendientes.size
    fun cantidadCuarentena(): Int = colaCuarentena.size
}

/**
 * Representa la estación meteorológica externa Ecowitt.
 */
class Ecowitt(
    private val controller: EcowittController? = null
) {
    fun enviarLectura() {
        val json = """
            {
                "stationtype": "Ecowitt-GW1100",
                "PASSKEY": "ECOWITT-TOKEN-123",
                "tempc": 14.2,
                "humidity": 93.0,
                "windspeedmph": 7.5,
                "solarradiation": 120.0
            }
        """.trimIndent().toByteArray(StandardCharsets.UTF_8)

        controller?.recibir(json, "ECOWITT-TOKEN-123")
    }
}
