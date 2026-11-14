/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/ingesta/application/port/outbound/PersistenciaPorts.kt
 * Capa / módulo ....: Aplicación · Módulo de Ingesta IoT (Puertos de Persistencia)
 * Función ..........: Contratos de persistencia: Unit of Work y Repositorios segregados
 * Requerimientos ...: RF-01, RF-02, RF-03
 * Calidad (RNF) ....: RNF-01 (Transaccionalidad concurrente), RNF-03 (Tolerancia a fallos)
 * Casos de uso .....: CU-01 Registrar pesaje de balanza, CU-02 Registrar lectura meteorológica
 * Reglas de negocio : RN-02 (Registro atómico de lectura y reporte)
 * Patrones .........: Unit of Work, Repository, Ports & Adapters (Hexagonal - Puertos de Salida)
 * Depende de .......: Lectura, ResultadoIngesta, Instant
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.1 y 4.5
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.ingesta.application.port.outbound

import pe.edu.upt.simacmart.ingesta.domain.model.Lectura
import pe.edu.upt.simacmart.ingesta.domain.model.ResultadoIngesta
import java.time.Instant

/**
 * Patrón Repository: Operaciones de persistencia para la entidad Lectura.
 */
interface LecturaRepository {
    /**
     * Inserta la lectura si no existe previamente por UNIQUE(dispositivoId, claveOrigen).
     * Retorna true si fue insertada, false si ya existía (duplicada).
     */
    fun insertarSiNueva(lectura: Lectura): Boolean
}

/**
 * Patrón Repository: Operaciones de actualización sobre el estado y telemetría de dispositivos.
 */
interface DispositivoRepository {
    /**
     * Actualiza la fecha del último reporte recibido del dispositivo.
     */
    fun registrarReporte(dispositivoId: String, fecha: Instant)
}

/**
 * Patrón Unit of Work: Garantiza transaccionalidad atómica (Commit / Rollback)
 * sobre una misma conexión compartida entre múltiples repositorios.
 */
interface UnitOfWork {
    fun ejecutar(operacion: (LecturaRepository, DispositivoRepository) -> ResultadoIngesta): ResultadoIngesta
}
