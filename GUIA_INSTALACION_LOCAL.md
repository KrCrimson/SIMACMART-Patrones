# 🛠️ GUÍA DE INSTALACIÓN Y DESPLIEGUE LOCAL DESDE CERO — SIMACMART

> **Para cualquier computadora recién armada, formateada o sin programas previos.**  
> Esta guía asume que tu PC solo tiene instalado el sistema operativo (Windows 10/11, Linux o macOS) y un navegador web (Edge o Chrome). No necesitas conocimientos avanzados ni programas de pago.

---

## 🧭 Índice Rápido

1. [¿Qué necesita el sistema? (Cero programas de pago)](#1-qué-necesita-el-sistema)
2. [Paso 1: Instalar los 3 programas base en tu PC nueva](#paso-1-instalar-los-3-programas-base-en-tu-pc-nueva)
   * [Opción Automática (1 solo comando en Windows)](#opción-a-automática-con-winget-la-más-rápida)
   * [Opción Manual (Descarga con clics en el navegador)](#opción-b-manual-paso-a-paso-desde-el-navegador)
3. [Paso 2: Verificar que tu PC ya está lista](#paso-2-verificar-que-tu-pc-está-lista)
4. [Paso 3: Descargar el proyecto](#paso-3-descargar-el-proyecto)
5. [Paso 4: Encender el sistema (¡Método de 1 Clic con scripts .bat!)](#paso-4-encender-el-sistema)
6. [Paso 5: Acceder y usar el Dashboard en el navegador](#paso-5-acceder-y-usar-el-dashboard)
7. [Solución de problemas comunes en computadoras recién formateadas](#solución-de-problemas-comunes)

---

## 1. ¿Qué necesita el sistema?

El proyecto **SIMACMART** fue diseñado bajo el principio de **autonomía total**:
* ❌ **NO necesitas instalar Gradle:** El proyecto ya incluye el ejecutable `gradlew` / `gradlew.bat`.
* ❌ **NO necesitas instalar bases de datos pesadas (como PostgreSQL o Docker):** El sistema cuenta con **persistencia híbrida**; si no hay PostgreSQL instalado, se inicia automáticamente con una base de datos local SQLite (`data/simacmart.db`) con datos semilla listos para probar.
* ❌ **NO necesitas IDEs pesados:** Funciona directo desde la consola o con dos clics.

Solo necesitas **3 herramientas gratuitas**:
1. **Git** (para descargar y gestionar el código).
2. **Java JDK 17 o 21 LTS** (el motor para compilar y correr el backend Kotlin).
3. **Python 3** (o Node.js) (para servir el frontend web en el puerto 3000).

---

## Paso 1: Instalar los 3 programas base en tu PC nueva

Elige **una** de las dos opciones siguientes:

### Opción A: Automática con `winget` (La más rápida en Windows 10/11)

Windows 10 y 11 traen un gestor oficial de programas llamado `winget`.
1. Presiona la tecla `Windows`, escribe **PowerShell**, haz clic derecho y selecciona **"Ejecutar como administrador"**.
2. Copia y pega el siguiente comando completo y presiona `Enter`:

```powershell
winget install Git.Git EclipseAdoptium.Temurin.17.JDK Python.Python.3.11 --accept-source-agreements --accept-package-agreements
```

3. Espera 2 o 3 minutos a que descargue e instale todo automáticamente.
4. **Cierra esa ventana de PowerShell.** (¡Importante para que se actualicen las rutas del sistema!).

---

### Opción B: Manual paso a paso desde el navegador

Si prefieres descargar los instaladores tú mismo con el ratón:

#### 1. Instalar Git para Windows
* **Enlace oficial:** [https://git-scm.com/download/win](https://git-scm.com/download/win)
* Haz clic en **"Click here to download"** (versión de 64 bits para Windows).
* Abre el instalador descargado (`Git-...-64-bit.exe`).
* Presiona **Next** en todas las pantallas dejando las opciones recomendadas por defecto, y finalmente haz clic en **Install**.

#### 2. Instalar Java JDK 17 o 21 (Eclipse Temurin)
* **Enlace oficial:** [https://adoptium.net/temurin/releases/?version=17](https://adoptium.net/temurin/releases/?version=17)
* En *Operating System* elige **Windows**, en *Architecture* elige **x64**, y descarga el archivo con extensión **.msi** (instalador de Windows).
* Abre el instalador `.msi`.
* ⚠️ **MUY IMPORTANTE:** En la pantalla donde te muestra los componentes a instalar, asegúrate de que las opciones:
  * **"Set JAVA_HOME variable"**
  * **"Add to PATH"**  
  estén seleccionadas con la opción *"Will be installed on local hard drive"*. (Vienen listas por defecto).
* Haz clic en **Next** e **Install**.

#### 3. Instalar Python 3 (para el servidor web)
* **Enlace oficial:** [https://www.python.org/downloads/](https://www.python.org/downloads/)
* Haz clic en el botón amarillo grande **"Download Python 3.x.x"**.
* Abre el archivo descargado (`python-3.x.x-amd64.exe`).
* 🚨 **EL ERROR #1 DE PRINCIPIANTES:** En la primerísima pantalla del instalador, en la parte inferior, verás una casilla desmarcada que dice:  
  👉 ☑ **"Add python.exe to PATH"**  
  **¡MÁRCALA OBLIGATORIAMENTE!** Si no la marcas, Windows no sabrá dónde está Python.
* Ahora haz clic en **"Install Now"**. Al terminar, presiona **Close**.

---

## Paso 2: Verificar que tu PC está lista

Abre una **NUEVA** ventana de PowerShell o CMD (presiona `Windows + R`, escribe `powershell` y presiona `Enter`).

Escribe uno a uno estos 3 comandos para comprobar:

```powershell
git --version
java -version
python --version
```

**¿Qué deberías ver en tu pantalla?**
* `git version 2.x.x`
* `openjdk version "17.0.x"` (o `21.0.x`)
* `Python 3.x.x`

Si los tres te devuelven su versión, **¡felicidades, tu computadora tiene todo lo necesario!**

---

## Paso 3: Descargar el proyecto

En tu consola de PowerShell, elige una carpeta donde quieras guardar el proyecto (por ejemplo en `C:\Proyectos`):

```powershell
mkdir C:\Proyectos
cd C:\Proyectos
git clone https://github.com/tu-usuario/SIMACMART-Patrones.git
cd SIMACMART-Patrones
```

*(Si no tienes cuenta de GitHub configurada o descargaste el proyecto como archivo `.ZIP`, simplemente descomprímelo en `C:\Proyectos\SIMACMART-Patrones` y abre esa carpeta en el Explorador de Archivos de Windows).*

---

## Paso 4: Encender el sistema

Hemos preparado dos archivos lanzadores directos para que no tengas que escribir comandos largos:

```
SIMACMART-Patrones/
├── iniciar_backend.bat   <--- 1. Doble clic aquí primero
├── iniciar_frontend.bat  <--- 2. Doble clic aquí segundo
```

### 1. Iniciar el Backend (Servidor Kotlin en puerto 8080)
1. Ve a la carpeta `C:\Proyectos\SIMACMART-Patrones` en tu Explorador de Archivos.
2. Haz **doble clic en `iniciar_backend.bat`**.
3. Se abrirá una ventana de consola negra:
   * **La primera vez que lo ejecutes:** Tardará entre 1 y 2 minutos porque descargará automáticamente los paquetes de Gradle y las librerías necesarias. Solo requiere internet esa primera vez.
   * **Cuando esté listo:** Verás el banner de bienvenida con el siguiente mensaje final:
     ```text
     =================================================================
     >>> INICIANDO SISTEMA MONOLITO MODULAR SIMACMART (UPT)
     >>> Arquitectura Hexagonal + Patrones GoF (Strategy, Adapter, CoR, UoW)
     =================================================================
     >>> [DatabaseConfig] Conexión establecida con éxito (SQLite local).
     >>> [DatabaseInitializer] Tablas y datos semilla verificados.
     >>> [ServidorHttpMonolito] Servidor iniciado en http://localhost:8080
     ```
   * ⚠️ **NO cierres esta ventana**, déjala abierta en segundo plano minimizada.

### 2. Iniciar el Frontend (Dashboard Web en puerto 3000)
1. En la misma carpeta, haz **doble clic en `iniciar_frontend.bat`**.
2. Este script detectará Python en tu sistema, levantará el servidor web en el puerto 3000 y **abrirá automáticamente tu navegador web** en `http://localhost:3000`.

---

## Paso 5: Acceder y usar el Dashboard

Si tu navegador no se abrió solo, abre **Google Chrome**, **Microsoft Edge** o **Firefox** e ingresa a:

👉 **`http://localhost:3000`**

### ¿Qué debes observar en pantalla?
1. **Estado de Conexión (Esquina inferior izquierda):**  
   Verás un indicador verde brillante: 🟢 **"Backend Conectado"**. Significa que la SPA en JavaScript está comunicándose en tiempo real con el backend en Kotlin en `http://localhost:8080`.
2. **Las Tarjetas de Telemetría:**
   * **Captación Acumulada Hoy:** Marca `42.8 Litros` (agua total del día acreditada en reservorio).
   * **Peso en Tina Colectora:** Marca `1 kg` (peso instantáneo actual en la balanza de 50 kg).
   * **Condiciones Ambientales:** Humedad relativa, temperatura y velocidad de viento (estación Ecowitt).
3. **Módulos Navegables del Menú Lateral:**
   * 📊 **Monitoreo en Vivo:** Telemetría en tiempo real y gráficos en vivo.
   * 📈 **Historial y Filtros:** Registro histórico por rangos de fecha y variables hídricas.
   * 💧 **Eventos de Vaciado:** Demostración de la regla de negocio **RN-03** (Arqueo Hídrico tras la activación del sifón automático).
   * 🧠 **Predicción Hídrica:** Demostración del patrón **Strategy** (alternar en vivo entre el modelo de *Machine Learning* ONNX y la *Línea Base Histórica*).
   * ⚙️ **Gestión de Nodos:** Estado de salud del nodo Pachía #01 y watchdog de desconexión de 10 minutos (Diagrama 4).

---

## 🧪 Ejecutar Pruebas Automatizadas (Unit Tests)

Si el profesor o evaluador desea verificar que todos los patrones de diseño y reglas de negocio pasan las pruebas unitarias:

1. Abre PowerShell en la carpeta `C:\Proyectos\SIMACMART-Patrones\SIMACMART-Backend`.
2. Ejecuta:
   ```powershell
   .\gradlew.bat test
   ```
3. Verás la salida:
   ```text
   BUILD SUCCESSFUL in 12s
   5 actionable tasks: 5 executed
   ```
   *(Todos los tests de Chain of Responsibility, Strategy, Unit of Work, Idempotencia y Observer ejecutados al 100%).*

---

## Solución de problemas comunes en computadoras recién formateadas

### 1. Error: *"La ejecución de scripts está deshabilitada en este sistema"* (PowerShell)
En Windows nuevos, PowerShell viene por seguridad con la ejecución de scripts bloqueada.  
* **Solución rápida:** Abre PowerShell como Administrador y escribe:
  ```powershell
  Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned -Force
  ```

### 2. Error: *"'java' no se reconoce como un comando interno o externo"*
Significa que acabas de instalar Java pero la consola que tienes abierta todavía no se enteró del cambio de variables de entorno.  
* **Solución:** Simplemente **cierra todas las ventanas de consola** y vuelve a abrirlas.  
* Si persiste, reinicia la computadora para que Windows recargue el `PATH` del sistema.

### 3. Error: *"'python' no se reconoce como un comando interno o externo"*
Al instalar Python olvidaste marcar la casilla *"Add python.exe to PATH"*.  
* **Solución:**
  1. Vuelve a ejecutar el instalador descargado de Python.
  2. Haz clic en **"Modify"**.
  3. Haz clic en **Next** hasta llegar a la pantalla *"Advanced Options"*.
  4. Marca la casilla **"Add Python to environment variables"** y dale a **Install**.
  5. Cierra y reabre tu terminal.

### 4. Alerta de Seguridad de Windows Defender Firewall
La primera vez que arranques `iniciar_backend.bat` o `iniciar_frontend.bat`, es posible que Windows muestre un cartel azul diciendo:  
*"Windows Defender Firewall bloqueó algunas características de esta aplicación (OpenJDK Platform binary o Python)"*.  
* **Solución:** Marca la casilla **"Redes privadas, como la red doméstica o del trabajo"** y haz clic en **"Permitir acceso"**.

### 5. Conflicto de Puertos (El puerto 8080 o 3000 ya están ocupados)
Si tienes otro programa (como Oracle XE, Tomcat, VMware o Skype) usando el puerto 8080:  
* Puedes cambiar el puerto del backend temporalmente antes de ejecutar:
  ```powershell
  $env:PORT="9090"
  cd SIMACMART-Backend
  .\gradlew.bat run
  ```
* Y en `SIMACMART-Frontend/js/config.js` cambias:
  `API_BASE_URL: 'http://localhost:9090/api/v1'`

---

## 🎯 Resumen de Comandos para Usuarios Avanzados (Cheat Sheet)

Si ya tienes las herramientas instaladas y prefieres hacerlo por comandos en terminal:

```bash
# Terminal 1 - Backend (Kotlin):
cd SIMACMART-Backend
./gradlew run

# Terminal 2 - Frontend (SPA):
cd SIMACMART-Frontend
python -m http.server 3000

# Navegador:
# http://localhost:3000
```
