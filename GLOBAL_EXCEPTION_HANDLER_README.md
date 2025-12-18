# Global Exception Handler

## Genel Bakış

Tüm uygulama hatalarını merkezi olarak yakalayan, `error_logs` tablosuna kaydeden ve standart JSON response dönen global exception handler sistemi.

## Oluşturulan Dosyalar

### 1. GlobalExceptionHandler.java
`@ControllerAdvice` ile tüm controller'lardaki exception'ları yakalar.

**Yakalanan Exception'lar:**
- ✅ `RuntimeException` - Genel runtime hataları
- ✅ `Exception` - Tüm exception'lar (fallback)
- ✅ `MethodArgumentNotValidException` - Validation hataları (@Valid)
- ✅ `IllegalArgumentException` - Geçersiz parametre
- ✅ `NullPointerException` - Null değer hataları
- ✅ `AuthenticationException` - Kimlik doğrulama hataları (401)
- ✅ `BadCredentialsException` - Yanlış şifre/email (401)
- ✅ `AccessDeniedException` - Yetki hatası (403)
- ✅ `ResourceNotFoundException` - Kayıt bulunamadı (404)

### 2. ErrorLogService.java
Error log kayıtlarını yöneten servis.

**Özellikler:**
- ✅ Asenkron log kaydetme (@Async)
- ✅ Stack trace'i string'e çevirme
- ✅ Log sorgulama metodları
- ✅ Eski log temizleme
- ✅ Konsol çıktısı

### 3. ErrorResponse.java
Standart hata response DTO'su.

**Alanlar:**
- `status`: HTTP durum kodu (400, 401, 403, 404, 500)
- `message`: Kullanıcıya gösterilecek mesaj
- `details`: Teknik detay (opsiyonel)
- `path`: Hatanın oluştuğu endpoint
- `timestamp`: Hata zamanı
- `exceptionType`: Exception sınıfı (opsiyonel)

## Nasıl Çalışır?

### 1. Exception Flow

```
Controller Method
    ↓
Exception Thrown
    ↓
GlobalExceptionHandler
    - @ExceptionHandler yakalar
    - getCurrentUserId() (JWT token'dan)
    - ErrorLogService.saveErrorLog() [ASYNC]
    - ErrorResponse oluştur
    ↓
error_logs tablosuna kayıt
    ↓
Client'a JSON response
```

### 2. Loglanan Bilgiler

| Alan | Kaynak | Örnek |
|------|--------|-------|
| userId | JWT token | `1` veya `null` |
| endpoint | HttpServletRequest.getRequestURI() | `/egitim/123` |
| exceptionType | exception.getClass().getName() | `java.lang.RuntimeException` |
| message | exception.getMessage() | `"Eğitim bulunamadı: 123"` |
| stacktrace | exception.printStackTrace() | `at com.akademi...` |
| createdAt | @CreationTimestamp | `2024-12-04 12:30:15` |

## Standart JSON Response Formatı

### Örnek 1: 500 Internal Server Error

```json
{
  "status": 500,
  "message": "İşlem sırasında bir hata oluştu: Eğitim bulunamadı: 123",
  "details": "RuntimeException",
  "path": "/egitim/123",
  "timestamp": "2024-12-04T12:30:15.123",
  "exceptionType": "java.lang.RuntimeException"
}
```

### Örnek 2: 400 Bad Request (Validation)

```json
{
  "status": 400,
  "message": "Geçersiz veri",
  "details": "{ad=Eğitim adı boş olamaz, durum=Durum seçilmeli}",
  "path": "/egitim",
  "timestamp": "2024-12-04T12:30:15.123",
  "exceptionType": "ValidationException"
}
```

### Örnek 3: 401 Unauthorized

```json
{
  "status": 401,
  "message": "Kullanıcı adı veya şifre hatalı",
  "details": null,
  "path": "/auth/login",
  "timestamp": "2024-12-04T12:30:15.123",
  "exceptionType": null
}
```

### Örnek 4: 403 Forbidden

```json
{
  "status": 403,
  "message": "Bu işlem için yetkiniz yok",
  "details": "Access is denied",
  "path": "/egitim/delete/123",
  "timestamp": "2024-12-04T12:30:15.123",
  "exceptionType": "org.springframework.security.access.AccessDeniedException"
}
```

### Örnek 5: 404 Not Found

```json
{
  "status": 404,
  "message": "Eğitim bulunamadı: 999",
  "details": null,
  "path": "/egitim/999",
  "timestamp": "2024-12-04T12:30:15.123",
  "exceptionType": null
}
```

## Kullanım Örnekleri

### Service Layer'da Exception Fırlatma

```java
@Service
public class EgitimService {
    
    public EgitimResponseDTO getEgitimById(Long id) {
        Egitim egitim = egitimRepository.findById(id)
            .orElseThrow(() -> 
                new GlobalExceptionHandler.ResourceNotFoundException("Eğitim", id)
            );
        return egitimMapper.toResponseDTO(egitim);
    }
    
    public EgitimResponseDTO createEgitim(EgitimRequestDTO dto) {
        if (dto.getAd() == null || dto.getAd().isEmpty()) {
            throw new IllegalArgumentException("Eğitim adı boş olamaz");
        }
        // ... iş mantığı ...
    }
}
```

### Controller Layer'da Validation

```java
@RestController
@RequestMapping("/egitim")
public class EgitimController {
    
    @PostMapping
    public ResponseEntity<EgitimResponseDTO> createEgitim(
            @Valid @RequestBody EgitimRequestDTO requestDTO) {
        // @Valid annotation otomatik validation yapar
        // Hata varsa MethodArgumentNotValidException fırlatılır
        // GlobalExceptionHandler yakalar ve 400 döner
        return ResponseEntity.ok(egitimService.createEgitim(requestDTO));
    }
}
```

### Custom Exception Kullanımı

```java
// Service'de
public EgitimResponseDTO getEgitimById(Long id) {
    return egitimRepository.findById(id)
        .map(egitimMapper::toResponseDTO)
        .orElseThrow(() -> 
            new GlobalExceptionHandler.ResourceNotFoundException("Eğitim", id)
        );
}

// Otomatik yakalanır ve 404 response döner:
// GET /egitim/999 → 404 Not Found
// {
//   "status": 404,
//   "message": "Eğitim bulunamadı: 999",
//   "path": "/egitim/999"
// }
```

## ErrorLogService Kullanımı

```java
@Autowired
private ErrorLogService errorLogService;

// Son 100 hata
List<ErrorLog> recentErrors = errorLogService.getRecentErrors();

// Belirli endpoint'teki hatalar
List<ErrorLog> egitimErrors = errorLogService.getErrorsByEndpoint("/egitim");

// Belirli exception türü
List<ErrorLog> nullPointers = errorLogService.getErrorsByExceptionType(
    "java.lang.NullPointerException"
);

// Tarih aralığı
LocalDateTime start = LocalDateTime.now().minusDays(7);
LocalDateTime end = LocalDateTime.now();
List<ErrorLog> weekErrors = errorLogService.getErrorsByDateRange(start, end);

// Eski logları temizle (60 gün öncesi)
errorLogService.deleteOldErrors(LocalDateTime.now().minusDays(60));
```

## H2 Console'da Sorgulama

```sql
-- Tüm hatalar
SELECT * FROM error_logs 
ORDER BY created_at DESC 
LIMIT 100;

-- En sık oluşan hatalar
SELECT exception_type, COUNT(*) as error_count
FROM error_logs
GROUP BY exception_type
ORDER BY error_count DESC;

-- Belirli endpoint'teki hatalar
SELECT * FROM error_logs
WHERE endpoint LIKE '%/egitim%'
ORDER BY created_at DESC;

-- Son 24 saatteki hatalar
SELECT * FROM error_logs
WHERE created_at >= DATEADD('HOUR', -24, CURRENT_TIMESTAMP)
ORDER BY created_at DESC;

-- Kullanıcıya göre hata sayısı
SELECT user_id, COUNT(*) as error_count
FROM error_logs
WHERE user_id IS NOT NULL
GROUP BY user_id
ORDER BY error_count DESC;

-- NullPointerException'lar
SELECT endpoint, message, created_at
FROM error_logs
WHERE exception_type = 'java.lang.NullPointerException'
ORDER BY created_at DESC;
```

## Konsol Çıktısı

Her hata loglandığında konsola bilgi yazılır:

```
🔴 Error Log: [RuntimeException] /egitim/123 at 2024-12-04T12:30:15 by User: 1 - Eğitim bulunamadı: 123
🔴 Error Log: [NullPointerException] /proje/save at 2024-12-04T12:31:20 by User: 2 - null
🔴 Error Log: [AccessDeniedException] /admin/users at 2024-12-04T12:32:10 by User: 3 - Access is denied
```

## Test Etme

### 1. 500 Internal Server Error

```bash
# Olmayan ID ile eğitim al
curl -X GET http://localhost:8080/egitim/99999
```

**Response:**
```json
{
  "status": 500,
  "message": "İşlem sırasında bir hata oluştu: Eğitim bulunamadı: 99999",
  "path": "/egitim/99999",
  "timestamp": "2024-12-04T12:30:15.123"
}
```

### 2. 400 Validation Error

```bash
# Geçersiz veri ile eğitim oluştur
curl -X POST http://localhost:8080/egitim \
  -H "Content-Type: application/json" \
  -d '{"ad":"","durum":""}'
```

**Response:**
```json
{
  "status": 400,
  "message": "Geçersiz veri",
  "details": "{ad=Eğitim adı boş olamaz}",
  "path": "/egitim"
}
```

### 3. 401 Unauthorized

```bash
# Geçersiz token
curl -X GET http://localhost:8080/egitim \
  -H "Authorization: Bearer INVALID_TOKEN"
```

### 4. 403 Forbidden

```bash
# Yetki olmayan endpoint
curl -X DELETE http://localhost:8080/egitim/123 \
  -H "Authorization: Bearer SORUMLU_TOKEN"
```

## Custom Exception Tanımlama

Kendi exception'larınızı oluşturabilirsiniz:

```java
// Custom Exception
public class DuplicateEntityException extends RuntimeException {
    public DuplicateEntityException(String message) {
        super(message);
    }
}

// GlobalExceptionHandler'a ekle
@ExceptionHandler(DuplicateEntityException.class)
public ResponseEntity<ErrorResponse> handleDuplicateException(
        DuplicateEntityException ex,
        HttpServletRequest request) {
    
    Long userId = getCurrentUserId(request);
    errorLogService.saveErrorLog(userId, request.getRequestURI(), ex);
    
    ErrorResponse errorResponse = ErrorResponse.of(
        HttpStatus.CONFLICT.value(),
        ex.getMessage(),
        request.getRequestURI()
    );
    
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(errorResponse);
}

// Kullanım
throw new DuplicateEntityException("Bu email zaten kayıtlı");
```

## HTTP Status Codes

| Status | Kullanım | Exception |
|--------|----------|-----------|
| 400 | Bad Request | MethodArgumentNotValidException, IllegalArgumentException |
| 401 | Unauthorized | AuthenticationException, BadCredentialsException |
| 403 | Forbidden | AccessDeniedException |
| 404 | Not Found | ResourceNotFoundException |
| 409 | Conflict | DuplicateEntityException (custom) |
| 500 | Internal Server Error | RuntimeException, Exception, NullPointerException |

## Frontend Entegrasyonu

### Axios Interceptor (React)

```javascript
// src/services/api.js
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080'
});

// Response interceptor
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response) {
      const { status, data } = error.response;
      
      // Standart error response
      const errorMessage = data.message || 'Bir hata oluştu';
      
      switch (status) {
        case 400:
          alert('Geçersiz veri: ' + errorMessage);
          break;
        case 401:
          alert('Oturum süreniz doldu');
          localStorage.removeItem('token');
          window.location.href = '/login';
          break;
        case 403:
          alert('Bu işlem için yetkiniz yok');
          break;
        case 404:
          alert('Kayıt bulunamadı');
          break;
        case 500:
          alert('Sunucu hatası: ' + errorMessage);
          break;
        default:
          alert('Hata: ' + errorMessage);
      }
    }
    return Promise.reject(error);
  }
);

export default api;
```

## Performans

- **Asenkron Loglama:** @Async ile error logging ana iş akışını bloke etmez
- **Minimal Overhead:** Exception handling çok hızlı
- **Stack Trace:** Truncate ile bellek kontrolü (max 10KB)

## Best Practices

### 1. Specific Exception'lar Kullan

```java
// ❌ Kötü
throw new RuntimeException("Hata");

// ✅ İyi
throw new IllegalArgumentException("Geçersiz ID: " + id);
throw new GlobalExceptionHandler.ResourceNotFoundException("Egitim", id);
```

### 2. Meaningful Messages

```java
// ❌ Kötü
throw new RuntimeException("Error");

// ✅ İyi
throw new RuntimeException("Eğitim bulunamadı: " + id);
```

### 3. Validation

```java
// DTO'da
@NotBlank(message = "Eğitim adı boş olamaz")
private String ad;

// Controller'da
@PostMapping
public ResponseEntity<EgitimResponseDTO> create(
    @Valid @RequestBody EgitimRequestDTO dto) {
    // ...
}
```

## Scheduled Log Cleanup

```java
@Component
public class LogCleanupScheduler {
    
    @Autowired
    private ErrorLogService errorLogService;
    
    @Scheduled(cron = "0 0 4 * * *") // Her gün 04:00
    public void cleanupOldErrorLogs() {
        LocalDateTime twoMonthsAgo = LocalDateTime.now().minusMonths(2);
        errorLogService.deleteOldErrors(twoMonthsAgo);
    }
}
```

## Özellikler Özeti

✅ **Merkezi Exception Handling** (@ControllerAdvice)
✅ **Otomatik Loglama** (error_logs tablosu)
✅ **Standart JSON Response** (ErrorResponse DTO)
✅ **Asenkron Kayıt** (@Async)
✅ **Stack Trace** (Detaylı hata izleme)
✅ **HTTP Status Codes** (400, 401, 403, 404, 500)
✅ **Validation Support** (@Valid)
✅ **JWT Integration** (userId extraction)
✅ **Console Output** (Development friendly)
✅ **Query Methods** (ErrorLogService)

## Notlar

- Tüm hatalar otomatik yakalanır ve loglanır
- Hassas bilgiler (şifre, token) response'da gösterilmez
- Stack trace sadece error_logs'ta tutulur, client'a gönderilmez
- Asenkron loglama performans kaybı yaratmaz
- Frontend için standart error format kolaylık sağlar

Artık tüm exception'lar merkezi olarak yönetiliyor! 🎉





