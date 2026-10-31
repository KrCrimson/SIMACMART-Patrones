package pe.edu.upt.simacmart.cuentas.domain.model

import java.time.Instant

data class Cuenta(
    val idCuenta: Int = 0,
    val usuario: String,
    val nombreCompleto: String,
    val correo: String = "",
    val passwordHash: String,
    val rol: Rol = Rol.OPERADOR,
    val activo: Boolean = true,
    val creadoEn: Instant = Instant.now()
)
