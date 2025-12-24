import React, { useState, useRef } from 'react';
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import './ReservationModal.css';

const ReservationModal = ({ isOpen, onClose, restaurant, reservations, isLoading, onCreateReservation }) => {
  const [showPrompt, setShowPrompt] = useState(false);
  const [reservationName, setReservationName] = useState('');
  const [guestCount, setGuestCount] = useState(1);
  const [selectedRange, setSelectedRange] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const calendarRef = useRef(null);

  if (!isOpen) return null;

  const events = [
    ...reservations.map(res => ({
      id: res.id,
      title: 'Occupied',
      start: res.startTime,
      end: res.endTime,
      backgroundColor: '#e0e0e0',
      borderColor: '#bdbdbd',
      textColor: '#757575',
      display: 'block'
    })),
    ...(selectedRange ? [{
      id: 'selection-highlight',
      start: selectedRange.start,
      end: selectedRange.end,
      display: 'background',
      backgroundColor: 'rgba(52, 152, 219, 0.4)'
    }] : [])
  ];

  const handleSelect = (selectionInfo) => {
    setSelectedRange(selectionInfo);
    setShowPrompt(true);
  };

  const handleCancel = () => {
    setShowPrompt(false);
    setSelectedRange(null);
    if (calendarRef.current) {
      calendarRef.current.getApi().unselect();
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!reservationName || guestCount < 1) return;

    setIsSubmitting(true);
    // Remove timezone offset for backend (e.g., "2025-01-10T18:00:00-05:00" -> "2025-01-10T18:00:00")
    const startTime = selectedRange.startStr.includes('-') && selectedRange.startStr.includes('T') && selectedRange.startStr.lastIndexOf('-') > selectedRange.startStr.indexOf('T')
      ? selectedRange.startStr.substring(0, selectedRange.startStr.lastIndexOf('-'))
      : selectedRange.startStr.split('+')[0];
    
    const endTime = selectedRange.endStr.includes('-') && selectedRange.endStr.includes('T') && selectedRange.endStr.lastIndexOf('-') > selectedRange.endStr.indexOf('T')
      ? selectedRange.endStr.substring(0, selectedRange.endStr.lastIndexOf('-'))
      : selectedRange.endStr.split('+')[0];

    const result = await onCreateReservation({
      restaurantId: restaurant.id,
      reservationName,
      guestCount,
      startTime,
      endTime
    });

    setIsSubmitting(false);
    if (result.success) {
      setShowPrompt(false);
      setReservationName('');
      setGuestCount(1);
      setSelectedRange(null);
      if (calendarRef.current) {
        calendarRef.current.getApi().unselect();
      }
    } else {
      alert(`Error: ${result.error}`);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Reservations for {restaurant?.name}</h2>
          <button className="close-button" onClick={onClose}>&times;</button>
        </div>
        <div className="modal-body">
          {isLoading ? (
            <div className="loading-container">
              <div className="spinner"></div>
              <p>Loading reservations...</p>
            </div>
          ) : (
            <>
              {showPrompt && (
                <div className="reservation-prompt">
                  <h3>New Reservation</h3>
                  <p>
                    {new Date(selectedRange.start).toLocaleString()} - {new Date(selectedRange.end).toLocaleTimeString()}
                  </p>
                  <form onSubmit={handleSubmit}>
                    <div className="form-group">
                      <label>Name:</label>
                      <input
                        type="text"
                        value={reservationName}
                        onChange={(e) => setReservationName(e.target.value)}
                        required
                        placeholder="Your Name"
                      />
                    </div>
                    <div className="form-group">
                      <label>Guests:</label>
                      <input
                        type="number"
                        min="1"
                        value={guestCount}
                        onChange={(e) => setGuestCount(parseInt(e.target.value))}
                        required
                      />
                    </div>
                    <div className="prompt-actions">
                      <button type="submit" disabled={isSubmitting}>
                        {isSubmitting ? 'Reserving...' : 'Confirm'}
                      </button>
                      <button type="button" onClick={handleCancel}>Cancel</button>
                    </div>
                  </form>
                </div>
              )}
              <div className="calendar-container">
                <FullCalendar
                  ref={calendarRef}
                  plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
                  initialView="timeGridWeek"
                  headerToolbar={{
                    left: 'prev,next today',
                    center: 'title',
                    right: 'dayGridMonth,timeGridWeek,timeGridDay'
                  }}
                  events={events}
                  height="auto"
                  allDaySlot={false}
                  selectable={true}
                  selectMirror={true}
                  unselectAuto={false}
                  select={handleSelect}
                  slotMinTime="08:00:00"
                  slotMaxTime="22:00:00"
                />
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default ReservationModal;
