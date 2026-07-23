import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAppContext } from '../../context/AppContext';
import { dashboardRouteForRole, ROUTES } from '../utils/constants';

interface ProtectedRouteProps {
  children: React.ReactElement;
  roles?: string[];
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children, roles }) => {
  const { user } = useAppContext();

  if (!user) {
    return <Navigate to={ROUTES.login} replace />;
  }

  if (roles && roles.length > 0) {
    const normalized = user.role.toLowerCase();
    if (!roles.map((r) => r.toLowerCase()).includes(normalized)) {
      return <Navigate to={dashboardRouteForRole(user.role)} replace />;
    }
  }

  return children;
};

export default ProtectedRoute;
