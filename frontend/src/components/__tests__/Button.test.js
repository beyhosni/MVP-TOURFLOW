import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import Button from '../Button';

describe('Button Component', () => {
  test('renders button with text', () => {
    render(<Button>Click me</Button>);
    const buttonElement = screen.getByText(/click me/i);
    expect(buttonElement).toBeInTheDocument();
  });

  test('applies variant classes correctly', () => {
    const { container } = render(<Button variant="primary">Primary Button</Button>);
    const button = container.querySelector('button');
    expect(button).toHaveClass('btn-primary');
  });

  test('applies size classes correctly', () => {
    const { container } = render(<Button size="large">Large Button</Button>);
    const button = container.querySelector('button');
    expect(button).toHaveClass('btn-large');
  });

  test('is disabled when disabled prop is true', () => {
    render(<Button disabled>Disabled Button</Button>);
    const buttonElement = screen.getByText(/disabled button/i);
    expect(buttonElement).toBeDisabled();
  });

  test('calls onClick handler when clicked', () => {
    const handleClick = jest.fn();
    render(<Button onClick={handleClick}>Click me</Button>);
    const buttonElement = screen.getByText(/click me/i);
    fireEvent.click(buttonElement);
    expect(handleClick).toHaveBeenCalledTimes(1);
  });

  test('does not call onClick when disabled', () => {
    const handleClick = jest.fn();
    render(<Button disabled onClick={handleClick}>Disabled Button</Button>);
    const buttonElement = screen.getByText(/disabled button/i);
    fireEvent.click(buttonElement);
    expect(handleClick).not.toHaveBeenCalled();
  });

  test('renders with loading state', () => {
    render(<Button loading>Loading Button</Button>);
    const buttonElement = screen.getByText(/loading button/i);
    expect(buttonElement).toBeDisabled();
    expect(buttonElement).toHaveClass('btn-loading');
  });

  test('renders with icon', () => {
    const { container } = render(<Button icon="search">Search</Button>);
    const icon = container.querySelector('.icon');
    expect(icon).toBeInTheDocument();
    expect(icon).toHaveClass('icon-search');
  });

  test('renders as link when href is provided', () => {
    render(<Button href="https://example.com">Link Button</Button>);
    const linkElement = screen.getByText(/link button/i);
    expect(linkElement.tagName).toBe('A');
    expect(linkElement).toHaveAttribute('href', 'https://example.com');
  });

  test('applies custom className', () => {
    const { container } = render(<Button className="custom-class">Custom Button</Button>);
    const button = container.querySelector('button');
    expect(button).toHaveClass('custom-class');
  });
});
