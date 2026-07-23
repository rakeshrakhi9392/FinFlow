import apiClient from './apiClient';
import { API_PATHS } from '../shared/utils/constants';
import { AuthUser, LoginCredentials, RegistrationPayload } from '../types';

export const authService = {
  async login(credentials: LoginCredentials): Promise<AuthUser> {
    const response = await apiClient.post<AuthUser>(API_PATHS.login, credentials);
    return response.data;
  },

  async register(payload: RegistrationPayload): Promise<string> {
    const response = await apiClient.post<string>(API_PATHS.users, payload);
    return typeof response.data === 'string' ? response.data : 'Registration successful';
  },
};
