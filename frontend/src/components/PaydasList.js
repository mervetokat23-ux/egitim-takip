import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { paydasAPI } from '../services/api';
import Navbar from './Navbar';
import './PaydasList.css';

function PaydasList() {
  const [paydaslar, setPaydaslar] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const user = JSON.parse(localStorage.getItem('user') || '{}');
  const isAdmin = user.rol === 'ADMIN';
  const canEdit = isAdmin || user.rol === 'SORUMLU';

  useEffect(() => {
    fetchPaydaslar();
  }, []);

  const fetchPaydaslar = async () => {
    try {
      const response = await paydasAPI.getAll();
      setPaydaslar(response.data);
      setLoading(false);
    } catch (err) {
      console.error('Paydaşlar yüklenirken hata:', err);
      const errorMessage = err.response?.data?.message || err.message || 'Paydaş listesi alınamadı.';
      setError(errorMessage + ' (Backend çalışıyor mu?)');
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Bu paydaşı silmek istediğinizden emin misiniz?')) {
      try {
        await paydasAPI.delete(id);
        fetchPaydaslar();
      } catch (err) {
        console.error('Silme hatası:', err);
        alert('Paydaş silinirken bir hata oluştu.');
      }
    }
  };

  if (loading) return <div className="loading">Yükleniyor...</div>;
  if (error) return <div className="error-message">{error}</div>;

  return (
    <div className="paydas-list-page">
      <Navbar />
      <div className="container">
        <div className="header-section">
          <h2>Paydaşlar</h2>
          {canEdit && (
            <Link to="/paydas/new" className="btn btn-primary">
              + Yeni Paydaş Ekle
            </Link>
          )}
        </div>

        <div className="table-responsive">
          <table className="table">
            <thead>
              <tr>
                <th>Ad</th>
                <th>Tip</th>
                <th>Email</th>
                <th>Telefon</th>
                <th>İşlemler</th>
              </tr>
            </thead>
            <tbody>
              {paydaslar.map((paydas) => (
                <tr key={paydas.id}>
                  <td>{paydas.ad}</td>
                  <td>
                    <span className="badge badge-info">{paydas.tip || '-'}</span>
                  </td>
                  <td>{paydas.email}</td>
                  <td>{paydas.telefon}</td>
                  <td>
                    <button
                      className="btn btn-sm btn-info"
                      onClick={() => navigate(`/paydas/${paydas.id}`)}
                      style={{ marginRight: '5px' }}
                    >
                      Detay
                    </button>
                    {canEdit && (
                      <button
                        className="btn btn-sm btn-warning"
                        onClick={() => navigate(`/paydas/edit/${paydas.id}`)}
                        style={{ marginRight: '5px' }}
                      >
                        Düzenle
                      </button>
                    )}
                    {isAdmin && (
                      <button
                        className="btn btn-sm btn-danger"
                        onClick={() => handleDelete(paydas.id)}
                      >
                        Sil
                      </button>
                    )}
                  </td>
                </tr>
              ))}
              {paydaslar.length === 0 && (
                <tr>
                  <td colSpan={5} className="text-center">
                    Henüz kayıtlı paydaş yok.
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

export default PaydasList;

