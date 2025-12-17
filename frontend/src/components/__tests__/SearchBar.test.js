import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import SearchBar from '../SearchBar';

describe('SearchBar Component', () => {
  test('renders search input and button', () => {
    render(<SearchBar />);

    expect(screen.getByPlaceholderText('Search tours...')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Search' })).toBeInTheDocument();
  });

  test('calls onSearch when search button is clicked', () => {
    const handleSearch = jest.fn();
    render(<SearchBar onSearch={handleSearch} />);

    const searchInput = screen.getByPlaceholderText('Search tours...');
    const searchButton = screen.getByRole('button', { name: 'Search' });

    fireEvent.change(searchInput, { target: { value: 'Paris' } });
    fireEvent.click(searchButton);

    expect(handleSearch).toHaveBeenCalledTimes(1);
    expect(handleSearch).toHaveBeenCalledWith('Paris');
  });

  test('calls onSearch when Enter key is pressed', () => {
    const handleSearch = jest.fn();
    render(<SearchBar onSearch={handleSearch} />);

    const searchInput = screen.getByPlaceholderText('Search tours...');

    fireEvent.change(searchInput, { target: { value: 'Paris' } });
    fireEvent.keyPress(searchInput, { key: 'Enter', code: 'Enter', charCode: 13 });

    expect(handleSearch).toHaveBeenCalledTimes(1);
    expect(handleSearch).toHaveBeenCalledWith('Paris');
  });

  test('calls onSearch when search term changes and debounce is configured', async () => {
    const handleSearch = jest.fn();
    render(<SearchBar onSearch={handleSearch} debounce={300} />);

    const searchInput = screen.getByPlaceholderText('Search tours...');

    fireEvent.change(searchInput, { target: { value: 'Paris' } });

    // Wait for debounce
    await waitFor(() => {
      expect(handleSearch).toHaveBeenCalledTimes(1);
      expect(handleSearch).toHaveBeenCalledWith('Paris');
    }, { timeout: 500 });
  });

  test('displays search suggestions when provided', () => {
    const suggestions = ['Paris City Tour', 'Paris Adventure', 'Paris Day Trip'];
    render(<SearchBar suggestions={suggestions} />);

    const searchInput = screen.getByPlaceholderText('Search tours...');
    fireEvent.focus(searchInput);

    expect(screen.getByText('Paris City Tour')).toBeInTheDocument();
    expect(screen.getByText('Paris Adventure')).toBeInTheDocument();
    expect(screen.getByText('Paris Day Trip')).toBeInTheDocument();
  });

  test('filters suggestions based on search term', () => {
    const suggestions = ['Paris City Tour', 'Paris Adventure', 'Paris Day Trip', 'Rome Tour'];
    render(<SearchBar suggestions={suggestions} />);

    const searchInput = screen.getByPlaceholderText('Search tours...');
    fireEvent.focus(searchInput);
    fireEvent.change(searchInput, { target: { value: 'Paris' } });

    expect(screen.getByText('Paris City Tour')).toBeInTheDocument();
    expect(screen.getByText('Paris Adventure')).toBeInTheDocument();
    expect(screen.getByText('Paris Day Trip')).toBeInTheDocument();
    expect(screen.queryByText('Rome Tour')).not.toBeInTheDocument();
  });

  test('calls onSuggestionClick when a suggestion is clicked', () => {
    const suggestions = ['Paris City Tour', 'Paris Adventure', 'Paris Day Trip'];
    const handleSuggestionClick = jest.fn();
    render(<SearchBar suggestions={suggestions} onSuggestionClick={handleSuggestionClick} />);

    const searchInput = screen.getByPlaceholderText('Search tours...');
    fireEvent.focus(searchInput);

    const suggestion = screen.getByText('Paris City Tour');
    fireEvent.click(suggestion);

    expect(handleSuggestionClick).toHaveBeenCalledTimes(1);
    expect(handleSuggestionClick).toHaveBeenCalledWith('Paris City Tour');
  });

  test('displays search history when provided', () => {
    const searchHistory = ['Paris', 'Rome', 'London'];
    render(<SearchBar searchHistory={searchHistory} />);

    const searchInput = screen.getByPlaceholderText('Search tours...');
    fireEvent.focus(searchInput);

    expect(screen.getByText('Recent Searches')).toBeInTheDocument();
    expect(screen.getByText('Paris')).toBeInTheDocument();
    expect(screen.getByText('Rome')).toBeInTheDocument();
    expect(screen.getByText('London')).toBeInTheDocument();
  });

  test('calls onHistoryItemClick when a history item is clicked', () => {
    const searchHistory = ['Paris', 'Rome', 'London'];
    const handleHistoryItemClick = jest.fn();
    render(<SearchBar searchHistory={searchHistory} onHistoryItemClick={handleHistoryItemClick} />);

    const searchInput = screen.getByPlaceholderText('Search tours...');
    fireEvent.focus(searchInput);

    const historyItem = screen.getByText('Paris');
    fireEvent.click(historyItem);

    expect(handleHistoryItemClick).toHaveBeenCalledTimes(1);
    expect(handleHistoryItemClick).toHaveBeenCalledWith('Paris');
  });

  test('applies custom className', () => {
    const { container } = render(<SearchBar className="custom-search-bar" />);
    const searchBar = container.querySelector('.search-bar');
    expect(searchBar).toHaveClass('custom-search-bar');
  });

  test('applies size variant correctly', () => {
    const { container } = render(<SearchBar size="large" />);
    const searchBar = container.querySelector('.search-bar');
    expect(searchBar).toHaveClass('search-bar-large');
  });

  test('displays clear button when search term is provided', () => {
    render(<SearchBar defaultValue="Paris" />);

    expect(screen.getByRole('button', { name: 'Clear search' })).toBeInTheDocument();
  });

  test('calls onClear when clear button is clicked', () => {
    const handleClear = jest.fn();
    render(<SearchBar defaultValue="Paris" onClear={handleClear} />);

    const clearButton = screen.getByRole('button', { name: 'Clear search' });
    fireEvent.click(clearButton);

    expect(handleClear).toHaveBeenCalledTimes(1);
  });

  test('displays loading state when isLoading is true', () => {
    render(<SearchBar isLoading={true} />);

    expect(screen.getByTestId('search-loading')).toBeInTheDocument();
  });

  test('applies placeholder text correctly', () => {
    render(<SearchBar placeholder="Search destinations..." />);

    expect(screen.getByPlaceholderText('Search destinations...')).toBeInTheDocument();
  });

  test('displays search filters when showFilters is true', () => {
    render(<SearchBar showFilters={true} />);

    expect(screen.getByText('Filters')).toBeInTheDocument();
  });

  test('opens filter panel when filters button is clicked', () => {
    render(<SearchBar showFilters={true} />);

    const filtersButton = screen.getByText('Filters');
    fireEvent.click(filtersButton);

    expect(screen.getByText('Price Range')).toBeInTheDocument();
    expect(screen.getByText('Duration')).toBeInTheDocument();
    expect(screen.getByText('Rating')).toBeInTheDocument();
  });

  test('calls onFilterChange when filter values change', () => {
    const handleFilterChange = jest.fn();
    render(<SearchBar showFilters={true} onFilterChange={handleFilterChange} />);

    const filtersButton = screen.getByText('Filters');
    fireEvent.click(filtersButton);

    const minPriceInput = screen.getByLabelText('Min Price');
    fireEvent.change(minPriceInput, { target: { value: '50' } });

    expect(handleFilterChange).toHaveBeenCalledTimes(1);
    expect(handleFilterChange).toHaveBeenCalledWith({ minPrice: '50' });
  });
});
