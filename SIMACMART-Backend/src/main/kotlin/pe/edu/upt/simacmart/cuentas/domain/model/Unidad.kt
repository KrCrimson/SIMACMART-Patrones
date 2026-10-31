package pe.edu.upt.simacmart.cuentas.domain.model

import java.math.BigDecimal

/**
 * Entidad de dominio: Unidad Atrapanieblas.
 * Nota Ubicua: En el lenguaje de campo y red IoT se denomina "Nodo" (UC-15: Agregar nodo, UC-16: Quitar nodo).
 */
data class Unidad(
    val idUnidad: Int = 0,
    val nombre: String,
    val localidad: String,
    val latitud: BigDecimal,
    val longitud: BigDecimal,
    val capacidadTinaKg: BigDecimal? = null
)
