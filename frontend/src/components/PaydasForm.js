import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Select from 'react-select';
import { paydasAPI } from '../services/api';
import Navbar from './Navbar';
import './PaydasForm.css';

const TIP_OPTIONS = [
  { value: 'Kurum', label: 'Kurum' },
  { value: 'Birey', label: 'Birey' },
  { value: 'STK', label: 'STK' },
  { value: 'Kamu', label: 'Kamu' },
  { value: 'Özel Sektör', label: 'Özel Sektör' },
  { value: 'Üniversite', label: 'Üniversite' }
];

function PaydasForm() {
  const [formData, setFormData] = useState({
    ad: '',
    email: '',
    telefon: '',
    adres: '',
    tip: ''
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [initialLoading, setInitialLoading] = useState(false);
  
  const navigate = useNavigate();
  const { id } = useParams();
  const isEditMode = !!id;

  useEffect(() => {
    if (isEditMode) {
      fetchPaydas();
    }
  }, [id]);

  const fetchPaydas = async () => {
    setInitialLoading(true);
    try {
      const response = await paydasAPI.getById(id);
      setFormData({
        ad: response.data.ad || '',
        email: response.data.email || '',
        telefon: response.data.telefon || '',
        adres: response.data.adres || '',
        tip: response.data.tip || ''
      });
    } catch (err) {
      console.error('Paydaş detayları alınamadı:', err);
      alert('Paydaş bilgileri yüklenemedi.');
      navigate('/paydas');
    } finally {
      setInitialLoading(false);
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.ad.trim()) newErrors.ad = 'Paydaş adı zorunludur';
    if (formData.email && !/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = 'Geçerli bir email adresi giriniz';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    // Hata mesajını temizle
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: null }));
    }
  };

  const handleSelectChange = (selectedOption) => {
    setFormData(prev => ({
      ...prev,
      tip: selectedOption ? selectedOption.value : ''
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;

    setLoading(true);
    try {
      if (isEditMode) {
        await paydasAPI.update(id, formData);
        alert('Paydaş başarıyla güncellendi');
      } else {
        await paydasAPI.create(formData);
        alert('Yeni paydaş başarıyla eklendi');
      }
      navigate('/paydas');
    } catch (err) {
      console.error('Kaydetme hatası:', err);
      alert('İşlem sırasında bir hata oluştu: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  if (initialLoading) return <div className="loading">Yükleniyor...</div>;

  const selectedTip = TIP_OPTIONS.find(opt => opt.value === formData.tip);

  return (
    <div className="paydas-form-page">
      <Navbar />
      <div className="container">
        <div className="form-card">
          <h2>{isEditMode ? 'Paydaş Düzenle' : 'Yeni Paydaş Ekle'}</h2>
          
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Paydaş Adı *</label>
              <input
                type="text"
                name="ad"
                value={formData.ad}
                onChange={handleChange}
                className={errors.ad ? 'error-input' : ''}
                placeholder="Kurum veya kişi adı"
              />
              {errors.ad && <span className="error-text">{errors.ad}</span>}
            </div>

            <div className="form-row">
              <div className="form-group half">
                <label>Paydaş Türü</label>
                <Select
                  value={selectedTip}
                  onChange={handleSelectChange}
                  options={TIP_OPTIONS}
                  isClearable
                  placeholder="Seçiniz..."
                  className="basic-single"
                  classNamePrefix="select"
                />
              </div>
              
              <div className="form-group half">
                <label>Telefon</label>
                <input
                  type="text"
                  name="telefon"
                  value={formData.telefon}
                  onChange={handleChange}
                  placeholder="05XX XXX XX XX"
                />
              </div>
            </div>

            <div className="form-group">
              <label>Email</label>
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                className={errors.email ? 'error-input' : ''}
                placeholder="ornek@email.com"
              />
              {errors.email && <span className="error-text">{errors.email}</span>}
            </div>

            <div className="form-group">
              <label>Adres</label>
              <textarea
                name="adres"
                value={formData.adres}
                onChange={handleChange}
                rows="3"
                placeholder="Açık adres..."
              />
            </div>

            <div className="form-actions">
              <button 
                type="button" 
                className="btn btn-secondary"
                onClick={() => navigate('/paydas')}
              >
                İptal
              </button>
              <button 
                type="submit" 
                className="btn btn-primary"
                disabled={loading}
              >
                {loading ? 'Kaydediliyor...' : (isEditMode ? 'Güncelle' : 'Kaydet')}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export default PaydasForm;

