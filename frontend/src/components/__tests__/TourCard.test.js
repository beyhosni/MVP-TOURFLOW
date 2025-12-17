import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import TourCard from '../TourCard';

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

describe('TourCard Component', () => {
  test('renders tour information correctly', () => {
    render(<TourCard tour={mockTour} />);

    expect(screen.getByText('Paris City Tour')).toBeInTheDocument();
    expect(screen.getByText('Explore the beautiful city of Paris')).toBeInTheDocument();
    expect(screen.getByText('€99.99')).toBeInTheDocument();
    expect(screen.getByText('3 days')).toBeInTheDocument();
    expect(screen.getByText('Paris, France')).toBeInTheDocument();
    expect(screen.getByText('4.5')).toBeInTheDocument();
    expect(screen.getByText('(128)')).toBeInTheDocument();
  });

  test('displays tour image when imageUrl is provided', () => {
    render(<TourCard tour={mockTour} />);

    const image = screen.getByAltText('Paris City Tour');
    expect(image).toBeInTheDocument();
    expect(image).toHaveAttribute('src', 'https://example.com/paris-tour.jpg');
  });

  test('displays placeholder image when imageUrl is not provided', () => {
    const tourWithoutImage = { ...mockTour, imageUrl: null };
    render(<TourCard tour={tourWithoutImage} />);

    const image = screen.getByAltText('Tour placeholder');
    expect(image).toBeInTheDocument();
    expect(image).toHaveAttribute('src', 'https://via.placeholder.com/300x200?text=No+Image');
  });

  test('calls onBookClick when book button is clicked', () => {
    const handleBookClick = jest.fn();
    render(<TourCard tour={mockTour} onBookClick={handleBookClick} />);

    const bookButton = screen.getByText('Book Now');
    fireEvent.click(bookButton);
    expect(handleBookClick).toHaveBeenCalledTimes(1);
    expect(handleBookClick).toHaveBeenCalledWith(mockTour);
  });

  test('calls onViewDetailsClick when view details button is clicked', () => {
    const handleViewDetailsClick = jest.fn();
    render(<TourCard tour={mockTour} onViewDetailsClick={handleViewDetailsClick} />);

    const viewDetailsButton = screen.getByText('View Details');
    fireEvent.click(viewDetailsButton);
    expect(handleViewDetailsClick).toHaveBeenCalledTimes(1);
    expect(handleViewDetailsClick).toHaveBeenCalledWith(mockTour);
  });

  test('displays sale badge when salePrice is provided', () => {
    const tourWithSale = { ...mockTour, salePrice: 79.99 };
    render(<TourCard tour={tourWithSale} />);

    expect(screen.getByText('Sale')).toBeInTheDocument();
    expect(screen.getByText('€79.99')).toBeInTheDocument();
    expect(screen.getByText('€99.99')).toHaveClass('original-price');
  });

  test('displays "Limited Spots" badge when available spots are low', () => {
    const tourWithLowAvailability = { ...mockTour, availableSpots: 3 };
    render(<TourCard tour={tourWithLowAvailability} />);

    expect(screen.getByText('Limited Spots')).toBeInTheDocument();
  });

  test('displays "Sold Out" badge when tour is sold out', () => {
    const soldOutTour = { ...mockTour, status: 'SOLD_OUT' };
    render(<TourCard tour={soldOutTour} />);

    expect(screen.getByText('Sold Out')).toBeInTheDocument();
    expect(screen.getByText('Book Now')).toBeDisabled();
  });

  test('applies custom className', () => {
    const { container } = render(<TourCard tour={mockTour} className="custom-tour-card" />);
    const tourCard = container.querySelector('.tour-card');
    expect(tourCard).toHaveClass('custom-tour-card');
  });

  test('displays "New" badge when tour is new', () => {
    const newTour = { ...mockTour, isNew: true };
    render(<TourCard tour={newTour} />);

    expect(screen.getByText('New')).toBeInTheDocument();
  });

  test('displays "Popular" badge when tour is popular', () => {
    const popularTour = { ...mockTour, isPopular: true };
    render(<TourCard tour={popularTour} />);

    expect(screen.getByText('Popular')).toBeInTheDocument();
  });

  test('applies size variant correctly', () => {
    const { container } = render(<TourCard tour={mockTour} size="large" />);
    const tourCard = container.querySelector('.tour-card');
    expect(tourCard).toHaveClass('tour-card-large');
  });

  test('displays formatted date correctly', () => {
    render(<TourCard tour={mockTour} />);

    expect(screen.getByText('Dec 25, 2023')).toBeInTheDocument();
  });

  test('displays wishlist button when showWishlistButton is true', () => {
    render(<TourCard tour={mockTour} showWishlistButton={true} />);

    const wishlistButton = screen.getByLabelText('Add to wishlist');
    expect(wishlistButton).toBeInTheDocument();
  });

  test('calls onWishlistClick when wishlist button is clicked', () => {
    const handleWishlistClick = jest.fn();
    render(<TourCard tour={mockTour} showWishlistButton={true} onWishlistClick={handleWishlistClick} />);

    const wishlistButton = screen.getByLabelText('Add to wishlist');
    fireEvent.click(wishlistButton);
    expect(handleWishlistClick).toHaveBeenCalledTimes(1);
    expect(handleWishlistClick).toHaveBeenCalledWith(mockTour);
  });

  test('displays "In Wishlist" when tour is in wishlist', () => {
    render(<TourCard tour={mockTour} showWishlistButton={true} isInWishlist={true} />);

    const wishlistButton = screen.getByLabelText('Remove from wishlist');
    expect(wishlistButton).toBeInTheDocument();
    expect(screen.getByText('In Wishlist')).toBeInTheDocument();
  });
});
