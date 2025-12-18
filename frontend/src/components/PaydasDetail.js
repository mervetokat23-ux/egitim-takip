import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { paydasAPI } from '../services/api';
import Navbar from './Navbar';
import './PaydasDetail.css';

function PaydasDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [paydas, setPaydas] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const user = JSON.parse(localStorage.getItem('user') || '{}');
  const canEdit = user.rol === 'ADMIN' || user.rol === 'SORUMLU';

  useEffect(() => {
    const fetchDetail = async () => {
      try {
        const response = await paydasAPI.getById(id);
        setPaydas(response.data);
        setLoading(false);
      } catch (err) {
        console.error('Detay yükleme hatası:', err);
        setError('Paydaş detayları alınamadı.');
        setLoading(false);
      }
    };

    fetchDetail();
  }, [id]);

  if (loading) return <div className="loading">Yükleniyor...</div>;
  if (error) return <div className="error-message">{error}</div>;
  if (!paydas) return <div className="error-message">Paydaş bulunamadı.</div>;

  return (
    <div className="paydas-detail-page">
      <Navbar />
      <div className="container">
        <div className="detail-header">
          <div className="header-left">
            <button className="back-btn" onClick={() => navigate('/paydas')}>
              ← Geri Dön
            </button>
            <h1>{paydas.ad}</h1>
            <span className="badge badge-primary">{paydas.tip}</span>
          </div>
          {canEdit && (
            <button 
              className="btn btn-warning"
              onClick={() => navigate(`/paydas/edit/${paydas.id}`)}
            >
              Düzenle
            </button>
          )}
        </div>

        <div className="detail-grid">
          {/* Sol Kolon: Temel Bilgiler */}
          <div className="detail-card info-card">
            <h3>İletişim Bilgileri</h3>
            <div className="info-row">
              <label>Email:</label>
              <span>{paydas.email || '-'}</span>
            </div>
            <div className="info-row">
              <label>Telefon:</label>
              <span>{paydas.telefon || '-'}</span>
            </div>
            <div className="info-row">
              <label>Adres:</label>
              <span>{paydas.adres || '-'}</span>
            </div>
          </div>

          {/* Sağ Kolon: İlişkiler */}
          <div className="relations-column">
            {/* İlişkili Eğitimler */}
            <div className="detail-card">
              <h3>İlişkili Eğitimler ({paydas.egitimler?.length || 0})</h3>
              {paydas.egitimler && paydas.egitimler.length > 0 ? (
                <ul className="relation-list">
                  {paydas.egitimler.map(egitim => (
                    <li key={egitim.id}>
                      <Link to={`/egitim/${egitim.id}`}>{egitim.ad}</Link>
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="empty-text">Henüz bir eğitimle ilişkilendirilmemiş.</p>
              )}
            </div>

            {/* İlişkili Projeler */}
            <div className="detail-card">
              <h3>İlişkili Projeler ({paydas.projeler?.length || 0})</h3>
              {paydas.projeler && paydas.projeler.length > 0 ? (
                <ul className="relation-list">
                  {paydas.projeler.map(proje => (
                    <li key={proje.id}>
                      <Link to={`/proje/${proje.id}`}>{proje.isim}</Link>
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="empty-text">Henüz bir projede yer almıyor.</p>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default PaydasDetail;

