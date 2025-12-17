import React from 'react';
import PropTypes from 'prop-types';
import { Card, Button, Badge } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { formatPrice, formatDate } from '../../utils/formatters';

const TourCard = ({ tour, onBook }) => {
  const { id, title, description, price, duration, maxParticipants, status, startDate, imageUrl } = tour;

  const handleBookClick = (e) => {
    e.preventDefault();
    onBook(id);
  };

  return (
    <Card className="tour-card h-100">
      <Card.Img variant="top" src={imageUrl || '/images/default-tour.jpg'} alt={title} />
      <Card.Body className="d-flex flex-column">
        <div className="d-flex justify-content-between align-items-start mb-2">
          <Card.Title as="h5">{title}</Card.Title>
          <Badge bg={status === 'ACTIVE' ? 'success' : 'secondary'}>
            {status === 'ACTIVE' ? 'Disponible' : 'Indisponible'}
          </Badge>
        </div>
        <Card.Text className="flex-grow-1">{description}</Card.Text>
        <div className="tour-details mb-3">
          <div className="d-flex justify-content-between mb-2">
            <span>Prix:</span>
            <span className="fw-bold">{formatPrice(price)}</span>
          </div>
          <div className="d-flex justify-content-between mb-2">
            <span>Durée:</span>
            <span>{duration} jour(s)</span>
          </div>
          <div className="d-flex justify-content-between mb-2">
            <span>Participants max:</span>
            <span>{maxParticipants}</span>
          </div>
          <div className="d-flex justify-content-between">
            <span>Date de départ:</span>
            <span>{formatDate(startDate)}</span>
          </div>
        </div>
        <div className="d-grid gap-2 mt-auto">
          <Link to={`/tours/${id}`} className="btn btn-outline-primary">
            Voir les détails
          </Link>
          {status === 'ACTIVE' && (
            <Button variant="primary" onClick={handleBookClick}>
              Réserver
            </Button>
          )}
        </div>
      </Card.Body>
    </Card>
  );
};

TourCard.propTypes = {
  tour: PropTypes.shape({
    id: PropTypes.number.isRequired,
    title: PropTypes.string.isRequired,
    description: PropTypes.string.isRequired,
    price: PropTypes.number.isRequired,
    duration: PropTypes.number.isRequired,
    maxParticipants: PropTypes.number.isRequired,
    status: PropTypes.string.isRequired,
    startDate: PropTypes.string.isRequired,
    imageUrl: PropTypes.string,
  }).isRequired,
  onBook: PropTypes.func.isRequired,
};

export default TourCard;
