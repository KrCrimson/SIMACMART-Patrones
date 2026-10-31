package pe.edu.upt.simacmart.monitoreo.application.service

import pe.edu.upt.simacmart.monitoreo.application.port.inbound.ConsultarDashboardUseCase
import pe.edu.upt.simacmart.monitoreo.application.port.inbound.DashboardResumenDto
import pe.edu.upt.simacmart.shared.infrastructure.db.DatabaseConfig

class ServicioMonitoreo : ConsultarDashboardUseCase {

    override fun obtenerResumen(unidadId: Int): DashboardResumenDto {
        var nombre = "Nodo Pachia Central #01"
        var localidad = "Pachia (Tacna) - Sector Calientes"
        var pesoActual = 18.40
        var litrosHoy = 42.80
        var temp = 14.1
        var hum = 94.2
        var viento = 3.6
        var rad = 15.0
        var vaciados = 4
        var ultimoVaciado = "2026-10-24 07:15:00"

        try {
            DatabaseConfig.obtenerConexion().use { conn ->
                // 1. Obtener datos de la Unidad
                val sqlUnidad = "SELECT nombre, localidad FROM unidades WHERE id_unidad = ?"
                conn.prepareStatement(sqlUnidad).use { pstmt ->
                    pstmt.setInt(1, unidadId)
                    val rs = pstmt.executeQuery()
                    if (rs.next()) {
                        nombre = rs.getString("nombre")
                        localidad = rs.getString("localidad")
                    }
                }

                // 2. Obtener ultima lectura de balanza si existe
                val sqlBalanza = "SELECT peso_kg FROM lecturas_balanza WHERE id_unidad = ? ORDER BY fecha_hora DESC LIMIT 1"
                conn.prepareStatement(sqlBalanza).use { pstmt ->
                    pstmt.setInt(1, unidadId)
                    val rs = pstmt.executeQuery()
                    if (rs.next()) {
                        pesoActual = rs.getDouble("peso_kg")
                    }
                }

                // 3. Obtener ultima lectura climatica si existe
                val sqlEstacion = "SELECT temperatura_c, humedad_pct, viento_ms, radiacion_wm2 FROM lecturas_estacion WHERE id_unidad = ? ORDER BY fecha_hora DESC LIMIT 1"
                conn.prepareStatement(sqlEstacion).use { pstmt ->
                    pstmt.setInt(1, unidadId)
                    val rs = pstmt.executeQuery()
                    if (rs.next()) {
                        temp = rs.getDouble("temperatura_c")
                        hum = rs.getDouble("humedad_pct")
                        viento = rs.getDouble("viento_ms")
                        rad = rs.getDouble("radiacion_wm2")
                    }
                }

                // 4. Conteo de vaciados
                val sqlVaciados = "SELECT COUNT(*), MAX(fecha_hora) FROM eventos WHERE id_unidad = ? AND tipo = 'vaciado'"
                conn.prepareStatement(sqlVaciados).use { pstmt ->
                    pstmt.setInt(1, unidadId)
                    val rs = pstmt.executeQuery()
                    if (rs.next()) {
                        val count = rs.getInt(1)
                        if (count > 0) {
                            vaciados = count
                            ultimoVaciado = rs.getString(2) ?: ultimoVaciado
                        }
                    }
                }
            }
        } catch (e: Exception) {
            println("[Monitoreo] Nota en consulta DB: ${e.message}")
        }

        // Calculo del punto de rocio aproximado
        val rocio = temp - ((100.0 - hum) / 5.0)

        return DashboardResumenDto(
            idUnidad = unidadId,
            nombreUnidad = nombre,
            localidad = localidad,
            litrosHoy = litrosHoy,
            pesoActualBalanzaKg = pesoActual,
            temperaturaC = temp,
            humedadPct = hum,
            vientoMs = viento,
            radiacionWm2 = rad,
            puntoRocioC = Math.round(rocio * 10.0) / 10.0,
            vaciadosRegistrados = vaciados,
            ultimoVaciado = ultimoVaciado
        )
    }
}
