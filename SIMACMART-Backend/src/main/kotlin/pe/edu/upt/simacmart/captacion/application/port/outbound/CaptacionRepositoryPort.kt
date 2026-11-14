package pe.edu.upt.simacmart.captacion.application.port.outbound

import pe.edu.upt.simacmart.captacion.domain.model.CaptacionPeriodo
import pe.edu.upt.simacmart.captacion.domain.model.EventoVaciado
import java.math.BigDecimal

/**
 * Puerto de persistencia de Captacion segun Diagrama UML (Pag 4).
 */
interface CaptacionRepository {
    fun guardarOActualizar(captacion: CaptacionPeriodo) {}
    fun consultar(unidadId: String, periodo: String): CaptacionPeriodo? = null
}

/**
 * Extension para operaciones analiticas y vaciados de tina.
 */
interface CaptacionRepositoryPort : CaptacionRepository {
    fun guardarCaptacion(captacion: CaptacionPeriodo): Int
    fun registrarVaciado(vaciado: EventoVaciado): Int
    fun sumarLitrosDia(unidadId: Int): BigDecimal
    fun obtenerUltimoPesoRegistrado(unidadId: Int): BigDecimal?
    fun listarVaciados(unidadId: Int, limite: Int = 10): List<EventoVaciado>
}
