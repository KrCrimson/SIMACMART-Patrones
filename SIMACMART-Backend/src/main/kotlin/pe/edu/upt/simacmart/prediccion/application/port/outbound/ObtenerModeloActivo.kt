/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/prediccion/application/port/outbound/ObtenerModeloActivo.kt
 * Capa / módulo ....: Aplicación · Módulo de Predicción Hídrica (Puerto de Salida)
 * Función ..........: Contrato para obtener metadatos y ubicación del modelo de Machine Learning activo
 * Requerimientos ...: RF-09, RF-10
 * Calidad (RNF) ....: RNF-04 (Interoperabilidad ML)
 * Casos de uso .....: CU-11 Generar proyección hídrica
 * Reglas de negocio : RN-09 (Versionado estricto de modelo ONNX)
 * Patrones .........: Ports & Adapters (Hexagonal - Puerto de Salida)
 * Depende de .......: ModeloVersionado
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.3
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.prediccion.application.port.outbound

import pe.edu.upt.simacmart.prediccion.domain.model.ModeloVersionado

interface ObtenerModeloActivo {
    fun obtener(): ModeloVersionado
}
