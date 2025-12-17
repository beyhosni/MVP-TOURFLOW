import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import TourDetail from '../TourDetail';

const mockTour = {
  id: 1,
  title: 'Paris City Tour',
  description: 'Explore the beautiful city of Paris with our expert guides. Visit the Eiffel Tower, Louvre Museum, and more.',
  price: 99.99,
  duration: 3,
  maxParticipants: 20,
  status: 'ACTIVE',
  startDate: '2023-12-25T10:00:00',
  endDate: '2023-12-28T18:00:00',
  imageUrl: 'https://example.com/paris-tour.jpg',
  rating: 4.5,
  reviewCount: 128,
  location: 'Paris, France',
  itinerary: [
    {
      day: 1,
      title: 'Arrival in Paris',
      description: 'Check into your hotel and explore the neighborhood.',
      activities: ['Hotel check-in', 'Neighborhood walk', 'Welcome dinner']
    },
    {
      day: 2,
      title: 'Paris City Tour',
      description: 'Visit the main attractions of Paris.',
      activities: ['Eiffel Tower', 'Louvre Museum', 'Seine River Cruise']
    },
    {
      day: 3,
      title: 'Day Trip to Versailles',
      description: 'Explore the magnificent Palace of Versailles.',
      activities: ['Palace tour', 'Gardens visit', 'Return to Paris']
    }
  ],
  inclusions: [
    'Hotel accommodation',
    'Daily breakfast',
    'Transportation',
    'Guide services',
    'Entrance fees'
  ],
  exclusions: [
    'Lunch and dinner',
    'Personal expenses',
    'Travel insurance',
    'Tips and gratuities'
  ],
  whatToBring: [
    'Comfortable walking shoes',
    'Weather-appropriate clothing',
    'Camera',
    'Sunscreen',
    'Water bottle'
  ],
  meetingPoint: 'Eiffel Tower, Paris',
  departureTime: '09:00 AM',
  returnTime: '06:00 PM',
  availableDates: [
    '2023-12-25',
    '2023-12-26',
    '2023-12-27',
    '2023-12-28',
    '2023-12-29'
  ],
  reviews: [
    {
      id: 1,
      user: {
        firstName: 'John',
        lastName: 'Doe',
        avatar: 'https://example.com/avatar.jpg'
      },
      rating: 5,
      comment: 'Amazing tour! The guide was very knowledgeable and friendly.',
      createdAt: '2023-11-15T10:00:00'
    },
    {
      id: 2,
      user: {
        firstName: 'Jane',
        lastName: 'Smith',
        avatar: 'https://example.com/avatar2.jpg'
      },
      rating: 4,
      comment: 'Great experience, but the weather was not on our side.',
      createdAt: '2023-11-10T14:30:00'
    }
  ]
};

describe('TourDetail Component', () => {
  test('renders tour information correctly', () => {
    render(<TourDetail tour={mockTour} />);

    expect(screen.getByText('Paris City Tour')).toBeInTheDocument();
    expect(screen.getByText('Explore the beautiful city of Paris with our expert guides. Visit the Eiffel Tower, Louvre Museum, and more.')).toBeInTheDocument();
    expect(screen.getByText('€99.99')).toBeInTheDocument();
    expect(screen.getByText('3 days')).toBeInTheDocument();
    expect(screen.getByText('Paris, France')).toBeInTheDocument();
    expect(screen.getByText('4.5')).toBeInTheDocument();
    expect(screen.getByText('(128 reviews)')).toBeInTheDocument();
  });

  test('displays tour image', () => {
    render(<TourDetail tour={mockTour} />);

    const image = screen.getByAltText('Paris City Tour');
    expect(image).toBeInTheDocument();
    expect(image).toHaveAttribute('src', 'https://example.com/paris-tour.jpg');
  });

  test('displays itinerary', () => {
    render(<TourDetail tour={mockTour} />);

    expect(screen.getByText('Itinerary')).toBeInTheDocument();
    expect(screen.getByText('Day 1: Arrival in Paris')).toBeInTheDocument();
    expect(screen.getByText('Day 2: Paris City Tour')).toBeInTheDocument();
    expect(screen.getByText('Day 3: Day Trip to Versailles')).toBeInTheDocument();
    expect(screen.getByText('Check into your hotel and explore the neighborhood.')).toBeInTheDocument();
    expect(screen.getByText('Visit the main attractions of Paris.')).toBeInTheDocument();
    expect(screen.getByText('Explore the magnificent Palace of Versailles.')).toBeInTheDocument();
  });

  test('displays inclusions and exclusions', () => {
    render(<TourDetail tour={mockTour} />);

    expect(screen.getByText('What's Included')).toBeInTheDocument();
    expect(screen.getByText('What's Not Included')).toBeInTheDocument();
    expect(screen.getByText('Hotel accommodation')).toBeInTheDocument();
    expect(screen.getByText('Daily breakfast')).toBeInTheDocument();
    expect(screen.getByText('Transportation')).toBeInTheDocument();
    expect(screen.getByText('Guide services')).toBeInTheDocument();
    expect(screen.getByText('Entrance fees')).toBeInTheDocument();
    expect(screen.getByText('Lunch and dinner')).toBeInTheDocument();
    expect(screen.getByText('Personal expenses')).toBeInTheDocument();
    expect(screen.getByText('Travel insurance')).toBeInTheDocument();
    expect(screen.getByText('Tips and gratuities')).toBeInTheDocument();
  });

  test('displays what to bring', () => {
    render(<TourDetail tour={mockTour} />);

    expect(screen.getByText('What to Bring')).toBeInTheDocument();
    expect(screen.getByText('Comfortable walking shoes')).toBeInTheDocument();
    expect(screen.getByText('Weather-appropriate clothing')).toBeInTheDocument();
    expect(screen.getByText('Camera')).toBeInTheDocument();
    expect(screen.getByText('Sunscreen')).toBeInTheDocument();
    expect(screen.getByText('Water bottle')).toBeInTheDocument();
  });

  test('displays meeting point and times', () => {
    render(<TourDetail tour={mockTour} />);

    expect(screen.getByText('Meeting Point')).toBeInTheDocument();
    expect(screen.getByText('Eiffel Tower, Paris')).toBeInTheDocument();
    expect(screen.getByText('Departure Time')).toBeInTheDocument();
    expect(screen.getByText('09:00 AM')).toBeInTheDocument();
    expect(screen.getByText('Return Time')).toBeInTheDocument();
    expect(screen.getByText('06:00 PM')).toBeInTheDocument();
  });

  test('displays available dates', () => {
    render(<TourDetail tour={mockTour} />);

    expect(screen.getByText('Available Dates')).toBeInTheDocument();
    expect(screen.getByText('Dec 25, 2023')).toBeInTheDocument();
    expect(screen.getByText('Dec 26, 2023')).toBeInTheDocument();
    expect(screen.getByText('Dec 27, 2023')).toBeInTheDocument();
    expect(screen.getByText('Dec 28, 2023')).toBeInTheDocument();
    expect(screen.getByText('Dec 29, 2023')).toBeInTheDocument();
  });

  test('displays reviews', () => {
    render(<TourDetail tour={mockTour} />);

    expect(screen.getByText('Reviews')).toBeInTheDocument();
    expect(screen.getByText('John Doe')).toBeInTheDocument();
    expect(screen.getByText('Jane Smith')).toBeInTheDocument();
    expect(screen.getByText('Amazing tour! The guide was very knowledgeable and friendly.')).toBeInTheDocument();
    expect(screen.getByText('Great experience, but the weather was not on our side.')).toBeInTheDocument();
  });

  test('calls onBookClick when book button is clicked', () => {
    const handleBookClick = jest.fn();
    render(<TourDetail tour={mockTour} onBookClick={handleBookClick} />);

    const bookButton = screen.getByText('Book Now');
    fireEvent.click(bookButton);

    expect(handleBookClick).toHaveBeenCalledTimes(1);
    expect(handleBookClick).toHaveBeenCalledWith(mockTour);
  });

  test('calls onAddToWishlistClick when wishlist button is clicked', () => {
    const handleAddToWishlistClick = jest.fn();
    render(<TourDetail tour={mockTour} onAddToWishlistClick={handleAddToWishlistClick} />);

    const wishlistButton = screen.getByLabelText('Add to wishlist');
    fireEvent.click(wishlistButton);

    expect(handleAddToWishlistClick).toHaveBeenCalledTimes(1);
    expect(handleAddToWishlistClick).toHaveBeenCalledWith(mockTour);
  });

  test('displays "In Wishlist" when tour is in wishlist', () => {
    render(<TourDetail tour={mockTour} isInWishlist={true} />);

    expect(screen.getByLabelText('Remove from wishlist')).toBeInTheDocument();
    expect(screen.getByText('In Wishlist')).toBeInTheDocument();
  });

  test('displays "Sold Out" when tour is sold out', () => {
    const soldOutTour = { ...mockTour, status: 'SOLD_OUT' };
    render(<TourDetail tour={soldOutTour} />);

    expect(screen.getByText('Sold Out')).toBeInTheDocument();
    expect(screen.getByText('Book Now')).toBeDisabled();
  });

  test('applies custom className', () => {
    const { container } = render(<TourDetail tour={mockTour} className="custom-tour-detail" />);
    const tourDetail = container.querySelector('.tour-detail');
    expect(tourDetail).toHaveClass('custom-tour-detail');
  });

  test('displays "Sale" badge when tour is on sale', () => {
    const saleTour = { ...mockTour, salePrice: 79.99 };
    render(<TourDetail tour={saleTour} />);

    expect(screen.getByText('Sale')).toBeInTheDocument();
    expect(screen.getByText('€79.99')).toBeInTheDocument();
    expect(screen.getByText('€99.99')).toHaveClass('original-price');
  });

  test('displays "Limited Spots" badge when available spots are low', () => {
    const tourWithLowAvailability = { ...mockTour, availableSpots: 3 };
    render(<TourDetail tour={tourWithLowAvailability} />);

    expect(screen.getByText('Limited Spots')).toBeInTheDocument();
  });

  test('displays "New" badge when tour is new', () => {
    const newTour = { ...mockTour, isNew: true };
    render(<TourDetail tour={newTour} />);

    expect(screen.getByText('New')).toBeInTheDocument();
  });

  test('displays "Popular" badge when tour is popular', () => {
    const popularTour = { ...mockTour, isPopular: true };
    render(<TourDetail tour={popularTour} />);

    expect(screen.getByText('Popular')).toBeInTheDocument();
  });

  test('displays loading state when isLoading is true', () => {
    render(<TourDetail isLoading={true} />);

    expect(screen.getByTestId('tour-detail-loading')).toBeInTheDocument();
  });

  test('displays error message when error is provided', () => {
    render(<TourDetail error="Failed to load tour details" />);

    expect(screen.getByText('Failed to load tour details')).toBeInTheDocument();
  });
});
