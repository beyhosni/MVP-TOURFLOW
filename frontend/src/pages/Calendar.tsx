import React, { useState, useEffect, useCallback } from 'react';
import {
  Container,
  Typography,
  Box,
  Paper,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Grid,
  IconButton,
  Tooltip,
  Chip,
} from '@mui/material';
import {
  Add as AddIcon,
  EventBusy as BlockIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Download as ExportIcon,
  Upload as ImportIcon,
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';
import { toursAPI, availabilityAPI, calendarAPI } from '../services/api';

// Note: Pour le MVP, nous utiliserons un calendrier simple avec FullCalendar
// qui sera ajouté comme dépendance dans le package.json

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

interface AvailabilityRule {
  id: string;
  daysOfWeek: string[];
  startTimes: string[];
  minBookingHours: number;
  maxCapacity: number;
  active: boolean;
  tour: Tour;
}

interface AvailabilityException {
  id: string;
  startDate: string;
  endDate: string;
  reason: string;
  tour: Tour;
}

interface ExternalCalendar {
  id: string;
  name: string;
  icsUrl: string;
  active: boolean;
}

const Calendar: React.FC = () => {
  const { user } = useAuth();

  const [tours, setTours] = useState<Tour[]>([]);
  const [availabilityRules, setAvailabilityRules] = useState<AvailabilityRule[]>([]);
  const [availabilityExceptions, setAvailabilityExceptions] = useState<AvailabilityException[]>([]);
  const [externalCalendars, setExternalCalendars] = useState<ExternalCalendar[]>([]);
  const [loading, setLoading] = useState(true);
  const [ruleDialogOpen, setRuleDialogOpen] = useState(false);
  const [exceptionDialogOpen, setExceptionDialogOpen] = useState(false);
  const [selectedTour, setSelectedTour] = useState<Tour | null>(null);
  const [currentRule, setCurrentRule] = useState<Partial<AvailabilityRule>>({
    daysOfWeek: [],
    startTimes: [],
    minBookingHours: 12,
    maxCapacity: 10,
    active: true,
  });
  const [currentException, setCurrentException] = useState<Partial<AvailabilityException>>({
    startDate: '',
    endDate: '',
    reason: '',
  });
  const [isEditing, setIsEditing] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);

  useEffect(() => {
    fetchCalendarData();
  }, []);

  const fetchCalendarData = async () => {
    try {
      setLoading(true);

      // Récupérer les tours du guide
      const toursResponse = await toursAPI.getMyTours();
      setTours(toursResponse.data);

      // Récupérer les règles de disponibilité pour chaque tour
      const rulesPromises = toursResponse.data.map(async (tour: Tour) => {
        const response = await availabilityAPI.getRulesByTour(tour.id);
        return response.data;
      });

      const rulesArrays = await Promise.all(rulesPromises);
      const allRules = rulesArrays.flat();
      setAvailabilityRules(allRules);

      // Récupérer les exceptions de disponibilité pour chaque tour
      const exceptionsPromises = toursResponse.data.map(async (tour: Tour) => {
        const response = await availabilityAPI.getExceptionsByTour(tour.id);
        return response.data;
      });

      const exceptionsArrays = await Promise.all(exceptionsPromises);
      const allExceptions = exceptionsArrays.flat();
      setAvailabilityExceptions(allExceptions);

      // Récupérer les calendriers externes
      const calendarsResponse = await calendarAPI.getExternalCalendars();
      setExternalCalendars(calendarsResponse.data);
    } catch (error) {
      console.error('Erreur lors de la récupération des données du calendrier:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenRuleDialog = (tour: Tour, rule?: AvailabilityRule) => {
    setSelectedTour(tour);
    if (rule) {
      setCurrentRule(rule);
      setIsEditing(true);
      setEditingId(rule.id);
    } else {
      setCurrentRule({
        daysOfWeek: [],
        startTimes: [],
        minBookingHours: 12,
        maxCapacity: 10,
        active: true,
      });
      setIsEditing(false);
      setEditingId(null);
    }
    setRuleDialogOpen(true);
  };

  const handleCloseRuleDialog = () => {
    setRuleDialogOpen(false);
    setSelectedTour(null);
    setCurrentRule({
      daysOfWeek: [],
      startTimes: [],
      minBookingHours: 12,
      maxCapacity: 10,
      active: true,
    });
    setIsEditing(false);
    setEditingId(null);
  };

  const handleSaveRule = async () => {
    if (!selectedTour) return;

    try {
      const ruleData = {
        ...currentRule,
        tour: selectedTour,
      };

      if (isEditing && editingId) {
        await availabilityAPI.updateRule(editingId, ruleData);
      } else {
        await availabilityAPI.createRule(ruleData);
      }

      fetchCalendarData();
      handleCloseRuleDialog();
    } catch (error) {
      console.error('Erreur lors de la sauvegarde de la règle:', error);
    }
  };

  const handleDeleteRule = async (id: string) => {
    try {
      await availabilityAPI.deleteRule(id);
      fetchCalendarData();
    } catch (error) {
      console.error('Erreur lors de la suppression de la règle:', error);
    }
  };

  const handleOpenExceptionDialog = (tour: Tour, exception?: AvailabilityException) => {
    setSelectedTour(tour);
    if (exception) {
      setCurrentException(exception);
      setIsEditing(true);
      setEditingId(exception.id);
    } else {
      setCurrentException({
        startDate: '',
        endDate: '',
        reason: '',
      });
      setIsEditing(false);
      setEditingId(null);
    }
    setExceptionDialogOpen(true);
  };

  const handleCloseExceptionDialog = () => {
    setExceptionDialogOpen(false);
    setSelectedTour(null);
    setCurrentException({
      startDate: '',
      endDate: '',
      reason: '',
    });
    setIsEditing(false);
    setEditingId(null);
  };

  const handleSaveException = async () => {
    if (!selectedTour) return;

    try {
      const exceptionData = {
        ...currentException,
        tour: selectedTour,
      };

      if (isEditing && editingId) {
        await availabilityAPI.updateException(editingId, exceptionData);
      } else {
        await availabilityAPI.createException(exceptionData);
      }

      fetchCalendarData();
      handleCloseExceptionDialog();
    } catch (error) {
      console.error('Erreur lors de la sauvegarde de l\'exception:', error);
    }
  };

  const handleDeleteException = async (id: string) => {
    try {
      await availabilityAPI.deleteException(id);
      fetchCalendarData();
    } catch (error) {
      console.error('Erreur lors de la suppression de l\'exception:', error);
    }
  };

  const handleExportCalendar = async () => {
    try {
      const response = await calendarAPI.getICalCalendar(user!.id);
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'tourflow-calendar.ics');
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      console.error('Erreur lors de l\'export du calendrier:', error);
    }
  };

  const handleRuleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setCurrentRule({
      ...currentRule,
      [name]: value,
    });
  };

  const handleExceptionInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setCurrentException({
      ...currentException,
      [name]: value,
    });
  };

  // Note: Pour le MVP, nous allons afficher les règles et exceptions de manière simple
  // Dans une version ultérieure, nous pourrions utiliser FullCalendar pour une meilleure visualisation

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" component="h1">
          Mon calendrier
        </Typography>
        <Box>
          <Tooltip title="Exporter le calendrier">
            <Button
              variant="outlined"
              startIcon={<ExportIcon />}
              onClick={handleExportCalendar}
              sx={{ mr: 1 }}
            >
              Exporter
            </Button>
          </Tooltip>
          <Tooltip title="Importer un calendrier externe">
            <Button
              variant="outlined"
              startIcon={<ImportIcon />}
              sx={{ mr: 1 }}
            >
              Importer
            </Button>
          </Tooltip>
        </Box>
      </Box>

      {loading ? (
        <Box display="flex" justifyContent="center" alignItems="center" height="400px">
          <CircularProgress />
        </Box>
      ) : (
        <>
          {tours.length === 0 ? (
            <Paper sx={{ p: 3, textAlign: 'center' }}>
              <Typography variant="h6" gutterBottom>
                Vous n'avez pas encore créé de tour
              </Typography>
              <Typography variant="body2" color="text.secondary" paragraph>
                Commencez par créer un tour pour pouvoir définir vos disponibilités.
              </Typography>
              <Button
                variant="contained"
                onClick={() => navigate('/tours/create')}
              >
                Créer un tour
              </Button>
            </Paper>
          ) : (
            <Grid container spacing={3}>
              {tours.map((tour) => (
                <Grid item xs={12} md={6} key={tour.id}>
                  <Paper sx={{ p: 3 }}>
                    <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                      <Typography variant="h6">
                        {tour.title}
                      </Typography>
                      <Box>
                        <Tooltip title="Ajouter une règle de disponibilité">
                          <IconButton
                            color="primary"
                            onClick={() => handleOpenRuleDialog(tour)}
                            size="small"
                          >
                            <AddIcon />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="Ajouter une exception">
                          <IconButton
                            color="secondary"
                            onClick={() => handleOpenExceptionDialog(tour)}
                            size="small"
                          >
                            <BlockIcon />
                          </IconButton>
                        </Tooltip>
                      </Box>
                    </Box>

                    <Typography variant="subtitle1" gutterBottom>
                      Règles de disponibilité
                    </Typography>

                    {availabilityRules
                      .filter(rule => rule.tour.id === tour.id)
                      .length === 0 ? (
                      <Typography variant="body2" color="text.secondary" paragraph>
                        Aucune règle de disponibilité définie
                      </Typography>
                    ) : (
                      availabilityRules
                        .filter(rule => rule.tour.id === tour.id)
                        .map((rule) => (
                          <Box key={rule.id} sx={{ mb: 1, p: 1, border: '1px solid #eee', borderRadius: 1 }}>
                            <Box display="flex" justifyContent="space-between" alignItems="center">
                              <Box>
                                <Typography variant="body2">
                                  Jours: {rule.daysOfWeek.join(', ')}
                                </Typography>
                                <Typography variant="body2">
                                  Heures: {rule.startTimes.join(', ')}
                                </Typography>
                                <Typography variant="body2">
                                  Capacité: {rule.maxCapacity} pers.
                                </Typography>
                                <Typography variant="body2">
                                  Délai min: {rule.minBookingHours}h
                                </Typography>
                                <Chip 
                                  label={rule.active ? 'Actif' : 'Inactif'} 
                                  color={rule.active ? 'success' : 'default'} 
                                  size="small" 
                                />
                              </Box>
                              <Box>
                                <IconButton
                                  size="small"
                                  onClick={() => handleOpenRuleDialog(tour, rule)}
                                >
                                  <EditIcon fontSize="small" />
                                </IconButton>
                                <IconButton
                                  size="small"
                                  onClick={() => handleDeleteRule(rule.id)}
                                >
                                  <DeleteIcon fontSize="small" />
                                </IconButton>
                              </Box>
                            </Box>
                          </Box>
                        ))
                    )}

                    <Typography variant="subtitle1" gutterBottom sx={{ mt: 2 }}>
                      Exceptions
                    </Typography>

                    {availabilityExceptions
                      .filter(exception => exception.tour.id === tour.id)
                      .length === 0 ? (
                      <Typography variant="body2" color="text.secondary" paragraph>
                        Aucune exception définie
                      </Typography>
                    ) : (
                      availabilityExceptions
                        .filter(exception => exception.tour.id === tour.id)
                        .map((exception) => (
                          <Box key={exception.id} sx={{ mb: 1, p: 1, border: '1px solid #eee', borderRadius: 1 }}>
                            <Box display="flex" justifyContent="space-between" alignItems="center">
                              <Box>
                                <Typography variant="body2">
                                  Du: {new Date(exception.startDate).toLocaleString()}
                                </Typography>
                                <Typography variant="body2">
                                  Au: {new Date(exception.endDate).toLocaleString()}
                                </Typography>
                                <Typography variant="body2">
                                  Raison: {exception.reason}
                                </Typography>
                              </Box>
                              <Box>
                                <IconButton
                                  size="small"
                                  onClick={() => handleOpenExceptionDialog(tour, exception)}
                                >
                                  <EditIcon fontSize="small" />
                                </IconButton>
                                <IconButton
                                  size="small"
                                  onClick={() => handleDeleteException(exception.id)}
                                >
                                  <DeleteIcon fontSize="small" />
                                </IconButton>
                              </Box>
                            </Box>
                          </Box>
                        ))
                    )}
                  </Paper>
                </Grid>
              ))}
            </Grid>
          )}
        </>
      )}

      {/* Dialogue pour ajouter/modifier une règle de disponibilité */}
      <Dialog open={ruleDialogOpen} onClose={handleCloseRuleDialog} maxWidth="sm" fullWidth>
        <DialogTitle>
          {isEditing ? 'Modifier la règle de disponibilité' : 'Ajouter une règle de disponibilité'}
        </DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Capacité maximale"
                name="maxCapacity"
                type="number"
                value={currentRule.maxCapacity}
                onChange={handleRuleInputChange}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Délai minimum avant réservation (heures)"
                name="minBookingHours"
                type="number"
                value={currentRule.minBookingHours}
                onChange={handleRuleInputChange}
              />
            </Grid>
            {/* Note: Pour simplifier, nous n'implémentons pas la sélection des jours et heures ici */}
            {/* Dans une version complète, nous aurions des sélecteurs pour ces champs */}
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseRuleDialog}>Annuler</Button>
          <Button onClick={handleSaveRule} variant="contained">
            {isEditing ? 'Mettre à jour' : 'Ajouter'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Dialogue pour ajouter/modifier une exception */}
      <Dialog open={exceptionDialogOpen} onClose={handleCloseExceptionDialog} maxWidth="sm" fullWidth>
        <DialogTitle>
          {isEditing ? 'Modifier l\'exception' : 'Ajouter une exception'}
        </DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Date de début"
                name="startDate"
                type="datetime-local"
                value={currentException.startDate}
                onChange={handleExceptionInputChange}
                InputLabelProps={{
                  shrink: true,
                }}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Date de fin"
                name="endDate"
                type="datetime-local"
                value={currentException.endDate}
                onChange={handleExceptionInputChange}
                InputLabelProps={{
                  shrink: true,
                }}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Raison"
                name="reason"
                value={currentException.reason}
                onChange={handleExceptionInputChange}
                multiline
                rows={3}
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseExceptionDialog}>Annuler</Button>
          <Button onClick={handleSaveException} variant="contained">
            {isEditing ? 'Mettre à jour' : 'Ajouter'}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default Calendar;
