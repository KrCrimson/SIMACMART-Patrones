package pe.edu.upt.simacmart.shared.infrastructure.db

import java.sql.Connection

/**
 * Puerto / Abstraccion JDBC para DataSource segun Diagrama UML de Persistencia (Pags 4-6).
 */
interface DataSource {
    fun getConnection(): Connection
}

/**
 * Adaptador por defecto que delega al gestor resiliente DatabaseConfig.
 * Ofrece conmutacion transparente entre PostgreSQL (nube/Neon) y SQLite (local).
 */
object ResilienteDataSource : DataSource {
    override fun getConnection(): Connection {
        return DatabaseConfig.obtenerConexion()
    }
}

