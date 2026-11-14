/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/prediccion/application/port/outbound/EvaluarModelo.kt
 * Capa / módulo ....: Aplicación · Módulo de Predicción Hídrica (Puerto de Salida)
 * Función ..........: Contrato para ejecutar la inferencia numérica sobre un modelo versionado
 * Requerimientos ...: RF-09, RF-10
 * Calidad (RNF) ....: RNF-04 (Interoperabilidad ML)
 * Casos de uso .....: CU-11 Generar proyección hídrica
 * Reglas de negocio : RN-09 (Versionado estricto de modelo ONNX)
 * Patrones .........: Ports & Adapters (Hexagonal - Puerto de Salida)
 * Depende de .......: ModeloVersionado, VectorVariables, BigDecimal
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.3
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.prediccion.application.port.outbound

import pe.edu.upt.simacmart.prediccion.domain.model.ModeloVersionado
import pe.edu.upt.simacmart.prediccion.domain.model.VectorVariables
import java.math.BigDecimal

interface EvaluarModelo {
    fun ejecutar(modelo: ModeloVersionado, variables: VectorVariables): BigDecimal
}
