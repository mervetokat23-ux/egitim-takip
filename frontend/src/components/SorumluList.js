import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { sorumluAPI } from '../services/api';
import Navbar from './Navbar';
import './SorumluList.css';

function SorumluList() {
  const [sorumlular, setSorumlular] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const user = JSON.parse(localStorage.getItem('user') || '{}');
  const isAdmin = user.rol === 'ADMIN';
  const canEdit = isAdmin || user.rol === 'SORUMLU';

  useEffect(() => {
    fetchSorumlular();
  }, []);

  const fetchSorumlular = async () => {
    try {
      const response = await sorumluAPI.getAll();
      setSorumlular(response.data);
      setLoading(false);
    } catch (err) {
      console.error('Sorumlular yüklenirken hata:', err);
      const errorMessage = err.response?.data?.message || err.message || 'Sorumlu listesi alınamadı.';
      setError(errorMessage + ' (Backend çalışıyor mu?)');
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Bu sorumluyu silmek istediğinizden emin misiniz?')) {
      try {
        await sorumluAPI.delete(id);
        fetchSorumlular();
      } catch (err) {
        console.error('Silme hatası:', err);
        alert('Sorumlu silinirken bir hata oluştu.');
      }
    }
  };

  if (loading) return <div className="loading">Yükleniyor...</div>;
  if (error) return <div className="error-message">{error}</div>;

  return (
    <div className="sorumlu-list-page">
      <Navbar />
      <div className="container">
        <div className="header-section">
          <h2>Eğitim Sorumluları</h2>
          {canEdit && (
            <Link to="/sorumlu/new" className="btn btn-primary">
              + Yeni Sorumlu Ekle
            </Link>
          )}
        </div>

        <div className="table-responsive">
          <table className="table">
            <thead>
              <tr>
                <th>Ad Soyad</th>
                <th>Email</th>
                <th>Telefon</th>
                <th>Ünvanlar</th>
                {canEdit && <th>İşlemler</th>}
              </tr>
            </thead>
            <tbody>
              {sorumlular.map((sorumlu) => (
                <tr key={sorumlu.id}>
                  <td>{sorumlu.ad} {sorumlu.soyad}</td>
                  <td>{sorumlu.email}</td>
                  <td>{sorumlu.telefon}</td>
                  <td>
                    {sorumlu.unvanlar && sorumlu.unvanlar.length > 0 ? (
                      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '4px' }}>
                        {sorumlu.unvanlar.map((unvan, idx) => (
                          <span 
                            key={idx}
                            style={{
                              backgroundColor: '#e3f2fd',
                              color: '#1976d2',
                              padding: '2px 8px',
                              borderRadius: '12px',
                              fontSize: '12px',
                              whiteSpace: 'nowrap'
                            }}
                          >
                            {unvan}
                          </span>
                        ))}
                      </div>
                    ) : '-'}
                  </td>
                  {canEdit && (
                    <td>
                      <button
                        className="btn btn-sm btn-warning"
                        onClick={() => navigate(`/sorumlu/edit/${sorumlu.id}`)}
                        style={{ marginRight: '5px' }}
                      >
                        Düzenle
                      </button>
                      {isAdmin && (
                        <button
                          className="btn btn-sm btn-danger"
                          onClick={() => handleDelete(sorumlu.id)}
                        >
                          Sil
                        </button>
                      )}
                    </td>
                  )}
                </tr>
              ))}
              {sorumlular.length === 0 && (
                <tr>
                  <td colSpan={canEdit ? 5 : 4} className="text-center">
                    Henüz kayıtlı sorumlu yok.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

export default SorumluList;

