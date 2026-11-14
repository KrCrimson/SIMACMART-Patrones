package pe.edu.upt.simacmart.captacion.domain.model

import java.math.BigDecimal
import java.time.Instant

enum class TipoPeriodo { HORA, DIA, MES }

/**
 * Modelo de Dominio para periodos de captacion hidrica (Diagrama Persistencia Pag 5).
 */
data class CaptacionPeriodo(
    val idCaptacion: Int = 0,
    val unidadId: String = "1",
    val inicio: Instant = Instant.now(),
    val fin: Instant = Instant.now(),
    val litrosCaptados: BigDecimal = BigDecimal.ZERO,
    val lecturasValidas: Int = 0,
    val fechaCalculo: Instant = Instant.now(),
    val tipoPeriodo: TipoPeriodo = TipoPeriodo.DIA
) {
    // Constructor de conveniencia para compatibilidad con codigo existente
    constructor(
        idCaptacion: Int = 0,
        idUnidad: Int,
        tipoPeriodo: TipoPeriodo,
        inicioPeriodo: Instant,
        litrosCaptados: BigDecimal
    ) : this(
        idCaptacion = idCaptacion,
        unidadId = idUnidad.toString(),
        inicio = inicioPeriodo,
        fin = inicioPeriodo.plusSeconds(86400),
        litrosCaptados = litrosCaptados,
        lecturasValidas = 0,
        fechaCalculo = Instant.now(),
        tipoPeriodo = tipoPeriodo
    )

    val idUnidad: Int
        get() = unidadId.toIntOrNull() ?: 1
}

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
