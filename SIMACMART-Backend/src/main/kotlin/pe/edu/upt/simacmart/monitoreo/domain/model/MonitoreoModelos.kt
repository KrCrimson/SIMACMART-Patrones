/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/monitoreo/domain/model/MonitoreoModelos.kt
 * Capa / módulo ....: Dominio · Módulo de Monitoreo y Dashboard
 * Función ..........: Entidades ActividadDispositivo, AlertaDispositivo y DTOs de cálculo y series temporales
 * Requerimientos ...: RF-06, RF-07
 * Calidad (RNF) ....: RNF-02 (Mantenibilidad), RNF-05 (Tiempo de respuesta <= 3s)
 * Casos de uso .....: CU-06 Consultar KPIs del dashboard, CU-07 Visualizar series históricas, CU-09 Monitorear salud de nodos
 * Reglas de negocio : RN-04 (Silencio de dispositivo tras 2 intervalos esperados = 10 min)
 * Patrones .........: Domain Entity, Value Object
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.4
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.monitoreo.domain.model

import java.time.Duration
import java.time.Instant

enum class TipoAlerta { SIN_REPORTE, RESTABLECIDO }

enum class Variable { PESO, TEMPERATURA, HUMEDAD, VIENTO, RADIACION, LLUVIA }

/**
 * Entidad de Dominio que modela la actividad y salud de un nodo de campo.
 */
data class ActividadDispositivo(
    val dispositivoId: String,
    val unidadId: String,
    val activadoEn: Instant,
    val ultimoReporte: Instant?,
    val ultimaMedicion: Instant?,
    val intervaloEsperado: Duration = Duration.ofMinutes(5)
) {
    /**
     * Plazo máximo antes de considerar silencio (2 x intervalo = 10 minutos).
     */
    fun plazoVencimiento(): Instant {
        val base = ultimoReporte ?: activadoEn
        return base.plus(intervaloEsperado.multipliedBy(2))
    }

    /**
     * Retorna true si ha superado el plazo de vencimiento sin reportar.
     */
    fun estaSilenciado(ahora: Instant = Instant.now()): Boolean {
        return ahora.isAfter(plazoVencimiento())
    }
}

/**
 * Evento de alerta de salud de nodo emitido por el Supervisor.
 */
data class AlertaDispositivo(
    val unidadId: String,
    val dispositivoId: String,
    val tipo: TipoAlerta,
    val instante: Instant = Instant.now()
)

data class EstadoCalculoDTO(
    val unidadId: String,
    val ultimaCaptacionCalculada: Instant?,
    val pendienteMasAntiguo: Instant?,
    val estado: String = "AL_DIA"
)

data class PuntoSerieDto(
    val fechaHora: String,
    val valor: Double
)

data class EvaluacionDto(
    val unidadId: String,
    val litrosTotales: Double,
    val litrosEsperados: Double,
    val rendimientoPct: Double,
    val fechaCalculo: Instant = Instant.now()
)

data class ResumenDiaDTO(
    val idUnidad: String,
    val nombreUnidad: String,
    val localidad: String,
    val captacionLitrosHoy: Double,
    val pesoActualKg: Double,
    val temperaturaC: Double,
    val humedadPct: Double,
    val vientoMs: Double,
    val radiacionWm2: Double,
    val estadoCalculo: EstadoCalculoDTO,
    val dispositivos: List<ActividadDispositivo>
)

data class HistorialDTO(
    val unidadId: String,
    val variable: String,
    val puntos: List<PuntoSerieDto>
)

data class CompararDTO(
    val unidadId: String,
    val mesObjetivo: String,
    val litrosRealesCosechados: Double,
    val litrosMetaProyectadosML: Double,
    val desviacionPct: Double
)
