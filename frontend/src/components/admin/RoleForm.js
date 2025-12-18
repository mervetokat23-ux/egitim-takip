import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Navbar from '../Navbar';
import api from '../../services/api';
import './Admin.css';

/**
 * RoleForm
 * - Create or edit a role
 * - Allows selecting permissions grouped by module
 */
function RoleForm() {
  const navigate = useNavigate();
  const { id } = useParams(); // if id exists -> edit mode

  const isEdit = Boolean(id);

  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [permissions, setPermissions] = useState([]);
  const [selectedPermissions, setSelectedPermissions] = useState(new Set());
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

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
    permissions: 'İzin Yönetimi',
  };

  useEffect(() => {
    fetchData();
  }, [id]);

  const fetchData = async () => {
    try {
      setLoading(true);
      setError('');

      // Load all permissions
      const permissionsRes = await api.get('/permissions');
      setPermissions(permissionsRes.data);

      // If edit, load role details
      if (isEdit) {
        const roleRes = await api.get(`/roles/${id}`);
        setName(roleRes.data.name);
        setDescription(roleRes.data.description || '');
        const permIds = new Set(roleRes.data.permissions.map((p) => p.id));
        setSelectedPermissions(permIds);
      }
    } catch (err) {
      setError('Veriler yüklenirken hata oluştu: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  const handlePermissionToggle = (permissionId) => {
    const newSet = new Set(selectedPermissions);
    if (newSet.has(permissionId)) {
      newSet.delete(permissionId);
    } else {
      newSet.add(permissionId);
    }
    setSelectedPermissions(newSet);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    setSuccess('');

    try {
      const payload = {
        name,
        description,
        permissionIds: Array.from(selectedPermissions),
      };

      if (isEdit) {
        await api.put(`/roles/${id}`, payload);
      } else {
        await api.post('/roles', payload);
      }

      setSuccess('Rol başarıyla kaydedildi');
      setTimeout(() => navigate('/admin/roles'), 800);
    } catch (err) {
      setError('Kayıt sırasında hata oluştu: ' + (err.response?.data?.message || err.message));
    } finally {
      setSaving(false);
    }
  };

  // Group permissions by module for UI
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
          <h1>{isEdit ? 'Rolü Düzenle' : 'Yeni Rol Oluştur'}</h1>
          <button className="btn btn-secondary" onClick={() => navigate('/admin/roles')}>
            Geri
          </button>
        </div>

        {error && <div className="alert alert-danger">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-section">
            <h2>Rol Bilgileri</h2>
            <div className="form-group">
              <label>Rol Adı *</label>
              <input
                type="text"
                className="form-control"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
                maxLength={100}
              />
            </div>
            <div className="form-group">
              <label>Açıklama</label>
              <textarea
                className="form-control"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                maxLength={500}
                rows={3}
              />
            </div>
          </div>

          <div className="form-section">
            <h2>İzinler</h2>
            {Object.keys(groupedPermissions).length === 0 && (
              <div className="alert alert-info">Tanımlı izin bulunamadı.</div>
            )}

            {Object.keys(groupedPermissions).map((module) => (
              <div key={module} className="permission-module-group">
                <h3>
                  {moduleNames[module] || module}{' '}
                  <span
                    style={{
                      marginLeft: '8px',
                      fontSize: '13px',
                      color: '#999',
                      fontWeight: 'normal',
                    }}
                  >
                    ({groupedPermissions[module].filter((p) => selectedPermissions.has(p.id)).length} /{' '}
                    {groupedPermissions[module].length})
                  </span>
                </h3>
                <div className="permission-grid">
                  {groupedPermissions[module].map((permission) => (
                    <div key={permission.id} className="permission-item">
                      <input
                        type="checkbox"
                        id={`perm-${permission.id}`}
                        checked={selectedPermissions.has(permission.id)}
                        onChange={() => handlePermissionToggle(permission.id)}
                      />
                      <label htmlFor={`perm-${permission.id}`}>
                        <strong>{permission.action.toUpperCase()}</strong>
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

          <div style={{ marginTop: '20px', display: 'flex', gap: '10px' }}>
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? 'Kaydediliyor...' : isEdit ? 'Güncelle' : 'Oluştur'}
            </button>
            <button type="button" className="btn btn-secondary" onClick={() => navigate('/admin/roles')}>
              İptal
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default RoleForm;


