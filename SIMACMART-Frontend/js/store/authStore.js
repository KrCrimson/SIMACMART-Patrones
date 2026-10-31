/**
 * Manejo de estado de autenticación y token JWT en localStorage
 */
const TOKEN_KEY = 'simacmart_auth_token';
const USER_KEY = 'simacmart_auth_user';

export const AuthStore = {
  getToken() {
    return localStorage.getItem(TOKEN_KEY);
  },

  getUser() {
    try {
      const data = localStorage.getItem(USER_KEY);
      return data ? JSON.parse(data) : { id: 1, usuario: 'Operador Pachía', rol: 'OPERADOR' };
    } catch {
      return { id: 1, usuario: 'Operador Pachía', rol: 'OPERADOR' };
    }
  },

  setSession(token, user) {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  },

  clearSession() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  },

  isAuthenticated() {
    return !!this.getToken();
  }
};
