# SIMACMART - Frontend Desacoplado (Dashboard IoT)

Aplicación Web SPA (*Single Page Application*) para el **Sistema de Monitoreo y Análisis de Captación de Niebla en Atrapanieblas (SIMACMART)** en Pachía, Tacna.

Diseñada bajo los requisitos oficiales de arquitectura técnica:
- **Sin frameworks pesados** (HTML5 Semántico, CSS3 con tema oscuro hídrico y JavaScript Vanilla modular ES6).
- **Gráficos interactivos de series temporales** mediante [Chart.js](https://www.chartjs.org/).
- **Desacoplado del backend Kotlin** mediante llamadas REST seguras con soporte CORS.
- **Tolerancia y modo demo automático:** Si el backend Kotlin no está corriendo o está levantando, el cliente entra automáticamente en modo fallback para que la interfaz nunca se quede rota o vacía.

---

## 📂 Estructura del Proyecto

```text
SIMACMART-Frontend/
├── index.html                   # Shell SPA interactiva (Dashboard, Historial, Nodos, Vaciados, Predicción)
├── css/
│   ├── theme.css                # Paleta de colores HSL, glassmorphism, variables de diseño
│   ├── layout.css               # Estructura de navegación lateral (Sidebar) y topbar flotante
│   └── components.css           # Estilos para KPI cards, tablas responsivas, modales y botones
├── js/
│   ├── app.js                   # Enrutador por URL hash (#/dashboard, #/historial, etc.) y polling
│   ├── config.js                # Parámetros globales y URL del backend (http://localhost:8080/api/v1)
│   ├── api/
│   │   └── client.js            # Cliente fetch() con interceptor JWT y fallback a datos demo
│   ├── components/
│   │   ├── charts.js            # Configuración de 4 gráficos interactivos con Chart.js
│   │   ├── kpiCards.js          # Actualizador reactivo de tarjetas de telemetría
│   │   ├── eventTable.js        # Tablas dinámicas de eventos de vaciado e historial
│   │   └── exportCsv.js         # Generación y descarga directa de archivos CSV (UC-09)
│   └── store/
│       └── authStore.js         # Manejo de token de sesión y roles en localStorage
└── README.md
```

---

## 🚀 Cómo Ejecutar el Frontend

Dado que es una aplicación estándar basada en módulos ES6 (`type="module"`), debe ser servida a través de un servidor HTTP local para evitar bloqueos por políticas de seguridad del navegador (`file://`):

### Opción 1: Con Python (Más Rápido y sin instalar nada)
Abre una terminal en esta carpeta y ejecuta:
```bash
python -m http.server 3000
```
Luego abre tu navegador en:  
👉 **`http://localhost:3000`**

### Opción 2: Con la extensión Live Server de VS Code
1. Abre esta carpeta en VS Code.
2. Clic derecho en `index.html` → **"Open with Live Server"** (puerto predeterminado `5500`).

### Opción 3: Con Node.js (`npx serve`)
```bash
npx serve .
```

---

## 🔗 Conexión con el Backend Kotlin

1. Asegúrate de iniciar el backend Kotlin ejecutando `./gradlew run` en `SIMACMART-Backend`.
2. El backend levantará en `http://localhost:8080`.
3. El frontend detectará automáticamente el backend y el indicador cambiará a **"Backend Conectado (Live)"** con un punto verde esmeralda.
4. Si el backend está apagado, el frontend mostrará **"Modo Local / Demo"** con un punto ámbar y presentará datos simulados continuos para revisión estética y funcional.
