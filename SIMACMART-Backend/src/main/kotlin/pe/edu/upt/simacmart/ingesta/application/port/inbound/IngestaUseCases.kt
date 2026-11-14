/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/ingesta/application/port/inbound/IngestaUseCases.kt
 * Capa / módulo ....: Aplicación · Módulo de Ingesta IoT
 * Función ..........: DTOs y casos de uso para controladores de entrada HTTP
 * Requerimientos ...: RF-01, RF-02
 * Calidad (RNF) ....: RNF-01 (Concurrencia)
 * Casos de uso .....: CU-01 Registrar pesaje de balanza, CU-02 Registrar lectura meteorológica
 * Reglas de negocio : RN-01, RN-02
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART)
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.ingesta.application.port.inbound

import pe.edu.upt.simacmart.shared.domain.Resultado
import java.math.BigDecimal

data class IngestaBalanzaDto(
    val apiKey: String,
    val dispositivoCodigo: String,
    val secuencia: Long,
    val pesoKg: BigDecimal,
    val bateriaV: BigDecimal? = null
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
