import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import useEventLogger from '../hooks/useEventLogger';
import './Login.css';

function Login() {
  const { login, isAuthenticated } = useAuth();
  const { logFormSubmit, logButtonClick } = useEventLogger();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  // Redirect if already authenticated
  useEffect(() => {
    if (isAuthenticated) {
      navigate('/egitim');
    }
  }, [isAuthenticated, navigate]);

  // Login sayfasına girildiğinde eski oturumu temizle
  useEffect(() => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      console.log('Giriş denemesi:', { email, sifre: password });
      logFormSubmit('Login Form', `Email: ${email}`);
      
      const userData = await login({ email, sifre: password });
      console.log('Login successful:', userData);
      
      logButtonClick('Login Success', `User: ${email}, Role: ${userData.rol}`);
      
      // Redirect to main page
      navigate('/egitim');
    } catch (err) {
      console.error('Login error:', err);
      logButtonClick('Login Failed', `Email: ${email}, Error: ${err.message}`);
      const errorMessage = err.response?.data?.message || 
                          err.response?.data?.error || 
                          err.message || 
                          'Giriş başarısız. Lütfen bilgilerinizi kontrol edin.';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <h2>Akademi Eğitim Takip Sistemi</h2>
        <form onSubmit={handleSubmit}>
          {error && <div className="error">{error}</div>}
          <div className="form-group">
            <label>Email</label>
            <input
              type="email"
              className="form-control"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              autoFocus
            />
          </div>
          <div className="form-group">
            <label>Şifre</label>
            <input
              type="password"
              className="form-control"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Giriş yapılıyor...' : 'Giriş Yap'}
          </button>
        </form>
        <div style={{ marginTop: '20px', fontSize: '12px', color: '#666' }}>
          <p><strong>Test Kullanıcıları:</strong></p>
          <p>Admin: admin@akademi.com / admin123</p>
          <p>Sorumlu: sorumlu@akademi.com / sorumlu123</p>
          <p>Eğitmen: egitmen@akademi.com / egitmen123</p>
        </div>
      </div>
    </div>
  );
}

export default Login;
