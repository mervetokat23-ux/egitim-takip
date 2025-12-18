import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { projeAPI } from '../services/api';
import Navbar from './Navbar';
import { format } from 'date-fns';
import './ProjeList.css';

function ProjeList() {
  const [projeler, setProjeler] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const user = JSON.parse(localStorage.getItem('user') || '{}');
  const isAdmin = user.rol === 'ADMIN';
  const canEdit = isAdmin || user.rol === 'SORUMLU';

  useEffect(() => {
    fetchProjeler();
  }, []);

  const fetchProjeler = async () => {
    try {
      const response = await projeAPI.getAll();
      setProjeler(response.data);
      setLoading(false);
    } catch (err) {
      console.error('Projeler yüklenirken hata:', err);
      const errorMessage = err.response?.data?.message || err.message || 'Proje listesi alınamadı.';
      setError(errorMessage + ' (Backend çalışıyor mu?)');
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Bu projeyi silmek istediğinizden emin misiniz?')) {
      try {
        await projeAPI.delete(id);
        fetchProjeler();
      } catch (err) {
        console.error('Silme hatası:', err);
        alert('Proje silinirken bir hata oluştu.');
      }
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return '-';
    return format(new Date(dateString), 'dd.MM.yyyy');
  };

  if (loading) return <div className="loading">Yükleniyor...</div>;
  if (error) return <div className="error-message">{error}</div>;

  return (
    <div className="proje-list-page">
      <Navbar />
      <div className="container">
        <div className="header-section">
          <h2>Projeler</h2>
          {canEdit && (
            <Link to="/proje/new" className="btn btn-primary">
              + Yeni Proje Ekle
            </Link>
          )}
        </div>

        <div className="table-responsive">
          <table className="table">
            <thead>
              <tr>
                <th>Proje Adı</th>
                <th>Eğitimler</th>
                <th>Başlangıç</th>
                <th>Bitiş/Tarih</th>
                <th>Paydaş</th>
                <th>İşlemler</th>
              </tr>
            </thead>
            <tbody>
              {projeler.map((proje) => (
                <tr key={proje.id}>
                  <td>{proje.isim}</td>
                  <td>
                    {proje.egitimler && proje.egitimler.length > 0 
                      ? proje.egitimler.map(e => e.ad).join(', ') 
                      : '-'}
                  </td>
                  <td>{formatDate(proje.baslangicTarihi)}</td>
                  <td>{formatDate(proje.tarih)}</td>
                  <td>{proje.paydas ? proje.paydas.ad : '-'}</td>
                  <td>
                    <button
                      className="btn btn-sm btn-info"
                      onClick={() => navigate(`/proje/${proje.id}`)}
                      style={{ marginRight: '5px' }}
                    >
                      Detay
                    </button>
                    {canEdit && (
                      <button
                        className="btn btn-sm btn-warning"
                        onClick={() => navigate(`/proje/edit/${proje.id}`)}
                        style={{ marginRight: '5px' }}
                      >
                        Düzenle
                      </button>
                    )}
                    {isAdmin && (
                      <button
                        className="btn btn-sm btn-danger"
                        onClick={() => handleDelete(proje.id)}
                      >
                        Sil
                      </button>
                    )}
                  </td>
                </tr>
              ))}
              {projeler.length === 0 && (
                <tr>
                  <td colSpan={6} className="text-center">
                    Henüz kayıtlı proje yok.
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

export default ProjeList;

