import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { AuthProvider } from './contexts/AuthContext';
import { Elements } from '@stripe/react-stripe-js';
import { loadStripe } from '@stripe/stripe-js';

// Pages
import Home from './pages/Home';
import Tours from './pages/Tours';
import TourDetails from './pages/TourDetails';
import Booking from './pages/Booking';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Calendar from './pages/Calendar';
import Profile from './pages/Profile';

// Composants
import Layout from './components/Layout';
import PrivateRoute from './components/PrivateRoute';

// Services
import { stripePublishableKey } from './services/api';

// Thème
const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
});

// Charger Stripe
const stripePromise = loadStripe(stripePublishableKey);

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <Elements stripe={stripePromise}>
          <Router>
            <Routes>
              {/* Routes publiques */}
              <Route path="/" element={<Layout><Home /></Layout>} />
              <Route path="/tours" element={<Layout><Tours /></Layout>} />
              <Route path="/tours/:id" element={<Layout><TourDetails /></Layout>} />
              <Route path="/booking/:tourId" element={<Layout><Booking /></Layout>} />
              <Route path="/login" element={<Layout><Login /></Layout>} />
              <Route path="/register" element={<Layout><Register /></Layout>} />

              {/* Routes privées */}
              <Route 
                path="/dashboard" 
                element={
                  <PrivateRoute>
                    <Layout><Dashboard /></Layout>
                  </PrivateRoute>
                } 
              />
              <Route 
                path="/calendar" 
                element={
                  <PrivateRoute>
                    <Layout><Calendar /></Layout>
                  </PrivateRoute>
                } 
              />
              <Route 
                path="/profile" 
                element={
                  <PrivateRoute>
                    <Layout><Profile /></Layout>
                  </PrivateRoute>
                } 
              />

              {/* Redirection par défaut */}
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </Router>
        </Elements>
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;
