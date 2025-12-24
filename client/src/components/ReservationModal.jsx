import React from 'react';
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import './ReservationModal.css';

const ReservationModal = ({ isOpen, onClose, restaurant, reservations, isLoading }) => {
  if (!isOpen) return null;

  const events = reservations.map(res => ({
    id: res.id,
    title: 'Occupied',
    start: res.startTime,
    end: res.endTime,
    backgroundColor: '#e0e0e0',
    borderColor: '#bdbdbd',
    textColor: '#757575',
    display: 'block'
  }));

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
            <div className="calendar-container">
              <FullCalendar
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
              />
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ReservationModal;
