/**
 * Componentes de renderizado para tablas de vaciados e historial
 */
export function renderVaciadosTable() {
  const tbody = document.getElementById('vaciadosTableBody');
  if (!tbody) return;

  const eventos = [
    { id: 'EV-104', fecha: '2026-10-24 07:15:22', nodo: 'Nodo Pachía #01', antes: 48.5, despues: 0.2, litros: 48.3, tipo: 'Vaciado Manual' },
    { id: 'EV-103', fecha: '2026-10-23 06:40:11', nodo: 'Nodo Pachía #01', antes: 47.8, despues: 0.1, litros: 47.7, tipo: 'Vaciado Manual' },
    { id: 'EV-102', fecha: '2026-10-22 08:02:45', nodo: 'Nodo Pachía #01', antes: 49.1, despues: 0.3, litros: 48.8, tipo: 'Vaciado Automático' },
    { id: 'EV-101', fecha: '2026-10-21 07:10:00', nodo: 'Nodo Pachía #01', antes: 46.2, despues: 0.2, litros: 46.0, tipo: 'Vaciado Manual' },
    { id: 'EV-100', fecha: '2026-10-20 06:55:18', nodo: 'Nodo Pachía #01', antes: 45.0, despues: 0.1, litros: 44.9, tipo: 'Vaciado Manual' }
  ];

  tbody.innerHTML = eventos.map(e => `
    <tr>
      <td><strong>${e.id}</strong></td>
      <td>${e.fecha}</td>
      <td>${e.nodo}</td>
      <td>${e.antes.toFixed(2)} kg</td>
      <td>${e.despues.toFixed(2)} kg</td>
      <td><span class="badge-tag badge-ok">+${e.litros.toFixed(1)} L</span></td>
      <td><span class="badge-tag badge-vaciado">${e.tipo}</span></td>
    </tr>
  `).join('');
}

export function renderHistorialTable() {
  const tbody = document.getElementById('historialTableBody');
  if (!tbody) return;

  const lecturas = [
    { fecha: '2026-10-24 22:00', nodo: 'Nodo Pachía #01', peso: 18.4, litros: 42.8, hr: 94.2, temp: 13.8, viento: 3.5, estado: 'Válida' },
    { fecha: '2026-10-24 20:00', nodo: 'Nodo Pachía #01', peso: 16.2, litros: 40.6, hr: 91.5, temp: 14.5, viento: 3.1, estado: 'Válida' },
    { fecha: '2026-10-24 18:00', nodo: 'Nodo Pachía #01', peso: 14.8, litros: 39.2, hr: 86.0, temp: 15.2, viento: 2.8, estado: 'Válida' },
    { fecha: '2026-10-24 16:00', nodo: 'Nodo Pachía #01', peso: 13.9, litros: 38.3, hr: 78.4, temp: 16.0, viento: 3.4, estado: 'Válida' },
    { fecha: '2026-10-24 14:00', nodo: 'Nodo Pachía #01', peso: 13.5, litros: 37.9, hr: 68.1, temp: 17.5, viento: 4.1, estado: 'Válida' },
    { fecha: '2026-10-24 12:00', nodo: 'Nodo Pachía #01', peso: 13.2, litros: 37.6, hr: 65.0, temp: 18.2, viento: 3.9, estado: 'Válida' }
  ];

  tbody.innerHTML = lecturas.map(l => `
    <tr>
      <td>${l.fecha}</td>
      <td>${l.nodo}</td>
      <td>${l.peso.toFixed(1)}</td>
      <td><strong>${l.litros.toFixed(1)}</strong></td>
      <td>${l.hr.toFixed(1)}%</td>
      <td>${l.temp.toFixed(1)}°C</td>
      <td>${l.viento.toFixed(1)}</td>
      <td><span class="badge-tag badge-ok">${l.estado}</span></td>
    </tr>
  `).join('');
}

export function renderNodesGrid() {
  const container = document.getElementById('nodesContainer');
  if (!container) return;

  const nodos = [
    {
      id: 1,
      nombre: 'Nodo Pachía Central #01',
      localidad: 'Pachía (Tacna) - Sector Calientes',
      coords: '-17.8942° S, -70.1523° W',
      tina: '50.0 kg',
      dispositivos: [
        { tipo: 'BALANZA', codigo: 'BAL-ESP32-01', estado: 'Online (WiFi 4G)' },
        { tipo: 'ESTACION', codigo: 'ECOWITT-GW1100', estado: 'Online (HTTP Upload)' }
      ]
    },
    {
      id: 2,
      nombre: 'Nodo Pachía Experimental #02',
      localidad: 'Pachía (Tacna) - Cerro Blanco',
      coords: '-17.8810° S, -70.1415° W',
      tina: '50.0 kg',
      dispositivos: [
        { tipo: 'BALANZA', codigo: 'BAL-ESP32-02', estado: 'Standby / Batería 92%' },
        { tipo: 'ESTACION', codigo: 'ECOWITT-WS69', estado: 'Online (HTTP Upload)' }
      ]
    }
  ];

  container.innerHTML = nodos.map(n => `
    <div class="node-card">
      <div class="node-card-header">
        <h4>${n.nombre}</h4>
        <span class="badge-tag badge-ok">Activo</span>
      </div>
      <p style="font-size: 0.82rem; color: var(--text-secondary); margin-bottom: 8px;">📍 ${n.localidad}</p>
      <p style="font-size: 0.76rem; color: var(--text-muted); font-family: var(--font-mono); margin-bottom: 12px;">Coordenadas: ${n.coords}</p>
      <div style="border-top: var(--glass-border); padding-top: 10px;">
        <small style="display:block; margin-bottom: 6px; font-weight:700;">Dispositivos Asociados:</small>
        ${n.dispositivos.map(d => `
          <div style="display:flex; justify-content:space-between; font-size:0.78rem; padding:4px 0;">
            <span><strong>${d.tipo}:</strong> ${d.codigo}</span>
            <span style="color: var(--accent-emerald);">${d.estado}</span>
          </div>
        `).join('')}
      </div>
    </div>
  `).join('');
}
