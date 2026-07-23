import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useRegister } from '../hooks/useRegister';
import { ROUTES } from '../../../shared/utils/constants';
import { getErrorMessage } from '../../../shared/utils/errorUtils';
import { Icons } from '../../../shared/utils/icons';
import '../components/Auth.css';

export const Register: React.FC = () => {
  const [user, setUser] = useState({
    username: '',
    password: '',
    email: '',
    firstName: '',
    lastName: '',
    confirmPassword: '',
    role: 'employee',
  });
  const [formError, setFormError] = useState('');
  const { register, isSubmitting, error } = useRegister();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setUser((prev) => ({ ...prev, [name]: value }));
  };

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormError('');
    try {
      const { confirmPassword, ...userData } = user;
      await register(userData, confirmPassword);
    } catch (err) {
      if (err instanceof Error && err.message === 'Passwords do not match!') {
        setFormError('Passwords do not match.');
        return;
      }
      setFormError(getErrorMessage(err, 'Registration failed. Please try again.'));
    }
  };

  const displayError = formError || error;

  return (
    <main className="login" aria-labelledby="register-heading">
      <div className="text-container">
        <h1 id="register-heading">Create an account</h1>
        <p className="auth-subtitle">Join to submit and track reimbursements</p>
      </div>
      <form onSubmit={handleRegister} noValidate>
        {displayError ? (
          <p className="auth-error" role="alert">{displayError}</p>
        ) : null}
        <div className="input-container">
          <Icons.UserCircle className="icon" aria-hidden="true" />
          <label htmlFor="register-firstName" className="sr-only">First name</label>
          <input
            id="register-firstName"
            type="text"
            name="firstName"
            autoComplete="given-name"
            placeholder="First name"
            value={user.firstName}
            onChange={handleChange}
            required
          />
        </div>
        <div className="input-container">
          <Icons.IdBadge className="icon" aria-hidden="true" />
          <label htmlFor="register-lastName" className="sr-only">Last name</label>
          <input
            id="register-lastName"
            type="text"
            name="lastName"
            autoComplete="family-name"
            placeholder="Last name"
            value={user.lastName}
            onChange={handleChange}
            required
          />
        </div>
        <div className="input-container">
          <Icons.User className="icon" aria-hidden="true" />
          <label htmlFor="register-username" className="sr-only">Username</label>
          <input
            id="register-username"
            type="text"
            name="username"
            autoComplete="username"
            placeholder="Username"
            value={user.username}
            onChange={handleChange}
            required
            minLength={3}
          />
        </div>
        <div className="input-container">
          <Icons.Envelope className="icon" aria-hidden="true" />
          <label htmlFor="register-email" className="sr-only">Email</label>
          <input
            id="register-email"
            type="email"
            name="email"
            autoComplete="email"
            placeholder="Email (optional)"
            value={user.email}
            onChange={handleChange}
          />
        </div>
        <div className="input-container">
          <Icons.Briefcase className="icon" aria-hidden="true" />
          <label htmlFor="register-role" className="sr-only">Role</label>
          <select
            id="register-role"
            name="role"
            value={user.role}
            onChange={handleChange}
            required
            aria-label="Role"
          >
            <option value="employee">Employee</option>
            <option value="manager">Manager</option>
          </select>
        </div>
        <div className="input-container">
          <Icons.Lock className="icon" aria-hidden="true" />
          <label htmlFor="register-password" className="sr-only">Password</label>
          <input
            id="register-password"
            type="password"
            name="password"
            autoComplete="new-password"
            placeholder="Password (min 8 characters)"
            value={user.password}
            onChange={handleChange}
            required
            minLength={8}
          />
        </div>
        <div className="input-container">
          <Icons.Lock className="icon" aria-hidden="true" />
          <label htmlFor="register-confirm" className="sr-only">Confirm password</label>
          <input
            id="register-confirm"
            type="password"
            name="confirmPassword"
            autoComplete="new-password"
            placeholder="Confirm password"
            value={user.confirmPassword}
            onChange={handleChange}
            required
            minLength={8}
          />
        </div>
        <button className="login-button" type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Creating account…' : 'Sign up'}
        </button>
      </form>
      <p className="register-link">
        Have an account? <Link to={ROUTES.login}>Log in</Link>
      </p>
    </main>
  );
};

export default Register;
