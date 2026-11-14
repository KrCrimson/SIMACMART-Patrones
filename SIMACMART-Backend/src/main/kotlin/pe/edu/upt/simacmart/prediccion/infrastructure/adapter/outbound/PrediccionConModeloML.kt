/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/prediccion/infrastructure/adapter/outbound/PrediccionConModeloML.kt
 * Capa / módulo ....: Infraestructura / Adaptadores · Módulo de Predicción Hídrica
 * Función ..........: Estrategia concreta que ejecuta inferencia de Machine Learning con ONNX
 * Requerimientos ...: RF-09, RF-10
 * Calidad (RNF) ....: RNF-04 (Interoperabilidad ML), RNF-02 (Mantenibilidad)
 * Casos de uso .....: CU-11 Generar proyección hídrica
 * Reglas de negocio : RN-09 (Versionado estricto de modelo ONNX)
 * Patrones .........: Strategy (GoF - ConcreteStrategy), Adapter (GoF)
 * Depende de .......: ObtenerModeloActivo, EvaluarModelo, EntradaPrediccion, Estimacion
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.3
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.prediccion.infrastructure.adapter.outbound

import pe.edu.upt.simacmart.prediccion.application.port.outbound.EstrategiaPrediccion
import pe.edu.upt.simacmart.prediccion.application.port.outbound.EvaluarModelo
import pe.edu.upt.simacmart.prediccion.application.port.outbound.ObtenerModeloActivo
import pe.edu.upt.simacmart.prediccion.domain.model.EntradaPrediccion
import pe.edu.upt.simacmart.prediccion.domain.model.Estimacion

/**
 * ConcreteStrategy: Realiza la estimación consumiendo los puertos de salida
 * para desacoplar el motor de ML del núcleo de la aplicación.
 */
class PrediccionConModeloML(
    private val almacenModelo: ObtenerModeloActivo,
    private val evaluadorArtefacto: EvaluarModelo
) : EstrategiaPrediccion {

    override fun estimar(entrada: EntradaPrediccion): Estimacion {
        val modelo = almacenModelo.obtener()
        val litros = evaluadorArtefacto.ejecutar(modelo, entrada.variables)

        return Estimacion(
            litros = litros,
            metodo = "MODELO_ML",
            versionModelo = modelo.version
        )
    }
}
