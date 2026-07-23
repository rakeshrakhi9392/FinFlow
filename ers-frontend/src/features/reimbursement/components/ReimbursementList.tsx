import React from 'react';
import { Reimbursement } from '../../../types';
import './ReimbursementList.css';

interface ReimbursementListProps {
  reimbursements: Reimbursement[];
}

export const ReimbursementList: React.FC<ReimbursementListProps> = ({ reimbursements }) => {
  return (
    <div className="reimbursement-list">
      {reimbursements.length > 0 ? (
        <ul>
          {reimbursements.map((item, index) => (
            <li key={item.reimbursementId ?? index}>
              {item.description} - ${item.amount} ({item.status})
            </li>
          ))}
        </ul>
      ) : (
        <p>No reimbursement requests to display.</p>
      )}
    </div>
  );
};

export default ReimbursementList;
