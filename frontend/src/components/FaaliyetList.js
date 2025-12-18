import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { faaliyetAPI } from '../services/api';
import Navbar from './Navbar';
import { format } from 'date-fns';
import './FaaliyetList.css';

function FaaliyetList() {
  const [faaliyetler, setFaaliyetler] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const user = JSON.parse(localStorage.getItem('user') || '{}');
  const isAdmin = user.rol === 'ADMIN';
  const canEdit = isAdmin || user.rol === 'SORUMLU';

  useEffect(() => {
    fetchFaaliyetler();
  }, []);

  const fetchFaaliyetler = async () => {
    try {
      const response = await faaliyetAPI.getAll({ page: 0, size: 50, sort: 'id,desc' });
      const page = response.data;
      setFaaliyetler(page.content || page);
      setLoading(false);
    } catch (err) {
      console.error('Faaliyetler yüklenirken hata:', err);
      const errorMessage = err.response?.data?.message || err.message || 'Faaliyet listesi alınamadı.';
      setError(errorMessage + ' (Backend çalışıyor mu?)');
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Bu faaliyeti silmek istediğinizden emin misiniz?')) return;
    try {
      await faaliyetAPI.delete(id);
      fetchFaaliyetler();
    } catch (err) {
      console.error('Silme hatası:', err);
      alert('Faaliyet silinirken bir hata oluştu.');
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return '-';
    return format(new Date(dateString), 'dd.MM.yyyy');
  };

  if (loading) return <div className="loading">Yükleniyor...</div>;
  if (error) return <div className="error-message">{error}</div>;

  return (
    <div className="faaliyet-list-page">
      <Navbar />
      <div className="container">
        <div className="header-section">
          <h2>Faaliyetler</h2>
          {canEdit && (
            <Link to="/faaliyet/new" className="btn btn-primary">
              + Yeni Faaliyet Ekle
            </Link>
          )}
        </div>

        <div className="table-responsive">
          <table className="table">
            <thead>
              <tr>
                <th>Tarih</th>
                <th>İsim</th>
                <th>Türü</th>
                <th>Proje</th>
                <th>Sorumlular</th>
                <th>İşlemler</th>
              </tr>
            </thead>
            <tbody>
              {faaliyetler.map((f) => (
                <tr key={f.id}>
                  <td>{formatDate(f.tarih)}</td>
                  <td>{f.isim}</td>
                  <td>{f.turu || '-'}</td>
                  <td>{f.proje ? f.proje.isim : '-'}</td>
                  <td>
                    {f.sorumlular && f.sorumlular.length > 0
                      ? f.sorumlular.map((s) => `${s.ad} ${s.soyad}`).join(', ')
                      : '-'}
                  </td>
                  <td>
                    {canEdit && (
                      <button
                        className="btn btn-sm btn-warning"
                        onClick={() => navigate(`/faaliyet/edit/${f.id}`)}
                        style={{ marginRight: '5px' }}
                      >
                        Düzenle
                      </button>
                    )}
                    {isAdmin && (
                      <button
                        className="btn btn-sm btn-danger"
                        onClick={() => handleDelete(f.id)}
                      >
                        Sil
                      </button>
                    )}
                  </td>
                </tr>
              ))}
              {faaliyetler.length === 0 && (
                <tr>
                  <td colSpan={6} className="text-center">
                    Henüz kayıtlı faaliyet yok.
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

export default FaaliyetList;




