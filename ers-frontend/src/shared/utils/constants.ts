export const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080';

export const API_PATHS = {
  users: '/users',
  login: '/users/login',
  reimbursements: '/api/reimbursements',
  reimbursementsByUser: (userId: number) => `/api/reimbursements/user/${userId}`,
  approveReimbursement: (id: number) => `/api/reimbursements/approve/${id}`,
  denyReimbursement: (id: number) => `/api/reimbursements/deny/${id}`,
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
  budgetDashboard: '/budget-dashboard',
  home: '/',
} as const;
