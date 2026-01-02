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
                <div className="message-bubble">{msg.text}</div>
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
