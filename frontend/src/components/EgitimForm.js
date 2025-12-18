import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Select from 'react-select';
import { egitimAPI, kategoriAPI, egitmenAPI, sorumluAPI, paydasAPI, projeAPI } from '../services/api';
import Navbar from './Navbar';
import './EgitimForm.css';

function EgitimForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = !!id;

  const [formData, setFormData] = useState({
    ad: '',
    egitimKodu: '',
    programId: '',
    seviye: '',
    hedefKitle: '',
    aciklama: '',
    baslangicTarihi: '',
    bitisTarihi: '',
    egitimSaati: '',
    durum: 'Havuz',
    kategoriIds: [],
    egitmenIds: [],
    sorumluIds: [],
    paydasIds: [],
    projeId: null,
  });

  const [options, setOptions] = useState({
    kategoriler: [],
    egitmenler: [],
    sorumlular: [],
    paydaslar: [],
    projeler: [],
  });

  const [selectedOptions, setSelectedOptions] = useState({
    kategoriler: [],
    egitmenler: [],
    sorumlular: [],
    paydaslar: [],
    hedefKitleler: [],
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    loadOptions();
    if (isEdit) {
      loadEgitim();
    } else {
      // Yeni eğitim eklerken başlangıç tarihini bugüne ayarla
      const today = new Date().toISOString().split('T')[0];
      setFormData(prev => ({ ...prev, baslangicTarihi: today }));
    }
  }, [id]);

  const loadOptions = async () => {
    try {
      const [kategorilerRes, egitmenlerRes, sorumlularRes, paydaslarRes, projelerRes] = await Promise.all([
        kategoriAPI.getAll(),
        egitmenAPI.getAll(),
        sorumluAPI.getAll(),
        paydasAPI.getAll(),
        projeAPI.getAll(),
      ]);

      setOptions({
        kategoriler: kategorilerRes.data.map(k => ({ value: k.id, label: k.ad })),
        egitmenler: egitmenlerRes.data.map(e => ({ value: e.id, label: `${e.ad} ${e.soyad}` })),
        sorumlular: sorumlularRes.data.map(s => ({ value: s.id, label: `${s.ad} ${s.soyad}` })),
        paydaslar: paydaslarRes.data.map(p => ({ value: p.id, label: p.ad })),
        projeler: (projelerRes.data.content || projelerRes.data).map(p => ({ value: p.id, label: p.isim })),
      });
    } catch (err) {
      console.error('Options yüklenirken hata:', err);
    }
  };

  const loadEgitim = async () => {
    try {
      const response = await egitimAPI.getById(id);
      const egitim = response.data;

      // Tarih formatını düzelt (YYYY-MM-DD)
      const formatDate = (dateStr) => {
        if (!dateStr) return '';
        const date = new Date(dateStr);
        return date.toISOString().split('T')[0];
      };

      setFormData({
        ad: egitim.ad || '',
        egitimKodu: egitim.egitimKodu || '',
        programId: egitim.programId || '',
        seviye: egitim.seviye || '',
        hedefKitle: egitim.hedefKitle || '',
        aciklama: egitim.aciklama || '',
        baslangicTarihi: formatDate(egitim.baslangicTarihi),
        bitisTarihi: formatDate(egitim.bitisTarihi),
        egitimSaati: egitim.egitimSaati || '',
        durum: egitim.durum || 'Havuz',
        kategoriIds: egitim.kategoriler?.map(k => k.id) || [],
        egitmenIds: egitim.egitmenler?.map(e => e.id) || [],
        sorumluIds: egitim.sorumlular?.map(s => s.id) || [],
        paydasIds: egitim.paydaslar?.map(p => p.id) || [],
        projeId: egitim.proje?.id || null,
      });

      // Selected options'ı set et
      const hedefKitleArray = egitim.hedefKitle ? egitim.hedefKitle.split(',').map(h => h.trim()) : [];
      setSelectedOptions({
        kategoriler: egitim.kategoriler?.map(k => ({ value: k.id, label: k.ad })) || [],
        egitmenler: egitim.egitmenler?.map(e => ({ value: e.id, label: `${e.ad} ${e.soyad}` })) || [],
        sorumlular: egitim.sorumlular?.map(s => ({ value: s.id, label: `${s.ad} ${s.soyad}` })) || [],
        paydaslar: egitim.paydaslar?.map(p => ({ value: p.id, label: p.ad })) || [],
        hedefKitleler: hedefKitleArray.map(h => ({ value: h, label: h })),
      });
    } catch (err) {
      setError('Eğitim yüklenirken hata oluştu');
      console.error(err);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const data = {
        ...formData,
        kategoriIds: formData.kategoriIds,
        egitmenIds: formData.egitmenIds,
        sorumluIds: formData.sorumluIds,
        paydasIds: formData.paydasIds,
        egitimSaati: formData.egitimSaati ? parseInt(formData.egitimSaati) : null,
      };

      if (isEdit) {
        await egitimAPI.update(id, data);
      } else {
        await egitimAPI.create(data);
      }
      navigate('/egitim');
    } catch (err) {
      setError(err.response?.data?.message || 'Kayıt işlemi başarısız');
    } finally {
      setLoading(false);
    }
  };

  const handleMultiSelectChange = (field, selected) => {
    setSelectedOptions({ ...selectedOptions, [field]: selected });
    
    // Field name'i düzelt: kategoriler -> kategoriIds, egitmenler -> egitmenIds, vb.
    let fieldName;
    if (field === 'kategoriler') {
      fieldName = 'kategoriIds';
    } else if (field === 'egitmenler') {
      fieldName = 'egitmenIds';
    } else if (field === 'sorumlular') {
      fieldName = 'sorumluIds';
    } else if (field === 'paydaslar') {
      fieldName = 'paydasIds';
    }
    
    setFormData({
      ...formData,
      [fieldName]: selected ? selected.map(s => s.value) : [],
    });
  };

  return (
    <div>
      <Navbar />
      <div className="container">
        <div className="page-header">
          <h1>{isEdit ? 'Eğitim Düzenle' : 'Yeni Eğitim Ekle'}</h1>
          <button className="btn btn-secondary" onClick={() => navigate('/egitim')}>
            Geri
          </button>
        </div>

        {error && <div className="error">{error}</div>}

        <div className="card">
          <form onSubmit={handleSubmit}>
            <div className="form-row">
              <div className="form-group">
                <label>Eğitim Adı *</label>
                <input
                  type="text"
                  className="form-control"
                  value={formData.ad}
                  onChange={(e) => setFormData({ ...formData, ad: e.target.value })}
                  required
                />
              </div>

              <div className="form-group">
                <label>Durum</label>
                <select
                  className="form-control"
                  value={formData.durum}
                  onChange={(e) => setFormData({ ...formData, durum: e.target.value })}
                >
                  <option value="Havuz">Havuz</option>
                  <option value="Teslim">Teslim</option>
                  <option value="Gerçekleşme">Gerçekleşme</option>
                  <option value="Tamamlanan">Tamamlanan</option>
                  <option value="Kontrol">Kontrol</option>
                  <option value="Medya">Medya</option>
                  <option value="LMS">LMS</option>
                  <option value="Plan">Plan</option>
                  <option value="İptal">İptal</option>
                  <option value="Yayından Kaldırılan">Yayından Kaldırılan</option>
                  <option value="Anlaşma">Anlaşma</option>
                  <option value="İlan">İlan</option>
                </select>
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Eğitim Kodu</label>
                <input
                  type="text"
                  className="form-control"
                  value={formData.egitimKodu}
                  onChange={(e) => setFormData({ ...formData, egitimKodu: e.target.value })}
                  placeholder="Örn: EGT-2024-001"
                  maxLength={50}
                />
              </div>

              <div className="form-group">
                <label>Program ID</label>
                <input
                  type="text"
                  className="form-control"
                  value={formData.programId}
                  onChange={(e) => setFormData({ ...formData, programId: e.target.value })}
                  placeholder="Örn: PRG-2024-001"
                  maxLength={50}
                />
              </div>

              <div className="form-group">
                <label>Seviye</label>
                <select
                  className="form-control"
                  value={formData.seviye}
                  onChange={(e) => setFormData({ ...formData, seviye: e.target.value })}
                >
                  <option value="">Seçiniz...</option>
                  <option value="Temel">Temel</option>
                  <option value="Orta">Orta</option>
                  <option value="İleri">İleri</option>
                </select>
              </div>
            </div>

            <div className="form-group">
              <label>Hedef Kitle</label>
              <Select
                isMulti
                options={[
                  { value: 'Ortaokul', label: 'Ortaokul' },
                  { value: 'Lise', label: 'Lise' },
                  { value: 'Üniversite', label: 'Üniversite' },
                  { value: 'Kurum Personeli', label: 'Kurum Personeli' },
                  { value: 'Mezun', label: 'Mezun' },
                  { value: 'Diğer', label: 'Diğer' }
                ]}
                value={selectedOptions.hedefKitleler}
                onChange={(selected) => {
                  setSelectedOptions({ ...selectedOptions, hedefKitleler: selected || [] });
                  setFormData({
                    ...formData,
                    hedefKitle: selected ? selected.map(s => s.value).join(', ') : ''
                  });
                }}
                placeholder="Hedef kitle seçin..."
              />
            </div>

            <div className="form-group">
              <label>Açıklama</label>
              <textarea
                className="form-control"
                rows="3"
                value={formData.aciklama}
                onChange={(e) => setFormData({ ...formData, aciklama: e.target.value })}
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Başlangıç Tarihi</label>
                <input
                  type="date"
                  className="form-control"
                  value={formData.baslangicTarihi}
                  onChange={(e) => setFormData({ ...formData, baslangicTarihi: e.target.value })}
                />
              </div>

              <div className="form-group">
                <label>Bitiş Tarihi</label>
                <input
                  type="date"
                  className="form-control"
                  value={formData.bitisTarihi}
                  onChange={(e) => setFormData({ ...formData, bitisTarihi: e.target.value })}
                />
              </div>

              <div className="form-group">
                <label>Eğitim Saati (dakika)</label>
                <input
                  type="number"
                  className="form-control"
                  value={formData.egitimSaati}
                  onChange={(e) => setFormData({ ...formData, egitimSaati: e.target.value })}
                  placeholder="480"
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Kategoriler</label>
                <Select
                  isMulti
                  options={options.kategoriler}
                  value={selectedOptions.kategoriler}
                  onChange={(selected) => handleMultiSelectChange('kategoriler', selected || [])}
                  placeholder="Kategori seçin..."
                />
              </div>

              <div className="form-group">
                <label>Eğitmenler</label>
                <Select
                  isMulti
                  options={options.egitmenler}
                  value={selectedOptions.egitmenler}
                  onChange={(selected) => handleMultiSelectChange('egitmenler', selected || [])}
                  placeholder="Eğitmen seçin..."
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Eğitim Sorumluları</label>
                <Select
                  isMulti
                  options={options.sorumlular}
                  value={selectedOptions.sorumlular}
                  onChange={(selected) => handleMultiSelectChange('sorumlular', selected || [])}
                  placeholder="Sorumlu seçin..."
                />
              </div>

              <div className="form-group">
                <label>Paydaşlar</label>
                <Select
                  isMulti
                  options={options.paydaslar}
                  value={selectedOptions.paydaslar}
                  onChange={(selected) => handleMultiSelectChange('paydaslar', selected || [])}
                  placeholder="Paydaş seçin..."
                />
              </div>
            </div>

            <div className="form-group">
              <label>Proje</label>
              <Select
                options={options.projeler}
                value={options.projeler.find(p => p.value === formData.projeId)}
                onChange={(selected) => setFormData({ ...formData, projeId: selected ? selected.value : null })}
                isClearable
                placeholder="Proje seçin..."
              />
            </div>

            <div className="form-actions">
              <button type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? 'Kaydediliyor...' : isEdit ? 'Güncelle' : 'Kaydet'}
              </button>
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => navigate('/egitim')}
              >
                İptal
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export default EgitimForm;

