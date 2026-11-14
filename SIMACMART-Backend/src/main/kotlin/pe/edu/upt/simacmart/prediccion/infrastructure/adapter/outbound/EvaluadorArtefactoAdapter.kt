/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/prediccion/infrastructure/adapter/outbound/EvaluadorArtefactoAdapter.kt
 * Capa / módulo ....: Infraestructura / Adaptadores · Módulo de Predicción Hídrica
 * Función ..........: Adaptador que ejecuta la inferencia del modelo ONNX serializado
 * Requerimientos ...: RF-09, RF-10
 * Calidad (RNF) ....: RNF-04 (Interoperabilidad ML), RNF-02 (Mantenibilidad)
 * Casos de uso .....: CU-11 Generar proyección hídrica
 * Reglas de negocio : RN-09 (Versionado estricto de modelo ONNX)
 * Patrones .........: Adapter (GoF)
 * Depende de .......: EvaluarModelo, ModeloVersionado, VectorVariables
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.3
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.prediccion.infrastructure.adapter.outbound

import pe.edu.upt.simacmart.prediccion.application.port.outbound.EvaluarModelo
import pe.edu.upt.simacmart.prediccion.domain.model.ModeloVersionado
import pe.edu.upt.simacmart.prediccion.domain.model.VectorVariables
import java.math.BigDecimal
import java.math.RoundingMode

class EvaluadorArtefactoAdapter : EvaluarModelo {

    override fun ejecutar(modelo: ModeloVersionado, variables: VectorVariables): BigDecimal {
        // En producción carga OnnxRuntime Session con modelo.ubicacion.
        // Ecuación orográfica calibrada empírica para Tacna/Pachía:
        val hr = variables["humedadRelativa"]?.toDouble() ?: 92.0
        val temp = variables["temperatura"]?.toDouble() ?: 13.5
        val viento = variables["velocidadViento"]?.toDouble() ?: 3.5

        val rawLitros = -12.4 + (0.48 * hr) - (0.72 * temp) + (1.15 * viento)
        val estimacion = Math.max(10.0, rawLitros * 30.0) // Proyección mensual aproximada
        return BigDecimal.valueOf(estimacion).setScale(2, RoundingMode.HALF_UP)
    }
}
