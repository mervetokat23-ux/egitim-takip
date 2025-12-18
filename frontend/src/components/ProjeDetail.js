import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { projeAPI } from '../services/api';
import Navbar from './Navbar';
import { format } from 'date-fns';
import './ProjeDetail.css';

function ProjeDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [proje, setProje] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const user = JSON.parse(localStorage.getItem('user') || '{}');
  const canEdit = user.rol === 'ADMIN' || user.rol === 'SORUMLU';

  useEffect(() => {
    const fetchDetail = async () => {
      try {
        const response = await projeAPI.getById(id);
        setProje(response.data);
        setLoading(false);
      } catch (err) {
        console.error('Detay yükleme hatası:', err);
        setError('Proje detayları alınamadı.');
        setLoading(false);
      }
    };

    fetchDetail();
  }, [id]);

  const formatDate = (dateString) => {
    if (!dateString) return '-';
    return format(new Date(dateString), 'dd.MM.yyyy');
  };

  if (loading) return <div className="loading">Yükleniyor...</div>;
  if (error) return <div className="error-message">{error}</div>;
  if (!proje) return <div className="error-message">Proje bulunamadı.</div>;

  return (
    <div className="proje-detail-page">
      <Navbar />
      <div className="container">
        <div className="detail-header">
          <div className="header-left">
            <button className="back-btn" onClick={() => navigate('/proje')}>
              ← Geri Dön
            </button>
            <h1>{proje.isim}</h1>
          </div>
          {canEdit && (
            <button 
              className="btn btn-warning"
              onClick={() => navigate(`/proje/edit/${proje.id}`)}
            >
              Düzenle
            </button>
          )}
        </div>

        <div className="detail-grid">
          {/* Sol Kolon: Temel Bilgiler */}
          <div className="left-column">
            <div className="detail-card info-card">
              <h3>Proje Bilgileri</h3>
              <div className="info-row">
                <label>Başlangıç:</label>
                <span>{formatDate(proje.baslangicTarihi)}</span>
              </div>
              <div className="info-row">
                <label>Bitiş:</label>
                <span>{formatDate(proje.tarih)}</span>
              </div>
              <div className="info-row description-row">
                <label>Hakkında:</label>
                <p>{proje.projeHakkinda || 'Açıklama yok.'}</p>
              </div>
            </div>

          </div>

          {/* Sağ Kolon: İlişkiler */}
          <div className="right-column">
            {/* Sorumlu ve Paydaş */}
            <div className="detail-card">
              <h3>İlgili Kişiler</h3>
              <div className="info-row">
                <label>Sorumlu:</label>
                <span>
                  {proje.egitimSorumlu ? (
                    <Link to={`/sorumlu/edit/${proje.egitimSorumlu.id}`}>
                      {proje.egitimSorumlu.ad} {proje.egitimSorumlu.soyad}
                    </Link>
                  ) : '-'}
                </span>
              </div>
              <div className="info-row">
                <label>Paydaş:</label>
                <span>
                  {proje.paydas ? (
                    <Link to={`/paydas/${proje.paydas.id}`}>
                      {proje.paydas.ad}
                    </Link>
                  ) : '-'}
                </span>
              </div>
            </div>

            {/* Faaliyetler */}
            <div className="detail-card">
              <div className="detail-card-header">
                <h3>Faaliyetler ({proje.faaliyetler?.length || 0})</h3>
                {canEdit && (
                  <button
                    className="btn btn-sm btn-primary"
                    onClick={() => navigate(`/faaliyet/new?projeId=${proje.id}`)}
                  >
                    + Faaliyet Ekle
                  </button>
                )}
              </div>
              {proje.faaliyetler && proje.faaliyetler.length > 0 ? (
                <ul className="relation-list">
                  {proje.faaliyetler.map(faaliyet => (
                    <li key={faaliyet.id}>
                      <div className="faaliyet-item">
                        <strong
                          onClick={() => navigate(`/faaliyet/edit/${faaliyet.id}`)}
                          style={{ cursor: 'pointer', color: '#007bff' }}
                        >
                          {faaliyet.isim}
                        </strong>
                        <span className="faaliyet-meta">
                          {formatDate(faaliyet.tarih)} - {faaliyet.turu}
                        </span>
                      </div>
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="empty-text">Henüz faaliyet eklenmemiş.</p>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ProjeDetail;

