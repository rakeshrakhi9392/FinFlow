import apiClient from './apiClient';
import { API_PATHS } from '../shared/utils/constants';
import {
  ApprovalDecisionPayload,
  CreateReimbursementPayload,
  Reimbursement,
  WorkflowConfig,
} from '../types';

export const reimbursementService = {
  async getAll(): Promise<Reimbursement[]> {
    const response = await apiClient.get<Reimbursement[]>(API_PATHS.reimbursements);
    return response.data;
  },

  async getQueue(): Promise<Reimbursement[]> {
    const response = await apiClient.get<Reimbursement[]>(API_PATHS.reimbursementQueue);
    return response.data;
  },

  async getByUserId(userId: number): Promise<Reimbursement[]> {
    const response = await apiClient.get<Reimbursement[]>(API_PATHS.reimbursementsByUser(userId));
    return response.data;
  },

  async getById(id: number): Promise<Reimbursement> {
    const response = await apiClient.get<Reimbursement>(API_PATHS.reimbursementById(id));
    return response.data;
  },

  async create(payload: CreateReimbursementPayload): Promise<Reimbursement> {
    const response = await apiClient.post<Reimbursement>(API_PATHS.reimbursements, payload);
    return response.data;
  },

  async approve(id: number, payload: ApprovalDecisionPayload = {}): Promise<Reimbursement> {
    const response = await apiClient.post<Reimbursement>(API_PATHS.approveReimbursement(id), payload);
    return response.data;
  },

  async deny(id: number, payload: ApprovalDecisionPayload = {}): Promise<Reimbursement> {
    const response = await apiClient.post<Reimbursement>(API_PATHS.denyReimbursement(id), payload);
    return response.data;
  },

  async markPaid(id: number, payload: ApprovalDecisionPayload = {}): Promise<Reimbursement> {
    const response = await apiClient.post<Reimbursement>(API_PATHS.markPaid(id), payload);
    return response.data;
  },

  async getWorkflowConfig(): Promise<WorkflowConfig> {
    const response = await apiClient.get<WorkflowConfig>(API_PATHS.workflowConfig);
    return response.data;
  },

  async updateWorkflowConfig(payload: Partial<WorkflowConfig>): Promise<WorkflowConfig> {
    const response = await apiClient.put<WorkflowConfig>(API_PATHS.workflowConfig, payload);
    return response.data;
  },
};
