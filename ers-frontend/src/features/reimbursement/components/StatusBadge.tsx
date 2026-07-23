import React from 'react';
import { ReimbursementStatus } from '../../../types';
import { STATUS_LABELS } from '../../../shared/utils/constants';
import './StatusBadge.css';

interface StatusBadgeProps {
  status: ReimbursementStatus | string;
  label?: string;
}

const toneFor = (status: string): string => {
  switch (status) {
    case 'SUBMITTED':
      return 'tone-submitted';
    case 'MANAGER_REVIEW':
    case 'MANAGER_APPROVAL':
    case 'PENDING':
      return 'tone-manager';
    case 'SENIOR_MANAGER_REVIEW':
    case 'REQUIRES_SENIOR_APPROVAL':
      return 'tone-senior';
    case 'FINANCE_REVIEW':
      return 'tone-finance';
    case 'PENDING_VENDOR_CONFIRMATION':
      return 'tone-vendor';
    case 'FAILED_VENDOR_SYNC':
      return 'tone-denied';
    case 'VENDOR_PROCESSING':
      return 'tone-vendor';
    case 'PAID':
    case 'APPROVED':
      return 'tone-paid';
    case 'DENIED':
      return 'tone-denied';
    default:
      return 'tone-default';
  }
};

export const StatusBadge: React.FC<StatusBadgeProps> = ({ status, label }) => {
  const key = String(status);
  return (
    <span className={`status-badge ${toneFor(key)}`}>
      {label || STATUS_LABELS[key] || key.replace(/_/g, ' ')}
    </span>
  );
};

export default StatusBadge;
