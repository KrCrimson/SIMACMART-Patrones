package pe.edu.upt.simacmart.shared.domain

data class Resultado<T>(
    val exito: Boolean,
    val mensaje: String,
    val datos: T? = null
)
