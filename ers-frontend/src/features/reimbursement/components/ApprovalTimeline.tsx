import React from 'react';
import { WorkflowTimeline } from '../../../types';
import './ApprovalTimeline.css';

interface ApprovalTimelineProps {
  timeline?: WorkflowTimeline;
}

export const ApprovalTimeline: React.FC<ApprovalTimelineProps> = ({ timeline }) => {
  const stages = timeline?.stages || [];

  if (stages.length === 0) {
    return <p className="timeline-empty">No timeline available yet.</p>;
  }

  return (
    <ol className="approval-timeline" aria-label="Approval workflow timeline">
      {stages.map((stage, index) => (
        <li key={stage.key} className={`timeline-step state-${stage.state}`}>
          <div className="timeline-rail">
            <span className="timeline-dot" aria-hidden="true" />
            {index < stages.length - 1 && <span className="timeline-connector" aria-hidden="true" />}
          </div>
          <div className="timeline-body">
            <div className="timeline-heading">
              <strong>{stage.label}</strong>
              <span className="timeline-state-chip">{stage.state}</span>
            </div>
            {stage.skipped && <p className="timeline-meta">Skipped — not required for this claim</p>}
            {!stage.skipped && stage.completedAt && (
              <p className="timeline-meta">
                {stage.completedBy ? `${stage.completedBy} · ` : ''}
                {new Date(stage.completedAt.iso).toLocaleString()}
              </p>
            )}
            {!stage.skipped && stage.state === 'current' && (
              <p className="timeline-meta current-hint">Awaiting action</p>
            )}
          </div>
        </li>
      ))}
    </ol>
  );
};

export default ApprovalTimeline;
