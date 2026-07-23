import { useCallback, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../../../services/authService';
import { useAppContext } from '../../../context/AppContext';
import { getErrorMessage } from '../../../shared/utils/errorUtils';
import { dashboardRouteForRole } from '../../../shared/utils/constants';
import { LoginCredentials } from '../../../types';

export function useLogin() {
  const navigate = useNavigate();
  const { setUser } = useAppContext();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState('');

  const login = useCallback(async (credentials: LoginCredentials) => {
    setIsSubmitting(true);
    setError('');
    try {
      const authUser = await authService.login(credentials);
      setUser(authUser);
      navigate(dashboardRouteForRole(authUser.role));
      return authUser;
    } catch (err) {
      const message = getErrorMessage(err, 'Login Failed!');
      setError(message);
      throw err;
    } finally {
      setIsSubmitting(false);
    }
  }, [navigate, setUser]);

  return { login, isSubmitting, error };
}
