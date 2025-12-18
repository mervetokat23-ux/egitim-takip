import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Select from 'react-select';
import { kategoriAPI } from '../services/api';
import Navbar from './Navbar';
import './KategoriForm.css';

function KategoriForm() {
  const [formData, setFormData] = useState({
    ad: '',
    aciklama: '',
    ustKategoriId: null
  });
  const [kategoriListesi, setKategoriListesi] = useState([]); // Üst kategori seçimi için
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [initialLoading, setInitialLoading] = useState(false);
  
  const navigate = useNavigate();
  const { id } = useParams();
  const isEditMode = !!id;

  useEffect(() => {
    fetchData();
  }, [id]);

  const fetchData = async () => {
    setInitialLoading(true);
    try {
      // Önce kategori listesini al (üst kategori seçimi için)
      const listResponse = await kategoriAPI.getAll();
      setKategoriListesi(listResponse.data);

      if (isEditMode) {
        const response = await kategoriAPI.getById(id);
        setFormData({
          ad: response.data.ad || '',
          aciklama: response.data.aciklama || '',
          ustKategoriId: response.data.ustKategoriId || null
        });
      }
    } catch (err) {
      console.error('Veri yükleme hatası:', err);
      alert('Kategori bilgileri yüklenemedi.');
      navigate('/kategori');
    } finally {
      setInitialLoading(false);
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.ad.trim()) newErrors.ad = 'Kategori adı zorunludur';
    
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
      ustKategoriId: selectedOption ? selectedOption.value : null
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;

    setLoading(true);
    try {
      if (isEditMode) {
        await kategoriAPI.update(id, formData);
        alert('Kategori başarıyla güncellendi');
      } else {
        await kategoriAPI.create(formData);
        alert('Yeni kategori başarıyla eklendi');
      }
      navigate('/kategori');
    } catch (err) {
      console.error('Kaydetme hatası:', err);
      alert('İşlem sırasında bir hata oluştu: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  if (initialLoading) return <div className="loading">Yükleniyor...</div>;

  // React-select options
  const kategoriOptions = kategoriListesi
    .filter(k => k.id !== parseInt(id)) // Kendisini listeden çıkar (döngüsel referansı önlemek için)
    .map(k => ({
      value: k.id,
      label: k.ad
    }));

  const selectedUstKategori = kategoriOptions.find(opt => opt.value === formData.ustKategoriId);

  return (
    <div className="kategori-form-page">
      <Navbar />
      <div className="container">
        <div className="form-card">
          <h2>{isEditMode ? 'Kategori Düzenle' : 'Yeni Kategori Ekle'}</h2>
          
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Kategori Adı *</label>
              <input
                type="text"
                name="ad"
                value={formData.ad}
                onChange={handleChange}
                className={errors.ad ? 'error-input' : ''}
                placeholder="Örn: Yazılım"
              />
              {errors.ad && <span className="error-text">{errors.ad}</span>}
            </div>

            <div className="form-group">
              <label>Açıklama</label>
              <textarea
                name="aciklama"
                value={formData.aciklama}
                onChange={handleChange}
                rows="3"
                placeholder="Kategori açıklaması..."
              />
            </div>

            <div className="form-group">
              <label>Üst Kategori</label>
              <Select
                value={selectedUstKategori}
                onChange={handleSelectChange}
                options={kategoriOptions}
                isClearable
                placeholder="Üst kategori seçiniz (Opsiyonel)"
                className="basic-single"
                classNamePrefix="select"
              />
              <small className="form-text text-muted">
                Ana kategori ise boş bırakın.
              </small>
            </div>

            <div className="form-actions">
              <button 
                type="button" 
                className="btn btn-secondary"
                onClick={() => navigate('/kategori')}
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

export default KategoriForm;

