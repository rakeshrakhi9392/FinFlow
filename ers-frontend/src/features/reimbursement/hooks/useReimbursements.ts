import { useCallback, useEffect, useState } from 'react';
import { reimbursementService } from '../../../services/reimbursementService';
import { getErrorMessage } from '../../../shared/utils/errorUtils';
import { Reimbursement } from '../../../types';

interface UseReimbursementsOptions {
  /** When true, loads all reimbursements (manager view). */
  fetchAll?: boolean;
}

export function useReimbursements(userId?: number, options: UseReimbursementsOptions = {}) {
  const { fetchAll = false } = options;
  const [reimbursements, setReimbursements] = useState<Reimbursement[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchReimbursements = useCallback(async () => {
    if (!fetchAll && userId == null) {
      setReimbursements([]);
      setIsLoading(false);
      return;
    }

    setIsLoading(true);
    setError('');
    try {
      const data = fetchAll
        ? await reimbursementService.getAll()
        : await reimbursementService.getByUserId(userId as number);
      setReimbursements(data);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to fetch reimbursement requests.'));
    } finally {
      setIsLoading(false);
    }
  }, [userId, fetchAll]);

  useEffect(() => {
    fetchReimbursements();
  }, [fetchReimbursements]);

  return { reimbursements, setReimbursements, isLoading, error, setError, refresh: fetchReimbursements };
}
