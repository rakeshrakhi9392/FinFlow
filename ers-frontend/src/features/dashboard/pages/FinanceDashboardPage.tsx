import React from 'react';
import { ApprovalWorkspace } from '../components/ApprovalWorkspace';

const FinanceDashboard: React.FC = () => (
  <ApprovalWorkspace
    title="Finance & Vendor Processing"
    subtitle="Finance approval posts to Mock SAP. Use the Vendor Dashboard for integration status, last sync, vendor response, and retries."
    roleLabel="Finance"
    showBudgetLink
    showVendorLink
    fetchMode="queue"
  />
);

export default FinanceDashboard;
