import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useRegister } from '../hooks/useRegister';
import { ROUTES } from '../../../shared/utils/constants';
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
  const navigate = useNavigate();
  const { register } = useRegister();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setUser((prev) => ({ ...prev, [name]: value }));
  };

  const handleRegister = async () => {
    try {
      const { confirmPassword, ...userData } = user;
      await register(userData, confirmPassword);
      alert('Registration successful! You can now log in.');
    } catch (error) {
      if (error instanceof Error && error.message === 'Passwords do not match!') {
        alert('Passwords do not match!');
        return;
      }
      console.error('Registration failed', error);
      alert('Registration failed. Please try again.');
    }
  };

  return (
    <div className="login">
      <div className="text-container">
        <h1>Create a New Account</h1>
        <h3>Join us to manage your reimbursements!</h3>
      </div>
      <div className="input-container">
        <Icons.UserCircle className="icon" />
        <input type="text" name="firstName" placeholder="First Name" onChange={handleChange} required />
      </div>
      <div className="input-container">
        <Icons.IdBadge className="icon" />
        <input type="text" name="lastName" placeholder="Last Name" onChange={handleChange} required />
      </div>
      <div className="input-container">
        <Icons.User className="icon" />
        <input type="text" name="username" placeholder="Username" onChange={handleChange} required />
      </div>
      <div className="input-container">
        <Icons.Envelope className="icon" />
        <input type="email" name="email" placeholder="Email (optional)" onChange={handleChange} />
      </div>
      <div className="input-container">
        <Icons.Briefcase className="icon" />
        <select name="role" onChange={handleChange} required>
          <option value="employee">Employee</option>
          <option value="manager">Manager</option>
        </select>
      </div>
      <div className="input-container">
        <Icons.Lock className="icon" />
        <input type="password" name="password" placeholder="Password" onChange={handleChange} required />
      </div>
      <div className="input-container">
        <Icons.Lock className="icon" />
        <input type="password" name="confirmPassword" placeholder="Confirm Password" onChange={handleChange} required />
      </div>
      <button className="login-button" onClick={handleRegister}>Sign Up</button>
      <p className="register-link">
        Have an account? <span onClick={() => navigate(ROUTES.login)}>Log in</span>
      </p>
    </div>
  );
};

export default Register;
