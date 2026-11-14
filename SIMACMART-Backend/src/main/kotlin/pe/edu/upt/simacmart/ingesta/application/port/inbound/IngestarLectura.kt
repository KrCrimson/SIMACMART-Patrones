/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/ingesta/application/port/inbound/IngestarLectura.kt
 * Capa / módulo ....: Aplicación · Módulo de Ingesta IoT (Puerto de Entrada)
 * Función ..........: Contrato principal de entrada para la ingesta de telemetría de balanza y clima
 * Requerimientos ...: RF-01, RF-02
 * Calidad (RNF) ....: RNF-01 (Concurrencia), RNF-03 (Tolerancia a fallos)
 * Casos de uso .....: CU-01 Registrar pesaje de balanza, CU-02 Registrar lectura meteorológica
 * Reglas de negocio : RN-01, RN-02
 * Patrones .........: Ports & Adapters (Hexagonal - Puerto de Entrada)
 * Depende de .......: DatosLectura, ResultadoIngesta
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.1
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.ingesta.application.port.inbound

import pe.edu.upt.simacmart.ingesta.domain.model.DatosLectura
import pe.edu.upt.simacmart.ingesta.domain.model.ResultadoIngesta

interface IngestarLectura {
    fun ejecutar(datos: DatosLectura, credencial: String): ResultadoIngesta
}
