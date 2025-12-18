import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { kategoriAPI } from '../services/api';
import Navbar from './Navbar';
import './KategoriList.css';

function KategoriList() {
  const [kategoriler, setKategoriler] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const user = JSON.parse(localStorage.getItem('user') || '{}');
  const isAdmin = user.rol === 'ADMIN';
  const canEdit = isAdmin || user.rol === 'SORUMLU';

  useEffect(() => {
    fetchKategoriler();
  }, []);

  const fetchKategoriler = async () => {
    try {
      const response = await kategoriAPI.getAll();
      setKategoriler(response.data);
      setLoading(false);
    } catch (err) {
      console.error('Kategoriler yüklenirken hata:', err);
      const errorMessage = err.response?.data?.message || err.message || 'Kategori listesi alınamadı.';
      setError(errorMessage + ' (Backend çalışıyor mu?)');
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Bu kategoriyi silmek istediğinizden emin misiniz? Alt kategorileri varsa onlar da silinebilir.')) {
      try {
        await kategoriAPI.delete(id);
        fetchKategoriler();
      } catch (err) {
        console.error('Silme hatası:', err);
        alert('Kategori silinirken bir hata oluştu.');
      }
    }
  };

  if (loading) return <div className="loading">Yükleniyor...</div>;
  if (error) return <div className="error-message">{error}</div>;

  return (
    <div className="kategori-list-page">
      <Navbar />
      <div className="container">
        <div className="header-section">
          <h2>Kategoriler</h2>
          {canEdit && (
            <Link to="/kategori/new" className="btn btn-primary">
              + Yeni Kategori Ekle
            </Link>
          )}
        </div>

        <div className="table-responsive">
          <table className="table">
            <thead>
              <tr>
                <th>Ad</th>
                <th>Açıklama</th>
                <th>Üst Kategori</th>
                {canEdit && <th>İşlemler</th>}
              </tr>
            </thead>
            <tbody>
              {kategoriler.map((kategori) => (
                <tr key={kategori.id}>
                  <td>{kategori.ad}</td>
                  <td>{kategori.aciklama}</td>
                  <td>{kategori.ustKategoriAd || '-'}</td>
                  {canEdit && (
                    <td>
                      <button
                        className="btn btn-sm btn-warning"
                        onClick={() => navigate(`/kategori/edit/${kategori.id}`)}
                        style={{ marginRight: '5px' }}
                      >
                        Düzenle
                      </button>
                      {isAdmin && (
                        <button
                          className="btn btn-sm btn-danger"
                          onClick={() => handleDelete(kategori.id)}
                        >
                          Sil
                        </button>
                      )}
                    </td>
                  )}
                </tr>
              ))}
              {kategoriler.length === 0 && (
                <tr>
                  <td colSpan={canEdit ? 4 : 3} className="text-center">
                    Henüz kayıtlı kategori yok.
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

export default KategoriList;

