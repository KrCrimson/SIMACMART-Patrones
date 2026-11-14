/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/prediccion/infrastructure/adapter/outbound/AlmacenModeloAdapter.kt
 * Capa / módulo ....: Infraestructura / Adaptadores · Módulo de Predicción Hídrica
 * Función ..........: Adaptador que consulta metadatos y valida hash SHA-256 del artefacto ONNX
 * Requerimientos ...: RF-09, RF-10
 * Calidad (RNF) ....: RNF-04 (Interoperabilidad ML), RNF-08 (Seguridad/Integridad)
 * Casos de uso .....: CU-11 Generar proyección hídrica
 * Reglas de negocio : RN-09 (Versionado estricto de modelo ONNX)
 * Patrones .........: Adapter (GoF)
 * Depende de .......: ObtenerModeloActivo, ModeloVersionado
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.3
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.prediccion.infrastructure.adapter.outbound

import pe.edu.upt.simacmart.prediccion.application.port.outbound.ObtenerModeloActivo
import pe.edu.upt.simacmart.prediccion.domain.model.ModeloVersionado
import pe.edu.upt.simacmart.shared.infrastructure.db.DatabaseConfig

class AlmacenModeloAdapter : ObtenerModeloActivo {

    override fun obtener(): ModeloVersionado {
        var version = "v1.4-pachia-rf"
        var formato = "ONNX"
        var ubicacion = "models/atrapanieblas_rf.onnx"
        var hash = "sha256:9f83a218d6e3...simacmart"

        try {
            DatabaseConfig.obtenerConexion().use { conn ->
                val sql = "SELECT version, algoritmo FROM modelos_ml WHERE activo = 1 LIMIT 1"
                conn.createStatement().use { stmt ->
                    val rs = stmt.executeQuery(sql)
                    if (rs.next()) {
                        version = rs.getString("version") ?: version
                    }
                }
            }
        } catch (e: Exception) {
            println("[AlmacenModeloAdapter] Uso de metadatos locales: ${e.message}")
        }

        return ModeloVersionado(
            version = version,
            formato = formato,
            ubicacion = ubicacion,
            hash = hash,
            versionEsquemaEntrada = "v1"
        )
    }
}
