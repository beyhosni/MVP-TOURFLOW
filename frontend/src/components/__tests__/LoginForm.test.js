import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import LoginForm from '../LoginForm';

describe('LoginForm Component', () => {
  test('renders form with email and password fields', () => {
    render(<LoginForm />);

    expect(screen.getByLabelText('Email')).toBeInTheDocument();
    expect(screen.getByLabelText('Password')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Login' })).toBeInTheDocument();
  });

  test('calls onSubmit with correct data when form is valid', async () => {
    const handleSubmit = jest.fn();
    render(<LoginForm onSubmit={handleSubmit} />);

    const emailInput = screen.getByLabelText('Email');
    const passwordInput = screen.getByLabelText('Password');
    const submitButton = screen.getByRole('button', { name: 'Login' });

    fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(handleSubmit).toHaveBeenCalledTimes(1);
      expect(handleSubmit).toHaveBeenCalledWith({
        email: 'test@example.com',
        password: 'password123',
        rememberMe: false
      });
    });
  });

  test('calls onSubmit with rememberMe when checkbox is checked', async () => {
    const handleSubmit = jest.fn();
    render(<LoginForm onSubmit={handleSubmit} />);

    const emailInput = screen.getByLabelText('Email');
    const passwordInput = screen.getByLabelText('Password');
    const rememberMeCheckbox = screen.getByLabelText('Remember me');
    const submitButton = screen.getByRole('button', { name: 'Login' });

    fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.click(rememberMeCheckbox);
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(handleSubmit).toHaveBeenCalledTimes(1);
      expect(handleSubmit).toHaveBeenCalledWith({
        email: 'test@example.com',
        password: 'password123',
        rememberMe: true
      });
    });
  });

  test('validates required fields', async () => {
    render(<LoginForm />);

    const submitButton = screen.getByRole('button', { name: 'Login' });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Email is required')).toBeInTheDocument();
      expect(screen.getByText('Password is required')).toBeInTheDocument();
    });
  });

  test('validates email format', async () => {
    render(<LoginForm />);

    const emailInput = screen.getByLabelText('Email');
    const submitButton = screen.getByRole('button', { name: 'Login' });

    fireEvent.change(emailInput, { target: { value: 'invalid-email' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Please enter a valid email')).toBeInTheDocument();
    });
  });

  test('validates password length', async () => {
    render(<LoginForm />);

    const emailInput = screen.getByLabelText('Email');
    const passwordInput = screen.getByLabelText('Password');
    const submitButton = screen.getByRole('button', { name: 'Login' });

    fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
    fireEvent.change(passwordInput, { target: { value: '123' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Password must be at least 8 characters')).toBeInTheDocument();
    });
  });

  test('displays loading state when submitting', async () => {
    const handleSubmit = jest.fn(() => new Promise(resolve => setTimeout(resolve, 100)));
    render(<LoginForm onSubmit={handleSubmit} />);

    const emailInput = screen.getByLabelText('Email');
    const passwordInput = screen.getByLabelText('Password');
    const submitButton = screen.getByRole('button', { name: 'Login' });

    fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.click(submitButton);

    // Check for loading state
    expect(screen.getByText('Logging in...')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Logging in...' })).toBeDisabled();
  });

  test('displays error message when login fails', async () => {
    const handleSubmit = jest.fn(() => Promise.reject(new Error('Invalid email or password')));
    render(<LoginForm onSubmit={handleSubmit} />);

    const emailInput = screen.getByLabelText('Email');
    const passwordInput = screen.getByLabelText('Password');
    const submitButton = screen.getByRole('button', { name: 'Login' });

    fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
    fireEvent.change(passwordInput, { target: { value: 'wrongpassword' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Invalid email or password')).toBeInTheDocument();
    });
  });

  test('applies custom className', () => {
    const { container } = render(<LoginForm className="custom-login-form" />);
    const form = container.querySelector('.login-form');
    expect(form).toHaveClass('custom-login-form');
  });

  test('displays forgot password link', () => {
    render(<LoginForm />);

    expect(screen.getByText('Forgot password?')).toBeInTheDocument();
  });

  test('calls onForgotPasswordClick when forgot password link is clicked', () => {
    const handleForgotPasswordClick = jest.fn();
    render(<LoginForm onForgotPasswordClick={handleForgotPasswordClick} />);

    const forgotPasswordLink = screen.getByText('Forgot password?');
    fireEvent.click(forgotPasswordLink);

    expect(handleForgotPasswordClick).toHaveBeenCalledTimes(1);
  });

  test('displays register link', () => {
    render(<LoginForm />);

    expect(screen.getByText('Don't have an account?')).toBeInTheDocument();
    expect(screen.getByText('Sign up')).toBeInTheDocument();
  });

  test('calls onRegisterClick when register link is clicked', () => {
    const handleRegisterClick = jest.fn();
    render(<LoginForm onRegisterClick={handleRegisterClick} />);

    const registerLink = screen.getByText('Sign up');
    fireEvent.click(registerLink);

    expect(handleRegisterClick).toHaveBeenCalledTimes(1);
  });

  test('toggles password visibility when eye icon is clicked', () => {
    render(<LoginForm />);

    const passwordInput = screen.getByLabelText('Password');
    const toggleButton = screen.getByLabelText('Toggle password visibility');

    // Initially password should be hidden
    expect(passwordInput).toHaveAttribute('type', 'password');

    // Click to show password
    fireEvent.click(toggleButton);
    expect(passwordInput).toHaveAttribute('type', 'text');

    // Click to hide password
    fireEvent.click(toggleButton);
    expect(passwordInput).toHaveAttribute('type', 'password');
  });

  test('applies initial values', () => {
    const initialValues = {
      email: 'test@example.com',
      password: 'password123',
      rememberMe: true
    };

    render(<LoginForm initialValues={initialValues} />);

    expect(screen.getByDisplayValue('test@example.com')).toBeInTheDocument();
    expect(screen.getByDisplayValue('password123')).toBeInTheDocument();
    expect(screen.getByLabelText('Remember me')).toBeChecked();
  });

  test('displays social login buttons when showSocialLogin is true', () => {
    render(<LoginForm showSocialLogin={true} />);

    expect(screen.getByText('Or login with')).toBeInTheDocument();
    expect(screen.getByTitle('Login with Google')).toBeInTheDocument();
    expect(screen.getByTitle('Login with Facebook')).toBeInTheDocument();
  });

  test('calls onGoogleLoginClick when Google login button is clicked', () => {
    const handleGoogleLoginClick = jest.fn();
    render(<LoginForm showSocialLogin={true} onGoogleLoginClick={handleGoogleLoginClick} />);

    const googleButton = screen.getByTitle('Login with Google');
    fireEvent.click(googleButton);

    expect(handleGoogleLoginClick).toHaveBeenCalledTimes(1);
  });

  test('calls onFacebookLoginClick when Facebook login button is clicked', () => {
    const handleFacebookLoginClick = jest.fn();
    render(<LoginForm showSocialLogin={true} onFacebookLoginClick={handleFacebookLoginClick} />);

    const facebookButton = screen.getByTitle('Login with Facebook');
    fireEvent.click(facebookButton);

    expect(handleFacebookLoginClick).toHaveBeenCalledTimes(1);
  });
});
