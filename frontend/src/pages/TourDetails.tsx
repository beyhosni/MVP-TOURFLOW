import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Container,
  Typography,
  Box,
  Button,
  Card,
  CardContent,
  CardMedia,
  CircularProgress,
  Grid,
  Divider,
  Chip,
} from '@mui/material';
import { toursAPI } from '../services/api';

interface Tour {
  id: string;
  title: string;
  description: string;
  durationMinutes: number;
  location: string;
  maxCapacity: number;
  price: number;
  language: string;
  photoUrls?: string;
  active: boolean;
}

const TourDetails: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [tour, setTour] = useState<Tour | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (id) {
      fetchTourDetails(id);
    }
  }, [id]);

  const fetchTourDetails = async (tourId: string) => {
    try {
      setLoading(true);
      const response = await toursAPI.getById(tourId);
      setTour(response.data);
      setError(null);
    } catch (error: any) {
      setError(error.response?.data?.error || 'Impossible de charger les détails du tour');
    } finally {
      setLoading(false);
    }
  };

  const handleBookingClick = () => {
    if (tour) {
      navigate(`/booking/${tour.id}`);
    }
  };

  const handleBackClick = () => {
    navigate('/tours');
  };

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Box display="flex" justifyContent="center" alignItems="center" height="400px">
          <CircularProgress />
        </Box>
      </Container>
    );
  }

  if (error || !tour) {
    return (
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Box textAlign="center">
          <Typography variant="h6" color="error" gutterBottom>
            {error || 'Tour non trouvé'}
          </Typography>
          <Button variant="contained" onClick={handleBackClick}>
            Retour aux tours
          </Button>
        </Box>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Button variant="outlined" onClick={handleBackClick} sx={{ mb: 3 }}>
        ← Retour aux tours
      </Button>

      <Grid container spacing={4}>
        <Grid item xs={12} md={8}>
          <Card>
            {tour.photoUrls ? (
              <CardMedia
                component="div"
                sx={{
                  height: 400,
                  backgroundImage: `url(${tour.photoUrls})`,
                  backgroundSize: 'cover',
                  backgroundPosition: 'center',
                }}
              />
            ) : (
              <CardMedia
                component="div"
                sx={{
                  height: 400,
                  backgroundColor: '#f0f0f0',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                }}
              >
                <Typography variant="body2" color="text.secondary">
                  Image non disponible
                </Typography>
              </CardMedia>
            )}
            <CardContent>
              <Typography variant="h4" component="h1" gutterBottom>
                {tour.title}
              </Typography>

              <Box sx={{ mb: 2 }}>
                <Chip label={tour.language} color="primary" size="small" sx={{ mr: 1 }} />
                <Chip label={`${tour.durationMinutes} minutes`} variant="outlined" size="small" />
              </Box>

              <Typography variant="body1" paragraph>
                {tour.description}
              </Typography>

              <Divider sx={{ my: 2 }} />

              <Typography variant="h6" gutterBottom>
                Informations pratiques
              </Typography>

              <Grid container spacing={2}>
                <Grid item xs={12} sm={6}>
                  <Typography variant="body2" color="text.secondary">
                    <strong>Lieu:</strong> {tour.location}
                  </Typography>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Typography variant="body2" color="text.secondary">
                    <strong>Capacité maximale:</strong> {tour.maxCapacity} personnes
                  </Typography>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Typography variant="body2" color="text.secondary">
                    <strong>Durée:</strong> {tour.durationMinutes} minutes
                  </Typography>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Typography variant="body2" color="text.secondary">
                    <strong>Langue:</strong> {tour.language}
                  </Typography>
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card sx={{ position: 'sticky', top: 20 }}>
            <CardContent>
              <Typography variant="h5" gutterBottom>
                Prix
              </Typography>
              <Typography variant="h4" color="primary" gutterBottom>
                {tour.price} €
              </Typography>
              <Typography variant="body2" color="text.secondary" gutterBottom>
                Par personne
              </Typography>

              <Divider sx={{ my: 2 }} />

              <Button
                variant="contained"
                size="large"
                fullWidth
                onClick={handleBookingClick}
                sx={{ mb: 2 }}
              >
                Réserver maintenant
              </Button>

              <Typography variant="body2" color="text.secondary" align="center">
                Réservation sécurisée via Stripe
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default TourDetails;
