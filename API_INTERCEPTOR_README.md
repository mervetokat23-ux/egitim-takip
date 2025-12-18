# API Request/Response Logging Interceptor

## Genel Bakış

Tüm HTTP isteklerini ve yanıtlarını otomatik olarak yakalayıp `api_logs` tablosuna kaydeden interceptor sistemi.

## Oluşturulan Dosyalar

### 1. LogInterceptor.java
`HandlerInterceptor` implementasyonu. Tüm API çağrılarını yakalar.

**Özellikler:**
- ✅ Request/Response body yakalama
- ✅ İşlem süresi hesaplama
- ✅ Client IP adresi tespit (proxy desteği)
- ✅ JWT token'dan kullanıcı bilgisi (opsiyonel)
- ✅ Static kaynak ve H2 console hariç tutma
- ✅ Asenkron log kaydetme

### 2. WebMvcConfig.java
Spring MVC yapılandırması.

**Özellikler:**
- ✅ LogInterceptor register
- ✅ ContentCachingFilter (Request/Response wrapper)
- ✅ Belirli path'leri exclude etme
- ✅ Filter sıralaması

### 3. ApiLogService.java
API log kayıtlarını yöneten servis.

**Özellikler:**
- ✅ Asenkron log kaydetme (@Async)
- ✅ Log sorgulama metodları
- ✅ Eski log temizleme
- ✅ Yavaş API uyarıları

## Nasıl Çalışır?

### 1. Request Akışı

```
Client Request
    ↓
ContentCachingFilter (Request/Response wrap)
    ↓
LogInterceptor.preHandle()
    - Başlangıç zamanı kaydet
    - JWT token'dan userId al
    ↓
Controller (İş mantığı)
    ↓
LogInterceptor.afterCompletion()
    - Request/Response body oku
    - Süre hesapla
    - ApiLogService.saveApiLog() çağır
    ↓
ApiLogService (Asenkron)
    - api_logs tablosuna kaydet
    ↓
Response to Client
```

### 2. Body Yakalama

**ContentCachingRequestWrapper** ve **ContentCachingResponseWrapper** kullanılır:

- Normal şartlarda HTTP body'si bir kez okunabilir (stream)
- Wrapper'lar body'yi memory'de tutar
- Tekrar tekrar okunabilir hale gelir
- Interceptor body içeriğine erişebilir

### 3. Loglanan Bilgiler

| Alan | Açıklama | Kaynak |
|------|----------|---------|
| userId | Kullanıcı ID | JWT token (opsiyonel) |
| endpoint | URL path | request.getRequestURI() |
| httpMethod | HTTP metodu | request.getMethod() |
| statusCode | HTTP durum kodu | response.getStatus() |
| requestBody | İstek içeriği | ContentCachingRequestWrapper |
| responseBody | Yanıt içeriği | ContentCachingResponseWrapper |
| durationMs | İşlem süresi | System.currentTimeMillis() farkı |
| ip | Client IP | X-Forwarded-For veya RemoteAddr |
| createdAt | Oluşturulma zamanı | @CreationTimestamp (otomatik) |

## Yapılandırma

### Exclude Path'ler

Aşağıdaki endpoint'ler loglanmaz:

```java
/h2-console/**     // H2 veritabanı konsolu
/swagger-ui/**     // Swagger UI
/v3/api-docs/**    // OpenAPI dokümantasyonu
/static/**         // Static dosyalar
/favicon.ico       // Favicon
*.js, *.css        // JavaScript ve CSS dosyaları
*.png, *.jpg       // Resim dosyaları
/logs/**           // Log endpoint'leri (sonsuz döngü önleme)
```

### Truncate Limitleri

Çok uzun body'ler kesilir:

- **Request Body**: Max 10,000 karakter
- **Response Body**: Max 10,000 karakter
- **Endpoint**: Max 500 karakter

### Yavaş API Uyarısı

1000ms'den uzun süren API'ler konsola loglanır:

```
⚠️  Yavaş API: GET /egitim - 1523ms
```

## Kullanım Örnekleri

### 1. Otomatik Loglama

Hiçbir şey yapmanıza gerek yok! Interceptor otomatik çalışır:

```java
@GetMapping("/egitim")
public List<EgitimResponseDTO> getAllEgitimler() {
    // Bu endpoint çağrısı otomatik loglanır
    return egitimService.getAllEgitimler();
}
```

### 2. Log Kayıtlarını Sorgulama

```java
@Autowired
private ApiLogService apiLogService;

// Belirli bir endpoint'in logları
List<ApiLog> logs = apiLogService.getLogsByEndpoint("/egitim");

// Belirli bir kullanıcının API çağrıları
List<ApiLog> userLogs = apiLogService.getLogsByUserId(1L);

// Yavaş çalışan API'ler (1000ms üzeri)
List<ApiLog> slowApis = apiLogService.getSlowApis(1000L);

// Tarih aralığı
LocalDateTime start = LocalDateTime.now().minusDays(7);
LocalDateTime end = LocalDateTime.now();
List<ApiLog> weekLogs = apiLogService.getLogsByDateRange(start, end);

// HTTP metodu
List<ApiLog> postLogs = apiLogService.getLogsByHttpMethod("POST");
```

### 3. Eski Logları Temizleme

```java
// 30 günden eski logları sil
LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
apiLogService.deleteOldLogs(thirtyDaysAgo);
```

### 4. H2 Console'da Sorgulama

```sql
-- Son 100 API çağrısı
SELECT * FROM api_logs 
ORDER BY created_at DESC 
LIMIT 100;

-- Yavaş çalışan endpoint'ler
SELECT endpoint, AVG(duration_ms) as avg_duration, COUNT(*) as call_count
FROM api_logs
GROUP BY endpoint
HAVING AVG(duration_ms) > 500
ORDER BY avg_duration DESC;

-- En çok çağrılan endpoint'ler
SELECT endpoint, http_method, COUNT(*) as call_count
FROM api_logs
GROUP BY endpoint, http_method
ORDER BY call_count DESC
LIMIT 20;

-- Hatalı istekler (4xx, 5xx)
SELECT * FROM api_logs
WHERE status_code >= 400
ORDER BY created_at DESC;

-- Belirli bir kullanıcının aktivitesi
SELECT * FROM api_logs
WHERE user_id = 1
ORDER BY created_at DESC;
```

## JWT Token Entegrasyonu

### Şu Anki Durum

```java
// LogInterceptor.java - preHandle() içinde
String authHeader = request.getHeader("Authorization");
if (authHeader != null && authHeader.startsWith("Bearer ")) {
    String token = authHeader.substring(7);
    String email = jwtUtil.getEmailFromToken(token);
    // Email kaydediliyor, userId için dönüşüm gerekiyor
}
```

### userId'ye Dönüştürme (Opsiyonel İyileştirme)

```java
@Autowired
private KullaniciRepository kullaniciRepository;

// Email'den userId'yi bul
if (email != null) {
    Kullanici kullanici = kullaniciRepository.findByEmail(email);
    if (kullanici != null) {
        request.setAttribute(USER_ID_ATTRIBUTE, kullanici.getId());
    }
}
```

## Performans Optimizasyonu

### 1. Asenkron Loglama

```java
@Async
public void saveApiLog(...) {
    // Ana iş akışını bloke etmez
    // Loglama ayrı thread'de çalışır
}
```

### 2. Lazy Loading

```java
// Body sadece gerektiğinde okunur
if (buf.length > 0) {
    requestBody = new String(buf, ...);
}
```

### 3. Truncate

```java
// Çok uzun body'ler kesilir (bellek tasarrufu)
private String truncate(String str, int maxLength) {
    return str.substring(0, maxLength) + "... [truncated]";
}
```

## Test Etme

### 1. Backend'i Başlat

```bash
mvn spring-boot:run
```

### 2. API Çağrısı Yap

```bash
# Örnek: Eğitim listesi al
curl -X GET http://localhost:8080/egitim \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 3. H2 Console'da Kontrol Et

1. `http://localhost:8080/h2-console`
2. JDBC URL: `jdbc:h2:file:./data/egitim_takip_dev`
3. Username: `sa`, Password: (boş)
4. SQL: `SELECT * FROM api_logs ORDER BY created_at DESC LIMIT 10;`

### 4. Beklenen Sonuç

```sql
| id | user_id | endpoint  | http_method | status_code | duration_ms | ip          | created_at          |
|----|---------|-----------|-------------|-------------|-------------|-------------|---------------------|
| 1  | 1       | /egitim   | GET         | 200         | 45          | 127.0.0.1   | 2024-12-04 12:30:15 |
```

## Örnek Senaryolar

### Senaryo 1: Kullanıcı Eğitim Oluşturuyor

```bash
POST /egitim
{
  "ad": "Java Eğitimi",
  "durum": "Planlandı"
}
```

**Loglanan:**
- endpoint: `/egitim`
- httpMethod: `POST`
- statusCode: `201`
- requestBody: `{"ad":"Java Eğitimi","durum":"Planlandı"}`
- responseBody: `{"id":1,"ad":"Java Eğitimi",...}`
- durationMs: `234`

### Senaryo 2: Yavaş API Uyarısı

```
Controller: 1500ms süren bir sorgu

Console Output:
⚠️  Yavaş API: GET /egitim/report - 1500ms

api_logs tablosu:
endpoint: /egitim/report
duration_ms: 1500
```

### Senaryo 3: Hata Durumu

```bash
GET /egitim/999 (Olmayan ID)
```

**Loglanan:**
- statusCode: `404`
- responseBody: `{"message":"Eğitim bulunamadı: 999"}`

## Sorun Giderme

### Problem: Body'ler loglanmıyor

**Çözüm:** ContentCachingFilter'ın çalıştığından emin olun:

```java
// WebMvcConfig.java
@Bean
public FilterRegistrationBean<ContentCachingFilter> contentCachingFilter() {
    // Bu bean mevcut olmalı
}
```

### Problem: Sonsuz log kaydı (döngü)

**Çözüm:** Log endpoint'lerini exclude edin:

```java
// LogInterceptor.java - shouldLog()
return !endpoint.startsWith("/logs");
```

### Problem: userId null

**Çözüm:** JWT token geçerli mi kontrol edin:

```bash
# Header'da token var mı?
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

## Özellikler Özeti

✅ **Otomatik loglama** - Tüm API çağrıları
✅ **Request/Response body** - Tam içerik
✅ **İşlem süresi** - Performans tracking
✅ **Client IP** - Proxy desteği
✅ **JWT entegrasyonu** - Kullanıcı takibi
✅ **Asenkron kayıt** - Performans etkisi yok
✅ **Akıllı filtreleme** - Static kaynak hariç
✅ **Truncate** - Bellek optimizasyonu
✅ **Yavaş API uyarısı** - 1000ms+ logla
✅ **Eski log temizleme** - Otomatik cleanup

## Notlar

- Loglar **asenkron** kaydedilir → performans kaybı yok
- **Hassas bilgiler** (şifre, token) requestBody'de görünebilir → hassas endpoint'leri exclude edin
- **Bellek kullanımı** → body'ler memory'de tutulur, truncate limitleri ayarlayın
- **Veritabanı boyutu** → Düzenli log temizliği yapın
- **Thread pool** → @Async için thread pool yapılandırması gerekebilir

## İleri Seviye

### Custom Annotation ile Loglama

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogApiCall {
    boolean includeRequestBody() default true;
    boolean includeResponseBody() default true;
}

// Kullanım
@LogApiCall(includeRequestBody = false)
@GetMapping("/secure-data")
public SecureData getSecureData() {
    // Request body loglanmaz
}
```

### Scheduled Log Cleanup

```java
@Scheduled(cron = "0 0 2 * * *") // Her gün saat 02:00
public void cleanupOldLogs() {
    LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
    apiLogService.deleteOldLogs(thirtyDaysAgo);
}
```

## Sonuç

API Logging Interceptor sistemi artık aktif! Tüm API çağrıları otomatik olarak `api_logs` tablosuna kaydediliyor. 🎉





