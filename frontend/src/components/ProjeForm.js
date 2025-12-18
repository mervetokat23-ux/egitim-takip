import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Select from 'react-select';
import { projeAPI, sorumluAPI, paydasAPI } from '../services/api';
import Navbar from './Navbar';
import './ProjeForm.css';

function ProjeForm() {
  const [formData, setFormData] = useState({
    isim: '',
    baslangicTarihi: '',
    tarih: '', // Bitiş tarihi
    projeHakkinda: '',
    egitimSorumluId: null,
    paydasId: null
  });
  
  // Dropdown listeleri
  const [sorumluListesi, setSorumluListesi] = useState([]);
  const [paydasListesi, setPaydasListesi] = useState([]);
  
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
      // Dropdown verilerini paralel çek
      const [sorumluRes, paydasRes] = await Promise.all([
        sorumluAPI.getAll(),
        paydasAPI.getAll()
      ]);

      setSorumluListesi(sorumluRes.data);
      setPaydasListesi(paydasRes.data);

      if (isEditMode) {
        const response = await projeAPI.getById(id);
        const data = response.data;
        setFormData({
          isim: data.isim || '',
          baslangicTarihi: data.baslangicTarihi || '',
          tarih: data.tarih || '',
          projeHakkinda: data.projeHakkinda || '',
          egitimSorumluId: data.egitimSorumlu ? data.egitimSorumlu.id : null,
          paydasId: data.paydas ? data.paydas.id : null
        });
      }
    } catch (err) {
      console.error('Veri yükleme hatası:', err);
      alert('Form verileri yüklenemedi.');
      navigate('/proje');
    } finally {
      setInitialLoading(false);
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.isim.trim()) newErrors.isim = 'Proje adı zorunludur';
    if (formData.baslangicTarihi && formData.tarih) {
      if (new Date(formData.baslangicTarihi) > new Date(formData.tarih)) {
        newErrors.tarih = 'Bitiş tarihi başlangıç tarihinden önce olamaz';
      }
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
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: null }));
    }
  };

  const handleMultiSelectChange = (field, selectedOptions) => {
    setFormData(prev => ({
      ...prev,
      [field]: selectedOptions ? selectedOptions.map(opt => opt.value) : []
    }));
  };

  const handleSelectChange = (field, selectedOption) => {
    setFormData(prev => ({
      ...prev,
      [field]: selectedOption ? selectedOption.value : null
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;

    setLoading(true);
    try {
      if (isEditMode) {
        await projeAPI.update(id, formData);
        alert('Proje başarıyla güncellendi');
      } else {
        await projeAPI.create(formData);
        alert('Yeni proje başarıyla eklendi');
      }
      navigate('/proje');
    } catch (err) {
      console.error('Kaydetme hatası:', err);
      alert('İşlem sırasında bir hata oluştu: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  if (initialLoading) return <div className="loading">Yükleniyor...</div>;

  // Options for React-Select
  const sorumluOptions = sorumluListesi.map(item => ({ value: item.id, label: `${item.ad} ${item.soyad}` }));
  const paydasOptions = paydasListesi.map(item => ({ value: item.id, label: item.ad }));

  return (
    <div className="proje-form-page">
      <Navbar />
      <div className="container">
        <div className="form-card">
          <h2>{isEditMode ? 'Proje Düzenle' : 'Yeni Proje Ekle'}</h2>
          
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Proje Adı *</label>
              <input
                type="text"
                name="isim"
                value={formData.isim}
                onChange={handleChange}
                className={errors.isim ? 'error-input' : ''}
                placeholder="Proje adı"
              />
              {errors.isim && <span className="error-text">{errors.isim}</span>}
            </div>

            <div className="form-row">
              <div className="form-group half">
                <label>Başlangıç Tarihi</label>
                <input
                  type="date"
                  name="baslangicTarihi"
                  value={formData.baslangicTarihi}
                  onChange={handleChange}
                />
              </div>
              
              <div className="form-group half">
                <label>Bitiş Tarihi</label>
                <input
                  type="date"
                  name="tarih"
                  value={formData.tarih}
                  onChange={handleChange}
                  className={errors.tarih ? 'error-input' : ''}
                />
                {errors.tarih && <span className="error-text">{errors.tarih}</span>}
              </div>
            </div>

            <div className="form-row">
              <div className="form-group half">
                <label>Proje Sorumlusu</label>
                <Select
                  value={sorumluOptions.find(opt => opt.value === formData.egitimSorumluId)}
                  onChange={(opt) => handleSelectChange('egitimSorumluId', opt)}
                  options={sorumluOptions}
                  isClearable
                  placeholder="Sorumlu Seçiniz..."
                />
              </div>

              <div className="form-group half">
                <label>Paydaş</label>
                <Select
                  value={paydasOptions.find(opt => opt.value === formData.paydasId)}
                  onChange={(opt) => handleSelectChange('paydasId', opt)}
                  options={paydasOptions}
                  isClearable
                  placeholder="Paydaş Seçiniz..."
                />
              </div>
            </div>

            <div className="form-group">
              <label>Proje Hakkında</label>
              <textarea
                name="projeHakkinda"
                value={formData.projeHakkinda}
                onChange={handleChange}
                rows="4"
                placeholder="Proje detayları..."
              />
            </div>

            <div className="form-actions">
              <button 
                type="button" 
                className="btn btn-secondary"
                onClick={() => navigate('/proje')}
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

export default ProjeForm;

