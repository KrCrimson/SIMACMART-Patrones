/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/monitoreo/application/port/inbound/PuertosEntrada.kt
 * Capa / módulo ....: Aplicación · Puertos de Entrada de Consulta y Supervisión
 * Función ..........: Contratos de entrada para renderizado del Dashboard y ejecución periódica de supervisión
 * Requerimientos ...: RF-06, RF-07
 * Calidad (RNF) ....: RNF-02 (Mantenibilidad)
 * Casos de uso .....: CU-06 Consultar KPIs del dashboard, CU-07 Visualizar series históricas, CU-09 Monitorear salud de nodos
 * Patrones .........: Ports & Adapters (Hexagonal - Puertos de Entrada)
 * Depende de .......: ResumenDiaDTO, HistorialDTO, CompararDTO, ActividadDispositivo, Variable
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.4
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.monitoreo.application.port.inbound

import pe.edu.upt.simacmart.monitoreo.domain.model.*
import java.time.YearMonth

interface ConsultarMonitoreo {
    fun resumenDia(unidadId: String = "1"): ResumenDiaDTO
    fun historial(unidadId: String = "1", variable: Variable = Variable.PESO, dias: Int = 7): HistorialDTO
    fun comparar(unidadId: String = "1", mes: YearMonth = YearMonth.now()): CompararDTO
    fun estadoDispositivos(unidadId: String = "1"): List<ActividadDispositivo>
}

interface SupervisarDispositivos {
    fun verificar()
}
