import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import BookingForm from '../BookingForm';

const mockTour = {
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
};

describe('BookingForm Component', () => {
  test('renders form with correct fields', () => {
    render(<BookingForm tour={mockTour} />);

    expect(screen.getByText('Book Paris City Tour')).toBeInTheDocument();
    expect(screen.getByLabelText('Number of Participants')).toBeInTheDocument();
    expect(screen.getByLabelText('Booking Date')).toBeInTheDocument();
    expect(screen.getByLabelText('Special Requests')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Proceed to Payment' })).toBeInTheDocument();
  });

  test('displays tour information', () => {
    render(<BookingForm tour={mockTour} />);

    expect(screen.getByText('Paris City Tour')).toBeInTheDocument();
    expect(screen.getByText('€99.99 per person')).toBeInTheDocument();
    expect(screen.getByText('3 days')).toBeInTheDocument();
    expect(screen.getByText('Paris, France')).toBeInTheDocument();
  });

  test('updates total price when number of participants changes', () => {
    render(<BookingForm tour={mockTour} />);

    const participantsInput = screen.getByLabelText('Number of Participants');
    fireEvent.change(participantsInput, { target: { value: '2' } });

    expect(screen.getByText('Total: €199.98')).toBeInTheDocument();
  });

  test('validates required fields', async () => {
    render(<BookingForm tour={mockTour} />);

    const submitButton = screen.getByRole('button', { name: 'Proceed to Payment' });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Number of participants is required')).toBeInTheDocument();
      expect(screen.getByText('Booking date is required')).toBeInTheDocument();
    });
  });

  test('validates participant count', async () => {
    render(<BookingForm tour={mockTour} />);

    const participantsInput = screen.getByLabelText('Number of Participants');
    const submitButton = screen.getByRole('button', { name: 'Proceed to Payment' });

    // Test with 0 participants
    fireEvent.change(participantsInput, { target: { value: '0' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('At least 1 participant is required')).toBeInTheDocument();
    });

    // Test with more than max participants
    fireEvent.change(participantsInput, { target: { value: '25' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Maximum 20 participants allowed')).toBeInTheDocument();
    });
  });

  test('validates booking date', async () => {
    render(<BookingForm tour={mockTour} />);

    const dateInput = screen.getByLabelText('Booking Date');
    const submitButton = screen.getByRole('button', { name: 'Proceed to Payment' });

    // Test with past date
    const pastDate = new Date();
    pastDate.setDate(pastDate.getDate() - 1);
    fireEvent.change(dateInput, { target: { value: pastDate.toISOString().split('T')[0] } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Booking date must be in the future')).toBeInTheDocument();
    });
  });

  test('calls onSubmit with correct data when form is valid', async () => {
    const handleSubmit = jest.fn();
    render(<BookingForm tour={mockTour} onSubmit={handleSubmit} />);

    const participantsInput = screen.getByLabelText('Number of Participants');
    const dateInput = screen.getByLabelText('Booking Date');
    const specialRequestsInput = screen.getByLabelText('Special Requests');
    const submitButton = screen.getByRole('button', { name: 'Proceed to Payment' });

    const futureDate = new Date();
    futureDate.setDate(futureDate.getDate() + 7);

    fireEvent.change(participantsInput, { target: { value: '2' } });
    fireEvent.change(dateInput, { target: { value: futureDate.toISOString().split('T')[0] } });
    fireEvent.change(specialRequestsInput, { target: { value: 'Vegetarian meals required' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(handleSubmit).toHaveBeenCalledTimes(1);
      expect(handleSubmit).toHaveBeenCalledWith({
        tourId: 1,
        participants: 2,
        bookingDate: futureDate.toISOString().split('T')[0],
        specialRequests: 'Vegetarian meals required',
        totalPrice: 199.98
      });
    });
  });

  test('displays loading state when submitting', async () => {
    const handleSubmit = jest.fn(() => new Promise(resolve => setTimeout(resolve, 100)));
    render(<BookingForm tour={mockTour} onSubmit={handleSubmit} />);

    const participantsInput = screen.getByLabelText('Number of Participants');
    const dateInput = screen.getByLabelText('Booking Date');
    const submitButton = screen.getByRole('button', { name: 'Proceed to Payment' });

    const futureDate = new Date();
    futureDate.setDate(futureDate.getDate() + 7);

    fireEvent.change(participantsInput, { target: { value: '2' } });
    fireEvent.change(dateInput, { target: { value: futureDate.toISOString().split('T')[0] } });
    fireEvent.click(submitButton);

    // Check for loading state
    expect(screen.getByText('Processing...')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Processing...' })).toBeDisabled();
  });

  test('displays error message when submission fails', async () => {
    const handleSubmit = jest.fn(() => Promise.reject(new Error('Booking failed')));
    render(<BookingForm tour={mockTour} onSubmit={handleSubmit} />);

    const participantsInput = screen.getByLabelText('Number of Participants');
    const dateInput = screen.getByLabelText('Booking Date');
    const submitButton = screen.getByRole('button', { name: 'Proceed to Payment' });

    const futureDate = new Date();
    futureDate.setDate(futureDate.getDate() + 7);

    fireEvent.change(participantsInput, { target: { value: '2' } });
    fireEvent.change(dateInput, { target: { value: futureDate.toISOString().split('T')[0] } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Booking failed')).toBeInTheDocument();
    });
  });

  test('applies custom className', () => {
    const { container } = render(<BookingForm tour={mockTour} className="custom-booking-form" />);
    const form = container.querySelector('.booking-form');
    expect(form).toHaveClass('custom-booking-form');
  });

  test('displays terms and conditions checkbox', () => {
    render(<BookingForm tour={mockTour} />);

    expect(screen.getByLabelText('I agree to the terms and conditions')).toBeInTheDocument();
  });

  test('validates terms and conditions checkbox', async () => {
    render(<BookingForm tour={mockTour} />);

    const participantsInput = screen.getByLabelText('Number of Participants');
    const dateInput = screen.getByLabelText('Booking Date');
    const submitButton = screen.getByRole('button', { name: 'Proceed to Payment' });

    const futureDate = new Date();
    futureDate.setDate(futureDate.getDate() + 7);

    fireEvent.change(participantsInput, { target: { value: '2' } });
    fireEvent.change(dateInput, { target: { value: futureDate.toISOString().split('T')[0] } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('You must agree to the terms and conditions')).toBeInTheDocument();
    });
  });
});
