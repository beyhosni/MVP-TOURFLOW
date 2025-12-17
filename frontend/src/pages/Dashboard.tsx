import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Box,
  Grid,
  Card,
  CardContent,
  CircularProgress,
  Button,
  Tabs,
  Tab,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  Fab,
} from '@mui/material';
import { Add as AddIcon } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { toursAPI, bookingsAPI } from '../services/api';

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function TabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`simple-tabpanel-${index}`}
      aria-labelledby={`simple-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
    </div>
  );
}

interface Tour {
  id: string;
  title: string;
  description: string;
  durationMinutes: number;
  location: string;
  maxCapacity: number;
  price: number;
  language: string;
  active: boolean;
}

interface Booking {
  id: string;
  startDate: string;
  endDate: string;
  participants: number;
  totalPrice: number;
  customerName: string;
  customerEmail: string;
  status: string;
  tour: Tour;
}

const Dashboard: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();

  const [tours, setTours] = useState<Tour[]>([]);
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [loading, setLoading] = useState(true);
  const [tabValue, setTabValue] = useState(0);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);

      // Récupérer les tours du guide
      const toursResponse = await toursAPI.getMyTours();
      setTours(toursResponse.data);

      // Récupérer les réservations pour tous les tours du guide
      const bookingsPromises = toursResponse.data.map(async (tour: Tour) => {
        const bookingResponse = await bookingsAPI.getByTour(tour.id);
        return bookingResponse.data;
      });

      const bookingsArrays = await Promise.all(bookingsPromises);
      const allBookings = bookingsArrays.flat();
      setBookings(allBookings);
    } catch (error) {
      console.error('Erreur lors de la récupération des données du tableau de bord:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
  };

  const handleCreateTour = () => {
    navigate('/tours/create');
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'CONFIRMED':
        return 'success';
      case 'PENDING':
        return 'warning';
      case 'CANCELLED':
        return 'error';
      case 'EXPIRED':
        return 'default';
      default:
        return 'default';
    }
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleString('fr-FR');
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

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Tableau de bord
      </Typography>

      <Typography variant="h6" gutterBottom>
        Bienvenue, {user?.firstName} {user?.lastName}!
      </Typography>

      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography variant="h4" component="div">
                {tours.length}
              </Typography>
              <Typography color="text.secondary" gutterBottom>
                Tours actifs
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography variant="h4" component="div">
                {bookings.filter(b => b.status === 'CONFIRMED').length}
              </Typography>
              <Typography color="text.secondary" gutterBottom>
                Réservations confirmées
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography variant="h4" component="div">
                {bookings.filter(b => b.status === 'PENDING').length}
              </Typography>
              <Typography color="text.secondary" gutterBottom>
                Réservations en attente
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography variant="h4" component="div">
                {bookings.reduce((sum, b) => b.status === 'CONFIRMED' ? sum + b.totalPrice : sum, 0).toFixed(2)} €
              </Typography>
              <Typography color="text.secondary" gutterBottom>
                Revenus totaux
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
        <Tabs value={tabValue} onChange={handleTabChange} aria-label="dashboard tabs">
          <Tab label="Mes tours" />
          <Tab label="Réservations" />
        </Tabs>
      </Box>

      <TabPanel value={tabValue} index={0}>
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
          <Typography variant="h6">
            Mes tours
          </Typography>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={handleCreateTour}
          >
            Créer un tour
          </Button>
        </Box>

        {tours.length === 0 ? (
          <Box textAlign="center" py={4}>
            <Typography variant="h6" gutterBottom>
              Vous n'avez pas encore créé de tour
            </Typography>
            <Typography variant="body2" color="text.secondary" paragraph>
              Commencez par créer votre premier tour pour commencer à recevoir des réservations.
            </Typography>
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={handleCreateTour}
            >
              Créer un tour
            </Button>
          </Box>
        ) : (
          <Grid container spacing={3}>
            {tours.map((tour) => (
              <Grid item xs={12} sm={6} md={4} key={tour.id}>
                <Card>
                  <CardContent>
                    <Typography variant="h6" gutterBottom>
                      {tour.title}
                    </Typography>
                    <Typography variant="body2" color="text.secondary" paragraph>
                      {tour.description.length > 100 
                        ? `${tour.description.substring(0, 100)}...` 
                        : tour.description}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      <strong>Lieu:</strong> {tour.location}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      <strong>Durée:</strong> {tour.durationMinutes} min
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      <strong>Prix:</strong> {tour.price} €
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      <strong>Langue:</strong> {tour.language}
                    </Typography>
                  </CardContent>
                  <CardActions>
                    <Button size="small" onClick={() => navigate(`/tours/${tour.id}/edit`)}>
                      Modifier
                    </Button>
                    <Button size="small" onClick={() => navigate(`/calendar/${tour.id}`)}>
                      Calendrier
                    </Button>
                  </CardActions>
                </Card>
              </Grid>
            ))}
          </Grid>
        )}
      </TabPanel>

      <TabPanel value={tabValue} index={1}>
        <Typography variant="h6" gutterBottom>
          Réservations
        </Typography>

        {bookings.length === 0 ? (
          <Box textAlign="center" py={4}>
            <Typography variant="h6" gutterBottom>
              Vous n'avez aucune réservation
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Les réservations apparaîtront ici dès que les clients commenceront à réserver vos tours.
            </Typography>
          </Box>
        ) : (
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Date</TableCell>
                  <TableCell>Tour</TableCell>
                  <TableCell>Client</TableCell>
                  <TableCell>Participants</TableCell>
                  <TableCell>Prix</TableCell>
                  <TableCell>Statut</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {bookings.map((booking) => (
                  <TableRow key={booking.id}>
                    <TableCell>{formatDate(booking.startDate)}</TableCell>
                    <TableCell>{booking.tour.title}</TableCell>
                    <TableCell>{booking.customerName}</TableCell>
                    <TableCell>{booking.participants}</TableCell>
                    <TableCell>{booking.totalPrice} €</TableCell>
                    <TableCell>
                      <Chip 
                        label={booking.status} 
                        color={getStatusColor(booking.status) as any}
                        size="small"
                      />
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </TabPanel>

      <Fab
        color="primary"
        aria-label="add"
        onClick={handleCreateTour}
        sx={{
          position: 'fixed',
          bottom: 16,
          right: 16,
        }}
      >
        <AddIcon />
      </Fab>
    </Container>
  );
};

export default Dashboard;
