import React from 'react';
import { ApprovalWorkspace } from '../components/ApprovalWorkspace';

const SeniorDashboard: React.FC = () => (
  <ApprovalWorkspace
    title="Senior Manager Review"
    subtitle="Review escalated claims that exceed the amount threshold or remaining department budget."
    roleLabel="Senior Manager"
    showBudgetLink
    fetchMode="queue"
  />
);

export default SeniorDashboard;
