import React from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../Navbar';
import './Admin.css';

function AdminDashboard() {
  const navigate = useNavigate();

  const menuItems = [
    {
      title: 'Rol Yönetimi',
      description: 'Sistem rollerini görüntüle ve yönet',
      icon: '👥',
      path: '/admin/roles',
      color: '#667eea'
    },
    {
      title: 'İzin Yönetimi',
      description: 'Sistem izinlerini görüntüle ve yönet',
      icon: '🔐',
      path: '/admin/permissions',
      color: '#764ba2'
    },
    {
      title: 'Rol-İzin Ataması',
      description: 'Rollere izin ata veya kaldır',
      icon: '🔗',
      path: '/admin/role-permissions',
      color: '#f093fb'
    },
    {
      title: 'Kullanıcı-Rol Ataması',
      description: 'Kullanıcılara rol ata',
      icon: '👤',
      path: '/admin/user-roles',
      color: '#4facfe'
    }
  ];

  return (
    <div>
      <Navbar />
      <div className="container" style={{ padding: '20px' }}>
        <div className="page-header">
          <h1>🛡️ Yetkilendirme Yönetimi</h1>
        </div>

        <div className="admin-card">
          <p style={{ color: '#666', marginBottom: '30px' }}>
            Sistem rollerini, izinlerini ve kullanıcı yetkilerini buradan yönetebilirsiniz.
          </p>

          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
            gap: '20px'
          }}>
            {menuItems.map((item, index) => (
              <div
                key={index}
                onClick={() => navigate(item.path)}
                style={{
                  background: `linear-gradient(135deg, ${item.color} 0%, ${item.color}dd 100%)`,
                  color: 'white',
                  borderRadius: '12px',
                  padding: '30px 20px',
                  cursor: 'pointer',
                  transition: 'all 0.3s ease',
                  boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)',
                }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.transform = 'translateY(-5px)';
                  e.currentTarget.style.boxShadow = '0 8px 20px rgba(0, 0, 0, 0.2)';
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.transform = 'translateY(0)';
                  e.currentTarget.style.boxShadow = '0 4px 12px rgba(0, 0, 0, 0.1)';
                }}
              >
                <div style={{ fontSize: '48px', marginBottom: '15px' }}>{item.icon}</div>
                <h3 style={{ margin: '0 0 10px 0', fontSize: '20px' }}>{item.title}</h3>
                <p style={{ margin: 0, fontSize: '14px', opacity: 0.9 }}>{item.description}</p>
              </div>
            ))}
          </div>
        </div>

        <div style={{ marginTop: '30px', padding: '20px', background: '#fff3cd', borderRadius: '8px', border: '1px solid #ffc107' }}>
          <h3 style={{ margin: '0 0 10px 0', color: '#856404' }}>⚠️ Önemli Notlar</h3>
          <ul style={{ margin: 0, paddingLeft: '20px', color: '#856404' }}>
            <li>ADMIN rolü tüm izinlere sahiptir ve silinemez.</li>
            <li>Rol veya izin değişiklikleri kullanıcıların tekrar giriş yapmasını gerektirebilir.</li>
            <li>Kritik rolleri silmeden önce kullanıcı atamalarını kontrol edin.</li>
          </ul>
        </div>

        <div style={{ marginTop: '20px' }}>
          <button className="btn btn-secondary" onClick={() => navigate('/egitim')}>
            Ana Sayfaya Dön
          </button>
        </div>
      </div>
    </div>
  );
}

export default AdminDashboard;


