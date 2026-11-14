/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/ingesta/infrastructure/adapter/inbound/MqttFacade.kt
 * Capa / módulo ....: Infraestructura / Recepción · Módulo de Ingesta IoT
 * Función ..........: Fachada del cliente MQTT (Paho / EMQX) y observador concreto de la balanza
 * Requerimientos ...: RF-01, RF-02
 * Calidad (RNF) ....: RNF-01 (Concurrencia), RNF-03 (Tolerancia a fallos de red)
 * Casos de uso .....: CU-01 Registrar pesaje de balanza
 * Reglas de negocio : RN-02 (Acuse de recepción inmediato)
 * Patrones .........: Facade (GoF), Observer (GoF), Adapter (GoF)
 * Depende de .......: TransporteMensajes, ObservadorMensaje, BalanzaAdapter, IngestarLectura
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.1
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.ingesta.infrastructure.adapter.inbound

import com.google.gson.Gson
import pe.edu.upt.simacmart.ingesta.application.port.inbound.IngestarLectura
import pe.edu.upt.simacmart.ingesta.application.port.transport.ObservadorMensaje
import pe.edu.upt.simacmart.ingesta.application.port.transport.TransporteMensajes
import pe.edu.upt.simacmart.ingesta.domain.model.DatosLectura
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Patrón Facade (GoF): Simplifica el acceso y suscripción a la red MQTT.
 */
class MqttFacade : TransporteMensajes {
    private val observadoresPorTema = ConcurrentHashMap<String, CopyOnWriteArrayList<ObservadorMensaje>>()
    private val mensajesPublicados = CopyOnWriteArrayList<Pair<String, ByteArray>>()

    override fun suscribir(tema: String, observador: ObservadorMensaje) {
        val lista = observadoresPorTema.computeIfAbsent(tema) { CopyOnWriteArrayList() }
        if (!lista.contains(observador)) {
            lista.add(observador)
        }
    }

    override fun publicar(tema: String, contenido: ByteArray) {
        mensajesPublicados.add(tema to contenido)
        println("[MqttFacade] Mensaje publicado en tema '$tema' (${contenido.size} bytes)")
    }

    /**
     * Invocado cuando llega un mensaje desde la red física del broker.
     */
    fun alEventoDelCliente(tema: String, contenido: ByteArray) {
        for ((temaRegistrado, observadores) in observadoresPorTema) {
            if (coincideTema(temaRegistrado, tema)) {
                for (obs in observadores) {
                    try {
                        obs.alRecibir(tema, contenido)
                    } catch (e: Exception) {
                        println("[MqttFacade] Error en observador: ${e.message}")
                    }
                }
            }
        }
    }

    private fun coincideTema(patron: String, tema: String): Boolean {
        if (patron == tema || patron == "#") return true
        val partesPatron = patron.split("/")
        val partesTema = tema.split("/")
        if (partesPatron.size != partesTema.size && !patron.contains("#")) return false
        for (i in partesPatron.indices) {
            if (partesPatron[i] == "#") return true
            if (partesPatron[i] != "+" && partesPatron[i] != partesTema.getOrNull(i)) return false
        }
        return true
    }

    fun obtenerUltimosPublicados(): List<Pair<String, ByteArray>> = mensajesPublicados
}

/**
 * Patrón Adapter (GoF): Traduce los bytes JSON del sensor de balanza a DatosLectura de aplicación.
 */
class BalanzaAdapter {
    private val gson = Gson()

    fun adaptar(tema: String, contenido: ByteArray): DatosLectura {
        val json = String(contenido, StandardCharsets.UTF_8)
        val mapa = try {
            gson.fromJson(json, Map::class.java) as? Map<String, Any> ?: emptyMap()
        } catch (_: Exception) {
            emptyMap()
        }

        val dispositivo = (mapa["dispositivo"] as? String)
            ?: (mapa["idDispositivo"] as? String)
            ?: tema.split("/").getOrElse(1) { "BAL-PACHIA-01" }

        val sec = ((mapa["secuencia"] as? Number)?.toLong()) ?: 1L
        val peso = BigDecimal.valueOf((mapa["pesoKg"] as? Number)?.toDouble() ?: 0.0)
        val bat = (mapa["bateriaV"] as? Number)?.let { BigDecimal.valueOf(it.toDouble()) }
        val credencial = (mapa["credencial"] as? String) ?: (mapa["apiKey"] as? String) ?: "secret-token"

        val mediciones = mutableMapOf<String, BigDecimal>("pesoKg" to peso)
        if (bat != null) mediciones["bateriaV"] = bat

        return DatosLectura(
            dispositivoExterno = dispositivo,
            claveOrigen = "SEQ-$sec",
            secuencia = sec,
            fechaMedicion = Instant.now(),
            mediciones = mediciones,
            credencial = credencial
        )
    }
}

/**
 * Patrón ConcreteObserver (GoF): Escucha lecturas de balanza, las adapta,
 * llama a IngestarLectura y publica el acuse en MQTT.
 */
class ReceptorBalanza(
    private val adapter: BalanzaAdapter,
    private val ingesta: IngestarLectura,
    private val transporte: TransporteMensajes
) : ObservadorMensaje {

    override fun alRecibir(tema: String, contenido: ByteArray) {
        val datosLectura = adapter.adaptar(tema, contenido)
        val resultado = ingesta.ejecutar(datosLectura, datosLectura.credencial)

        // Publicar acuse de recepción hacia la balanza física
        val acuseJson = """{"dispositivo":"${datosLectura.dispositivoExterno}","secuencia":${datosLectura.secuencia ?: 0},"estado":"${resultado.estado}","mensaje":"${resultado.mensaje}"}"""
        val temaAcuse = "simacmart/balanza/acuse/${datosLectura.dispositivoExterno}"
        transporte.publicar(temaAcuse, acuseJson.toByteArray(StandardCharsets.UTF_8))
    }
}
