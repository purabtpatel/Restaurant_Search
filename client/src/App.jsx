import { useState } from 'react'
import SearchBar from './components/SearchBar'
import RestaurantList from './components/RestaurantList'
import './App.css'

function App() {
  const [restaurants, setRestaurants] = useState([])
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState(null)

  const API_BASE_URL = 'http://localhost:8080/search'

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

      const url = `${API_BASE_URL}/advanced${queryParams.toString() ? `?${queryParams.toString()}` : ''}`
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
        />
      </main>
    </div>
  )
}

export default App
