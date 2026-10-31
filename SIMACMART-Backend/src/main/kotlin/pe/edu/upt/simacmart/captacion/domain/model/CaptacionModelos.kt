package pe.edu.upt.simacmart.captacion.domain.model

import java.math.BigDecimal
import java.time.Instant

enum class TipoPeriodo { HORA, DIA, MES }

data class CaptacionPeriodo(
    val idCaptacion: Int = 0,
    val idUnidad: Int,
    val tipoPeriodo: TipoPeriodo,
    val inicioPeriodo: Instant,
    val litrosCaptados: BigDecimal
)

data class EventoVaciado(
    val idEvento: Int = 0,
    val idUnidad: Int,
    val tipo: String = "vaciado",
    val fechaHora: Instant = Instant.now(),
    val pesoAntesKg: BigDecimal,
    val pesoDespuesKg: BigDecimal,
    val volumenLitros: BigDecimal,
    val descripcion: String? = "Caida abrupta de peso (Vaciado de tina)"
)
