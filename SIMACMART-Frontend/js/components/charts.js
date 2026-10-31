/**
 * Inicialización y gestión de gráficos interactivos con Chart.js
 */
let captacionChartInstance = null;
let hourlyBarChartInstance = null;
let comparativaChartInstance = null;
let predictionChartInstance = null;

// Configuración global de estilos oscuros para Chart.js
if (window.Chart) {
  Chart.defaults.color = 'hsl(215, 20%, 75%)';
  Chart.defaults.font.family = "'Plus Jakarta Sans', sans-serif";
  Chart.defaults.plugins.tooltip.backgroundColor = 'hsl(222, 45%, 10%)';
  Chart.defaults.plugins.tooltip.borderColor = 'hsla(220, 30%, 45%, 0.3)';
  Chart.defaults.plugins.tooltip.borderWidth = 1;
  Chart.defaults.plugins.tooltip.padding = 10;
}

export function initCharts() {
  initMainCaptacionChart();
  initHourlyBarChart();
  initComparativaChart();
  initPredictionChart();
}

function initMainCaptacionChart() {
  const ctx = document.getElementById('captacionChart')?.getContext('2d');
  if (!ctx) return;

  const horas = ['00:00', '02:00', '04:00', '06:00', '08:00', '10:00', '12:00', '14:00', '16:00', '18:00', '20:00', '22:00'];
  const litros = [2.1, 8.4, 19.5, 34.2, 38.6, 39.8, 40.1, 40.5, 41.2, 42.0, 42.8, 43.5];
  const peso = [2.1, 8.4, 19.5, 34.2, 12.0, 13.2, 13.5, 13.9, 14.6, 15.4, 18.4, 19.1]; // Se nota vaciado a las 08:00
  const humedad = [88, 94, 98, 97, 85, 72, 68, 65, 78, 86, 92, 95];

  captacionChartInstance = new Chart(ctx, {
    type: 'line',
    data: {
      labels: horas,
      datasets: [
        {
          label: 'Litros Captados Acumulados',
          data: litros,
          borderColor: '#0fe3ff',
          backgroundColor: 'rgba(15, 227, 255, 0.12)',
          fill: true,
          tension: 0.35,
          yAxisID: 'yLitros',
          borderWidth: 2.5,
          pointRadius: 3
        },
        {
          label: 'Peso Tina (kg)',
          data: peso,
          borderColor: '#f7b928',
          borderDash: [5, 5],
          tension: 0.2,
          yAxisID: 'yLitros',
          borderWidth: 2,
          pointRadius: 2
        },
        {
          label: 'Humedad Relativa (%)',
          data: humedad,
          borderColor: '#1f8fff',
          tension: 0.35,
          yAxisID: 'yHumedad',
          borderWidth: 2,
          pointRadius: 2
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        x: { grid: { color: 'rgba(255, 255, 255, 0.05)' } },
        yLitros: {
          type: 'linear',
          position: 'left',
          title: { display: true, text: 'Litros / kg' },
          grid: { color: 'rgba(255, 255, 255, 0.05)' }
        },
        yHumedad: {
          type: 'linear',
          position: 'right',
          min: 40,
          max: 100,
          title: { display: true, text: 'Humedad (%)' },
          grid: { drawOnChartArea: false }
        }
      }
    }
  });
}

function initHourlyBarChart() {
  const ctx = document.getElementById('hourlyBarChart')?.getContext('2d');
  if (!ctx) return;

  hourlyBarChartInstance = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: ['00-02', '02-04', '04-06', '06-08', '08-10', '10-12', '12-14', '14-16', '16-18', '18-20', '20-22', '22-24'],
      datasets: [{
        label: 'Litros por tramo',
        data: [2.1, 6.3, 11.1, 14.7, 4.4, 1.2, 0.3, 0.4, 0.7, 0.8, 0.8, 1.2],
        backgroundColor: 'rgba(30, 203, 135, 0.65)',
        borderColor: '#1ecb87',
        borderWidth: 1,
        borderRadius: 6
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        x: { grid: { display: false } },
        y: {
          title: { display: true, text: 'Litros (L)' },
          grid: { color: 'rgba(255, 255, 255, 0.05)' }
        }
      }
    }
  });
}

function initComparativaChart() {
  const ctx = document.getElementById('comparativaChart')?.getContext('2d');
  if (!ctx) return;

  comparativaChartInstance = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: ['Lun 19', 'Mar 20', 'Mié 21', 'Jue 22', 'Vie 23', 'Sáb 24', 'Dom 25'],
      datasets: [
        {
          label: 'Nodo Pachía #01 (Malla Raschel 35%)',
          data: [38.5, 41.2, 35.8, 48.2, 44.1, 42.8, 45.0],
          backgroundColor: 'rgba(15, 227, 255, 0.7)',
          borderRadius: 6
        },
        {
          label: 'Nodo Pachía #02 (Malla Raschel 50%)',
          data: [31.0, 34.5, 29.2, 41.0, 37.8, 36.2, 39.1],
          backgroundColor: 'rgba(160, 102, 255, 0.7)',
          borderRadius: 6
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        x: { grid: { color: 'rgba(255, 255, 255, 0.05)' } },
        y: {
          title: { display: true, text: 'Litros Totales por Día' },
          grid: { color: 'rgba(255, 255, 255, 0.05)' }
        }
      }
    }
  });
}

function initPredictionChart() {
  const ctx = document.getElementById('predictionChart')?.getContext('2d');
  if (!ctx) return;

  predictionChartInstance = new Chart(ctx, {
    type: 'line',
    data: {
      labels: ['Hoy (Real)', 'Mañana +1d', '+2d', '+3d', '+4d', '+5d', '+6d'],
      datasets: [
        {
          label: 'Captación Estimada (Litros)',
          data: [42.8, 45.2, 44.0, 39.5, 48.1, 51.3, 47.0],
          borderColor: '#a066ff',
          backgroundColor: 'rgba(160, 102, 255, 0.15)',
          fill: true,
          tension: 0.35,
          borderWidth: 3,
          pointRadius: 4
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        x: { grid: { color: 'rgba(255, 255, 255, 0.05)' } },
        y: {
          title: { display: true, text: 'Litros Proyectados' },
          grid: { color: 'rgba(255, 255, 255, 0.05)' }
        }
      }
    }
  });
}
