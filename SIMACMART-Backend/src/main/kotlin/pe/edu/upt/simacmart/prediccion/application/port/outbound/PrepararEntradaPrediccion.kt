/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/prediccion/application/port/outbound/PrepararEntradaPrediccion.kt
 * Capa / módulo ....: Aplicación · Módulo de Predicción Hídrica (Puerto de Salida)
 * Función ..........: Contrato para recolectar variables meteorológicas y series históricas
 * Requerimientos ...: RF-08, RF-09
 * Calidad (RNF) ....: RNF-02 (Mantenibilidad)
 * Casos de uso .....: CU-11 Generar proyección hídrica
 * Reglas de negocio : RN-08
 * Patrones .........: Ports & Adapters (Hexagonal - Puerto de Salida)
 * Depende de .......: EntradaPrediccion, YearMonth
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.3
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.prediccion.application.port.outbound

import pe.edu.upt.simacmart.prediccion.domain.model.EntradaPrediccion
import java.time.YearMonth

interface PrepararEntradaPrediccion {
    fun obtener(unidadId: String, mesObjetivo: YearMonth): EntradaPrediccion
}
