import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { ReimbursementForm } from '../../reimbursement/components/ReimbursementForm';
import { ClaimCard } from '../../reimbursement/components/ClaimCard';
import { useReimbursements } from '../../reimbursement/hooks/useReimbursements';
import { useAppContext } from '../../../context/AppContext';
import { ROUTES } from '../../../shared/utils/constants';
import './EmployeeDashboard.css';

const FILTER_KEYS = [
  'MANAGER_REVIEW',
  'SENIOR_MANAGER_REVIEW',
  'FINANCE_REVIEW',
  'VENDOR_PROCESSING',
  'PAID',
  'DENIED',
] as const;

function normalizeStatus(status: string): string {
  if (status === 'MANAGER_APPROVAL' || status === 'PENDING' || status === 'Pending' || status === 'SUBMITTED') {
    return 'MANAGER_REVIEW';
  }
  if (status === 'REQUIRES_SENIOR_APPROVAL') {
    return 'SENIOR_MANAGER_REVIEW';
  }
  if (status === 'APPROVED' || status === 'Approved') {
    return 'PAID';
  }
  return status;
}

const EmployeeDashboard: React.FC = () => {
  const { user } = useAppContext();
  const [filters, setFilters] = useState<Record<(typeof FILTER_KEYS)[number], boolean>>({
    MANAGER_REVIEW: true,
    SENIOR_MANAGER_REVIEW: true,
    FINANCE_REVIEW: true,
    VENDOR_PROCESSING: true,
    PAID: true,
    DENIED: true,
  });

  const { reimbursements, isLoading, error, refresh } = useReimbursements(user?.userId);

  const handleCheckboxChange = (status: (typeof FILTER_KEYS)[number]) => {
    setFilters((prev) => ({ ...prev, [status]: !prev[status] }));
  };

  const filteredReimbursements = reimbursements.filter((reimbursement) => {
    const normalized = normalizeStatus(String(reimbursement.status));
    return FILTER_KEYS.some((key) => filters[key] && normalized === key);
  });

  return (
    <main className="employee-dashboard">
      <h1>Employee Dashboard</h1>
      <p className="employee-intro">
        Track your claims through manager, senior, finance, and vendor stages.
      </p>
      <ReimbursementForm onReimbursementSubmit={refresh} />
      <fieldset className="filter-row">
        <legend className="sr-only">Filter claims by status</legend>
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
      </fieldset>
      <h2>Your reimbursement requests</h2>
      {isLoading ? (
        <p aria-live="polite">Loading…</p>
      ) : error ? (
        <p className="auth-error" role="alert">{error}</p>
      ) : filteredReimbursements.length === 0 ? (
        <p>No reimbursement requests to display.</p>
      ) : (
        <div className="claim-list" aria-live="polite">
          {filteredReimbursements.map((item) => (
            <ClaimCard key={item.reimbursementId} claim={item} showActions={false} />
          ))}
        </div>
      )}
      <p className="register-link"><Link to={ROUTES.logout}>Logout</Link></p>
    </main>
  );
};

export default EmployeeDashboard;
