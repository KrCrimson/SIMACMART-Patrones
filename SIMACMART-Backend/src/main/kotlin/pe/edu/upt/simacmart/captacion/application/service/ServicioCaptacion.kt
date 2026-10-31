package pe.edu.upt.simacmart.captacion.application.service

import pe.edu.upt.simacmart.captacion.application.port.inbound.CalcularCaptacionUseCase
import pe.edu.upt.simacmart.captacion.application.port.outbound.CaptacionRepositoryPort
import pe.edu.upt.simacmart.captacion.domain.model.CaptacionPeriodo
import pe.edu.upt.simacmart.captacion.domain.model.EventoVaciado
import pe.edu.upt.simacmart.captacion.domain.model.TipoPeriodo
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant

/**
 * Algoritmo de Calculo Hidrico y Deteccion de Vaciados (UC-04, UC-05).
 * Analiza la dinamica de pesaje en la tina colectora de 50 kg.
 */
class ServicioCaptacion(
    private val repo: CaptacionRepositoryPort
) : CalcularCaptacionUseCase {

    companion object {
        // Umbral de caida abrupta para detectar vaciado: Delta >= 5.0 kg
        val UMBRAL_VACIADO_KG = BigDecimal("5.000")
    }

    override fun procesarNuevaLectura(unidadId: Int, nuevoPesoKg: BigDecimal, fechaHora: Instant): EventoVaciado? {
        val pesoAnterior = repo.obtenerUltimoPesoRegistrado(unidadId)
                if (pesoAnterior == null) {
                        return null
        }

        val caida = pesoAnterior.subtract(nuevoPesoKg)

        // Deteccion de Vaciado de Tina
        if (caida >= UMBRAL_VACIADO_KG) {
            val litrosVaciados = caida.setScale(2, RoundingMode.HALF_UP)
            val vaciado = EventoVaciado(
                idUnidad = unidadId,
                tipo = "vaciado",
                fechaHora = fechaHora,
                pesoAntesKg = pesoAnterior,
                pesoDespuesKg = nuevoPesoKg,
                volumenLitros = litrosVaciados,
                descripcion = "Deteccion automatica: caida de ${pesoAnterior} kg a ${nuevoPesoKg} kg"
            )
            val id = repo.registrarVaciado(vaciado)
            println("[ServicioCaptacion] >>> Vaciado registrado con ID #$id ($litrosVaciados L)")
            return vaciado.copy(idEvento = id)
        }

        // Deteccion de condensacion de agua (incremento positivo)
        if (nuevoPesoKg > pesoAnterior) {
            val incremento = nuevoPesoKg.subtract(pesoAnterior).setScale(2, RoundingMode.HALF_UP)
            val captacion = CaptacionPeriodo(
                idUnidad = unidadId,
                tipoPeriodo = TipoPeriodo.HORA,
                inicioPeriodo = fechaHora,
                litrosCaptados = incremento
            )
            repo.guardarCaptacion(captacion)
        }

        return null
    }

    override fun calcularLitrosHoy(unidadId: Int): BigDecimal {
        return repo.sumarLitrosDia(unidadId)
    }

    override fun obtenerEventosRecientes(unidadId: Int, limite: Int): List<EventoVaciado> {
        return repo.listarVaciados(unidadId, limite)
    }
}
