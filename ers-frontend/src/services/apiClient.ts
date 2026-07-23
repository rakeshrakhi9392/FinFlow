import axios from 'axios';
import { API_BASE_URL, ROUTES, SESSION_USER_KEY } from '../shared/utils/constants';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error?.response?.status === 401) {
      sessionStorage.removeItem(SESSION_USER_KEY);
      if (typeof window !== 'undefined') {
        const path = window.location.pathname;
        if (path !== ROUTES.login && path !== ROUTES.register) {
          window.location.assign(ROUTES.login);
        }
      }
    }
    return Promise.reject(error);
  }
);

export default apiClient;
