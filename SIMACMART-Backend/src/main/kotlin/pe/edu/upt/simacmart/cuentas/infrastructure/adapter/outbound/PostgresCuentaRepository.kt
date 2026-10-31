package pe.edu.upt.simacmart.cuentas.infrastructure.adapter.outbound

import pe.edu.upt.simacmart.cuentas.application.port.outbound.CuentaRepositoryPort
import pe.edu.upt.simacmart.cuentas.domain.model.Cuenta
import pe.edu.upt.simacmart.cuentas.domain.model.Rol
import pe.edu.upt.simacmart.shared.infrastructure.db.DatabaseConfig
import java.sql.Statement
import java.time.Instant

class PostgresCuentaRepository : CuentaRepositoryPort {

    override fun buscarPorUsuario(usuario: String): Cuenta? {
        val sql = "SELECT id_cuenta, usuario, nombre_completo, correo, password_hash, rol, activo, creado_en FROM cuentas WHERE usuario = ?"
        try {
            DatabaseConfig.obtenerConexion().use { conn ->
                conn.prepareStatement(sql).use { pstmt ->
                    pstmt.setString(1, usuario)
                    val rs = pstmt.executeQuery()
                    if (rs.next()) {
                        return Cuenta(
                            idCuenta = rs.getInt("id_cuenta"),
                            usuario = rs.getString("usuario"),
                            nombreCompleto = rs.getString("nombre_completo"),
                            correo = rs.getString("correo") ?: "",
                            passwordHash = rs.getString("password_hash"),
                            rol = Rol.valueOf(rs.getString("rol").uppercase()),
                            activo = rs.getBoolean("activo"),
                            creadoEn = Instant.now()
                        )
                    }
                }
            }
        } catch (e: Exception) {
            println("[CuentasRepo] Error al buscar usuario $usuario: ${e.message}")
        }
        return null
    }

    override fun guardar(cuenta: Cuenta): Int {
        val sql = "INSERT INTO cuentas (usuario, nombre_completo, correo, password_hash, rol, activo) VALUES (?, ?, ?, ?, ?, ?)"
        try {
            DatabaseConfig.obtenerConexion().use { conn ->
                conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { pstmt ->
                    pstmt.setString(1, cuenta.usuario)
                    pstmt.setString(2, cuenta.nombreCompleto)
                    pstmt.setString(3, cuenta.correo)
                    pstmt.setString(4, cuenta.passwordHash)
                    pstmt.setString(5, cuenta.rol.name)
                    pstmt.setBoolean(6, cuenta.activo)
                    pstmt.executeUpdate()
                    val rs = pstmt.generatedKeys
                    if (rs.next()) return rs.getInt(1)
                }
            }
        } catch (e: Exception) {
            println("[CuentasRepo] Error al guardar usuario ${cuenta.usuario}: ${e.message}")
        }
        return 0
    }
}
