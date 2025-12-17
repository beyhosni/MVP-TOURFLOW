import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import ErrorBoundary from '../ErrorBoundary';

// Component that throws an error for testing
const ThrowError = ({ shouldThrow }) => {
  if (shouldThrow) {
    throw new Error('Test error');
  }
  return <div>No error</div>;
};

describe('ErrorBoundary Component', () => {
  // Mock console.error to avoid error output in test console
  let originalError;
  beforeEach(() => {
    originalError = console.error;
    console.error = jest.fn();
  });

  afterEach(() => {
    console.error = originalError;
  });

  test('renders children when there is no error', () => {
    render(
      <ErrorBoundary>
        <ThrowError shouldThrow={false} />
      </ErrorBoundary>
    );

    expect(screen.getByText('No error')).toBeInTheDocument();
    expect(screen.queryByText('Something went wrong')).not.toBeInTheDocument();
  });

  test('displays error message when child component throws an error', () => {
    render(
      <ErrorBoundary>
        <ThrowError shouldThrow={true} />
      </ErrorBoundary>
    );

    expect(screen.getByText('Something went wrong')).toBeInTheDocument();
    expect(screen.getByText('An error occurred while rendering this component. Please try again later.')).toBeInTheDocument();
  });

  test('displays custom error message when error prop is provided', () => {
    render(
      <ErrorBoundary 
        error="Custom error message" 
        errorDescription="This is a custom error description."
      >
        <ThrowError shouldThrow={true} />
      </ErrorBoundary>
    );

    expect(screen.getByText('Custom error message')).toBeInTheDocument();
    expect(screen.getByText('This is a custom error description.')).toBeInTheDocument();
  });

  test('calls onError when an error occurs', () => {
    const handleError = jest.fn();

    render(
      <ErrorBoundary onError={handleError}>
        <ThrowError shouldThrow={true} />
      </ErrorBoundary>
    );

    expect(handleError).toHaveBeenCalledTimes(1);
    expect(handleError).toHaveBeenCalledWith(expect.any(Error), expect.any(ErrorInfo));
  });

  test('displays retry button when showRetry prop is true', () => {
    render(
      <ErrorBoundary showRetry={true}>
        <ThrowError shouldThrow={true} />
      </ErrorBoundary>
    );

    expect(screen.getByText('Retry')).toBeInTheDocument();
  });

  test('calls onRetry when retry button is clicked', () => {
    const handleRetry = jest.fn();

    render(
      <ErrorBoundary showRetry={true} onRetry={handleRetry}>
        <ThrowError shouldThrow={true} />
      </ErrorBoundary>
    );

    const retryButton = screen.getByText('Retry');
    retryButton.click();

    expect(handleRetry).toHaveBeenCalledTimes(1);
  });

  test('displays custom retry button text when retryButtonText prop is provided', () => {
    render(
      <ErrorBoundary showRetry={true} retryButtonText="Try Again">
        <ThrowError shouldThrow={true} />
      </ErrorBoundary>
    );

    expect(screen.getByText('Try Again')).toBeInTheDocument();
  });

  test('applies custom className', () => {
    const { container } = render(
      <ErrorBoundary className="custom-error-boundary">
        <ThrowError shouldThrow={true} />
      </ErrorBoundary>
    );

    const errorBoundary = container.querySelector('.error-boundary');
    expect(errorBoundary).toHaveClass('custom-error-boundary');
  });

  test('displays custom icon when errorIcon prop is provided', () => {
    render(
      <ErrorBoundary errorIcon="custom-error-icon">
        <ThrowError shouldThrow={true} />
      </ErrorBoundary>
    );

    const errorIcon = document.querySelector('.custom-error-icon');
    expect(errorIcon).toBeInTheDocument();
  });

  test('displays error details in development mode', () => {
    // Mock NODE_ENV to be 'development'
    const originalNodeEnv = process.env.NODE_ENV;
    process.env.NODE_ENV = 'development';

    render(
      <ErrorBoundary>
        <ThrowError shouldThrow={true} />
      </ErrorBoundary>
    );

    expect(screen.getByText(/Error: Test error/)).toBeInTheDocument();
    expect(screen.getByText(/Component Stack/)).toBeInTheDocument();

    // Restore original NODE_ENV
    process.env.NODE_ENV = originalNodeEnv;
  });

  test('hides error details in production mode', () => {
    // Mock NODE_ENV to be 'production'
    const originalNodeEnv = process.env.NODE_ENV;
    process.env.NODE_ENV = 'production';

    render(
      <ErrorBoundary>
        <ThrowError shouldThrow={true} />
      </ErrorBoundary>
    );

    expect(screen.queryByText(/Error: Test error/)).not.toBeInTheDocument();
    expect(screen.queryByText(/Component Stack/)).not.toBeInTheDocument();

    // Restore original NODE_ENV
    process.env.NODE_ENV = originalNodeEnv;
  });

  test('displays fallback component when fallback prop is provided', () => {
    const FallbackComponent = () => (
      <div data-testid="custom-fallback">Custom fallback component</div>
    );

    render(
      <ErrorBoundary fallback={<FallbackComponent />}>
        <ThrowError shouldThrow={true} />
      </ErrorBoundary>
    );

    expect(screen.getByTestId('custom-fallback')).toBeInTheDocument();
    expect(screen.getByText('Custom fallback component')).toBeInTheDocument();
  });

  test('resets error when children change after error', () => {
    const { rerender } = render(
      <ErrorBoundary>
        <ThrowError shouldThrow={true} />
      </ErrorBoundary>
    );

    expect(screen.getByText('Something went wrong')).toBeInTheDocument();

    // Rerender with no error
    rerender(
      <ErrorBoundary>
        <ThrowError shouldThrow={false} />
      </ErrorBoundary>
    );

    expect(screen.getByText('No error')).toBeInTheDocument();
    expect(screen.queryByText('Something went wrong')).not.toBeInTheDocument();
  });
});
