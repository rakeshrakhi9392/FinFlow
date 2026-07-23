import { apiClient } from './apiClient';
import { API_PATHS } from '../shared/utils/constants';
import { BudgetSummary, DepartmentBudget, ExpenseCategory, Department } from '../types';

export const budgetService = {
  getSummary: async (year?: number, quarter?: string): Promise<BudgetSummary> => {
    const params: Record<string, string | number> = {};
    if (year != null) params.year = year;
    if (quarter) params.quarter = quarter;
    const { data } = await apiClient.get<BudgetSummary>(API_PATHS.budgetSummary, { params });
    return data;
  },

  getDepartmentSpend: async (year?: number, quarter?: string): Promise<DepartmentBudget[]> => {
    const params: Record<string, string | number> = {};
    if (year != null) params.year = year;
    if (quarter) params.quarter = quarter;
    const { data } = await apiClient.get<DepartmentBudget[]>(API_PATHS.budgetDepartments, { params });
    return data;
  },

  listCategories: async (): Promise<ExpenseCategory[]> => {
    const { data } = await apiClient.get<ExpenseCategory[]>(API_PATHS.categories);
    return data;
  },

  listDepartments: async (): Promise<Department[]> => {
    const { data } = await apiClient.get<Department[]>(API_PATHS.departments);
    return data;
  },
};
