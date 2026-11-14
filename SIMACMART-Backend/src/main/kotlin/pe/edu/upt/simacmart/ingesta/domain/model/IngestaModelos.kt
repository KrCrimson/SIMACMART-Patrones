/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/ingesta/domain/model/IngestaModelos.kt
 * Capa / módulo ....: Dominio · Módulo de Ingesta IoT
 * Función ..........: Entidad Dispositivo y tipos de nodos de telemetría de campo
 * Requerimientos ...: RF-01, RF-02
 * Calidad (RNF) ....: RNF-08 (Seguridad / Identidad de dispositivos)
 * Casos de uso .....: CU-01 Registrar pesaje de balanza, CU-02 Registrar lectura meteorológica
 * Reglas de negocio : RN-02 (Identificación de nodos)
 * Patrones .........: Domain Entity
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART)
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.ingesta.domain.model

enum class TipoDispositivo { ESTACION, BALANZA }

/**
 * Entidad Dispositivo para control y auditoría de nodos de telemetría.
 */
data class Dispositivo(
    val id: String,
    val unidadId: String = "1",
    val activo: Boolean = true,
    val codigo: String = id,
    val tipo: TipoDispositivo = TipoDispositivo.BALANZA,
    val apiKeyHash: String = ""
) {
    val idDispositivo: Int get() = id.toIntOrNull() ?: 1
    val idUnidad: Int get() = unidadId.toIntOrNull() ?: 1

    constructor(
        idDispositivo: Int,
        idUnidad: Int,
        codigo: String,
        tipo: TipoDispositivo,
        apiKeyHash: String,
        activo: Boolean = true
    ) : this(
        id = idDispositivo.toString(),
        unidadId = idUnidad.toString(),
        activo = activo,
        codigo = codigo,
        tipo = tipo,
        apiKeyHash = apiKeyHash
    )
}
