package pe.edu.upt.simacmart.prediccion.application.port.inbound

import pe.edu.upt.simacmart.prediccion.domain.model.EstimacionHidricaResponse
import pe.edu.upt.simacmart.prediccion.domain.model.ModeloMLInfo

interface GenerarPrediccionUseCase {
    fun estimarConEstrategia(unidadId: Int, codigoEstrategia: String): EstimacionHidricaResponse
    fun listarModelosDisponibles(): List<ModeloMLInfo>
}
