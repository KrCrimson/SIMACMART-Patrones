package pe.edu.upt.simacmart.shared.domain

/**
 * Resultado de operacion en Repositorio (Pags 4-6 y Pag 17 del UML).
 */
sealed interface ResultadoGuardado {
    val identificadorLectura: String

    data class Insertada(
        override val identificadorLectura: String
    ) : ResultadoGuardado

    data class YaExistia(
        override val identificadorLectura: String
    ) : ResultadoGuardado
}

