package pe.edu.upt.simacmart.ingesta.application.port.outbound

import pe.edu.upt.simacmart.ingesta.domain.model.LecturaBalanza
import pe.edu.upt.simacmart.ingesta.domain.model.LecturaEstacion
import pe.edu.upt.simacmart.ingesta.domain.model.Dispositivo

interface IngestaRepositoryPort {
    fun buscarDispositivo(codigo: String): Dispositivo?
    fun guardarLecturaBalanza(lectura: LecturaBalanza): Long
    fun guardarLecturaEstacion(lectura: LecturaEstacion): Long
    fun obtenerUltimoPeso(idDispositivo: Int): java.math.BigDecimal?
}
