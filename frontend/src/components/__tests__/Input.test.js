import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import Input from '../Input';

describe('Input Component', () => {
  test('renders input with placeholder', () => {
    render(<Input placeholder="Enter your name" />);
    const inputElement = screen.getByPlaceholderText(/enter your name/i);
    expect(inputElement).toBeInTheDocument();
  });

  test('applies type correctly', () => {
    render(<Input type="email" />);
    const inputElement = screen.getByRole('textbox');
    expect(inputElement).toHaveAttribute('type', 'email');
  });

  test('calls onChange handler when value changes', () => {
    const handleChange = jest.fn();
    render(<Input onChange={handleChange} />);
    const inputElement = screen.getByRole('textbox');
    fireEvent.change(inputElement, { target: { value: 'test value' } });
    expect(handleChange).toHaveBeenCalledTimes(1);
  });

  test('displays error message when error prop is provided', () => {
    render(<Input error="This field is required" />);
    const errorMessage = screen.getByText(/this field is required/i);
    expect(errorMessage).toBeInTheDocument();
    expect(errorMessage).toHaveClass('input-error');
  });

  test('applies disabled state correctly', () => {
    render(<Input disabled />);
    const inputElement = screen.getByRole('textbox');
    expect(inputElement).toBeDisabled();
  });

  test('applies required attribute when required prop is true', () => {
    render(<Input required />);
    const inputElement = screen.getByRole('textbox');
    expect(inputElement).toBeRequired();
  });

  test('applies custom className', () => {
    const { container } = render(<Input className="custom-input" />);
    const inputElement = container.querySelector('input');
    expect(inputElement).toHaveClass('custom-input');
  });

  test('displays label when label prop is provided', () => {
    render(<Input label="Name" />);
    const labelElement = screen.getByText(/name/i);
    expect(labelElement).toBeInTheDocument();
    expect(labelElement.tagName).toBe('LABEL');
  });

  test('associates label with input correctly', () => {
    render(<Input label="Email" id="email-input" />);
    const labelElement = screen.getByText(/email/i);
    const inputElement = screen.getByDisplayValue('');
    expect(labelElement).toHaveAttribute('for', 'email-input');
    expect(inputElement).toHaveAttribute('id', 'email-input');
  });

  test('applies variant classes correctly', () => {
    const { container } = render(<Input variant="outlined" />);
    const inputElement = container.querySelector('input');
    expect(inputElement).toHaveClass('input-outlined');
  });

  test('applies size classes correctly', () => {
    const { container } = render(<Input size="large" />);
    const inputElement = container.querySelector('input');
    expect(inputElement).toHaveClass('input-large');
  });

  test('displays helper text when helperText prop is provided', () => {
    render(<Input helperText="Enter at least 8 characters" />);
    const helperText = screen.getByText(/enter at least 8 characters/i);
    expect(helperText).toBeInTheDocument();
    expect(helperText).toHaveClass('input-helper');
  });

  test('applies focus styles when focused', () => {
    const { container } = render(<Input />);
    const inputElement = screen.getByRole('textbox');
    inputElement.focus();
    expect(inputElement).toHaveFocus();
  });

  test('renders with default value', () => {
    render(<Input defaultValue="default value" />);
    const inputElement = screen.getByDisplayValue(/default value/i);
    expect(inputElement).toBeInTheDocument();
  });

  test('applies maxLength when maxLength prop is provided', () => {
    render(<Input maxLength={10} />);
    const inputElement = screen.getByRole('textbox');
    expect(inputElement).toHaveAttribute('maxlength', '10');
  });

  test('applies min and max when type is number', () => {
    render(<Input type="number" min={1} max={10} />);
    const inputElement = screen.getByRole('spinbutton');
    expect(inputElement).toHaveAttribute('min', '1');
    expect(inputElement).toHaveAttribute('max', '10');
  });
});
