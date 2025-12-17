import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import BookingList from '../BookingList';

const mockBookings = [
  {
    id: 1,
    tour: {
      id: 1,
      title: 'Paris City Tour',
      imageUrl: 'https://example.com/paris-tour.jpg',
      location: 'Paris, France'
    },
    status: 'CONFIRMED',
    bookingDate: '2023-12-25',
    totalPrice: 199.98,
    participants: 2,
    createdAt: '2023-11-15T10:00:00'
  },
  {
    id: 2,
    tour: {
      id: 2,
      title: 'Rome Adventure',
      imageUrl: 'https://example.com/rome-tour.jpg',
      location: 'Rome, Italy'
    },
    status: 'COMPLETED',
    bookingDate: '2023-10-15',
    totalPrice: 299.99,
    participants: 1,
    createdAt: '2023-09-10T14:30:00'
  },
  {
    id: 3,
    tour: {
      id: 3,
      title: 'London Experience',
      imageUrl: 'https://example.com/london-tour.jpg',
      location: 'London, UK'
    },
    status: 'CANCELLED',
    bookingDate: '2023-08-20',
    totalPrice: 149.99,
    participants: 3,
    createdAt: '2023-07-05T09:15:00'
  }
];

describe('BookingList Component', () => {
  test('renders list of bookings', () => {
    render(<BookingList bookings={mockBookings} />);

    expect(screen.getByText('Paris City Tour')).toBeInTheDocument();
    expect(screen.getByText('Rome Adventure')).toBeInTheDocument();
    expect(screen.getByText('London Experience')).toBeInTheDocument();
    expect(screen.getByText('Paris, France')).toBeInTheDocument();
    expect(screen.getByText('Rome, Italy')).toBeInTheDocument();
    expect(screen.getByText('London, UK')).toBeInTheDocument();
  });

  test('displays booking status correctly', () => {
    render(<BookingList bookings={mockBookings} />);

    expect(screen.getByText('Confirmed')).toBeInTheDocument();
    expect(screen.getByText('Completed')).toBeInTheDocument();
    expect(screen.getByText('Cancelled')).toBeInTheDocument();
  });

  test('displays booking dates correctly', () => {
    render(<BookingList bookings={mockBookings} />);

    expect(screen.getByText('Dec 25, 2023')).toBeInTheDocument();
    expect(screen.getByText('Oct 15, 2023')).toBeInTheDocument();
    expect(screen.getByText('Aug 20, 2023')).toBeInTheDocument();
  });

  test('displays booking prices correctly', () => {
    render(<BookingList bookings={mockBookings} />);

    expect(screen.getByText('$199.98')).toBeInTheDocument();
    expect(screen.getByText('$299.99')).toBeInTheDocument();
    expect(screen.getByText('$149.99')).toBeInTheDocument();
  });

  test('displays number of participants correctly', () => {
    render(<BookingList bookings={mockBookings} />);

    expect(screen.getByText('2 participants')).toBeInTheDocument();
    expect(screen.getByText('1 participant')).toBeInTheDocument();
    expect(screen.getByText('3 participants')).toBeInTheDocument();
  });

  test('calls onBookingClick when a booking is clicked', () => {
    const handleBookingClick = jest.fn();
    render(<BookingList bookings={mockBookings} onBookingClick={handleBookingClick} />);

    const bookingCard = screen.getByText('Paris City Tour').closest('.booking-card');
    fireEvent.click(bookingCard);

    expect(handleBookingClick).toHaveBeenCalledTimes(1);
    expect(handleBookingClick).toHaveBeenCalledWith(mockBookings[0]);
  });

  test('calls onViewDetailsClick when view details button is clicked', () => {
    const handleViewDetailsClick = jest.fn();
    render(<BookingList bookings={mockBookings} onViewDetailsClick={handleViewDetailsClick} />);

    const viewDetailsButton = screen.getAllByText('View Details')[0];
    fireEvent.click(viewDetailsButton);

    expect(handleViewDetailsClick).toHaveBeenCalledTimes(1);
    expect(handleViewDetailsClick).toHaveBeenCalledWith(mockBookings[0]);
  });

  test('calls onCancelClick when cancel button is clicked for a confirmed booking', () => {
    const handleCancelClick = jest.fn();
    render(<BookingList bookings={mockBookings} onCancelClick={handleCancelClick} />);

    const cancelButton = screen.getAllByText('Cancel')[0];
    fireEvent.click(cancelButton);

    expect(handleCancelClick).toHaveBeenCalledTimes(1);
    expect(handleCancelClick).toHaveBeenCalledWith(mockBookings[0]);
  });

  test('calls onReviewClick when review button is clicked for a completed booking', () => {
    const handleReviewClick = jest.fn();
    render(<BookingList bookings={mockBookings} onReviewClick={handleReviewClick} />);

    const reviewButton = screen.getAllByText('Write Review')[1];
    fireEvent.click(reviewButton);

    expect(handleReviewClick).toHaveBeenCalledTimes(1);
    expect(handleReviewClick).toHaveBeenCalledWith(mockBookings[1]);
  });

  test('displays loading state when isLoading is true', () => {
    render(<BookingList isLoading={true} />);

    expect(screen.getByText('Loading bookings...')).toBeInTheDocument();
  });

  test('displays message when no bookings are available', () => {
    render(<BookingList bookings={[]} />);

    expect(screen.getByText('You haven't made any bookings yet.')).toBeInTheDocument();
    expect(screen.getByText('Browse Tours')).toBeInTheDocument();
  });

  test('calls onBrowseToursClick when browse tours button is clicked', () => {
    const handleBrowseToursClick = jest.fn();
    render(<BookingList bookings={[]} onBrowseToursClick={handleBrowseToursClick} />);

    const browseButton = screen.getByText('Browse Tours');
    fireEvent.click(browseButton);

    expect(handleBrowseToursClick).toHaveBeenCalledTimes(1);
  });

  test('applies custom className', () => {
    const { container } = render(<BookingList bookings={mockBookings} className="custom-booking-list" />);
    const bookingList = container.querySelector('.booking-list');
    expect(bookingList).toHaveClass('custom-booking-list');
  });

  test('filters bookings by status', () => {
    render(<BookingList bookings={mockBookings} statusFilter="CONFIRMED" />);

    expect(screen.getByText('Paris City Tour')).toBeInTheDocument();
    expect(screen.queryByText('Rome Adventure')).not.toBeInTheDocument();
    expect(screen.queryByText('London Experience')).not.toBeInTheDocument();
  });

  test('sorts bookings by date', () => {
    render(<BookingList bookings={mockBookings} sortBy="date" sortOrder="desc" />);

    const bookingCards = screen.getAllByTestId('booking-card');
    expect(bookingCards[0]).toHaveTextContent('Paris City Tour');
    expect(bookingCards[1]).toHaveTextContent('Rome Adventure');
    expect(bookingCards[2]).toHaveTextContent('London Experience');
  });

  test('displays pagination when there are many bookings', () => {
    const manyBookings = Array.from({ length: 25 }, (_, i) => ({
      ...mockBookings[0],
      id: i + 1,
      tour: {
        ...mockBookings[0].tour,
        title: `Booking ${i + 1}`
      }
    }));

    render(<BookingList bookings={manyBookings} itemsPerPage={10} />);

    expect(screen.getByText('Page 1 of 3')).toBeInTheDocument();
    expect(screen.getByText('Next')).toBeInTheDocument();
    expect(screen.queryByText('Previous')).not.toBeInTheDocument();
  });

  test('navigates to next page when next button is clicked', () => {
    const manyBookings = Array.from({ length: 25 }, (_, i) => ({
      ...mockBookings[0],
      id: i + 1,
      tour: {
        ...mockBookings[0].tour,
        title: `Booking ${i + 1}`
      }
    }));

    render(<BookingList bookings={manyBookings} itemsPerPage={10} />);

    const nextButton = screen.getByText('Next');
    fireEvent.click(nextButton);

    expect(screen.getByText('Page 2 of 3')).toBeInTheDocument();
    expect(screen.getByText('Previous')).toBeInTheDocument();
    expect(screen.getByText('Next')).toBeInTheDocument();
  });

  test('displays search bar when showSearch is true', () => {
    render(<BookingList bookings={mockBookings} showSearch={true} />);

    expect(screen.getByPlaceholderText('Search bookings...')).toBeInTheDocument();
  });

  test('filters bookings by search term', () => {
    render(<BookingList bookings={mockBookings} showSearch={true} />);

    const searchInput = screen.getByPlaceholderText('Search bookings...');
    fireEvent.change(searchInput, { target: { value: 'Paris' } });

    expect(screen.getByText('Paris City Tour')).toBeInTheDocument();
    expect(screen.queryByText('Rome Adventure')).not.toBeInTheDocument();
    expect(screen.queryByText('London Experience')).not.toBeInTheDocument();
  });

  test('displays filter panel when showFilters is true', () => {
    render(<BookingList bookings={mockBookings} showFilters={true} />);

    expect(screen.getByText('Filters')).toBeInTheDocument();
  });

  test('filters bookings by selected filters', () => {
    render(<BookingList bookings={mockBookings} showFilters={true} />);

    const filtersButton = screen.getByText('Filters');
    fireEvent.click(filtersButton);

    const statusFilter = screen.getByLabelText('Status');
    fireEvent.change(statusFilter, { target: { value: 'CONFIRMED' } });

    expect(screen.getByText('Paris City Tour')).toBeInTheDocument();
    expect(screen.queryByText('Rome Adventure')).not.toBeInTheDocument();
    expect(screen.queryByText('London Experience')).not.toBeInTheDocument();
  });
});
