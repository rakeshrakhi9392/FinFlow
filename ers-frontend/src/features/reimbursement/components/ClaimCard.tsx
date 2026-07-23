import React, { useEffect, useState } from 'react';
import { Reimbursement } from '../../../types';
import { reimbursementService } from '../../../services/reimbursementService';
import { StatusBadge } from './StatusBadge';
import { ApprovalTimeline } from './ApprovalTimeline';
import { ApprovalHistoryPanel } from './ApprovalHistory';
import './ClaimCard.css';

interface ClaimCardProps {
  claim: Reimbursement;
  onApprove?: (id: number, comment: string) => Promise<void>;
  onDeny?: (id: number, comment: string) => Promise<void>;
  onMarkPaid?: (id: number, comment: string) => Promise<void>;
  showActions?: boolean;
}

export const ClaimCard: React.FC<ClaimCardProps> = ({
  claim: initialClaim,
  onApprove,
  onDeny,
  onMarkPaid,
  showActions = true,
}) => {
  const [claim, setClaim] = useState(initialClaim);
  const [comment, setComment] = useState('');
  const [busy, setBusy] = useState(false);
  const [expanded, setExpanded] = useState(false);
  const [loadingDetail, setLoadingDetail] = useState(false);
  const id = claim.reimbursementId!;
  const actions = claim.allowedActions || [];

  useEffect(() => {
    setClaim(initialClaim);
  }, [initialClaim]);

  const toggleExpanded = async () => {
    const next = !expanded;
    setExpanded(next);
    if (next && (!claim.timeline || !claim.approvalHistory?.length)) {
      setLoadingDetail(true);
      try {
        const detailed = await reimbursementService.getById(id);
        setClaim((prev) => ({
          ...prev,
          ...detailed,
          allowedActions: detailed.allowedActions || prev.allowedActions,
        }));
      } catch {
        // Keep summary if detail fetch fails.
      } finally {
        setLoadingDetail(false);
      }
    }
  };

  const run = async (fn?: (id: number, comment: string) => Promise<void>) => {
    if (!fn) return;
    setBusy(true);
    try {
      await fn(id, comment.trim());
      setComment('');
    } finally {
      setBusy(false);
    }
  };

  return (
    <article className="claim-card">
      <header className="claim-card-header">
        <div>
          <h3>{claim.description}</h3>
          <p className="claim-meta">
            ${Number(claim.amount).toFixed(2)}
            {claim.departmentName ? ` · ${claim.departmentName}` : ''}
            {claim.submitterUsername ? ` · ${claim.submitterUsername}` : ''}
            {claim.dateSubmitted ? ` · ${claim.dateSubmitted}` : ''}
          </p>
          <div className="claim-flags">
            <StatusBadge status={claim.status} label={claim.statusLabel} />
            {claim.requiresSeniorReview && <span className="flag escalate">Senior path</span>}
            {claim.escalatedByAmount && <span className="flag amount">Amount escalation</span>}
            {claim.escalatedByBudget && <span className="flag budget">Budget escalation</span>}
          </div>
        </div>
        <button type="button" className="ghost-btn" onClick={toggleExpanded}>
          {expanded ? 'Hide timeline' : 'View timeline'}
        </button>
      </header>

      {expanded && (
        <div className="claim-detail-grid">
          {loadingDetail ? (
            <p>Loading timeline...</p>
          ) : (
            <>
              <section>
                <h4>Workflow timeline</h4>
                <ApprovalTimeline timeline={claim.timeline} />
              </section>
              <section>
                <h4>Approval history</h4>
                <ApprovalHistoryPanel history={claim.approvalHistory} />
              </section>
            </>
          )}
        </div>
      )}

      {showActions && actions.length > 0 && (
        <footer className="claim-actions">
          <textarea
            placeholder="Approval comment (optional)"
            value={comment}
            onChange={(e) => setComment(e.target.value)}
            rows={2}
            disabled={busy}
          />
          <div className="action-buttons">
            {actions.includes('APPROVE') && (
              <button type="button" disabled={busy} onClick={() => run(onApprove)}>
                Approve
              </button>
            )}
            {actions.includes('DENY') && (
              <button type="button" className="danger" disabled={busy} onClick={() => run(onDeny)}>
                Deny
              </button>
            )}
            {actions.includes('MARK_PAID') && (
              <button type="button" className="paid" disabled={busy} onClick={() => run(onMarkPaid)}>
                Mark Paid
              </button>
            )}
          </div>
        </footer>
      )}
    </article>
  );
};

export default ClaimCard;
