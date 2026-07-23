import React from 'react';
import { ApprovalWorkspace } from '../components/ApprovalWorkspace';

const AdminDashboard: React.FC = () => (
  <ApprovalWorkspace
    title="Admin Workflow Control"
    subtitle="Configure escalation rules, monitor all stages, and act on any review queue when needed."
    roleLabel="Admin"
    showBudgetLink
    showAdminConfig
    fetchMode="all"
  />
);

export default AdminDashboard;
