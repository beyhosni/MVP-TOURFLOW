import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Container,
  Typography,
  Box,
  Button,
  Card,
  CardContent,
  CircularProgress,
  Grid,
  TextField,
  Alert,
  Stepper,
  Step,
  StepLabel,
  Paper,
} from '@mui/material';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker';
import dayjs, { Dayjs } from 'dayjs';
import 'dayjs/locale/fr';
import { toursAPI, availabilityAPI, bookingsAPI, paymentsAPI } from '../services/api';
import { loadStripe } from '@stripe/stripe-js';
import { Elements } from '@stripe/react-stripe-js';
import CheckoutForm from '../components/CheckoutForm';

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

interface BookingData {
  tourId: string;
  startDate: Dayjs | null;
  participants: number;
  customerName: string;
  customerEmail: string;
  customerPhone: string;
}

const steps = ['Sélectionner une date', 'Informations personnelles', 'Paiement'];

const Booking: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [tour, setTour] = useState<Tour | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeStep, setActiveStep] = useState(0);
  const [availableSlots, setAvailableSlots] = useState<string[]>([]);
  const [loadingSlots, setLoadingSlots] = useState(false);
  const [booking, setBooking] = useState<BookingData>({
    tourId: id || '',
    startDate: null,
    participants: 1,
    customerName: '',
    customerEmail: '',
    customerPhone: '',
  });
  const [bookingId, setBookingId] = useState<string | null>(null);
  const [stripePromise, setStripePromise] = useState<any>(null);

  useEffect(() => {
    if (id) {
      fetchTourDetails(id);
      fetchStripePublishableKey();
    }
  }, [id]);

  useEffect(() => {
    if (booking.startDate) {
      fetchAvailableSlots();
    }
  }, [booking.startDate]);

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

  const fetchStripePublishableKey = async () => {
    try {
      const response = await paymentsAPI.getStripePublishableKey();
      const { publishableKey } = response.data;
      setStripePromise(loadStripe(publishableKey));
    } catch (error) {
      console.error('Erreur lors de la récupération de la clé Stripe:', error);
    }
  };

  const fetchAvailableSlots = async () => {
    if (!booking.startDate) return;

    try {
      setLoadingSlots(true);
      const startDate = booking.startDate.toISOString();
      const endDate = booking.startDate.add(1, 'day').toISOString();

      const response = await availabilityAPI.getAvailableSlots(id!, startDate, endDate);
      setAvailableSlots(response.data);
    } catch (error) {
      console.error('Erreur lors de la récupération des créneaux disponibles:', error);
      setAvailableSlots([]);
    } finally {
      setLoadingSlots(false);
    }
  };

  const handleNext = () => {
    if (activeStep === 0) {
      // Validation de l'étape 1: Date et participants
      if (!booking.startDate) {
        setError('Veuillez sélectionner une date');
        return;
      }
      if (booking.participants < 1) {
        setError('Veuillez spécifier au moins un participant');
        return;
      }
    } else if (activeStep === 1) {
      // Validation de l'étape 2: Informations personnelles
      if (!booking.customerName || !booking.customerEmail || !booking.customerPhone) {
        setError('Veuillez remplir tous les champs');
        return;
      }
      // Créer la réservation
      createBooking();
      return;
    }

    setError(null);
    setActiveStep((prevActiveStep) => prevActiveStep + 1);
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  const createBooking = async () => {
    try {
      const bookingData = {
        tourId: booking.tourId,
        startDate: booking.startDate!.toISOString(),
        participants: booking.participants,
        customerName: booking.customerName,
        customerEmail: booking.customerEmail,
        customerPhone: booking.customerPhone,
      };

      const response = await bookingsAPI.create(bookingData);
      setBookingId(response.data.id);
      setActiveStep((prevActiveStep) => prevActiveStep + 1);
    } catch (error: any) {
      setError(error.response?.data?.error || 'Erreur lors de la création de la réservation');
    }
  };

  const handleDateChange = (date: Dayjs | null) => {
    setBooking({ ...booking, startDate: date });
  };

  const handleParticipantsChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = parseInt(event.target.value);
    if (!isNaN(value) && value > 0) {
      setBooking({ ...booking, participants: value });
    }
  };

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
    setBooking({ ...booking, [name]: value });
  };

  const getStepContent = (step: number) => {
    switch (step) {
      case 0:
        return (
          <Box>
            <Typography variant="h6" gutterBottom>
              Sélectionnez une date pour votre tour
            </Typography>

            {tour && (
              <Box sx={{ mb: 3 }}>
                <Typography variant="body1" gutterBottom>
                  <strong>{tour.title}</strong>
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Durée: {tour.durationMinutes} minutes | 
                  Prix: {tour.price} € par personne | 
                  Lieu: {tour.location}
                </Typography>
              </Box>
            )}

            <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="fr">
              <DateTimePicker
                label="Date et heure"
                value={booking.startDate}
                onChange={handleDateChange}
                renderInput={(params) => <TextField {...params} fullWidth />}
                minDateTime={dayjs()}
                shouldDisableTime={(timeValue, clockType) => {
                  if (clockType === 'hours' || clockType === 'minutes') {
                    // Ici, on pourrait implémenter une logique pour désactiver les heures non disponibles
                    // Pour simplifier, nous allons juste vérifier si la date est dans le futur
                    return dayjs(timeValue).isBefore(dayjs());
                  }
                  return false;
                }}
              />
            </LocalizationProvider>

            <TextField
              fullWidth
              label="Nombre de participants"
              type="number"
              inputProps={{ min: 1, max: tour?.maxCapacity || 1 }}
              value={booking.participants}
              onChange={handleParticipantsChange}
              sx={{ mt: 2 }}
            />

            {tour && (
              <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                Capacité maximale: {tour.maxCapacity} personnes
              </Typography>
            )}
          </Box>
        );
      case 1:
        return (
          <Box>
            <Typography variant="h6" gutterBottom>
              Vos informations
            </Typography>

            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Nom complet"
                  name="customerName"
                  value={booking.customerName}
                  onChange={handleInputChange}
                  required
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Adresse email"
                  name="customerEmail"
                  type="email"
                  value={booking.customerEmail}
                  onChange={handleInputChange}
                  required
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Numéro de téléphone"
                  name="customerPhone"
                  value={booking.customerPhone}
                  onChange={handleInputChange}
                  required
                />
              </Grid>
            </Grid>

            {tour && (
              <Box sx={{ mt: 3 }}>
                <Typography variant="h6" gutterBottom>
                  Récapitulatif de la réservation
                </Typography>
                <Paper variant="outlined" sx={{ p: 2 }}>
                  <Typography variant="body1" gutterBottom>
                    <strong>Tour:</strong> {tour.title}
                  </Typography>
                  <Typography variant="body1" gutterBottom>
                    <strong>Date:</strong> {booking.startDate?.format('DD/MM/YYYY HH:mm')}
                  </Typography>
                  <Typography variant="body1" gutterBottom>
                    <strong>Participants:</strong> {booking.participants}
                  </Typography>
                  <Typography variant="body1" gutterBottom>
                    <strong>Prix total:</strong> {tour.price * booking.participants} €
                  </Typography>
                </Paper>
              </Box>
            )}
          </Box>
        );
      case 2:
        return (
          <Box>
            <Typography variant="h6" gutterBottom>
              Paiement sécurisé
            </Typography>

            {bookingId && stripePromise ? (
              <Elements stripe={stripePromise}>
                <CheckoutForm bookingId={bookingId} />
              </Elements>
            ) : (
              <Box display="flex" justifyContent="center" alignItems="center" height="200px">
                <CircularProgress />
              </Box>
            )}
          </Box>
        );
      default:
        return 'Étape inconnue';
    }
  };

  if (loading) {
    return (
      <Container maxWidth="md" sx={{ py: 4 }}>
        <Box display="flex" justifyContent="center" alignItems="center" height="400px">
          <CircularProgress />
        </Box>
      </Container>
    );
  }

  if (error || !tour) {
    return (
      <Container maxWidth="md" sx={{ py: 4 }}>
        <Box textAlign="center">
          <Typography variant="h6" color="error" gutterBottom>
            {error || 'Tour non trouvé'}
          </Typography>
          <Button variant="contained" onClick={() => navigate('/tours')}>
            Retour aux tours
          </Button>
        </Box>
      </Container>
    );
  }

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Réserver votre tour
      </Typography>

      <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
        {steps.map((label) => (
          <Step key={label}>
            <StepLabel>{label}</StepLabel>
          </Step>
        ))}
      </Stepper>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <Paper sx={{ p: 3, mb: 3 }}>
        {getStepContent(activeStep)}
      </Paper>

      <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
        <Button
          color="inherit"
          disabled={activeStep === 0}
          onClick={handleBack}
          sx={{ mr: 1 }}
        >
          Précédent
        </Button>
        <Button
          variant="contained"
          onClick={handleNext}
          disabled={activeStep === 2}
        >
          {activeStep === steps.length - 2 ? 'Finaliser la réservation' : 'Suivant'}
        </Button>
      </Box>
    </Container>
  );
};

export default Booking;
