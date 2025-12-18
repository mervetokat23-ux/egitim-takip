import React from 'react';
import {
  Box,
  Paper,
  Grid,
  Card,
  CardContent,
  CardActionArea,
  Typography
} from '@mui/material';
import {
  Api,
  Assessment,
  Error as ErrorIcon,
  Speed,
  Computer
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import Navbar from '../Navbar';

function LogDashboard() {
  const navigate = useNavigate();

  const logTypes = [
    {
      title: 'API Logları',
      description: 'HTTP request/response logları',
      icon: <Api sx={{ fontSize: 60, color: '#1976d2' }} />,
      path: '/logs/api',
      color: '#1976d2'
    },
    {
      title: 'Kullanıcı Aksiyon Logları',
      description: 'Kullanıcı aktiviteleri (CREATE, UPDATE, DELETE)',
      icon: <Assessment sx={{ fontSize: 60, color: '#2e7d32' }} />,
      path: '/logs/activity',
      color: '#2e7d32'
    },
    {
      title: 'Hata Logları',
      description: 'Exception ve hata kayıtları',
      icon: <ErrorIcon sx={{ fontSize: 60, color: '#d32f2f' }} />,
      path: '/logs/errors',
      color: '#d32f2f'
    },
    {
      title: 'Performans Logları',
      description: 'Yavaş çalışan işlemler (1s+)',
      icon: <Speed sx={{ fontSize: 60, color: '#ed6c02' }} />,
      path: '/logs/performance',
      color: '#ed6c02'
    },
    {
      title: 'Frontend Logları',
      description: 'Frontend kullanıcı aksiyonları',
      icon: <Computer sx={{ fontSize: 60, color: '#9c27b0' }} />,
      path: '/logs/frontend',
      color: '#9c27b0'
    }
  ];

  return (
    <div>
      <Navbar />
      <Box sx={{ p: 3 }}>
        <Typography variant="h4" gutterBottom>
          Log Yönetimi
        </Typography>
      <Typography variant="body1" color="text.secondary" paragraph>
        Sistem loglarını görüntüleyin ve analiz edin
      </Typography>

      <Grid container spacing={3}>
        {logTypes.map((logType, index) => (
          <Grid item xs={12} sm={6} md={4} key={index}>
            <Card 
              sx={{ 
                height: '100%',
                transition: 'transform 0.2s, box-shadow 0.2s',
                '&:hover': {
                  transform: 'translateY(-4px)',
                  boxShadow: 6
                }
              }}
            >
              <CardActionArea 
                onClick={() => navigate(logType.path)}
                sx={{ height: '100%' }}
              >
                <CardContent sx={{ textAlign: 'center', py: 4 }}>
                  <Box sx={{ mb: 2 }}>
                    {logType.icon}
                  </Box>
                  <Typography 
                    variant="h6" 
                    gutterBottom 
                    sx={{ color: logType.color, fontWeight: 'bold' }}
                  >
                    {logType.title}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {logType.description}
                  </Typography>
                </CardContent>
              </CardActionArea>
            </Card>
          </Grid>
        ))}
      </Grid>

      {/* Quick Stats */}
      <Paper sx={{ mt: 4, p: 3 }}>
        <Typography variant="h6" gutterBottom>
          Hızlı Bilgiler
        </Typography>
        <Grid container spacing={2}>
          <Grid item xs={12} sm={6} md={3}>
            <Box sx={{ textAlign: 'center', p: 2, bgcolor: '#e3f2fd', borderRadius: 2 }}>
              <Typography variant="h4" color="primary">-</Typography>
              <Typography variant="body2">API Çağrısı</Typography>
              <Typography variant="caption" color="text.secondary">Son 24 saat</Typography>
            </Box>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Box sx={{ textAlign: 'center', p: 2, bgcolor: '#e8f5e9', borderRadius: 2 }}>
              <Typography variant="h4" color="success.main">-</Typography>
              <Typography variant="body2">Kullanıcı Aksiyonu</Typography>
              <Typography variant="caption" color="text.secondary">Son 24 saat</Typography>
            </Box>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Box sx={{ textAlign: 'center', p: 2, bgcolor: '#ffebee', borderRadius: 2 }}>
              <Typography variant="h4" color="error.main">-</Typography>
              <Typography variant="body2">Hata</Typography>
              <Typography variant="caption" color="text.secondary">Son 24 saat</Typography>
            </Box>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Box sx={{ textAlign: 'center', p: 2, bgcolor: '#fff3e0', borderRadius: 2 }}>
              <Typography variant="h4" color="warning.main">-</Typography>
              <Typography variant="body2">Yavaş İşlem</Typography>
              <Typography variant="caption" color="text.secondary">Son 24 saat</Typography>
            </Box>
          </Grid>
        </Grid>
      </Paper>
      </Box>
    </div>
  );
}

export default LogDashboard;

