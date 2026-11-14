/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/ingesta/application/port/transport/TransporteMensajes.kt
 * Capa / módulo ....: Aplicación · Transporte y Mensajería IoT
 * Función ..........: Contratos del patrón Observer para desacoplar el broker MQTT del flujo de aplicación
 * Requerimientos ...: RF-01, RF-02
 * Calidad (RNF) ....: RNF-03 (Desacoplamiento y disponibilidad)
 * Casos de uso .....: CU-01 Registrar pesaje de balanza
 * Reglas de negocio : RN-02
 * Patrones .........: Observer (GoF - Comportamiento)
 * Depende de .......: ByteArray, String
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.1
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.ingesta.application.port.transport

interface ObservadorMensaje {
    fun alRecibir(tema: String, contenido: ByteArray)
}

interface TransporteMensajes {
    fun suscribir(tema: String, observador: ObservadorMensaje)
    fun publicar(tema: String, contenido: ByteArray)
}
