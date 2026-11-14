/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/cuentas/application/port/inbound/AutenticarSesion.kt
 * Capa / módulo ....: Aplicación · Módulo de Cuentas (Puerto de Entrada)
 * Función ..........: Contrato para validar vigencia de tokens/sesiones de usuario desde otros módulos (Dashboard)
 * Requerimientos ...: RF-11, RF-12
 * Calidad (RNF) ....: RNF-08 (Seguridad / Control de Acceso)
 * Casos de uso .....: CU-13 Iniciar sesión, CU-14 Validar sesión activa
 * Patrones .........: Ports & Adapters (Hexagonal - Puerto de Entrada)
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.4
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.cuentas.application.port.inbound

interface AutenticarSesion {
    /**
     * Verifica si una credencial de sesión / JWT o token es válida y no ha expirado.
     */
    fun sesionVigente(sesion: String): Boolean
}
