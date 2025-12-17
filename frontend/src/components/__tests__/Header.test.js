import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import { BrowserRouter } from 'react-router-dom';
import Header from '../Header';

// Mock the useAuth hook
jest.mock('../../hooks/useAuth', () => ({
  __esModule: true,
  default: () => ({
    user: null,
    isAuthenticated: false,
    login: jest.fn(),
    logout: jest.fn()
  })
}));

const MockHeader = () => {
  return (
    <BrowserRouter>
      <Header />
    </BrowserRouter>
  );
};

describe('Header Component', () => {
  test('renders header with logo and navigation links', () => {
    render(<MockHeader />);

    expect(screen.getByAltText('TourFlow Logo')).toBeInTheDocument();
    expect(screen.getByText('Home')).toBeInTheDocument();
    expect(screen.getByText('Tours')).toBeInTheDocument();
    expect(screen.getByText('About')).toBeInTheDocument();
    expect(screen.getByText('Contact')).toBeInTheDocument();
  });

  test('renders login and register buttons when user is not authenticated', () => {
    render(<MockHeader />);

    expect(screen.getByText('Login')).toBeInTheDocument();
    expect(screen.getByText('Register')).toBeInTheDocument();
    expect(screen.queryByText('My Account')).not.toBeInTheDocument();
    expect(screen.queryByText('Logout')).not.toBeInTheDocument();
  });

  test('renders user menu when user is authenticated', () => {
    // Mock the useAuth hook to return an authenticated user
    jest.doMock('../../hooks/useAuth', () => ({
      __esModule: true,
      default: () => ({
        user: {
          id: 1,
          firstName: 'John',
          lastName: 'Doe',
          email: 'john.doe@example.com',
          role: 'USER'
        },
        isAuthenticated: true,
        login: jest.fn(),
        logout: jest.fn()
      })
    }));

    render(<MockHeader />);

    expect(screen.queryByText('Login')).not.toBeInTheDocument();
    expect(screen.queryByText('Register')).not.toBeInTheDocument();
    expect(screen.getByText('My Account')).toBeInTheDocument();
    expect(screen.getByText('Logout')).toBeInTheDocument();
  });

  test('opens mobile menu when menu button is clicked', () => {
    render(<MockHeader />);

    const menuButton = screen.getByLabelText('Open menu');
    expect(screen.queryByText('Home')).not.toBeVisible();

    fireEvent.click(menuButton);

    expect(screen.getByText('Home')).toBeVisible();
    expect(screen.getByText('Tours')).toBeVisible();
    expect(screen.getByText('About')).toBeVisible();
    expect(screen.getByText('Contact')).toBeVisible();
  });

  test('closes mobile menu when close button is clicked', () => {
    render(<MockHeader />);

    const menuButton = screen.getByLabelText('Open menu');
    fireEvent.click(menuButton);

    const closeButton = screen.getByLabelText('Close menu');
    fireEvent.click(closeButton);

    expect(screen.queryByText('Home')).not.toBeVisible();
  });

  test('navigates to correct page when navigation links are clicked', () => {
    render(<MockHeader />);

    const toursLink = screen.getByText('Tours');
    fireEvent.click(toursLink);

    expect(window.location.pathname).toBe('/tours');
  });

  test('opens login modal when login button is clicked', () => {
    render(<MockHeader />);

    const loginButton = screen.getByText('Login');
    fireEvent.click(loginButton);

    expect(screen.getByText('Login to Your Account')).toBeInTheDocument();
  });

  test('opens register modal when register button is clicked', () => {
    render(<MockHeader />);

    const registerButton = screen.getByText('Register');
    fireEvent.click(registerButton);

    expect(screen.getByText('Create Your Account')).toBeInTheDocument();
  });

  test('opens user dropdown when user menu is clicked', () => {
    // Mock the useAuth hook to return an authenticated user
    jest.doMock('../../hooks/useAuth', () => ({
      __esModule: true,
      default: () => ({
        user: {
          id: 1,
          firstName: 'John',
          lastName: 'Doe',
          email: 'john.doe@example.com',
          role: 'USER'
        },
        isAuthenticated: true,
        login: jest.fn(),
        logout: jest.fn()
      })
    }));

    render(<MockHeader />);

    const userMenuButton = screen.getByText('My Account');
    fireEvent.click(userMenuButton);

    expect(screen.getByText('My Bookings')).toBeInTheDocument();
    expect(screen.getByText('My Profile')).toBeInTheDocument();
    expect(screen.getByText('Logout')).toBeInTheDocument();
  });

  test('calls logout function when logout button is clicked', () => {
    const mockLogout = jest.fn();

    // Mock the useAuth hook to return an authenticated user
    jest.doMock('../../hooks/useAuth', () => ({
      __esModule: true,
      default: () => ({
        user: {
          id: 1,
          firstName: 'John',
          lastName: 'Doe',
          email: 'john.doe@example.com',
          role: 'USER'
        },
        isAuthenticated: true,
        login: jest.fn(),
        logout: mockLogout
      })
    }));

    render(<MockHeader />);

    const userMenuButton = screen.getByText('My Account');
    fireEvent.click(userMenuButton);

    const logoutButton = screen.getByText('Logout');
    fireEvent.click(logoutButton);

    expect(mockLogout).toHaveBeenCalledTimes(1);
  });

  test('applies custom className', () => {
    const { container } = render(<Header className="custom-header" />);
    const header = container.querySelector('.header');
    expect(header).toHaveClass('custom-header');
  });

  test('displays notification badge when there are notifications', () => {
    // Mock the useAuth hook to return an authenticated user with notifications
    jest.doMock('../../hooks/useAuth', () => ({
      __esModule: true,
      default: () => ({
        user: {
          id: 1,
          firstName: 'John',
          lastName: 'Doe',
          email: 'john.doe@example.com',
          role: 'USER',
          notifications: [
            {
              id: 1,
              title: 'Booking Confirmed',
              message: 'Your booking has been confirmed',
              read: false,
              createdAt: '2023-12-15T10:00:00'
            }
          ]
        },
        isAuthenticated: true,
        login: jest.fn(),
        logout: jest.fn()
      })
    }));

    render(<MockHeader />);

    const notificationIcon = screen.getByLabelText('Notifications');
    expect(notificationIcon).toBeInTheDocument();
    expect(screen.getByText('1')).toBeInTheDocument(); // Notification count
  });

  test('opens notification dropdown when notification icon is clicked', () => {
    // Mock the useAuth hook to return an authenticated user with notifications
    jest.doMock('../../hooks/useAuth', () => ({
      __esModule: true,
      default: () => ({
        user: {
          id: 1,
          firstName: 'John',
          lastName: 'Doe',
          email: 'john.doe@example.com',
          role: 'USER',
          notifications: [
            {
              id: 1,
              title: 'Booking Confirmed',
              message: 'Your booking has been confirmed',
              read: false,
              createdAt: '2023-12-15T10:00:00'
            }
          ]
        },
        isAuthenticated: true,
        login: jest.fn(),
        logout: jest.fn()
      })
    }));

    render(<MockHeader />);

    const notificationIcon = screen.getByLabelText('Notifications');
    fireEvent.click(notificationIcon);

    expect(screen.getByText('Booking Confirmed')).toBeInTheDocument();
    expect(screen.getByText('Your booking has been confirmed')).toBeInTheDocument();
  });
});
