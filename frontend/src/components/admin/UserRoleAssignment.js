import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../Navbar';
import api from '../../services/api';
import './Admin.css';

function UserRoleAssignment() {
  const [sorumlular, setSorumlular] = useState([]);
  const [roles, setRoles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [sorumluRes, rolesRes] = await Promise.all([
        api.get('/sorumlu'),
        api.get('/roles')
      ]);
      setSorumlular(sorumluRes.data.content || sorumluRes.data);
      setRoles(rolesRes.data);
    } catch (err) {
      setError('Veriler yüklenirken hata oluştu: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  const handleRoleAssignment = async (sorumluId, roleId) => {
    if (!roleId) {
      // Unassign role
      if (!window.confirm('Bu kullanıcının rolünü kaldırmak istediğinizden emin misiniz?')) {
        return;
      }
      
      try {
        setSaving(true);
        await api.delete(`/roles/unassign/${sorumluId}`);
        setSuccess('Rol başarıyla kaldırıldı!');
        fetchData();
        setTimeout(() => setSuccess(''), 3000);
      } catch (err) {
        setError('Rol kaldırılırken hata oluştu: ' + (err.response?.data?.message || err.message));
      } finally {
        setSaving(false);
      }
    } else {
      // Assign role
      try {
        setSaving(true);
        await api.put(`/roles/assign/${sorumluId}/${roleId}`);
        setSuccess('Rol başarıyla atandı!');
        fetchData();
        setTimeout(() => setSuccess(''), 3000);
      } catch (err) {
        setError('Rol atanırken hata oluştu: ' + (err.response?.data?.message || err.message));
      } finally {
        setSaving(false);
      }
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
          <h1>Kullanıcı-Rol Ataması</h1>
        </div>

        {error && <div className="alert alert-danger">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}

        <div className="admin-card">
          <p style={{ color: '#666', marginBottom: '20px' }}>
            Sorumlu kullanıcılara rol atayabilir veya mevcut rollerini değiştirebilirsiniz.
          </p>

          <table className="admin-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Ad Soyad</th>
                <th>Email</th>
                <th>Ünvanlar</th>
                <th>Mevcut Rol</th>
                <th>Yeni Rol Ata</th>
                <th>İşlem</th>
              </tr>
            </thead>
            <tbody>
              {sorumlular.length === 0 ? (
                <tr>
                  <td colSpan="7" style={{ textAlign: 'center', padding: '20px' }}>
                    Henüz sorumlu kullanıcı bulunmamaktadır.
                  </td>
                </tr>
              ) : (
                sorumlular.map((sorumlu) => (
                  <tr key={sorumlu.id}>
                    <td>{sorumlu.id}</td>
                    <td><strong>{sorumlu.adSoyad}</strong></td>
                    <td>{sorumlu.email || '-'}</td>
                    <td>
                      {sorumlu.unvanlar && sorumlu.unvanlar.length > 0 ? (
                        <div style={{ fontSize: '12px' }}>
                          {sorumlu.unvanlar.join(', ')}
                        </div>
                      ) : '-'}
                    </td>
                    <td>
                      {sorumlu.roleName ? (
                        <span style={{
                          padding: '4px 8px',
                          borderRadius: '4px',
                          fontSize: '12px',
                          fontWeight: 'bold',
                          background: sorumlu.roleName === 'ADMIN' ? '#dc3545' :
                                    sorumlu.roleName === 'STAFF' ? '#28a745' : '#17a2b8',
                          color: 'white'
                        }}>
                          {sorumlu.roleName}
                        </span>
                      ) : (
                        <span style={{ color: '#999', fontSize: '12px' }}>Rol atanmamış</span>
                      )}
                    </td>
                    <td>
                      <select
                        className="form-control"
                        style={{ fontSize: '14px', padding: '6px' }}
                        defaultValue=""
                        onChange={(e) => {
                          if (e.target.value) {
                            handleRoleAssignment(sorumlu.id, parseInt(e.target.value));
                            e.target.value = ''; // Reset selection
                          }
                        }}
                        disabled={saving}
                      >
                        <option value="">-- Rol Seç --</option>
                        {roles.map((role) => (
                          <option 
                            key={role.id} 
                            value={role.id}
                            disabled={sorumlu.roleId === role.id}
                          >
                            {role.name}
                          </option>
                        ))}
                      </select>
                    </td>
                    <td>
                      {sorumlu.roleId && (
                        <button
                          className="btn btn-sm btn-danger"
                          onClick={() => handleRoleAssignment(sorumlu.id, null)}
                          disabled={saving}
                          title="Rolü Kaldır"
                        >
                          🗑️ Rolü Kaldır
                        </button>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        <div style={{ marginTop: '30px', padding: '20px', background: '#d1ecf1', borderRadius: '8px', border: '1px solid #bee5eb' }}>
          <h3 style={{ margin: '0 0 10px 0', color: '#0c5460' }}>ℹ️ Bilgi</h3>
          <ul style={{ margin: 0, paddingLeft: '20px', color: '#0c5460' }}>
            <li>Rol atama işlemi kullanıcının yetki seviyesini belirler.</li>
            <li>Kullanıcılar rol değişikliği sonrası tekrar giriş yapmalıdır.</li>
            <li>ADMIN rolü tüm izinlere sahiptir.</li>
            <li>Rol kaldırıldığında kullanıcı sadece temel izinlere sahip olur.</li>
          </ul>
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

export default UserRoleAssignment;


