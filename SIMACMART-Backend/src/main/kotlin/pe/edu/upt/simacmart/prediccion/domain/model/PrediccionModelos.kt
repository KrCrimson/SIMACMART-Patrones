/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/prediccion/domain/model/PrediccionModelos.kt
 * Capa / módulo ....: Dominio · Módulo de Predicción Hídrica
 * Función ..........: Entidades y objetos de valor para estimación y proyección de captación hídrica
 * Requerimientos ...: RF-08, RF-09, RF-10
 * Calidad (RNF) ....: RNF-02 (Mantenibilidad), RNF-04 (Interoperabilidad ML)
 * Casos de uso .....: CU-10 Consultar estimación de captación, CU-11 Generar proyección hídrica
 * Reglas de negocio : RN-08 (Inmutabilidad de predicción generada), RN-09 (Versionado estricto de modelo ONNX)
 * Depende de .......: Java Time API, Java Math BigDecimal
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.3
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.prediccion.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.time.YearMonth
import java.util.UUID

/**
 * Alias de tipo para vector de variables meteorológicas y orográficas.
 */
typealias VectorVariables = Map<String, BigDecimal>

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

/**
 * Entidad de Dominio según Diagrama UML de Predicción (Hernández y Arce).
 */
data class PrediccionMensual(
    val id: String = UUID.randomUUID().toString(),
    val unidadId: String = "1",
    val mesObjetivo: YearMonth = YearMonth.now().plusMonths(1),
    val litrosEstimados: BigDecimal = BigDecimal.ZERO,
    val metodo: String = "MODELO_ML",
    val versionModelo: String = "v1.0.0-ONNX",
    val fechaGeneracion: Instant = Instant.now()
)

/**
 * Snapshot de características de entrada para alimentar los algoritmos predictivos.
 */
data class EntradaPrediccion(
    val unidadId: String,
    val mesObjetivo: YearMonth,
    val fechaCorteDatos: Instant = Instant.now(),
    val variables: VectorVariables = emptyMap(),
    val historicoMensual: List<BigDecimal> = emptyList()
)

/**
 * Metadatos y firma de seguridad del artefacto de Machine Learning serializado.
 */
data class ModeloVersionado(
    val version: String,
    val formato: String = "ONNX",
    val ubicacion: String = "/app/models/atrapanieblas_rf.onnx",
    val hash: String = "sha256:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
    val versionEsquemaEntrada: String = "v1"
)

/**
 * Objeto de valor que encapsula la inferencia bruta producida por una estrategia.
 */
data class Estimacion(
    val litros: BigDecimal,
    val metodo: String,
    val versionModelo: String
)
