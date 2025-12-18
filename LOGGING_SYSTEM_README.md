# Loglama Sistemi Dokümantasyonu

## Genel Bakış

Spring Boot projesine 5 farklı log tablosu eklenmiştir:

1. **api_logs** - API endpoint kullanım logları
2. **activity_logs** - Kullanıcı aksiyonları
3. **error_logs** - Exception ve hata logları
4. **performance_logs** - Yavaş çalışan işlemler
5. **frontend_logs** - Frontend kullanıcı aksiyonları

## Veritabanı Tabloları

### 1. API Logs (api_logs)
API çağrılarını loglar.

| Alan | Tip | Açıklama |
|------|-----|----------|
| id | BIGINT | Primary key |
| user_id | BIGINT | Kullanıcı ID |
| endpoint | VARCHAR(500) | API endpoint |
| http_method | VARCHAR(10) | HTTP metodu (GET, POST, vb.) |
| status_code | INTEGER | HTTP durum kodu |
| request_body | TEXT | İstek gövdesi |
| response_body | TEXT | Yanıt gövdesi |
| duration_ms | BIGINT | İşlem süresi (milisaniye) |
| ip | VARCHAR(45) | IP adresi |
| created_at | TIMESTAMP | Oluşturulma zamanı |

### 2. Activity Logs (activity_logs)
Kullanıcı aktivitelerini loglar.

| Alan | Tip | Açıklama |
|------|-----|----------|
| id | BIGINT | Primary key |
| user_id | BIGINT | Kullanıcı ID |
| action | VARCHAR(100) | Aksiyon (CREATE, UPDATE, DELETE, vb.) |
| entity_type | VARCHAR(100) | Entity türü (Egitim, Proje, vb.) |
| entity_id | BIGINT | Entity ID |
| description | VARCHAR(1000) | Açıklama |
| created_at | TIMESTAMP | Oluşturulma zamanı |

### 3. Error Logs (error_logs)
Hata ve exception'ları loglar.

| Alan | Tip | Açıklama |
|------|-----|----------|
| id | BIGINT | Primary key |
| user_id | BIGINT | Kullanıcı ID |
| endpoint | VARCHAR(500) | Hatanın oluştuğu endpoint |
| exception_type | VARCHAR(255) | Exception türü |
| message | TEXT | Hata mesajı |
| stacktrace | TEXT | Stack trace |
| created_at | TIMESTAMP | Oluşturulma zamanı |

### 4. Performance Logs (performance_logs)
Performans metriklerini loglar.

| Alan | Tip | Açıklama |
|------|-----|----------|
| id | BIGINT | Primary key |
| endpoint | VARCHAR(500) | Endpoint |
| duration_ms | BIGINT | İşlem süresi (milisaniye) |
| method_name | VARCHAR(255) | Metod adı |
| created_at | TIMESTAMP | Oluşturulma zamanı |

### 5. Frontend Logs (frontend_logs)
Frontend aksiyonlarını loglar.

| Alan | Tip | Açıklama |
|------|-----|----------|
| id | BIGINT | Primary key |
| user_id | BIGINT | Kullanıcı ID |
| action | VARCHAR(255) | Aksiyon türü |
| page | VARCHAR(255) | Sayfa |
| details | TEXT | Detaylar |
| created_at | TIMESTAMP | Oluşturulma zamanı |

## Kullanım

### LogService Kullanımı

```java
@Autowired
private LogService logService;

// API logu kaydet
logService.logApiCall(userId, "/egitim", "GET", 200, 
    requestBody, responseBody, 150L, "192.168.1.1");

// Aktivite logu kaydet
logService.logActivity(userId, "CREATE", "Egitim", egitimId, 
    "Yeni eğitim oluşturuldu: Java Eğitimi");

// Hata logu kaydet
try {
    // İşlem
} catch (Exception e) {
    logService.logError(userId, "/egitim", e);
}

// Performans logu kaydet
long startTime = System.currentTimeMillis();
// İşlem
long duration = System.currentTimeMillis() - startTime;
logService.logPerformance("/egitim", duration, "createEgitim");

// Frontend logu kaydet
logService.logFrontend(userId, "BUTTON_CLICK", "/egitim", 
    "Yeni eğitim ekle butonuna tıklandı");
```

### REST API Endpoint'leri

Tüm log endpoint'leri **sadece ADMIN** kullanıcılar tarafından erişilebilir.

#### API Loglarını Getir
```http
GET /logs/api?start=2024-01-01T00:00:00&end=2024-12-31T23:59:59
Authorization: Bearer {jwt_token}
```

#### Kullanıcı Aktivitelerini Getir
```http
GET /logs/activities/{userId}
Authorization: Bearer {jwt_token}
```

#### Son Hataları Getir
```http
GET /logs/errors/recent
Authorization: Bearer {jwt_token}
```

#### Yavaş Endpoint'leri Getir
```http
GET /logs/performance/slow
Authorization: Bearer {jwt_token}
```

#### Frontend'ten Log Kaydet
```http
POST /logs/frontend
Content-Type: application/json

{
  "userId": 1,
  "action": "BUTTON_CLICK",
  "page": "/egitim",
  "details": "Yeni eğitim ekle butonuna tıklandı"
}
```

## Otomatik Tablo Oluşturma

Veritabanı tabloları **otomatik olarak** oluşturulur:

- `application-dev.properties` dosyasında `spring.jpa.hibernate.ddl-auto=update` ayarı aktif
- Uygulama başladığında Hibernate, entity sınıflarına göre tabloları otomatik oluşturur
- Mevcut tablolar varsa günceller, yoksa yeni tablo oluşturur

## Asenkron Loglama

Tüm log işlemleri **@Async** ile asenkron çalışır:

- Loglama işlemi ana iş akışını bloke etmez
- Performans kaybı yaşanmaz
- Log hatası uygulamayı etkilemez

## Örnek Senaryolar

### Senaryo 1: Controller'da Aktivite Logla

```java
@PostMapping
@PreAuthorize("hasAnyRole('ADMIN', 'SORUMLU')")
public ResponseEntity<EgitimResponseDTO> createEgitim(@RequestBody EgitimRequestDTO requestDTO) {
    EgitimResponseDTO egitim = egitimService.createEgitim(requestDTO);
    
    // Aktivite logla
    Long userId = getCurrentUserId(); // JWT'den kullanıcı ID'si al
    logService.logActivity(userId, "CREATE", "Egitim", egitim.getId(), 
        "Yeni eğitim oluşturuldu: " + egitim.getAd());
    
    return ResponseEntity.ok(egitim);
}
```

### Senaryo 2: Global Exception Handler ile Hata Logla

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @Autowired
    private LogService logService;
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e, HttpServletRequest request) {
        Long userId = getCurrentUserId();
        logService.logError(userId, request.getRequestURI(), e);
        
        return ResponseEntity.status(500).body("İşlem sırasında hata oluştu");
    }
}
```

### Senaryo 3: Frontend'ten Log Gönder

```javascript
// React örneği
const logAction = async (action, page, details) => {
  try {
    const userId = getUserIdFromToken();
    await axios.post('/logs/frontend', {
      userId,
      action,
      page,
      details
    });
  } catch (error) {
    console.error('Log gönderilemedi:', error);
  }
};

// Kullanım
logAction('BUTTON_CLICK', '/egitim', 'Yeni eğitim ekle butonuna tıklandı');
logAction('FORM_SUBMIT', '/egitim/new', 'Eğitim formu gönderildi');
logAction('PAGE_VIEW', '/egitim/123', 'Eğitim detay sayfası görüntülendi');
```

## H2 Console'da Log Tabloları

Uygulama çalışırken log tablolarını görmek için:

1. Tarayıcıda `http://localhost:8080/h2-console` adresine git
2. JDBC URL: `jdbc:h2:file:./data/egitim_takip_dev`
3. Username: `sa`
4. Password: (boş)
5. Connect'e tıkla

SQL sorguları ile logları görüntüleyebilirsiniz:

```sql
-- Son 100 API logu
SELECT * FROM api_logs ORDER BY created_at DESC LIMIT 100;

-- Belirli bir kullanıcının aktiviteleri
SELECT * FROM activity_logs WHERE user_id = 1 ORDER BY created_at DESC;

-- Son hatalar
SELECT * FROM error_logs ORDER BY created_at DESC LIMIT 50;

-- Yavaş endpoint'ler (1 saniyeden uzun)
SELECT * FROM performance_logs WHERE duration_ms > 1000 ORDER BY duration_ms DESC;
```

## Özellikler

✅ 5 farklı log tablosu
✅ JPA Entity'ler ile otomatik tablo oluşturma
✅ @CreationTimestamp ile otomatik zaman damgası
✅ Repository'ler ile kolay sorgulama
✅ LogService ile merkezi log yönetimi
✅ @Async ile asenkron loglama
✅ REST API endpoint'leri (ADMIN yetkisi ile)
✅ Frontend entegrasyonu
✅ H2 Console ile kolay görüntüleme

## Notlar

- Loglar asenkron kaydedilir, performansı etkilemez
- Log hatası uygulamayı etkilemez
- ADMIN kullanıcılar tüm loglara erişebilir
- Frontend logları için auth gerekmez
- Uzun metinler otomatik olarak kesilir (truncate)





