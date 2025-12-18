# Performance Monitoring System

## Genel Bakış

AOP (Aspect-Oriented Programming) kullanarak 1 saniyeden uzun süren tüm servis metodlarını otomatik olarak `performance_logs` tablosuna kaydeden sistem.

## Oluşturulan Dosyalar

### 1. PerformanceAspect.java
`@Aspect` sınıfı. @Service annotasyonlu sınıflardaki tüm metodları yakalar.

**Özellikler:**
- ✅ @Around advice ile method execution time ölçümü
- ✅ Sadece @Service sınıfları
- ✅ Log service'leri hariç (sonsuz döngü önleme)
- ✅ 1000ms üzeri işlemler kaydedilir
- ✅ ClassName.methodName formatında kayıt

### 2. PerformanceLogService.java
Performans log kayıtlarını yöneten servis.

**Özellikler:**
- ✅ Asenkron log kaydetme (@Async)
- ✅ Otomatik filtreleme (1000ms+ kontrol)
- ✅ Log sorgulama metodları
- ✅ İstatistik raporlama
- ✅ Eski log temizleme
- ✅ Konsol output (yavaş işlem uyarıları)

## Nasıl Çalışır?

### 1. AOP Flow

```
@Service Method Çağrısı
    ↓
PerformanceAspect.measureMethodExecutionTime() [BEFORE]
    - startTime = System.currentTimeMillis()
    ↓
Actual Service Method Execution
    ↓
PerformanceAspect [AFTER]
    - endTime = System.currentTimeMillis()
    - duration = endTime - startTime
    - if (duration > 1000ms)
        → PerformanceLogService.savePerformanceLog() [ASYNC]
    ↓
performance_logs tablosuna kayıt
    ↓
Return to Client
```

### 2. Pointcut Expression

```java
@Around("execution(* com.akademi.egitimtakip.service.*.*(..)) && ...")
```

**Anlamı:**
- `execution(*)` - Herhangi bir return type
- `com.akademi.egitimtakip.service.*` - Service package'ındaki tüm sınıflar
- `.*(..)` - Herhangi bir metod, herhangi bir parametre

**Hariç tutulanlar:**
- `PerformanceLogService` (sonsuz döngü önleme)
- `LogService` (sonsuz döngü önleme)
- `ApiLogService` (sonsuz döngü önleme)
- `ActivityLogService` (sonsuz döngü önleme)
- `ErrorLogService` (sonsuz döngü önleme)

## Loglanan Bilgiler

| Alan | Değer | Örnek |
|------|-------|-------|
| endpoint | methodName | `EgitimService.createEgitim` |
| durationMs | İşlem süresi | `1523` (milisaniye) |
| methodName | ClassName.methodName | `EgitimService.createEgitim` |
| createdAt | Otomatik timestamp | `2024-12-04 12:30:15` |

## Kullanım Örnekleri

### Otomatik Loglama (Hiçbir şey yapmanıza gerek yok!)

```java
@Service
public class EgitimService {
    
    // Bu metod 1500ms sürerse otomatik loglanır
    public EgitimResponseDTO createEgitim(EgitimRequestDTO dto) {
        // ... iş mantığı (1500ms sürer) ...
        return result;
    }
    
    // Bu metod 500ms sürerse loglanmaz (1000ms altı)
    public EgitimResponseDTO getEgitimById(Long id) {
        // ... hızlı işlem (500ms) ...
        return result;
    }
}
```

### Log Sorgulama

```java
@Autowired
private PerformanceLogService performanceLogService;

// En yavaş 50 işlem
List<PerformanceLog> slowest = performanceLogService.getTop50SlowestOperations();

// 2 saniyeden uzun sürenler
List<PerformanceLog> verySlow = performanceLogService.getSlowOperations(2000L);

// Belirli bir metod
List<PerformanceLog> createLogs = performanceLogService.getLogsByEndpoint("createEgitim");

// Ortalama süreler
List<Object[]> avgDurations = performanceLogService.getAverageDurationByEndpoint();
for (Object[] row : avgDurations) {
    String method = (String) row[0];
    Double avgMs = (Double) row[1];
    System.out.println(method + ": " + avgMs + "ms");
}

// İstatistik raporu
performanceLogService.printPerformanceStatistics();
```

### Eski Logları Temizle

```java
// 30 günden eski performans loglarını sil
LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
performanceLogService.deleteOldLogs(thirtyDaysAgo);
```

## Konsol Çıktısı

Yavaş işlemler konsola otomatik yazdırılır:

```
⚠️  Yavaş İşlem: EgitimService.createEgitim - 1523ms (1.52s)
⚠️  Yavaş İşlem: ProjeService.generateReport - 3210ms (3.21s)
⚠️  Yavaş İşlem: EgitimService.getAllEgitimler - 1100ms (1.10s)
⚠️  Yavaş İşlem: PaydasService.exportToExcel - 5432ms (5.43s)
```

## İstatistik Raporu

```java
performanceLogService.printPerformanceStatistics();
```

**Çıktı:**
```
📊 Performans İstatistikleri:
═══════════════════════════════════════════════════════
Endpoint/Method                                    Ortalama Süre
───────────────────────────────────────────────────────
EgitimService.createEgitim                         1523ms
ProjeService.generateReport                        3210ms
EgitimService.getAllEgitimler                      1100ms
PaydasService.exportToExcel                        5432ms
═══════════════════════════════════════════════════════
```

## H2 Console'da Sorgulama

```sql
-- Tüm yavaş işlemler
SELECT * FROM performance_logs 
ORDER BY duration_ms DESC 
LIMIT 100;

-- En yavaş 10 metod
SELECT method_name, duration_ms, created_at
FROM performance_logs
ORDER BY duration_ms DESC
LIMIT 10;

-- Method'a göre ortalama süreler
SELECT method_name, 
       AVG(duration_ms) as avg_duration,
       MAX(duration_ms) as max_duration,
       MIN(duration_ms) as min_duration,
       COUNT(*) as call_count
FROM performance_logs
GROUP BY method_name
ORDER BY avg_duration DESC;

-- Son 24 saatteki yavaş işlemler
SELECT * FROM performance_logs
WHERE created_at >= DATEADD('HOUR', -24, CURRENT_TIMESTAMP)
ORDER BY duration_ms DESC;

-- 3 saniyeden uzun sürenler
SELECT * FROM performance_logs
WHERE duration_ms > 3000
ORDER BY created_at DESC;

-- Method çağrı sıklığı
SELECT method_name, COUNT(*) as slow_call_count
FROM performance_logs
GROUP BY method_name
ORDER BY slow_call_count DESC;
```

## Örnek Senaryolar

### Senaryo 1: Yavaş Database Query

```java
@Service
public class EgitimService {
    
    // Bu metod N+1 problemi içeriyor ve yavaş
    public List<EgitimResponseDTO> getAllEgitimlerWithDetails() {
        List<Egitim> egitimler = egitimRepository.findAll();
        // N+1 query problemi: Her eğitim için ayrı sorgu
        for (Egitim egitim : egitimler) {
            egitim.getKategoriler().size(); // Lazy load
            egitim.getEgitmenler().size();  // Lazy load
        }
        return mapToDTO(egitimler);
    }
}
```

**Performance Log:**
```
⚠️  Yavaş İşlem: EgitimService.getAllEgitimlerWithDetails - 2345ms (2.35s)

performance_logs tablosu:
method_name: EgitimService.getAllEgitimlerWithDetails
duration_ms: 2345
```

**Çözüm:** Eager loading veya JOIN FETCH kullanın.

### Senaryo 2: Yavaş External API Call

```java
@Service
public class NotificationService {
    
    // External SMS API çağrısı yavaş
    public void sendSmsNotifications(List<String> phones) {
        for (String phone : phones) {
            externalSmsApi.send(phone, message); // Her biri 500ms
        }
        // 10 telefon = 5000ms
    }
}
```

**Performance Log:**
```
⚠️  Yavaş İşlem: NotificationService.sendSmsNotifications - 5123ms (5.12s)
```

**Çözüm:** Parallel processing veya async batch sending.

### Senaryo 3: Yavaş File Operation

```java
@Service
public class ReportService {
    
    // Büyük Excel dosyası oluşturma
    public byte[] generateExcelReport(List<Egitim> egitimler) {
        // 10000 satır Excel oluşturma
        return createExcel(egitimler); // 3500ms
    }
}
```

**Performance Log:**
```
⚠️  Yavaş İşlem: ReportService.generateExcelReport - 3567ms (3.57s)
```

**Çözüm:** Pagination, stream processing, veya async generation.

## Test Etme

### 1. Yavaş Method Oluştur (Test Amaçlı)

```java
@Service
public class TestService {
    
    public String slowMethod() {
        try {
            Thread.sleep(1500); // 1.5 saniye bekle
        } catch (InterruptedException e) {
            // ignore
        }
        return "Done";
    }
}
```

### 2. Method'u Çağır

```java
@RestController
@RequestMapping("/test")
public class TestController {
    
    @Autowired
    private TestService testService;
    
    @GetMapping("/slow")
    public String testSlowMethod() {
        return testService.slowMethod();
    }
}
```

### 3. API Çağrısı Yap

```bash
curl http://localhost:8080/test/slow
```

### 4. Konsolu İzle

```
⚠️  Yavaş İşlem: TestService.slowMethod - 1502ms (1.50s)
```

### 5. H2 Console'da Kontrol Et

```sql
SELECT * FROM performance_logs 
WHERE method_name = 'TestService.slowMethod'
ORDER BY created_at DESC;
```

## Performans İyileştirme Önerileri

### 1. Database Query Optimization

**Problem:**
```java
// N+1 Query
List<Egitim> egitimler = egitimRepository.findAll();
egitimler.forEach(e -> e.getKategoriler().size());
```

**Çözüm:**
```java
// JOIN FETCH
@Query("SELECT DISTINCT e FROM Egitim e LEFT JOIN FETCH e.kategoriler")
List<Egitim> findAllWithKategoriler();
```

### 2. Pagination

**Problem:**
```java
// Tüm kayıtları getir (10000+ satır)
List<Egitim> all = egitimRepository.findAll();
```

**Çözüm:**
```java
// Sayfalama kullan
Page<Egitim> page = egitimRepository.findAll(PageRequest.of(0, 20));
```

### 3. Caching

**Problem:**
```java
// Her çağrıda database'den çek
public List<Kategori> getAllKategoriler() {
    return kategoriRepository.findAll();
}
```

**Çözüm:**
```java
@Cacheable("kategoriler")
public List<Kategori> getAllKategoriler() {
    return kategoriRepository.findAll();
}
```

### 4. Async Processing

**Problem:**
```java
// Senkron email gönderimi
public void sendEmails(List<String> emails) {
    emails.forEach(this::sendEmail);
}
```

**Çözüm:**
```java
@Async
public void sendEmailsAsync(List<String> emails) {
    emails.forEach(this::sendEmail);
}
```

## Scheduled Reporting

Otomatik performans raporu:

```java
@Component
public class PerformanceReportScheduler {
    
    @Autowired
    private PerformanceLogService performanceLogService;
    
    @Scheduled(cron = "0 0 9 * * *") // Her gün 09:00
    public void dailyPerformanceReport() {
        System.out.println("\n🕐 Günlük Performans Raporu - " + LocalDate.now());
        performanceLogService.printPerformanceStatistics();
        
        // Son 24 saatte 5 saniyeden uzun sürenler
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        LocalDateTime now = LocalDateTime.now();
        List<PerformanceLog> criticalSlow = performanceLogService
            .getLogsByDateRange(yesterday, now)
            .stream()
            .filter(log -> log.getDurationMs() > 5000)
            .toList();
        
        if (!criticalSlow.isEmpty()) {
            System.out.println("\n🔴 Kritik Yavaş İşlemler (5s+):");
            criticalSlow.forEach(log -> 
                System.out.println("  - " + log.getMethodName() + ": " + log.getDurationMs() + "ms")
            );
        }
    }
}
```

## Scheduled Cleanup

Eski logları otomatik temizle:

```java
@Component
public class PerformanceLogCleanupScheduler {
    
    @Autowired
    private PerformanceLogService performanceLogService;
    
    @Scheduled(cron = "0 0 3 * * SUN") // Her Pazar 03:00
    public void weeklyCleanup() {
        LocalDateTime twoMonthsAgo = LocalDateTime.now().minusMonths(2);
        performanceLogService.deleteOldLogs(twoMonthsAgo);
    }
}
```

## Custom Threshold

Eşik değerini değiştirmek için:

```java
@Around("execution(* com.akademi.egitimtakip.service.*.*(..))")
public Object measureMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    // ...
    
    // Özel eşik: 2 saniye
    if (duration > 2000) {
        performanceLogService.savePerformanceLog(methodName, duration, methodName);
    }
    
    return result;
}
```

Veya annotation-based:

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MonitorPerformance {
    long thresholdMs() default 1000;
}

// Kullanım
@MonitorPerformance(thresholdMs = 500) // 500ms eşiği
public void criticalMethod() {
    // ...
}
```

## Performans

- **AOP Overhead:** Minimal (~1-2ms per method call)
- **Asenkron Loglama:** Ana iş akışını bloke etmez
- **Filtreleme:** Sadece yavaş işlemler kaydedilir (bellek tasarrufu)

## Best Practices

1. **Eşik Değeri:** 1000ms genellikle iyi bir başlangıç noktasıdır
2. **Düzenli Temizlik:** Eski logları temizleyin (disk alanı)
3. **İstatistik İnceleme:** Periyodik olarak performans trendlerini inceleyin
4. **Root Cause Analysis:** Yavaş metodların nedenini bulun ve optimize edin
5. **Monitoring:** Production'da monitoring tool ile entegre edin

## Özellikler Özeti

✅ **Otomatik Monitoring:** Tüm @Service metodları
✅ **Eşik Tabanlı:** Sadece 1000ms+ kaydedilir
✅ **AOP Magic:** Kod kirliliği yok
✅ **Asenkron:** Performans etkisi yok
✅ **Detailed Logging:** ClassName.methodName formatı
✅ **Console Alerts:** Gerçek zamanlı uyarılar
✅ **Statistics:** Ortalama, min, max süre raporları
✅ **Query Methods:** Çeşitli sorgulama seçenekleri
✅ **Cleanup:** Otomatik eski log temizleme
✅ **Zero Configuration:** Hiçbir şey yapmanıza gerek yok!

## Notlar

- Log service'leri hariç tutulur (sonsuz döngü önleme)
- Asenkron loglama performans kaybı yaratmaz
- Konsol çıktısı development ortamında yararlıdır
- Production'da monitoring tool entegrasyonu önerilir
- Düzenli log temizliği önemlidir

Artık tüm yavaş servis metodları otomatik olarak loglanıyor! 🎉





