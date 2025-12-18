import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { egitmenAPI } from '../services/api';
import Navbar from './Navbar';
import './EgitmenForm.css';

function EgitmenForm() {
  const [formData, setFormData] = useState({
    ad: '',
    soyad: '',
    email: '',
    telefon: '',
    il: '',
    calismaYeri: ''
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [initialLoading, setInitialLoading] = useState(false);
  
  const navigate = useNavigate();
  const { id } = useParams();
  const isEditMode = !!id;

  useEffect(() => {
    if (isEditMode) {
      fetchEgitmen();
    }
  }, [id]);

  const fetchEgitmen = async () => {
    setInitialLoading(true);
    try {
      const response = await egitmenAPI.getById(id);
      setFormData({
        ad: response.data.ad || '',
        soyad: response.data.soyad || '',
        email: response.data.email || '',
        telefon: response.data.telefon || '',
        il: response.data.il || '',
        calismaYeri: response.data.calismaYeri || ''
      });
    } catch (err) {
      console.error('Eğitmen detayları alınamadı:', err);
      alert('Eğitmen bilgileri yüklenemedi.');
      navigate('/egitmen');
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

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;

    setLoading(true);
    try {
      if (isEditMode) {
        await egitmenAPI.update(id, formData);
        alert('Eğitmen başarıyla güncellendi');
      } else {
        await egitmenAPI.create(formData);
        alert('Yeni eğitmen başarıyla eklendi');
      }
      navigate('/egitmen');
    } catch (err) {
      console.error('Kaydetme hatası:', err);
      alert('İşlem sırasında bir hata oluştu: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  if (initialLoading) return <div className="loading">Yükleniyor...</div>;

  return (
    <div className="egitmen-form-page">
      <Navbar />
      <div className="container">
        <div className="form-card">
          <h2>{isEditMode ? 'Eğitmen Düzenle' : 'Yeni Eğitmen Ekle'}</h2>
          
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
                  placeholder="Örn: Ahmet"
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
                  placeholder="Örn: Yılmaz"
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
              <label>İl</label>
              <select
                name="il"
                value={formData.il}
                onChange={handleChange}
                className="form-control"
              >
                <option value="">Seçiniz</option>
                <option value="Adana">Adana</option>
                <option value="Adıyaman">Adıyaman</option>
                <option value="Afyonkarahisar">Afyonkarahisar</option>
                <option value="Ağrı">Ağrı</option>
                <option value="Amasya">Amasya</option>
                <option value="Ankara">Ankara</option>
                <option value="Antalya">Antalya</option>
                <option value="Artvin">Artvin</option>
                <option value="Aydın">Aydın</option>
                <option value="Balıkesir">Balıkesir</option>
                <option value="Bilecik">Bilecik</option>
                <option value="Bingöl">Bingöl</option>
                <option value="Bitlis">Bitlis</option>
                <option value="Bolu">Bolu</option>
                <option value="Burdur">Burdur</option>
                <option value="Bursa">Bursa</option>
                <option value="Çanakkale">Çanakkale</option>
                <option value="Çankırı">Çankırı</option>
                <option value="Çorum">Çorum</option>
                <option value="Denizli">Denizli</option>
                <option value="Diyarbakır">Diyarbakır</option>
                <option value="Edirne">Edirne</option>
                <option value="Elazığ">Elazığ</option>
                <option value="Erzincan">Erzincan</option>
                <option value="Erzurum">Erzurum</option>
                <option value="Eskişehir">Eskişehir</option>
                <option value="Gaziantep">Gaziantep</option>
                <option value="Giresun">Giresun</option>
                <option value="Gümüşhane">Gümüşhane</option>
                <option value="Hakkari">Hakkari</option>
                <option value="Hatay">Hatay</option>
                <option value="Isparta">Isparta</option>
                <option value="Mersin">Mersin</option>
                <option value="İstanbul">İstanbul</option>
                <option value="İzmir">İzmir</option>
                <option value="Kars">Kars</option>
                <option value="Kastamonu">Kastamonu</option>
                <option value="Kayseri">Kayseri</option>
                <option value="Kırklareli">Kırklareli</option>
                <option value="Kırşehir">Kırşehir</option>
                <option value="Kocaeli">Kocaeli</option>
                <option value="Konya">Konya</option>
                <option value="Kütahya">Kütahya</option>
                <option value="Malatya">Malatya</option>
                <option value="Manisa">Manisa</option>
                <option value="Kahramanmaraş">Kahramanmaraş</option>
                <option value="Mardin">Mardin</option>
                <option value="Muğla">Muğla</option>
                <option value="Muş">Muş</option>
                <option value="Nevşehir">Nevşehir</option>
                <option value="Niğde">Niğde</option>
                <option value="Ordu">Ordu</option>
                <option value="Rize">Rize</option>
                <option value="Sakarya">Sakarya</option>
                <option value="Samsun">Samsun</option>
                <option value="Siirt">Siirt</option>
                <option value="Sinop">Sinop</option>
                <option value="Sivas">Sivas</option>
                <option value="Tekirdağ">Tekirdağ</option>
                <option value="Tokat">Tokat</option>
                <option value="Trabzon">Trabzon</option>
                <option value="Tunceli">Tunceli</option>
                <option value="Şanlıurfa">Şanlıurfa</option>
                <option value="Uşak">Uşak</option>
                <option value="Van">Van</option>
                <option value="Yozgat">Yozgat</option>
                <option value="Zonguldak">Zonguldak</option>
                <option value="Aksaray">Aksaray</option>
                <option value="Bayburt">Bayburt</option>
                <option value="Karaman">Karaman</option>
                <option value="Kırıkkale">Kırıkkale</option>
                <option value="Batman">Batman</option>
                <option value="Şırnak">Şırnak</option>
                <option value="Bartın">Bartın</option>
                <option value="Ardahan">Ardahan</option>
                <option value="Iğdır">Iğdır</option>
                <option value="Yalova">Yalova</option>
                <option value="Karabük">Karabük</option>
                <option value="Kilis">Kilis</option>
                <option value="Osmaniye">Osmaniye</option>
                <option value="Düzce">Düzce</option>
              </select>
            </div>

            <div className="form-group">
              <label>Çalışma Yeri</label>
              <select
                name="calismaYeri"
                value={formData.calismaYeri}
                onChange={handleChange}
                className="form-control"
              >
                <option value="">Seçiniz</option>
                <option value="MEB">MEB</option>
                <option value="Üniversite">Üniversite</option>
                <option value="Kamu">Kamu</option>
                <option value="Özel Sektör">Özel Sektör</option>
                <option value="Diğer">Diğer</option>
              </select>
            </div>

            <div className="form-actions">
              <button 
                type="button" 
                className="btn btn-secondary"
                onClick={() => navigate('/egitmen')}
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

export default EgitmenForm;

