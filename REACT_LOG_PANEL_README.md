# React Admin Log Panel

## Genel Bakış

Material-UI (MUI) kullanılarak oluşturulmuş, kapsamlı log görüntüleme ve yönetim sistemi.

## Oluşturulan Dosyalar

### 📁 Log Components (6 adet)

1. **LogDashboard.js** - Ana log yönetimi dashboard'u
2. **ApiLogs.js** - API request/response logları
3. **ActivityLogs.js** - Kullanıcı aksiyon logları
4. **ErrorLogs.js** - Hata ve exception logları
5. **PerformanceLogs.js** - Performans logları (yavaş işlemler)
6. **FrontendLogs.js** - Frontend kullanıcı aksiyonları

### 📝 Updated Files

7. **App.js** - Log route'ları eklendi
8. **Navbar.js** - "Log Yönetimi" menü butonu eklendi (sadece ADMIN)
9. **package.json** - MUI dependencies eklendi

## Kurulum

### 1. MUI ve Bağımlılıklarını Yükle

```bash
cd frontend
npm install
```

Yüklenen paketler:
- `@mui/material` - Material-UI core
- `@mui/icons-material` - MUI iconları
- `@emotion/react` - MUI styling dependency
- `@emotion/styled` - MUI styling dependency

### 2. Backend'i Başlat

```bash
cd ..
mvn spring-boot:run
```

### 3. Frontend'i Başlat

```bash
cd frontend
npm start
```

## UI Özellikleri

### 🏠 Log Dashboard

Ana log yönetimi ekranı. 5 log türü için kartlar:

- **API Logları** (Mavi) - HTTP request/response
- **Kullanıcı Aksiyon Logları** (Yeşil) - CREATE, UPDATE, DELETE
- **Hata Logları** (Kırmızı) - Exception'lar
- **Performans Logları** (Turuncu) - Yavaş işlemler
- **Frontend Logları** (Mor) - Frontend aksiyonları

Her kart tıklanabilir, ilgili log sayfasına yönlendirir.

### 📊 Her Log Sayfası

#### Özellikler:
- ✅ **Pagination** - Sayfa başına 10/20/50/100 kayıt
- ✅ **Filtreleme** - Çoklu filtre desteği
- ✅ **Search** - Text-based arama
- ✅ **Table UI** - Material-UI Table component
- ✅ **Detay Modal** - JSON pretty print
- ✅ **Yenile** - Refresh button
- ✅ **Sorting** - Otomatik (en yeni/yavaş ilk)
- ✅ **Responsive** - Mobil uyumlu

#### Filtreler:

**API Logs:**
- User ID
- Status Code (200, 404, 500, vb.)
- Endpoint
- Min Duration (ms)

**Activity Logs:**
- User ID
- Entity Type (Egitim, Proje, vb.)
- Action (CREATE, UPDATE, DELETE)
- Entity ID

**Error Logs:**
- User ID
- Exception Type
- Endpoint

**Performance Logs:**
- Min Duration (ms)
- Method/Endpoint

**Frontend Logs:**
- User ID
- Action
- Page

## Menü Yapısı

### Navbar Güncellemesi

"Log Yönetimi" butonu **sadece ADMIN kullanıcılar için** görünür:

```javascript
{user.rol === 'ADMIN' && (
  <button className="nav-link" onClick={() => navigate('/logs')}>
    Log Yönetimi
  </button>
)}
```

### Route Yapısı

```
/logs                  → LogDashboard (Ana sayfa)
/logs/api              → ApiLogs
/logs/activity         → ActivityLogs
/logs/errors           → ErrorLogs
/logs/performance      → PerformanceLogs
/logs/frontend         → FrontendLogs
```

## Ekran Görüntüleri (Yapı)

### 1. Log Dashboard

```
┌─────────────────────────────────────────────────┐
│  Log Yönetimi                                   │
│  Sistem loglarını görüntüleyin ve analiz edin  │
├─────────────────────────────────────────────────┤
│  ┌─────────┐  ┌─────────┐  ┌─────────┐        │
│  │  API    │  │ Activity│  │  Error  │        │
│  │  Logs   │  │  Logs   │  │  Logs   │        │
│  └─────────┘  └─────────┘  └─────────┘        │
│  ┌─────────┐  ┌─────────┐                      │
│  │Perform. │  │Frontend │                      │
│  │  Logs   │  │  Logs   │                      │
│  └─────────┘  └─────────┘                      │
├─────────────────────────────────────────────────┤
│  Hızlı Bilgiler                                 │
│  [1234 API]  [567 Activity]  [12 Error]  [8 Slow]│
└─────────────────────────────────────────────────┘
```

### 2. API Logs Sayfası

```
┌─────────────────────────────────────────────────┐
│  API Logları                                     │
├─────────────────────────────────────────────────┤
│  🔍 Filtreler                                    │
│  [User ID] [Status Code ▼] [Endpoint] [Filtrele]│
├─────────────────────────────────────────────────┤
│  Toplam: 234 kayıt                    [Yenile]  │
│  ┌────┬──────────┬────────┬────────┬─────────┐ │
│  │ ID │ Endpoint │ Method │ Status │  Süre   │ │
│  ├────┼──────────┼────────┼────────┼─────────┤ │
│  │ 1  │ /egitim  │  GET   │  200   │  123ms  │ │
│  │ 2  │ /proje   │  POST  │  201   │  456ms  │ │
│  └────┴──────────┴────────┴────────┴─────────┘ │
│  [< Önceki] Sayfa 1 / 12 [Sonraki >]           │
└─────────────────────────────────────────────────┘
```

### 3. Detay Modal

```
┌────────────────────────────────────┐
│  API Log Detayı #123          [X]  │
├────────────────────────────────────┤
│  {                                 │
│    "id": 123,                      │
│    "endpoint": "/egitim",          │
│    "httpMethod": "GET",            │
│    "statusCode": 200,              │
│    "requestBody": null,            │
│    "responseBody": "[...]",        │
│    "durationMs": 123,              │
│    "ip": "127.0.0.1",              │
│    "createdAt": "2024-12-04..."    │
│  }                                 │
│                                    │
│                        [Kapat]     │
└────────────────────────────────────┘
```

## Kullanım

### 1. Admin Girişi

```
Email: admin@akademi.com
Password: admin123
```

### 2. Log Yönetimine Git

Navbar'da **"Log Yönetimi"** butonuna tıklayın (sadece ADMIN kullanıcılar görür).

### 3. Log Türünü Seç

Dashboard'da istediğiniz log türüne tıklayın.

### 4. Filtrele ve Ara

- Filtre alanlarını doldurun
- "Filtrele" butonuna tıklayın
- Sonuçları görüntüleyin

### 5. Detay Görüntüle

Her satırdaki 👁️ (göz) iconuna tıklayarak JSON detayını görün.

## Kod Örnekleri

### API Çağrısı (React)

```javascript
// ApiLogs.js içinde
const fetchLogs = async () => {
  const params = {
    page: 0,
    size: 20,
    userId: 1,
    statusCode: 200,
    minDuration: 1000
  };

  const response = await axios.get('/api/logs/api', { params });
  setLogs(response.data.content);
  setTotalElements(response.data.totalElements);
};
```

### Filtre Uygulama

```javascript
const handleApplyFilters = () => {
  setPage(0);  // İlk sayfaya dön
  fetchLogs(); // Yeni filtreyle veri çek
};
```

### Modal Açma

```javascript
const handleViewDetails = (log) => {
  setSelectedLog(log);
  setOpenModal(true);
};
```

## MUI Component'leri

Kullanılan MUI component'leri:

- **Box** - Container
- **Paper** - Card container
- **Table** - Data table
- **TablePagination** - Pagination
- **TextField** - Input field
- **Select** - Dropdown
- **Button** - Action button
- **Chip** - Badge/label
- **Dialog** - Modal
- **Grid** - Layout
- **Typography** - Text
- **IconButton** - Icon button
- **Card** - Dashboard kartları

## Renk Kodları

### Status Code Colors (API Logs)
- 2xx (Success) - Yeşil
- 4xx (Client Error) - Turuncu
- 5xx (Server Error) - Kırmızı

### Action Colors (Activity Logs)
- CREATE - Yeşil
- UPDATE - Mavi
- DELETE - Kırmızı
- VIEW - Gri
- EXPORT - Turuncu

### Duration Colors (Performance Logs)
- < 2s - Turuncu
- 2-5s - Kırmızı
- > 5s - Koyu kırmızı

## Pagination

Her tablo pagination destekler:

- Sayfa başına: 10, 20, 50, 100 kayıt
- Sayfa navigasyonu: İlk, Önceki, Sonraki, Son
- Toplam kayıt sayısı gösterimi
- "1-20 / 234" formatında bilgi

## JSON Pretty Print

Modal'da JSON verisi düzgün formatlı gösterilir:

```javascript
<pre style={{
  backgroundColor: '#f5f5f5',
  padding: '16px',
  borderRadius: '4px',
  overflow: 'auto',
  maxHeight: '500px'
}}>
  {JSON.stringify(selectedLog, null, 2)}
</pre>
```

## Güvenlik

- ✅ Sadece ADMIN kullanıcılar erişebilir
- ✅ JWT token kontrolü
- ✅ Backend'de `@PreAuthorize("hasRole('ADMIN')")`
- ✅ Frontend'de `user.rol === 'ADMIN'` kontrolü

## Test Etme

### 1. Backend ve Frontend'i Başlat

```bash
# Terminal 1: Backend
cd C:\Users\MET\Training_Tracking
mvn spring-boot:run

# Terminal 2: Frontend
cd C:\Users\MET\Training_Tracking\frontend
npm start
```

### 2. Admin Olarak Giriş Yap

```
http://localhost:3000/login
Email: admin@akademi.com
Password: admin123
```

### 3. Log Yönetimine Git

Navbar'da **"Log Yönetimi"** butonuna tıkla.

### 4. Log Türünü Seç

Dashboard'da bir kart seç (örn. "API Logları").

### 5. Filtreleri Kullan

- User ID: `1`
- Status Code: `200`
- "Filtrele" butonuna tıkla

### 6. Detay Görüntüle

Bir satırdaki göz iconuna tıklayarak JSON detayını görüntüle.

## Sorun Giderme

### Problem: MUI component'leri yüklenmiyor

**Çözüm:**
```bash
cd frontend
npm install @mui/material @mui/icons-material @emotion/react @emotion/styled
```

### Problem: "Log Yönetimi" butonu görünmüyor

**Çözüm:**
- ADMIN rolüyle giriş yapın: `admin@akademi.com` / `admin123`
- `user.rol === 'ADMIN'` kontrolü yapılıyor

### Problem: Log kayıtları gelmiyor

**Çözüm:**
1. Backend çalışıyor mu? → `http://localhost:8080/api/logs/api`
2. ADMIN yetkisi var mı?
3. Console'da hata var mı? (F12)

## Özellikler Özeti

✅ **5 Log Türü:** API, Activity, Error, Performance, Frontend
✅ **Material-UI:** Modern ve profesyonel UI
✅ **Pagination:** Spring Data Page entegrasyonu
✅ **Filtreleme:** Çoklu filtre desteği
✅ **Search:** Text-based arama
✅ **Responsive:** Mobil uyumlu
✅ **Modal:** JSON pretty print
✅ **Icons:** MUI Icons
✅ **Colors:** Semantic renk kodlaması
✅ **ADMIN Only:** Role-based access
✅ **Dashboard:** Hızlı erişim kartları

## Ekran Akışı

```
Login (admin@akademi.com)
    ↓
Navbar → "Log Yönetimi" (Mor buton)
    ↓
Log Dashboard (5 kart)
    ↓
Log Sayfası Seç (örn. API Logs)
    ↓
Filtrele & Ara
    ↓
Detay Modal (👁️ icon)
    ↓
JSON Pretty Print
```

## Component Yapısı

```
frontend/src/components/logs/
├── LogDashboard.js     (Ana dashboard)
├── ApiLogs.js          (API logları)
├── ActivityLogs.js     (Activity logları)
├── ErrorLogs.js        (Error logları)
├── PerformanceLogs.js  (Performance logları)
└── FrontendLogs.js     (Frontend logları)
```

## API Endpoints (Backend)

| Endpoint | Method | Filtreler |
|----------|--------|-----------|
| `/api/logs/api` | GET | userId, statusCode, endpoint, minDuration, date |
| `/api/logs/activity` | GET | userId, entityType, action, entityId, date |
| `/api/logs/errors` | GET | userId, exceptionType, endpoint, date |
| `/api/logs/performance` | GET | minDuration, endpoint, date |
| `/api/logs/frontend` | GET | userId, action, page, date |

## Örnek Kullanım Senaryoları

### Senaryo 1: Yavaş API'leri Bul

1. "API Logları" kartına tıkla
2. "Min Süre (ms)" → `1000` yaz
3. "Filtrele" butonuna tıkla
4. En yavaş istekleri görüntüle

### Senaryo 2: Kullanıcı Aktivitelerini İzle

1. "Kullanıcı Aksiyon Logları" kartına tıkla
2. "Entity Type" → `Egitim` seç
3. "Action" → `DELETE` seç
4. Silme işlemlerini görüntüle

### Senaryo 3: Hataları Analiz Et

1. "Hata Logları" kartına tıkla
2. "Exception Type" → `NullPointerException` yaz
3. Stack trace'leri incele
4. Tekrarlanan hataları tespit et

### Senaryo 4: Performans Sorunlarını Tespit Et

1. "Performans Logları" kartına tıkla
2. "Min Süre" → `2000` (2 saniye)
3. Kritik yavaş işlemleri görüntüle
4. Optimizasyon gereken metodları belirle

## İleri Seviye

### Custom Hook (useLogFilters)

```javascript
// hooks/useLogFilters.js
import { useState } from 'react';

export const useLogFilters = (initialFilters) => {
  const [filters, setFilters] = useState(initialFilters);

  const handleFilterChange = (field, value) => {
    setFilters({ ...filters, [field]: value });
  };

  const clearFilters = () => {
    setFilters(initialFilters);
  };

  return { filters, handleFilterChange, clearFilters };
};

// Kullanım
const { filters, handleFilterChange, clearFilters } = useLogFilters({
  userId: '',
  statusCode: ''
});
```

### Export to Excel

```javascript
const exportToExcel = () => {
  // Tüm logları export et
  axios.get('/api/logs/api/export', {
    params: filters,
    responseType: 'blob'
  }).then(response => {
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', 'api_logs.xlsx');
    document.body.appendChild(link);
    link.click();
  });
};
```

### Real-time Updates (WebSocket)

```javascript
useEffect(() => {
  const socket = new WebSocket('ws://localhost:8080/logs/stream');
  
  socket.onmessage = (event) => {
    const newLog = JSON.parse(event.data);
    setLogs(prev => [newLog, ...prev]);
  };

  return () => socket.close();
}, []);
```

## Best Practices

1. **Pagination:** Her zaman pagination kullanın (performans)
2. **Filtreleme:** Gereksiz veri çekmeyin
3. **Yenileme:** Otomatik yenileme yerine manuel yenile butonu
4. **Detay:** Stack trace gibi uzun metinler modal'da gösterin
5. **Role Check:** ADMIN kontrolü hem frontend hem backend'de

## Performans

- Pagination ile veri miktarı kontrol altında
- Lazy loading ile sadece görünen sayfa çekilir
- Filtreleme backend'de yapılır
- Modal lazy render (sadece açıldığında içerik render edilir)

## Notlar

- MUI 5.14.20 kullanılıyor
- React 18.2.0 uyumlu
- Responsive design (xs, sm, md breakpoints)
- Dark mode desteği eklenebilir
- Grafik ve chart'lar eklenebilir (Chart.js, Recharts)

Artık admin panelinde profesyonel bir log yönetim sistemi var! 🎉





