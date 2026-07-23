import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Login } from './features/auth/pages/LoginPage';
import Logout from './features/auth/pages/LogoutPage';
import { Register } from './features/auth/pages/RegisterPage';
import EmployeeDashboard from './features/dashboard/pages/EmployeeDashboardPage';
import ManagerDashboard from './features/dashboard/pages/ManagerDashboardPage';
import SeniorDashboard from './features/dashboard/pages/SeniorDashboardPage';
import FinanceDashboard from './features/dashboard/pages/FinanceDashboardPage';
import AdminDashboard from './features/dashboard/pages/AdminDashboardPage';
import BudgetDashboard from './features/dashboard/pages/BudgetDashboardPage';
import { dashboardRouteForRole, ROUTES } from './shared/utils/constants';
import './App.css';

const App: React.FC = () => {
  const [role, setRole] = useState<string | null>(null);
  const isAuthenticated = true;

  const renderMainRoute = () => {
    if (!isAuthenticated) {
      return <Navigate to={ROUTES.login} />;
    }
    return <Navigate to={dashboardRouteForRole(role)} />;
  };

  return (
    <div className="App">
      <Router>
        <Routes>
          <Route path={ROUTES.login} element={<Login setUserRole={setRole} />} />
          <Route path={ROUTES.logout} element={<Logout />} />
          <Route path={ROUTES.register} element={<Register />} />
          <Route path={ROUTES.employeeDashboard} element={<EmployeeDashboard setUserRole={setRole} />} />
          <Route path={ROUTES.managerDashboard} element={<ManagerDashboard />} />
          <Route path={ROUTES.seniorDashboard} element={<SeniorDashboard />} />
          <Route path={ROUTES.financeDashboard} element={<FinanceDashboard />} />
          <Route path={ROUTES.adminDashboard} element={<AdminDashboard />} />
          <Route path={ROUTES.budgetDashboard} element={<BudgetDashboard />} />
          <Route path={ROUTES.home} element={renderMainRoute()} />
        </Routes>
      </Router>
    </div>
  );
};

export default App;
