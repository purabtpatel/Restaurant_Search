import { useState, useRef, useEffect } from 'react'
import './ChatBox.css'

const ChatBox = () => {
  const [messages, setMessages] = useState([])
  const [input, setInput] = useState('')
  const [context, setContext] = useState(null)
  const [isLoading, setIsLoading] = useState(false)
  const [isCollapsed, setIsCollapsed] = useState(true)
  const messagesEndRef = useRef(null)

  const API_BASE_URL = 'http://localhost:8080'

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }

  useEffect(() => {
    scrollToBottom()
  }, [messages])

  const sendMessage = async (e) => {
    e.preventDefault()
    if (!input.trim() || isLoading) return

    const userMessage = { text: input, sender: 'user' }
    setMessages((prev) => [...prev, userMessage])
    const currentInput = input
    setInput('')
    setIsLoading(true)

    try {
      const response = await fetch(`${API_BASE_URL}/agents/chat`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          message: currentInput,
          context: context,
        }),
      })

      if (!response.ok) {
        throw new Error(`Server error: ${response.status}`)
      }

      const data = await response.json()
      const assistantMessage = { text: data.reply, sender: 'assistant' }
      
      setMessages((prev) => [...prev, assistantMessage])
      setContext(data.context)
    } catch (err) {
      console.error('Failed to send message:', err)
      setMessages((prev) => [
        ...prev,
        { text: 'Sorry, I encountered an error. Please try again.', sender: 'assistant', isError: true },
      ])
    } finally {
      setIsLoading(false)
    }
  }

  const parseReply = (text) => {
    const lines = text.split('\n');
    const elements = [];
    let currentText = '';

    const restaurantRegex = /^- (.*) \(Rating: (\d+), Distance: (\d+), Price: (\d+), Cuisine: (.*)\)$/;

    lines.forEach((line, index) => {
      const match = line.match(restaurantRegex);
      if (match) {
        if (currentText) {
          elements.push({ type: 'text', content: currentText.trim() });
          currentText = '';
        }
        elements.push({
          type: 'restaurant',
          content: {
            name: match[1],
            rating: match[2],
            distance: match[3],
            price: match[4],
            cuisine: match[5]
          }
        });
      } else {
        currentText += line + '\n';
      }
    });

    if (currentText) {
      elements.push({ type: 'text', content: currentText.trim() });
    }

    return elements;
  };

  const RestaurantCard = ({ restaurant }) => (
    <div className="restaurant-card-mini">
      <div className="restaurant-card-mini-header">
        <span className="restaurant-card-mini-name">{restaurant.name}</span>
        <span className="restaurant-card-mini-rating">⭐ {restaurant.rating}</span>
      </div>
      <div className="restaurant-card-mini-details">
        <span>📍 {restaurant.distance}m</span>
        <span>💰 ${restaurant.price}</span>
        <span>🍴 {restaurant.cuisine}</span>
      </div>
    </div>
  );

  return (
    <div className={`chat-box ${isCollapsed ? 'collapsed' : ''}`}>
      <div className="chat-header" onClick={() => setIsCollapsed(!isCollapsed)}>
        <h3>🍽️ Restaurant Assistant</h3>
        <button className="toggle-button">
          {isCollapsed ? '▲' : '▼'}
        </button>
      </div>
      {!isCollapsed && (
        <>
          <div className="chat-messages">
            {messages.length === 0 && (
              <div className="chat-placeholder">
                Ask me anything about restaurants or reservations!
              </div>
            )}
            {messages.map((msg, index) => (
              <div key={index} className={`chat-message ${msg.sender}`}>
                <div className="message-bubble">
                  {msg.sender === 'assistant' ? (
                    parseReply(msg.text).map((el, i) => (
                      el.type === 'restaurant' ? (
                        <RestaurantCard key={i} restaurant={el.content} />
                      ) : (
                        <p key={i}>{el.content}</p>
                      )
                    ))
                  ) : (
                    msg.text
                  )}
                </div>
              </div>
            ))}
            {isLoading && (
              <div className="chat-message assistant">
                <div className="message-bubble loading">...</div>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>
          <form className="chat-input" onSubmit={sendMessage}>
            <input
              type="text"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder="Type your message..."
              disabled={isLoading}
            />
            <button type="submit" disabled={isLoading || !input.trim()}>
              Send
            </button>
          </form>
        </>
      )}
    </div>
  )
}

export default ChatBox
