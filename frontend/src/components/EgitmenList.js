import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { egitmenAPI } from '../services/api';
import Navbar from './Navbar';
import './EgitmenList.css';

function EgitmenList() {
  const [egitmenler, setEgitmenler] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const user = JSON.parse(localStorage.getItem('user') || '{}');
  const isAdmin = user.rol === 'ADMIN';
  const canEdit = isAdmin || user.rol === 'SORUMLU';

  useEffect(() => {
    fetchEgitmenler();
  }, []);

  const fetchEgitmenler = async () => {
    try {
      // Tüm listeyi çekiyoruz (sayfalama istenirse getPage kullanılabilir)
      const response = await egitmenAPI.getAll();
      setEgitmenler(response.data);
      setLoading(false);
    } catch (err) {
      console.error('Eğitmenler yüklenirken hata:', err);
      const errorMessage = err.response?.data?.message || err.message || 'Eğitmen listesi alınamadı.';
      setError(errorMessage + ' (Backend çalışıyor mu?)');
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Bu eğitmeni silmek istediğinizden emin misiniz?')) {
      try {
        await egitmenAPI.delete(id);
        fetchEgitmenler(); // Listeyi yenile
      } catch (err) {
        console.error('Silme hatası:', err);
        alert('Eğitmen silinirken bir hata oluştu.');
      }
    }
  };

  if (loading) return <div className="loading">Yükleniyor...</div>;
  if (error) return <div className="error-message">{error}</div>;

  return (
    <div className="egitmen-list-page">
      <Navbar />
      <div className="container">
        <div className="header-section">
          <h2>Eğitmenler</h2>
          {canEdit && (
            <Link to="/egitmen/new" className="btn btn-primary">
              + Yeni Eğitmen Ekle
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
                <th>İl</th>
                <th>Çalışma Yeri</th>
                {canEdit && <th>İşlemler</th>}
              </tr>
            </thead>
            <tbody>
              {egitmenler.map((egitmen) => (
                <tr key={egitmen.id}>
                  <td>{egitmen.ad} {egitmen.soyad}</td>
                  <td>{egitmen.email}</td>
                  <td>{egitmen.telefon}</td>
                  <td>{egitmen.il || '-'}</td>
                  <td>{egitmen.calismaYeri || '-'}</td>
                  {canEdit && (
                    <td>
                      <button
                        className="btn btn-sm btn-warning"
                        onClick={() => navigate(`/egitmen/edit/${egitmen.id}`)}
                        style={{ marginRight: '5px' }}
                      >
                        Düzenle
                      </button>
                      {isAdmin && (
                        <button
                          className="btn btn-sm btn-danger"
                          onClick={() => handleDelete(egitmen.id)}
                        >
                          Sil
                        </button>
                      )}
                    </td>
                  )}
                </tr>
              ))}
              {egitmenler.length === 0 && (
                <tr>
                  <td colSpan={canEdit ? 6 : 5} className="text-center">
                    Henüz kayıtlı eğitmen yok.
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

export default EgitmenList;

