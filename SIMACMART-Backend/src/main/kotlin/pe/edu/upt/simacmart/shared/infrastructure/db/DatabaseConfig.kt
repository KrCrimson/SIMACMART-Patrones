package pe.edu.upt.simacmart.shared.infrastructure.db

import java.io.File
import java.sql.Connection
import java.sql.DriverManager

enum class TipoMotor {
    POSTGRESQL,
    SQLITE
}

/**
 * Gestor Central de Conexiones a Base de Datos.
 * Soporta conexion a PostgreSQL en la nube (ej. Neon) y conmutacion automatica
 * a SQLite local ante desconexion o entorno offline (Store-and-Forward).
 */
object DatabaseConfig {
    var postgresUrl: String = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/simacmart"
    var postgresUser: String = System.getenv("DB_USER") ?: "postgres"
    var postgresPass: String = System.getenv("DB_PASSWORD") ?: "postgres"
    var sqlitePath: String = System.getenv("SQLITE_PATH") ?: "data/simacmart.db"

    private var motorActivo: TipoMotor = TipoMotor.SQLITE

    fun obtenerConexion(): Connection {
        // 1. Intento primario: PostgreSQL
        try {
            val conn = DriverManager.getConnection(postgresUrl, postgresUser, postgresPass)
            motorActivo = TipoMotor.POSTGRESQL
            return conn
        } catch (e: Exception) {
            // 2. Fallback secundario: SQLite local offline
            motorActivo = TipoMotor.SQLITE
            return obtenerConexionSqlite()
        }
    }

    fun obtenerConexionSqlite(): Connection {
        val file = File(sqlitePath)
        file.parentFile?.mkdirs()
        return DriverManager.getConnection("jdbc:sqlite:$sqlitePath")
    }

    fun obtenerConexionPostgres(): Connection {
        return DriverManager.getConnection(postgresUrl, postgresUser, postgresPass)
    }

    // Alias de compatibilidad
    fun getPostgresConnection(): Connection = obtenerConexionPostgres()
    fun getSqliteConnection(): Connection = obtenerConexionSqlite()

    fun motorActual(): TipoMotor = motorActivo

    fun esPostgresDisponible(): Boolean {
        return try {
            DriverManager.getConnection(postgresUrl, postgresUser, postgresPass).use { true }
        } catch (e: Exception) {
            false
        }
    }
}
