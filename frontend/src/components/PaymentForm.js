import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
  getOdemeById,
  createOdeme,
  updateOdeme,
  calculateTotalPrice,
  getEgitimler,
  getSorumlular
} from '../services/api';
import './PaymentForm.css';

/**
 * PaymentForm Component
 * 
 * Form for creating and editing payments.
 * Features:
 * - Education dropdown (required)
 * - Unit price input (required)
 * - Total price (auto-calculate or manual)
 * - Payment source (input)
 * - Responsible dropdown (optional)
 * - Status dropdown (required)
 * - Operation dropdown/input (optional)
 * - Quantity field for auto-calculation
 * - Currency formatting
 * - Validation
 */
const PaymentForm = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEditMode = !!id;

  const [loading, setLoading] = useState(false);
  const [calculating, setCalculating] = useState(false);
  const [error, setError] = useState(null);

  // Form data
  const [formData, setFormData] = useState({
    egitimId: '',
    birimUcret: '',
    toplamUcret: '',
    odemeKaynagi: '',
    sorumluId: '',
    durum: 'Bekliyor',
    operasyon: '',
    miktar: 1
  });

  // Dropdown data
  const [egitimler, setEgitimler] = useState([]);
  const [sorumlular, setSorumlular] = useState([]);

  // Validation errors
  const [errors, setErrors] = useState({});

  useEffect(() => {
    fetchDropdownData();
    if (isEditMode) {
      fetchOdeme();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const fetchDropdownData = async () => {
    try {
      const egitimResponse = await getEgitimler({ page: 0, size: 1000 });
      setEgitimler(egitimResponse.content || []);

      const sorumluResponse = await getSorumlular({ page: 0, size: 1000 });
      setSorumlular(sorumluResponse.content || []);
    } catch (err) {
      console.error('Dropdown verileri getirilemedi:', err);
      showToast('Dropdown verileri yüklenemedi!', 'error');
    }
  };

  const fetchOdeme = async () => {
    setLoading(true);
    try {
      const odeme = await getOdemeById(id);
      setFormData({
        egitimId: odeme.egitim?.id || '',
        birimUcret: odeme.birimUcret || '',
        toplamUcret: odeme.toplamUcret || '',
        odemeKaynagi: odeme.odemeKaynagi || '',
        sorumluId: odeme.sorumlu?.id || '',
        durum: odeme.durum || 'Bekliyor',
        operasyon: odeme.operasyon || '',
        miktar: 1
      });
    } catch (err) {
      console.error('Ödeme getirilemedi:', err);
      setError('Ödeme bilgileri yüklenemedi.');
      showToast('Ödeme bilgileri yüklenemedi!', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));

    // Clear error for this field
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: null
      }));
    }
  };

  const handleCalculateTotal = async () => {
    if (!formData.birimUcret || !formData.miktar) {
      showToast('Birim ücret ve miktar giriniz!', 'error');
      return;
    }

    setCalculating(true);
    try {
      const result = await calculateTotalPrice(formData.birimUcret, formData.miktar);
      setFormData(prev => ({
        ...prev,
        toplamUcret: result.totalPrice
      }));
      showToast('Toplam ücret hesaplandı!', 'success');
    } catch (err) {
      console.error('Hesaplama hatası:', err);
      showToast('Toplam ücret hesaplanamadı!', 'error');
    } finally {
      setCalculating(false);
    }
  };

  const validateForm = () => {
    const newErrors = {};

    if (!formData.egitimId) {
      newErrors.egitimId = 'Eğitim seçimi zorunludur';
    }

    if (!formData.birimUcret || parseFloat(formData.birimUcret) <= 0) {
      newErrors.birimUcret = 'Geçerli bir birim ücret giriniz';
    }

    if (!formData.toplamUcret || parseFloat(formData.toplamUcret) <= 0) {
      newErrors.toplamUcret = 'Geçerli bir toplam ücret giriniz';
    }

    if (!formData.odemeKaynagi || formData.odemeKaynagi.trim() === '') {
      newErrors.odemeKaynagi = 'Ödeme kaynağı zorunludur';
    }

    if (!formData.durum) {
      newErrors.durum = 'Durum seçimi zorunludur';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      showToast('Lütfen tüm zorunlu alanları doldurunuz!', 'error');
      return;
    }

    setLoading(true);
    try {
      const payload = {
        egitimId: parseInt(formData.egitimId),
        birimUcret: parseFloat(formData.birimUcret),
        toplamUcret: parseFloat(formData.toplamUcret),
        odemeKaynagi: formData.odemeKaynagi,
        durum: formData.durum,
        operasyon: formData.operasyon || null,
        sorumluId: formData.sorumluId ? parseInt(formData.sorumluId) : null,
        miktar: parseInt(formData.miktar) || 1
      };

      if (isEditMode) {
        await updateOdeme(id, payload);
        showToast('Ödeme başarıyla güncellendi!', 'success');
      } else {
        await createOdeme(payload);
        showToast('Ödeme başarıyla oluşturuldu!', 'success');
      }

      setTimeout(() => {
        navigate('/payments');
      }, 1000);
    } catch (err) {
      console.error('Ödeme kaydedilemedi:', err);
      const errorMessage = err.response?.data?.error || 'Ödeme kaydedilemedi!';
      showToast(errorMessage, 'error');
    } finally {
      setLoading(false);
    }
  };

  const showToast = (message, type) => {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    document.body.appendChild(toast);

    setTimeout(() => {
      toast.classList.add('show');
    }, 100);

    setTimeout(() => {
      toast.classList.remove('show');
      setTimeout(() => {
        document.body.removeChild(toast);
      }, 300);
    }, 3000);
  };

  if (loading && isEditMode) {
    return (
      <div className="loading-spinner">
        <div className="spinner"></div>
        <p>Yükleniyor...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="error-container">
        <div className="error-message">{error}</div>
        <button className="btn btn-secondary" onClick={() => navigate('/payments')}>
          Geri Dön
        </button>
      </div>
    );
  }

  return (
    <div className="payment-form-container">
      <div className="form-header">
        <h2>{isEditMode ? 'Ödeme Düzenle' : 'Yeni Ödeme Ekle'}</h2>
        <button className="btn btn-secondary" onClick={() => navigate('/payments')}>
          ← Geri Dön
        </button>
      </div>

      <form onSubmit={handleSubmit} className="payment-form">
        {/* Education Selection */}
        <div className="form-group">
          <label htmlFor="egitimId">
            Eğitim <span className="required">*</span>
          </label>
          <select
            id="egitimId"
            name="egitimId"
            value={formData.egitimId}
            onChange={handleChange}
            className={errors.egitimId ? 'error' : ''}
            required
          >
            <option value="">Eğitim Seçiniz</option>
            {egitimler.map(egitim => (
              <option key={egitim.id} value={egitim.id}>
                {egitim.ad} - {egitim.il}
              </option>
            ))}
          </select>
          {errors.egitimId && <span className="error-text">{errors.egitimId}</span>}
        </div>

        {/* Unit Price and Quantity */}
        <div className="form-row">
          <div className="form-group">
            <label htmlFor="birimUcret">
              Birim Ücret (₺) <span className="required">*</span>
            </label>
            <input
              type="number"
              id="birimUcret"
              name="birimUcret"
              value={formData.birimUcret}
              onChange={handleChange}
              step="0.01"
              min="0.01"
              placeholder="100.00"
              className={errors.birimUcret ? 'error' : ''}
              required
            />
            {errors.birimUcret && <span className="error-text">{errors.birimUcret}</span>}
          </div>

          <div className="form-group">
            <label htmlFor="miktar">Miktar</label>
            <input
              type="number"
              id="miktar"
              name="miktar"
              value={formData.miktar}
              onChange={handleChange}
              min="1"
              placeholder="1"
            />
          </div>
        </div>

        {/* Calculate Total Button */}
        <div className="form-group">
          <button
            type="button"
            className="btn btn-secondary"
            onClick={handleCalculateTotal}
            disabled={calculating}
          >
            {calculating ? 'Hesaplanıyor...' : '🧮 Toplam Ücreti Hesapla'}
          </button>
        </div>

        {/* Total Price */}
        <div className="form-group">
          <label htmlFor="toplamUcret">
            Toplam Ücret (₺) <span className="required">*</span>
          </label>
          <input
            type="number"
            id="toplamUcret"
            name="toplamUcret"
            value={formData.toplamUcret}
            onChange={handleChange}
            step="0.01"
            min="0.01"
            placeholder="500.00"
            className={errors.toplamUcret ? 'error' : ''}
            required
          />
          {errors.toplamUcret && <span className="error-text">{errors.toplamUcret}</span>}
          <small className="help-text">
            Otomatik hesapla butonunu kullanabilir veya manuel girebilirsiniz
          </small>
        </div>

        {/* Payment Source */}
        <div className="form-group">
          <label htmlFor="odemeKaynagi">
            Ödeme Kaynağı <span className="required">*</span>
          </label>
          <select
            id="odemeKaynagi"
            name="odemeKaynagi"
            value={formData.odemeKaynagi}
            onChange={handleChange}
            className={errors.odemeKaynagi ? 'error' : ''}
            required
          >
            <option value="">Seçiniz</option>
            <option value="Banka Havalesi">Banka Havalesi</option>
            <option value="Kredi Kartı">Kredi Kartı</option>
            <option value="Nakit">Nakit</option>
            <option value="Çek">Çek</option>
            <option value="Sponsor">Sponsor</option>
            <option value="Diğer">Diğer</option>
          </select>
          {errors.odemeKaynagi && <span className="error-text">{errors.odemeKaynagi}</span>}
        </div>

        {/* Responsible Person */}
        <div className="form-group">
          <label htmlFor="sorumluId">Sorumlu Kişi</label>
          <select
            id="sorumluId"
            name="sorumluId"
            value={formData.sorumluId}
            onChange={handleChange}
          >
            <option value="">Seçiniz (Opsiyonel)</option>
            {sorumlular.map(sorumlu => (
              <option key={sorumlu.id} value={sorumlu.id}>
                {sorumlu.ad}
              </option>
            ))}
          </select>
        </div>

        {/* Status */}
        <div className="form-group">
          <label htmlFor="durum">
            Durum <span className="required">*</span>
          </label>
          <select
            id="durum"
            name="durum"
            value={formData.durum}
            onChange={handleChange}
            className={errors.durum ? 'error' : ''}
            required
          >
            <option value="Ödendi">Ödendi</option>
            <option value="Bekliyor">Bekliyor</option>
            <option value="İptal">İptal</option>
          </select>
          {errors.durum && <span className="error-text">{errors.durum}</span>}
        </div>

        {/* Operation */}
        <div className="form-group">
          <label htmlFor="operasyon">Operasyon Türü</label>
          <select
            id="operasyon"
            name="operasyon"
            value={formData.operasyon}
            onChange={handleChange}
          >
            <option value="">Seçiniz (Opsiyonel)</option>
            <option value="Havale">Havale</option>
            <option value="Nakit">Nakit</option>
            <option value="POS">POS</option>
            <option value="Sistem içi">Sistem içi</option>
            <option value="Çek">Çek</option>
            <option value="Diğer">Diğer</option>
          </select>
        </div>

        {/* Form Actions */}
        <div className="form-actions">
          <button
            type="button"
            className="btn btn-secondary"
            onClick={() => navigate('/payments')}
            disabled={loading}
          >
            İptal
          </button>
          <button
            type="submit"
            className="btn btn-primary"
            disabled={loading}
          >
            {loading ? 'Kaydediliyor...' : isEditMode ? 'Güncelle' : 'Oluştur'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default PaymentForm;

