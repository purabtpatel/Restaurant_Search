import './RestaurantCard.css';

const RestaurantCard = ({ restaurant, onClick }) => {
  return (
    <div className="restaurant-card" onClick={onClick} style={{ cursor: 'pointer' }}>
      <div className="card-header">
        <h3 className="restaurant-name">{restaurant.name}</h3>
        <div className="rating">
          <span className="star">★</span>
          <span className="rating-value">{restaurant.rating}</span>
        </div>
      </div>

      <div className="card-body">
        <div className="info-row">
          <span className="info-label">Cuisine</span>
          <span className="info-value cuisine-tag">{restaurant.cuisine}</span>
        </div>

        <div className="info-row">
          <span className="info-label">Distance</span>
          <span className="info-value">{restaurant.distance} km</span>
        </div>

        <div className="info-row">
          <span className="info-label">Price</span>
          <span className="info-value price">${restaurant.price}</span>
        </div>
      </div>
    </div>
  );
};

export default RestaurantCard;
