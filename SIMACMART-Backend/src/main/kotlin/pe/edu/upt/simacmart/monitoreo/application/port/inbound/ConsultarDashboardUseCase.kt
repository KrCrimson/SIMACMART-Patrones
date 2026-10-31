package pe.edu.upt.simacmart.monitoreo.application.port.inbound

data class DashboardResumenDto(
    val idUnidad: Int,
    val nombreUnidad: String,
    val localidad: String,
    val litrosHoy: Double,
    val pesoActualBalanzaKg: Double,
    val temperaturaC: Double,
    val humedadPct: Double,
    val vientoMs: Double,
    val radiacionWm2: Double,
    val puntoRocioC: Double,
    val vaciadosRegistrados: Int,
    val ultimoVaciado: String
)

interface ConsultarDashboardUseCase {
    fun obtenerResumen(unidadId: Int): DashboardResumenDto
}
