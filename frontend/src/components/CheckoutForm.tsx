import React, { useState } from 'react';
import {
  Box,
  Button,
  CircularProgress,
  Typography,
  Alert,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import {
  PaymentElement,
  useStripe,
  useElements,
} from '@stripe/react-stripe-js';
import { paymentsAPI } from '../services/api';

interface CheckoutFormProps {
  bookingId: string;
}

const CheckoutForm: React.FC<CheckoutFormProps> = ({ bookingId }) => {
  const stripe = useStripe();
  const elements = useElements();
  const navigate = useNavigate();

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    if (!stripe || !elements) {
      return;
    }

    setLoading(true);
    setError(null);

    try {
      // Créer une session de paiement Stripe
      const successUrl = `${window.location.origin}/booking/success?session_id={CHECKOUT_SESSION_ID}`;
      const cancelUrl = `${window.location.origin}/booking/cancel`;

      const response = await paymentsAPI.createCheckoutSession(
        bookingId,
        successUrl,
        cancelUrl
      );

      const { checkoutUrl } = response.data;

      // Rediriger vers la page de paiement Stripe
      window.location.href = checkoutUrl;
    } catch (error: any) {
      setError(error.response?.data?.error || 'Une erreur est survenue lors du traitement du paiement');
      setLoading(false);
    }
  };

  if (success) {
    return (
      <Box textAlign="center" py={4}>
        <Alert severity="success" sx={{ mb: 2 }}>
          Votre réservation a été confirmée avec succès !
        </Alert>
        <Typography variant="body1" paragraph>
          Un email de confirmation vous a été envoyé avec tous les détails de votre réservation.
        </Typography>
        <Button
          variant="contained"
          onClick={() => navigate('/')}
        >
          Retour à l'accueil
        </Button>
      </Box>
    );
  }

  return (
    <Box component="form" onSubmit={handleSubmit}>
      <Typography variant="body2" color="text.secondary" paragraph>
        Veuillez remplir les informations de paiement ci-dessous pour finaliser votre réservation.
      </Typography>

      <PaymentElement />

      {error && (
        <Alert severity="error" sx={{ mt: 2 }}>
          {error}
        </Alert>
      )}

      <Button
        type="submit"
        variant="contained"
        fullWidth
        size="large"
        disabled={!stripe || loading}
        startIcon={loading ? <CircularProgress size={20} /> : null}
        sx={{ mt: 3 }}
      >
        {loading ? 'Traitement en cours...' : 'Payer maintenant'}
      </Button>

      <Typography variant="caption" color="text.secondary" sx={{ mt: 2, display: 'block' }}>
        Vos informations de paiement sont sécurisées et cryptées. TourFlow ne stocke jamais vos données bancaires.
      </Typography>
    </Box>
  );
};

export default CheckoutForm;
