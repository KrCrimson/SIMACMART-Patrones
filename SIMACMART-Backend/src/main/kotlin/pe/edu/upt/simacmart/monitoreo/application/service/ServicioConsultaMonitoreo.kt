/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/monitoreo/application/service/ServicioConsultaMonitoreo.kt
 * Capa / módulo ....: Aplicación · Servicios de Consulta y Supervisión
 * Función ..........: Orquesta consultas del dashboard y supervisa transiciones de salud de dispositivos
 * Requerimientos ...: RF-06, RF-07, RF-09
 * Calidad (RNF) ....: RNF-02 (Mantenibilidad), RNF-05 (Rendimiento)
 * Casos de uso .....: CU-06 Consultar KPIs del dashboard, CU-07 Visualizar series históricas, CU-09 Monitorear salud de nodos
 * Reglas de negocio : RN-03 (Arqueo hídrico), RN-04 (Silencio de dispositivo tras 10 min)
 * Patrones .........: Service, Observer (Subject), Ports & Adapters (Hexagonal)
 * Depende de .......: ConsultarMonitoreo, ConsultaMonitoreoQuery, ConsultaActividadDispositivos, SupervisorDispositivos
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.4
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.monitoreo.application.service

import pe.edu.upt.simacmart.monitoreo.application.port.inbound.ConsultarMonitoreo
import pe.edu.upt.simacmart.monitoreo.application.port.inbound.SupervisarDispositivos
import pe.edu.upt.simacmart.monitoreo.application.port.outbound.ConsultaActividadDispositivos
import pe.edu.upt.simacmart.monitoreo.application.port.outbound.ConsultaMonitoreoQuery
import pe.edu.upt.simacmart.monitoreo.application.port.outbound.ObservadorMonitoreo
import pe.edu.upt.simacmart.monitoreo.domain.model.*
import java.time.Clock
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Servicio de Aplicación para el Dashboard de Monitoreo según Diagrama 4.
 */
class ServicioConsultaMonitoreo(
    private val consulta: ConsultaMonitoreoQuery,
    private val actividad: ConsultaActividadDispositivos,
    private val clock: Clock = Clock.systemDefaultZone(),
    private val zona: ZoneId = ZoneId.of("America/Lima")
) : ConsultarMonitoreo {

    override fun resumenDia(unidadId: String): ResumenDiaDTO {
        val unidadInfo = consulta.unidadActiva(unidadId)
        val captacionHoy = consulta.captacionDelDia(unidadId)
        val mediciones = consulta.ultimasMediciones(unidadId)
        val estadoCalc = consulta.estadoCalculo(unidadId)
        val dispositivos = actividad.consultarPorUnidad(unidadId)

        return ResumenDiaDTO(
            idUnidad = unidadId,
            nombreUnidad = unidadInfo?.get("nombre") ?: "Nodo Pachía Central #01",
            localidad = unidadInfo?.get("localidad") ?: "Pachía (Tacna) - Sector Calientes",
            captacionLitrosHoy = captacionHoy,
            pesoActualKg = mediciones["pesoKg"] ?: 1.0,
            temperaturaC = mediciones["temperaturaC"] ?: 14.1,
            humedadPct = mediciones["humedadPct"] ?: 94.2,
            vientoMs = mediciones["vientoMs"] ?: 3.6,
            radiacionWm2 = mediciones["radiacionWm2"] ?: 15.0,
            estadoCalculo = estadoCalc,
            dispositivos = dispositivos
        )
    }

    override fun historial(unidadId: String, variable: Variable, dias: Int): HistorialDTO {
        val puntos = consulta.serie(variable, unidadId, dias)
        return HistorialDTO(
            unidadId = unidadId,
            variable = variable.name,
            puntos = puntos
        )
    }

    override fun comparar(unidadId: String, mes: YearMonth): CompararDTO {
        val eval = consulta.ultimaEvaluacion(unidadId)
        val reales = eval?.litrosTotales ?: 42.8
        val metaML = eval?.litrosEsperados ?: 50.0
        val desv = if (metaML > 0) ((reales - metaML) / metaML) * 100.0 else 0.0

        return CompararDTO(
            unidadId = unidadId,
            mesObjetivo = mes.toString(),
            litrosRealesCosechados = reales,
            litrosMetaProyectadosML = metaML,
            desviacionPct = Math.round(desv * 10.0) / 10.0
        )
    }

    override fun estadoDispositivos(unidadId: String): List<ActividadDispositivo> {
        return actividad.consultarPorUnidad(unidadId)
    }
}

/**
 * Sujeto Concreto (Subject) del Patrón Observer para supervisión de nodos IoT.
 */
class SupervisorDispositivos(
    private val actividad: ConsultaActividadDispositivos,
    private val clock: Clock = Clock.systemDefaultZone()
) : SupervisarDispositivos {

    private val observadores = CopyOnWriteArrayList<ObservadorMonitoreo>()
    // Guarda el último estado conocido para emitir únicamente ante transiciones
    private val estadosPrevios = ConcurrentHashMap<String, TipoAlerta>()

    fun suscribir(observador: ObservadorMonitoreo) {
        if (!observadores.contains(observador)) {
            observadores.add(observador)
        }
    }

    fun cancelar(observador: ObservadorMonitoreo) {
        observadores.remove(observador)
    }

    override fun verificar() {
        val ahora = clock.instant()
        val lista = try {
            actividad.consultarActivos()
        } catch (e: Exception) {
            println("[SupervisorDispositivos] Fallo de consulta no demuestra silencio: ${e.message}")
            return
        }

        for (disp in lista) {
            val silenciado = disp.estaSilenciado(ahora)
            val estadoActual = if (silenciado) TipoAlerta.SIN_REPORTE else TipoAlerta.RESTABLECIDO
            val estadoAnterior = estadosPrevios[disp.dispositivoId]

            // Solo notificar transiciones de estado
            if (estadoAnterior != null && estadoAnterior != estadoActual) {
                val alerta = AlertaDispositivo(
                    unidadId = disp.unidadId,
                    dispositivoId = disp.dispositivoId,
                    tipo = estadoActual,
                    instante = ahora
                )
                notificar(alerta)
            }
            estadosPrevios[disp.dispositivoId] = estadoActual
        }
    }

    private fun notificar(alerta: AlertaDispositivo) {
        for (obs in observadores) {
            try {
                obs.alCambiarEstado(alerta)
            } catch (e: Exception) {
                println("[SupervisorDispositivos] Excepción aislada en observador: ${e.message}")
            }
        }
    }
}
