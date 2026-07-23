import { useCallback, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../../../services/authService';
import { getErrorMessage } from '../../../shared/utils/errorUtils';
import { ROUTES } from '../../../shared/utils/constants';
import { RegistrationPayload } from '../../../types';

export function useRegister() {
  const navigate = useNavigate();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState('');

  const register = useCallback(async (payload: RegistrationPayload, confirmPassword: string) => {
    if (payload.password !== confirmPassword) {
      const message = 'Passwords do not match!';
      setError(message);
      throw new Error(message);
    }

    setIsSubmitting(true);
    setError('');
    try {
      await authService.register(payload);
      navigate(ROUTES.login);
    } catch (err) {
      const message = getErrorMessage(err, 'Registration failed. Please try again.');
      setError(message);
      throw err;
    } finally {
      setIsSubmitting(false);
    }
  }, [navigate]);

  return { register, isSubmitting, error };
}
