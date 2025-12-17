import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Box,
  Paper,
  TextField,
  Button,
  Avatar,
  Grid,
  Divider,
  Alert,
  CircularProgress,
} from '@mui/material';
import { useAuth } from '../contexts/AuthContext';

const Profile: React.FC = () => {
  const { user, logout } = useAuth();

  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [currentPassword, setCurrentPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [editMode, setEditMode] = useState(false);
  const [passwordMode, setPasswordMode] = useState(false);

  useEffect(() => {
    if (user) {
      setFirstName(user.firstName);
      setLastName(user.lastName);
      setEmail(user.email);
    }
  }, [user]);

  const handleEditToggle = () => {
    setEditMode(!editMode);
    if (!editMode) {
      // Réinitialiser les valeurs
      if (user) {
        setFirstName(user.firstName);
        setLastName(user.lastName);
        setEmail(user.email);
      }
    }
  };

  const handlePasswordToggle = () => {
    setPasswordMode(!passwordMode);
    if (!passwordMode) {
      // Réinitialiser les valeurs
      setPassword('');
      setConfirmPassword('');
      setCurrentPassword('');
    }
  };

  const validateProfileForm = () => {
    if (!firstName || !lastName || !email) {
      setError('Tous les champs sont requis');
      return false;
    }
    return true;
  };

  const validatePasswordForm = () => {
    if (!currentPassword || !password || !confirmPassword) {
      setError('Tous les champs sont requis');
      return false;
    }
    if (password.length < 8) {
      setError('Le mot de passe doit contenir au moins 8 caractères');
      return false;
    }
    if (password !== confirmPassword) {
      setError('Les mots de passe ne correspondent pas');
      return false;
    }
    return true;
  };

  const handleProfileSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateProfileForm()) return;

    try {
      setLoading(true);
      setError(null);

      // Ici, nous appellerions une API pour mettre à jour le profil
      // Pour le MVP, nous allons juste simuler la mise à jour
      await new Promise(resolve => setTimeout(resolve, 1000));

      setSuccess('Profil mis à jour avec succès');
      setEditMode(false);
    } catch (error: any) {
      setError(error.response?.data?.error || 'Une erreur est survenue lors de la mise à jour du profil');
    } finally {
      setLoading(false);
    }
  };

  const handlePasswordSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validatePasswordForm()) return;

    try {
      setLoading(true);
      setError(null);

      // Ici, nous appellerions une API pour mettre à jour le mot de passe
      // Pour le MVP, nous allons juste simuler la mise à jour
      await new Promise(resolve => setTimeout(resolve, 1000));

      setSuccess('Mot de passe mis à jour avec succès');
      setPasswordMode(false);
      setPassword('');
      setConfirmPassword('');
      setCurrentPassword('');
    } catch (error: any) {
      setError(error.response?.data?.error || 'Une erreur est survenue lors de la mise à jour du mot de passe');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    logout();
  };

  if (!user) {
    return (
      <Container maxWidth="md" sx={{ py: 4 }}>
        <Box textAlign="center">
          <Typography variant="h6">
            Vous devez être connecté pour accéder à cette page
          </Typography>
        </Box>
      </Container>
    );
  }

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Mon profil
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {success && (
        <Alert severity="success" sx={{ mb: 2 }}>
          {success}
        </Alert>
      )}

      <Paper sx={{ p: 3, mb: 3 }}>
        <Box display="flex" alignItems="center" mb={3}>
          <Avatar sx={{ width: 64, height: 64, mr: 2 }}>
            {user.firstName.charAt(0)}{user.lastName.charAt(0)}
          </Avatar>
          <Box>
            <Typography variant="h5">
              {user.firstName} {user.lastName}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {user.role === 'GUIDE' ? 'Guide touristique' : 'Administrateur'}
            </Typography>
          </Box>
        </Box>

        <Divider sx={{ mb: 3 }} />

        <Box component="form" onSubmit={handleProfileSubmit}>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Prénom"
                value={firstName}
                onChange={(e) => setFirstName(e.target.value)}
                disabled={!editMode}
                margin="normal"
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Nom"
                value={lastName}
                onChange={(e) => setLastName(e.target.value)}
                disabled={!editMode}
                margin="normal"
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Adresse email"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                disabled={!editMode}
                margin="normal"
              />
            </Grid>
          </Grid>

          <Box mt={2} display="flex" justifyContent="flex-end">
            {editMode ? (
              <>
                <Button
                  variant="outlined"
                  onClick={handleEditToggle}
                  sx={{ mr: 1 }}
                  disabled={loading}
                >
                  Annuler
                </Button>
                <Button
                  type="submit"
                  variant="contained"
                  disabled={loading}
                  startIcon={loading ? <CircularProgress size={20} /> : null}
                >
                  Enregistrer
                </Button>
              </>
            ) : (
              <Button
                variant="contained"
                onClick={handleEditToggle}
              >
                Modifier mes informations
              </Button>
            )}
          </Box>
        </Box>
      </Paper>

      <Paper sx={{ p: 3 }}>
        <Typography variant="h6" gutterBottom>
          Changer mon mot de passe
        </Typography>

        {passwordMode ? (
          <Box component="form" onSubmit={handlePasswordSubmit}>
            <TextField
              fullWidth
              label="Mot de passe actuel"
              type="password"
              value={currentPassword}
              onChange={(e) => setCurrentPassword(e.target.value)}
              margin="normal"
              required
            />
            <TextField
              fullWidth
              label="Nouveau mot de passe"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              margin="normal"
              required
            />
            <TextField
              fullWidth
              label="Confirmer le nouveau mot de passe"
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              margin="normal"
              required
            />

            <Box mt={2} display="flex" justifyContent="flex-end">
              <Button
                variant="outlined"
                onClick={handlePasswordToggle}
                sx={{ mr: 1 }}
                disabled={loading}
              >
                Annuler
              </Button>
              <Button
                type="submit"
                variant="contained"
                disabled={loading}
                startIcon={loading ? <CircularProgress size={20} /> : null}
              >
                Mettre à jour le mot de passe
              </Button>
            </Box>
          </Box>
        ) : (
          <Button
            variant="outlined"
            onClick={handlePasswordToggle}
          >
            Changer mon mot de passe
          </Button>
        )}
      </Paper>

      <Box mt={3} display="flex" justifyContent="center">
        <Button
          variant="outlined"
          color="error"
          onClick={handleLogout}
        >
          Se déconnecter
        </Button>
      </Box>
    </Container>
  );
};

export default Profile;
