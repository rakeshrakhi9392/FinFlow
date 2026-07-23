import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAppContext } from '../../../context/AppContext';
import { useLogin } from '../hooks/useLogin';
import { dashboardRouteForRole, ROUTES } from '../../../shared/utils/constants';
import { Icons } from '../../../shared/utils/icons';
import { UserFormState } from '../../../types';
import '../components/Auth.css';

interface LoginProps {
  setUserRole: (role: string) => void;
}

export const Login: React.FC<LoginProps> = ({ setUserRole }) => {
  const [user, setUser] = useState<UserFormState>({ username: '', password: '', role: '' });
  const navigate = useNavigate();
  const { user: sessionUser, setUser: setAuthUser } = useAppContext();
  const { login } = useLogin();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setUser((prev) => ({ ...prev, [name]: value }));
  };

  useEffect(() => {
    if (sessionUser) {
      setUserRole(sessionUser.role);
      navigate(dashboardRouteForRole(sessionUser.role));
    }
  }, [sessionUser, setUserRole, navigate]);

  const handleLogin = async () => {
    try {
      const authUser = await login({
        username: user.username,
        password: user.password || '',
      });
      setUserRole(authUser.role);
      setAuthUser(authUser);
    } catch {
      alert('Login Failed!');
    }
  };

  return (
    <div className="login">
      <div className="text-container">
        <h1>Welcome to the Employee Reimbursement System</h1>
        <h3>Sign in to manage your reimbursements!</h3>
        <div className="input-container">
          <Icons.User className="icon" />
          <input type="text" aria-label="Username" placeholder="Username" name="username" onChange={handleChange} />
        </div>
        <div className="input-container">
          <Icons.Lock className="icon" />
          <input type="password" aria-label="Password" placeholder="Password" name="password" onChange={handleChange} />
        </div>
        <button className="login-button" onClick={handleLogin}>Login</button>
        <p className="register-link">Don't have an account? <span onClick={() => navigate(ROUTES.register)}>Sign up</span></p>
      </div>
    </div>
  );
};

export default Login;
