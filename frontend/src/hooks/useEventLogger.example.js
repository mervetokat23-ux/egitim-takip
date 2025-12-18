/**
 * useEventLogger Hook - Kullanım Örnekleri
 * 
 * Bu dosya hook'un nasıl kullanılacağını gösterir.
 * Gerçek component'lerde kullanım için referans.
 */

import React, { useState } from 'react';
import useEventLogger from './useEventLogger';

// ÖRNEK 1: Basit Button Click Loglaması
function ExampleButtonLogging() {
  const { logButtonClick } = useEventLogger();

  return (
    <div>
      <button onClick={() => logButtonClick('Save Button')}>
        Kaydet
      </button>
      
      <button onClick={() => logButtonClick('Delete Button', 'User ID: 123')}>
        Sil
      </button>
    </div>
  );
}

// ÖRNEK 2: Form Submit Loglaması
function ExampleFormLogging() {
  const { logFormSubmit } = useEventLogger();
  const [formData, setFormData] = useState({ name: '', email: '' });

  const handleSubmit = (e) => {
    e.preventDefault();
    
    // Form submit'i logla
    logFormSubmit('User Registration Form', `Email: ${formData.email}`);
    
    // Normal form işlemleri
    console.log('Form submitted:', formData);
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        value={formData.name}
        onChange={(e) => setFormData({ ...formData, name: e.target.value })}
        placeholder="İsim"
      />
      <input
        type="email"
        value={formData.email}
        onChange={(e) => setFormData({ ...formData, email: e.target.value })}
        placeholder="Email"
      />
      <button type="submit">Gönder</button>
    </form>
  );
}

// ÖRNEK 3: Otomatik Page View (sadece import etmek yeterli)
function ExamplePageView() {
  // Hook import edildiğinde otomatik olarak sayfa görüntülemeyi loglar
  useEventLogger();

  return (
    <div>
      <h1>Bu sayfanın görüntülenmesi otomatik loglandı</h1>
    </div>
  );
}

// ÖRNEK 4: Custom Event Loglaması
function ExampleCustomEvent() {
  const { logCustomEvent } = useEventLogger();

  const handleDownload = () => {
    logCustomEvent('FILE_DOWNLOAD', 'Report_2024.pdf');
    // Download işlemi...
  };

  const handleShare = () => {
    logCustomEvent('SHARE_CONTENT', 'Egitim ID: 456');
    // Share işlemi...
  };

  return (
    <div>
      <button onClick={handleDownload}>Rapor İndir</button>
      <button onClick={handleShare}>Paylaş</button>
    </div>
  );
}

// ÖRNEK 5: Tam Entegre Component
function FullExample() {
  const { logButtonClick, logFormSubmit, logCustomEvent } = useEventLogger();
  const [data, setData] = useState({ title: '' });

  return (
    <div>
      {/* Page view otomatik loglandı */}
      
      <h1>Eğitim Yönetimi</h1>
      
      {/* Button clicks */}
      <button onClick={() => logButtonClick('New Training Button')}>
        Yeni Eğitim
      </button>
      
      <button 
        onClick={() => {
          logButtonClick('Export Button', 'Excel format');
          // Export işlemi...
        }}
      >
        Excel'e Aktar
      </button>
      
      {/* Form submit */}
      <form onSubmit={(e) => {
        e.preventDefault();
        logFormSubmit('Training Creation Form', `Title: ${data.title}`);
        // Form işlemi...
      }}>
        <input
          type="text"
          value={data.title}
          onChange={(e) => setData({ title: e.target.value })}
          placeholder="Eğitim Başlığı"
        />
        <button type="submit">Kaydet</button>
      </form>
      
      {/* Custom events */}
      <button 
        onClick={() => {
          logCustomEvent('FILTER_APPLIED', 'Status: Active, Year: 2024');
          // Filtreleme işlemi...
        }}
      >
        Filtrele
      </button>
    </div>
  );
}

export {
  ExampleButtonLogging,
  ExampleFormLogging,
  ExamplePageView,
  ExampleCustomEvent,
  FullExample
};


