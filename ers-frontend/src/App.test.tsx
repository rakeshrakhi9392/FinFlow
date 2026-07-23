import React from 'react';
import { render, screen } from '@testing-library/react';
import App from './App';
import { AppProvider } from './context/AppContext';

test('renders employee dashboard by default', () => {
  render(
    <AppProvider>
      <App />
    </AppProvider>
  );
  const heading = screen.getByText(/Employee Dashboard/i);
  expect(heading).toBeInTheDocument();
});
