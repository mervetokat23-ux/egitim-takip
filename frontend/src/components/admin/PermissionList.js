import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../Navbar';
import api from '../../services/api';
import './Admin.css';

function PermissionList() {
  const [permissions, setPermissions] = useState([]);
  const [groupedPermissions, setGroupedPermissions] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const moduleNames = {
    education: 'Eğitimler',
    trainer: 'Eğitmenler',
    responsible: 'Sorumlular',
    category: 'Kategoriler',
    stakeholder: 'Paydaşlar',
    project: 'Projeler',
    activity: 'Faaliyetler',
    payment: 'Ödemeler',
    logs: 'Log Yönetimi',
    roles: 'Rol Yönetimi',
    permissions: 'İzin Yönetimi'
  };

  const moduleIcons = {
    education: '📚',
    trainer: '👨‍🏫',
    responsible: '👔',
    category: '📁',
    stakeholder: '🤝',
    project: '📊',
    activity: '📅',
    payment: '💰',
    logs: '📝',
    roles: '👥',
    permissions: '🔐'
  };

  useEffect(() => {
    fetchPermissions();
  }, []);

  const fetchPermissions = async () => {
    try {
      setLoading(true);
      const response = await api.get('/permissions');
      setPermissions(response.data);
      
      // Group permissions by module
      const grouped = response.data.reduce((acc, permission) => {
        const module = permission.module;
        if (!acc[module]) {
          acc[module] = [];
        }
        acc[module].push(permission);
        return acc;
      }, {});
      
      setGroupedPermissions(grouped);
    } catch (err) {
      setError('İzinler yüklenirken hata oluştu: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
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
          <h1>İzin Yönetimi</h1>
        </div>

        {error && <div className="alert alert-danger">{error}</div>}

        <div className="admin-card">
          <p style={{ color: '#666', marginBottom: '20px' }}>
            Toplam <strong>{permissions.length}</strong> izin, <strong>{Object.keys(groupedPermissions).length}</strong> modülde tanımlı.
          </p>

          {Object.keys(groupedPermissions).map((module) => (
            <div key={module} className="permission-module-group">
              <h3>
                <span className="module-icon">{moduleIcons[module] || '📦'}</span>
                {moduleNames[module] || module}
                <span style={{ 
                  marginLeft: '10px', 
                  fontSize: '14px', 
                  color: '#999',
                  fontWeight: 'normal'
                }}>
                  ({groupedPermissions[module].length} izin)
                </span>
              </h3>
              
              <table className="admin-table">
                <thead>
                  <tr>
                    <th style={{ width: '80px' }}>ID</th>
                    <th style={{ width: '150px' }}>Aksiyon</th>
                    <th>Açıklama</th>
                    <th style={{ width: '180px' }}>Oluşturma Tarihi</th>
                  </tr>
                </thead>
                <tbody>
                  {groupedPermissions[module].map((permission) => (
                    <tr key={permission.id}>
                      <td>{permission.id}</td>
                      <td>
                        <span style={{
                          padding: '4px 8px',
                          borderRadius: '4px',
                          fontSize: '12px',
                          fontWeight: 'bold',
                          background: permission.action === 'view' ? '#d1ecf1' :
                                    permission.action === 'create' ? '#d4edda' :
                                    permission.action === 'update' ? '#fff3cd' :
                                    permission.action === 'delete' ? '#f8d7da' : '#e2e3e5',
                          color: permission.action === 'view' ? '#0c5460' :
                                permission.action === 'create' ? '#155724' :
                                permission.action === 'update' ? '#856404' :
                                permission.action === 'delete' ? '#721c24' : '#383d41'
                        }}>
                          {permission.action.toUpperCase()}
                        </span>
                      </td>
                      <td>{permission.description || '-'}</td>
                      <td>{new Date(permission.createdAt).toLocaleDateString('tr-TR')}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ))}
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

export default PermissionList;


