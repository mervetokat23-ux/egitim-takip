import React, { useState, useEffect } from 'react';
import {
  Box,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TablePagination,
  TextField,
  Button,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Grid,
  Typography,
  IconButton
} from '@mui/material';
import { Visibility, FilterList, Refresh, Error as ErrorIcon } from '@mui/icons-material';
import { format } from 'date-fns';
import Navbar from '../Navbar';
import api from '../../services/api';

function ErrorLogs() {
  const [logs, setLogs] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(20);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(false);
  const [selectedLog, setSelectedLog] = useState(null);
  const [openModal, setOpenModal] = useState(false);
  
  const [filters, setFilters] = useState({
    userId: '',
    exceptionType: '',
    endpoint: '',
    startDate: '',
    endDate: ''
  });

  useEffect(() => {
    fetchLogs();
  }, [page, rowsPerPage]);

  const fetchLogs = async () => {
    setLoading(true);
    try {
      const params = {
        page,
        size: rowsPerPage,
        ...Object.fromEntries(
          Object.entries(filters).filter(([_, v]) => v !== '')
        )
      };

      // Tarih formatını backend için ISO formatına çevir
      if (params.startDate) {
        params.startDate = new Date(params.startDate).toISOString();
      }
      if (params.endDate) {
        const endDate = new Date(params.endDate);
        endDate.setHours(23, 59, 59, 999);
        params.endDate = endDate.toISOString();
      }

      const response = await api.get('/api/logs/errors', { params });
      setLogs(response.data.content);
      setTotalElements(response.data.totalElements);
    } catch (error) {
      console.error('Error fetching error logs:', error);
      alert('Log yüklenirken hata oluştu');
    } finally {
      setLoading(false);
    }
  };

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleFilterChange = (field, value) => {
    setFilters({ ...filters, [field]: value });
  };

  const handleApplyFilters = () => {
    setPage(0);
    fetchLogs();
  };

  const handleClearFilters = () => {
    setFilters({
      userId: '',
      exceptionType: '',
      endpoint: '',
      startDate: '',
      endDate: ''
    });
    setPage(0);
  };

  const handleViewDetails = (log) => {
    setSelectedLog(log);
    setOpenModal(true);
  };

  const getExceptionShortName = (exceptionType) => {
    if (!exceptionType) return '';
    const parts = exceptionType.split('.');
    return parts[parts.length - 1];
  };

  return (
    <div>
      <Navbar />
      <Box sx={{ p: 3 }}>
        <Typography variant="h4" gutterBottom>
          <ErrorIcon color="error" sx={{ verticalAlign: 'middle', mr: 1 }} />
          Hata Logları
        </Typography>

      {/* Filters */}
      <Paper sx={{ p: 2, mb: 2 }}>
        <Typography variant="h6" gutterBottom>
          <FilterList /> Filtreler
        </Typography>
        <Grid container spacing={2}>
          <Grid item xs={12} sm={6} md={3}>
            <TextField
              fullWidth
              label="User ID"
              size="small"
              value={filters.userId}
              onChange={(e) => handleFilterChange('userId', e.target.value)}
            />
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <TextField
              fullWidth
              label="Exception Type"
              size="small"
              value={filters.exceptionType}
              onChange={(e) => handleFilterChange('exceptionType', e.target.value)}
              placeholder="NullPointerException"
            />
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <TextField
              fullWidth
              label="Endpoint"
              size="small"
              value={filters.endpoint}
              onChange={(e) => handleFilterChange('endpoint', e.target.value)}
              placeholder="/egitim"
            />
          </Grid>
          <Grid item xs={12} sm={6} md={2}>
            <TextField
              fullWidth
              label="Başlangıç Tarihi"
              type="date"
              size="small"
              value={filters.startDate}
              onChange={(e) => handleFilterChange('startDate', e.target.value)}
              InputLabelProps={{ shrink: true }}
            />
          </Grid>
          <Grid item xs={12} sm={6} md={2}>
            <TextField
              fullWidth
              label="Bitiş Tarihi"
              type="date"
              size="small"
              value={filters.endDate}
              onChange={(e) => handleFilterChange('endDate', e.target.value)}
              InputLabelProps={{ shrink: true }}
            />
          </Grid>
          <Grid item xs={12} sm={6} md={2}>
            <Button
              fullWidth
              variant="contained"
              onClick={handleApplyFilters}
              disabled={loading}
            >
              Filtrele
            </Button>
          </Grid>
          <Grid item xs={12} sm={6} md={1}>
            <Button
              fullWidth
              variant="outlined"
              onClick={handleClearFilters}
            >
              Temizle
            </Button>
          </Grid>
        </Grid>
      </Paper>

      {/* Table */}
      <Paper>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', p: 2 }}>
          <Typography variant="h6">
            Toplam: {totalElements} hata
          </Typography>
          <Button
            startIcon={<Refresh />}
            onClick={fetchLogs}
            disabled={loading}
          >
            Yenile
          </Button>
        </Box>

        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>User ID</TableCell>
                <TableCell>Endpoint</TableCell>
                <TableCell>Exception Type</TableCell>
                <TableCell>Message</TableCell>
                <TableCell>Tarih</TableCell>
                <TableCell>İşlem</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {loading ? (
                <TableRow>
                  <TableCell colSpan={7} align="center">Yükleniyor...</TableCell>
                </TableRow>
              ) : logs.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={7} align="center">Hata kaydı bulunamadı</TableCell>
                </TableRow>
              ) : (
                logs.map((log) => (
                  <TableRow key={log.id} hover>
                    <TableCell>{log.id}</TableCell>
                    <TableCell>{log.userId || '-'}</TableCell>
                    <TableCell>
                      <Typography variant="body2" noWrap sx={{ maxWidth: 200 }}>
                        {log.endpoint}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Chip
                        label={getExceptionShortName(log.exceptionType)}
                        size="small"
                        color="error"
                        variant="outlined"
                      />
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2" noWrap sx={{ maxWidth: 300 }}>
                        {log.message}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      {log.createdAt ? format(new Date(log.createdAt), 'dd.MM.yyyy HH:mm:ss') : '-'}
                    </TableCell>
                    <TableCell>
                      <IconButton
                        size="small"
                        color="primary"
                        onClick={() => handleViewDetails(log)}
                      >
                        <Visibility />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </TableContainer>

        <TablePagination
          component="div"
          count={totalElements}
          page={page}
          onPageChange={handleChangePage}
          rowsPerPage={rowsPerPage}
          onRowsPerPageChange={handleChangeRowsPerPage}
          rowsPerPageOptions={[10, 20, 50, 100]}
          labelRowsPerPage="Sayfa başına:"
          labelDisplayedRows={({ from, to, count }) => `${from}-${to} / ${count}`}
        />
      </Paper>

      {/* Detail Modal */}
      <Dialog open={openModal} onClose={() => setOpenModal(false)} maxWidth="lg" fullWidth>
        <DialogTitle>Error Log Detayı #{selectedLog?.id}</DialogTitle>
        <DialogContent>
          {selectedLog && (
            <Box sx={{ mt: 1 }}>
              <Typography variant="subtitle1" color="error" gutterBottom>
                <strong>Exception:</strong> {selectedLog.exceptionType}
              </Typography>
              <Typography variant="body2" gutterBottom>
                <strong>Message:</strong> {selectedLog.message}
              </Typography>
              <Typography variant="body2" gutterBottom>
                <strong>Endpoint:</strong> {selectedLog.endpoint}
              </Typography>
              <Typography variant="subtitle2" sx={{ mt: 2, mb: 1 }}>
                Stack Trace:
              </Typography>
              <pre style={{
                backgroundColor: '#f5f5f5',
                padding: '16px',
                borderRadius: '4px',
                overflow: 'auto',
                maxHeight: '400px',
                fontSize: '12px'
              }}>
                {selectedLog.stacktrace}
              </pre>
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenModal(false)}>Kapat</Button>
        </DialogActions>
      </Dialog>
      </Box>
    </div>
  );
}

export default ErrorLogs;

