package pe.edu.upt.simacmart.prediccion.application.port.outbound

import pe.edu.upt.simacmart.prediccion.domain.model.EstimacionHidricaResponse

/**
 * Patron Strategy (GoF - Comportamiento):
 * Define la interfaz comun para todos los algoritmos de estimacion hidrica.
 */
interface EstrategiaPrediccionPort {
    val codigo: String
    val nombreEstrategia: String
    fun estimar(unidadId: Int, dias: Int = 7): EstimacionHidricaResponse
}
