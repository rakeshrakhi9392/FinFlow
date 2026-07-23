export type UserRole = 'employee' | 'manager' | string;

export type ReimbursementStatus =
  | 'MANAGER_APPROVAL'
  | 'REQUIRES_SENIOR_APPROVAL'
  | 'APPROVED'
  | 'DENIED'
  | 'PENDING'
  | 'Pending'
  | 'Approved'
  | 'Denied';

export type FiscalQuarter = 'Q1' | 'Q2' | 'Q3' | 'Q4';

export interface AuthUser {
  userId: number;
  username: string;
  role: UserRole;
}

export interface LoginCredentials {
  username: string;
  password: string;
}

export interface RegistrationPayload {
  username: string;
  password: string;
  email?: string;
  firstName?: string;
  lastName?: string;
  role?: UserRole;
  departmentId?: number;
}

export interface UserFormState {
  userId?: number;
  username: string;
  password?: string;
  role?: UserRole;
  firstName?: string;
  lastName?: string;
  email?: string;
  confirmPassword?: string;
}

export interface Reimbursement {
  reimbursementId?: number;
  amount: number;
  description: string;
  status: ReimbursementStatus;
  userId?: number;
  dateSubmitted?: string;
  departmentId?: number;
  departmentName?: string;
  categoryId?: number;
  categoryName?: string;
  budgetId?: number;
  remainingBudgetAtSubmit?: number;
}

export interface CreateReimbursementPayload {
  amount: number | string;
  description: string;
  categoryId?: number;
  departmentId?: number;
}

export interface Department {
  id: number;
  name: string;
  code: string;
}

export interface ExpenseCategory {
  id: number;
  name: string;
  code: string;
  description?: string;
}

export interface DepartmentBudget {
  budgetId: number;
  departmentId: number;
  departmentName: string;
  departmentCode: string;
  fiscalYear: number;
  quarter: FiscalQuarter;
  allocatedAmount: number;
  spentAmount: number;
  remainingBudget: number;
  utilizationPercent: number;
}

export interface BudgetSummary {
  fiscalYear: number;
  quarter: FiscalQuarter;
  totalAllocated: number;
  totalSpent: number;
  remainingBudget: number;
  utilizationPercent: number;
  departments: DepartmentBudget[];
}
