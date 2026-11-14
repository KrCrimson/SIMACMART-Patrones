package pe.edu.upt.simacmart.ingesta.infrastructure.adapter.inbound

import java.math.BigDecimal

/**
 * ============================================================================
 * DTOs PARA ADAPTADORES DE ENTRADA MQTT (Pags 15 y 16 UML)
 * ============================================================================
 */

/**
 * Encapsula el mensaje en bruto recibido desde el Broker MQTT.
 */
data class MensajeLecturaMqtt(
    val tema: String,
    val contenido: ByteArray
) {
    val contenidoString: String
        get() = String(contenido, Charsets.UTF_8)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as MensajeLecturaMqtt
        if (tema != other.tema) return false
        if (!contenido.contentEquals(other.contenido)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = tema.hashCode()
        result = 31 * result + contenido.contentHashCode()
        return result
    }
}

/**
 * DTO para deserializar el payload MQTT de la Balanza de Tina (Pags 15-16).
 */
data class LecturaBalanzaDTO(
    val identificadorExterno: String,
    val secuencia: Long = 0L,
    val fechaMedicion: String? = null,
    val pesoKg: BigDecimal = BigDecimal.ZERO,
    val bateriaV: BigDecimal? = null
)

/**
 * DTO para deserializar el payload MQTT de la Estacion Ambiental (Pag 15).
 */
data class EcowittLecturaDTO(
    val identificadorExterno: String,
    val idLecturaOrigen: String = java.util.UUID.randomUUID().toString(),
    val fechaMedicion: String? = null,
    val temperatura: BigDecimal? = null,
    val humedad: BigDecimal? = null,
    val viento: BigDecimal? = null,
    val lluvia: BigDecimal? = null,
    val radiacion: BigDecimal? = null
)

/**
 * Acuse de recibo estandarizado de vuelta al bus de mensajeria (Pag 16).
 */
data class AcuseLecturaDTO(
    val idLecturaOrigen: String,
    val estado: String,
    val identificadorLectura: String? = null,
    val detalle: String? = null
)

