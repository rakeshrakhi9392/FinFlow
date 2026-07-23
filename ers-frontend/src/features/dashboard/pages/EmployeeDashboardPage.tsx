import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { ReimbursementForm } from '../../reimbursement/components/ReimbursementForm';
import { ReimbursementList } from '../../reimbursement/components/ReimbursementList';
import { useReimbursements } from '../../reimbursement/hooks/useReimbursements';
import { useAppContext } from '../../../context/AppContext';
import { ROUTES } from '../../../shared/utils/constants';
import './EmployeeDashboard.css';

interface EmployeeDashboardProps {
  setUserRole: (role: string) => void;
}

const EmployeeDashboard: React.FC<EmployeeDashboardProps> = ({ setUserRole }) => {
  const { user } = useAppContext();
  const [filters, setFilters] = useState({
    MANAGER_APPROVAL: true,
    REQUIRES_SENIOR_APPROVAL: true,
    APPROVED: true,
    DENIED: true,
  });

  useEffect(() => {
    if (user?.role) {
      setUserRole(user.role);
    }
  }, [user, setUserRole]);

  const { reimbursements, isLoading, error, refresh } = useReimbursements(user?.userId);

  const handleCheckboxChange = (status: keyof typeof filters) => {
    setFilters((prev) => ({ ...prev, [status]: !prev[status] }));
  };

  const filteredReimbursements = reimbursements.filter((reimbursement) =>
    Object.entries(filters).some(([status, checked]) => checked && reimbursement.status === status)
  );

  return (
    <div className="employee-dashboard">
      <h1>Employee Dashboard</h1>
      <ReimbursementForm onReimbursementSubmit={refresh} />
      <div>
        {Object.keys(filters).map((status) => (
          <label key={status}>
            <input
              type="checkbox"
              checked={filters[status as keyof typeof filters]}
              onChange={() => handleCheckboxChange(status as keyof typeof filters)}
            />
            Show {status.replace(/_/g, ' ')}
          </label>
        ))}
      </div>
      <h2>Your Reimbursement Requests</h2>
      {isLoading ? (
        <p>Loading...</p>
      ) : error ? (
        <p>{error}</p>
      ) : (
        <ReimbursementList reimbursements={filteredReimbursements} />
      )}
      <p className="register-link"><Link to={ROUTES.logout}>Logout</Link></p>
    </div>
  );
};

export default EmployeeDashboard;
