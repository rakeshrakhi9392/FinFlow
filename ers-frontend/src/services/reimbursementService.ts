import apiClient from './apiClient';
import { API_PATHS } from '../shared/utils/constants';
import { CreateReimbursementPayload, Reimbursement } from '../types';

export const reimbursementService = {
  async getAll(): Promise<Reimbursement[]> {
    const response = await apiClient.get<Reimbursement[]>(API_PATHS.reimbursements);
    return response.data;
  },

  async getByUserId(userId: number): Promise<Reimbursement[]> {
    const response = await apiClient.get<Reimbursement[]>(API_PATHS.reimbursementsByUser(userId));
    return response.data;
  },

  async create(payload: CreateReimbursementPayload): Promise<Reimbursement> {
    const response = await apiClient.post<Reimbursement>(API_PATHS.reimbursements, payload);
    return response.data;
  },

  async approve(id: number): Promise<Reimbursement> {
    const response = await apiClient.post<Reimbursement>(API_PATHS.approveReimbursement(id));
    return response.data;
  },

  async deny(id: number): Promise<Reimbursement> {
    const response = await apiClient.post<Reimbursement>(API_PATHS.denyReimbursement(id));
    return response.data;
  },
};
