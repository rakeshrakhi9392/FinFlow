import React from 'react';
import { BrowserRouter as Router, Navigate, Route, Routes } from 'react-router-dom';
import { Login } from './features/auth/pages/LoginPage';
import Logout from './features/auth/pages/LogoutPage';
import { Register } from './features/auth/pages/RegisterPage';
import EmployeeDashboard from './features/dashboard/pages/EmployeeDashboardPage';
import ManagerDashboard from './features/dashboard/pages/ManagerDashboardPage';
import SeniorDashboard from './features/dashboard/pages/SeniorDashboardPage';
import FinanceDashboard from './features/dashboard/pages/FinanceDashboardPage';
import AdminDashboard from './features/dashboard/pages/AdminDashboardPage';
import BudgetDashboard from './features/dashboard/pages/BudgetDashboardPage';
import VendorDashboard from './features/dashboard/pages/VendorDashboardPage';
import { useAppContext } from './context/AppContext';
import { ProtectedRoute } from './shared/components/ProtectedRoute';
import { dashboardRouteForRole, ROUTES } from './shared/utils/constants';
import './App.css';

const App: React.FC = () => {
  const { user } = useAppContext();

  return (
    <div className="App">
      <Router>
        <Routes>
          <Route path={ROUTES.login} element={
            user ? <Navigate to={dashboardRouteForRole(user.role)} replace /> : <Login />
          } />
          <Route path={ROUTES.register} element={
            user ? <Navigate to={dashboardRouteForRole(user.role)} replace /> : <Register />
          } />
          <Route path={ROUTES.logout} element={<Logout />} />

          <Route path={ROUTES.employeeDashboard} element={
            <ProtectedRoute roles={['employee', 'admin']}>
              <EmployeeDashboard />
            </ProtectedRoute>
          } />
          <Route path={ROUTES.managerDashboard} element={
            <ProtectedRoute roles={['manager', 'admin']}>
              <ManagerDashboard />
            </ProtectedRoute>
          } />
          <Route path={ROUTES.seniorDashboard} element={
            <ProtectedRoute roles={['senior_manager', 'admin']}>
              <SeniorDashboard />
            </ProtectedRoute>
          } />
          <Route path={ROUTES.financeDashboard} element={
            <ProtectedRoute roles={['finance', 'admin']}>
              <FinanceDashboard />
            </ProtectedRoute>
          } />
          <Route path={ROUTES.adminDashboard} element={
            <ProtectedRoute roles={['admin']}>
              <AdminDashboard />
            </ProtectedRoute>
          } />
          <Route path={ROUTES.budgetDashboard} element={
            <ProtectedRoute roles={['manager', 'senior_manager', 'finance', 'admin']}>
              <BudgetDashboard />
            </ProtectedRoute>
          } />
          <Route path={ROUTES.vendorDashboard} element={
            <ProtectedRoute roles={['finance', 'admin']}>
              <VendorDashboard />
            </ProtectedRoute>
          } />

          <Route path={ROUTES.home} element={
            user
              ? <Navigate to={dashboardRouteForRole(user.role)} replace />
              : <Navigate to={ROUTES.login} replace />
          } />
          <Route path="*" element={<Navigate to={ROUTES.home} replace />} />
        </Routes>
      </Router>
    </div>
  );
};

export default App;
