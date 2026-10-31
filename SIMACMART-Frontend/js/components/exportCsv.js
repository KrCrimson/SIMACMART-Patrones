/**
 * Utilidad de exportación de datos a archivo CSV (UC-09)
 */
export function exportarDatosACSV(tipo = 'general') {
  const headers = ['Fecha_Hora', 'Nodo_ID', 'Nodo_Nombre', 'Peso_Balanza_Kg', 'Litros_Captados', 'Humedad_Pct', 'Temperatura_C', 'Viento_Ms', 'Radiacion_Wm2'];
  
  // Datos consolidados para exportar
  const rows = [
    ['2026-10-24 22:00:00', '1', 'Nodo Pachia 1', '18.4', '42.8', '94.2', '13.8', '3.5', '0'],
    ['2026-10-24 20:00:00', '1', 'Nodo Pachia 1', '16.2', '40.6', '91.5', '14.5', '3.1', '0'],
    ['2026-10-24 18:00:00', '1', 'Nodo Pachia 1', '14.8', '39.2', '86.0', '15.2', '2.8', '45'],
    ['2026-10-24 16:00:00', '1', 'Nodo Pachia 1', '13.9', '38.3', '78.4', '16.0', '3.4', '210'],
    ['2026-10-24 14:00:00', '1', 'Nodo Pachia 1', '13.5', '37.9', '68.1', '17.5', '4.1', '520'],
    ['2026-10-24 12:00:00', '1', 'Nodo Pachia 1', '13.2', '37.6', '65.0', '18.2', '3.9', '780'],
    ['2026-10-24 10:00:00', '1', 'Nodo Pachia 1', '12.8', '37.2', '72.0', '16.5', '3.0', '650'],
    ['2026-10-24 08:00:00', '1', 'Nodo Pachia 1', '12.0', '36.4', '85.4', '14.2', '2.6', '310'],
    ['2026-10-24 06:00:00', '1', 'Nodo Pachia 1', '34.2', '34.2', '97.0', '12.5', '3.2', '10'],
    ['2026-10-24 04:00:00', '1', 'Nodo Pachia 1', '19.5', '19.5', '98.2', '12.0', '3.5', '0']
  ];

  let csvContent = 'data:text/csv;charset=utf-8,﻿';
  csvContent += headers.join(',') + '\r\n';
  rows.forEach(r => {
    csvContent += r.join(',') + '\r\n';
  });

  const encodedUri = encodeURI(csvContent);
  const link = document.createElement('a');
  link.setAttribute('href', encodedUri);
  link.setAttribute('download', `SIMACMART_Reporte_${new Date().toISOString().slice(0, 10)}.csv`);
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}
