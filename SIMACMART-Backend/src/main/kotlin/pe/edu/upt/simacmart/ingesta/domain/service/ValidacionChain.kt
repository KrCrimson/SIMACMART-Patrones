/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/ingesta/domain/service/ValidacionChain.kt
 * Capa / módulo ....: Dominio · Servicios de Dominio de Ingesta
 * Función ..........: Cadena de responsabilidad para validar campos, coherencia de fechas y rangos físicos
 * Requerimientos ...: RF-01, RF-02
 * Calidad (RNF) ....: RNF-02 (Mantenibilidad), RNF-03 (Tolerancia a fallos)
 * Casos de uso .....: CU-01 Registrar pesaje de balanza, CU-02 Registrar lectura meteorológica
 * Reglas de negocio : RN-01 (Validación estricta de rangos climáticos y pesaje)
 * Patrones .........: Chain of Responsibility (GoF - Comportamiento)
 * Depende de .......: Lectura
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.1
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.ingesta.domain.service

import pe.edu.upt.simacmart.ingesta.domain.model.Lectura
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant

/**
 * Patrón Chain of Responsibility (GoF):
 * Define la interfaz del eslabón de validación y el método para delegar al siguiente.
 */
interface ValidacionHandler {
    fun establecerSiguiente(siguiente: ValidacionHandler)
    fun validar(lectura: Lectura)
}

abstract class BaseValidacionHandler : ValidacionHandler {
    protected var siguiente: ValidacionHandler? = null

    override fun establecerSiguiente(siguiente: ValidacionHandler) {
        this.siguiente = siguiente
    }

    override fun validar(lectura: Lectura) {
        ejecutarValidacion(lectura)
        siguiente?.validar(lectura)
    }

    protected abstract fun ejecutarValidacion(lectura: Lectura)
}

/**
 * Eslabón 1: Valida que los campos indispensables (identificador, origen, mediciones) existan.
 */
class CamposHandler : BaseValidacionHandler() {
    override fun ejecutarValidacion(lectura: Lectura) {
        if (lectura.dispositivoId.isBlank()) {
            lectura.registrarInvalidez("El identificador del dispositivo no puede estar vacío.")
        }
        if (lectura.claveOrigen.isBlank()) {
            lectura.registrarInvalidez("La clave de origen no puede estar vacía.")
        }
        if (lectura.mediciones.isEmpty()) {
            lectura.registrarInvalidez("La lectura no contiene ningún canal de medición.")
        }
    }
}

/**
 * Eslabón 2: Valida coherencia temporal (no futuras, no con desfase mayor a 7 días).
 */
class FechaHandler : BaseValidacionHandler() {
    override fun ejecutarValidacion(lectura: Lectura) {
        val ahora = Instant.now()
        if (lectura.fechaMedicion.isAfter(ahora.plusSeconds(300))) {
            lectura.registrarInvalidez("La fecha de medición es futura en relación al servidor.")
        }
        val desfaseDias = Duration.between(lectura.fechaMedicion, ahora).toDays()
        if (desfaseDias > 7) {
            lectura.registrarInvalidez("La lectura presenta un desfase temporal excesivo (> 7 días).")
        }
    }
}

/**
 * Eslabón 3: Valida rangos físicos admisibles para pesaje (0 a 100 kg) y clima de Tacna/Pachía.
 */
class RangosHandler : BaseValidacionHandler() {
    override fun ejecutarValidacion(lectura: Lectura) {
        // Validación de peso
        lectura.mediciones["pesoKg"]?.let { peso ->
            if (peso < BigDecimal.ZERO || peso > BigDecimal("100.0")) {
                lectura.registrarInvalidez("Peso fuera de rango admisible (0.0 a 100.0 kg): $peso")
            }
        }

        // Validación de temperatura ambiental (-5 a 50 °C)
        lectura.mediciones["temperaturaC"]?.let { temp ->
            if (temp < BigDecimal("-5.0") || temp > BigDecimal("50.0")) {
                lectura.registrarInvalidez("Temperatura fuera de rango (-5.0 a 50.0 °C): $temp")
            }
        }

        // Validación de humedad relativa (0 a 100 %)
        lectura.mediciones["humedadPct"]?.let { hr ->
            if (hr < BigDecimal.ZERO || hr > BigDecimal("100.0")) {
                lectura.registrarInvalidez("Humedad fuera de rango (0 a 100 %): $hr")
            }
        }

        // Validación de velocidad del viento (0 a 45 m/s)
        lectura.mediciones["vientoMs"]?.let { viento ->
            if (viento < BigDecimal.ZERO || viento > BigDecimal("45.0")) {
                lectura.registrarInvalidez("Velocidad de viento fuera de rango (0 a 45 m/s): $viento")
            }
        }
    }
}
