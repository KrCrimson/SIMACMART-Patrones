package pe.edu.upt.simacmart.cuentas.application.service

import org.mindrot.jbcrypt.BCrypt
import pe.edu.upt.simacmart.cuentas.application.port.inbound.CrearCuentaComando
import pe.edu.upt.simacmart.cuentas.application.port.inbound.CrearCuentaUseCase
import pe.edu.upt.simacmart.cuentas.application.port.inbound.LoginComando
import pe.edu.upt.simacmart.cuentas.application.port.inbound.LoginUseCase
import pe.edu.upt.simacmart.cuentas.application.port.outbound.CuentaRepositoryPort
import pe.edu.upt.simacmart.cuentas.domain.model.Cuenta

class ServicioCuentas(
    private val cuentaRepo: CuentaRepositoryPort
) : LoginUseCase, CrearCuentaUseCase {

    override fun ejecutar(comando: LoginComando): Cuenta? {
        val cuenta = cuentaRepo.buscarPorUsuario(comando.usuario) ?: return null
        if (!cuenta.activo) return null
        return if (BCrypt.checkpw(comando.passwordPlano, cuenta.passwordHash)) cuenta else null
    }

    override fun ejecutar(comando: CrearCuentaComando): Cuenta {
        val hash = BCrypt.hashpw(comando.passwordPlano, BCrypt.gensalt(10))
        val nueva = Cuenta(
            usuario = comando.usuario,
            nombreCompleto = comando.nombreCompleto,
            correo = comando.correo,
            passwordHash = hash,
            rol = comando.rol
        )
        val id = cuentaRepo.guardar(nueva)
        return nueva.copy(idCuenta = id)
    }
}
