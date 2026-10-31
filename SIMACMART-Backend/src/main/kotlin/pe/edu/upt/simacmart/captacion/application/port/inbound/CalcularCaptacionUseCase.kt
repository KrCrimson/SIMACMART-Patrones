package pe.edu.upt.simacmart.captacion.application.port.inbound

import pe.edu.upt.simacmart.captacion.domain.model.EventoVaciado
import java.math.BigDecimal
import java.time.Instant

interface CalcularCaptacionUseCase {
    fun procesarNuevaLectura(unidadId: Int, nuevoPesoKg: BigDecimal, fechaHora: Instant): EventoVaciado?
    fun calcularLitrosHoy(unidadId: Int): BigDecimal
    fun obtenerEventosRecientes(unidadId: Int, limite: Int = 10): List<EventoVaciado>
}
