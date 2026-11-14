/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/prediccion/application/service/ServicioPrediccionMensual.kt
 * Capa / módulo ....: Aplicación · Módulo de Predicción Hídrica
 * Función ..........: Contexto del patrón Strategy (GoF) que orquesta la generación de predicciones mensuales
 * Requerimientos ...: RF-08, RF-09, RF-10
 * Calidad (RNF) ....: RNF-02 (Mantenibilidad), RNF-04 (Interoperabilidad ML)
 * Casos de uso .....: CU-10 Consultar estimación de captación, CU-11 Generar proyección hídrica
 * Reglas de negocio : RN-08 (Inmutabilidad de predicción generada), RN-09 (Versionado estricto de modelo ONNX)
 * Patrones .........: Strategy (GoF - Contexto), Ports & Adapters (Hexagonal - Servicio de Aplicación)
 * Depende de .......: EstrategiaPrediccion, PrepararEntradaPrediccion, PrediccionRepository, PrediccionMensual
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.3
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.prediccion.application.service

import pe.edu.upt.simacmart.prediccion.application.port.outbound.EstrategiaPrediccion
import pe.edu.upt.simacmart.prediccion.application.port.outbound.PrediccionRepository
import pe.edu.upt.simacmart.prediccion.application.port.outbound.PrepararEntradaPrediccion
import pe.edu.upt.simacmart.prediccion.domain.model.PrediccionMensual
import java.time.Instant
import java.time.YearMonth

/**
 * Contexto del Patrón Strategy (GoF - Comportamiento):
 * Orquesta la recolección de entradas climáticas, delega el cálculo a la estrategia
 * inyectada (ML o Promedio Histórico) y persiste el resultado inmutable.
 */
class ServicioPrediccionMensual(
    private var estrategia: EstrategiaPrediccion,
    private val preparadorEntrada: PrepararEntradaPrediccion,
    private val prediccionRepository: PrediccionRepository
) {

    /**
     * Genera y persiste una predicción mensual para la unidad especificada.
     */
    fun generar(unidadId: String, mesObjetivo: YearMonth): PrediccionMensual {
        val entrada = preparadorEntrada.obtener(unidadId, mesObjetivo)
        val estimacion = estrategia.estimar(entrada)

        val prediccion = PrediccionMensual(
            unidadId = unidadId,
            mesObjetivo = mesObjetivo,
            litrosEstimados = estimacion.litros,
            metodo = estimacion.metodo,
            versionModelo = estimacion.versionModelo,
            fechaGeneracion = Instant.now()
        )

        prediccionRepository.guardar(prediccion)
        return prediccion
    }

    /**
     * Permite conmutar la estrategia de predicción en tiempo de ejecución (OCP / Strategy).
     */
    fun cambiarEstrategia(nuevaEstrategia: EstrategiaPrediccion) {
        this.estrategia = nuevaEstrategia
    }
}
