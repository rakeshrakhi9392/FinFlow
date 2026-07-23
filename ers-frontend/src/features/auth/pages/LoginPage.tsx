import React, { useState } from 'react';
import { Link, Navigate } from 'react-router-dom';
import { useAppContext } from '../../../context/AppContext';
import { useLogin } from '../hooks/useLogin';
import { dashboardRouteForRole, ROUTES } from '../../../shared/utils/constants';
import { Icons } from '../../../shared/utils/icons';
import { UserFormState } from '../../../types';
import '../components/Auth.css';

export const Login: React.FC = () => {
  const [user, setUser] = useState<UserFormState>({ username: '', password: '', role: '' });
  const { user: sessionUser } = useAppContext();
  const { login, isSubmitting, error } = useLogin();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setUser((prev) => ({ ...prev, [name]: value }));
  };

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await login({
        username: user.username,
        password: user.password || '',
      });
    } catch {
      // Error message is surfaced via useLogin.
    }
  };

  if (sessionUser) {
    return <Navigate to={dashboardRouteForRole(sessionUser.role)} replace />;
  }

  return (
    <main className="login" aria-labelledby="login-heading">
      <div className="text-container">
        <h1 id="login-heading">Employee Reimbursement System</h1>
        <p className="auth-subtitle">Sign in to manage your reimbursements</p>
      </div>
      <form onSubmit={handleLogin} noValidate>
        {error ? (
          <p className="auth-error" role="alert">{error}</p>
        ) : null}
        <div className="input-container">
          <Icons.User className="icon" aria-hidden="true" />
          <label htmlFor="login-username" className="sr-only">Username</label>
          <input
            id="login-username"
            type="text"
            autoComplete="username"
            placeholder="Username"
            name="username"
            value={user.username}
            onChange={handleChange}
            required
          />
        </div>
        <div className="input-container">
          <Icons.Lock className="icon" aria-hidden="true" />
          <label htmlFor="login-password" className="sr-only">Password</label>
          <input
            id="login-password"
            type="password"
            autoComplete="current-password"
            placeholder="Password"
            name="password"
            value={user.password}
            onChange={handleChange}
            required
          />
        </div>
        <button className="login-button" type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Signing in…' : 'Login'}
        </button>
      </form>
      <p className="register-link">
        Don&apos;t have an account?{' '}
        <Link to={ROUTES.register}>Sign up</Link>
      </p>
    </main>
  );
};

export default Login;
