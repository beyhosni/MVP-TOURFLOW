import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Grid,
  Card,
  CardContent,
  CardMedia,
  CardActions,
  Button,
  Box,
  CircularProgress,
  TextField,
  InputAdornment,
  Pagination,
} from '@mui/material';
import { Search } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
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

const Tours: React.FC = () => {
  const [tours, setTours] = useState<Tour[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  const navigate = useNavigate();

  useEffect(() => {
    fetchTours();
  }, [page, searchTerm]);

  const fetchTours = async () => {
    try {
      setLoading(true);
      const response = await toursAPI.getAll();

      // Filtrer les tours si un terme de recherche est fourni
      let filteredTours = response.data;
      if (searchTerm) {
        filteredTours = response.data.filter((tour: Tour) => 
          tour.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
          tour.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
          tour.location.toLowerCase().includes(searchTerm.toLowerCase())
        );
      }

      // Pagination simple (10 tours par page)
      const itemsPerPage = 10;
      const startIndex = (page - 1) * itemsPerPage;
      const endIndex = startIndex + itemsPerPage;
      const paginatedTours = filteredTours.slice(startIndex, endIndex);

      setTours(paginatedTours);
      setTotalPages(Math.ceil(filteredTours.length / itemsPerPage));
    } catch (error) {
      console.error('Erreur lors de la récupération des tours:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(event.target.value);
    setPage(1); // Réinitialiser à la première page lors de la recherche
  };

  const handlePageChange = (event: React.ChangeEvent<unknown>, value: number) => {
    setPage(value);
  };

  const handleBookingClick = (tourId: string) => {
    navigate(`/booking/${tourId}`);
  };

  const handleDetailsClick = (tourId: string) => {
    navigate(`/tours/${tourId}`);
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Découvrez nos tours
      </Typography>

      <Box sx={{ mb: 3 }}>
        <TextField
          fullWidth
          variant="outlined"
          placeholder="Rechercher un tour..."
          value={searchTerm}
          onChange={handleSearch}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <Search />
              </InputAdornment>
            ),
          }}
        />
      </Box>

      {loading ? (
        <Box display="flex" justifyContent="center" alignItems="center" height="400px">
          <CircularProgress />
        </Box>
      ) : (
        <>
          {tours.length === 0 ? (
            <Box textAlign="center" py={4}>
              <Typography variant="h6">
                Aucun tour trouvé
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Essayez de modifier votre recherche ou revenez plus tard.
              </Typography>
            </Box>
          ) : (
            <Grid container spacing={4}>
              {tours.map((tour) => (
                <Grid item key={tour.id} xs={12} sm={6} md={4}>
                  <Card className="tour-card">
                    {tour.photoUrls ? (
                      <CardMedia
                        component="div"
                        height="140"
                        sx={{
                          backgroundImage: `url(${tour.photoUrls})`,
                          backgroundSize: 'cover',
                          backgroundPosition: 'center',
                        }}
                      />
                    ) : (
                      <CardMedia
                        component="div"
                        height="140"
                        sx={{
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
                    <CardContent className="tour-card-content">
                      <Typography gutterBottom variant="h5" component="h2">
                        {tour.title}
                      </Typography>
                      <Typography variant="body2" color="text.secondary" paragraph>
                        {tour.description.length > 100 
                          ? `${tour.description.substring(0, 100)}...` 
                          : tour.description}
                      </Typography>
                      <Box display="flex" justifyContent="space-between" mb={1}>
                        <Typography variant="body2" color="text.secondary">
                          <strong>Lieu:</strong> {tour.location}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                          <strong>Durée:</strong> {tour.durationMinutes} min
                        </Typography>
                      </Box>
                      <Box display="flex" justifyContent="space-between" mb={1}>
                        <Typography variant="body2" color="text.secondary">
                          <strong>Capacité:</strong> {tour.maxCapacity} pers.
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                          <strong>Langue:</strong> {tour.language}
                        </Typography>
                      </Box>
                      <Typography variant="h6" color="primary">
                        {tour.price} €
                      </Typography>
                    </CardContent>
                    <CardActions className="tour-card-actions">
                      <Button size="small" onClick={() => handleDetailsClick(tour.id)}>
                        Détails
                      </Button>
                      <Button 
                        size="small" 
                        variant="contained" 
                        onClick={() => handleBookingClick(tour.id)}
                      >
                        Réserver
                      </Button>
                    </CardActions>
                  </Card>
                </Grid>
              ))}
            </Grid>
          )}

          {totalPages > 1 && (
            <Box display="flex" justifyContent="center" mt={4}>
              <Pagination
                count={totalPages}
                page={page}
                onChange={handlePageChange}
                color="primary"
              />
            </Box>
          )}
        </>
      )}
    </Container>
  );
};

export default Tours;
