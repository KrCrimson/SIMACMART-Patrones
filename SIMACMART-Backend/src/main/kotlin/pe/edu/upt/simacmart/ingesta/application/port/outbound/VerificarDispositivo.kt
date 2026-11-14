/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/ingesta/application/port/outbound/VerificarDispositivo.kt
 * Capa / módulo ....: Aplicación · Módulo de Ingesta IoT (Puerto de Salida)
 * Función ..........: Contrato para validar credenciales y obtener el ID interno del dispositivo
 * Requerimientos ...: RF-01, RF-02
 * Calidad (RNF) ....: RNF-08 (Seguridad / Autenticación)
 * Casos de uso .....: CU-01 Registrar pesaje de balanza, CU-02 Registrar lectura meteorológica
 * Reglas de negocio : RN-02 (Autenticación de nodos de campo)
 * Patrones .........: Ports & Adapters (Hexagonal - Puerto de Salida)
 * Depende de .......: String
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.1
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.ingesta.application.port.outbound

interface VerificarDispositivo {
    /**
     * Valida la credencial del dispositivo externo.
     * Retorna el identificador interno si es válido, o lanza excepción / retorna vacío si falla.
     */
    fun autorizar(dispositivoExterno: String, credencial: String): String
}
