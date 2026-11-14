/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/prediccion/infrastructure/adapter/outbound/JdbcPrediccionRepository.kt
 * Capa / módulo ....: Persistencia / Infraestructura · Módulo de Predicción Hídrica
 * Función ..........: Adaptador JDBC para el almacenamiento y consulta de predicciones mensuales
 * Requerimientos ...: RF-08, RF-10
 * Calidad (RNF) ....: RNF-01 (Persistencia Concurrente), RNF-02 (Mantenibilidad)
 * Casos de uso .....: CU-10 Consultar estimación de captación, CU-11 Generar proyección hídrica
 * Reglas de negocio : RN-08 (Inmutabilidad de predicción generada)
 * Patrones .........: Repository, Adapter (GoF)
 * Depende de .......: PrediccionRepository, PrediccionMensual, DataSource
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.3 y 4.5
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.prediccion.infrastructure.adapter.outbound

import pe.edu.upt.simacmart.prediccion.application.port.outbound.PrediccionRepository
import pe.edu.upt.simacmart.prediccion.domain.model.PrediccionMensual
import pe.edu.upt.simacmart.shared.infrastructure.db.DataSource
import pe.edu.upt.simacmart.shared.infrastructure.db.ResilienteDataSource
import java.math.BigDecimal
import java.time.Instant
import java.time.YearMonth

/**
 * Adaptador JDBC para el repositorio de Predicción según Diagrama UML (Pág 5 y Pág 10).
 */
class JdbcPrediccionRepository(
    private val dataSource: DataSource = ResilienteDataSource
) : PrediccionRepository {

    override fun guardar(prediccion: PrediccionMensual) {
        val sql = "INSERT INTO predicciones (id_unidad, mes_objetivo, litros_estimados, metodo, version_modelo, fecha_generacion) VALUES (?, ?, ?, ?, ?, ?)"
        try {
            dataSource.getConnection().use { conn ->
                conn.prepareStatement(sql).use { pstmt ->
                    pstmt.setInt(1, prediccion.unidadId.toIntOrNull() ?: 1)
                    pstmt.setString(2, prediccion.mesObjetivo.toString())
                    pstmt.setDouble(3, prediccion.litrosEstimados.toDouble())
                    pstmt.setString(4, prediccion.metodo)
                    pstmt.setString(5, prediccion.versionModelo)
                    pstmt.setString(6, prediccion.fechaGeneracion.toString())
                    pstmt.executeUpdate()
                }
            }
        } catch (e: Exception) {
            println("[JdbcPrediccionRepo] Error al guardar prediccion: ${e.message}")
        }
    }

    override fun consultar(unidadId: String, mesObjetivo: YearMonth): PrediccionMensual? {
        val sql = "SELECT id, id_unidad, mes_objetivo, litros_estimados, metodo, version_modelo, fecha_generacion FROM predicciones WHERE id_unidad = ? AND mes_objetivo = ? ORDER BY fecha_generacion DESC LIMIT 1"
        try {
            dataSource.getConnection().use { conn ->
                conn.prepareStatement(sql).use { pstmt ->
                    pstmt.setInt(1, unidadId.toIntOrNull() ?: 1)
                    pstmt.setString(2, mesObjetivo.toString())
                    val rs = pstmt.executeQuery()
                    if (rs.next()) {
                        return PrediccionMensual(
                            id = rs.getString("id") ?: rs.getInt("id").toString(),
                            unidadId = rs.getInt("id_unidad").toString(),
                            mesObjetivo = YearMonth.parse(rs.getString("mes_objetivo")),
                            litrosEstimados = BigDecimal.valueOf(rs.getDouble("litros_estimados")),
                            metodo = rs.getString("metodo") ?: "MODELO_ML",
                            versionModelo = rs.getString("version_modelo") ?: "v1.0",
                            fechaGeneracion = Instant.parse(rs.getString("fecha_generacion"))
                        )
                    }
                }
            }
        } catch (e: Exception) {
            println("[JdbcPrediccionRepo] Error al consultar prediccion: ${e.message}")
        }
        return null
    }
}
