import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Login } from './features/auth/pages/LoginPage';
import Logout from './features/auth/pages/LogoutPage';
import { Register } from './features/auth/pages/RegisterPage';
import EmployeeDashboard from './features/dashboard/pages/EmployeeDashboardPage';
import ManagerDashboard from './features/dashboard/pages/ManagerDashboardPage';
import BudgetDashboard from './features/dashboard/pages/BudgetDashboardPage';
import { ROUTES } from './shared/utils/constants';
import './App.css';

const App: React.FC = () => {
  const [role, setRole] = useState<string | null>(null);

  // Preserved prior routing behavior: home always routes by role when "authenticated".
  const isAuthenticated = true;

  const renderMainRoute = () => {
    if (!isAuthenticated) {
      return <Navigate to={ROUTES.login} />;
    }
    return role === 'manager'
      ? <Navigate to={ROUTES.managerDashboard} />
      : <Navigate to={ROUTES.employeeDashboard} />;
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
          <Route path={ROUTES.budgetDashboard} element={<BudgetDashboard />} />
          <Route path={ROUTES.home} element={renderMainRoute()} />
        </Routes>
      </Router>
    </div>
  );
};

export default App;
