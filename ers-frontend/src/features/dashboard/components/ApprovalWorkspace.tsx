import React, { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { ClaimCard } from '../../reimbursement/components/ClaimCard';
import { reimbursementService } from '../../../services/reimbursementService';
import { vendorService } from '../../../services/vendorService';
import { getErrorMessage } from '../../../shared/utils/errorUtils';
import { ROUTES } from '../../../shared/utils/constants';
import { Reimbursement } from '../../../types';
import './ApprovalWorkspace.css';

interface ApprovalWorkspaceProps {
  title: string;
  subtitle: string;
  roleLabel: string;
  showBudgetLink?: boolean;
  showVendorLink?: boolean;
  showAdminConfig?: boolean;
  fetchMode?: 'queue' | 'all';
}

export const ApprovalWorkspace: React.FC<ApprovalWorkspaceProps> = ({
  title,
  subtitle,
  roleLabel,
  showBudgetLink = false,
  showVendorLink = false,
  showAdminConfig = false,
  fetchMode = 'queue',
}) => {
  const [claims, setClaims] = useState<Reimbursement[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  const [threshold, setThreshold] = useState('5000');
  const [escalateBudget, setEscalateBudget] = useState(true);
  const [configMessage, setConfigMessage] = useState('');

  const load = useCallback(async () => {
    setIsLoading(true);
    setError('');
    try {
      const data = fetchMode === 'all'
        ? await reimbursementService.getAll()
        : await reimbursementService.getQueue();
      setClaims(data);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load reimbursement queue'));
    } finally {
      setIsLoading(false);
    }
  }, [fetchMode]);

  useEffect(() => {
    load();
  }, [load]);

  useEffect(() => {
    if (!showAdminConfig) return;
    reimbursementService.getWorkflowConfig()
      .then((cfg) => {
        setThreshold(String(cfg.seniorApprovalAmountThreshold));
        setEscalateBudget(cfg.escalateOnBudgetExceed);
      })
      .catch(() => undefined);
  }, [showAdminConfig]);

  const refreshAfter = async (updated: Reimbursement) => {
    setClaims((prev) => {
      const next = prev.map((c) => (c.reimbursementId === updated.reimbursementId ? updated : c));
      if (fetchMode === 'queue' && !(updated.allowedActions && updated.allowedActions.length)) {
        return next.filter((c) => c.reimbursementId !== updated.reimbursementId);
      }
      return next;
    });
    await load();
  };

  const handleApprove = async (id: number, comment: string) => {
    try {
      const updated = await reimbursementService.approve(id, { comment });
      await refreshAfter(updated);
    } catch (err) {
      setError(getErrorMessage(err, `Failed to approve #${id}`));
    }
  };

  const handleDeny = async (id: number, comment: string) => {
    try {
      const updated = await reimbursementService.deny(id, { comment });
      await refreshAfter(updated);
    } catch (err) {
      setError(getErrorMessage(err, `Failed to deny #${id}`));
    }
  };

  const handleMarkPaid = async (id: number, comment: string) => {
    try {
      const updated = await reimbursementService.markPaid(id, { comment });
      await refreshAfter(updated);
    } catch (err) {
      setError(getErrorMessage(err, `Failed to mark paid #${id}`));
    }
  };

  const handleRetryVendor = async (id: number) => {
    try {
      const updated = await vendorService.retry(id);
      await refreshAfter(updated);
    } catch (err) {
      setError(getErrorMessage(err, `Failed to retry vendor sync #${id}`));
    }
  };

  const saveConfig = async () => {
    try {
      await reimbursementService.updateWorkflowConfig({
        seniorApprovalAmountThreshold: Number(threshold),
        escalateOnBudgetExceed: escalateBudget,
        seniorManagerStageEnabled: true,
        financeStageEnabled: true,
      });
      setConfigMessage('Workflow configuration saved.');
    } catch (err) {
      setConfigMessage(getErrorMessage(err, 'Failed to save configuration'));
    }
  };

  return (
    <main className="approval-workspace">
      <header className="workspace-hero">
        <p className="role-pill">{roleLabel}</p>
        <h1>{title}</h1>
        <p>{subtitle}</p>
        <nav className="workspace-nav" aria-label="Dashboard navigation">
          {showBudgetLink && <Link to={ROUTES.budgetDashboard}>Budget Dashboard</Link>}
          {showVendorLink && <Link to={ROUTES.vendorDashboard}>Vendor Dashboard</Link>}
          <Link to={ROUTES.logout}>Logout</Link>
        </nav>
      </header>

      {showAdminConfig && (
        <section className="config-panel" aria-labelledby="config-heading">
          <h2 id="config-heading">Configurable approval levels</h2>
          <div className="config-row">
            <label>
              Senior escalation amount ($)
              <input
                type="number"
                min={0}
                value={threshold}
                onChange={(e) => setThreshold(e.target.value)}
              />
            </label>
            <label className="checkbox">
              <input
                type="checkbox"
                checked={escalateBudget}
                onChange={(e) => setEscalateBudget(e.target.checked)}
              />
              Escalate when claim exceeds remaining budget
            </label>
            <button type="button" onClick={saveConfig}>Save rules</button>
          </div>
          {configMessage && <p className="config-msg" role="status">{configMessage}</p>}
        </section>
      )}

      <section className="queue-section" aria-labelledby="queue-heading">
        <div className="queue-heading">
          <h2 id="queue-heading">{fetchMode === 'all' ? 'All claims' : 'Your action queue'}</h2>
          <button type="button" className="ghost" onClick={load}>Refresh</button>
        </div>
        {isLoading ? (
          <p aria-live="polite">Loading…</p>
        ) : error ? (
          <p className="error-text" role="alert">{error}</p>
        ) : claims.length === 0 ? (
          <p className="empty-queue">No claims require your action right now.</p>
        ) : (
          <div aria-live="polite">
            {claims.map((claim) => (
              <ClaimCard
                key={claim.reimbursementId}
                claim={claim}
                onApprove={handleApprove}
                onDeny={handleDeny}
                onMarkPaid={handleMarkPaid}
                onRetryVendor={handleRetryVendor}
              />
            ))}
          </div>
        )}
      </section>
    </main>
  );
};

export default ApprovalWorkspace;
