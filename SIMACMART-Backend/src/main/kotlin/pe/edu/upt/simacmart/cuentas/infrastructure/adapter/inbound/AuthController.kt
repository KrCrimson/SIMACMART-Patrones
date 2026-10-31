package pe.edu.upt.simacmart.cuentas.infrastructure.adapter.inbound

import com.google.gson.Gson
import com.google.gson.JsonObject
import pe.edu.upt.simacmart.cuentas.application.port.inbound.CrearCuentaComando
import pe.edu.upt.simacmart.cuentas.application.port.inbound.LoginComando
import pe.edu.upt.simacmart.cuentas.application.service.ServicioCuentas
import pe.edu.upt.simacmart.cuentas.domain.model.Rol
import pe.edu.upt.simacmart.shared.infrastructure.http.ServidorHttpMonolito
import java.io.InputStreamReader
import java.util.UUID

class AuthController(
    private val servicioCuentas: ServicioCuentas
) {
    private val gson = Gson()

    fun registrarRutas(servidor: ServidorHttpMonolito) {
        servidor.registrarRuta("/api/v1/auth/login") { exchange ->
            if (exchange.requestMethod.equals("POST", ignoreCase = true)) {
                try {
                    val reader = InputStreamReader(exchange.requestBody, Charsets.UTF_8)
                    val body = gson.fromJson(reader, JsonObject::class.java)
                    val usuario = body.get("usuario")?.asString.orEmpty()
                    val password = body.get("password")?.asString.orEmpty()

                    val cuenta = servicioCuentas.ejecutar(LoginComando(usuario, password))
                    if (cuenta != null) {
                        val token = "jwt_simacmart_" + UUID.randomUUID().toString().replace("-", "")
                        val resJson = "{\"exito\":true,\"token\":\"" + token + "\",\"usuario\":{\"id\":" + cuenta.idCuenta + ",\"usuario\":\"" + cuenta.usuario + "\",\"nombre\":\"" + cuenta.nombreCompleto + "\",\"rol\":\"" + cuenta.rol + "\"}}"
                        ServidorHttpMonolito.responderJson(exchange, 200, resJson)
                    } else {
                        ServidorHttpMonolito.responderJson(exchange, 401, "{\"exito\":false,\"error\":\"Credenciales incorrectas o usuario inactivo\"}")
                    }
                } catch (e: Exception) {
                    ServidorHttpMonolito.responderJson(exchange, 400, "{\"exito\":false,\"error\":\"Payload JSON invalido: " + e.message + "\"}")
                }
            } else {
                ServidorHttpMonolito.responderJson(exchange, 405, "{\"error\":\"Metodo no permitido\"}")
            }
        }

        servidor.registrarRuta("/api/v1/auth/registro") { exchange ->
            if (exchange.requestMethod.equals("POST", ignoreCase = true)) {
                try {
                    val reader = InputStreamReader(exchange.requestBody, Charsets.UTF_8)
                    val body = gson.fromJson(reader, JsonObject::class.java)
                    val usuario = body.get("usuario")?.asString.orEmpty()
                    val nombreCompleto = body.get("nombreCompleto")?.asString.orEmpty()
                    val correo = body.get("correo")?.asString.orEmpty()
                    val password = body.get("password")?.asString.orEmpty()
                    val rolStr = body.get("rol")?.asString?.uppercase() ?: "OPERADOR"

                    val rol = try { Rol.valueOf(rolStr) } catch (_: Exception) { Rol.OPERADOR }
                    val nueva = servicioCuentas.ejecutar(CrearCuentaComando(usuario, nombreCompleto, correo, password, rol))

                    val resJson = "{\"exito\":true,\"id\":" + nueva.idCuenta + ",\"usuario\":\"" + nueva.usuario + "\",\"rol\":\"" + nueva.rol + "\"}"
                    ServidorHttpMonolito.responderJson(exchange, 201, resJson)
                } catch (e: Exception) {
                    ServidorHttpMonolito.responderJson(exchange, 400, "{\"exito\":false,\"error\":\"" + e.message + "\"}")
                }
            } else {
                ServidorHttpMonolito.responderJson(exchange, 405, "{\"error\":\"Metodo no permitido\"}")
            }
        }
    }
}
