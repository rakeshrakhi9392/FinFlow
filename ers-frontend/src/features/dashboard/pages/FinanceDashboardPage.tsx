import React from 'react';
import { ApprovalWorkspace } from '../components/ApprovalWorkspace';

const FinanceDashboard: React.FC = () => (
  <ApprovalWorkspace
    title="Finance & Vendor Processing"
    subtitle="Complete finance review, then mark vendor-processed claims as paid. Budget spend is applied when finance approves."
    roleLabel="Finance"
    showBudgetLink
    fetchMode="queue"
  />
);

export default FinanceDashboard;
