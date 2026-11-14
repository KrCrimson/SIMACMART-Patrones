/**
 * === SIMACMART · Trazabilidad ===
 * Archivo ..........: src/main/kotlin/pe/edu/upt/simacmart/ingesta/application/service/ServicioIngesta.kt
 * Capa / módulo ....: Aplicación · Módulo de Ingesta IoT
 * Función ..........: Orquestador de ingesta: autentica dispositivo, ejecuta cadena de validación y persiste en Unit of Work
 * Requerimientos ...: RF-01, RF-02, RF-03
 * Calidad (RNF) ....: RNF-01 (Transaccionalidad concurrente), RNF-02 (Mantenibilidad), RNF-03 (Tolerancia a fallos)
 * Casos de uso .....: CU-01 Registrar pesaje de balanza, CU-02 Registrar lectura meteorológica
 * Reglas de negocio : RN-01 (Rangos válidos e inválidos), RN-02 (Atrapamiento de duplicados sin sobrescritura)
 * Patrones .........: Chain of Responsibility, Unit of Work, Repository, Ports & Adapters (Hexagonal - Servicio de Aplicación)
 * Depende de .......: IngestarLectura, ValidacionHandler, VerificarDispositivo, UnitOfWork, Lectura, DatosLectura, ResultadoIngesta
 * Documento ........: FD04 - Informe de Arquitectura de Software (SAD SIMACMART), Sección 4.1 y 4.5
 * Autores ..........: Hernández y Arce (Ingeniería de Sistemas - UPT)
 * =========================================================================================
 */
package pe.edu.upt.simacmart.ingesta.application.service

import pe.edu.upt.simacmart.captacion.application.service.ServicioCaptacion
import pe.edu.upt.simacmart.ingesta.application.port.inbound.IngestaBalanzaDto
import pe.edu.upt.simacmart.ingesta.application.port.inbound.IngestaBalanzaUseCase
import pe.edu.upt.simacmart.ingesta.application.port.inbound.IngestaEcowittDto
import pe.edu.upt.simacmart.ingesta.application.port.inbound.IngestaEcowittUseCase
import pe.edu.upt.simacmart.ingesta.application.port.inbound.IngestarLectura
import pe.edu.upt.simacmart.ingesta.application.port.outbound.DispositivoRepository
import pe.edu.upt.simacmart.ingesta.application.port.outbound.LecturaRepository
import pe.edu.upt.simacmart.ingesta.application.port.outbound.UnitOfWork
import pe.edu.upt.simacmart.ingesta.application.port.outbound.VerificarDispositivo
import pe.edu.upt.simacmart.ingesta.domain.model.*
import pe.edu.upt.simacmart.ingesta.domain.service.ValidacionHandler
import pe.edu.upt.simacmart.shared.domain.Resultado
import java.math.BigDecimal
import java.time.Instant

/**
 * Servicio de Ingesta según Diagrama UML de Ingesta y Persistencia.
 */
class ServicioIngesta(
    private val validacion: ValidacionHandler,
    private val verificador: VerificarDispositivo,
    private val uow: UnitOfWork,
    private val captacion: ServicioCaptacion? = null
) : IngestarLectura, IngestaBalanzaUseCase, IngestaEcowittUseCase {

    override fun ejecutar(datos: DatosLectura, credencial: String): ResultadoIngesta {
        // 1. Autorización del dispositivo emisor mediante puerto VerificarDispositivo
        val dispositivoId = try {
            verificador.autorizar(datos.dispositivoExterno, credencial)
        } catch (e: Exception) {
            return ResultadoIngesta(
                estado = EstadoIngesta.RECHAZADA,
                mensaje = "Autenticación fallida para el dispositivo ${datos.dispositivoExterno}: ${e.message}"
            )
        }

        if (dispositivoId.isBlank()) {
            return ResultadoIngesta(
                estado = EstadoIngesta.RECHAZADA,
                mensaje = "Dispositivo no autorizado o credencial inválida."
            )
        }

        // 2. Creación de la Entidad de Dominio Lectura
        val lectura = Lectura(
            dispositivoId = dispositivoId,
            claveOrigen = datos.claveOrigen,
            secuencia = datos.secuencia,
            fechaMedicion = datos.fechaMedicion,
            fechaRecepcion = Instant.now(),
            mediciones = datos.mediciones
        )

        // 3. Ejecución de la Cadena de Responsabilidad (Campos -> Fecha -> Rangos)
        validacion.validar(lectura)

        // 4. Notificar a captación si es pesaje válido (evaluación de vaciados de tina)
        lectura.mediciones["pesoKg"]?.let { peso ->
            if (lectura.esValida()) {
                captacion?.procesarNuevaLectura(1, peso, lectura.fechaMedicion)
            }
        }

        // 5. Persistencia Atómica bajo Transacción (Unit of Work)
        return uow.ejecutar { lecturaRepo: LecturaRepository, dispositivoRepo: DispositivoRepository ->
            // Registrar fecha de reporte del dispositivo en la misma transacción
            dispositivoRepo.registrarReporte(dispositivoId, lectura.fechaRecepcion)

            // Inserción idempotente
            val insertada = lecturaRepo.insertarSiNueva(lectura)

            if (!insertada) {
                ResultadoIngesta(
                    estado = EstadoIngesta.DUPLICADA,
                    mensaje = "La lectura con clave ${lectura.claveOrigen} ya existía en el repositorio (idempotente).",
                    identificadorLectura = "${dispositivoId}:${lectura.claveOrigen}"
                )
            } else if (!lectura.esValida()) {
                ResultadoIngesta(
                    estado = EstadoIngesta.CONSERVADA_INVALIDA,
                    mensaje = "Lectura almacenada para auditoría con anomalías: " + lectura.motivosInvalidez.joinToString("; "),
                    identificadorLectura = "${dispositivoId}:${lectura.claveOrigen}"
                )
            } else {
                ResultadoIngesta(
                    estado = EstadoIngesta.ACEPTADA,
                    mensaje = "Lectura aceptada y persistida exitosamente.",
                    identificadorLectura = "${dispositivoId}:${lectura.claveOrigen}"
                )
            }
        }
    }

    // --- Métodos de compatibilidad para IngestaHttpController ---

    override fun recibirLectura(dto: IngestaBalanzaDto): Resultado<Long> {
        val mediciones = mutableMapOf<String, BigDecimal>("pesoKg" to dto.pesoKg)
        if (dto.bateriaV != null) mediciones["bateriaV"] = dto.bateriaV

        val datos = DatosLectura(
            dispositivoExterno = dto.dispositivoCodigo,
            claveOrigen = "SEQ-${dto.secuencia}",
            secuencia = dto.secuencia,
            fechaMedicion = Instant.now(),
            mediciones = mediciones,
            credencial = dto.apiKey
        )

        val res = ejecutar(datos, dto.apiKey)
        return when (res.estado) {
            EstadoIngesta.ACEPTADA -> Resultado(true, res.mensaje, dto.secuencia)
            EstadoIngesta.CONSERVADA_INVALIDA -> Resultado(true, res.mensaje, dto.secuencia)
            EstadoIngesta.DUPLICADA -> Resultado(false, res.mensaje, 0L)
            EstadoIngesta.RECHAZADA -> Resultado(false, res.mensaje, 0L)
        }
    }

    override fun recibirClima(dto: IngestaEcowittDto): Resultado<Long> {
        val mediciones = mutableMapOf<String, BigDecimal>()
        if (dto.tempC != null) mediciones["temperaturaC"] = dto.tempC
        if (dto.humedadPct != null) mediciones["humedadPct"] = dto.humedadPct
        if (dto.vientoMs != null) mediciones["vientoMs"] = dto.vientoMs
        if (dto.lluviaMm != null) mediciones["lluviaMm"] = dto.lluviaMm

        val datos = DatosLectura(
            dispositivoExterno = dto.dispositivoCodigo,
            claveOrigen = Instant.now().toString(),
            secuencia = null,
            fechaMedicion = Instant.now(),
            mediciones = mediciones,
            credencial = dto.apiKey
        )

        val res = ejecutar(datos, dto.apiKey)
        return when (res.estado) {
            EstadoIngesta.ACEPTADA -> Resultado(true, res.mensaje, 1L)
            EstadoIngesta.CONSERVADA_INVALIDA -> Resultado(true, res.mensaje, 1L)
            EstadoIngesta.DUPLICADA -> Resultado(false, res.mensaje, 0L)
            EstadoIngesta.RECHAZADA -> Resultado(false, res.mensaje, 0L)
        }
    }
}
