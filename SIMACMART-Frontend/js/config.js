/**
 * SIMACMART Frontend Configuration
 */
export const CONFIG = {
  // URL base del backend Monolito Modular en Kotlin
  API_BASE_URL: 'http://localhost:8080/api/v1',
  
  // Intervalo de sondeo automático en milisegundos (5 segundos)
  POLL_INTERVAL_MS: 5000,
  
  // Versión del cliente
  VERSION: '1.0.0-PROD',
  
  // Nodo por defecto (Unidad Pachía 1)
  DEFAULT_NODE_ID: 1,
  
  // Factor de conversión de peso en balanza a litros de agua
  // Densidad del agua = 1.0 kg/L (aproximación volumétrica)
  KG_TO_LITERS: 1.0
};
