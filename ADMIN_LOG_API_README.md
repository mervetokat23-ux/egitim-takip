# Admin Panel Log API

## Genel Bakış

Admin paneli için kapsamlı log görüntüleme REST API. 5 farklı log türü için pagination ve filtreleme desteği sağlar.

## API Endpoint'leri

Tüm endpoint'ler **sadece ADMIN** kullanıcılar tarafından erişilebilir.

Base URL: `/api/logs`

### 1. API Logs
**GET** `/api/logs/api`

HTTP isteklerinin loglarını getirir.

**Query Parameters:**
| Parametre | Tip | Zorunlu | Varsayılan | Açıklama |
|-----------|-----|---------|------------|----------|
| page | int | Hayır | 0 | Sayfa numarası |
| size | int | Hayır | 20 | Sayfa boyutu |
| userId | Long | Hayır | - | Kullanıcı ID filtresi |
| startDate | DateTime | Hayır | - | Başlangıç tarihi (ISO 8601) |
| endDate | DateTime | Hayır | - | Bitiş tarihi (ISO 8601) |
| statusCode | int | Hayır | - | HTTP durum kodu (200, 404, 500 vb.) |
| endpoint | String | Hayır | - | Endpoint filtresi (contains) |
| minDuration | Long | Hayır | - | Minimum süre (ms) |

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "userId": 1,
      "endpoint": "/egitim",
      "httpMethod": "GET",
      "statusCode": 200,
      "requestBody": null,
      "responseBody": "[...]",
      "durationMs": 123,
      "ip": "127.0.0.1",
      "createdAt": "2024-12-04T12:30:15"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalPages": 5,
  "totalElements": 95,
  "last": false,
  "first": true
}
```

**Örnek:**
```bash
# Tüm API logları
curl -H "Authorization: Bearer TOKEN" \
  "http://localhost:8080/api/logs/api?page=0&size=20"

# Belirli kullanıcının logları
curl -H "Authorization: Bearer TOKEN" \
  "http://localhost:8080/api/logs/api?userId=1"

# 500 hataları
curl -H "Authorization: Bearer TOKEN" \
  "http://localhost:8080/api/logs/api?statusCode=500"

# Yavaş istekler (1 saniyeden uzun)
curl -H "Authorization: Bearer TOKEN" \
  "http://localhost:8080/api/logs/api?minDuration=1000"

# Tarih aralığı
curl -H "Authorization: Bearer TOKEN" \
  "http://localhost:8080/api/logs/api?startDate=2024-12-01T00:00:00&endDate=2024-12-31T23:59:59"
```

### 2. Activity Logs
**GET** `/api/logs/activity`

Kullanıcı aktivitelerini getirir (CREATE, UPDATE, DELETE vb.)

**Query Parameters:**
| Parametre | Tip | Zorunlu | Varsayılan | Açıklama |
|-----------|-----|---------|------------|----------|
| page | int | Hayır | 0 | Sayfa numarası |
| size | int | Hayır | 20 | Sayfa boyutu |
| userId | Long | Hayır | - | Kullanıcı ID filtresi |
| startDate | DateTime | Hayır | - | Başlangıç tarihi |
| endDate | DateTime | Hayır | - | Bitiş tarihi |
| entityType | String | Hayır | - | Entity türü (Egitim, Proje, vb.) |
| action | String | Hayır | - | Aksiyon türü (CREATE, UPDATE, DELETE) |
| entityId | Long | Hayır | - | Entity ID |

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "userId": 1,
      "action": "CREATE",
      "entityType": "Egitim",
      "entityId": 123,
      "description": "Yeni eğitim oluşturuldu: Java Eğitimi",
      "createdAt": "2024-12-04T12:30:15"
    }
  ],
  "totalElements": 45
}
```

**Örnek:**
```bash
# Tüm aktiviteler
curl -H "Authorization: Bearer TOKEN" \
  "http://localhost:8080/api/logs/activity"

# Eğitim aktiviteleri
curl -H "Authorization: Bearer TOKEN" \
  "http://localhost:8080/api/logs/activity?entityType=Egitim"

# Silme işlemleri
curl -H "Authorization: Bearer TOKEN" \
  "http://localhost:8080/api/logs/activity?action=DELETE"

# Belirli bir eğitimin tüm değişiklikleri
curl -H "Authorization: Bearer TOKEN" \
  "http://localhost:8080/api/logs/activity?entityType=Egitim&entityId=123"
```

### 3. Error Logs
**GET** `/api/logs/errors`

Uygulama hatalarını getirir.

**Query Parameters:**
| Parametre | Tip | Zorunlu | Varsayılan | Açıklama |
|-----------|-----|---------|------------|----------|
| page | int | Hayır | 0 | Sayfa numarası |
| size | int | Hayır | 20 | Sayfa boyutu |
| userId | Long | Hayır | - | Kullanıcı ID filtresi |
| startDate | DateTime | Hayır | - | Başlangıç tarihi |
| endDate | DateTime | Hayır | - | Bitiş tarihi |
| exceptionType | String | Hayır | - | Exception türü (contains) |
| endpoint | String | Hayır | - | Endpoint filtresi |

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "userId": 1,
      "endpoint": "/egitim/999",
      "exceptionType": "java.lang.RuntimeException",
      "message": "Eğitim bulunamadı: 999",
      "stacktrace": "at com.akademi...",
      "createdAt": "2024-12-04T12:30:15"
    }
  ],
  "totalElements": 12
}
```

**Örnek:**
```bash
# Tüm hatalar
curl -H "Authorization: Bearer TOKEN" \
  "http://localhost:8080/api/logs/errors"

# NullPointerException'lar
curl -H "Authorization: Bearer TOKEN" \
  "http://localhost:8080/api/logs/errors?exceptionType=NullPointerException"

# Belirli endpoint'teki hatalar
curl -H "Authorization: Bearer TOKEN" \
  "http://localhost:8080/api/logs/errors?endpoint=/egitim"
```

### 4. Performance Logs
**GET** `/api/logs/performance`

Yavaş çalışan işlemleri getirir.

**Query Parameters:**
| Parametre | Tip | Zorunlu | Varsayılan | Açıklama |
|-----------|-----|---------|------------|----------|
| page | int | Hayır | 0 | Sayfa numarası |
| size | int | Hayır | 20 | Sayfa boyutu |
| startDate | DateTime | Hayır | - | Başlangıç tarihi |
| endDate | DateTime | Hayır | - | Bitiş tarihi |
| minDuration | Long | Hayır | - | Minimum süre (ms) |
| endpoint | String | Hayır | - | Method/endpoint filtresi |

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "endpoint": "EgitimService.createEgitim",
      "durationMs": 1523,
      "methodName": "EgitimService.createEgitim",
      "createdAt": "2024-12-04T12:30:15"
    }
  ],
  "totalElements": 28
}
```

**Örnek:**
```bash
# Tüm yavaş işlemler
curl -H "Authorization: Bearer TOKEN" \
  "http://localhost:8080/api/logs/performance"

# 2 saniyeden uzun sürenler
curl -H "Authorization: Bearer TOKEN" \
  "http://localhost:8080/api/logs/performance?minDuration=2000"

# Belirli service'in performansı
curl -H "Authorization: Bearer TOKEN" \
  "http://localhost:8080/api/logs/performance?endpoint=EgitimService"
```

### 5. Frontend Logs
**GET** `/api/logs/frontend`

Frontend kullanıcı aksiyonlarını getirir.

**Query Parameters:**
| Parametre | Tip | Zorunlu | Varsayılan | Açıklama |
|-----------|-----|---------|------------|----------|
| pageNum | int | Hayır | 0 | Sayfa numarası |
| size | int | Hayır | 20 | Sayfa boyutu |
| userId | Long | Hayır | - | Kullanıcı ID filtresi |
| startDate | DateTime | Hayır | - | Başlangıç tarihi |
| endDate | DateTime | Hayır | - | Bitiş tarihi |
| action | String | Hayır | - | Aksiyon türü |
| page | String | Hayır | - | Sayfa filtresi |

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "userId": 1,
      "action": "BUTTON_CLICK",
      "page": "/egitim",
      "details": "Yeni eğitim ekle butonuna tıklandı",
      "createdAt": "2024-12-04T12:30:15"
    }
  ],
  "totalElements": 234
}
```

**Örnek:**
```bash
# Tüm frontend logları
curl -H "Authorization: Bearer TOKEN" \
  "http://localhost:8080/api/logs/frontend"

# Button click aksiyonları
curl -H "Authorization: Bearer TOKEN" \
  "http://localhost:8080/api/logs/frontend?action=BUTTON_CLICK"

# Belirli sayfadaki aktiviteler
curl -H "Authorization: Bearer TOKEN" \
  "http://localhost:8080/api/logs/frontend?page=/egitim"
```

## Frontend Log Kaydetme

Frontend'ten log göndermek için:

**POST** `/api/logs/frontend`

**Request Body:**
```json
{
  "userId": 1,
  "action": "BUTTON_CLICK",
  "page": "/egitim",
  "details": "Yeni eğitim ekle butonuna tıklandı"
}
```

**Response:**
```json
"Log kaydedildi"
```

**Örnek (React):**
```javascript
const logFrontendAction = async (action, page, details) => {
  const userId = getUserIdFromToken();
  
  await axios.post('/api/logs/frontend', {
    userId,
    action,
    page,
    details
  });
};

// Kullanım
logFrontendAction('BUTTON_CLICK', '/egitim', 'Yeni eğitim ekle');
logFrontendAction('FORM_SUBMIT', '/egitim/new', 'Eğitim formu gönderildi');
```

## Pagination Response Formatı

Tüm endpoint'ler Spring Data `Page<T>` formatında response döner:

```json
{
  "content": [...],           // Sayfa içeriği
  "pageable": {
    "pageNumber": 0,          // Mevcut sayfa (0-indexed)
    "pageSize": 20,           // Sayfa boyutu
    "offset": 0,              // Offset
    "paged": true,
    "unpaged": false
  },
  "totalPages": 5,            // Toplam sayfa sayısı
  "totalElements": 95,        // Toplam kayıt sayısı
  "last": false,              // Son sayfa mı?
  "first": true,              // İlk sayfa mı?
  "number": 0,                // Sayfa numarası
  "size": 20,                 // Sayfa boyutu
  "numberOfElements": 20,     // Bu sayfadaki kayıt sayısı
  "empty": false              // Boş mu?
}
```

## Tarih Formatı

Tüm tarih parametreleri **ISO 8601** formatında olmalıdır:

```
2024-12-04T12:30:15
2024-12-04T00:00:00
2024-12-31T23:59:59
```

## Frontend Entegrasyonu

### React Example

```javascript
import axios from 'axios';

const logAPI = {
  // API Logs
  getApiLogs: (params) => 
    axios.get('/api/logs/api', { params }),

  // Activity Logs
  getActivityLogs: (params) => 
    axios.get('/api/logs/activity', { params }),

  // Error Logs
  getErrorLogs: (params) => 
    axios.get('/api/logs/errors', { params }),

  // Performance Logs
  getPerformanceLogs: (params) => 
    axios.get('/api/logs/performance', { params }),

  // Frontend Logs
  getFrontendLogs: (params) => 
    axios.get('/api/logs/frontend', { params }),
};

// Kullanım
const fetchLogs = async () => {
  const response = await logAPI.getActivityLogs({
    page: 0,
    size: 20,
    entityType: 'Egitim',
    action: 'CREATE'
  });
  
  console.log('Logs:', response.data.content);
  console.log('Total:', response.data.totalElements);
};
```

### Admin Panel Component (React)

```javascript
import React, { useState, useEffect } from 'react';

function AdminLogViewer() {
  const [logs, setLogs] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [filters, setFilters] = useState({
    entityType: '',
    action: '',
    userId: ''
  });

  useEffect(() => {
    fetchLogs();
  }, [page, filters]);

  const fetchLogs = async () => {
    const response = await axios.get('/api/logs/activity', {
      params: {
        page,
        size: 20,
        ...filters
      },
      headers: {
        'Authorization': `Bearer ${getToken()}`
      }
    });

    setLogs(response.data.content);
    setTotalPages(response.data.totalPages);
  };

  return (
    <div>
      <h1>Activity Logs</h1>
      
      {/* Filters */}
      <div>
        <input
          placeholder="Entity Type"
          value={filters.entityType}
          onChange={(e) => setFilters({...filters, entityType: e.target.value})}
        />
        <select
          value={filters.action}
          onChange={(e) => setFilters({...filters, action: e.target.value})}
        >
          <option value="">All Actions</option>
          <option value="CREATE">CREATE</option>
          <option value="UPDATE">UPDATE</option>
          <option value="DELETE">DELETE</option>
        </select>
      </div>

      {/* Logs Table */}
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>User</th>
            <th>Action</th>
            <th>Entity</th>
            <th>Description</th>
            <th>Date</th>
          </tr>
        </thead>
        <tbody>
          {logs.map(log => (
            <tr key={log.id}>
              <td>{log.id}</td>
              <td>{log.userId}</td>
              <td>{log.action}</td>
              <td>{log.entityType}</td>
              <td>{log.description}</td>
              <td>{new Date(log.createdAt).toLocaleString()}</td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* Pagination */}
      <div>
        <button
          disabled={page === 0}
          onClick={() => setPage(page - 1)}
        >
          Previous
        </button>
        <span>Page {page + 1} of {totalPages}</span>
        <button
          disabled={page >= totalPages - 1}
          onClick={() => setPage(page + 1)}
        >
          Next
        </button>
      </div>
    </div>
  );
}
```

## Güvenlik

- ✅ Tüm endpoint'ler `@PreAuthorize("hasRole('ADMIN')")` ile korunmuştur
- ✅ Sadece ADMIN rolüne sahip kullanıcılar erişebilir
- ✅ JWT token gereklidir

## Filtreleme Stratejisi

Filtreleme şu an **in-memory** olarak yapılmaktadır (tüm kayıtlar çekilip filtreli yor). 

**Production için öneriler:**
1. JPA Specification API kullanın
2. Query DSL kullanın
3. Custom native query'ler yazın

**Specification Örneği:**
```java
public class ActivityLogSpecification {
    public static Specification<ActivityLog> hasUserId(Long userId) {
        return (root, query, cb) -> 
            userId == null ? null : cb.equal(root.get("userId"), userId);
    }
    
    public static Specification<ActivityLog> hasEntityType(String entityType) {
        return (root, query, cb) -> 
            entityType == null ? null : cb.equal(root.get("entityType"), entityType);
    }
}

// Kullanım
Specification<ActivityLog> spec = Specification
    .where(hasUserId(userId))
    .and(hasEntityType(entityType));
    
Page<ActivityLog> logs = repository.findAll(spec, pageable);
```

## Özellikler Özeti

✅ **5 Log Türü:** API, Activity, Error, Performance, Frontend
✅ **Pagination:** Spring Data Page desteği
✅ **Filtreleme:** Çoklu filtre kombinasyonları
✅ **Tarih Aralığı:** startDate & endDate
✅ **Sorting:** En yeni/yavaş ilk sırada
✅ **ADMIN Only:** Role-based access control
✅ **REST Standart:** JSON response
✅ **Frontend Ready:** React entegrasyonu kolay

## Notlar

- Default sayfa boyutu: 20
- Tüm tarihler ISO 8601 formatında
- Filtreleme case-insensitive
- Pagination 0-indexed
- Sort otomatik (en yeni/yavaş ilk)

Artık admin paneli için komple log API hazır! 🎉





