import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import Loading from '../Loading';

describe('Loading Component', () => {
  test('renders loading indicator', () => {
    render(<Loading />);

    expect(screen.getByTestId('loading-indicator')).toBeInTheDocument();
  });

  test('displays loading text when provided', () => {
    render(<Loading text="Loading tours..." />);

    expect(screen.getByText('Loading tours...')).toBeInTheDocument();
  });

  test('displays default loading text when not provided', () => {
    render(<Loading />);

    expect(screen.getByText('Loading...')).toBeInTheDocument();
  });

  test('applies size variant correctly', () => {
    const { container } = render(<Loading size="large" />);
    const loading = container.querySelector('.loading');
    expect(loading).toHaveClass('loading-large');
  });

  test('applies custom className', () => {
    const { container } = render(<Loading className="custom-loading" />);
    const loading = container.querySelector('.loading');
    expect(loading).toHaveClass('custom-loading');
  });

  test('displays spinner when type is spinner', () => {
    const { container } = render(<Loading type="spinner" />);
    const spinner = container.querySelector('.loading-spinner');
    expect(spinner).toBeInTheDocument();
  });

  test('displays dots when type is dots', () => {
    const { container } = render(<Loading type="dots" />);
    const dots = container.querySelector('.loading-dots');
    expect(dots).toBeInTheDocument();
  });

  test('displays bars when type is bars', () => {
    const { container } = render(<Loading type="bars" />);
    const bars = container.querySelector('.loading-bars');
    expect(bars).toBeInTheDocument();
  });

  test('displays pulse when type is pulse', () => {
    const { container } = render(<Loading type="pulse" />);
    const pulse = container.querySelector('.loading-pulse');
    expect(pulse).toBeInTheDocument();
  });

  test('applies color variant correctly', () => {
    const { container } = render(<Loading color="primary" />);
    const loading = container.querySelector('.loading');
    expect(loading).toHaveClass('loading-primary');
  });

  test('displays overlay when overlay prop is true', () => {
    const { container } = render(<Loading overlay={true} />);
    const overlay = container.querySelector('.loading-overlay');
    expect(overlay).toBeInTheDocument();
  });

  test('displays centered when centered prop is true', () => {
    const { container } = render(<Loading centered={true} />);
    const loading = container.querySelector('.loading');
    expect(loading).toHaveClass('loading-centered');
  });

  test('displays full screen when fullscreen prop is true', () => {
    const { container } = render(<Loading fullscreen={true} />);
    const loading = container.querySelector('.loading');
    expect(loading).toHaveClass('loading-fullscreen');
  });

  test('displays inline when inline prop is true', () => {
    const { container } = render(<Loading inline={true} />);
    const loading = container.querySelector('.loading');
    expect(loading).toHaveClass('loading-inline');
  });

  test('displays custom icon when icon prop is provided', () => {
    const { container } = render(<Loading icon="custom-icon" />);
    const icon = container.querySelector('.custom-icon');
    expect(icon).toBeInTheDocument();
  });

  test('displays custom animation when animation prop is provided', () => {
    const { container } = render(<Loading animation="custom-animation" />);
    const animation = container.querySelector('.loading-animation');
    expect(animation).toHaveClass('custom-animation');
  });

  test('displays with custom speed when speed prop is provided', () => {
    const { container } = render(<Loading speed="slow" />);
    const loading = container.querySelector('.loading');
    expect(loading).toHaveClass('loading-slow');
  });

  test('displays with custom opacity when opacity prop is provided', () => {
    const { container } = render(<Loading opacity={0.5} />);
    const loading = container.querySelector('.loading');
    expect(loading).toHaveStyle('opacity: 0.5');
  });

  test('displays with custom background when background prop is provided', () => {
    const { container } = render(<Loading background="#ffffff" />);
    const loading = container.querySelector('.loading');
    expect(loading).toHaveStyle('background-color: #ffffff');
  });

  test('displays with custom text color when textColor prop is provided', () => {
    const { container } = render(<Loading textColor="#333333" />);
    const loadingText = container.querySelector('.loading-text');
    expect(loadingText).toHaveStyle('color: #333333');
  });
});
