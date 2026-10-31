package pe.edu.upt.simacmart.ingesta.application.port.inbound

import pe.edu.upt.simacmart.shared.domain.Resultado
import java.math.BigDecimal

data class IngestaBalanzaDto(
    val apiKey: String,
    val dispositivoCodigo: String,
    val secuencia: Long,
    val pesoKg: BigDecimal,
    val bateriaV: BigDecimal?
)

data class IngestaEcowittDto(
    val apiKey: String,
    val dispositivoCodigo: String,
    val tempC: BigDecimal?,
    val humedadPct: BigDecimal?,
    val vientoMs: BigDecimal?,
    val lluviaMm: BigDecimal?
)

interface IngestaBalanzaUseCase {
    fun recibirLectura(dto: IngestaBalanzaDto): Resultado<Long>
}

interface IngestaEcowittUseCase {
    fun recibirClima(dto: IngestaEcowittDto): Resultado<Long>
}
