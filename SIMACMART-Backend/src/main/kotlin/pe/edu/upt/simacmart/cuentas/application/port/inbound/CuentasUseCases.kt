package pe.edu.upt.simacmart.cuentas.application.port.inbound

import pe.edu.upt.simacmart.cuentas.domain.model.Cuenta
import pe.edu.upt.simacmart.cuentas.domain.model.Rol

data class LoginComando(
    val usuario: String,
    val passwordPlano: String
)

data class CrearCuentaComando(
    val usuario: String,
    val nombreCompleto: String,
    val correo: String,
    val passwordPlano: String,
    val rol: Rol
)

interface LoginUseCase {
    fun ejecutar(comando: LoginComando): Cuenta?
}

interface CrearCuentaUseCase {
    fun ejecutar(comando: CrearCuentaComando): Cuenta
}
