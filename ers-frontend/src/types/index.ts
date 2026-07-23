export type UserRole =
  | 'employee'
  | 'manager'
  | 'senior_manager'
  | 'finance'
  | 'admin'
  | string;

export type ReimbursementStatus =
  | 'SUBMITTED'
  | 'MANAGER_REVIEW'
  | 'SENIOR_MANAGER_REVIEW'
  | 'FINANCE_REVIEW'
  | 'VENDOR_PROCESSING'
  | 'PAID'
  | 'DENIED'
  | 'MANAGER_APPROVAL'
  | 'REQUIRES_SENIOR_APPROVAL'
  | 'APPROVED'
  | 'PENDING'
  | 'Pending'
  | 'Approved'
  | 'Denied';

export type ApprovalAction =
  | 'SUBMITTED'
  | 'APPROVED'
  | 'DENIED'
  | 'ESCALATED'
  | 'VENDOR_MARKED_PAID'
  | 'COMMENT'
  | 'SYSTEM';

export type TimelineStageState = 'completed' | 'current' | 'upcoming' | 'skipped' | 'denied';

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

export interface ApprovalHistoryEntry {
  id: number;
  action: ApprovalAction;
  fromStatus?: ReimbursementStatus;
  toStatus: ReimbursementStatus;
  comment?: string;
  actedAt: string;
  actorId?: number;
  actorUsername?: string;
  actorDisplayName?: string;
  actorRole?: string;
}

export interface TimelineStage {
  key: string;
  label: string;
  status: ReimbursementStatus;
  state: TimelineStageState;
  skipped?: boolean;
  completedAt?: { iso: string; epochMillis: number };
  completedBy?: string;
}

export interface WorkflowTimeline {
  stages: TimelineStage[];
}

export interface Reimbursement {
  reimbursementId?: number;
  amount: number;
  description: string;
  status: ReimbursementStatus;
  statusLabel?: string;
  userId?: number;
  dateSubmitted?: string;
  statusChangedAt?: string;
  departmentId?: number;
  departmentName?: string;
  categoryId?: number;
  categoryName?: string;
  budgetId?: number;
  remainingBudgetAtSubmit?: number;
  requiresSeniorReview?: boolean;
  escalatedByAmount?: boolean;
  escalatedByBudget?: boolean;
  submitterId?: number;
  submitterUsername?: string;
  allowedActions?: string[];
  approvalHistory?: ApprovalHistoryEntry[];
  timeline?: WorkflowTimeline;
}

export interface CreateReimbursementPayload {
  amount: number | string;
  description: string;
  categoryId?: number;
  departmentId?: number;
}

export interface ApprovalDecisionPayload {
  comment?: string;
}

export interface WorkflowConfig {
  id?: number;
  configKey: string;
  seniorApprovalAmountThreshold: number;
  escalateOnBudgetExceed: boolean;
  seniorManagerStageEnabled: boolean;
  financeStageEnabled: boolean;
  description?: string;
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
