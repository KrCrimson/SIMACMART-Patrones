/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/ingesta/domain/model/Lectura.kt
 * Capa / módulo ....: Dominio · Módulo de Ingesta IoT
 * Función ..........: Entidad Lectura y objetos de valor para telemetría de balanzas y clima
 * Requerimientos ...: RF-01, RF-02, RF-03
 * Calidad (RNF) ....: RNF-01 (Concurrencia), RNF-03 (Tolerancia a fallos)
 * Casos de uso .....: CU-01 Registrar pesaje de balanza, CU-02 Registrar lectura meteorológica
 * Reglas de negocio : RN-01 (Validación de rangos físicos), RN-02 (Detección de duplicados)
 * Patrones .........: Domain Entity, Value Object
 * Depende de .......: Java Time Instant, Java Math BigDecimal
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.1 y 4.2
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.ingesta.domain.model

import java.math.BigDecimal
import java.time.Instant

/**
 * Estados posibles del procesamiento de una lectura en el subsistema de ingesta.
 */
enum class EstadoIngesta {
    ACEPTADA,
    DUPLICADA,
    CONSERVADA_INVALIDA,
    RECHAZADA
}

/**
 * DTO que transporta los datos adaptados desde cualquier fuente (MQTT o HTTP) hacia Aplicación.
 */
data class DatosLectura(
    val dispositivoExterno: String,
    val claveOrigen: String,
    val secuencia: Long? = null,
    val fechaMedicion: Instant = Instant.now(),
    val mediciones: Map<String, BigDecimal> = emptyMap(),
    val credencial: String = ""
)

/**
 * Resultado estructurado retornado por el Servicio de Ingesta.
 */
data class ResultadoIngesta(
    val estado: EstadoIngesta,
    val mensaje: String = "",
    val identificadorLectura: String? = null
)

/**
 * Entidad de Dominio Lectura según Diagrama UML de Ingesta y Persistencia.
 */
class Lectura(
    val dispositivoId: String,
    val claveOrigen: String,
    val secuencia: Long? = null,
    val fechaMedicion: Instant,
    val fechaRecepcion: Instant = Instant.now(),
    val mediciones: Map<String, BigDecimal> = emptyMap()
) {
    val motivosInvalidez: MutableList<String> = mutableListOf()

    fun registrarInvalidez(motivo: String) {
        motivosInvalidez.add(motivo)
    }

    fun esValida(): Boolean = motivosInvalidez.isEmpty()
}
