/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/test/kotlin/pe/edu/upt/simacmart/ingesta/MqttObserverIngestaTest.kt
 * Capa / módulo ....: Tests · Módulo de Ingesta IoT
 * Función ..........: Prueba unitaria para el flujo completo: MqttFacade, BalanzaAdapter, ReceptorBalanza y ServicioIngesta con Chain of Responsibility y Unit of Work
 * Requerimientos ...: RF-01, RF-02, RF-03
 * Calidad (RNF) ....: RNF-01, RNF-03
 * Patrones .........: Facade, Observer, Adapter, Chain of Responsibility, Unit of Work, Repository
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.ingesta

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import pe.edu.upt.simacmart.ingesta.application.port.outbound.DispositivoRepository
import pe.edu.upt.simacmart.ingesta.application.port.outbound.LecturaRepository
import pe.edu.upt.simacmart.ingesta.application.port.outbound.UnitOfWork
import pe.edu.upt.simacmart.ingesta.application.port.outbound.VerificarDispositivo
import pe.edu.upt.simacmart.ingesta.application.service.ServicioIngesta
import pe.edu.upt.simacmart.ingesta.domain.model.EstadoIngesta
import pe.edu.upt.simacmart.ingesta.domain.model.Lectura
import pe.edu.upt.simacmart.ingesta.domain.model.ResultadoIngesta
import pe.edu.upt.simacmart.ingesta.domain.service.CamposHandler
import pe.edu.upt.simacmart.ingesta.domain.service.FechaHandler
import pe.edu.upt.simacmart.ingesta.domain.service.RangosHandler
import pe.edu.upt.simacmart.ingesta.infrastructure.adapter.inbound.BalanzaAdapter
import pe.edu.upt.simacmart.ingesta.infrastructure.adapter.inbound.MqttFacade
import pe.edu.upt.simacmart.ingesta.infrastructure.adapter.inbound.ReceptorBalanza
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.time.Instant

class MqttObserverIngestaTest {

    private lateinit var servicioIngesta: ServicioIngesta
    private lateinit var mqttFacade: MqttFacade
    private lateinit var balanzaAdapter: BalanzaAdapter
    private lateinit var receptorBalanza: ReceptorBalanza

    private val lecturasGuardadas = mutableListOf<Lectura>()
    private val reportesActualizados = mutableListOf<Pair<String, Instant>>()

    @BeforeEach
    fun setUp() {
        lecturasGuardadas.clear()
        reportesActualizados.clear()

        // 1. Cadena de Responsabilidad
        val campos = CamposHandler()
        val fecha = FechaHandler()
        val rangos = RangosHandler()
        campos.establecerSiguiente(fecha)
        fecha.establecerSiguiente(rangos)

        // 2. Mock de Verificador de Dispositivo
        val verificadorMock = object : VerificarDispositivo {
            override fun autorizar(dispositivoExterno: String, credencial: String): String {
                if (dispositivoExterno == "INACTIVO") throw IllegalStateException("Dispositivo suspendido")
                return "1" // ID interno
            }
        }

        // 3. Mock de Unit of Work y Repositorios
        val uowMock = object : UnitOfWork {
            override fun ejecutar(operacion: (LecturaRepository, DispositivoRepository) -> ResultadoIngesta): ResultadoIngesta {
                val lecturaRepoMock = object : LecturaRepository {
                    override fun insertarSiNueva(lectura: Lectura): Boolean {
                        // Simula idempotencia: si ya existe misma claveOrigen, retorna false
                        if (lecturasGuardadas.any { it.claveOrigen == lectura.claveOrigen }) {
                            return false
                        }
                        lecturasGuardadas.add(lectura)
                        return true
                    }
                }
                val dispositivoRepoMock = object : DispositivoRepository {
                    override fun registrarReporte(dispositivoId: String, fecha: Instant) {
                        reportesActualizados.add(dispositivoId to fecha)
                    }
                }
                return operacion(lecturaRepoMock, dispositivoRepoMock)
            }
        }

        // 4. Servicio de Ingesta
        servicioIngesta = ServicioIngesta(
            validacion = campos,
            verificador = verificadorMock,
            uow = uowMock,
            captacion = null
        )

        // 5. Infraestructura MQTT (Facade, Adapter y Observer)
        mqttFacade = MqttFacade()
        balanzaAdapter = BalanzaAdapter()
        receptorBalanza = ReceptorBalanza(balanzaAdapter, servicioIngesta, mqttFacade)

        mqttFacade.suscribir("simacmart/+/balanza", receptorBalanza)
    }

    @Test
    fun `cuando balanza publica por MQTT entonces el observador procesa, persiste en UoW y responde con acuse`() {
        val payload = """
            {
                "dispositivo": "BAL-ESP32-01",
                "secuencia": 105,
                "pesoKg": 24.50,
                "bateriaV": 3.92,
                "credencial": "balanza-token"
            }
        """.trimIndent()

        mqttFacade.alEventoDelCliente("simacmart/nodo1/balanza", payload.toByteArray(StandardCharsets.UTF_8))

        // Verifica que la lectura se haya guardado
        assertEquals(1, lecturasGuardadas.size)
        val lectura = lecturasGuardadas[0]
        assertEquals("1", lectura.dispositivoId)
        assertEquals("SEQ-105", lectura.claveOrigen)
        assertEquals(0, BigDecimal("24.50").compareTo(lectura.mediciones["pesoKg"]))
        assertTrue(lectura.esValida())

        // Verifica actualización de reporte en DispositivoRepository
        assertEquals(1, reportesActualizados.size)

        // Verifica que se haya publicado el acuse en MQTT
        val publicados = mqttFacade.obtenerUltimosPublicados()
        assertEquals(1, publicados.size)
        assertTrue(publicados[0].first.contains("simacmart/balanza/acuse/BAL-ESP32-01"))
        val acuseTexto = String(publicados[0].second, StandardCharsets.UTF_8)
        assertTrue(acuseTexto.contains("ACEPTADA"))
    }

    @Test
    fun `cuando se recibe una lectura con peso fuera de rango se conserva como invalida para auditoria`() {
        val payload = """
            {
                "dispositivo": "BAL-ESP32-01",
                "secuencia": 106,
                "pesoKg": 150.00,
                "credencial": "balanza-token"
            }
        """.trimIndent()

        mqttFacade.alEventoDelCliente("simacmart/nodo1/balanza", payload.toByteArray(StandardCharsets.UTF_8))

        assertEquals(1, lecturasGuardadas.size)
        val lectura = lecturasGuardadas[0]
        assertFalse(lectura.esValida())
        assertTrue(lectura.motivosInvalidez.any { it.contains("fuera de rango") })

        val publicados = mqttFacade.obtenerUltimosPublicados()
        val acuseTexto = String(publicados[0].second, StandardCharsets.UTF_8)
        assertTrue(acuseTexto.contains("CONSERVADA_INVALIDA"))
    }

    @Test
    fun `cuando se reenvia una lectura duplicada el sistema la reconoce como DUPLICADA sin sobrescribir`() {
        val payload = """
            {
                "dispositivo": "BAL-ESP32-01",
                "secuencia": 200,
                "pesoKg": 18.00,
                "credencial": "balanza-token"
            }
        """.trimIndent()

        // Primer envío
        mqttFacade.alEventoDelCliente("simacmart/nodo1/balanza", payload.toByteArray(StandardCharsets.UTF_8))
        assertEquals(1, lecturasGuardadas.size)

        // Segundo envío (duplicado por reintento de red)
        mqttFacade.alEventoDelCliente("simacmart/nodo1/balanza", payload.toByteArray(StandardCharsets.UTF_8))
        assertEquals(1, lecturasGuardadas.size) // No aumenta la lista

        val publicados = mqttFacade.obtenerUltimosPublicados()
        assertEquals(2, publicados.size)
        val acuseDuplicado = String(publicados[1].second, StandardCharsets.UTF_8)
        assertTrue(acuseDuplicado.contains("DUPLICADA"))
    }
}
