package pe.edu.upt.simacmart.prediccion.application.service

import pe.edu.upt.simacmart.prediccion.application.port.inbound.GenerarPrediccionUseCase
import pe.edu.upt.simacmart.prediccion.application.port.outbound.EstrategiaPrediccionPort
import pe.edu.upt.simacmart.prediccion.domain.model.EstimacionHidricaResponse
import pe.edu.upt.simacmart.prediccion.domain.model.ModeloMLInfo
import pe.edu.upt.simacmart.prediccion.infrastructure.adapter.outbound.PrediccionConModeloMLStrategy
import pe.edu.upt.simacmart.prediccion.infrastructure.adapter.outbound.PromedioHistoricoStrategy
import pe.edu.upt.simacmart.shared.infrastructure.db.DatabaseConfig

/**
 * Contexto del Patron Strategy (GoF):
 * Mantiene la estrategia activa y permite conmutar algoritmos en tiempo de ejecucion.
 */
class ServicioPrediccion(
    private var estrategiaActiva: EstrategiaPrediccionPort
) : GenerarPrediccionUseCase {

    private val estrategias = mapOf(
        "ML" to PrediccionConModeloMLStrategy(),
        "HISTORICO" to PromedioHistoricoStrategy()
    )

    fun cambiarEstrategia(codigo: String) {
        val nueva = estrategias[codigo.uppercase()]
        if (nueva != null) {
            this.estrategiaActiva = nueva
            println("[ServicioPrediccion] Estrategia conmutada a: ${nueva.nombreEstrategia}")
        }
    }

    override fun estimarConEstrategia(unidadId: Int, codigoEstrategia: String): EstimacionHidricaResponse {
        val estrategia = estrategias[codigoEstrategia.uppercase()] ?: estrategiaActiva
        return estrategia.estimar(unidadId, 7)
    }

    override fun listarModelosDisponibles(): List<ModeloMLInfo> {
        val lista = mutableListOf<ModeloMLInfo>()
        try {
            DatabaseConfig.obtenerConexion().use { conn ->
                val sql = "SELECT id_modelo, algoritmo, version, error_medio_litros, activo FROM modelos_ml"
                conn.createStatement().use { stmt ->
                    val rs = stmt.executeQuery(sql)
                    while (rs.next()) {
                        lista.add(
                            ModeloMLInfo(
                                idModelo = rs.getInt("id_modelo"),
                                algoritmo = rs.getString("algoritmo"),
                                version = rs.getString("version"),
                                errorMedioLitros = rs.getDouble("error_medio_litros"),
                                activo = rs.getBoolean("activo")
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            println("[ServicioPrediccion] Nota al listar modelos: ${e.message}")
        }
        return lista
    }
}
