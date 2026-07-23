import React from 'react';
import { ApprovalWorkspace } from '../components/ApprovalWorkspace';

const ManagerDashboard: React.FC = () => (
  <ApprovalWorkspace
    title="Manager Review Queue"
    subtitle="Approve or deny claims at the manager stage. Over-threshold claims escalate to senior manager after your approval."
    roleLabel="Manager"
    showBudgetLink
    fetchMode="queue"
  />
);

export default ManagerDashboard;
