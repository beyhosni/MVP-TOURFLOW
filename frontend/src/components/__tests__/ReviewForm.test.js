import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import ReviewForm from '../ReviewForm';

const mockTour = {
  id: 1,
  title: 'Paris City Tour',
  imageUrl: 'https://example.com/paris-tour.jpg'
};

const mockBooking = {
  id: 1,
  tour: mockTour,
  status: 'COMPLETED',
  bookingDate: '2023-10-15',
  totalPrice: 199.98,
  participants: 2
};

describe('ReviewForm Component', () => {
  test('renders form with rating and comment fields', () => {
    render(<ReviewForm booking={mockBooking} />);

    expect(screen.getByText('Write a Review')).toBeInTheDocument();
    expect(screen.getByText('Paris City Tour')).toBeInTheDocument();
    expect(screen.getByText('Oct 15, 2023')).toBeInTheDocument();
    expect(screen.getByLabelText('Rating')).toBeInTheDocument();
    expect(screen.getByLabelText('Comment')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Submit Review' })).toBeInTheDocument();
  });

  test('calls onSubmit with correct data when form is valid', async () => {
    const handleSubmit = jest.fn();
    render(<ReviewForm booking={mockBooking} onSubmit={handleSubmit} />);

    const ratingInput = screen.getByLabelText('Rating');
    const commentInput = screen.getByLabelText('Comment');
    const submitButton = screen.getByRole('button', { name: 'Submit Review' });

    // Set rating to 4 stars
    const star4 = screen.getByLabelText('4 stars');
    fireEvent.click(star4);

    // Add comment
    fireEvent.change(commentInput, { target: { value: 'Great tour, highly recommended!' } });

    // Submit form
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(handleSubmit).toHaveBeenCalledTimes(1);
      expect(handleSubmit).toHaveBeenCalledWith({
        bookingId: 1,
        rating: 4,
        comment: 'Great tour, highly recommended!'
      });
    });
  });

  test('validates rating is required', async () => {
    render(<ReviewForm booking={mockBooking} />);

    const submitButton = screen.getByRole('button', { name: 'Submit Review' });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Please select a rating')).toBeInTheDocument();
    });
  });

  test('validates comment is required', async () => {
    render(<ReviewForm booking={mockBooking} />);

    // Set rating to 4 stars
    const star4 = screen.getByLabelText('4 stars');
    fireEvent.click(star4);

    const submitButton = screen.getByRole('button', { name: 'Submit Review' });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Please enter a comment')).toBeInTheDocument();
    });
  });

  test('validates comment length', async () => {
    render(<ReviewForm booking={mockBooking} />);

    // Set rating to 4 stars
    const star4 = screen.getByLabelText('4 stars');
    fireEvent.click(star4);

    const commentInput = screen.getByLabelText('Comment');
    const submitButton = screen.getByRole('button', { name: 'Submit Review' });

    // Add a comment that's too long (more than 500 characters)
    const longComment = 'a'.repeat(501);
    fireEvent.change(commentInput, { target: { value: longComment } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Comment must be less than 500 characters')).toBeInTheDocument();
    });
  });

  test('displays loading state when submitting', async () => {
    const handleSubmit = jest.fn(() => new Promise(resolve => setTimeout(resolve, 100)));
    render(<ReviewForm booking={mockBooking} onSubmit={handleSubmit} />);

    // Set rating to 4 stars
    const star4 = screen.getByLabelText('4 stars');
    fireEvent.click(star4);

    // Add comment
    const commentInput = screen.getByLabelText('Comment');
    fireEvent.change(commentInput, { target: { value: 'Great tour, highly recommended!' } });

    // Submit form
    const submitButton = screen.getByRole('button', { name: 'Submit Review' });
    fireEvent.click(submitButton);

    // Check for loading state
    expect(screen.getByText('Submitting...')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Submitting...' })).toBeDisabled();
  });

  test('displays error message when submission fails', async () => {
    const handleSubmit = jest.fn(() => Promise.reject(new Error('Failed to submit review')));
    render(<ReviewForm booking={mockBooking} onSubmit={handleSubmit} />);

    // Set rating to 4 stars
    const star4 = screen.getByLabelText('4 stars');
    fireEvent.click(star4);

    // Add comment
    const commentInput = screen.getByLabelText('Comment');
    fireEvent.change(commentInput, { target: { value: 'Great tour, highly recommended!' } });

    // Submit form
    const submitButton = screen.getByRole('button', { name: 'Submit Review' });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Failed to submit review')).toBeInTheDocument();
    });
  });

  test('applies custom className', () => {
    const { container } = render(<ReviewForm booking={mockBooking} className="custom-review-form" />);
    const form = container.querySelector('.review-form');
    expect(form).toHaveClass('custom-review-form');
  });

  test('displays tour image', () => {
    render(<ReviewForm booking={mockBooking} />);

    const image = screen.getByAltText('Paris City Tour');
    expect(image).toBeInTheDocument();
    expect(image).toHaveAttribute('src', 'https://example.com/paris-tour.jpg');
  });

  test('displays character count for comment', () => {
    render(<ReviewForm booking={mockBooking} />);

    const commentInput = screen.getByLabelText('Comment');
    fireEvent.change(commentInput, { target: { value: 'This is a test comment.' } });

    expect(screen.getByText('24 / 500')).toBeInTheDocument();
  });

  test('highlights selected stars when rating is clicked', () => {
    render(<ReviewForm booking={mockBooking} />);

    // Click 3 stars
    const star3 = screen.getByLabelText('3 stars');
    fireEvent.click(star3);

    // Check that stars 1, 2, and 3 are highlighted
    expect(screen.getByLabelText('1 star')).toHaveClass('star-highlighted');
    expect(screen.getByLabelText('2 stars')).toHaveClass('star-highlighted');
    expect(screen.getByLabelText('3 stars')).toHaveClass('star-highlighted');
    expect(screen.getByLabelText('4 stars')).not.toHaveClass('star-highlighted');
    expect(screen.getByLabelText('5 stars')).not.toHaveClass('star-highlighted');
  });

  test('updates rating when different stars are clicked', () => {
    render(<ReviewForm booking={mockBooking} />);

    // Click 3 stars
    const star3 = screen.getByLabelText('3 stars');
    fireEvent.click(star3);

    // Check that stars 1, 2, and 3 are highlighted
    expect(screen.getByLabelText('1 star')).toHaveClass('star-highlighted');
    expect(screen.getByLabelText('2 stars')).toHaveClass('star-highlighted');
    expect(screen.getByLabelText('3 stars')).toHaveClass('star-highlighted');
    expect(screen.getByLabelText('4 stars')).not.toHaveClass('star-highlighted');
    expect(screen.getByLabelText('5 stars')).not.toHaveClass('star-highlighted');

    // Click 5 stars
    const star5 = screen.getByLabelText('5 stars');
    fireEvent.click(star5);

    // Check that all stars are now highlighted
    expect(screen.getByLabelText('1 star')).toHaveClass('star-highlighted');
    expect(screen.getByLabelText('2 stars')).toHaveClass('star-highlighted');
    expect(screen.getByLabelText('3 stars')).toHaveClass('star-highlighted');
    expect(screen.getByLabelText('4 stars')).toHaveClass('star-highlighted');
    expect(screen.getByLabelText('5 stars')).toHaveClass('star-highlighted');
  });

  test('displays rating labels when hovering over stars', () => {
    render(<ReviewForm booking={mockBooking} />);

    // Hover over 3 stars
    const star3 = screen.getByLabelText('3 stars');
    fireEvent.mouseEnter(star3);

    expect(screen.getByText('Good')).toBeInTheDocument();

    // Hover over 5 stars
    const star5 = screen.getByLabelText('5 stars');
    fireEvent.mouseEnter(star5);

    expect(screen.getByText('Excellent')).toBeInTheDocument();
  });

  test('applies initial values', () => {
    const initialValues = {
      rating: 4,
      comment: 'Great tour, highly recommended!'
    };

    render(<ReviewForm booking={mockBooking} initialValues={initialValues} />);

    // Check that 4 stars are highlighted
    expect(screen.getByLabelText('1 star')).toHaveClass('star-highlighted');
    expect(screen.getByLabelText('2 stars')).toHaveClass('star-highlighted');
    expect(screen.getByLabelText('3 stars')).toHaveClass('star-highlighted');
    expect(screen.getByLabelText('4 stars')).toHaveClass('star-highlighted');
    expect(screen.getByLabelText('5 stars')).not.toHaveClass('star-highlighted');

    // Check that comment has initial value
    expect(screen.getByDisplayValue('Great tour, highly recommended!')).toBeInTheDocument();
  });

  test('calls onCancel when cancel button is clicked', () => {
    const handleCancel = jest.fn();
    render(<ReviewForm booking={mockBooking} onCancel={handleCancel} />);

    const cancelButton = screen.getByRole('button', { name: 'Cancel' });
    fireEvent.click(cancelButton);

    expect(handleCancel).toHaveBeenCalledTimes(1);
  });

  test('displays helper text for rating', () => {
    render(<ReviewForm booking={mockBooking} />);

    expect(screen.getByText('Please rate your experience')).toBeInTheDocument();
  });

  test('displays helper text for comment', () => {
    render(<ReviewForm booking={mockBooking} />);

    expect(screen.getByText('Share your experience with this tour')).toBeInTheDocument();
  });
});
