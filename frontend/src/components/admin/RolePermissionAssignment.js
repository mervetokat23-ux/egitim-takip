import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../Navbar';
import api from '../../services/api';
import './Admin.css';

function RolePermissionAssignment() {
  const [roles, setRoles] = useState([]);
  const [permissions, setPermissions] = useState([]);
  const [selectedRole, setSelectedRole] = useState(null);
  const [rolePermissions, setRolePermissions] = useState(new Set());
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
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

  useEffect(() => {
    fetchData();
  }, []);

  useEffect(() => {
    if (selectedRole) {
      fetchRolePermissions();
    }
  }, [selectedRole]);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [rolesRes, permissionsRes] = await Promise.all([
        api.get('/roles'),
        api.get('/permissions')
      ]);
      setRoles(rolesRes.data);
      setPermissions(permissionsRes.data);
    } catch (err) {
      setError('Veriler yüklenirken hata oluştu: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  const fetchRolePermissions = async () => {
    try {
      const response = await api.get(`/roles/${selectedRole.id}`);
      const permissionIds = new Set(response.data.permissions.map(p => p.id));
      setRolePermissions(permissionIds);
    } catch (err) {
      setError('Rol izinleri yüklenirken hata oluştu: ' + (err.response?.data?.message || err.message));
    }
  };

  const handlePermissionToggle = (permissionId) => {
    const newPermissions = new Set(rolePermissions);
    if (newPermissions.has(permissionId)) {
      newPermissions.delete(permissionId);
    } else {
      newPermissions.add(permissionId);
    }
    setRolePermissions(newPermissions);
  };

  const handleSave = async () => {
    if (!selectedRole) return;

    try {
      setSaving(true);
      setError('');
      setSuccess('');

      // Get current permissions
      const currentResponse = await api.get(`/roles/${selectedRole.id}`);
      const currentPermissionIds = new Set(currentResponse.data.permissions.map(p => p.id));

      // Find permissions to add and remove
      const toAdd = Array.from(rolePermissions).filter(id => !currentPermissionIds.has(id));
      const toRemove = Array.from(currentPermissionIds).filter(id => !rolePermissions.has(id));

      // Add new permissions
      for (const permissionId of toAdd) {
        await api.post(`/roles/${selectedRole.id}/permissions/${permissionId}`);
      }

      // Remove old permissions
      for (const permissionId of toRemove) {
        await api.delete(`/roles/${selectedRole.id}/permissions/${permissionId}`);
      }

      setSuccess('İzinler başarıyla güncellendi!');
      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      setError('İzinler güncellenirken hata oluştu: ' + (err.response?.data?.message || err.message));
    } finally {
      setSaving(false);
    }
  };

  // Group permissions by module
  const groupedPermissions = permissions.reduce((acc, permission) => {
    const module = permission.module;
    if (!acc[module]) {
      acc[module] = [];
    }
    acc[module].push(permission);
    return acc;
  }, {});

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
          <h1>Rol-İzin Ataması</h1>
        </div>

        {error && <div className="alert alert-danger">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}

        <div className="admin-card">
          <div className="form-group">
            <label><strong>Rol Seçin:</strong></label>
            <select
              className="form-control"
              value={selectedRole?.id || ''}
              onChange={(e) => {
                const role = roles.find(r => r.id === parseInt(e.target.value));
                setSelectedRole(role);
              }}
            >
              <option value="">-- Rol Seçin --</option>
              {roles.map((role) => (
                <option key={role.id} value={role.id}>
                  {role.name} - {role.description || 'Açıklama yok'}
                </option>
              ))}
            </select>
          </div>

          {selectedRole && (
            <>
              <div style={{ 
                marginTop: '20px', 
                padding: '15px', 
                background: '#f8f9fa', 
                borderRadius: '6px',
                borderLeft: '4px solid #667eea'
              }}>
                <h3 style={{ margin: '0 0 5px 0', color: '#333' }}>{selectedRole.name}</h3>
                <p style={{ margin: 0, color: '#666' }}>{selectedRole.description || 'Açıklama yok'}</p>
                <p style={{ margin: '10px 0 0 0', fontSize: '14px', color: '#999' }}>
                  Seçili İzin Sayısı: <strong>{rolePermissions.size}</strong> / {permissions.length}
                </p>
              </div>

              <div style={{ marginTop: '30px' }}>
                {Object.keys(groupedPermissions).map((module) => (
                  <div key={module} className="permission-module-group">
                    <h3>
                      {moduleNames[module] || module}
                      <span style={{ 
                        marginLeft: '10px', 
                        fontSize: '14px', 
                        color: '#999',
                        fontWeight: 'normal'
                      }}>
                        ({groupedPermissions[module].filter(p => rolePermissions.has(p.id)).length} / {groupedPermissions[module].length})
                      </span>
                    </h3>
                    
                    <div className="permission-grid">
                      {groupedPermissions[module].map((permission) => (
                        <div key={permission.id} className="permission-item">
                          <input
                            type="checkbox"
                            id={`permission-${permission.id}`}
                            checked={rolePermissions.has(permission.id)}
                            onChange={() => handlePermissionToggle(permission.id)}
                            disabled={selectedRole.name === 'ADMIN'} // ADMIN role cannot be modified
                          />
                          <label htmlFor={`permission-${permission.id}`}>
                            <strong>{permission.action}</strong>
                            {permission.description && (
                              <div style={{ fontSize: '12px', color: '#999', marginTop: '2px' }}>
                                {permission.description}
                              </div>
                            )}
                          </label>
                        </div>
                      ))}
                    </div>
                  </div>
                ))}
              </div>

              <div style={{ marginTop: '30px', display: 'flex', gap: '10px' }}>
                <button
                  className="btn btn-primary"
                  onClick={handleSave}
                  disabled={saving || selectedRole.name === 'ADMIN'}
                >
                  {saving ? 'Kaydediliyor...' : 'Kaydet'}
                </button>
                {selectedRole.name === 'ADMIN' && (
                  <span style={{ color: '#dc3545', alignSelf: 'center', fontSize: '14px' }}>
                    ⚠️ ADMIN rolü değiştirilemez
                  </span>
                )}
              </div>
            </>
          )}
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

export default RolePermissionAssignment;


