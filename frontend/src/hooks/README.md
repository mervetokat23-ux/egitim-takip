# useEventLogger Hook

Frontend'de kullanıcı aksiyonlarını backend'e otomatik olarak loglamak için React custom hook.

## 🎯 Özellikler

- ✅ **Button Click** loglaması
- ✅ **Page View** loglaması (otomatik)
- ✅ **Form Submit** loglaması
- ✅ **Custom Event** loglaması
- ✅ Kullanıcı ID otomatik eklenir
- ✅ Mevcut sayfa path'i otomatik eklenir
- ✅ Hata durumunda kullanıcı deneyimini etkilemez

## 📦 Kurulum

Hook zaten `frontend/src/hooks/useEventLogger.js` dosyasında mevcut.

```javascript
import useEventLogger from '../hooks/useEventLogger';
// veya
import { useEventLogger } from '../hooks';
```

## 🚀 Kullanım

### 1. Otomatik Page View Loglaması

Hook import edildiğinde otomatik olarak sayfa görüntülemeyi loglar:

```javascript
import useEventLogger from '../hooks/useEventLogger';

function MyComponent() {
  // Sadece hook'u çağır, sayfa görüntüleme otomatik loglanır
  useEventLogger();

  return <div>İçerik...</div>;
}
```

**Loglanan Veri:**
```json
{
  "userId": 5,
  "action": "PAGE_VIEW",
  "page": "/egitim",
  "details": "Viewed /egitim"
}
```

### 2. Button Click Loglaması

```javascript
import useEventLogger from '../hooks/useEventLogger';

function MyComponent() {
  const { logButtonClick } = useEventLogger();

  return (
    <div>
      {/* Basit kullanım */}
      <button onClick={() => logButtonClick('Save Button')}>
        Kaydet
      </button>

      {/* Ek detay ile */}
      <button onClick={() => logButtonClick('Delete Button', 'Egitim ID: 123')}>
        Sil
      </button>

      {/* Diğer işlemlerle birlikte */}
      <button 
        onClick={() => {
          logButtonClick('Export Button', 'Excel format');
          handleExport();
        }}
      >
        Excel'e Aktar
      </button>
    </div>
  );
}
```

**Loglanan Veri:**
```json
{
  "userId": 5,
  "action": "BUTTON_CLICK",
  "page": "/egitim",
  "details": "Delete Button - Egitim ID: 123"
}
```

### 3. Form Submit Loglaması

```javascript
import React, { useState } from 'react';
import useEventLogger from '../hooks/useEventLogger';

function LoginForm() {
  const { logFormSubmit } = useEventLogger();
  const [email, setEmail] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Form submit'i logla
    logFormSubmit('Login Form', `Email: ${email}`);
    
    // Normal form işlemleri
    await loginUser(email);
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
      />
      <button type="submit">Giriş Yap</button>
    </form>
  );
}
```

**Loglanan Veri:**
```json
{
  "userId": null,
  "action": "FORM_SUBMIT",
  "page": "/login",
  "details": "Login Form - Email: user@example.com"
}
```

### 4. Custom Event Loglaması

```javascript
import useEventLogger from '../hooks/useEventLogger';

function MyComponent() {
  const { logCustomEvent } = useEventLogger();

  const handleDownload = () => {
    logCustomEvent('FILE_DOWNLOAD', 'Report_2024.pdf');
    // Download işlemi...
  };

  const handleFilter = (filterData) => {
    logCustomEvent('FILTER_APPLIED', JSON.stringify(filterData));
    // Filtreleme işlemi...
  };

  return (
    <div>
      <button onClick={handleDownload}>Rapor İndir</button>
      <button onClick={() => handleFilter({ status: 'active', year: 2024 })}>
        Filtrele
      </button>
    </div>
  );
}
```

**Loglanan Veri:**
```json
{
  "userId": 5,
  "action": "FILE_DOWNLOAD",
  "page": "/reports",
  "details": "Report_2024.pdf"
}
```

## 📊 Backend API

Hook aşağıdaki endpoint'e POST request gönderir:

```
POST /api/logs/frontend
Content-Type: application/json

{
  "userId": 5,          // Otomatik eklenir (localStorage'dan)
  "action": "BUTTON_CLICK",
  "page": "/egitim",    // Otomatik eklenir (react-router location)
  "details": "Save Button clicked"
}
```

## 🔧 Hook Metodları

| Metod | Parametreler | Açıklama |
|-------|-------------|----------|
| `logPageView()` | - | Sayfa görüntülemeyi loglar (otomatik) |
| `logButtonClick(buttonName, details?)` | buttonName: string, details?: string | Button tıklamasını loglar |
| `logFormSubmit(formName, details?)` | formName: string, details?: string | Form submit'ini loglar |
| `logCustomEvent(action, details?)` | action: string, details?: string | Özel event'i loglar |

## 💡 İyi Pratikler

### ✅ YAPILMASI GEREKENLER:

1. **Button isimlerini açıklayıcı yapın:**
   ```javascript
   logButtonClick('Yeni Eğitim Ekle Button')  // ✅ İyi
   logButtonClick('Button 1')                 // ❌ Kötü
   ```

2. **Önemli bağlamı details'e ekleyin:**
   ```javascript
   logButtonClick('Delete', `Egitim ID: ${id}`)  // ✅ İyi
   logButtonClick('Delete')                       // ⚠️ Eksik
   ```

3. **Form isimlerini benzersiz yapın:**
   ```javascript
   logFormSubmit('User Registration Form')   // ✅ İyi
   logFormSubmit('Form')                      // ❌ Kötü
   ```

### ❌ YAPILMAMASI GEREKENLER:

1. **Hassas bilgileri loglama:**
   ```javascript
   logFormSubmit('Login', `Password: ${password}`)  // ❌ ASLA!
   logFormSubmit('Login', `Email: ${email}`)        // ✅ Güvenli
   ```

2. **Çok sık loglama (her render'da):**
   ```javascript
   // ❌ Her render'da loglar
   useEffect(() => {
     logButtonClick('Component rendered');
   });
   
   // ✅ Sadece kullanıcı aksiyonunda loglar
   <button onClick={() => logButtonClick('Action')}>Click</button>
   ```

## 🎨 Gerçek Örnekler

### EgitimList Component'inde Kullanım

```javascript
import useEventLogger from '../hooks/useEventLogger';

function EgitimList() {
  const { logButtonClick } = useEventLogger(); // Page view otomatik
  
  return (
    <div>
      <button 
        onClick={() => {
          logButtonClick('Yeni Eğitim Ekle Button');
          navigate('/egitim/new');
        }}
      >
        Yeni Eğitim Ekle
      </button>
      
      <button 
        onClick={() => {
          logButtonClick('Eğitim Sil Button', `ID: ${egitim.id}`);
          handleDelete(egitim.id);
        }}
      >
        Sil
      </button>
    </div>
  );
}
```

### Login Component'inde Kullanım

```javascript
import useEventLogger from '../hooks/useEventLogger';

function Login() {
  const { logFormSubmit, logButtonClick } = useEventLogger();
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      logFormSubmit('Login Form', `Email: ${email}`);
      const response = await authAPI.login({ email, password });
      
      logButtonClick('Login Success', `User: ${email}`);
      navigate('/egitim');
    } catch (err) {
      logButtonClick('Login Failed', `Email: ${email}`);
      setError(err.message);
    }
  };
  
  return <form onSubmit={handleSubmit}>...</form>;
}
```

## 🐛 Debug & Test

Hook, hata durumunda console'a debug mesajı yazar ama kullanıcı deneyimini etkilemez:

```javascript
// Tarayıcı console'unda:
Event logging failed: Error: Network Error
```

Logların backend'e ulaştığını kontrol etmek için:

1. Browser DevTools → Network → POST /api/logs/frontend
2. Backend log ekranında: Navbar → Log Yönetimi → Frontend Logları

## 📝 Notlar

- Hook, kullanıcı bilgisini `localStorage.getItem('user')` ile alır
- Sayfa path'ini `react-router-dom`'un `useLocation` hook'u ile alır
- API çağrısı başarısız olursa hata sessizce yutulur (UX etkilenmez)
- Her sayfa değişiminde otomatik PAGE_VIEW logu oluşturulur

## 🔗 İlgili Dosyalar

- `frontend/src/hooks/useEventLogger.js` - Hook implementasyonu
- `frontend/src/hooks/useEventLogger.example.js` - Detaylı örnekler
- `frontend/src/components/Login.js` - Gerçek kullanım (form submit)
- `frontend/src/components/EgitimList.js` - Gerçek kullanım (button click)
- Backend: `src/main/java/com/akademi/egitimtakip/controller/LogController.java`


