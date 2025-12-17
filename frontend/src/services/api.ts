import axios from 'axios';

// URL de base de l'API
const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';

// Clé publique Stripe (sera remplacée par la clé réelle)
export const stripePublishableKey = process.env.REACT_APP_STRIPE_PUBLISHABLE_KEY || 'pk_test_...';

// Créer une instance Axios avec configuration par défaut
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Intercepteur pour ajouter le token d'authentification à chaque requête
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Intercepteur pour gérer les erreurs de réponse
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Token expiré ou invalide, déconnecter l'utilisateur
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// API d'authentification
export const authAPI = {
  login: (email: string, password: string) =>
    api.post('/auth/login', { email, password }),

  register: (email: string, password: string, firstName: string, lastName: string) =>
    api.post('/auth/register', { email, password, firstName, lastName }),
};

// API des tours
export const toursAPI = {
  getAll: () => api.get('/tours'),
  getById: (id: string) => api.get(`/tours/${id}`),
  getMyTours: () => api.get('/tours/my-tours'),
  create: (tour: any) => api.post('/tours', tour),
  update: (id: string, tour: any) => api.put(`/tours/${id}`, tour),
  delete: (id: string) => api.delete(`/tours/${id}`),
  findAvailable: (startDate: string, endDate: string) =>
    api.get(`/tours/available?startDate=${startDate}&endDate=${endDate}`),
};

// API des disponibilités
export const availabilityAPI = {
  // Règles de disponibilité
  createRule: (rule: any) => api.post('/availability/rules', rule),
  updateRule: (id: string, rule: any) => api.put(`/availability/rules/${id}`, rule),
  deleteRule: (id: string) => api.delete(`/availability/rules/${id}`),
  getRulesByTour: (tourId: string) => api.get(`/availability/rules/tour/${tourId}`),

  // Exceptions de disponibilité
  createException: (exception: any) => api.post('/availability/exceptions', exception),
  updateException: (id: string, exception: any) => api.put(`/availability/exceptions/${id}`, exception),
  deleteException: (id: string) => api.delete(`/availability/exceptions/${id}`),
  getExceptionsByTour: (tourId: string) => api.get(`/availability/exceptions/tour/${tourId}`),

  // Créneaux disponibles
  getAvailableSlots: (tourId: string, startDate: string, endDate: string) =>
    api.get(`/availability/slots/${tourId}?startDate=${startDate}&endDate=${endDate}`),
};

// API des réservations
export const bookingsAPI = {
  create: (booking: any) => api.post('/bookings', booking),
  getById: (id: string) => api.get(`/bookings/${id}`),
  confirm: (id: string) => api.post(`/bookings/${id}/confirm`),
  cancel: (id: string, reason: string) => 
    api.post(`/bookings/${id}/cancel`, { reason }),
  getByTour: (tourId: string) => api.get(`/bookings/tour/${tourId}`),
  getByCustomerEmail: (email: string) => api.get(`/bookings/customer/${email}`),
};

// API des paiements
export const paymentsAPI = {
  createCheckoutSession: (bookingId: string, successUrl: string, cancelUrl: string) =>
    api.post('/payments/create-checkout-session', { bookingId, successUrl, cancelUrl }),
  getStripePublishableKey: () => api.get('/payments/stripe-publishable-key'),
  refund: (paymentId: string) => api.post(`/payments/${paymentId}/refund`),
};

// API des calendriers
export const calendarAPI = {
  getICalCalendar: (guideId: string) => 
    api.get(`/calendar/ics/${guideId}`, { responseType: 'blob' }),

  // Calendriers externes
  addExternalCalendar: (name: string, icsUrl: string) =>
    api.post('/calendar/external', { name, icsUrl }),
  updateExternalCalendar: (id: string, name: string, icsUrl: string) =>
    api.put(`/calendar/external/${id}`, { name, icsUrl }),
  deleteExternalCalendar: (id: string) => api.delete(`/calendar/external/${id}`),
  getExternalCalendars: () => api.get('/calendar/external'),
};

export default api;
