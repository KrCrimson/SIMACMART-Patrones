/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/prediccion/application/port/outbound/PrediccionRepository.kt
 * Capa / módulo ....: Aplicación · Módulo de Predicción Hídrica (Puerto de Salida)
 * Función ..........: Contrato para almacenamiento y consulta de predicciones mensuales calculadas
 * Requerimientos ...: RF-08, RF-10
 * Calidad (RNF) ....: RNF-02 (Mantenibilidad), RNF-01 (Persistencia Concurrente)
 * Casos de uso .....: CU-10 Consultar estimación de captación, CU-11 Generar proyección hídrica
 * Reglas de negocio : RN-08 (Inmutabilidad de predicción generada)
 * Patrones .........: Repository, Ports & Adapters (Hexagonal - Puerto de Salida)
 * Depende de .......: PrediccionMensual, YearMonth
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.3
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.prediccion.application.port.outbound

import pe.edu.upt.simacmart.prediccion.domain.model.PrediccionMensual
import java.time.YearMonth

interface PrediccionRepository {
    fun guardar(prediccion: PrediccionMensual)
    fun consultar(unidadId: String, mesObjetivo: YearMonth): PrediccionMensual?
}
