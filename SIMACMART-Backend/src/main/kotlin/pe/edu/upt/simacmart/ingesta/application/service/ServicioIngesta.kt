package pe.edu.upt.simacmart.ingesta.application.service

import pe.edu.upt.simacmart.ingesta.application.port.inbound.*
import pe.edu.upt.simacmart.ingesta.application.port.outbound.IngestaRepositoryPort
import pe.edu.upt.simacmart.ingesta.domain.model.*
import pe.edu.upt.simacmart.shared.domain.Resultado
import java.time.Instant

class ServicioIngesta(
    private val repo: IngestaRepositoryPort
) : IngestaBalanzaUseCase, IngestaEcowittUseCase {

    override fun recibirLectura(dto: IngestaBalanzaDto): Resultado<Long> {
        val disp = repo.buscarDispositivo(dto.dispositivoCodigo)
            ?: return Resultado(false, "Dispositivo no reconocido")

        if (!disp.activo) return Resultado(false, "Dispositivo desactivado")

        // Filtrado básico de lectura imposible (anomalía)
        val esValida = dto.pesoKg >= java.math.BigDecimal.ZERO && dto.pesoKg <= java.math.BigDecimal("500.0")
        val motivo = if (!esValida) "Lectura fuera de rango físico" else null

        val lectura = LecturaBalanza(
            idDispositivo = disp.idDispositivo,
            secuencia = dto.secuencia,
            fechaHora = Instant.now(),
            pesoKg = dto.pesoKg,
            bateriaV = dto.bateriaV,
            valida = esValida,
            motivoInvalida = motivo
        )

        val id = repo.guardarLecturaBalanza(lectura)
        return Resultado(true, "Lectura de balanza aceptada", id)
    }

    override fun recibirClima(dto: IngestaEcowittDto): Resultado<Long> {
        val disp = repo.buscarDispositivo(dto.dispositivoCodigo)
            ?: return Resultado(false, "Estación Ecowitt no reconocida")

        val lectura = LecturaEstacion(
            idDispositivo = disp.idDispositivo,
            fechaHora = Instant.now(),
            temperaturaC = dto.tempC,
            humedadPct = dto.humedadPct,
            vientoMs = dto.vientoMs,
            radiacionWm2 = null,
            lluviaMm = dto.lluviaMm,
            valida = true
        )

        val id = repo.guardarLecturaEstacion(lectura)
        return Resultado(true, "Datos climáticos Ecowitt registrados", id)
    }
}
