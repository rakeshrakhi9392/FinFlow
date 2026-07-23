import React from 'react';
import { ApprovalHistoryEntry } from '../../../types';
import './ApprovalHistory.css';

interface ApprovalHistoryProps {
  history?: ApprovalHistoryEntry[];
}

export const ApprovalHistoryPanel: React.FC<ApprovalHistoryProps> = ({ history }) => {
  const entries = history || [];

  if (entries.length === 0) {
    return <p className="history-empty">No approval history yet.</p>;
  }

  return (
    <ul className="approval-history" aria-label="Approval history">
      {entries.map((entry) => (
        <li key={entry.id} className={`history-item action-${entry.action.toLowerCase()}`}>
          <div className="history-top">
            <span className="history-action">{entry.action.replace(/_/g, ' ')}</span>
            <time dateTime={entry.actedAt}>{new Date(entry.actedAt).toLocaleString()}</time>
          </div>
          <p className="history-transition">
            {(entry.fromStatus || '—').replace(/_/g, ' ')}
            {' → '}
            {String(entry.toStatus).replace(/_/g, ' ')}
          </p>
          <p className="history-actor">
            {entry.actorDisplayName || entry.actorUsername || 'System'}
            {entry.actorRole ? ` (${entry.actorRole})` : ''}
          </p>
          {entry.comment && <blockquote className="history-comment">{entry.comment}</blockquote>}
        </li>
      ))}
    </ul>
  );
};

export default ApprovalHistoryPanel;
