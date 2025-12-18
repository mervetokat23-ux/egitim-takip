import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Select from 'react-select';
import { sorumluAPI } from '../services/api';
import Navbar from './Navbar';
import './SorumluForm.css';

function SorumluForm() {
  const [formData, setFormData] = useState({
    ad: '',
    soyad: '',
    email: '',
    telefon: '',
    unvanlar: []
  });
  const [selectedUnvanlar, setSelectedUnvanlar] = useState([]);
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [initialLoading, setInitialLoading] = useState(false);
  
  const navigate = useNavigate();
  const { id } = useParams();
  const isEditMode = !!id;

  useEffect(() => {
    if (isEditMode) {
      fetchSorumlu();
    }
  }, [id]);

  const unvanOptions = [
    { value: 'Müfredat Sorumlusu', label: 'Müfredat Sorumlusu' },
    { value: 'Operasyon Sorumlusu', label: 'Operasyon Sorumlusu' },
    { value: 'Proje Sorumlusu', label: 'Proje Sorumlusu' },
    { value: 'TGTD', label: 'TGTD' },
    { value: 'Medya Sorumlusu', label: 'Medya Sorumlusu' },
    { value: 'Ödeme Sorumlusu', label: 'Ödeme Sorumlusu' }
  ];

  const fetchSorumlu = async () => {
    setInitialLoading(true);
    try {
      const response = await sorumluAPI.getById(id);
      const unvanlar = response.data.unvanlar || [];
      
      setFormData({
        ad: response.data.ad || '',
        soyad: response.data.soyad || '',
        email: response.data.email || '',
        telefon: response.data.telefon || '',
        unvanlar: unvanlar
      });

      // Selected options'ı set et
      const selectedOptions = unvanlar.map(unvan => 
        unvanOptions.find(opt => opt.value === unvan)
      ).filter(Boolean);
      setSelectedUnvanlar(selectedOptions);
    } catch (err) {
      console.error('Sorumlu detayları alınamadı:', err);
      alert('Sorumlu bilgileri yüklenemedi.');
      navigate('/sorumlu');
    } finally {
      setInitialLoading(false);
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.ad.trim()) newErrors.ad = 'Ad alanı zorunludur';
    if (!formData.soyad.trim()) newErrors.soyad = 'Soyad alanı zorunludur';
    
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

  const handleUnvanChange = (selected) => {
    setSelectedUnvanlar(selected || []);
    setFormData(prev => ({
      ...prev,
      unvanlar: (selected || []).map(opt => opt.value)
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;

    setLoading(true);
    try {
      if (isEditMode) {
        await sorumluAPI.update(id, formData);
        alert('Sorumlu başarıyla güncellendi');
      } else {
        await sorumluAPI.create(formData);
        alert('Yeni sorumlu başarıyla eklendi');
      }
      navigate('/sorumlu');
    } catch (err) {
      console.error('Kaydetme hatası:', err);
      alert('İşlem sırasında bir hata oluştu: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  if (initialLoading) return <div className="loading">Yükleniyor...</div>;

  return (
    <div className="sorumlu-form-page">
      <Navbar />
      <div className="container">
        <div className="form-card">
          <h2>{isEditMode ? 'Sorumlu Düzenle' : 'Yeni Sorumlu Ekle'}</h2>
          
          <form onSubmit={handleSubmit}>
            <div className="form-row">
              <div className="form-group half">
                <label>Ad *</label>
                <input
                  type="text"
                  name="ad"
                  value={formData.ad}
                  onChange={handleChange}
                  className={errors.ad ? 'error-input' : ''}
                  placeholder="Örn: Mehmet"
                />
                {errors.ad && <span className="error-text">{errors.ad}</span>}
              </div>
              
              <div className="form-group half">
                <label>Soyad *</label>
                <input
                  type="text"
                  name="soyad"
                  value={formData.soyad}
                  onChange={handleChange}
                  className={errors.soyad ? 'error-input' : ''}
                  placeholder="Örn: Demir"
                />
                {errors.soyad && <span className="error-text">{errors.soyad}</span>}
              </div>
            </div>

            <div className="form-row">
              <div className="form-group half">
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
              <label>Ünvanlar (Birden fazla seçilebilir)</label>
              <Select
                isMulti
                options={unvanOptions}
                value={selectedUnvanlar}
                onChange={handleUnvanChange}
                placeholder="Ünvan seçiniz..."
                noOptionsMessage={() => 'Seçenek bulunamadı'}
                className="react-select-container"
                classNamePrefix="react-select"
              />
              <small style={{ color: '#666', fontSize: '12px', marginTop: '4px', display: 'block' }}>
                Bir kişi birden fazla ünvana sahip olabilir
              </small>
            </div>

            <div className="form-actions">
              <button 
                type="button" 
                className="btn btn-secondary"
                onClick={() => navigate('/sorumlu')}
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

export default SorumluForm;

