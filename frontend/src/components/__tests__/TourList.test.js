import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import TourList from '../TourList';

const mockTours = [
  {
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
  {
    id: 2,
    title: 'Rome Adventure',
    description: 'Discover the ancient city of Rome',
    price: 149.99,
    duration: 5,
    maxParticipants: 15,
    status: 'ACTIVE',
    startDate: '2023-12-30T10:00:00',
    endDate: '2024-01-03T18:00:00',
    imageUrl: 'https://example.com/rome-tour.jpg',
    rating: 4.8,
    reviewCount: 95,
    location: 'Rome, Italy'
  }
];

describe('TourList Component', () => {
  test('renders list of tours', () => {
    render(<TourList tours={mockTours} />);

    expect(screen.getByText('Paris City Tour')).toBeInTheDocument();
    expect(screen.getByText('Rome Adventure')).toBeInTheDocument();
    expect(screen.getByText('Explore the beautiful city of Paris')).toBeInTheDocument();
    expect(screen.getByText('Discover the ancient city of Rome')).toBeInTheDocument();
  });

  test('displays loading state when isLoading is true', () => {
    render(<TourList tours={[]} isLoading={true} />);

    expect(screen.getByText('Loading tours...')).toBeInTheDocument();
  });

  test('displays message when no tours are available', () => {
    render(<TourList tours={[]} isLoading={false} />);

    expect(screen.getByText('No tours available at the moment. Please check back later.')).toBeInTheDocument();
  });

  test('renders tour cards with correct props', () => {
    render(<TourList tours={mockTours} />);

    const parisTourCard = screen.getByText('Paris City Tour').closest('.tour-card');
    expect(parisTourCard).toBeInTheDocument();

    const romeTourCard = screen.getByText('Rome Adventure').closest('.tour-card');
    expect(romeTourCard).toBeInTheDocument();
  });

  test('calls onTourClick when a tour card is clicked', () => {
    const handleTourClick = jest.fn();
    render(<TourList tours={mockTours} onTourClick={handleTourClick} />);

    const parisTourCard = screen.getByText('Paris City Tour').closest('.tour-card');
    fireEvent.click(parisTourCard);

    expect(handleTourClick).toHaveBeenCalledTimes(1);
    expect(handleTourClick).toHaveBeenCalledWith(mockTours[0]);
  });

  test('calls onBookClick when book button is clicked', () => {
    const handleBookClick = jest.fn();
    render(<TourList tours={mockTours} onBookClick={handleBookClick} />);

    const bookButton = screen.getAllByText('Book Now')[0];
    fireEvent.click(bookButton);

    expect(handleBookClick).toHaveBeenCalledTimes(1);
    expect(handleBookClick).toHaveBeenCalledWith(mockTours[0]);
  });

  test('calls onViewDetailsClick when view details button is clicked', () => {
    const handleViewDetailsClick = jest.fn();
    render(<TourList tours={mockTours} onViewDetailsClick={handleViewDetailsClick} />);

    const viewDetailsButton = screen.getAllByText('View Details')[0];
    fireEvent.click(viewDetailsButton);

    expect(handleViewDetailsClick).toHaveBeenCalledTimes(1);
    expect(handleViewDetailsClick).toHaveBeenCalledWith(mockTours[0]);
  });

  test('filters tours by search term', () => {
    render(<TourList tours={mockTours} searchTerm="Paris" />);

    expect(screen.getByText('Paris City Tour')).toBeInTheDocument();
    expect(screen.queryByText('Rome Adventure')).not.toBeInTheDocument();
  });

  test('filters tours by location', () => {
    render(<TourList tours={mockTours} selectedLocation="Paris" />);

    expect(screen.getByText('Paris City Tour')).toBeInTheDocument();
    expect(screen.queryByText('Rome Adventure')).not.toBeInTheDocument();
  });

  test('filters tours by price range', () => {
    render(<TourList tours={mockTours} minPrice={100} maxPrice={200} />);

    expect(screen.queryByText('Paris City Tour')).not.toBeInTheDocument();
    expect(screen.getByText('Rome Adventure')).toBeInTheDocument();
  });

  test('filters tours by duration', () => {
    render(<TourList tours={mockTours} minDuration={4} maxDuration={6} />);

    expect(screen.queryByText('Paris City Tour')).not.toBeInTheDocument();
    expect(screen.getByText('Rome Adventure')).toBeInTheDocument();
  });

  test('sorts tours by price (low to high)', () => {
    render(<TourList tours={mockTours} sortBy="price" sortOrder="asc" />);

    const tourCards = screen.getAllByTestId('tour-card');
    expect(tourCards[0]).toHaveTextContent('Paris City Tour');
    expect(tourCards[1]).toHaveTextContent('Rome Adventure');
  });

  test('sorts tours by price (high to low)', () => {
    render(<TourList tours={mockTours} sortBy="price" sortOrder="desc" />);

    const tourCards = screen.getAllByTestId('tour-card');
    expect(tourCards[0]).toHaveTextContent('Rome Adventure');
    expect(tourCards[1]).toHaveTextContent('Paris City Tour');
  });

  test('sorts tours by rating (high to low)', () => {
    render(<TourList tours={mockTours} sortBy="rating" sortOrder="desc" />);

    const tourCards = screen.allByTestId('tour-card');
    expect(tourCards[0]).toHaveTextContent('Rome Adventure');
    expect(tourCards[1]).toHaveTextContent('Paris City Tour');
  });

  test('applies grid layout when viewMode is grid', () => {
    const { container } = render(<TourList tours={mockTours} viewMode="grid" />);
    const tourList = container.querySelector('.tour-list');
    expect(tourList).toHaveClass('tour-list-grid');
  });

  test('applies list layout when viewMode is list', () => {
    const { container } = render(<TourList tours={mockTours} viewMode="list" />);
    const tourList = container.querySelector('.tour-list');
    expect(tourList).toHaveClass('tour-list-list');
  });

  test('displays pagination when there are many tours', () => {
    const manyTours = Array.from({ length: 25 }, (_, i) => ({
      ...mockTours[0],
      id: i + 1,
      title: `Tour ${i + 1}`
    }));

    render(<TourList tours={manyTours} itemsPerPage={10} />);

    expect(screen.getByText('Page 1 of 3')).toBeInTheDocument();
    expect(screen.getByText('Next')).toBeInTheDocument();
    expect(screen.queryByText('Previous')).not.toBeInTheDocument();
  });

  test('navigates to next page when next button is clicked', () => {
    const manyTours = Array.from({ length: 25 }, (_, i) => ({
      ...mockTours[0],
      id: i + 1,
      title: `Tour ${i + 1}`
    }));

    render(<TourList tours={manyTours} itemsPerPage={10} />);

    const nextButton = screen.getByText('Next');
    fireEvent.click(nextButton);

    expect(screen.getByText('Page 2 of 3')).toBeInTheDocument();
    expect(screen.getByText('Previous')).toBeInTheDocument();
    expect(screen.getByText('Next')).toBeInTheDocument();
  });

  test('applies custom className', () => {
    const { container } = render(<TourList tours={mockTours} className="custom-tour-list" />);
    const tourList = container.querySelector('.tour-list');
    expect(tourList).toHaveClass('custom-tour-list');
  });

  test('displays message when no tours match filters', () => {
    render(<TourList tours={mockTours} searchTerm="Nonexistent" />);

    expect(screen.getByText('No tours match your search criteria. Please try different filters.')).toBeInTheDocument();
  });
});
