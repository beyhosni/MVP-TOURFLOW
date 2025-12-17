import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import PaymentForm from '../PaymentForm';

const mockBooking = {
  id: 1,
  tour: {
    id: 1,
    title: 'Paris City Tour',
    description: 'Explore the beautiful city of Paris',
    price: 99.99,
    duration: 3,
    maxParticipants: 20,
    status: 'ACTIVE',
    startDate: '2023-12-25T10:00:00',
    endDate: '2023-12-28T18:00:00',
    imageUrl: 'https://example.com/paris-tour.jpg',
    rating: 4.5,
    reviewCount: 128,
    location: 'Paris, France'
  },
  user: {
    id: 1,
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@example.com'
  },
  participants: 2,
  totalPrice: 199.98,
  status: 'PENDING',
  bookingDate: '2023-12-20T10:00:00',
  createdAt: '2023-12-15T10:00:00',
  updatedAt: '2023-12-15T10:00:00'
};

describe('PaymentForm Component', () => {
  test('renders form with correct fields', () => {
    render(<PaymentForm booking={mockBooking} />);

    expect(screen.getByText('Payment for Paris City Tour')).toBeInTheDocument();
    expect(screen.getByText('Total Amount: €199.98')).toBeInTheDocument();
    expect(screen.getByLabelText('Card Number')).toBeInTheDocument();
    expect(screen.getByLabelText('Cardholder Name')).toBeInTheDocument();
    expect(screen.getByLabelText('Expiry Date')).toBeInTheDocument();
    expect(screen.getByLabelText('CVV')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Complete Payment' })).toBeInTheDocument();
  });

  test('displays booking information', () => {
    render(<PaymentForm booking={mockBooking} />);

    expect(screen.getByText('Paris City Tour')).toBeInTheDocument();
    expect(screen.getByText('€199.98')).toBeInTheDocument();
    expect(screen.getByText('2 participants')).toBeInTheDocument();
    expect(screen.getByText('Dec 20, 2023')).toBeInTheDocument();
  });

  test('validates required fields', async () => {
    render(<PaymentForm booking={mockBooking} />);

    const submitButton = screen.getByRole('button', { name: 'Complete Payment' });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Card number is required')).toBeInTheDocument();
      expect(screen.getByText('Cardholder name is required')).toBeInTheDocument();
      expect(screen.getByText('Expiry date is required')).toBeInTheDocument();
      expect(screen.getByText('CVV is required')).toBeInTheDocument();
    });
  });

  test('validates card number format', async () => {
    render(<PaymentForm booking={mockBooking} />);

    const cardNumberInput = screen.getByLabelText('Card Number');
    const submitButton = screen.getByRole('button', { name: 'Complete Payment' });

    fireEvent.change(cardNumberInput, { target: { value: '1234' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Please enter a valid card number')).toBeInTheDocument();
    });
  });

  test('validates expiry date format', async () => {
    render(<PaymentForm booking={mockBooking} />);

    const expiryDateInput = screen.getByLabelText('Expiry Date');
    const submitButton = screen.getByRole('button', { name: 'Complete Payment' });

    fireEvent.change(expiryDateInput, { target: { value: '13/25' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Please enter a valid expiry date')).toBeInTheDocument();
    });
  });

  test('validates CVV format', async () => {
    render(<PaymentForm booking={mockBooking} />);

    const cvvInput = screen.getByLabelText('CVV');
    const submitButton = screen.getByRole('button', { name: 'Complete Payment' });

    fireEvent.change(cvvInput, { target: { value: '12' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('CVV must be 3 or 4 digits')).toBeInTheDocument();
    });
  });

  test('validates past expiry date', async () => {
    render(<PaymentForm booking={mockBooking} />);

    const expiryDateInput = screen.getByLabelText('Expiry Date');
    const submitButton = screen.getByRole('button', { name: 'Complete Payment' });

    // Use a past date
    const pastDate = new Date();
    pastDate.setFullYear(pastDate.getFullYear() - 1);
    const month = String(pastDate.getMonth() + 1).padStart(2, '0');
    const year = String(pastDate.getFullYear()).substring(2);

    fireEvent.change(expiryDateInput, { target: { value: `${month}/${year}` } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Card has expired')).toBeInTheDocument();
    });
  });

  test('calls onSubmit with correct data when form is valid', async () => {
    const handleSubmit = jest.fn();
    render(<PaymentForm booking={mockBooking} onSubmit={handleSubmit} />);

    const cardNumberInput = screen.getByLabelText('Card Number');
    const cardholderNameInput = screen.getByLabelText('Cardholder Name');
    const expiryDateInput = screen.getByLabelText('Expiry Date');
    const cvvInput = screen.getByLabelText('CVV');
    const submitButton = screen.getByRole('button', { name: 'Complete Payment' });

    // Use a future date
    const futureDate = new Date();
    futureDate.setFullYear(futureDate.getFullYear() + 1);
    const month = String(futureDate.getMonth() + 1).padStart(2, '0');
    const year = String(futureDate.getFullYear()).substring(2);

    fireEvent.change(cardNumberInput, { target: { value: '4242424242424242' } });
    fireEvent.change(cardholderNameInput, { target: { value: 'John Doe' } });
    fireEvent.change(expiryDateInput, { target: { value: `${month}/${year}` } });
    fireEvent.change(cvvInput, { target: { value: '123' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(handleSubmit).toHaveBeenCalledTimes(1);
      expect(handleSubmit).toHaveBeenCalledWith({
        bookingId: 1,
        cardNumber: '4242424242424242',
        cardholderName: 'John Doe',
        expiryDate: `${month}/${year}`,
        cvv: '123',
        saveCard: false
      });
    });
  });

  test('displays loading state when submitting', async () => {
    const handleSubmit = jest.fn(() => new Promise(resolve => setTimeout(resolve, 100)));
    render(<PaymentForm booking={mockBooking} onSubmit={handleSubmit} />);

    const cardNumberInput = screen.getByLabelText('Card Number');
    const cardholderNameInput = screen.getByLabelText('Cardholder Name');
    const expiryDateInput = screen.getByLabelText('Expiry Date');
    const cvvInput = screen.getByLabelText('CVV');
    const submitButton = screen.getByRole('button', { name: 'Complete Payment' });

    // Use a future date
    const futureDate = new Date();
    futureDate.setFullYear(futureDate.getFullYear() + 1);
    const month = String(futureDate.getMonth() + 1).padStart(2, '0');
    const year = String(futureDate.getFullYear()).substring(2);

    fireEvent.change(cardNumberInput, { target: { value: '4242424242424242' } });
    fireEvent.change(cardholderNameInput, { target: { value: 'John Doe' } });
    fireEvent.change(expiryDateInput, { target: { value: `${month}/${year}` } });
    fireEvent.change(cvvInput, { target: { value: '123' } });
    fireEvent.click(submitButton);

    // Check for loading state
    expect(screen.getByText('Processing...')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Processing...' })).toBeDisabled();
  });

  test('displays error message when submission fails', async () => {
    const handleSubmit = jest.fn(() => Promise.reject(new Error('Payment failed')));
    render(<PaymentForm booking={mockBooking} onSubmit={handleSubmit} />);

    const cardNumberInput = screen.getByLabelText('Card Number');
    const cardholderNameInput = screen.getByLabelText('Cardholder Name');
    const expiryDateInput = screen.getByLabelText('Expiry Date');
    const cvvInput = screen.getByLabelText('CVV');
    const submitButton = screen.getByRole('button', { name: 'Complete Payment' });

    // Use a future date
    const futureDate = new Date();
    futureDate.setFullYear(futureDate.getFullYear() + 1);
    const month = String(futureDate.getMonth() + 1).padStart(2, '0');
    const year = String(futureDate.getFullYear()).substring(2);

    fireEvent.change(cardNumberInput, { target: { value: '4242424242424242' } });
    fireEvent.change(cardholderNameInput, { target: { value: 'John Doe' } });
    fireEvent.change(expiryDateInput, { target: { value: `${month}/${year}` } });
    fireEvent.change(cvvInput, { target: { value: '123' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Payment failed')).toBeInTheDocument();
    });
  });

  test('applies custom className', () => {
    const { container } = render(<PaymentForm booking={mockBooking} className="custom-payment-form" />);
    const form = container.querySelector('.payment-form');
    expect(form).toHaveClass('custom-payment-form');
  });

  test('displays save card checkbox', () => {
    render(<PaymentForm booking={mockBooking} />);

    expect(screen.getByLabelText('Save card for future payments')).toBeInTheDocument();
  });

  test('includes save card option in submission data when checked', async () => {
    const handleSubmit = jest.fn();
    render(<PaymentForm booking={mockBooking} onSubmit={handleSubmit} />);

    const cardNumberInput = screen.getByLabelText('Card Number');
    const cardholderNameInput = screen.getByLabelText('Cardholder Name');
    const expiryDateInput = screen.getByLabelText('Expiry Date');
    const cvvInput = screen.getByLabelText('CVV');
    const saveCardCheckbox = screen.getByLabelText('Save card for future payments');
    const submitButton = screen.getByRole('button', { name: 'Complete Payment' });

    // Use a future date
    const futureDate = new Date();
    futureDate.setFullYear(futureDate.getFullYear() + 1);
    const month = String(futureDate.getMonth() + 1).padStart(2, '0');
    const year = String(futureDate.getFullYear()).substring(2);

    fireEvent.change(cardNumberInput, { target: { value: '4242424242424242' } });
    fireEvent.change(cardholderNameInput, { target: { value: 'John Doe' } });
    fireEvent.change(expiryDateInput, { target: { value: `${month}/${year}` } });
    fireEvent.change(cvvInput, { target: { value: '123' } });
    fireEvent.click(saveCardCheckbox);
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(handleSubmit).toHaveBeenCalledTimes(1);
      expect(handleSubmit).toHaveBeenCalledWith({
        bookingId: 1,
        cardNumber: '4242424242424242',
        cardholderName: 'John Doe',
        expiryDate: `${month}/${year}`,
        cvv: '123',
        saveCard: true
      });
    });
  });
});
