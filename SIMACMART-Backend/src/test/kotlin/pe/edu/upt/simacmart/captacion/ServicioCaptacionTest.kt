package pe.edu.upt.simacmart.captacion

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pe.edu.upt.simacmart.captacion.application.port.outbound.CaptacionRepositoryPort
import pe.edu.upt.simacmart.captacion.application.service.ServicioCaptacion
import pe.edu.upt.simacmart.captacion.domain.model.CaptacionPeriodo
import pe.edu.upt.simacmart.captacion.domain.model.EventoVaciado
import java.math.BigDecimal
import java.time.Instant

class FakeCaptacionRepository : CaptacionRepositoryPort {
    var ultimoPeso: BigDecimal? = null
    val vaciados = mutableListOf<EventoVaciado>()
    val captaciones = mutableListOf<CaptacionPeriodo>()

    override fun obtenerUltimoPesoRegistrado(unidadId: Int): BigDecimal? = ultimoPeso

    override fun registrarVaciado(evento: EventoVaciado): Int {
        val id = vaciados.size + 1
        vaciados.add(evento.copy(idEvento = id))
        return id
    }

    override fun guardarCaptacion(captacion: CaptacionPeriodo): Int {
        val id = captaciones.size + 1
        captaciones.add(captacion.copy(idCaptacion = id))
        return id
    }

    override fun sumarLitrosDia(unidadId: Int): BigDecimal {
        return captaciones.fold(BigDecimal.ZERO) { acc, c -> acc.add(c.litrosCaptados) }
    }

    override fun listarVaciados(unidadId: Int, limite: Int): List<EventoVaciado> = vaciados.takeLast(limite)
}

class ServicioCaptacionTest {

    @Test
    fun `debe detectar vaciado si el peso cae 5 kg o mas`() {
        val fakeRepo = FakeCaptacionRepository()
        fakeRepo.ultimoPeso = BigDecimal("48.000") // Tina casi llena

        val servicio = ServicioCaptacion(fakeRepo)
        // Nuevo peso: 1.0 kg (caida de 47 kg)
        val evento = servicio.procesarNuevaLectura(unidadId = 1, nuevoPesoKg = BigDecimal("1.000"), fechaHora = Instant.now())

        assertNotNull(evento)
        assertEquals(BigDecimal("47.00"), evento?.volumenLitros)
        assertEquals(1, fakeRepo.vaciados.size)
    }

    @Test
    fun `no debe disparar vaciado si la caida es menor a 5 kg (ruido u oscilacion de viento)`() {
        val fakeRepo = FakeCaptacionRepository()
        fakeRepo.ultimoPeso = BigDecimal("20.000")

        val servicio = ServicioCaptacion(fakeRepo)
        // Caida de solo 2 kg (ej. viento fuerte o balanceo)
        val evento = servicio.procesarNuevaLectura(unidadId = 1, nuevoPesoKg = BigDecimal("18.000"), fechaHora = Instant.now())

        assertNull(evento)
        assertEquals(0, fakeRepo.vaciados.size)
    }

    @Test
    fun `debe acumular agua captada si el peso incrementa de forma normal`() {
        val fakeRepo = FakeCaptacionRepository()
        fakeRepo.ultimoPeso = BigDecimal("10.000")

        val servicio = ServicioCaptacion(fakeRepo)
        // Nuevo peso: 14.5 kg (aumento de 4.5 kg de agua condensada)
        val evento = servicio.procesarNuevaLectura(unidadId = 1, nuevoPesoKg = BigDecimal("14.500"), fechaHora = Instant.now())

        assertNull(evento) // No fue vaciado
        assertEquals(1, fakeRepo.captaciones.size)
        assertEquals(BigDecimal("4.50"), fakeRepo.captaciones[0].litrosCaptados)
    }
}
