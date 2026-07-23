import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import {
  Bar,
  BarChart,
  CartesianGrid,
  Legend,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';
import { budgetService } from '../../../services/budgetService';
import { BudgetSummary, DepartmentBudget } from '../../../types';
import { getErrorMessage } from '../../../shared/utils/errorUtils';
import { ROUTES } from '../../../shared/utils/constants';
import './BudgetDashboard.css';

const currency = (value: number) =>
  value.toLocaleString(undefined, { style: 'currency', currency: 'USD', maximumFractionDigits: 0 });

const BudgetDashboard: React.FC = () => {
  const [summary, setSummary] = useState<BudgetSummary | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const load = async () => {
      try {
        setIsLoading(true);
        const data = await budgetService.getSummary();
        setSummary(data);
        setError(null);
      } catch (err) {
        setError(getErrorMessage(err, 'Failed to load budget dashboard'));
      } finally {
        setIsLoading(false);
      }
    };
    load();
  }, []);

  const chartData =
    summary?.departments.map((dept: DepartmentBudget) => ({
      name: dept.departmentCode,
      allocated: dept.allocatedAmount,
      spent: dept.spentAmount,
      remaining: dept.remainingBudget,
    })) ?? [];

  return (
    <div className="budget-dashboard">
      <header className="budget-dashboard__header">
        <div>
          <p className="budget-dashboard__eyebrow">Enterprise Finance</p>
          <h1>Budget Dashboard</h1>
          {summary && (
            <p className="budget-dashboard__period">
              Fiscal {summary.fiscalYear} · {summary.quarter}
            </p>
          )}
        </div>
        <nav className="budget-dashboard__nav">
          <Link to={ROUTES.managerDashboard}>Approvals</Link>
          <Link to={ROUTES.logout}>Logout</Link>
        </nav>
      </header>

      {isLoading ? (
        <p>Loading budget data...</p>
      ) : error ? (
        <p className="budget-dashboard__error">{error}</p>
      ) : summary ? (
        <>
          <section className="budget-summary-strip" aria-label="Budget summary">
            <div>
              <span>Total Allocated</span>
              <strong>{currency(summary.totalAllocated)}</strong>
            </div>
            <div>
              <span>Department Spend</span>
              <strong>{currency(summary.totalSpent)}</strong>
            </div>
            <div>
              <span>Remaining Budget</span>
              <strong>{currency(summary.remainingBudget)}</strong>
            </div>
            <div>
              <span>Budget Utilization</span>
              <strong>{summary.utilizationPercent.toFixed(1)}%</strong>
            </div>
          </section>

          <section className="budget-chart-panel" aria-label="Department spend chart">
            <h2>Allocated vs Spent by Department</h2>
            <div className="budget-chart-panel__chart">
              <ResponsiveContainer width="100%" height={320}>
                <BarChart data={chartData} margin={{ top: 8, right: 16, left: 8, bottom: 8 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#d7e0ea" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  <Tooltip formatter={(value) => currency(Number(value ?? 0))} />
                  <Legend />
                  <Bar dataKey="allocated" name="Allocated" fill="#1f4e79" radius={[4, 4, 0, 0]} />
                  <Bar dataKey="spent" name="Spent" fill="#2f7d4a" radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </section>

          <section className="department-cards" aria-label="Department budgets">
            {summary.departments.map((dept) => (
              <article key={dept.budgetId} className="department-card">
                <header>
                  <h3>{dept.departmentName}</h3>
                  <span>{dept.departmentCode}</span>
                </header>
                <dl>
                  <div>
                    <dt>Allocated</dt>
                    <dd>{currency(dept.allocatedAmount)}</dd>
                  </div>
                  <div>
                    <dt>Spent</dt>
                    <dd>{currency(dept.spentAmount)}</dd>
                  </div>
                  <div>
                    <dt>Remaining</dt>
                    <dd>{currency(dept.remainingBudget)}</dd>
                  </div>
                </dl>
                <div className="progress-block">
                  <div className="progress-block__label">
                    <span>Utilization</span>
                    <span>{dept.utilizationPercent.toFixed(1)}%</span>
                  </div>
                  <div className="progress-bar" role="progressbar" aria-valuenow={dept.utilizationPercent} aria-valuemin={0} aria-valuemax={100}>
                    <div
                      className={`progress-bar__fill ${dept.utilizationPercent >= 90 ? 'is-critical' : dept.utilizationPercent >= 70 ? 'is-warning' : ''}`}
                      style={{ width: `${Math.min(dept.utilizationPercent, 100)}%` }}
                    />
                  </div>
                </div>
              </article>
            ))}
          </section>
        </>
      ) : null}
    </div>
  );
};

export default BudgetDashboard;
