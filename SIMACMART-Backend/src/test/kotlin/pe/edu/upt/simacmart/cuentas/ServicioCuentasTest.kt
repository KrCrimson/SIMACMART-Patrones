package pe.edu.upt.simacmart.cuentas

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mindrot.jbcrypt.BCrypt
import pe.edu.upt.simacmart.cuentas.application.port.inbound.LoginComando
import pe.edu.upt.simacmart.cuentas.application.port.outbound.CuentaRepositoryPort
import pe.edu.upt.simacmart.cuentas.application.service.ServicioCuentas
import pe.edu.upt.simacmart.cuentas.domain.model.Cuenta
import pe.edu.upt.simacmart.cuentas.domain.model.Rol

class FakeCuentaRepository : CuentaRepositoryPort {
    private val cuentas = mutableListOf<Cuenta>()

    fun agregar(cuenta: Cuenta) { cuentas.add(cuenta) }

    override fun buscarPorUsuario(usuario: String): Cuenta? {
        return cuentas.find { it.usuario.equals(usuario, ignoreCase = true) }
    }

    override fun guardar(cuenta: Cuenta): Int {
        val id = cuentas.size + 1
        cuentas.add(cuenta.copy(idCuenta = id))
        return id
    }

    
}

class ServicioCuentasTest {

    @Test
    fun `debe autenticar exitosamente con credenciales validas`() {
        val fakeRepo = FakeCuentaRepository()
        val hash = BCrypt.hashpw("admin123", BCrypt.gensalt(10))
        fakeRepo.agregar(Cuenta(idCuenta = 1, usuario = "admin", nombreCompleto = "Admin", correo = "a@a.com", passwordHash = hash, rol = Rol.ADMINISTRADOR))

        val servicio = ServicioCuentas(fakeRepo)
        val resultado = servicio.ejecutar(LoginComando(usuario = "admin", passwordPlano = "admin123"))

        assertNotNull(resultado)
        assertEquals("admin", resultado?.usuario)
        assertEquals(Rol.ADMINISTRADOR, resultado?.rol)
    }

    @Test
    fun `debe rechazar autenticacion si la contrasena es incorrecta`() {
        val fakeRepo = FakeCuentaRepository()
        val hash = BCrypt.hashpw("admin123", BCrypt.gensalt(10))
        fakeRepo.agregar(Cuenta(idCuenta = 1, usuario = "admin", nombreCompleto = "Admin", correo = "a@a.com", passwordHash = hash, rol = Rol.ADMINISTRADOR))

        val servicio = ServicioCuentas(fakeRepo)
        val resultado = servicio.ejecutar(LoginComando(usuario = "admin", passwordPlano = "clave_invalida"))

        assertNull(resultado)
    }

    @Test
    fun `debe rechazar autenticacion si el usuario no existe`() {
        val fakeRepo = FakeCuentaRepository()
        val servicio = ServicioCuentas(fakeRepo)
        val resultado = servicio.ejecutar(LoginComando(usuario = "fantasma", passwordPlano = "123456"))

        assertNull(resultado)
    }
}
