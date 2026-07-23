import React from 'react';
import { Link } from 'react-router-dom';
import { useReimbursements } from '../../reimbursement/hooks/useReimbursements';
import { reimbursementService } from '../../../services/reimbursementService';
import { getErrorMessage } from '../../../shared/utils/errorUtils';
import { ROUTES } from '../../../shared/utils/constants';
import './ManagerDashboard.css';

const awaitingApproval = (status: string) =>
  status === 'MANAGER_APPROVAL' ||
  status === 'REQUIRES_SENIOR_APPROVAL' ||
  status === 'PENDING' ||
  status === 'Pending';

const ManagerDashboard: React.FC = () => {
  const { reimbursements: requests, setReimbursements: setRequests, isLoading, error, setError } =
    useReimbursements(undefined, { fetchAll: true });

  const handleApprove = async (reimbursementId: number) => {
    try {
      await reimbursementService.approve(reimbursementId);
      setRequests(requests.map((req) =>
        req.reimbursementId === reimbursementId ? { ...req, status: 'APPROVED' } : req
      ));
    } catch (err) {
      console.error('Failed to approve reimbursement', err);
      setError(getErrorMessage(err, `Failed to approve reimbursement ID ${reimbursementId}`));
    }
  };

  const handleDeny = async (reimbursementId: number) => {
    try {
      await reimbursementService.deny(reimbursementId);
      setRequests(requests.map((req) =>
        req.reimbursementId === reimbursementId ? { ...req, status: 'DENIED' } : req
      ));
    } catch (err) {
      console.error('Failed to deny reimbursement', err);
      setError(getErrorMessage(err, `Failed to deny reimbursement ID ${reimbursementId}`));
    }
  };

  return (
    <div className="manager-dashboard">
      <h1>Manager Dashboard</h1>
      <p className="register-link">
        <Link to={ROUTES.budgetDashboard}>Budget Dashboard</Link>
      </p>
      {isLoading ? (
        <p>Loading...</p>
      ) : error ? (
        <p>{error}</p>
      ) : (
        <ul>
          {requests.map((request) => (
            <li key={request.reimbursementId}>
              <span>
                {request.description} - ${request.amount} - {request.status}
                {request.departmentName ? ` · ${request.departmentName}` : ''}
                {request.status === 'REQUIRES_SENIOR_APPROVAL' ? ' · Senior review' : ''}
              </span>
              {awaitingApproval(request.status) && (
                <>
                  <button onClick={() => handleApprove(request.reimbursementId!)}>Approve</button>
                  <button className="red" onClick={() => handleDeny(request.reimbursementId!)}>Deny</button>
                </>
              )}
            </li>
          ))}
        </ul>
      )}
      <p className="register-link"><Link to={ROUTES.logout}>Logout</Link></p>
    </div>
  );
};

export default ManagerDashboard;
