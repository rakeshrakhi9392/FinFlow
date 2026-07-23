import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAppContext } from '../../../context/AppContext';
import { authService } from '../../../services/authService';
import { ROUTES } from '../../../shared/utils/constants';

const Logout: React.FC = () => {
  const navigate = useNavigate();
  const { clearUser } = useAppContext();

  useEffect(() => {
    let cancelled = false;
    (async () => {
      await authService.logout();
      if (!cancelled) {
        clearUser();
        navigate(ROUTES.login);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [clearUser, navigate]);

  return (
    <div className="logout">
      <h1>Logging out...</h1>
    </div>
  );
};

export default Logout;
