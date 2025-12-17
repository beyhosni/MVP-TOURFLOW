import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import RegisterForm from '../RegisterForm';

describe('RegisterForm Component', () => {
  test('renders form with all required fields', () => {
    render(<RegisterForm />);

    expect(screen.getByLabelText('First Name')).toBeInTheDocument();
    expect(screen.getByLabelText('Last Name')).toBeInTheDocument();
    expect(screen.getByLabelText('Email')).toBeInTheDocument();
    expect(screen.getByLabelText('Password')).toBeInTheDocument();
    expect(screen.getByLabelText('Confirm Password')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Sign Up' })).toBeInTheDocument();
  });

  test('calls onSubmit with correct data when form is valid', async () => {
    const handleSubmit = jest.fn();
    render(<RegisterForm onSubmit={handleSubmit} />);

    const firstNameInput = screen.getByLabelText('First Name');
    const lastNameInput = screen.getByLabelText('Last Name');
    const emailInput = screen.getByLabelText('Email');
    const passwordInput = screen.getByLabelText('Password');
    const confirmPasswordInput = screen.getByLabelText('Confirm Password');
    const submitButton = screen.getByRole('button', { name: 'Sign Up' });

    fireEvent.change(firstNameInput, { target: { value: 'John' } });
    fireEvent.change(lastNameInput, { target: { value: 'Doe' } });
    fireEvent.change(emailInput, { target: { value: 'john.doe@example.com' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.change(confirmPasswordInput, { target: { value: 'password123' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(handleSubmit).toHaveBeenCalledTimes(1);
      expect(handleSubmit).toHaveBeenCalledWith({
        firstName: 'John',
        lastName: 'Doe',
        email: 'john.doe@example.com',
        password: 'password123',
        confirmPassword: 'password123',
        agreeToTerms: false
      });
    });
  });

  test('validates required fields', async () => {
    render(<RegisterForm />);

    const submitButton = screen.getByRole('button', { name: 'Sign Up' });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('First name is required')).toBeInTheDocument();
      expect(screen.getByText('Last name is required')).toBeInTheDocument();
      expect(screen.getByText('Email is required')).toBeInTheDocument();
      expect(screen.getByText('Password is required')).toBeInTheDocument();
      expect(screen.getByText('Confirm password is required')).toBeInTheDocument();
    });
  });

  test('validates email format', async () => {
    render(<RegisterForm />);

    const emailInput = screen.getByLabelText('Email');
    const submitButton = screen.getByRole('button', { name: 'Sign Up' });

    fireEvent.change(emailInput, { target: { value: 'invalid-email' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Please enter a valid email')).toBeInTheDocument();
    });
  });

  test('validates password length', async () => {
    render(<RegisterForm />);

    const passwordInput = screen.getByLabelText('Password');
    const submitButton = screen.getByRole('button', { name: 'Sign Up' });

    fireEvent.change(passwordInput, { target: { value: '123' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Password must be at least 8 characters')).toBeInTheDocument();
    });
  });

  test('validates password complexity', async () => {
    render(<RegisterForm />);

    const passwordInput = screen.getByLabelText('Password');
    const submitButton = screen.getByRole('button', { name: 'Sign Up' });

    fireEvent.change(passwordInput, { target: { value: 'password' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Password must contain at least one uppercase letter, one lowercase letter, and one number')).toBeInTheDocument();
    });
  });

  test('validates password match', async () => {
    render(<RegisterForm />);

    const passwordInput = screen.getByLabelText('Password');
    const confirmPasswordInput = screen.getByLabelText('Confirm Password');
    const submitButton = screen.getByRole('button', { name: 'Sign Up' });

    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.change(confirmPasswordInput, { target: { value: 'password456' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Passwords do not match')).toBeInTheDocument();
    });
  });

  test('validates terms agreement', async () => {
    render(<RegisterForm />);

    const firstNameInput = screen.getByLabelText('First Name');
    const lastNameInput = screen.getByLabelText('Last Name');
    const emailInput = screen.getByLabelText('Email');
    const passwordInput = screen.getByLabelText('Password');
    const confirmPasswordInput = screen.getByLabelText('Confirm Password');
    const submitButton = screen.getByRole('button', { name: 'Sign Up' });

    fireEvent.change(firstNameInput, { target: { value: 'John' } });
    fireEvent.change(lastNameInput, { target: { value: 'Doe' } });
    fireEvent.change(emailInput, { target: { value: 'john.doe@example.com' } });
    fireEvent.change(passwordInput, { target: { value: 'Password123' } });
    fireEvent.change(confirmPasswordInput, { target: { value: 'Password123' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('You must agree to the terms and conditions')).toBeInTheDocument();
    });
  });

  test('calls onSubmit with agreeToTerms when checkbox is checked', async () => {
    const handleSubmit = jest.fn();
    render(<RegisterForm onSubmit={handleSubmit} />);

    const firstNameInput = screen.getByLabelText('First Name');
    const lastNameInput = screen.getByLabelText('Last Name');
    const emailInput = screen.getByLabelText('Email');
    const passwordInput = screen.getByLabelText('Password');
    const confirmPasswordInput = screen.getByLabelText('Confirm Password');
    const agreeToTermsCheckbox = screen.getByLabelText('I agree to the terms and conditions');
    const submitButton = screen.getByRole('button', { name: 'Sign Up' });

    fireEvent.change(firstNameInput, { target: { value: 'John' } });
    fireEvent.change(lastNameInput, { target: { value: 'Doe' } });
    fireEvent.change(emailInput, { target: { value: 'john.doe@example.com' } });
    fireEvent.change(passwordInput, { target: { value: 'Password123' } });
    fireEvent.change(confirmPasswordInput, { target: { value: 'Password123' } });
    fireEvent.click(agreeToTermsCheckbox);
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(handleSubmit).toHaveBeenCalledTimes(1);
      expect(handleSubmit).toHaveBeenCalledWith({
        firstName: 'John',
        lastName: 'Doe',
        email: 'john.doe@example.com',
        password: 'Password123',
        confirmPassword: 'Password123',
        agreeToTerms: true
      });
    });
  });

  test('displays loading state when submitting', async () => {
    const handleSubmit = jest.fn(() => new Promise(resolve => setTimeout(resolve, 100)));
    render(<RegisterForm onSubmit={handleSubmit} />);

    const firstNameInput = screen.getByLabelText('First Name');
    const lastNameInput = screen.getByLabelText('Last Name');
    const emailInput = screen.getByLabelText('Email');
    const passwordInput = screen.getByLabelText('Password');
    const confirmPasswordInput = screen.getByLabelText('Confirm Password');
    const agreeToTermsCheckbox = screen.getByLabelText('I agree to the terms and conditions');
    const submitButton = screen.getByRole('button', { name: 'Sign Up' });

    fireEvent.change(firstNameInput, { target: { value: 'John' } });
    fireEvent.change(lastNameInput, { target: { value: 'Doe' } });
    fireEvent.change(emailInput, { target: { value: 'john.doe@example.com' } });
    fireEvent.change(passwordInput, { target: { value: 'Password123' } });
    fireEvent.change(confirmPasswordInput, { target: { value: 'Password123' } });
    fireEvent.click(agreeToTermsCheckbox);
    fireEvent.click(submitButton);

    // Check for loading state
    expect(screen.getByText('Creating account...')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Creating account...' })).toBeDisabled();
  });

  test('displays error message when registration fails', async () => {
    const handleSubmit = jest.fn(() => Promise.reject(new Error('Email already exists')));
    render(<RegisterForm onSubmit={handleSubmit} />);

    const firstNameInput = screen.getByLabelText('First Name');
    const lastNameInput = screen.getByLabelText('Last Name');
    const emailInput = screen.getByLabelText('Email');
    const passwordInput = screen.getByLabelText('Password');
    const confirmPasswordInput = screen.getByLabelText('Confirm Password');
    const agreeToTermsCheckbox = screen.getByLabelText('I agree to the terms and conditions');
    const submitButton = screen.getByRole('button', { name: 'Sign Up' });

    fireEvent.change(firstNameInput, { target: { value: 'John' } });
    fireEvent.change(lastNameInput, { target: { value: 'Doe' } });
    fireEvent.change(emailInput, { target: { value: 'john.doe@example.com' } });
    fireEvent.change(passwordInput, { target: { value: 'Password123' } });
    fireEvent.change(confirmPasswordInput, { target: { value: 'Password123' } });
    fireEvent.click(agreeToTermsCheckbox);
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Email already exists')).toBeInTheDocument();
    });
  });

  test('applies custom className', () => {
    const { container } = render(<RegisterForm className="custom-register-form" />);
    const form = container.querySelector('.register-form');
    expect(form).toHaveClass('custom-register-form');
  });

  test('displays login link', () => {
    render(<RegisterForm />);

    expect(screen.getByText('Already have an account?')).toBeInTheDocument();
    expect(screen.getByText('Sign in')).toBeInTheDocument();
  });

  test('calls onLoginClick when login link is clicked', () => {
    const handleLoginClick = jest.fn();
    render(<RegisterForm onLoginClick={handleLoginClick} />);

    const loginLink = screen.getByText('Sign in');
    fireEvent.click(loginLink);

    expect(handleLoginClick).toHaveBeenCalledTimes(1);
  });

  test('toggles password visibility when eye icon is clicked', () => {
    render(<RegisterForm />);

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

  test('toggles confirm password visibility when eye icon is clicked', () => {
    render(<RegisterForm />);

    const confirmPasswordInput = screen.getByLabelText('Confirm Password');
    const toggleButton = screen.getByLabelText('Toggle confirm password visibility');

    // Initially password should be hidden
    expect(confirmPasswordInput).toHaveAttribute('type', 'password');

    // Click to show password
    fireEvent.click(toggleButton);
    expect(confirmPasswordInput).toHaveAttribute('type', 'text');

    // Click to hide password
    fireEvent.click(toggleButton);
    expect(confirmPasswordInput).toHaveAttribute('type', 'password');
  });

  test('displays social login buttons when showSocialLogin is true', () => {
    render(<RegisterForm showSocialLogin={true} />);

    expect(screen.getByText('Or sign up with')).toBeInTheDocument();
    expect(screen.getByTitle('Sign up with Google')).toBeInTheDocument();
    expect(screen.getByTitle('Sign up with Facebook')).toBeInTheDocument();
  });

  test('calls onGoogleRegisterClick when Google register button is clicked', () => {
    const handleGoogleRegisterClick = jest.fn();
    render(<RegisterForm showSocialLogin={true} onGoogleRegisterClick={handleGoogleRegisterClick} />);

    const googleButton = screen.getByTitle('Sign up with Google');
    fireEvent.click(googleButton);

    expect(handleGoogleRegisterClick).toHaveBeenCalledTimes(1);
  });

  test('calls onFacebookRegisterClick when Facebook register button is clicked', () => {
    const handleFacebookRegisterClick = jest.fn();
    render(<RegisterForm showSocialLogin={true} onFacebookRegisterClick={handleFacebookRegisterClick} />);

    const facebookButton = screen.getByTitle('Sign up with Facebook');
    fireEvent.click(facebookButton);

    expect(handleFacebookRegisterClick).toHaveBeenCalledTimes(1);
  });
});
