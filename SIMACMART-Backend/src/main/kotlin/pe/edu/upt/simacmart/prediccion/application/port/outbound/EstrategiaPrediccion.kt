/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/prediccion/application/port/outbound/EstrategiaPrediccion.kt
 * Capa / módulo ....: Aplicación · Módulo de Predicción Hídrica (Puerto de Salida)
 * Función ..........: Interfaz del patrón Strategy (GoF) para algoritmos de cálculo de captación
 * Requerimientos ...: RF-08, RF-09, RF-10
 * Calidad (RNF) ....: RNF-02 (Mantenibilidad), RNF-04 (Interoperabilidad ML)
 * Casos de uso .....: CU-11 Generar proyección hídrica
 * Reglas de negocio : RN-08 (Inmutabilidad de predicción generada)
 * Patrones .........: Strategy (GoF - Interfaz de Estrategia)
 * Depende de .......: EntradaPrediccion, Estimacion
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.3
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.prediccion.application.port.outbound

import pe.edu.upt.simacmart.prediccion.domain.model.EntradaPrediccion
import pe.edu.upt.simacmart.prediccion.domain.model.Estimacion

/**
 * Patrón Strategy (GoF - Comportamiento):
 * Define el contrato estándar para cualquier algoritmo de predicción hídrica mensual.
 */
interface EstrategiaPrediccion {
    fun estimar(entrada: EntradaPrediccion): Estimacion
}
