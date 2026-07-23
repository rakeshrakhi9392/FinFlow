export const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080';

export const API_PATHS = {
  users: '/users',
  login: '/users/login',
  logout: '/users/logout',
  reimbursements: '/api/reimbursements',
  reimbursementsByUser: (userId: number) => `/api/reimbursements/user/${userId}`,
  reimbursementById: (id: number) => `/api/reimbursements/${id}`,
  reimbursementQueue: '/api/reimbursements/queue',
  approveReimbursement: (id: number) => `/api/reimbursements/approve/${id}`,
  denyReimbursement: (id: number) => `/api/reimbursements/deny/${id}`,
  markPaid: (id: number) => `/api/reimbursements/mark-paid/${id}`,
  workflowConfig: '/api/workflow',
  vendorIntegrations: '/api/vendor',
  vendorIntegration: (id: number) => `/api/vendor/${id}`,
  retryVendorSync: (id: number) => `/api/vendor/${id}/retry`,
  budgetSummary: '/api/budgets/summary',
  budgetDepartments: '/api/budgets/departments',
  categories: '/api/categories',
  departments: '/api/departments',
} as const;

export const SESSION_USER_KEY = 'user';

export const ROUTES = {
  login: '/login',
  logout: '/logout',
  register: '/register',
  employeeDashboard: '/employee-dashboard',
  managerDashboard: '/manager-dashboard',
  seniorDashboard: '/senior-dashboard',
  financeDashboard: '/finance-dashboard',
  adminDashboard: '/admin-dashboard',
  budgetDashboard: '/budget-dashboard',
  vendorDashboard: '/vendor-dashboard',
  home: '/',
} as const;

export const STATUS_LABELS: Record<string, string> = {
  SUBMITTED: 'Submitted',
  MANAGER_REVIEW: 'Manager Review',
  SENIOR_MANAGER_REVIEW: 'Senior Manager Review',
  FINANCE_REVIEW: 'Finance Review',
  PENDING_VENDOR_CONFIRMATION: 'Pending Vendor Confirmation',
  FAILED_VENDOR_SYNC: 'Failed Vendor Sync',
  VENDOR_PROCESSING: 'Vendor Processing',
  PAID: 'Paid',
  DENIED: 'Denied',
  MANAGER_APPROVAL: 'Manager Review',
  REQUIRES_SENIOR_APPROVAL: 'Senior Manager Review',
  APPROVED: 'Paid',
  PENDING: 'Manager Review',
};

export function dashboardRouteForRole(role: string | null | undefined): string {
  switch ((role || '').toLowerCase()) {
    case 'manager':
      return ROUTES.managerDashboard;
    case 'senior_manager':
      return ROUTES.seniorDashboard;
    case 'finance':
      return ROUTES.financeDashboard;
    case 'admin':
      return ROUTES.adminDashboard;
    default:
      return ROUTES.employeeDashboard;
  }
}
