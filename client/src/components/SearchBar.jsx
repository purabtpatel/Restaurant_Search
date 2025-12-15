import { useState } from 'react';
import './SearchBar.css';

const SearchBar = ({ onSearch, isLoading }) => {
  const [filters, setFilters] = useState({
    name: '',
    rating: '',
    distance: '',
    price: '',
    cuisine: '',
    limit: ''
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFilters(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const cleanedFilters = Object.fromEntries(
      Object.entries(filters).filter(([, v]) => v !== '')
    );
    onSearch(cleanedFilters);
  };

  const handleReset = () => {
    setFilters({
      name: '',
      rating: '',
      distance: '',
      price: '',
      cuisine: '',
      limit: ''
    });
    onSearch({});
  };

  return (
    <div className="search-bar">
      <form onSubmit={handleSubmit}>
        <div className="search-grid">
          <div className="input-group">
            <label htmlFor="name">Restaurant Name</label>
            <input
              id="name"
              name="name"
              type="text"
              placeholder="Search by name..."
              value={filters.name}
              onChange={handleChange}
            />
          </div>

          <div className="input-group">
            <label htmlFor="cuisine">Cuisine</label>
            <input
              id="cuisine"
              name="cuisine"
              type="text"
              placeholder="e.g., Chinese, Spanish..."
              value={filters.cuisine}
              onChange={handleChange}
            />
          </div>

          <div className="input-group">
            <label htmlFor="rating">Min Rating</label>
            <input
              id="rating"
              name="rating"
              type="number"
              min="1"
              max="5"
              placeholder="1-5"
              value={filters.rating}
              onChange={handleChange}
            />
          </div>

          <div className="input-group">
            <label htmlFor="distance">Max Distance (km)</label>
            <input
              id="distance"
              name="distance"
              type="number"
              min="1"
              placeholder="Distance..."
              value={filters.distance}
              onChange={handleChange}
            />
          </div>

          <div className="input-group">
            <label htmlFor="price">Max Price ($)</label>
            <input
              id="price"
              name="price"
              type="number"
              min="1"
              placeholder="Price..."
              value={filters.price}
              onChange={handleChange}
            />
          </div>

          <div className="input-group">
            <label htmlFor="limit">Results Limit</label>
            <input
              id="limit"
              name="limit"
              type="number"
              min="1"
              max="100"
              placeholder="Max results..."
              value={filters.limit}
              onChange={handleChange}
            />
          </div>
        </div>

        <div className="button-group">
          <button type="submit" className="btn-primary" disabled={isLoading}>
            {isLoading ? 'Searching...' : 'Search'}
          </button>
          <button type="button" className="btn-secondary" onClick={handleReset} disabled={isLoading}>
            Reset
          </button>
        </div>
      </form>
    </div>
  );
};

export default SearchBar;
