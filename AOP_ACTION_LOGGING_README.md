# AOP Action Logging System

## Genel Bakış

Spring AOP (Aspect-Oriented Programming) kullanarak kullanıcı aksiyonlarını otomatik olarak loglayan sistem.

**@LogAction** annotation'ı ile işaretlenen servis metodları, çalıştırıldıklarında `activity_logs` tablosuna otomatik kayıt atar.

## Oluşturulan Dosyalar

### 1. LogAction.java (Custom Annotation)
`@interface` tanımı. Servis metodlarına eklenir.

**Parametreler:**
- `action`: Aksiyon türü (CREATE, UPDATE, DELETE, VIEW, EXPORT, vb.)
- `entityType`: Entity türü (Egitim, Sorumlu, Proje, vb.)
- `description`: Açıklama (SpEL expression destekler)
- `entityIdParam`: Entity ID'nin hangi parametreden alınacağı (0-indexed, -1 = return value)
- `logOnError`: Hata durumunda da loglansın mı? (default: false)

### 2. ActionLogAspect.java (AOP Aspect)
`@Aspect` sınıfı. Annotation'ı yakalayıp loglama işlemini yapar.

**Özellikler:**
- ✅ @Around advice ile metod öncesi ve sonrası kontrol
- ✅ SecurityContext'ten kullanıcı bilgisi
- ✅ JWT token'dan userId çıkarma
- ✅ Method parametrelerinden entityId çıkarma
- ✅ SpEL expression evaluation
- ✅ Reflection ile getId() çağırma
- ✅ Hata durumunda loglama (opsiyonel)

### 3. ActivityLogService.java (Log Service)
Activity log kayıtlarını yöneten servis.

**Özellikler:**
- ✅ Asenkron log kaydetme (@Async)
- ✅ Log sorgulama metodları
- ✅ Helper metodlar (logCreate, logUpdate, logDelete, vb.)
- ✅ Eski log temizleme

## Nasıl Çalışır?

### 1. AOP Proxy Akışı

```
Client → Service Method (with @LogAction)
    ↓
ActionLogAspect.logAction() [BEFORE]
    - getCurrentUserId() (JWT / SecurityContext)
    - Save start time
    ↓
Actual Service Method Execution
    ↓
ActionLogAspect.logAction() [AFTER]
    - extractEntityId() from params/result
    - evaluateDescription() (SpEL)
    - ActivityLogService.saveActivityLog() [ASYNC]
    ↓
activity_logs tablosuna kayıt
    ↓
Return to Client
```

### 2. SpEL Expression Support

Description alanında SpEL (Spring Expression Language) kullanabilirsiniz:

| Expression | Anlamı | Örnek |
|------------|--------|-------|
| `#{args[0]}` | İlk parametre | `#{args[0]}` → `123` |
| `#{args[1].name}` | İkinci parametrenin name field'ı | `#{args[1].name}` → `"Java"` |
| `#{result}` | Return value | `#{result}` → `EgitimResponseDTO` |
| `#{result.ad}` | Return value'nun ad field'ı | `#{result.ad}` → `"Java Eğitimi"` |
| `#{result.id}` | Return value'nun id field'ı | `#{result.id}` → `1` |

### 3. Entity ID Extraction

`entityIdParam` parametresine göre:

| Değer | Kaynak | Açıklama |
|-------|--------|----------|
| `-1` | Return value | `result.getId()` çağrılır |
| `0` | İlk parametre | `args[0]` veya `args[0].getId()` |
| `1` | İkinci parametre | `args[1]` veya `args[1].getId()` |
| `2+` | Sonraki parametreler | `args[n]` veya `args[n].getId()` |

## Kullanım Örnekleri

### Örnek 1: CREATE İşlemi

```java
@Service
public class EgitimService {
    
    @LogAction(
        action = "CREATE",
        entityType = "Egitim",
        description = "Yeni eğitim oluşturuldu: #{result.ad}",
        entityIdParam = -1  // Return value'dan ID al
    )
    public EgitimResponseDTO createEgitim(EgitimRequestDTO requestDTO) {
        // ... iş mantığı ...
        return egitimMapper.toResponseDTO(egitim);
    }
}
```

**Loglanan:**
```
userId: 1
action: CREATE
entityType: Egitim
entityId: 123
description: "Yeni eğitim oluşturuldu: Java Eğitimi"
```

### Örnek 2: UPDATE İşlemi

```java
@LogAction(
    action = "UPDATE",
    entityType = "Egitim",
    description = "Eğitim güncellendi: #{result.ad}",
    entityIdParam = 0  // İlk parametre (id) entity ID'si
)
public EgitimResponseDTO updateEgitim(Long id, EgitimRequestDTO requestDTO) {
    // ... iş mantığı ...
    return egitimMapper.toResponseDTO(egitim);
}
```

**Loglanan:**
```
userId: 1
action: UPDATE
entityType: Egitim
entityId: 123 (args[0] → id parametresi)
description: "Eğitim güncellendi: Java Eğitimi"
```

### Örnek 3: DELETE İşlemi

```java
@LogAction(
    action = "DELETE",
    entityType = "Egitim",
    description = "Eğitim silindi (ID: #{args[0]})",
    entityIdParam = 0
)
public void deleteEgitim(Long id) {
    egitimRepository.deleteById(id);
}
```

**Loglanan:**
```
userId: 1
action: DELETE
entityType: Egitim
entityId: 123
description: "Eğitim silindi (ID: 123)"
```

### Örnek 4: VIEW İşlemi

```java
@LogAction(
    action = "VIEW",
    entityType = "Egitim",
    description = "Eğitim detayı görüntülendi",
    entityIdParam = 0
)
public EgitimResponseDTO getEgitimById(Long id) {
    return egitimMapper.toResponseDTO(egitim);
}
```

### Örnek 5: EXPORT İşlemi

```java
@LogAction(
    action = "EXPORT",
    entityType = "Egitim",
    description = "Eğitim listesi Excel formatında export edildi"
    // entityId yok (null kalacak)
)
public byte[] exportEgitimlerToExcel() {
    // ... export mantığı ...
    return excelBytes;
}
```

### Örnek 6: APPROVE İşlemi

```java
@LogAction(
    action = "APPROVE",
    entityType = "Proje",
    description = "Proje onaylandı: #{result.isim}",
    entityIdParam = 0
)
public ProjeResponseDTO approveProje(Long id) {
    Proje proje = projeRepository.findById(id).orElseThrow();
    proje.setDurum("Onaylandı");
    return projeMapper.toResponseDTO(projeRepository.save(proje));
}
```

### Örnek 7: Hata Durumunda Loglama

```java
@LogAction(
    action = "PROCESS",
    entityType = "Odeme",
    description = "Ödeme işlendi",
    entityIdParam = 0,
    logOnError = true  // Hata olsa bile logla
)
public OdemeResponseDTO processPayment(Long id) throws PaymentException {
    // Hata fırlatılırsa bile log kaydedilir
    return processLogic(id);
}
```

## ActivityLogService Helper Metodları

Manuel loglama için kullanılabilir:

```java
@Autowired
private ActivityLogService activityLogService;

// Create
activityLogService.logCreate(userId, "Egitim", egitimId, "Java Eğitimi");

// Update
activityLogService.logUpdate(userId, "Egitim", egitimId, "Java Eğitimi");

// Delete
activityLogService.logDelete(userId, "Egitim", egitimId, "Java Eğitimi");

// View
activityLogService.logView(userId, "Egitim", egitimId);

// Export
activityLogService.logExport(userId, "Egitim", "Excel");

// Custom
activityLogService.saveActivityLog(userId, "CUSTOM", "EntityType", entityId, "Description");
```

## Log Kayıtlarını Sorgulama

```java
@Autowired
private ActivityLogService activityLogService;

// Kullanıcının tüm aktiviteleri
List<ActivityLog> userLogs = activityLogService.getActivitiesByUserId(1L);

// Belirli bir entity'nin tüm aktiviteleri
List<ActivityLog> egitimLogs = activityLogService.getActivitiesByEntity("Egitim", 123L);

// Belirli aksiyon türü
List<ActivityLog> deleteLogs = activityLogService.getActivitiesByAction("DELETE");

// Tarih aralığı
LocalDateTime start = LocalDateTime.now().minusDays(7);
LocalDateTime end = LocalDateTime.now();
List<ActivityLog> weekLogs = activityLogService.getActivitiesByDateRange(start, end);

// Eski logları temizle (30 gün öncesi)
activityLogService.deleteOldActivities(LocalDateTime.now().minusDays(30));
```

## H2 Console'da Sorgulama

```sql
-- Tüm aktivite logları
SELECT * FROM activity_logs 
ORDER BY created_at DESC 
LIMIT 100;

-- Belirli kullanıcının aktiviteleri
SELECT * FROM activity_logs 
WHERE user_id = 1 
ORDER BY created_at DESC;

-- Belirli bir entity'nin tüm değişiklikleri
SELECT * FROM activity_logs 
WHERE entity_type = 'Egitim' AND entity_id = 123
ORDER BY created_at DESC;

-- En çok yapılan aksiyonlar
SELECT action, COUNT(*) as count
FROM activity_logs
GROUP BY action
ORDER BY count DESC;

-- Kullanıcı aktivite istatistikleri
SELECT user_id, action, COUNT(*) as action_count
FROM activity_logs
GROUP BY user_id, action
ORDER BY user_id, action_count DESC;

-- Son 24 saatteki aktiviteler
SELECT * FROM activity_logs
WHERE created_at >= DATEADD('HOUR', -24, CURRENT_TIMESTAMP)
ORDER BY created_at DESC;
```

## Konsol Çıktısı

Her log kaydedildiğinde konsola bilgi yazılır:

```
📝 Activity Log: [CREATE] Egitim - 123 (ID: 123) by User: 1 - Yeni eğitim oluşturuldu: Java Eğitimi
📝 Activity Log: [UPDATE] Egitim - 123 (ID: 123) by User: 1 - Eğitim güncellendi: Java Eğitimi
📝 Activity Log: [DELETE] Egitim - 123 (ID: 123) by User: 1 - Eğitim silindi (ID: 123)
📝 Activity Log: [EXPORT] Egitim - N/A (ID: N/A) by User: 1 - Eğitim listesi Excel formatında export edildi
```

## Test Etme

### 1. Backend'i Başlat

```bash
mvn spring-boot:run
```

### 2. API Çağrısı Yap

```bash
# Eğitim oluştur
curl -X POST http://localhost:8080/egitim \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"ad":"Java Eğitimi","durum":"Havuz"}'
```

### 3. H2 Console'da Kontrol Et

```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:file:./data/egitim_takip_dev
Username: sa, Password: (boş)

SELECT * FROM activity_logs ORDER BY created_at DESC LIMIT 10;
```

### 4. Konsol Loglarını İzle

```
📝 Activity Log: [CREATE] Egitim - 1 (ID: 1) by User: anonymous - Yeni eğitim oluşturuldu: Java Eğitimi
```

## Diğer Servislere Uygulama

### SorumluService

```java
@Service
public class SorumluService {
    
    @LogAction(
        action = "CREATE",
        entityType = "Sorumlu",
        description = "Yeni sorumlu eklendi: #{result.ad} #{result.soyad}",
        entityIdParam = -1
    )
    public SorumluResponseDTO createSorumlu(SorumluRequestDTO dto) {
        // ...
    }
    
    @LogAction(
        action = "UPDATE",
        entityType = "Sorumlu",
        description = "Sorumlu güncellendi",
        entityIdParam = 0
    )
    public SorumluResponseDTO updateSorumlu(Long id, SorumluRequestDTO dto) {
        // ...
    }
    
    @LogAction(
        action = "DELETE",
        entityType = "Sorumlu",
        description = "Sorumlu silindi",
        entityIdParam = 0
    )
    public void deleteSorumlu(Long id) {
        // ...
    }
}
```

### ProjeService

```java
@Service
public class ProjeService {
    
    @LogAction(
        action = "CREATE",
        entityType = "Proje",
        description = "Yeni proje oluşturuldu: #{result.isim}",
        entityIdParam = -1
    )
    public ProjeResponseDTO createProje(ProjeRequestDTO dto) {
        // ...
    }
    
    @LogAction(
        action = "STATUS_CHANGE",
        entityType = "Proje",
        description = "Proje durumu değiştirildi: #{args[1]}",
        entityIdParam = 0
    )
    public ProjeResponseDTO changeStatus(Long id, String newStatus) {
        // ...
    }
}
```

## Performans

- **Asenkron:** Log kayıt işlemi @Async ile asenkron çalışır, ana iş akışını bloke etmez
- **AOP Overhead:** Minimal performans etkisi (proxy-based AOP)
- **SpEL Evaluation:** Expression değerlendirme çok hızlı
- **Reflection:** getId() çağrısı cached, performans kaybı yok

## İleri Seviye

### Conditional Logging

```java
@LogAction(
    action = "UPDATE",
    entityType = "Egitim",
    description = "Eğitim güncellendi",
    entityIdParam = 0
)
public EgitimResponseDTO updateEgitim(Long id, EgitimRequestDTO dto) {
    // Sadece önemli değişiklikler için ekstra log
    if (dto.getDurum().equals("Tamamlandı")) {
        activityLogService.saveActivityLog(
            userId, "STATUS_COMPLETE", "Egitim", id, 
            "Eğitim tamamlandı olarak işaretlendi"
        );
    }
    // ...
}
```

### Scheduled Log Cleanup

```java
@Scheduled(cron = "0 0 3 * * *") // Her gün 03:00
public void cleanupOldActivityLogs() {
    LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
    activityLogService.deleteOldActivities(sixMonthsAgo);
}
```

### Custom Context Variables

```java
// ActionLogAspect.java'da
context.setVariable("currentDate", LocalDate.now());

// Kullanım
@LogAction(
    description = "Raporlandı: #{#currentDate}"
)
```

## Sorun Giderme

### Problem: AOP çalışmıyor

**Çözüm:**
1. `@EnableAspectJAutoProxy` annotation'ı ekli mi?
2. Servis `@Service` annotation'ı ile işaretli mi?
3. Method `public` mi? (AOP proxy-based, public metodlarda çalışır)
4. Self-invocation yok mu? (Aynı sınıf içinden direkt çağrı AOP'yi bypass eder)

### Problem: userId null geliyor

**Çözüm:**
1. JWT token geçerli mi?
2. Authorization header gönderiliyor mu?
3. SecurityContext'te authentication var mı?

### Problem: SpEL expression çalışmıyor

**Çözüm:**
1. Syntax doğru mu? `#{result.field}`, `#{args[0]}`
2. Field public getter'a sahip mi?
3. Result/args null değil mi?

## Özellikler Özeti

✅ **Custom Annotation** (@LogAction)
✅ **AOP Aspect** (ActionLogAspect)
✅ **Asenkron Loglama** (@Async)
✅ **SpEL Expression** (Dynamic descriptions)
✅ **Reflection** (Auto getId() extraction)
✅ **SecurityContext** (JWT/Spring Security)
✅ **Flexible** (entityIdParam configuration)
✅ **Error Handling** (logOnError flag)
✅ **Console Output** (Development friendly)
✅ **Query Methods** (ActivityLogService)

## Notlar

- Log hatası uygulamayı etkilemez
- Asenkron kayıt performans kaybı yaratmaz
- SpEL expression'lar güçlü ama dikkatli kullanılmalı
- Self-invocation AOP'yi bypass eder
- Method `public` olmalı

Artık tüm servis metodlarına @LogAction ekleyerek kullanıcı aksiyonlarını otomatik olarak loglayabilirsiniz! 🎉





