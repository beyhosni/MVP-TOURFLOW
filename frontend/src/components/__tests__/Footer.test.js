import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import { BrowserRouter } from 'react-router-dom';
import Footer from '../Footer';

const MockFooter = () => {
  return (
    <BrowserRouter>
      <Footer />
    </BrowserRouter>
  );
};

describe('Footer Component', () => {
  test('renders footer with logo and navigation links', () => {
    render(<MockFooter />);

    expect(screen.getByAltText('TourFlow Logo')).toBeInTheDocument();
    expect(screen.getByText('About Us')).toBeInTheDocument();
    expect(screen.getByText('Contact')).toBeInTheDocument();
    expect(screen.getByText('Terms of Service')).toBeInTheDocument();
    expect(screen.getByText('Privacy Policy')).toBeInTheDocument();
  });

  test('renders social media links', () => {
    render(<MockFooter />);

    expect(screen.getByTitle('Facebook')).toBeInTheDocument();
    expect(screen.getByTitle('Twitter')).toBeInTheDocument();
    expect(screen.getByTitle('Instagram')).toBeInTheDocument();
    expect(screen.getByTitle('YouTube')).toBeInTheDocument();
  });

  test('renders contact information', () => {
    render(<MockFooter />);

    expect(screen.getByText('123 Tour Street, Travel City, TC 12345')).toBeInTheDocument();
    expect(screen.getByText('info@tourflow.com')).toBeInTheDocument();
    expect(screen.getByText('+1 (555) 123-4567')).toBeInTheDocument();
  });

  test('renders copyright information', () => {
    render(<MockFooter />);

    const currentYear = new Date().getFullYear();
    expect(screen.getByText(`© ${currentYear} TourFlow. All rights reserved.`)).toBeInTheDocument();
  });

  test('navigates to correct page when navigation links are clicked', () => {
    render(<MockFooter />);

    const aboutLink = screen.getByText('About Us');
    fireEvent.click(aboutLink);

    expect(window.location.pathname).toBe('/about');
  });

  test('opens social media links in new tabs', () => {
    render(<MockFooter />);

    const facebookLink = screen.getByTitle('Facebook');
    expect(facebookLink).toHaveAttribute('target', '_blank');
    expect(facebookLink).toHaveAttribute('rel', 'noopener noreferrer');
  });

  test('applies custom className', () => {
    const { container } = render(<Footer className="custom-footer" />);
    const footer = container.querySelector('.footer');
    expect(footer).toHaveClass('custom-footer');
  });

  test('renders newsletter subscription form', () => {
    render(<MockFooter />);

    expect(screen.getByText('Subscribe to our newsletter')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Enter your email')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Subscribe' })).toBeInTheDocument();
  });

  test('calls onNewsletterSubmit when newsletter form is submitted', () => {
    const handleNewsletterSubmit = jest.fn();
    render(<Footer onNewsletterSubmit={handleNewsletterSubmit} />);

    const emailInput = screen.getByPlaceholderText('Enter your email');
    const submitButton = screen.getByRole('button', { name: 'Subscribe' });

    fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
    fireEvent.click(submitButton);

    expect(handleNewsletterSubmit).toHaveBeenCalledTimes(1);
    expect(handleNewsletterSubmit).toHaveBeenCalledWith('test@example.com');
  });

  test('validates newsletter email', async () => {
    render(<Footer />);

    const emailInput = screen.getByPlaceholderText('Enter your email');
    const submitButton = screen.getByRole('button', { name: 'Subscribe' });

    fireEvent.change(emailInput, { target: { value: 'invalid-email' } });
    fireEvent.click(submitButton);

    expect(screen.getByText('Please enter a valid email address')).toBeInTheDocument();
  });

  test('displays success message after successful newsletter subscription', async () => {
    const handleNewsletterSubmit = jest.fn(() => Promise.resolve());
    render(<Footer onNewsletterSubmit={handleNewsletterSubmit} />);

    const emailInput = screen.getByPlaceholderText('Enter your email');
    const submitButton = screen.getByRole('button', { name: 'Subscribe' });

    fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Thank you for subscribing to our newsletter!')).toBeInTheDocument();
    });
  });

  test('displays error message after failed newsletter subscription', async () => {
    const handleNewsletterSubmit = jest.fn(() => Promise.reject(new Error('Subscription failed')));
    render(<Footer onNewsletterSubmit={handleNewsletterSubmit} />);

    const emailInput = screen.getByPlaceholderText('Enter your email');
    const submitButton = screen.getByRole('button', { name: 'Subscribe' });

    fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Failed to subscribe. Please try again later.')).toBeInTheDocument();
    });
  });

  test('renders payment methods', () => {
    render(<MockFooter />);

    expect(screen.getByAltText('Visa')).toBeInTheDocument();
    expect(screen.getByAltText('Mastercard')).toBeInTheDocument();
    expect(screen.getByAltText('American Express')).toBeInTheDocument();
    expect(screen.getByAltText('PayPal')).toBeInTheDocument();
  });

  test('renders app download links', () => {
    render(<MockFooter />);

    expect(screen.getByAltText('Download on the App Store')).toBeInTheDocument();
    expect(screen.getByAltText('Get it on Google Play')).toBeInTheDocument();
  });

  test('renders language selector', () => {
    render(<MockFooter />);

    expect(screen.getByText('Language')).toBeInTheDocument();
    expect(screen.getByText('English')).toBeInTheDocument();
    expect(screen.getByText('French')).toBeInTheDocument();
    expect(screen.getByText('Spanish')).toBeInTheDocument();
  });

  test('calls onLanguageChange when language is changed', () => {
    const handleLanguageChange = jest.fn();
    render(<Footer onLanguageChange={handleLanguageChange} />);

    const languageSelect = screen.getByDisplayValue('English');
    fireEvent.change(languageSelect, { target: { value: 'fr' } });

    expect(handleLanguageChange).toHaveBeenCalledTimes(1);
    expect(handleLanguageChange).toHaveBeenCalledWith('fr');
  });

  test('renders currency selector', () => {
    render(<MockFooter />);

    expect(screen.getByText('Currency')).toBeInTheDocument();
    expect(screen.getByText('USD')).toBeInTheDocument();
    expect(screen.getByText('EUR')).toBeInTheDocument();
    expect(screen.getByText('GBP')).toBeInTheDocument();
  });

  test('calls onCurrencyChange when currency is changed', () => {
    const handleCurrencyChange = jest.fn();
    render(<Footer onCurrencyChange={handleCurrencyChange} />);

    const currencySelect = screen.getByDisplayValue('USD');
    fireEvent.change(currencySelect, { target: { value: 'EUR' } });

    expect(handleCurrencyChange).toHaveBeenCalledTimes(1);
    expect(handleCurrencyChange).toHaveBeenCalledWith('EUR');
  });
});
