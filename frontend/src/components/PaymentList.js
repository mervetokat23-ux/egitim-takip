import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getOdemeler, deleteOdeme, getEgitimler, getSorumlular } from '../services/api';
import './PaymentList.css';

/**
 * PaymentList Component
 * 
 * Displays a list of payments with filtering, pagination, and CRUD actions.
 * Features:
 * - Table with education, unit price, total price, source, responsible, status
 * - Filters: education, status, payment source, responsible
 * - Actions: View, Edit, Delete
 * - Currency formatting for prices
 * - Admin-only: Link to view logs for each payment
 */
const PaymentList = () => {
  const navigate = useNavigate();
  const [odemeler, setOdemeler] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  
  // Pagination
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  
  // Filters
  const [filters, setFilters] = useState({
    egitimId: '',
    durum: '',
    odemeKaynagi: '',
    sorumluId: ''
  });
  
  // Dropdown data
  const [egitimler, setEgitimler] = useState([]);
  const [sorumlular, setSorumlular] = useState([]);
  
  // Delete modal
  const [deleteModal, setDeleteModal] = useState({ show: false, id: null, name: '' });
  
  // User role
  const [userRole, setUserRole] = useState('');

  useEffect(() => {
    fetchOdemeler();
    fetchDropdownData();
    
    // Get user role from localStorage
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    setUserRole(user.rol || '');
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, filters]);

  const fetchOdemeler = async () => {
    setLoading(true);
    setError(null);
    try {
      const params = {
        page,
        size,
        sort: 'id,desc'
      };
      
      // Add filters if they have values
      if (filters.egitimId) params.egitimId = filters.egitimId;
      if (filters.durum) params.durum = filters.durum;
      if (filters.odemeKaynagi) params.odemeKaynagi = filters.odemeKaynagi;
      if (filters.sorumluId) params.sorumluId = filters.sorumluId;
      
      const response = await getOdemeler(params);
      setOdemeler(response.content || []);
      setTotalPages(response.totalPages || 0);
      setTotalElements(response.totalElements || 0);
    } catch (err) {
      console.error('Ödemeler getirilemedi:', err);
      setError('Ödeme listesi alınamadı.');
    } finally {
      setLoading(false);
    }
  };

  const fetchDropdownData = async () => {
    try {
      // Fetch education list
      const egitimResponse = await getEgitimler({ page: 0, size: 1000 });
      setEgitimler(egitimResponse.content || []);
      
      // Fetch responsible list
      const sorumluResponse = await getSorumlular({ page: 0, size: 1000 });
      setSorumlular(sorumluResponse.content || []);
    } catch (err) {
      console.error('Dropdown verileri getirilemedi:', err);
    }
  };

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters(prev => ({
      ...prev,
      [name]: value
    }));
    setPage(0); // Reset to first page when filter changes
  };

  const clearFilters = () => {
    setFilters({
      egitimId: '',
      durum: '',
      odemeKaynagi: '',
      sorumluId: ''
    });
    setPage(0);
  };

  const handleDelete = async () => {
    try {
      await deleteOdeme(deleteModal.id);
      setDeleteModal({ show: false, id: null, name: '' });
      showToast('Ödeme başarıyla silindi!', 'success');
      fetchOdemeler();
    } catch (err) {
      console.error('Ödeme silinemedi:', err);
      showToast('Ödeme silinemedi!', 'error');
    }
  };

  const showToast = (message, type) => {
    // Simple toast notification
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    document.body.appendChild(toast);
    
    setTimeout(() => {
      toast.classList.add('show');
    }, 100);
    
    setTimeout(() => {
      toast.classList.remove('show');
      setTimeout(() => {
        document.body.removeChild(toast);
      }, 300);
    }, 3000);
  };

  const formatCurrency = (amount) => {
    if (!amount) return '₺0.00';
    return new Intl.NumberFormat('tr-TR', {
      style: 'currency',
      currency: 'TRY'
    }).format(amount);
  };

  const getStatusBadgeClass = (durum) => {
    switch (durum) {
      case 'Ödendi':
        return 'badge-success';
      case 'Bekliyor':
        return 'badge-warning';
      case 'İptal':
        return 'badge-danger';
      default:
        return 'badge-secondary';
    }
  };

  return (
    <div className="payment-list-container">
      <div className="payment-list-header">
        <h2>Ödeme Yönetimi</h2>
        <button
          className="btn btn-primary"
          onClick={() => navigate('/payments/create')}
        >
          + Yeni Ödeme Ekle
        </button>
      </div>

      {/* Filters */}
      <div className="filters-section">
        <h3>Filtreler</h3>
        <div className="filters-grid">
          <div className="filter-item">
            <label>Eğitim</label>
            <select
              name="egitimId"
              value={filters.egitimId}
              onChange={handleFilterChange}
            >
              <option value="">Tümü</option>
              {egitimler.map(egitim => (
                <option key={egitim.id} value={egitim.id}>
                  {egitim.ad}
                </option>
              ))}
            </select>
          </div>

          <div className="filter-item">
            <label>Durum</label>
            <select
              name="durum"
              value={filters.durum}
              onChange={handleFilterChange}
            >
              <option value="">Tümü</option>
              <option value="Ödendi">Ödendi</option>
              <option value="Bekliyor">Bekliyor</option>
              <option value="İptal">İptal</option>
            </select>
          </div>

          <div className="filter-item">
            <label>Ödeme Kaynağı</label>
            <input
              type="text"
              name="odemeKaynagi"
              value={filters.odemeKaynagi}
              onChange={handleFilterChange}
              placeholder="Ödeme kaynağı..."
            />
          </div>

          <div className="filter-item">
            <label>Sorumlu</label>
            <select
              name="sorumluId"
              value={filters.sorumluId}
              onChange={handleFilterChange}
            >
              <option value="">Tümü</option>
              {sorumlular.map(sorumlu => (
                <option key={sorumlu.id} value={sorumlu.id}>
                  {sorumlu.ad}
                </option>
              ))}
            </select>
          </div>
        </div>
        <button className="btn btn-secondary" onClick={clearFilters}>
          Filtreleri Temizle
        </button>
      </div>

      {/* Results count */}
      {!loading && (
        <div className="results-info">
          Toplam {totalElements} ödeme bulundu
        </div>
      )}

      {/* Loading state */}
      {loading && (
        <div className="loading-spinner">
          <div className="spinner"></div>
          <p>Yükleniyor...</p>
        </div>
      )}

      {/* Error state */}
      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {/* Payments table */}
      {!loading && !error && (
        <div className="table-container">
          <table className="payment-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Eğitim</th>
                <th>Birim Ücret</th>
                <th>Toplam Ücret</th>
                <th>Ödeme Kaynağı</th>
                <th>Sorumlu</th>
                <th>Durum</th>
                <th>Operasyon</th>
                <th>İşlemler</th>
              </tr>
            </thead>
            <tbody>
              {odemeler.length === 0 ? (
                <tr>
                  <td colSpan="9" className="no-data">
                    Ödeme bulunamadı
                  </td>
                </tr>
              ) : (
                odemeler.map(odeme => (
                  <tr key={odeme.id}>
                    <td>{odeme.id}</td>
                    <td>
                      {odeme.egitim ? odeme.egitim.ad : '-'}
                    </td>
                    <td className="currency">
                      {formatCurrency(odeme.birimUcret)}
                    </td>
                    <td className="currency">
                      {formatCurrency(odeme.toplamUcret)}
                    </td>
                    <td>{odeme.odemeKaynagi || '-'}</td>
                    <td>
                      {odeme.sorumlu ? odeme.sorumlu.ad : '-'}
                    </td>
                    <td>
                      <span className={`badge ${getStatusBadgeClass(odeme.durum)}`}>
                        {odeme.durum}
                      </span>
                    </td>
                    <td>{odeme.operasyon || '-'}</td>
                    <td className="actions">
                      <button
                        className="btn-icon btn-view"
                        onClick={() => navigate(`/payments/${odeme.id}/view`)}
                        title="Görüntüle"
                      >
                        👁️
                      </button>
                      <button
                        className="btn-icon btn-edit"
                        onClick={() => navigate(`/payments/${odeme.id}/edit`)}
                        title="Düzenle"
                      >
                        ✏️
                      </button>
                      {userRole === 'ADMIN' && (
                        <button
                          className="btn-icon btn-delete"
                          onClick={() => setDeleteModal({
                            show: true,
                            id: odeme.id,
                            name: `${odeme.egitim?.ad || 'Ödeme'} - ${formatCurrency(odeme.toplamUcret)}`
                          })}
                          title="Sil"
                        >
                          🗑️
                        </button>
                      )}
                      {userRole === 'ADMIN' && (
                        <button
                          className="btn-icon btn-logs"
                          onClick={() => navigate(`/logs/activity?entityType=PAYMENT&entityId=${odeme.id}`)}
                          title="Log Kayıtlarını Gör"
                        >
                          📋
                        </button>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* Pagination */}
      {!loading && totalPages > 1 && (
        <div className="pagination">
          <button
            onClick={() => setPage(Math.max(0, page - 1))}
            disabled={page === 0}
            className="btn btn-secondary"
          >
            ← Önceki
          </button>
          <span className="page-info">
            Sayfa {page + 1} / {totalPages}
          </span>
          <button
            onClick={() => setPage(Math.min(totalPages - 1, page + 1))}
            disabled={page >= totalPages - 1}
            className="btn btn-secondary"
          >
            Sonraki →
          </button>
        </div>
      )}

      {/* Delete Modal */}
      {deleteModal.show && (
        <div className="modal-overlay" onClick={() => setDeleteModal({ show: false, id: null, name: '' })}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h3>Ödeme Silme Onayı</h3>
            <p>
              <strong>{deleteModal.name}</strong> ödemesini silmek istediğinizden emin misiniz?
            </p>
            <p className="warning-text">Bu işlem geri alınamaz!</p>
            <div className="modal-actions">
              <button
                className="btn btn-secondary"
                onClick={() => setDeleteModal({ show: false, id: null, name: '' })}
              >
                İptal
              </button>
              <button
                className="btn btn-danger"
                onClick={handleDelete}
              >
                Sil
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default PaymentList;

