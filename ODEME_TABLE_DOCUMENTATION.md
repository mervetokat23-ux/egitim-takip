# Ödeme (Payments) Tablosu - Dokümantasyon

## 📊 Tablo Yapısı

### **`odeme` Tablosu**

| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique payment identifier |
| `egitim_id` | BIGINT | FOREIGN KEY → egitim(id), NOT NULL | Related training/education |
| `birim_ucret` | DECIMAL(10,2) | | Unit price |
| `toplam_ucret` | DECIMAL(10,2) | | Total price (calculated) |
| `odeme_kaynagi` | VARCHAR(200) | | Payment source/description |
| `sorumlu_id` | BIGINT | FOREIGN KEY → sorumlu(id) | Responsible person |
| `durum` | VARCHAR(50) | | Status: "Beklemede", "Ödendi", "İptal" |
| `operasyon` | VARCHAR(100) | | Operation/Method: "Havale", "Nakit", "POS", "Sistem içi" |
| `is_deleted` | BOOLEAN | DEFAULT false, NOT NULL | Soft delete flag |

---

## 🔗 İlişkiler (Relationships)

```
odeme N:1 egitim
  - Bir ödeme bir eğitime ait
  - Bir eğitimin birden fazla ödemesi olabilir

odeme N:1 sorumlu
  - Bir ödemeden bir sorumlu sorumlu
  - Bir sorumlu birden fazla ödemeden sorumlu olabilir
```

---

## 📈 Performans İndeksleri

```sql
-- Foreign Key İndeksleri (JOIN performansı için)
CREATE INDEX idx_odeme_egitim_id ON odeme(egitim_id);
CREATE INDEX idx_odeme_sorumlu_id ON odeme(sorumlu_id);

-- Filtreleme İndeksleri (WHERE sorgularında hız için)
CREATE INDEX idx_odeme_durum ON odeme(durum);
CREATE INDEX idx_odeme_is_deleted ON odeme(is_deleted);
```

**Faydaları:**
- ✅ Eğitime göre ödemeleri listeleme: `WHERE egitim_id = ?` → **Hızlı**
- ✅ Duruma göre filtreleme: `WHERE durum = 'Ödendi'` → **Hızlı**
- ✅ Aktif ödemeler: `WHERE is_deleted = false` → **Hızlı**

---

## 🗑️ Soft Delete (Yumuşak Silme)

Kayıtlar fiziksel olarak silinmez, sadece `is_deleted = true` yapılır.

### Backend'de Kullanım:

```java
// Soft delete
odeme.setIsDeleted(true);
odemeRepository.save(odeme);

// Aktif ödemeleri getir
@Query("SELECT o FROM Odeme o WHERE o.isDeleted = false")
List<Odeme> findAllActive();

// Silinenleri getir
@Query("SELECT o FROM Odeme o WHERE o.isDeleted = true")
List<Odeme> findAllDeleted();
```

### Avantajları:
- ✅ Veri kaybı yok (geri getirilebilir)
- ✅ Audit trail korunur
- ✅ Raporlamada silinmiş kayıtlar analiz edilebilir

---

## 💰 İş Kuralları (Business Rules)

### 1. Toplam Ücret Hesaplama

```java
// Backend'de (Service layer):
public OdemeResponseDTO createOdeme(OdemeRequestDTO dto) {
    // Toplam ücret hesaplama mantığı
    if (dto.getToplamUcret() == null && dto.getBirimUcret() != null) {
        // Örnek: birimUcret * katılımcı sayısı
        // Veya frontend'den gelen değeri kullan
        dto.setToplamUcret(dto.getBirimUcret());
    }
    
    Odeme odeme = odemeMapper.toEntity(dto);
    odeme = odemeRepository.save(odeme);
    return odemeMapper.toDTO(odeme);
}
```

### 2. Durum Değerleri

```java
public enum OdemeDurum {
    BEKLEMEDE("Beklemede"),
    ODENDI("Ödendi"),
    IPTAL("İptal"),
    IADE("İade");
    
    private final String label;
    
    OdemeDurum(String label) {
        this.label = label;
    }
}
```

### 3. Operasyon (Ödeme Yöntemi) Değerleri

```java
public enum OdemeOperasyon {
    HAVALE("Havale"),
    NAKIT("Nakit"),
    POS("POS"),
    SISTEM_ICI("Sistem içi"),
    CEKILE("Çek/Senet");
    
    private final String label;
    
    OdemeOperasyon(String label) {
        this.label = label;
    }
}
```

---

## 📝 SQL Örnek Sorgular

### Create (Insert)

```sql
INSERT INTO odeme 
    (egitim_id, birim_ucret, toplam_ucret, odeme_kaynagi, sorumlu_id, durum, operasyon, is_deleted)
VALUES 
    (1, 1500.00, 45000.00, 'Proje Bütçesi', 2, 'Ödendi', 'Havale', false);
```

### Read (Select)

```sql
-- Tüm aktif ödemeleri getir
SELECT * FROM odeme WHERE is_deleted = false;

-- Eğitime göre ödemeleri getir
SELECT 
    o.id,
    o.birim_ucret,
    o.toplam_ucret,
    o.durum,
    o.operasyon,
    e.ad AS egitim_adi,
    s.ad AS sorumlu_ad,
    s.soyad AS sorumlu_soyad
FROM odeme o
JOIN egitim e ON o.egitim_id = e.id
LEFT JOIN sorumlu s ON o.sorumlu_id = s.id
WHERE o.egitim_id = 1 
  AND o.is_deleted = false;

-- Duruma göre ödemeleri getir
SELECT * FROM odeme 
WHERE durum = 'Beklemede' 
  AND is_deleted = false
ORDER BY id DESC;
```

### Update

```sql
-- Ödeme durumunu güncelle
UPDATE odeme 
SET durum = 'Ödendi', operasyon = 'Havale'
WHERE id = 1;
```

### Soft Delete

```sql
-- Soft delete (kayıt silinmiş olarak işaretle)
UPDATE odeme SET is_deleted = true WHERE id = 1;

-- Hard delete (gerçek silme - ÖNERİLMEZ)
DELETE FROM odeme WHERE id = 1;

-- Geri getir (undelete)
UPDATE odeme SET is_deleted = false WHERE id = 1;
```

---

## 🔍 Örnek Veri

```sql
INSERT INTO odeme (egitim_id, birim_ucret, toplam_ucret, odeme_kaynagi, sorumlu_id, durum, operasyon, is_deleted) VALUES
(1, 1500.00, 45000.00, 'TÜBİTAK Projesi', 1, 'Ödendi', 'Havale', false),
(2, 2000.00, 60000.00, 'Üniversite Bütçesi', 2, 'Beklemede', 'Nakit', false),
(3, 1200.00, 36000.00, 'Özel Şirket Sponsorluğu', 1, 'Ödendi', 'POS', false),
(4, 1800.00, 54000.00, 'Kamu Bütçesi', 3, 'İptal', 'Sistem içi', false),
(5, 2500.00, 75000.00, 'AB Fonları', 2, 'Ödendi', 'Havale', false);
```

---

## 🔧 Backend Query Örnekleri

### Repository

```java
@Repository
public interface OdemeRepository extends JpaRepository<Odeme, Long> {
    
    // Aktif ödemeleri getir
    List<Odeme> findByIsDeletedFalse();
    
    // Eğitime göre aktif ödemeleri getir
    List<Odeme> findByEgitimIdAndIsDeletedFalse(Long egitimId);
    
    // Duruma göre aktif ödemeleri getir
    List<Odeme> findByDurumAndIsDeletedFalse(String durum);
    
    // Sorumlunun aktif ödemelerini getir
    List<Odeme> findBySorumluIdAndIsDeletedFalse(Long sorumluId);
    
    // Toplam ödeme tutarını hesapla (aktif kayıtlar)
    @Query("SELECT SUM(o.toplamUcret) FROM Odeme o WHERE o.isDeleted = false")
    BigDecimal calculateTotalPayments();
    
    // Eğitime göre toplam ödeme
    @Query("SELECT SUM(o.toplamUcret) FROM Odeme o WHERE o.egitim.id = :egitimId AND o.isDeleted = false")
    BigDecimal calculateTotalByEgitim(@Param("egitimId") Long egitimId);
}
```

### Service

```java
@Service
public class OdemeService {
    
    @Autowired
    private OdemeRepository odemeRepository;
    
    // Soft delete
    public void softDelete(Long id) {
        Odeme odeme = odemeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ödeme bulunamadı: " + id));
        odeme.setIsDeleted(true);
        odemeRepository.save(odeme);
    }
    
    // Aktif ödemeleri getir
    public List<OdemeResponseDTO> getActivePayments() {
        return odemeRepository.findByIsDeletedFalse()
            .stream()
            .map(odemeMapper::toDTO)
            .collect(Collectors.toList());
    }
}
```

---

## 🎨 Frontend Kullanımı

### Ödeme Durumu Seçenekleri

```javascript
const durumOptions = [
  { value: 'Beklemede', label: 'Beklemede', color: 'warning' },
  { value: 'Ödendi', label: 'Ödendi', color: 'success' },
  { value: 'İptal', label: 'İptal', color: 'error' },
  { value: 'İade', label: 'İade', color: 'info' }
];
```

### Operasyon (Ödeme Yöntemi) Seçenekleri

```javascript
const operasyonOptions = [
  { value: 'Havale', label: 'Havale' },
  { value: 'Nakit', label: 'Nakit' },
  { value: 'POS', label: 'Kredi Kartı (POS)' },
  { value: 'Sistem içi', label: 'Sistem içi Transfer' },
  { value: 'Çek/Senet', label: 'Çek/Senet' }
];
```

### Soft Delete Butonu

```jsx
<button
  onClick={() => {
    if (confirm('Bu ödemeyi silmek istediğinize emin misiniz?')) {
      odemeAPI.delete(id); // Backend'de soft delete yapılır
    }
  }}
>
  Sil
</button>
```

---

## 🔐 Güvenlik & Validasyon

### Backend Validations

```java
@PrePersist
@PreUpdate
private void validatePayment() {
    // Toplam ücret negatif olamaz
    if (toplamUcret != null && toplamUcret.compareTo(BigDecimal.ZERO) < 0) {
        throw new IllegalArgumentException("Toplam ücret negatif olamaz");
    }
    
    // Birim ücret negatif olamaz
    if (birimUcret != null && birimUcret.compareTo(BigDecimal.ZERO) < 0) {
        throw new IllegalArgumentException("Birim ücret negatif olamaz");
    }
    
    // Eğitim zorunlu
    if (egitim == null) {
        throw new IllegalArgumentException("Eğitim ID zorunludur");
    }
}
```

---

## 📈 Raporlama Sorguları

### Toplam Ödeme Tutarı

```sql
SELECT 
    SUM(toplam_ucret) AS toplam_odeme,
    COUNT(*) AS odeme_sayisi
FROM odeme
WHERE is_deleted = false;
```

### Duruma Göre Özet

```sql
SELECT 
    durum,
    COUNT(*) AS adet,
    SUM(toplam_ucret) AS toplam
FROM odeme
WHERE is_deleted = false
GROUP BY durum;
```

### Ödeme Yöntemine Göre Dağılım

```sql
SELECT 
    operasyon,
    COUNT(*) AS adet,
    SUM(toplam_ucret) AS toplam
FROM odeme
WHERE is_deleted = false AND durum = 'Ödendi'
GROUP BY operasyon
ORDER BY toplam DESC;
```

### Sorumlulara Göre Ödeme Takibi

```sql
SELECT 
    s.ad,
    s.soyad,
    COUNT(o.id) AS odeme_sayisi,
    SUM(o.toplam_ucret) AS toplam_tutar
FROM sorumlu s
LEFT JOIN odeme o ON s.id = o.sorumlu_id AND o.is_deleted = false
GROUP BY s.id, s.ad, s.soyad
ORDER BY toplam_tutar DESC;
```

---

## 🔧 Migration Dosyası

**Dosya:** `src/main/resources/db/migration/V2__add_soft_delete_and_indexes_to_odeme.sql`

**Özellikler:**
- ✅ Soft delete column (`is_deleted`)
- ✅ 4 adet performans index'i
- ✅ Mevcut kayıtları güncelleme
- ✅ Column açıklamaları (comments)

---

## 🧪 Test Senaryoları

### 1. Soft Delete Testi

```java
@Test
public void testSoftDelete() {
    // Create payment
    Odeme odeme = new Odeme();
    odeme.setBirimUcret(new BigDecimal("1500.00"));
    odeme.setToplamUcret(new BigDecimal("45000.00"));
    odeme.setDurum("Ödendi");
    odeme.setIsDeleted(false);
    odemeRepository.save(odeme);
    
    // Soft delete
    odeme.setIsDeleted(true);
    odemeRepository.save(odeme);
    
    // Verify
    List<Odeme> activePayments = odemeRepository.findByIsDeletedFalse();
    assertFalse(activePayments.contains(odeme));
}
```

### 2. Index Performance Testi

```sql
-- Index kullanımını kontrol et
EXPLAIN SELECT * FROM odeme WHERE egitim_id = 1 AND is_deleted = false;

-- Beklenen: "Using index: idx_odeme_egitim_id"
```

---

## 🚀 Kurulum & Çalıştırma

### 1. Migration'ı Uygula

```bash
mvn clean compile
mvn spring-boot:run
```

**Console'da göreceksiniz:**
```
Flyway: Migrating schema to version 2 - add soft delete and indexes to odeme
Flyway: Successfully applied 1 migration
```

### 2. Veritabanını Kontrol Et

**H2 Console:**
```
http://localhost:8080/h2-console

-- Tablo yapısını kontrol et
SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'ODEME';

-- Index'leri kontrol et
SELECT * FROM INFORMATION_SCHEMA.INDEXES WHERE TABLE_NAME = 'ODEME';
```

---

## 📋 API Endpoints (Mevcut)

```
GET    /odeme              - Tüm ödemeleri listele (pagination)
GET    /odeme/{id}         - Ödeme detayını getir
POST   /odeme              - Yeni ödeme oluştur
PUT    /odeme/{id}         - Ödemeyi güncelle
DELETE /odeme/{id}         - Ödemeyi sil (soft delete)
```

### Örnek Request (POST /odeme)

```json
{
  "egitimId": 1,
  "birimUcret": 1500.00,
  "toplamUcret": 45000.00,
  "odemeKaynagi": "TÜBİTAK Projesi",
  "sorumluId": 2,
  "durum": "Beklemede",
  "operasyon": "Havale"
}
```

### Örnek Response (GET /odeme/1)

```json
{
  "id": 1,
  "birimUcret": 1500.00,
  "toplamUcret": 45000.00,
  "odemeKaynagi": "TÜBİTAK Projesi",
  "durum": "Ödendi",
  "operasyon": "Havale",
  "isDeleted": false,
  "egitim": {
    "id": 1,
    "ad": "Java Spring Boot Eğitimi"
  },
  "sorumlu": {
    "id": 2,
    "ad": "Mehmet",
    "soyad": "Demir"
  }
}
```

---

## 🎯 Özet

| Özellik | Durum | Açıklama |
|---------|-------|----------|
| **Entity** | ✅ | Odeme.java güncel |
| **DTO** | ✅ | Request & Response DTO'lar güncel |
| **Soft Delete** | ✅ | isDeleted field eklendi |
| **Indexes** | ✅ | 4 adet performans index'i |
| **Migration** | ✅ | Flyway migration dosyası oluşturuldu |
| **Foreign Keys** | ✅ | egitim_id, sorumlu_id |
| **Decimal Precision** | ✅ | DECIMAL(10,2) |

**Not:** Projede BIGINT ID kullanılıyor (UUID değil). Bu proje standartlarına uygun ve daha performanslı.


