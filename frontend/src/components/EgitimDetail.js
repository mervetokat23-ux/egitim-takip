import React, { useState, useEffect } from 'react';
import { useNavigate, useParams, Link } from 'react-router-dom';
import { egitimAPI, odemeAPI } from '../services/api';
import { format } from 'date-fns';
import Navbar from './Navbar';
import './EgitimDetail.css';

function EgitimDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [egitim, setEgitim] = useState(null);
  const [odemeler, setOdemeler] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const user = JSON.parse(localStorage.getItem('user') || '{}');
  const canEdit = user.rol === 'ADMIN' || user.rol === 'SORUMLU';

  useEffect(() => {
    loadEgitim();
    loadOdemeler();
  }, [id]);

  const loadEgitim = async () => {
    try {
      const response = await egitimAPI.getById(id);
      setEgitim(response.data);
    } catch (err) {
      console.error('Eğitim yükleme hatası:', err);
      setError('Eğitim detayları alınamadı.');
    } finally {
      setLoading(false);
    }
  };

  const loadOdemeler = async () => {
    try {
      const response = await odemeAPI.getAll({ egitimId: id, size: 100 });
      setOdemeler(response.data.content || []);
    } catch (err) {
      console.error('Ödemeler yüklenirken hata:', err);
    }
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return '-';
    return format(new Date(dateStr), 'dd.MM.yyyy');
  };

  const formatTime = (minutes) => {
    if (!minutes) return '-';
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return `${hours} saat ${mins} dakika`;
  };

  const formatCurrency = (amount) => {
    if (!amount) return '-';
    return new Intl.NumberFormat('tr-TR', {
      style: 'currency',
      currency: 'TRY'
    }).format(amount);
  };

  const getDurumBadgeClass = (durum) => {
    if (!durum) return 'badge-secondary';
    
    switch (durum) {
      case 'Havuz':
        return 'badge-secondary';
      case 'Teslim':
        return 'badge-info';
      case 'Gerçekleşme':
        return 'badge-primary';
      case 'Tamamlanan':
        return 'badge-success';
      case 'Kontrol':
        return 'badge-warning';
      case 'Medya':
        return 'badge-info';
      case 'LMS':
        return 'badge-primary';
      case 'Plan':
        return 'badge-warning';
      case 'İptal':
        return 'badge-danger';
      case 'Yayından Kaldırılan':
        return 'badge-danger';
      case 'Anlaşma':
        return 'badge-success';
      case 'İlan':
        return 'badge-info';
      default:
        return 'badge-secondary';
    }
  };

  if (loading) {
    return (
      <div className="egitim-detail-page">
        <Navbar />
        <div className="container">
          <div className="loading">Yükleniyor...</div>
        </div>
      </div>
    );
  }

  if (error || !egitim) {
    return (
      <div className="egitim-detail-page">
        <Navbar />
        <div className="container">
          <div className="error-message">{error || 'Eğitim bulunamadı'}</div>
          <button className="btn btn-secondary" onClick={() => navigate('/egitim')}>
            Geri Dön
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="egitim-detail-page">
      <Navbar />
      <div className="container">
        <div className="detail-header">
          <div className="header-left">
            <button className="back-btn" onClick={() => navigate('/egitim')}>
              ← Geri Dön
            </button>
            <h1>{egitim.ad}</h1>
            <span className={`badge ${getDurumBadgeClass(egitim.durum)}`}>
              {egitim.durum || '-'}
            </span>
          </div>
          {canEdit && (
            <button
              className="btn btn-warning"
              onClick={() => navigate(`/egitim/edit/${id}`)}
            >
              Düzenle
            </button>
          )}
        </div>

        <div className="detail-grid">
          {/* Sol Kolon: Temel Bilgiler */}
          <div className="left-column">
            <div className="detail-card info-card">
              <h3>Eğitim Bilgileri</h3>
              <div className="info-row">
                <label>ID:</label>
                <span>{egitim.id}</span>
              </div>
              {egitim.egitimKodu && (
                <div className="info-row">
                  <label>Eğitim Kodu:</label>
                  <span>{egitim.egitimKodu}</span>
                </div>
              )}
              {egitim.programId && (
                <div className="info-row">
                  <label>Program ID:</label>
                  <span>{egitim.programId}</span>
                </div>
              )}
              {egitim.seviye && (
                <div className="info-row">
                  <label>Seviye:</label>
                  <span className={`badge badge-${egitim.seviye === 'Temel' ? 'success' : egitim.seviye === 'Orta' ? 'warning' : 'danger'}`}>
                    {egitim.seviye}
                  </span>
                </div>
              )}
              {egitim.hedefKitle && (
                <div className="info-row">
                  <label>Hedef Kitle:</label>
                  <div className="hedef-kitle-badges">
                    {egitim.hedefKitle.split(',').map((kitle, index) => (
                      <span key={index} className="badge badge-info" style={{ marginRight: '5px', marginBottom: '5px' }}>
                        {kitle.trim()}
                      </span>
                    ))}
                  </div>
                </div>
              )}
              <div className="info-row">
                <label>Başlangıç Tarihi:</label>
                <span>{formatDate(egitim.baslangicTarihi)}</span>
              </div>
              <div className="info-row">
                <label>Bitiş Tarihi:</label>
                <span>{formatDate(egitim.bitisTarihi)}</span>
              </div>
              <div className="info-row">
                <label>Eğitim Saati:</label>
                <span>{formatTime(egitim.egitimSaati)}</span>
              </div>
              <div className="info-row description-row">
                <label>Açıklama:</label>
                <p>{egitim.aciklama || 'Açıklama yok.'}</p>
              </div>
            </div>

            {/* Kategoriler */}
            <div className="detail-card">
              <h3>Kategoriler ({egitim.kategoriler?.length || 0})</h3>
              {egitim.kategoriler && egitim.kategoriler.length > 0 ? (
                <ul className="relation-list">
                  {egitim.kategoriler.map((kategori) => (
                    <li key={kategori.id}>
                      <strong>{kategori.ad}</strong>
                      {kategori.aciklama && (
                        <div className="sub-text">{kategori.aciklama}</div>
                      )}
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="empty-text">Kategori bulunamadı</p>
              )}
            </div>

            {/* Eğitmenler */}
            <div className="detail-card">
              <h3>Eğitmenler ({egitim.egitmenler?.length || 0})</h3>
              {egitim.egitmenler && egitim.egitmenler.length > 0 ? (
                <ul className="relation-list">
                  {egitim.egitmenler.map((egitmen) => (
                    <li key={egitmen.id}>
                      <Link to={`/egitmen/edit/${egitmen.id}`}>
                        <strong>{egitmen.ad} {egitmen.soyad}</strong>
                      </Link>
                      {egitmen.email && (
                        <div className="sub-text">Email: {egitmen.email}</div>
                      )}
                      {egitmen.uzmanlikAlani && (
                        <div className="sub-text">Uzmanlık: {egitmen.uzmanlikAlani}</div>
                      )}
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="empty-text">Eğitmen bulunamadı</p>
              )}
            </div>
          </div>

          {/* Sağ Kolon: İlişkiler ve Ödemeler */}
          <div className="right-column">
            {/* Sorumlular */}
            <div className="detail-card">
              <h3>Eğitim Sorumluları ({egitim.sorumlular?.length || 0})</h3>
              {egitim.sorumlular && egitim.sorumlular.length > 0 ? (
                <ul className="relation-list">
                  {egitim.sorumlular.map((sorumlu) => (
                    <li key={sorumlu.id}>
                      <Link to={`/sorumlu/edit/${sorumlu.id}`}>
                        <strong>{sorumlu.ad} {sorumlu.soyad}</strong>
                      </Link>
                      {sorumlu.unvan && (
                        <div className="sub-text">Unvan: {sorumlu.unvan}</div>
                      )}
                      {sorumlu.email && (
                        <div className="sub-text">Email: {sorumlu.email}</div>
                      )}
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="empty-text">Sorumlu bulunamadı</p>
              )}
            </div>

            {/* Paydaşlar */}
            <div className="detail-card">
              <h3>Paydaşlar ({egitim.paydaslar?.length || 0})</h3>
              {egitim.paydaslar && egitim.paydaslar.length > 0 ? (
                <ul className="relation-list">
                  {egitim.paydaslar.map((paydas) => (
                    <li key={paydas.id}>
                      <Link to={`/paydas/${paydas.id}`}>
                        <strong>{paydas.ad}</strong>
                      </Link>
                      {paydas.tip && (
                        <span className="badge badge-info" style={{ marginLeft: '10px' }}>
                          {paydas.tip}
                        </span>
                      )}
                      {paydas.email && (
                        <div className="sub-text">Email: {paydas.email}</div>
                      )}
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="empty-text">Paydaş bulunamadı</p>
              )}
            </div>

            {/* Proje */}
            {egitim.proje && (
              <div className="detail-card">
                <h3>Bağlı Proje</h3>
                <ul className="relation-list">
                  <li>
                    <Link to={`/proje/${egitim.proje.id}`}>
                      <strong>{egitim.proje.isim}</strong>
                    </Link>
                  </li>
                </ul>
              </div>
            )}

            {/* Ödemeler */}
            <div className="detail-card">
              <div className="detail-card-header">
                <h3>Ödemeler ({odemeler.length})</h3>
                {canEdit && (
                  <button
                    className="btn btn-sm btn-primary"
                    onClick={() => navigate(`/odeme/new?egitimId=${id}`)}
                  >
                    + Ödeme Ekle
                  </button>
                )}
              </div>
              {odemeler.length > 0 ? (
                <div className="odeme-list">
                  {odemeler.map((odeme) => (
                    <div key={odeme.id} className="odeme-item">
                      <div className="odeme-header">
                        <strong>{odeme.operasyon || 'Ödeme'}</strong>
                        <span className={`badge ${getDurumBadgeClass(odeme.durum)}`}>
                          {odeme.durum || '-'}
                        </span>
                      </div>
                      <div className="odeme-details">
                        {odeme.birimUcret && (
                          <div className="odeme-row">
                            <span>Birim Ücret:</span>
                            <strong>{formatCurrency(odeme.birimUcret)}</strong>
                          </div>
                        )}
                        {odeme.toplamUcret && (
                          <div className="odeme-row">
                            <span>Toplam Ücret:</span>
                            <strong>{formatCurrency(odeme.toplamUcret)}</strong>
                          </div>
                        )}
                        {odeme.odemeKaynagi && (
                          <div className="odeme-row">
                            <span>Ödeme Kaynağı:</span>
                            <span>{odeme.odemeKaynagi}</span>
                          </div>
                        )}
                        {odeme.sorumlu && (
                          <div className="odeme-row">
                            <span>Sorumlu:</span>
                            <Link to={`/sorumlu/edit/${odeme.sorumlu.id}`}>
                              {odeme.sorumlu.ad} {odeme.sorumlu.soyad}
                            </Link>
                          </div>
                        )}
                      </div>
                      {canEdit && (
                        <div className="odeme-actions">
                          <button
                            className="btn btn-sm btn-secondary"
                            onClick={() => navigate(`/odeme/edit/${odeme.id}`)}
                          >
                            Düzenle
                          </button>
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              ) : (
                <p className="empty-text">Henüz ödeme eklenmemiş.</p>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default EgitimDetail;
