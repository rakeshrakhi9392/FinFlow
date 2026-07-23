import React, { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { vendorService } from '../../../services/vendorService';
import { getErrorMessage } from '../../../shared/utils/errorUtils';
import { ROUTES } from '../../../shared/utils/constants';
import { VendorIntegration } from '../../../types';
import './VendorDashboard.css';

const formatWhen = (iso?: string) => {
  if (!iso) return '—';
  try {
    return new Date(iso).toLocaleString();
  } catch {
    return iso;
  }
};

const statusClass = (status?: string) => {
  switch (status) {
    case 'SYNCED':
      return 'vendor-status vendor-status--ok';
    case 'FAILED_VENDOR_SYNC':
      return 'vendor-status vendor-status--fail';
    case 'PENDING_VENDOR_CONFIRMATION':
      return 'vendor-status vendor-status--pending';
    default:
      return 'vendor-status';
  }
};

const VendorDashboard: React.FC = () => {
  const [rows, setRows] = useState<VendorIntegration[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [busyId, setBusyId] = useState<number | null>(null);

  const load = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      setRows(await vendorService.list());
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load vendor integrations'));
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const handleRetry = async (id: number) => {
    setBusyId(id);
    setError(null);
    try {
      await vendorService.retry(id);
      await load();
    } catch (err) {
      setError(getErrorMessage(err, `Failed to retry vendor sync #${id}`));
    } finally {
      setBusyId(null);
    }
  };

  return (
    <div className="vendor-dashboard">
      <header className="vendor-dashboard__header">
        <div>
          <p className="vendor-dashboard__eyebrow">ERP Integration</p>
          <h1>Vendor Dashboard</h1>
          <p className="vendor-dashboard__lede">
            Integration status, last sync, vendor response, and retry for Mock SAP / future ERP adapters.
          </p>
        </div>
        <nav className="vendor-dashboard__nav">
          <Link to={ROUTES.financeDashboard}>Finance Queue</Link>
          <Link to={ROUTES.budgetDashboard}>Budget</Link>
          <button type="button" className="vendor-dashboard__refresh" onClick={load}>
            Refresh
          </button>
          <Link to={ROUTES.logout}>Logout</Link>
        </nav>
      </header>

      {isLoading ? (
        <p>Loading vendor integrations...</p>
      ) : error ? (
        <p className="vendor-dashboard__error">{error}</p>
      ) : rows.length === 0 ? (
        <p className="vendor-dashboard__empty">
          No vendor integrations yet. Approve a claim through finance to post to Mock SAP.
        </p>
      ) : (
        <div className="vendor-table-wrap">
          <table className="vendor-table">
            <thead>
              <tr>
                <th>Claim</th>
                <th>Integration status</th>
                <th>Last sync</th>
                <th>Accounting doc</th>
                <th>Reference</th>
                <th>Vendor / payment</th>
                <th>Vendor response</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {rows.map((row) => (
                <tr key={row.reimbursementId}>
                  <td>
                    <strong>#{row.reimbursementId}</strong>
                    <div className="vendor-table__muted">
                      ${Number(row.amount).toFixed(2)}
                      {row.departmentName ? ` · ${row.departmentName}` : ''}
                    </div>
                    <div className="vendor-table__desc">{row.description}</div>
                  </td>
                  <td>
                    <span className={statusClass(row.integrationStatus)}>
                      {row.integrationStatus?.replace(/_/g, ' ')}
                    </span>
                    <div className="vendor-table__muted">
                      {row.vendorSystem || '—'} · attempts {row.syncAttempts}
                    </div>
                    {row.errorCode && (
                      <div className="vendor-table__error">
                        {row.errorCode}: {row.errorMessage}
                      </div>
                    )}
                  </td>
                  <td>{formatWhen(row.lastSyncAt)}</td>
                  <td>{row.accountingDocument || '—'}</td>
                  <td>{row.referenceNumber || '—'}</td>
                  <td>
                    <div>{row.vendorId || '—'}</div>
                    <div className="vendor-table__muted">
                      {row.paymentStatus?.replace(/_/g, ' ') || '—'}
                      {row.postingDate ? ` · ${row.postingDate}` : ''}
                    </div>
                  </td>
                  <td>
                    <pre className="vendor-response">{row.vendorResponse || '—'}</pre>
                  </td>
                  <td>
                    {row.retryAllowed && (
                      <button
                        type="button"
                        className="vendor-retry"
                        disabled={busyId === row.reimbursementId}
                        onClick={() => handleRetry(row.reimbursementId)}
                      >
                        {busyId === row.reimbursementId ? 'Retrying…' : 'Retry'}
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default VendorDashboard;
