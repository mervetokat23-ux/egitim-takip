import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Navbar.css';

function Navbar() {
  const navigate = useNavigate();
  const { user, logout, hasPermission, isAdmin } = useAuth();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  // Navigation items with permission requirements
  const navItems = [
    { label: 'Eğitimler', path: '/egitim', module: 'education', action: 'view' },
    { label: 'Eğitmenler', path: '/egitmen', module: 'trainer', action: 'view' },
    { label: 'Sorumlular', path: '/sorumlu', module: 'responsible', action: 'view' },
    { label: 'Kategoriler', path: '/kategori', module: 'category', action: 'view' },
    { label: 'Paydaşlar', path: '/paydas', module: 'stakeholder', action: 'view' },
    { label: 'Projeler', path: '/proje', module: 'project', action: 'view' },
    { label: 'Faaliyetler', path: '/faaliyet', module: 'activity', action: 'view' },
    { label: 'Ödemeler', path: '/payments', module: 'payment', action: 'view' },
  ];

  return (
    <nav className="navbar">
      <div className="navbar-content">
        <div className="navbar-brand" onClick={() => navigate('/egitim')} style={{ cursor: 'pointer' }}>
          Akademi Eğitim Takip
        </div>
        <div className="navbar-links">
          {navItems.map((item) => (
            hasPermission(item.module, item.action) && (
              <button 
                key={item.path}
                className="nav-link" 
                onClick={() => navigate(item.path)}
              >
                {item.label}
              </button>
            )
          ))}
          
          {/* Log Yönetimi - Only for users with logs.view permission */}
          {hasPermission('logs', 'view') && (
            <button 
              className="nav-link" 
              onClick={() => navigate('/logs')} 
              style={{ backgroundColor: '#667eea', color: 'white' }}
            >
              Log Yönetimi
            </button>
          )}
          
          {/* Admin Panel - Only for users with roles.view permission */}
          {hasPermission('roles', 'view') && (
            <button 
              className="nav-link" 
              onClick={() => navigate('/admin')} 
              style={{ backgroundColor: '#dc3545', color: 'white' }}
            >
              Yönetim Paneli
            </button>
          )}
        </div>
        <div className="navbar-actions">
          <span>
            {user?.adSoyad || user?.email}
            {user?.rol && (
              <span style={{ 
                marginLeft: '8px', 
                padding: '2px 6px', 
                background: user.rol === 'ADMIN' ? '#dc3545' : '#28a745',
                color: 'white',
                borderRadius: '4px',
                fontSize: '11px',
                fontWeight: 'bold'
              }}>
                {user.rol}
              </span>
            )}
          </span>
          <button className="btn btn-secondary" onClick={handleLogout}>
            Çıkış
          </button>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
