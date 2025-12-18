import React, { useState, useEffect } from 'react';
import { useNavigate, useParams, useLocation } from 'react-router-dom';
import Select from 'react-select';
import { faaliyetAPI, projeAPI, sorumluAPI } from '../services/api';
import Navbar from './Navbar';
import './FaaliyetForm.css';

function FaaliyetForm() {
  const { id } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  const isEditMode = !!id;

  const queryParams = new URLSearchParams(location.search);
  const initialProjeIdFromQuery = queryParams.get('projeId');

  const [formData, setFormData] = useState({
    tarih: '',
    isim: '',
    turu: '',
    projeId: initialProjeIdFromQuery ? Number(initialProjeIdFromQuery) : null,
    sorumluIds: [],
  });

  const [projeOptions, setProjeOptions] = useState([]);
  const [sorumluOptions, setSorumluOptions] = useState([]);
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [initialLoading, setInitialLoading] = useState(false);

  useEffect(() => {
    fetchOptions();
    if (isEditMode) {
      fetchFaaliyet();
    }
  }, [id]);

  const fetchOptions = async () => {
    setInitialLoading(true);
    try {
      const [projeRes, sorumluRes] = await Promise.all([
        projeAPI.getAll(),
        sorumluAPI.getAll(),
      ]);
      setProjeOptions(
        (projeRes.data || []).map((p) => ({ value: p.id, label: p.isim }))
      );
      setSorumluOptions(
        (sorumluRes.data || []).map((s) => ({
          value: s.id,
          label: `${s.ad} ${s.soyad}`,
        }))
      );
    } catch (err) {
      console.error('Faaliyet form seçenekleri yüklenirken hata:', err);
    } finally {
      setInitialLoading(false);
    }
  };

  const fetchFaaliyet = async () => {
    setInitialLoading(true);
    try {
      const res = await faaliyetAPI.getById(id);
      const f = res.data;
      const formatDate = (d) => (d ? new Date(d).toISOString().split('T')[0] : '');

      setFormData({
        tarih: formatDate(f.tarih),
        isim: f.isim || '',
        turu: f.turu || '',
        projeId: f.proje ? f.proje.id : null,
        sorumluIds: f.sorumlular ? f.sorumlular.map((s) => s.id) : [],
      });
    } catch (err) {
      console.error('Faaliyet detayları yüklenirken hata:', err);
      alert('Faaliyet bilgileri yüklenemedi.');
      navigate('/faaliyet');
    } finally {
      setInitialLoading(false);
    }
  };

  const validateForm = () => {
    const newErrors = {};
    if (!formData.tarih) newErrors.tarih = 'Tarih zorunludur';
    if (!formData.isim.trim()) newErrors.isim = 'Faaliyet adı zorunludur';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: null }));
    }
  };

  const handleSelectChange = (field, option) => {
    setFormData((prev) => ({ ...prev, [field]: option ? option.value : null }));
  };

  const handleMultiSelectChange = (field, options) => {
    setFormData((prev) => ({
      ...prev,
      [field]: options ? options.map((o) => o.value) : [],
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    setLoading(true);
    try {
      if (isEditMode) {
        await faaliyetAPI.update(id, formData);
        alert('Faaliyet güncellendi');
      } else {
        await faaliyetAPI.create(formData);
        alert('Yeni faaliyet oluşturuldu');
      }
      navigate('/faaliyet');
    } catch (err) {
      console.error('Faaliyet kaydetme hatası:', err);
      alert('İşlem sırasında bir hata oluştu: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  if (initialLoading) return <div className="loading">Yükleniyor...</div>;

  return (
    <div className="faaliyet-form-page">
      <Navbar />
      <div className="container">
        <div className="form-card">
          <h2>{isEditMode ? 'Faaliyet Düzenle' : 'Yeni Faaliyet Ekle'}</h2>
          <form onSubmit={handleSubmit}>
            <div className="form-row">
              <div className="form-group half">
                <label>Tarih *</label>
                <input
                  type="date"
                  name="tarih"
                  value={formData.tarih}
                  onChange={handleChange}
                  className={errors.tarih ? 'error-input' : ''}
                />
                {errors.tarih && <span className="error-text">{errors.tarih}</span>}
              </div>
              <div className="form-group half">
                <label>Türü</label>
                <input
                  type="text"
                  name="turu"
                  value={formData.turu}
                  onChange={handleChange}
                  placeholder="Toplantı, Seminer vb."
                />
              </div>
            </div>

            <div className="form-group">
              <label>Faaliyet Adı *</label>
              <input
                type="text"
                name="isim"
                value={formData.isim}
                onChange={handleChange}
                className={errors.isim ? 'error-input' : ''}
                placeholder="Faaliyet adı"
              />
              {errors.isim && <span className="error-text">{errors.isim}</span>}
            </div>

            <div className="form-row">
              <div className="form-group half">
                <label>Proje</label>
                <Select
                  value={projeOptions.find((opt) => opt.value === formData.projeId) || null}
                  onChange={(opt) => handleSelectChange('projeId', opt)}
                  options={projeOptions}
                  isClearable
                  placeholder="Proje seçiniz..."
                />
              </div>
              <div className="form-group half">
                <label>Sorumlular</label>
                <Select
                  value={sorumluOptions.filter((opt) =>
                    formData.sorumluIds.includes(opt.value)
                  )}
                  onChange={(opts) => handleMultiSelectChange('sorumluIds', opts)}
                  options={sorumluOptions}
                  isMulti
                  isClearable
                  placeholder="Sorumlu seçiniz..."
                />
              </div>
            </div>

            <div className="form-actions">
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => navigate('/faaliyet')}
              >
                İptal
              </button>
              <button type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? 'Kaydediliyor...' : isEditMode ? 'Güncelle' : 'Kaydet'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export default FaaliyetForm;




