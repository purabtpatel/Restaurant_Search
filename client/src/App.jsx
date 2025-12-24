import { useState } from 'react'
import SearchBar from './components/SearchBar'
import RestaurantList from './components/RestaurantList'
import ReservationModal from './components/ReservationModal'
import './App.css'

function App() {
  const [restaurants, setRestaurants] = useState([])
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState(null)
  const [selectedRestaurant, setSelectedRestaurant] = useState(null)
  const [reservations, setReservations] = useState([])
  const [isReservationsLoading, setIsReservationsLoading] = useState(false)
  const [isModalOpen, setIsModalOpen] = useState(false)

  const API_BASE_URL = 'http://localhost:8080'

  const fetchRestaurants = async (filters = {}) => {
    setIsLoading(true)
    setError(null)

    try {
      const queryParams = new URLSearchParams()
      Object.entries(filters).forEach(([key, value]) => {
        if (value !== '' && value !== null && value !== undefined) {
          queryParams.append(key, value)
        }
      })

      const url = `${API_BASE_URL}/search/advanced${queryParams.toString() ? `?${queryParams.toString()}` : ''}`
      const response = await fetch(url)

      if (!response.ok) {
        throw new Error(`Server error: ${response.status}`)
      }

      const data = await response.json()
      setRestaurants(data)
    } catch (err) {
      setError(err.message || 'Failed to fetch restaurants. Please try again.')
      setRestaurants([])
    } finally {
      setIsLoading(false)
    }
  }

  const fetchReservations = async (restaurantId) => {
    setIsReservationsLoading(true)
    try {
      const response = await fetch(`${API_BASE_URL}/reservations/restaurant/${restaurantId}`)
      if (!response.ok) {
        throw new Error(`Server error: ${response.status}`)
      }
      const data = await response.json()
      setReservations(data)
    } catch (err) {
      console.error('Failed to fetch reservations:', err)
      setReservations([])
    } finally {
      setIsReservationsLoading(false)
    }
  }

  const handleRestaurantClick = (restaurant) => {
    setSelectedRestaurant(restaurant)
    setIsModalOpen(true)
    fetchReservations(restaurant.id)
  }

  const handleCloseModal = () => {
    setIsModalOpen(false)
    setSelectedRestaurant(null)
    setReservations([])
  }

  const handleSearch = (filters) => {
    fetchRestaurants(filters)
  }

  return (
    <div className="app">
      <header className="app-header">
        <h1 className="app-title">🍽️ Restaurant Search</h1>
        <p className="app-subtitle">Find your perfect dining experience</p>
      </header>

      <main className="app-main">
        <SearchBar onSearch={handleSearch} isLoading={isLoading} />
        <RestaurantList
          restaurants={restaurants}
          isLoading={isLoading}
          error={error}
          onRestaurantClick={handleRestaurantClick}
        />
      </main>

      <ReservationModal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        restaurant={selectedRestaurant}
        reservations={reservations}
        isLoading={isReservationsLoading}
      />
    </div>
  )
}

export default App
