import { CONFIG } from './config.js';
import { ApiClient } from './api/client.js';
import { AuthStore } from './store/authStore.js';
import { updateKpiCards } from './components/kpiCards.js';
import { initCharts } from './components/charts.js';
import { renderVaciadosTable, renderHistorialTable, renderNodesGrid } from './components/eventTable.js';
import { exportarDatosACSV } from './components/exportCsv.js';

let pollingTimer = null;
let lastSyncTimestamp = Date.now();

document.addEventListener('DOMContentLoaded', () => {
  setupNavigation();
  setupLiveClock();
  setupModals();
  setupActions();
  
  // Inicialización de componentes gráficos
  initCharts();
  renderVaciadosTable();
  renderHistorialTable();
  renderNodesGrid();

  // Ciclo de actualización en vivo
  fetchDashboardData();
  startPolling();
});

/**
 * Enrutador SPA basado en hashes de URL
 */
function setupNavigation() {
  const navLinks = document.querySelectorAll('.sidebar-nav .nav-item');
  const panels = document.querySelectorAll('.view-panel');

  function navigateTo(route) {
    navLinks.forEach(link => {
      if (link.dataset.route === route) {
        link.classList.add('active');
      } else {
        link.classList.remove('active');
      }
    });

    panels.forEach(panel => {
      if (panel.id === `view-${route}`) {
        panel.classList.add('active');
      } else {
        panel.classList.remove('active');
      }
    });

    // Cerrar sidebar en móvil al navegar
    document.getElementById('sidebar')?.classList.remove('open');
  }

  window.addEventListener('hashchange', () => {
    const route = window.location.hash.replace('#/', '') || 'dashboard';
    navigateTo(route);
  });

  // Ruta inicial
  const initialRoute = window.location.hash.replace('#/', '') || 'dashboard';
  navigateTo(initialRoute);

  // Botón móvil
  document.getElementById('mobileMenuBtn')?.addEventListener('click', () => {
    document.getElementById('sidebar')?.classList.toggle('open');
  });
}

/**
 * Consulta de telemetría al endpoint /dashboard/resumen
 */
async function fetchDashboardData() {
  const nodeId = document.getElementById('nodeSelect')?.value || CONFIG.DEFAULT_NODE_ID;
  const data = await ApiClient.request(`/dashboard/resumen?idUnidad=${nodeId}`);
  
  if (data) {
    updateKpiCards(data);
    lastSyncTimestamp = Date.now();
    updateSyncCounter();
  }
}

/**
 * Ciclo de sondeo regular
 */
function startPolling() {
  if (pollingTimer) clearInterval(pollingTimer);
  pollingTimer = setInterval(() => {
    fetchDashboardData();
  }, CONFIG.POLL_INTERVAL_MS);
}

function updateSyncCounter() {
  const agoEl = document.getElementById('lastSyncAgo');
  if (agoEl) {
    const secs = Math.floor((Date.now() - lastSyncTimestamp) / 1000);
    agoEl.textContent = `${secs}s`;
  }
}

/**
 * Reloj en vivo en topbar
 */
function setupLiveClock() {
  const clockEl = document.getElementById('liveClock');
  setInterval(() => {
    if (clockEl) {
      const now = new Date();
      clockEl.textContent = now.toLocaleTimeString('es-PE', { hour12: false });
    }
    updateSyncCounter();
  }, 1000);
}

/**
 * Configuración de eventos de botones
 */
function setupActions() {
  document.getElementById('manualRefreshBtn')?.addEventListener('click', () => {
    fetchDashboardData();
  });

  document.getElementById('quickExportBtn')?.addEventListener('click', () => {
    exportarDatosACSV('general');
  });

  document.getElementById('btnExportHistorialCsv')?.addEventListener('click', () => {
    exportarDatosACSV('historial');
  });

  document.getElementById('nodeSelect')?.addEventListener('change', () => {
    fetchDashboardData();
  });
}

/**
 * Modal de Login y Autenticación
 */
function setupModals() {
  const modal = document.getElementById('loginModal');
  const authBtn = document.getElementById('authActionBtn');
  const closeBtn = document.getElementById('closeLoginModal');
  const cancelBtn = document.getElementById('cancelLoginBtn');
  const loginForm = document.getElementById('loginForm');

  authBtn?.addEventListener('click', () => {
    if (AuthStore.isAuthenticated()) {
      AuthStore.clearSession();
      authBtn.textContent = 'Iniciar Sesión';
      document.getElementById('userName').textContent = 'Operador Pachía';
      document.getElementById('userRole').textContent = 'OPERADOR';
    } else {
      modal?.classList.add('open');
    }
  });

  closeBtn?.addEventListener('click', () => modal?.classList.remove('open'));
  cancelBtn?.addEventListener('click', () => modal?.classList.remove('open'));

  loginForm?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const usuario = document.getElementById('inputUsuario').value;
    const password = document.getElementById('inputPassword').value;

    const res = await ApiClient.request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ usuario, password })
    });

    if (res && (res.token || res.exito)) {
      AuthStore.setSession(res.token || 'jwt_simacmart_session', res.usuario || { usuario, rol: 'ADMINISTRADOR' });
      document.getElementById('userName').textContent = usuario;
      document.getElementById('userRole').textContent = res.usuario?.rol || 'ADMINISTRADOR';
      authBtn.textContent = 'Cerrar Sesión';
      modal?.classList.remove('open');
    }
  });
}
