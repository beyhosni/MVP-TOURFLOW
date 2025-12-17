import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import TourFilters from '../TourFilters';

describe('TourFilters Component', () => {
  test('renders filter options', () => {
    render(<TourFilters />);

    expect(screen.getByText('Price Range')).toBeInTheDocument();
    expect(screen.getByText('Duration')).toBeInTheDocument();
    expect(screen.getByText('Rating')).toBeInTheDocument();
    expect(screen.getByText('Tour Type')).toBeInTheDocument();
    expect(screen.getByText('Departure Date')).toBeInTheDocument();
    expect(screen.getByText('Location')).toBeInTheDocument();
  });

  test('calls onFilterChange when price range changes', () => {
    const handleFilterChange = jest.fn();
    render(<TourFilters onFilterChange={handleFilterChange} />);

    const minPriceInput = screen.getByLabelText('Min Price');
    const maxPriceInput = screen.getByLabelText('Max Price');

    fireEvent.change(minPriceInput, { target: { value: '50' } });
    fireEvent.change(maxPriceInput, { target: { value: '200' } });

    expect(handleFilterChange).toHaveBeenCalledTimes(2);
    expect(handleFilterChange).toHaveBeenCalledWith({ minPrice: '50' });
    expect(handleFilterChange).toHaveBeenCalledWith({ maxPrice: '200' });
  });

  test('calls onFilterChange when duration changes', () => {
    const handleFilterChange = jest.fn();
    render(<TourFilters onFilterChange={handleFilterChange} />);

    const minDurationInput = screen.getByLabelText('Min Duration');
    const maxDurationInput = screen.getByLabelText('Max Duration');

    fireEvent.change(minDurationInput, { target: { value: '2' } });
    fireEvent.change(maxDurationInput, { target: { value: '7' } });

    expect(handleFilterChange).toHaveBeenCalledTimes(2);
    expect(handleFilterChange).toHaveBeenCalledWith({ minDuration: '2' });
    expect(handleFilterChange).toHaveBeenCalledWith({ maxDuration: '7' });
  });

  test('calls onFilterChange when rating changes', () => {
    const handleFilterChange = jest.fn();
    render(<TourFilters onFilterChange={handleFilterChange} />);

    const rating4Checkbox = screen.getByLabelText('4 stars & up');
    fireEvent.click(rating4Checkbox);

    expect(handleFilterChange).toHaveBeenCalledTimes(1);
    expect(handleFilterChange).toHaveBeenCalledWith({ minRating: 4 });
  });

  test('calls onFilterChange when tour type changes', () => {
    const handleFilterChange = jest.fn();
    render(<TourFilters onFilterChange={handleFilterChange} />);

    const adventureCheckbox = screen.getByLabelText('Adventure');
    fireEvent.click(adventureCheckbox);

    expect(handleFilterChange).toHaveBeenCalledTimes(1);
    expect(handleFilterChange).toHaveBeenCalledWith({ tourTypes: ['Adventure'] });
  });

  test('calls onFilterChange when departure date changes', () => {
    const handleFilterChange = jest.fn();
    render(<TourFilters onFilterChange={handleFilterChange} />);

    const departureDateInput = screen.getByLabelText('Departure Date');
    fireEvent.change(departureDateInput, { target: { value: '2023-12-25' } });

    expect(handleFilterChange).toHaveBeenCalledTimes(1);
    expect(handleFilterChange).toHaveBeenCalledWith({ departureDate: '2023-12-25' });
  });

  test('calls onFilterChange when location changes', () => {
    const handleFilterChange = jest.fn();
    render(<TourFilters onFilterChange={handleFilterChange} />);

    const locationSelect = screen.getByLabelText('Location');
    fireEvent.change(locationSelect, { target: { value: 'Paris' } });

    expect(handleFilterChange).toHaveBeenCalledTimes(1);
    expect(handleFilterChange).toHaveBeenCalledWith({ location: 'Paris' });
  });

  test('applies initial filter values', () => {
    const initialFilters = {
      minPrice: '50',
      maxPrice: '200',
      minDuration: '2',
      maxDuration: '7',
      minRating: 4,
      tourTypes: ['Adventure'],
      departureDate: '2023-12-25',
      location: 'Paris'
    };

    render(<TourFilters initialFilters={initialFilters} />);

    expect(screen.getByDisplayValue('50')).toBeInTheDocument();
    expect(screen.getByDisplayValue('200')).toBeInTheDocument();
    expect(screen.getByDisplayValue('2')).toBeInTheDocument();
    expect(screen.getByDisplayValue('7')).toBeInTheDocument();
    expect(screen.getByDisplayValue('2023-12-25')).toBeInTheDocument();
    expect(screen.getByDisplayValue('Paris')).toBeInTheDocument();
    expect(screen.getByLabelText('4 stars & up')).toBeChecked();
    expect(screen.getByLabelText('Adventure')).toBeChecked();
  });

  test('applies custom className', () => {
    const { container } = render(<TourFilters className="custom-tour-filters" />);
    const filters = container.querySelector('.tour-filters');
    expect(filters).toHaveClass('custom-tour-filters');
  });

  test('displays clear filters button when showClearButton is true', () => {
    render(<TourFilters showClearButton={true} />);

    expect(screen.getByRole('button', { name: 'Clear Filters' })).toBeInTheDocument();
  });

  test('calls onClearFilters when clear filters button is clicked', () => {
    const handleClearFilters = jest.fn();
    render(<TourFilters showClearButton={true} onClearFilters={handleClearFilters} />);

    const clearButton = screen.getByRole('button', { name: 'Clear Filters' });
    fireEvent.click(clearButton);

    expect(handleClearFilters).toHaveBeenCalledTimes(1);
  });

  test('displays apply filters button when showApplyButton is true', () => {
    render(<TourFilters showApplyButton={true} />);

    expect(screen.getByRole('button', { name: 'Apply Filters' })).toBeInTheDocument();
  });

  test('calls onApplyFilters when apply filters button is clicked', () => {
    const handleApplyFilters = jest.fn();
    render(<TourFilters showApplyButton={true} onApplyFilters={handleApplyFilters} />);

    const applyButton = screen.getByRole('button', { name: 'Apply Filters' });
    fireEvent.click(applyButton);

    expect(handleApplyFilters).toHaveBeenCalledTimes(1);
  });

  test('displays expanded state when expanded prop is true', () => {
    render(<TourFilters expanded={true} />);

    expect(screen.getByText('Price Range')).toBeVisible();
    expect(screen.getByText('Duration')).toBeVisible();
    expect(screen.getByText('Rating')).toBeVisible();
    expect(screen.getByText('Tour Type')).toBeVisible();
    expect(screen.getByText('Departure Date')).toBeVisible();
    expect(screen.getByText('Location')).toBeVisible();
  });

  test('displays collapsed state when expanded prop is false', () => {
    render(<TourFilters expanded={false} />);

    expect(screen.getByText('Price Range')).toBeVisible();
    expect(screen.queryByLabelText('Min Price')).not.toBeVisible();
    expect(screen.queryByLabelText('Max Price')).not.toBeVisible();
  });

  test('toggles expanded state when toggle button is clicked', () => {
    const { rerender } = render(<TourFilters expanded={false} />);

    expect(screen.queryByLabelText('Min Price')).not.toBeVisible();

    const toggleButton = screen.getByRole('button', { name: 'Toggle Filters' });
    fireEvent.click(toggleButton);

    rerender(<TourFilters expanded={true} />);

    expect(screen.getByLabelText('Min Price')).toBeVisible();
  });

  test('displays filter count when filters are applied', () => {
    const activeFilters = {
      minPrice: '50',
      maxPrice: '200',
      minDuration: '2',
      maxDuration: '7',
      minRating: 4,
      tourTypes: ['Adventure'],
      departureDate: '2023-12-25',
      location: 'Paris'
    };

    render(<TourFilters activeFilters={activeFilters} />);

    expect(screen.getByText('7 filters applied')).toBeInTheDocument();
  });

  test('validates price range', () => {
    render(<TourFilters />);

    const minPriceInput = screen.getByLabelText('Min Price');
    const maxPriceInput = screen.getByLabelText('Max Price');

    fireEvent.change(minPriceInput, { target: { value: '200' } });
    fireEvent.change(maxPriceInput, { target: { value: '50' } });

    expect(screen.getByText('Min price cannot be greater than max price')).toBeInTheDocument();
  });

  test('validates duration range', () => {
    render(<TourFilters />);

    const minDurationInput = screen.getByLabelText('Min Duration');
    const maxDurationInput = screen.getByLabelText('Max Duration');

    fireEvent.change(minDurationInput, { target: { value: '7' } });
    fireEvent.change(maxDurationInput, { target: { value: '2' } });

    expect(screen.getByText('Min duration cannot be greater than max duration')).toBeInTheDocument();
  });

  test('validates departure date', () => {
    render(<TourFilters />);

    const departureDateInput = screen.getByLabelText('Departure Date');
    const pastDate = new Date();
    pastDate.setDate(pastDate.getDate() - 1);

    fireEvent.change(departureDateInput, { target: { value: pastDate.toISOString().split('T')[0] } });

    expect(screen.getByText('Departure date must be in future')).toBeInTheDocument();
  });

  test('displays popular locations when showPopularLocations is true', () => {
    render(<TourFilters showPopularLocations={true} />);

    expect(screen.getByText('Popular Locations')).toBeInTheDocument();
    expect(screen.getByText('Paris')).toBeInTheDocument();
    expect(screen.getByText('Rome')).toBeInTheDocument();
    expect(screen.getByText('London')).toBeInTheDocument();
    expect(screen.getByText('New York')).toBeInTheDocument();
  });

  test('calls onLocationClick when a popular location is clicked', () => {
    const handleLocationClick = jest.fn();
    render(<TourFilters showPopularLocations={true} onLocationClick={handleLocationClick} />);

    const locationButton = screen.getByText('Paris');
    fireEvent.click(locationButton);

    expect(handleLocationClick).toHaveBeenCalledTimes(1);
    expect(handleLocationClick).toHaveBeenCalledWith('Paris');
  });
});
