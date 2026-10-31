package pe.edu.upt.simacmart.shared.infrastructure.http

import com.google.gson.*
import java.lang.reflect.Type
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

object JsonUtils {
    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Instant::class.java, JsonSerializer<Instant> { src, _, _ -> JsonPrimitive(src.toString()) })
        .registerTypeAdapter(Instant::class.java, JsonDeserializer<Instant> { json, _, _ -> Instant.parse(json.asString) })
        .registerTypeAdapter(LocalDate::class.java, JsonSerializer<LocalDate> { src, _, _ -> JsonPrimitive(src.toString()) })
        .registerTypeAdapter(BigDecimal::class.java, JsonSerializer<BigDecimal> { src, _, _ -> JsonPrimitive(src.toDouble()) })
        .serializeNulls()
        .create()
}
