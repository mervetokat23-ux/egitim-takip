import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../Navbar';
import api from '../../services/api';
import './Admin.css';

function RoleList() {
  const [roles, setRoles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    fetchRoles();
  }, []);

  const fetchRoles = async () => {
    try {
      setLoading(true);
      const response = await api.get('/roles');
      setRoles(response.data);
    } catch (err) {
      setError('Roller yüklenirken hata oluştu: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Bu rolü silmek istediğinizden emin misiniz?')) {
      return;
    }

    try {
      await api.delete(`/roles/${id}`);
      alert('Rol başarıyla silindi');
      fetchRoles();
    } catch (err) {
      alert('Rol silinirken hata oluştu: ' + (err.response?.data?.message || err.message));
    }
  };

  if (loading) {
    return (
      <div>
        <Navbar />
        <div className="container" style={{ padding: '20px', textAlign: 'center' }}>
          Yükleniyor...
        </div>
      </div>
    );
  }

  return (
    <div>
      <Navbar />
      <div className="container" style={{ padding: '20px' }}>
        <div className="page-header">
          <h1>Rol Yönetimi</h1>
          <button className="btn btn-primary" onClick={() => navigate('/admin/roles/create')}>
            Yeni Rol Ekle
          </button>
        </div>

        {error && <div className="alert alert-danger">{error}</div>}

        <div className="admin-card">
          <table className="admin-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Rol Adı</th>
                <th>Açıklama</th>
                <th>İzin Sayısı</th>
                <th>Oluşturma Tarihi</th>
                <th>İşlemler</th>
              </tr>
            </thead>
            <tbody>
              {roles.length === 0 ? (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center', padding: '20px' }}>
                    Henüz rol bulunmamaktadır.
                  </td>
                </tr>
              ) : (
                roles.map((role) => (
                  <tr key={role.id}>
                    <td>{role.id}</td>
                    <td><strong>{role.name}</strong></td>
                    <td>{role.description || '-'}</td>
                    <td>{role.permissions?.length || 0}</td>
                    <td>{new Date(role.createdAt).toLocaleDateString('tr-TR')}</td>
                    <td>
                      <div className="action-buttons">
                        <button
                          className="btn btn-sm btn-info"
                          onClick={() => navigate(`/admin/roles/${role.id}/edit`)}
                          title="Düzenle"
                        >
                          👁️
                        </button>
                        <button
                          className="btn btn-sm btn-warning"
                          onClick={() => navigate(`/admin/roles/${role.id}/edit`)}
                          title="Düzenle"
                        >
                          ✏️
                        </button>
                        <button
                          className="btn btn-sm btn-danger"
                          onClick={() => handleDelete(role.id)}
                          title="Sil"
                          disabled={role.name === 'ADMIN'} // Prevent deleting ADMIN role
                        >
                          🗑️
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        <div style={{ marginTop: '20px' }}>
          <button className="btn btn-secondary" onClick={() => navigate('/admin')}>
            Geri
          </button>
        </div>
      </div>
    </div>
  );
}

export default RoleList;

