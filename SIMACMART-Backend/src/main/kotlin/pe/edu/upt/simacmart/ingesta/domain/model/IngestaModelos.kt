package pe.edu.upt.simacmart.ingesta.domain.model

import java.math.BigDecimal
import java.time.Instant

enum class TipoDispositivo { ESTACION, BALANZA }

data class Dispositivo(
    val idDispositivo: Int,
    val idUnidad: Int,
    val codigo: String,
    val tipo: TipoDispositivo,
    val apiKeyHash: String,
    val activo: Boolean = true
)

data class LecturaBalanza(
    val idLectura: Long = 0,
    val idDispositivo: Int,
    val secuencia: Long,
    val fechaHora: Instant,
    val pesoKg: BigDecimal,
    val bateriaV: BigDecimal? = null,
    val valida: Boolean = true,
    val motivoInvalida: String? = null
)

data class LecturaEstacion(
    val idLectura: Long = 0,
    val idDispositivo: Int,
    val fechaHora: Instant,
    val temperaturaC: BigDecimal?,
    val humedadPct: BigDecimal?,
    val vientoMs: BigDecimal?,
    val radiacionWm2: BigDecimal?,
    val lluviaMm: BigDecimal?,
    val valida: Boolean = true
)
