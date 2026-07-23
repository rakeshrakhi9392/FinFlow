import apiClient from './apiClient';
import { API_PATHS } from '../shared/utils/constants';
import { Reimbursement, VendorIntegration } from '../types';

export const vendorService = {
  async list(): Promise<VendorIntegration[]> {
    const response = await apiClient.get<VendorIntegration[]>(API_PATHS.vendorIntegrations);
    return response.data;
  },

  async get(reimbursementId: number): Promise<VendorIntegration> {
    const response = await apiClient.get<VendorIntegration>(API_PATHS.vendorIntegration(reimbursementId));
    return response.data;
  },

  async retry(reimbursementId: number): Promise<Reimbursement> {
    const response = await apiClient.post<Reimbursement>(API_PATHS.retryVendorSync(reimbursementId));
    return response.data;
  },
};
