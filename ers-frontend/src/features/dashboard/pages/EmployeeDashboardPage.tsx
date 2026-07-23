import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { ReimbursementForm } from '../../reimbursement/components/ReimbursementForm';
import { ClaimCard } from '../../reimbursement/components/ClaimCard';
import { useReimbursements } from '../../reimbursement/hooks/useReimbursements';
import { useAppContext } from '../../../context/AppContext';
import { ROUTES } from '../../../shared/utils/constants';
import './EmployeeDashboard.css';

interface EmployeeDashboardProps {
  setUserRole: (role: string) => void;
}

const FILTER_KEYS = [
  'MANAGER_REVIEW',
  'SENIOR_MANAGER_REVIEW',
  'FINANCE_REVIEW',
  'VENDOR_PROCESSING',
  'PAID',
  'DENIED',
] as const;

const EmployeeDashboard: React.FC<EmployeeDashboardProps> = ({ setUserRole }) => {
  const { user } = useAppContext();
  const [filters, setFilters] = useState<Record<(typeof FILTER_KEYS)[number], boolean>>({
    MANAGER_REVIEW: true,
    SENIOR_MANAGER_REVIEW: true,
    FINANCE_REVIEW: true,
    VENDOR_PROCESSING: true,
    PAID: true,
    DENIED: true,
  });

  useEffect(() => {
    if (user?.role) {
      setUserRole(user.role);
    }
  }, [user, setUserRole]);

  const { reimbursements, isLoading, error, refresh } = useReimbursements(user?.userId);

  const handleCheckboxChange = (status: (typeof FILTER_KEYS)[number]) => {
    setFilters((prev) => ({ ...prev, [status]: !prev[status] }));
  };

  const filteredReimbursements = reimbursements.filter((reimbursement) => {
    const status = String(reimbursement.status);
    const normalized =
      status === 'MANAGER_APPROVAL' || status === 'PENDING' || status === 'Pending'
        ? 'MANAGER_REVIEW'
        : status === 'REQUIRES_SENIOR_APPROVAL'
          ? 'SENIOR_MANAGER_REVIEW'
          : status === 'APPROVED' || status === 'Approved'
            ? 'PAID'
            : status === 'SUBMITTED'
              ? 'MANAGER_REVIEW'
              : status;
    return FILTER_KEYS.some((key) => filters[key] && normalized === key);
  });

  return (
    <div className="employee-dashboard">
      <h1>Employee Dashboard</h1>
      <p className="employee-intro">
        Track your claims through manager, senior, finance, and vendor stages.
      </p>
      <ReimbursementForm onReimbursementSubmit={refresh} />
      <div className="filter-row">
        {FILTER_KEYS.map((status) => (
          <label key={status}>
            <input
              type="checkbox"
              checked={filters[status]}
              onChange={() => handleCheckboxChange(status)}
            />
            {status.replace(/_/g, ' ')}
          </label>
        ))}
      </div>
      <h2>Your Reimbursement Requests</h2>
      {isLoading ? (
        <p>Loading...</p>
      ) : error ? (
        <p>{error}</p>
      ) : filteredReimbursements.length === 0 ? (
        <p>No reimbursement requests to display.</p>
      ) : (
        filteredReimbursements.map((item) => (
          <ClaimCard key={item.reimbursementId} claim={item} showActions={false} />
        ))
      )}
      <p className="register-link"><Link to={ROUTES.logout}>Logout</Link></p>
    </div>
  );
};

export default EmployeeDashboard;
