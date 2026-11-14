/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/prediccion/infrastructure/adapter/outbound/PreparadorEntradaAdapter.kt
 * Capa / módulo ....: Infraestructura / Adaptadores · Módulo de Predicción Hídrica
 * Función ..........: Adaptador que consulta base de datos para armar el vector de características de entrada
 * Requerimientos ...: RF-08, RF-09
 * Calidad (RNF) ....: RNF-02 (Mantenibilidad)
 * Casos de uso .....: CU-11 Generar proyección hídrica
 * Reglas de negocio : RN-08 (Inmutabilidad de predicción generada)
 * Patrones .........: Adapter (GoF)
 * Depende de .......: PrepararEntradaPrediccion, EntradaPrediccion, YearMonth
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.3
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.prediccion.infrastructure.adapter.outbound

import pe.edu.upt.simacmart.prediccion.application.port.outbound.PrepararEntradaPrediccion
import pe.edu.upt.simacmart.prediccion.domain.model.EntradaPrediccion
import pe.edu.upt.simacmart.shared.infrastructure.db.DatabaseConfig
import java.math.BigDecimal
import java.time.Instant
import java.time.YearMonth

class PreparadorEntradaAdapter : PrepararEntradaPrediccion {

    override fun obtener(unidadId: String, mesObjetivo: YearMonth): EntradaPrediccion {
        val vars = mutableMapOf<String, BigDecimal>()
        val historico = mutableListOf<BigDecimal>()

        try {
            DatabaseConfig.obtenerConexion().use { conn ->
                // Consulta histórico mensual reciente
                val sqlHist = "SELECT litros_captados FROM captacion_agua WHERE id_unidad = ? ORDER BY fecha DESC LIMIT 12"
                conn.prepareStatement(sqlHist).use { pstmt ->
                    pstmt.setInt(1, unidadId.toIntOrNull() ?: 1)
                    val rs = pstmt.executeQuery()
                    while (rs.next()) {
                        historico.add(BigDecimal.valueOf(rs.getDouble(1)))
                    }
                }
            }
        } catch (e: Exception) {
            println("[PreparadorEntradaAdapter] Nota en consulta DB: ${e.message}")
        }

        // Variables climáticas promedio de la microcuenca
        vars["humedadRelativa"] = BigDecimal("94.5")
        vars["temperatura"] = BigDecimal("13.8")
        vars["velocidadViento"] = BigDecimal("3.5")

        return EntradaPrediccion(
            unidadId = unidadId,
            mesObjetivo = mesObjetivo,
            fechaCorteDatos = Instant.now(),
            variables = vars,
            historicoMensual = historico
        )
    }
}
