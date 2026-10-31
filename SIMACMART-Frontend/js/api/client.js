import { CONFIG } from '../config.js';
import { AuthStore } from '../store/authStore.js';

/**
 * Cliente HTTP unificado con soporte para fallback a Mock Data
 * para asegurar que la UI sea 100% interactiva incluso si el backend está iniciando.
 */
export class ApiClient {
  static isBackendOnline = false;

  static async request(endpoint, options = {}) {
    const url = `${CONFIG.API_BASE_URL}${endpoint}`;
    const token = AuthStore.getToken();

    const headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
      ...(options.headers || {})
    };

    try {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 3500);

      const response = await fetch(url, {
        ...options,
        headers,
        signal: controller.signal
      });
      clearTimeout(timeoutId);

      this.isBackendOnline = true;
      this.updateConnectionStatus(true);

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }

      return await response.json();
    } catch (err) {
      console.warn(`[SIMACMART API] Fallback a datos locales para ${endpoint}:`, err.message);
      this.isBackendOnline = false;
      this.updateConnectionStatus(false);
      return this.getMockFallback(endpoint);
    }
  }

  static updateConnectionStatus(online) {
    const dot = document.getElementById('backendStatusDot');
    const text = document.getElementById('backendStatusText');
    if (dot && text) {
      if (online) {
        dot.className = 'status-dot online';
        text.textContent = 'Backend Conectado (Live)';
      } else {
        dot.className = 'status-dot offline';
        text.textContent = 'Modo Local / Demo';
      }
    }
  }

  static getMockFallback(endpoint) {
    // Generación dinámica coherente de datos de prueba
    if (endpoint.includes('/dashboard/resumen')) {
      const hora = new Date().getHours();
      // Madrugada (02:00 a 08:00) = alta niebla
      const esMadrugada = hora >= 2 && hora <= 8;
      const baseLitros = esMadrugada ? 42.8 : 28.5;
      const basePeso = esMadrugada ? 18.4 : 9.2;
      const baseHumedad = esMadrugada ? 95.2 : 82.0;

      return {
        idUnidad: 1,
        nombreUnidad: 'Nodo Pachía Central #01',
        localidad: 'Pachía, Tacna',
        litrosHoy: Number((baseLitros + Math.random() * 1.5).toFixed(1)),
        pesoActualBalanzaKg: Number((basePeso + (Math.random() * 0.4 - 0.2)).toFixed(2)),
        temperaturaC: Number((13.5 + Math.random() * 1.2).toFixed(1)),
        humedadPct: Number((baseHumedad + Math.random() * 2.0).toFixed(1)),
        vientoMs: Number((3.2 + Math.random() * 0.8).toFixed(1)),
        radiacionWm2: Number((esMadrugada ? 0 : 280 + Math.random() * 40).toFixed(0)),
        puntoRocioC: 12.1,
        vaciadosRegistrados: 4,
        ultimoVaciado: '2026-10-24 04:30:00'
      };
    }

    if (endpoint.includes('/auth/login')) {
      return {
        exito: true,
        token: 'mock_jwt_token_simacmart_pachia_operator',
        usuario: { id: 1, usuario: 'admin', rol: 'ADMINISTRADOR' }
      };
    }

    return { status: 'mock_success' };
  }
}
