import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getOdemeById } from '../services/api';
import { format } from 'date-fns';
import { tr } from 'date-fns/locale';
import './PaymentDetail.css';

/**
 * PaymentDetail Component
 * 
 * Read-only view of payment details.
 * Features:
 * - Card layout with all payment information
 * - Education and responsible person details
 * - Currency formatting
 * - Timestamps (createdAt, updatedAt)
 * - Status badge
 * - Admin: Link to view logs
 */
const PaymentDetail = () => {
  const navigate = useNavigate();
  const { id } = useParams();

  const [odeme, setOdeme] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [userRole, setUserRole] = useState('');

  useEffect(() => {
    fetchOdeme();
    
    // Get user role from localStorage
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    setUserRole(user.rol || '');
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const fetchOdeme = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getOdemeById(id);
      setOdeme(data);
    } catch (err) {
      console.error('Ödeme getirilemedi:', err);
      setError('Ödeme bilgileri yüklenemedi.');
    } finally {
      setLoading(false);
    }
  };

  const formatCurrency = (amount) => {
    if (!amount) return '₺0.00';
    return new Intl.NumberFormat('tr-TR', {
      style: 'currency',
      currency: 'TRY'
    }).format(amount);
  };

  const formatDate = (dateString) => {
    if (!dateString) return '-';
    try {
      return format(new Date(dateString), 'dd MMMM yyyy, HH:mm', { locale: tr });
    } catch (err) {
      return dateString;
    }
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

  if (loading) {
    return (
      <div className="loading-spinner">
        <div className="spinner"></div>
        <p>Yükleniyor...</p>
      </div>
    );
  }

  if (error || !odeme) {
    return (
      <div className="error-container">
        <div className="error-message">{error || 'Ödeme bulunamadı'}</div>
        <button className="btn btn-secondary" onClick={() => navigate('/payments')}>
          ← Geri Dön
        </button>
      </div>
    );
  }

  return (
    <div className="payment-detail-container">
      <div className="detail-header">
        <h2>Ödeme Detayı #{odeme.id}</h2>
        <div className="header-actions">
          <button
            className="btn btn-secondary"
            onClick={() => navigate('/payments')}
          >
            ← Geri Dön
          </button>
          <button
            className="btn btn-primary"
            onClick={() => navigate(`/payments/${id}/edit`)}
          >
            ✏️ Düzenle
          </button>
          {userRole === 'ADMIN' && (
            <button
              className="btn btn-info"
              onClick={() => navigate(`/logs/activity?entityType=PAYMENT&entityId=${id}`)}
            >
              📋 Log Kayıtlarını Gör
            </button>
          )}
        </div>
      </div>

      <div className="detail-content">
        {/* Main Information Card */}
        <div className="detail-card">
          <h3>Ödeme Bilgileri</h3>
          
          <div className="detail-row">
            <span className="detail-label">Durum:</span>
            <span className={`badge ${getStatusBadgeClass(odeme.durum)}`}>
              {odeme.durum}
            </span>
          </div>

          <div className="detail-row">
            <span className="detail-label">Birim Ücret:</span>
            <span className="detail-value currency">
              {formatCurrency(odeme.birimUcret)}
            </span>
          </div>

          <div className="detail-row">
            <span className="detail-label">Toplam Ücret:</span>
            <span className="detail-value currency highlight">
              {formatCurrency(odeme.toplamUcret)}
            </span>
          </div>

          <div className="detail-row">
            <span className="detail-label">Ödeme Kaynağı:</span>
            <span className="detail-value">{odeme.odemeKaynagi || '-'}</span>
          </div>

          <div className="detail-row">
            <span className="detail-label">Operasyon Türü:</span>
            <span className="detail-value">{odeme.operasyon || '-'}</span>
          </div>
        </div>

        {/* Education Information Card */}
        <div className="detail-card">
          <h3>Eğitim Bilgileri</h3>
          
          {odeme.egitim ? (
            <>
              <div className="detail-row">
                <span className="detail-label">Eğitim Adı:</span>
                <span className="detail-value">{odeme.egitim.ad}</span>
              </div>

              {odeme.egitim.il && (
                <div className="detail-row">
                  <span className="detail-label">İl:</span>
                  <span className="detail-value">{odeme.egitim.il}</span>
                </div>
              )}

              {odeme.egitim.baslangicTarihi && (
                <div className="detail-row">
                  <span className="detail-label">Başlangıç Tarihi:</span>
                  <span className="detail-value">
                    {formatDate(odeme.egitim.baslangicTarihi)}
                  </span>
                </div>
              )}

              <div className="detail-row">
                <span className="detail-label">Eğitim ID:</span>
                <span className="detail-value">#{odeme.egitim.id}</span>
              </div>

              <button
                className="btn btn-link"
                onClick={() => navigate(`/egitim/${odeme.egitim.id}`)}
              >
                → Eğitim Detayına Git
              </button>
            </>
          ) : (
            <p className="no-data">Eğitim bilgisi bulunamadı</p>
          )}
        </div>

        {/* Responsible Person Card */}
        <div className="detail-card">
          <h3>Sorumlu Kişi</h3>
          
          {odeme.sorumlu ? (
            <>
              <div className="detail-row">
                <span className="detail-label">Ad Soyad:</span>
                <span className="detail-value">{odeme.sorumlu.ad}</span>
              </div>

              {odeme.sorumlu.email && (
                <div className="detail-row">
                  <span className="detail-label">E-posta:</span>
                  <span className="detail-value">{odeme.sorumlu.email}</span>
                </div>
              )}

              {odeme.sorumlu.telefon && (
                <div className="detail-row">
                  <span className="detail-label">Telefon:</span>
                  <span className="detail-value">{odeme.sorumlu.telefon}</span>
                </div>
              )}

              <div className="detail-row">
                <span className="detail-label">Sorumlu ID:</span>
                <span className="detail-value">#{odeme.sorumlu.id}</span>
              </div>

              <button
                className="btn btn-link"
                onClick={() => navigate(`/sorumlu/${odeme.sorumlu.id}`)}
              >
                → Sorumlu Detayına Git
              </button>
            </>
          ) : (
            <p className="no-data">Sorumlu kişi atanmamış</p>
          )}
        </div>

        {/* Timestamps Card */}
        <div className="detail-card">
          <h3>Zaman Bilgileri</h3>
          
          <div className="detail-row">
            <span className="detail-label">Oluşturulma Tarihi:</span>
            <span className="detail-value">
              {formatDate(odeme.createdAt)}
            </span>
          </div>

          <div className="detail-row">
            <span className="detail-label">Son Güncellenme:</span>
            <span className="detail-value">
              {formatDate(odeme.updatedAt)}
            </span>
          </div>

          {odeme.isDeleted && (
            <div className="detail-row">
              <span className="detail-label">Durum:</span>
              <span className="badge badge-danger">Silinmiş</span>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default PaymentDetail;

