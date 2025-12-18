import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { egitimAPI } from '../services/api';
import { format } from 'date-fns';
import Navbar from './Navbar';
import { useAuth } from '../context/AuthContext';
import useEventLogger from '../hooks/useEventLogger';
import './EgitimList.css';

function EgitimList() {
  const { hasPermission } = useAuth();
  const { logButtonClick } = useEventLogger();
  const [egitimler, setEgitimler] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [filters, setFilters] = useState({
    durum: '',
    yil: '',
  });
  const navigate = useNavigate();

  // Permission checks
  const canCreate = hasPermission('education', 'create');
  const canUpdate = hasPermission('education', 'update');
  const canDelete = hasPermission('education', 'delete');

  useEffect(() => {
    loadEgitimler();
  }, [page, filters]);

  const loadEgitimler = async () => {
    setLoading(true);
    setError('');
    try {
      const params = {
        page,
        size,
        sort: 'id,desc',
        ...filters,
      };
      const response = await egitimAPI.getAll(params);
      setEgitimler(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (err) {
      setError('Eğitimler yüklenirken hata oluştu');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!canDelete) {
      alert('Bu işlem için yetkiniz bulunmamaktadır.');
      return;
    }
    
    if (!window.confirm('Bu eğitimi silmek istediğinize emin misiniz?')) {
      return;
    }
    try {
      logButtonClick('Eğitim Sil Button', `Eğitim ID: ${id}`);
      await egitimAPI.delete(id);
      loadEgitimler();
    } catch (err) {
      alert('Silme işlemi başarısız');
    }
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

  return (
    <div>
      <Navbar />
      <div className="container">
        <div className="page-header">
          <h1>Eğitim Yönetimi</h1>
          {canCreate && (
            <button 
              className="btn btn-primary" 
              onClick={() => {
                logButtonClick('Yeni Eğitim Ekle Button');
                navigate('/egitim/new');
              }}
            >
              Yeni Eğitim Ekle
            </button>
          )}
        </div>

        {error && <div className="error">{error}</div>}

        <div className="card">
          <div className="filters">
            <div className="form-group">
              <label>Durum</label>
              <select
                className="form-control"
                value={filters.durum}
                onChange={(e) => setFilters({ ...filters, durum: e.target.value })}
              >
                <option value="">Tümü</option>
                <option value="Havuz">Havuz</option>
                <option value="Teslim">Teslim</option>
                <option value="Gerçekleşme">Gerçekleşme</option>
                <option value="Tamamlanan">Tamamlanan</option>
                <option value="Kontrol">Kontrol</option>
                <option value="Medya">Medya</option>
                <option value="LMS">LMS</option>
                <option value="Plan">Plan</option>
                <option value="İptal">İptal</option>
                <option value="Yayından Kaldırılan">Yayından Kaldırılan</option>
                <option value="Anlaşma">Anlaşma</option>
                <option value="İlan">İlan</option>
              </select>
            </div>
            <div className="form-group">
              <label>Yıl</label>
              <input
                type="number"
                className="form-control"
                placeholder="2024"
                value={filters.yil}
                onChange={(e) => setFilters({ ...filters, yil: e.target.value })}
              />
            </div>
          </div>
        </div>

        {loading ? (
          <div className="loading">Yükleniyor...</div>
        ) : (
          <>
            <div className="card">
              <table className="table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Ad</th>
                    <th>Eğitim Kodu</th>
                    <th>Program ID</th>
                    <th>Seviye</th>
                    <th>Başlangıç Tarihi</th>
                    <th>Bitiş Tarihi</th>
                    <th>Eğitim Saati</th>
                    <th>Durum</th>
                    {(canUpdate || canDelete) && <th>İşlemler</th>}
                  </tr>
                </thead>
                <tbody>
                  {egitimler.length === 0 ? (
                    <tr>
                      <td colSpan={canUpdate || canDelete ? "10" : "9"} style={{ textAlign: 'center' }}>
                        Eğitim bulunamadı
                      </td>
                    </tr>
                  ) : (
                    egitimler.map((egitim) => (
                      <tr key={egitim.id}>
                        <td>{egitim.id}</td>
                        <td>
                          <a
                            href="#"
                            onClick={(e) => {
                              e.preventDefault();
                              navigate(`/egitim/${egitim.id}`);
                            }}
                            style={{ color: '#007bff', textDecoration: 'none' }}
                          >
                            {egitim.ad}
                          </a>
                        </td>
                        <td>{egitim.egitimKodu || '-'}</td>
                        <td>{egitim.programId || '-'}</td>
                        <td>
                          {egitim.seviye ? (
                            <span className={`badge ${egitim.seviye === 'Temel' ? 'badge-success' : egitim.seviye === 'Orta' ? 'badge-warning' : 'badge-danger'}`}>
                              {egitim.seviye}
                            </span>
                          ) : '-'}
                        </td>
                        <td>
                          {egitim.baslangicTarihi
                            ? format(new Date(egitim.baslangicTarihi), 'dd.MM.yyyy')
                            : '-'}
                        </td>
                        <td>
                          {egitim.bitisTarihi
                            ? format(new Date(egitim.bitisTarihi), 'dd.MM.yyyy')
                            : '-'}
                        </td>
                        <td>
                          {egitim.egitimSaati
                            ? `${Math.floor(egitim.egitimSaati / 60)} saat ${egitim.egitimSaati % 60} dakika`
                            : '-'}
                        </td>
                        <td>
                          <span className={`badge ${getDurumBadgeClass(egitim.durum)}`}>
                            {egitim.durum || '-'}
                          </span>
                        </td>
                        {(canUpdate || canDelete) && (
                          <td>
                            {canUpdate && (
                              <button
                                className="btn btn-secondary"
                                style={{ marginRight: '5px', fontSize: '12px', padding: '5px 10px' }}
                                onClick={() => navigate(`/egitim/edit/${egitim.id}`)}
                              >
                                Düzenle
                              </button>
                            )}
                            {canDelete && (
                              <button
                                className="btn btn-danger"
                                style={{ fontSize: '12px', padding: '5px 10px' }}
                                onClick={() => handleDelete(egitim.id)}
                              >
                                Sil
                              </button>
                            )}
                          </td>
                        )}
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>

            {totalPages > 1 && (
              <div className="pagination">
                <button
                  onClick={() => setPage(0)}
                  disabled={page === 0}
                >
                  İlk
                </button>
                <button
                  onClick={() => setPage(page - 1)}
                  disabled={page === 0}
                >
                  Önceki
                </button>
                <span>
                  Sayfa {page + 1} / {totalPages}
                </span>
                <button
                  onClick={() => setPage(page + 1)}
                  disabled={page >= totalPages - 1}
                >
                  Sonraki
                </button>
                <button
                  onClick={() => setPage(totalPages - 1)}
                  disabled={page >= totalPages - 1}
                >
                  Son
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}

export default EgitimList;
