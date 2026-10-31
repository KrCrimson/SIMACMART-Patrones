package pe.edu.upt.simacmart.shared.infrastructure.db

import java.io.InputStreamReader
import java.sql.Connection

/**
 * Inicializador y migrador automático de Base de Datos.
 * Aplica el esquema DDL y los datos semilla según el motor activo (PostgreSQL o SQLite).
 */
object DatabaseInitializer {

    fun inicializar() {
        println("=================================================================")
        println(">>> [DB INITIALIZER] Comprobando estado de persistencia...")

        val conexion = try {
            DatabaseConfig.obtenerConexion()
        } catch (e: Exception) {
            println(">>> [DB INITIALIZER] Error al obtener conexion: ${e.message}")
            return
        }

        conexion.use { conn ->
            val motor = DatabaseConfig.motorActual()
            println(">>> [DB INITIALIZER] Motor de base de datos activo: $motor")

            if (motor == TipoMotor.POSTGRESQL) {
                aplicarMigracion(conn, "/db/migration/V1__schema.sql")
                aplicarMigracion(conn, "/db/migration/V2__seed.sql")
            } else {
                aplicarMigracion(conn, "/db/migration/V1__schema_sqlite.sql")
                aplicarMigracion(conn, "/db/migration/V2__seed_sqlite.sql")
            }

            verificarEstado(conn)
        }
        println("=================================================================")
    }

    private fun aplicarMigracion(conn: Connection, rutaRecurso: String) {
        val stream = DatabaseInitializer::class.java.getResourceAsStream(rutaRecurso)
        if (stream == null) {
            println(">>> [DB INITIALIZER] No se encontro el archivo de migracion: $rutaRecurso")
            return
        }

        val sqlCompleto = InputStreamReader(stream, Charsets.UTF_8).use { it.readText() }
        val sentencias = sqlCompleto.split(";").map { it.trim() }.filter { it.isNotEmpty() }

        conn.createStatement().use { stmt ->
            for (sentencia in sentencias) {
                try {
                    stmt.execute(sentencia)
                } catch (e: Exception) {
                    // Ignorar errores de objetos ya existentes
                    if (!e.message.orEmpty().contains("already exists", ignoreCase = true) &&
                        !e.message.orEmpty().contains("duplicate", ignoreCase = true)) {
                        println(">>> [DB INITIALIZER] Nota en sentencia: ${e.message}")
                    }
                }
            }
        }
        println(">>> [DB INITIALIZER] Migracion ejecutada con exito: $rutaRecurso")
    }

    private fun verificarEstado(conn: Connection) {
        conn.createStatement().use { stmt ->
            val rsUnidades = stmt.executeQuery("SELECT COUNT(*) FROM unidades")
            if (rsUnidades.next()) {
                val countUnidades = rsUnidades.getInt(1)
                println(">>> [DB INITIALIZER] Unidades activas registradas: $countUnidades")
            }

            val rsCuentas = stmt.executeQuery("SELECT COUNT(*) FROM cuentas")
            if (rsCuentas.next()) {
                val countCuentas = rsCuentas.getInt(1)
                println(">>> [DB INITIALIZER] Cuentas de usuario configuradas: $countCuentas")
            }
        }
    }
}
