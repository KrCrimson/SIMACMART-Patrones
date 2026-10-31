package pe.edu.upt.simacmart.prediccion.domain.model

import java.time.Instant

data class ProyeccionDiariaDto(
    val dia: String,
    val fecha: String,
    val litrosEstimados: Double,
    val tempMediaC: Double,
    val humedadMediaPct: Double,
    val probabilidadNieblaPct: Double
)

data class EstimacionHidricaResponse(
    val idUnidad: Int,
    val nombreUnidad: String,
    val codigoEstrategia: String,
    val nombreEstrategia: String,
    val litrosTotales48h: Double,
    val litrosTotales7Dias: Double,
    val confianzaPct: Double,
    val proyeccion: List<ProyeccionDiariaDto>,
    val recomendacionUso: String,
    val generadoEn: Instant = Instant.now()
)

data class ModeloMLInfo(
    val idModelo: Int,
    val algoritmo: String,
    val version: String,
    val errorMedioLitros: Double,
    val activo: Boolean
)
