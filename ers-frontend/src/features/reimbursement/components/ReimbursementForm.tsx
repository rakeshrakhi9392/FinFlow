import React, { useEffect, useState } from 'react';
import { reimbursementService } from '../../../services/reimbursementService';
import { budgetService } from '../../../services/budgetService';
import { ExpenseCategory } from '../../../types';
import { getErrorMessage } from '../../../shared/utils/errorUtils';
import { Icons } from '../../../shared/utils/icons';
import './ReimbursementForm.css';

interface ReimbursementFormProps {
  onReimbursementSubmit: () => void;
}

export const ReimbursementForm: React.FC<ReimbursementFormProps> = ({ onReimbursementSubmit }) => {
  const [formData, setFormData] = useState({
    amount: '',
    description: '',
    categoryId: '',
  });
  const [categories, setCategories] = useState<ExpenseCategory[]>([]);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    budgetService.listCategories()
      .then(setCategories)
      .catch((err) => setError(getErrorMessage(err, 'Failed to load categories')));
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError('');
    setMessage('');
    setIsSubmitting(true);
    try {
      const payload = {
        amount: formData.amount,
        description: formData.description,
        ...(formData.categoryId ? { categoryId: Number(formData.categoryId) } : {}),
      };
      const created = await reimbursementService.create(payload);
      const escalated = !!created.requiresSeniorReview;
      setMessage(
        escalated
          ? 'Submitted for manager review. This claim is flagged for senior manager escalation.'
          : 'Reimbursement submitted. It is now in Manager Review.'
      );
      setFormData({ amount: '', description: '', categoryId: '' });
      onReimbursementSubmit();
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to submit reimbursement'));
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <section className="form-container" aria-labelledby="submit-claim-heading">
      <div className="form-content">
        <h2 id="submit-claim-heading">Submit your reimbursement request</h2>
        <p>Fill out the form below. Claims may escalate by amount threshold or remaining budget.</p>
        {message ? <p className="form-success" role="status">{message}</p> : null}
        {error ? <p className="auth-error" role="alert">{error}</p> : null}
        <form onSubmit={handleSubmit}>
          <div className="input-container">
            <Icons.Money className="icon" aria-hidden="true" />
            <label htmlFor="claim-amount" className="sr-only">Amount</label>
            <input
              id="claim-amount"
              type="number"
              name="amount"
              placeholder="Amount"
              value={formData.amount}
              onChange={handleChange}
              required
              min="0.01"
              step="0.01"
            />
          </div>
          <div className="input-container">
            <label htmlFor="claim-category" className="sr-only">Expense category</label>
            <select
              id="claim-category"
              name="categoryId"
              value={formData.categoryId}
              onChange={handleChange}
              aria-label="Expense category"
            >
              <option value="">Expense category (optional)</option>
              {categories.map((category) => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
          </div>
          <div className="input-container">
            <Icons.AlignLeft className="icon" aria-hidden="true" />
            <label htmlFor="claim-description" className="sr-only">Description</label>
            <textarea
              id="claim-description"
              name="description"
              placeholder="Description"
              value={formData.description}
              onChange={handleChange}
              required
              maxLength={500}
            />
          </div>
          <button type="submit" disabled={isSubmitting}>
            {isSubmitting ? 'Submitting…' : 'Submit'}
          </button>
        </form>
      </div>
    </section>
  );
};

export default ReimbursementForm;
