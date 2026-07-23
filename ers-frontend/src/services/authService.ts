import apiClient from './apiClient';
import { API_PATHS } from '../shared/utils/constants';
import { AuthUser, LoginCredentials, RegistrationPayload } from '../types';

export const authService = {
  async login(credentials: LoginCredentials): Promise<AuthUser> {
    const response = await apiClient.post<AuthUser>(API_PATHS.login, credentials);
    return response.data;
  },

  async register(payload: RegistrationPayload): Promise<string> {
    const response = await apiClient.post<{ message?: string } | string>(API_PATHS.users, payload);
    const data = response.data;
    if (typeof data === 'string' && data.trim()) {
      return data;
    }
    if (data && typeof data === 'object' && typeof data.message === 'string') {
      return data.message;
    }
    return 'Registration successful';
  },

  async logout(): Promise<void> {
    try {
      await apiClient.post(API_PATHS.logout);
    } catch {
      // Clear local session even if the server call fails.
    }
  },
};
