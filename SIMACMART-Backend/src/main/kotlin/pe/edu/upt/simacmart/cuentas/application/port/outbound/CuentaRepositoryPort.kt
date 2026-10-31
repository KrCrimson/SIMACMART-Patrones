package pe.edu.upt.simacmart.cuentas.application.port.outbound

import pe.edu.upt.simacmart.cuentas.domain.model.Cuenta

interface CuentaRepositoryPort {
    fun buscarPorUsuario(usuario: String): Cuenta?
    fun guardar(cuenta: Cuenta): Int
}
