import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAppContext } from '../../../context/AppContext';
import { ROUTES } from '../../../shared/utils/constants';

const Logout: React.FC = () => {
  const navigate = useNavigate();
  const { clearUser } = useAppContext();

  useEffect(() => {
    clearUser();
    navigate(ROUTES.login);
  }, [clearUser, navigate]);

  return (
    <div className="logout">
      <h1>Logging out...</h1>
    </div>
  );
};

export default Logout;
