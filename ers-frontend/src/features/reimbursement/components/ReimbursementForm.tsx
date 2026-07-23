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

  useEffect(() => {
    budgetService.listCategories()
      .then(setCategories)
      .catch((err) => console.error('Failed to load categories', err));
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
    try {
      const payload = {
        amount: formData.amount,
        description: formData.description,
        ...(formData.categoryId ? { categoryId: Number(formData.categoryId) } : {}),
      };
      const created = await reimbursementService.create(payload);
      const escalated = !!created.requiresSeniorReview;
      alert(
        escalated
          ? 'Submitted for manager review. This claim is flagged for senior manager escalation (amount and/or budget rules).'
          : 'Reimbursement submitted. It is now in Manager Review.'
      );
      setFormData({ amount: '', description: '', categoryId: '' });
      onReimbursementSubmit();
    } catch (error) {
      alert(getErrorMessage(error, 'Failed to submit reimbursement'));
      console.error(error);
    }
  };

  return (
    <div className="form-container">
      <div className="form-content">
        <h1>Submit Your Reimbursement Request</h1>
        <p>Fill out the form below. Claims may escalate by amount threshold or remaining budget.</p>
        <form onSubmit={handleSubmit}>
          <div className="input-container">
            <Icons.Money className="icon" />
            <input
              type="number"
              name="amount"
              placeholder="Amount"
              value={formData.amount}
              onChange={handleChange}
              required
              min="1"
              step="1"
            />
          </div>
          <div className="input-container">
            <select
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
            <Icons.AlignLeft className="icon" />
            <textarea
              name="description"
              placeholder="Description"
              value={formData.description}
              onChange={handleChange}
              required
            />
          </div>
          <button type="submit">Submit</button>
        </form>
      </div>
    </div>
  );
};

export default ReimbursementForm;
