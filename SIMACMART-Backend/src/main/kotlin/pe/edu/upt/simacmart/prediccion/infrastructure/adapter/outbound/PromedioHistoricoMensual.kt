/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/prediccion/infrastructure/adapter/outbound/PromedioHistoricoMensual.kt
 * Capa / módulo ....: Infraestructura / Adaptadores · Módulo de Predicción Hídrica
 * Función ..........: Estrategia concreta de línea base estadística basada en promedio histórico
 * Requerimientos ...: RF-08
 * Calidad (RNF) ....: RNF-02 (Mantenibilidad)
 * Casos de uso .....: CU-11 Generar proyección hídrica
 * Reglas de negocio : RN-08 (Inmutabilidad de predicción generada)
 * Patrones .........: Strategy (GoF - ConcreteStrategy)
 * Depende de .......: EstrategiaPrediccion, EntradaPrediccion, Estimacion
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.3
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.prediccion.infrastructure.adapter.outbound

import pe.edu.upt.simacmart.prediccion.application.port.outbound.EstrategiaPrediccion
import pe.edu.upt.simacmart.prediccion.domain.model.EntradaPrediccion
import pe.edu.upt.simacmart.prediccion.domain.model.Estimacion
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * ConcreteStrategy: Algoritmo estadístico de respaldo y línea base.
 */
class PromedioHistoricoMensual : EstrategiaPrediccion {

    override fun estimar(entrada: EntradaPrediccion): Estimacion {
        val promedio = if (entrada.historicoMensual.isNotEmpty()) {
            val suma = entrada.historicoMensual.fold(BigDecimal.ZERO) { acc, v -> acc.add(v) }
            suma.divide(BigDecimal.valueOf(entrada.historicoMensual.size.toLong()), 2, RoundingMode.HALF_UP)
        } else {
            BigDecimal("38.50")
        }

        return Estimacion(
            litros = promedio,
            metodo = "PROMEDIO_HISTORICO",
            versionModelo = "LINEA_BASE_ESTACIONAL"
        )
    }
}
