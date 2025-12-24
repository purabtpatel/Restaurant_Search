import RestaurantCard from './RestaurantCard';
import './RestaurantList.css';

const RestaurantList = ({ restaurants, isLoading, error, onRestaurantClick }) => {
  if (isLoading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <p>Searching for restaurants...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="error-container">
        <p className="error-message">⚠️ {error}</p>
      </div>
    );
  }

  if (!restaurants || restaurants.length === 0) {
    return (
      <div className="empty-container">
        <p className="empty-message">No restaurants found. Try adjusting your filters.</p>
      </div>
    );
  }

  return (
    <div className="restaurant-list-container">
      <div className="results-header">
        <h2>Found {restaurants.length} restaurant{restaurants.length !== 1 ? 's' : ''}</h2>
      </div>
      <div className="restaurant-grid">
        {restaurants.map((restaurant, index) => (
          <RestaurantCard
            key={restaurant.id || index}
            restaurant={restaurant}
            onClick={() => onRestaurantClick(restaurant)}
          />
        ))}
      </div>
    </div>
  );
};

export default RestaurantList;
