import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import UserProfile from '../UserProfile';

const mockUser = {
  id: 1,
  firstName: 'John',
  lastName: 'Doe',
  email: 'john.doe@example.com',
  phone: '+1 (555) 123-4567',
  dateOfBirth: '1990-01-01',
  address: {
    street: '123 Main St',
    city: 'New York',
    state: 'NY',
    zipCode: '10001',
    country: 'USA'
  },
  avatar: 'https://example.com/avatar.jpg',
  bio: 'Travel enthusiast and adventure seeker.',
  joinDate: '2020-01-15',
  role: 'USER',
  preferences: {
    language: 'en',
    currency: 'USD',
    notifications: {
      email: true,
      push: false,
      sms: true
    }
  }
};

const mockBookings = [
  {
    id: 1,
    tour: {
      id: 1,
      title: 'Paris City Tour',
      imageUrl: 'https://example.com/paris-tour.jpg'
    },
    status: 'CONFIRMED',
    bookingDate: '2023-12-25',
    totalPrice: 199.98,
    participants: 2
  },
  {
    id: 2,
    tour: {
      id: 2,
      title: 'Rome Adventure',
      imageUrl: 'https://example.com/rome-tour.jpg'
    },
    status: 'COMPLETED',
    bookingDate: '2023-10-15',
    totalPrice: 299.99,
    participants: 1
  }
];

const mockReviews = [
  {
    id: 1,
    tour: {
      id: 1,
      title: 'Paris City Tour',
      imageUrl: 'https://example.com/paris-tour.jpg'
    },
    rating: 5,
    comment: 'Amazing tour! The guide was very knowledgeable and friendly.',
    createdAt: '2023-11-15T10:00:00'
  },
  {
    id: 2,
    tour: {
      id: 2,
      title: 'Rome Adventure',
      imageUrl: 'https://example.com/rome-tour.jpg'
    },
    rating: 4,
    comment: 'Great experience, but the weather was not on our side.',
    createdAt: '2023-10-10T14:30:00'
  }
];

describe('UserProfile Component', () => {
  test('renders user information correctly', () => {
    render(<UserProfile user={mockUser} />);

    expect(screen.getByText('John Doe')).toBeInTheDocument();
    expect(screen.getByText('john.doe@example.com')).toBeInTheDocument();
    expect(screen.getByText('+1 (555) 123-4567')).toBeInTheDocument();
    expect(screen.getByText('January 1, 1990')).toBeInTheDocument();
    expect(screen.getByText('123 Main St, New York, NY 10001, USA')).toBeInTheDocument();
    expect(screen.getByText('Travel enthusiast and adventure seeker.')).toBeInTheDocument();
    expect(screen.getByText('Member since January 15, 2020')).toBeInTheDocument();
  });

  test('displays user avatar', () => {
    render(<UserProfile user={mockUser} />);

    const avatar = screen.getByAltText('John Doe');
    expect(avatar).toBeInTheDocument();
    expect(avatar).toHaveAttribute('src', 'https://example.com/avatar.jpg');
  });

  test('displays placeholder avatar when avatar is not provided', () => {
    const userWithoutAvatar = { ...mockUser, avatar: null };
    render(<UserProfile user={userWithoutAvatar} />);

    const avatar = screen.getByAltText('User avatar');
    expect(avatar).toBeInTheDocument();
    expect(avatar).toHaveAttribute('src', expect.stringContaining('placeholder'));
  });

  test('displays bookings when provided', () => {
    render(<UserProfile user={mockUser} bookings={mockBookings} />);

    expect(screen.getByText('My Bookings')).toBeInTheDocument();
    expect(screen.getByText('Paris City Tour')).toBeInTheDocument();
    expect(screen.getByText('Rome Adventure')).toBeInTheDocument();
    expect(screen.getByText('Confirmed')).toBeInTheDocument();
    expect(screen.getByText('Completed')).toBeInTheDocument();
    expect(screen.getByText('$199.98')).toBeInTheDocument();
    expect(screen.getByText('$299.99')).toBeInTheDocument();
  });

  test('displays reviews when provided', () => {
    render(<UserProfile user={mockUser} reviews={mockReviews} />);

    expect(screen.getByText('My Reviews')).toBeInTheDocument();
    expect(screen.getByText('Amazing tour! The guide was very knowledgeable and friendly.')).toBeInTheDocument();
    expect(screen.getByText('Great experience, but the weather was not on our side.')).toBeInTheDocument();
  });

  test('calls onEditProfileClick when edit profile button is clicked', () => {
    const handleEditProfileClick = jest.fn();
    render(<UserProfile user={mockUser} onEditProfileClick={handleEditProfileClick} />);

    const editButton = screen.getByText('Edit Profile');
    fireEvent.click(editButton);

    expect(handleEditProfileClick).toHaveBeenCalledTimes(1);
  });

  test('calls onBookingClick when a booking is clicked', () => {
    const handleBookingClick = jest.fn();
    render(<UserProfile user={mockUser} bookings={mockBookings} onBookingClick={handleBookingClick} />);

    const bookingCard = screen.getByText('Paris City Tour').closest('.booking-card');
    fireEvent.click(bookingCard);

    expect(handleBookingClick).toHaveBeenCalledTimes(1);
    expect(handleBookingClick).toHaveBeenCalledWith(mockBookings[0]);
  });

  test('calls onReviewClick when a review is clicked', () => {
    const handleReviewClick = jest.fn();
    render(<UserProfile user={mockUser} reviews={mockReviews} onReviewClick={handleReviewClick} />);

    const reviewCard = screen.getByText('Amazing tour! The guide was very knowledgeable and friendly.').closest('.review-card');
    fireEvent.click(reviewCard);

    expect(handleReviewClick).toHaveBeenCalledTimes(1);
    expect(handleReviewClick).toHaveBeenCalledWith(mockReviews[0]);
  });

  test('displays loading state when isLoading is true', () => {
    render(<UserProfile isLoading={true} />);

    expect(screen.getByTestId('user-profile-loading')).toBeInTheDocument();
  });

  test('displays error message when error is provided', () => {
    render(<UserProfile error="Failed to load user profile" />);

    expect(screen.getByText('Failed to load user profile')).toBeInTheDocument();
  });

  test('applies custom className', () => {
    const { container } = render(<UserProfile user={mockUser} className="custom-user-profile" />);
    const userProfile = container.querySelector('.user-profile');
    expect(userProfile).toHaveClass('custom-user-profile');
  });

  test('displays empty state when no bookings are provided', () => {
    render(<UserProfile user={mockUser} bookings={[]} />);

    expect(screen.getByText('My Bookings')).toBeInTheDocument();
    expect(screen.getByText('You haven't made any bookings yet.')).toBeInTheDocument();
    expect(screen.getByText('Browse Tours')).toBeInTheDocument();
  });

  test('displays empty state when no reviews are provided', () => {
    render(<UserProfile user={mockUser} reviews={[]} />);

    expect(screen.getByText('My Reviews')).toBeInTheDocument();
    expect(screen.getByText('You haven't written any reviews yet.')).toBeInTheDocument();
    expect(screen.getByText('Write a Review')).toBeInTheDocument();
  });

  test('calls onBrowseToursClick when browse tours button is clicked', () => {
    const handleBrowseToursClick = jest.fn();
    render(<UserProfile user={mockUser} bookings={[]} onBrowseToursClick={handleBrowseToursClick} />);

    const browseButton = screen.getByText('Browse Tours');
    fireEvent.click(browseButton);

    expect(handleBrowseToursClick).toHaveBeenCalledTimes(1);
  });

  test('calls onWriteReviewClick when write review button is clicked', () => {
    const handleWriteReviewClick = jest.fn();
    render(<UserProfile user={mockUser} reviews={[]} onWriteReviewClick={handleWriteReviewClick} />);

    const writeReviewButton = screen.getByText('Write a Review');
    fireEvent.click(writeReviewButton);

    expect(handleWriteReviewClick).toHaveBeenCalledTimes(1);
  });

  test('displays user preferences when showPreferences is true', () => {
    render(<UserProfile user={mockUser} showPreferences={true} />);

    expect(screen.getByText('Preferences')).toBeInTheDocument();
    expect(screen.getByText('Language')).toBeInTheDocument();
    expect(screen.getByText('Currency')).toBeInTheDocument();
    expect(screen.getByText('Notifications')).toBeInTheDocument();
    expect(screen.getByText('Email')).toBeInTheDocument();
    expect(screen.getByText('Push')).toBeInTheDocument();
    expect(screen.getByText('SMS')).toBeInTheDocument();
  });

  test('calls onPreferenceChange when a preference is changed', () => {
    const handlePreferenceChange = jest.fn();
    render(<UserProfile user={mockUser} showPreferences={true} onPreferenceChange={handlePreferenceChange} />);

    const languageSelect = screen.getByDisplayValue('English');
    fireEvent.change(languageSelect, { target: { value: 'fr' } });

    expect(handlePreferenceChange).toHaveBeenCalledTimes(1);
    expect(handlePreferenceChange).toHaveBeenCalledWith('language', 'fr');
  });

  test('displays membership tier when user has premium status', () => {
    const premiumUser = { ...mockUser, membershipTier: 'PREMIUM' };
    render(<UserProfile user={premiumUser} />);

    expect(screen.getByText('Premium Member')).toBeInTheDocument();
  });

  test('displays membership benefits when user has premium status', () => {
    const premiumUser = { ...mockUser, membershipTier: 'PREMIUM' };
    render(<UserProfile user={premiumUser} showMembershipBenefits={true} />);

    expect(screen.getByText('Membership Benefits')).toBeInTheDocument();
    expect(screen.getByText('Priority Booking')).toBeInTheDocument();
    expect(screen.getByText('Exclusive Discounts')).toBeInTheDocument();
    expect(screen.getByText('Free Cancellation')).toBeInTheDocument();
  });

  test('calls onUpgradeClick when upgrade button is clicked', () => {
    const handleUpgradeClick = jest.fn();
    render(<UserProfile user={mockUser} onUpgradeClick={handleUpgradeClick} />);

    const upgradeButton = screen.getByText('Upgrade to Premium');
    fireEvent.click(upgradeButton);

    expect(handleUpgradeClick).toHaveBeenCalledTimes(1);
  });
});
