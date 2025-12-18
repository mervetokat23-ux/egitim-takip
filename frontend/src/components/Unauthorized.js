import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Unauthorized.css';

function Unauthorized() {
  const navigate = useNavigate();
  const { user } = useAuth();

  return (
    <div className="unauthorized-container">
      <div className="unauthorized-card">
        <div className="unauthorized-icon">🔒</div>
        <h1>Erişim Engellendi</h1>
        <p className="unauthorized-message">
          Bu sayfaya erişim yetkiniz bulunmamaktadır.
        </p>
        {user && (
          <p className="unauthorized-info">
            Kullanıcı: <strong>{user.adSoyad || user.email}</strong><br />
            Rol: <strong>{user.rol}</strong>
          </p>
        )}
        <div className="unauthorized-actions">
          <button 
            className="btn btn-primary" 
            onClick={() => navigate('/egitim')}
          >
            Ana Sayfaya Dön
          </button>
          <button 
            className="btn btn-secondary" 
            onClick={() => navigate(-1)}
          >
            Geri
          </button>
        </div>
        <p className="unauthorized-help">
          Eğer bu sayfaya erişmeniz gerektiğini düşünüyorsanız, 
          lütfen sistem yöneticinizle iletişime geçin.
        </p>
      </div>
    </div>
  );
}

export default Unauthorized;


