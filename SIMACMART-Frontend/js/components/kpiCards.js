/**
 * Componente reactivo para actualizar tarjetas KPI con animaciones suaves
 */
export function updateKpiCards(data) {
  if (!data) return;

  const setVal = (id, val) => {
    const el = document.getElementById(id);
    if (el) el.textContent = val !== undefined && val !== null ? val : '--';
  };

  setVal('kpiLitrosHoy', data.litrosHoy);
  setVal('kpiPesoBalanza', data.pesoActualBalanzaKg);
  setVal('kpiHumedad', data.humedadPct);
  setVal('kpiTemp', data.temperaturaC);
  setVal('kpiViento', data.vientoMs);
  setVal('kpiRadiacion', data.radiacionWm2);
  setVal('kpiRocio', data.puntoRocioC || (data.temperaturaC - ((100 - data.humedadPct) / 5)).toFixed(1));

  // Capacidad y ocupación de la tina
  const capacidad = 50.0;
  const ocupacion = ((data.pesoActualBalanzaKg / capacidad) * 100).toFixed(0);
  setVal('tinaPct', `${ocupacion}%`);

  // Condición de niebla según humedad
  const condEl = document.getElementById('nieblaCondition');
  if (condEl) {
    if (data.humedadPct >= 90) {
      condEl.textContent = '🌫️ Niebla densa activa (condensación alta)';
      condEl.style.color = 'var(--accent-cyan)';
    } else if (data.humedadPct >= 75) {
      condEl.textContent = 'Condición moderada para niebla';
      condEl.style.color = 'var(--text-muted)';
    } else {
      condEl.textContent = 'Ambiente seco / Niebla dispersa';
      condEl.style.color = 'var(--accent-amber)';
    }
  }
}
