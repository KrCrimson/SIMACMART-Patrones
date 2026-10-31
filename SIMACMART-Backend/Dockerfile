# ============================================================================
# SIMACMART BACKEND - DOCKERFILE MULTI-STAGE (UPT)
# Optimizacion para Render Cloud y Despliegue en Borde (Alpine + JDK 21)
# ============================================================================

# --- ETAPA 1: Compilacion y Empaquetado ---
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Copiar archivos de Gradle primero para cachear capas de dependencias
COPY gradlew gradlew.bat ./
COPY gradle gradle/
COPY build.gradle.kts settings.gradle.kts ./

# Dar permisos de ejecucion al wrapper
RUN chmod +x gradlew

# Descargar dependencias
RUN ./gradlew dependencies --no-daemon || true

# Copiar codigo fuente y recursos
COPY src src/

# Construir distribucion instalable de produccion
RUN ./gradlew installDist --no-daemon

# --- ETAPA 2: Runtime Ultraliviano de Produccion ---
FROM eclipse-temurin:21-jre-alpine AS runtime

LABEL maintainer="Universidad Privada de Tacna - Escuela Profesional de Ingenieria de Sistemas"
LABEL project="SIMACMART - Monitoreo y Captacion de Niebla en Pachia"

WORKDIR /app

# Crear usuario sin privilegios para seguridad (Principio de menor privilegio)
RUN addgroup -S simacgroup && adduser -S simacuser -G simacgroup && \
    mkdir -p /app/data && chown -R simacuser:simacgroup /app

# Copiar binarios y bibliotecas compiladas desde la etapa builder
COPY --from=builder --chown=simacuser:simacgroup /build/build/install/SIMACMART-Backend /app/

# Variables de entorno por defecto
ENV PORT=8080 \
    SQLITE_PATH=/app/data/simacmart.db \
    JAVA_OPTS="-Xms64m -Xmx256m -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"

# Exponer puerto HTTP
EXPOSE 8080

# Volumen persistente para buffer local offline Store-and-Forward
VOLUME ["/app/data"]

USER simacuser

# Comando de arranque del servidor nativo JVM
ENTRYPOINT ["/bin/sh", "-c", "exec /app/bin/SIMACMART-Backend $JAVA_OPTS"]
