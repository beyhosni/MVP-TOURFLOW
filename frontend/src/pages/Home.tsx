import React from 'react';
import {
  Box,
  Container,
  Typography,
  Button,
  Grid,
  Card,
  CardContent,
  CardMedia,
  CardActions,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { TravelExplore } from '@mui/icons-material';

const Home: React.FC = () => {
  const navigate = useNavigate();

  return (
    <Container maxWidth="lg">
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          my: 4,
        }}
      >
        <Typography variant="h2" component="h1" gutterBottom>
          Bienvenue sur TourFlow
        </Typography>
        <Typography variant="h5" color="text.secondary" paragraph>
          La plateforme de réservation directe pour guides touristiques indépendants
        </Typography>
        <Box sx={{ mt: 2 }}>
          <Button
            variant="contained"
            size="large"
            startIcon={<TravelExplore />}
            onClick={() => navigate('/tours')}
            sx={{ mr: 2 }}
          >
            Découvrir les tours
          </Button>
          <Button
            variant="outlined"
            size="large"
            onClick={() => navigate('/register')}
          >
            Devenir guide
          </Button>
        </Box>
      </Box>

      <Box sx={{ my: 6 }}>
        <Typography variant="h4" component="h2" gutterBottom align="center">
          Comment ça fonctionne ?
        </Typography>
        <Grid container spacing={4} sx={{ mt: 2 }}>
          <Grid item xs={12} md={4}>
            <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
              <CardContent sx={{ flexGrow: 1 }}>
                <Typography gutterBottom variant="h5" component="h2">
                  1. Choisissez votre tour
                </Typography>
                <Typography>
                  Parcourez notre sélection de tours guidés par des experts locaux.
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} md={4}>
            <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
              <CardContent sx={{ flexGrow: 1 }}>
                <Typography gutterBottom variant="h5" component="h2">
                  2. Réservez en ligne
                </Typography>
                <Typography>
                  Sélectionnez une date, le nombre de participants et procédez au paiement sécurisé.
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} md={4}>
            <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
              <CardContent sx={{ flexGrow: 1 }}>
                <Typography gutterBottom variant="h5" component="h2">
                  3. Profitez de votre expérience
                </Typography>
                <Typography>
                  Recevez une confirmation par email et profitez de votre tour sans souci.
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      </Box>

      <Box sx={{ my:6, textAlign: 'center' }}>
        <Typography variant="h4" component="h2" gutterBottom>
          Pourquoi choisir TourFlow ?
        </Typography>
        <Grid container spacing={4} sx={{ mt: 2 }}>
          <Grid item xs={12} md={4}>
            <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
              <CardContent sx={{ flexGrow: 1 }}>
                <Typography gutterBottom variant="h5" component="h2">
                  Pas de frais cachés
                </Typography>
                <Typography>
                  Le prix que vous voyez est le prix que vous payez. Aucune commission cachée.
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} md={4}>
            <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
              <CardContent sx={{ flexGrow: 1 }}>
                <Typography gutterBottom variant="h5" component="h2">
                  Support direct
                </Typography>
                <Typography>
                  Communiquez directement avec votre guide pour personnaliser votre expérience.
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} md={4}>
            <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
              <CardContent sx={{ flexGrow: 1 }}>
                <Typography gutterBottom variant="h5" component="h2">
                  Annulation flexible
                </Typography>
                <Typography>
                  Politiques d'annulation claires et équitables pour chaque tour.
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      </Box>
    </Container>
  );
};

export default Home;
