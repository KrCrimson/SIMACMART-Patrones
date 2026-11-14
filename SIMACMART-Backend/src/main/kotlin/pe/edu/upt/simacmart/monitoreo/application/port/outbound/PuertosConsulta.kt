/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/monitoreo/application/port/outbound/PuertosConsulta.kt
 * Capa / módulo ....: Aplicación · Puertos de Consulta y Supervisión (Salida)
 * Función ..........: Contratos para consultas optimizadas de dashboard y supervisión de actividad de dispositivos
 * Requerimientos ...: RF-06, RF-07, RF-09
 * Calidad (RNF) ....: RNF-02 (Mantenibilidad), RNF-05 (Rendimiento <= 3s)
 * Casos de uso .....: CU-06 Consultar KPIs del dashboard, CU-07 Visualizar series históricas, CU-09 Monitorear salud de nodos
 * Patrones .........: Ports & Adapters (Hexagonal - Puertos de Salida), Observer (GoF)
 * Depende de .......: ActividadDispositivo, AlertaDispositivo, Variable, EstadoCalculoDTO, PuntoSerieDto, EvaluacionDto
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.4
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.monitoreo.application.port.outbound

import pe.edu.upt.simacmart.monitoreo.domain.model.*

/**
 * Puerto de Salida para consultas agregadas y métricas del Dashboard.
 */
interface ConsultaMonitoreoQuery {
    fun unidadActiva(unidadId: String): Map<String, String>?
    fun captacionDelDia(unidadId: String): Double
    fun ultimasMediciones(unidadId: String): Map<String, Double>
    fun serie(variable: Variable, unidadId: String, dias: Int = 7): List<PuntoSerieDto>
    fun ultimaEvaluacion(unidadId: String): EvaluacionDto?
    fun estadoCalculo(unidadId: String): EstadoCalculoDTO
}

/**
 * Puerto de Salida para consultar el estado y último reporte de cada hardware.
 */
interface ConsultaActividadDispositivos {
    fun consultarActivos(): List<ActividadDispositivo>
    fun consultarPorUnidad(unidadId: String): List<ActividadDispositivo>
}

/**
 * Interfaz Observer (GoF) para recibir alertas de cambio de estado en nodos.
 */
interface ObservadorMonitoreo {
    fun alCambiarEstado(alerta: AlertaDispositivo)
}
