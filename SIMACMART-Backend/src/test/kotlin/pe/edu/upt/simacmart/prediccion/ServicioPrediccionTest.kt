package pe.edu.upt.simacmart.prediccion

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pe.edu.upt.simacmart.prediccion.application.service.ServicioPrediccion
import pe.edu.upt.simacmart.prediccion.infrastructure.adapter.outbound.PrediccionConModeloMLStrategy
import pe.edu.upt.simacmart.prediccion.infrastructure.adapter.outbound.PromedioHistoricoStrategy

class ServicioPrediccionTest {

    @Test
    fun `debe generar estimacion con estrategia de Machine Learning y confianza del 92 por ciento`() {
        val estrategiaML = PrediccionConModeloMLStrategy()
        val servicio = ServicioPrediccion(estrategiaML)

        val resultado = servicio.estimarConEstrategia(unidadId = 1, codigoEstrategia = "ML")

        assertNotNull(resultado)
        assertEquals("ML", resultado.codigoEstrategia)
        assertEquals(92.4, resultado.confianzaPct)
        assertTrue(resultado.litrosTotales48h > 0.0)
        assertTrue(resultado.litrosTotales7Dias >= resultado.litrosTotales48h)
        assertEquals(7, resultado.proyeccion.size)
    }

    @Test
    fun `debe conmutar dinamicamente a la estrategia de Promedio Historico con confianza del 75 por ciento`() {
        val estrategiaML = PrediccionConModeloMLStrategy()
        val servicio = ServicioPrediccion(estrategiaML)

        val resultado = servicio.estimarConEstrategia(unidadId = 1, codigoEstrategia = "HISTORICO")

        assertNotNull(resultado)
        assertEquals("HISTORICO", resultado.codigoEstrategia)
        assertEquals(75.0, resultado.confianzaPct)
        assertEquals(7, resultado.proyeccion.size)
    }
}
