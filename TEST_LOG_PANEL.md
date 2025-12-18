# Log Panel Test Adımları

## ✅ Tamamlananlar

1. ✅ MUI paketleri yüklendi (`@mui/material@5.18.0`)
2. ✅ Frontend başlatıldı (arka planda çalışıyor)
3. ✅ Backend başlatıldı (arka planda çalışıyor)
4. ✅ `FrontendLogService` oluşturuldu
5. ✅ `FrontendLogRepository` güncellendi

## 🧪 Test Etme (Manuel)

### 1. Frontend Açık mı Kontrol Et

Tarayıcıda açın:
```
http://localhost:3000
```

Beklenen: Login sayfası

### 2. ADMIN Olarak Giriş Yap

```
Email: admin@akademi.com
Password: admin123
```

### 3. Backend Çalışıyor mu Kontrol Et

Giriş yaptıktan sonra:
```
http://localhost:3000/egitim
```

- ✅ Eğitimler listesi görünüyorsa → Backend çalışıyor
- ❌ "Network Error" görünüyorsa → Backend çalışmıyor

### 4. Log Yönetimine Git

Navbar'da (üst menü) **"Log Yönetimi"** butonunu arayın:
- Mor renkte olmalı
- Sadece ADMIN kullanıcılar görür
- Sağ üstte, diğer menülerin yanında

### 5. Log Dashboard'u Aç

"Log Yönetimi" butonuna tıklayın.

Beklenen sonuç:
- 5 adet kart görmelisiniz:
  1. API Logları (Mavi)
  2. Kullanıcı Aksiyon Logları (Yeşil)
  3. Hata Logları (Kırmızı)
  4. Performans Logları (Turuncu)
  5. Frontend Logları (Mor)

### 6. Bir Log Türü Seç

Örnek: "API Logları" kartına tıklayın

#### Başarılı Durum (✅):
```
Toplam: X kayıt
┌────┬──────────┬────────┬────────┐
│ ID │ Endpoint │ Method │ Status │
├────┼──────────┼────────┼────────┤
│ ... tablo verileri ...          │
└────┴──────────┴────────┴────────┘
```

veya

```
Log kaydı bulunamadı
```

Bu normal! Sistem yeni başladı, henüz log yok.

#### Hata Durumu (❌):
```
Log yüklenirken hata oluştu
```

## 🔴 Hata Durumunda

### Adım 1: Browser Console Kontrol

1. **F12** tuşuna basın
2. **Console** sekmesine gidin
3. Hatayı kopyalayın ve paylaşın

Örnek hatalar:

#### Hata A: Backend çalışmıyor
```
GET http://localhost:8080/api/logs/api net::ERR_CONNECTION_REFUSED
```

**Çözüm:**
```bash
# Yeni terminal aç
cd C:\Users\MET\Training_Tracking
mvn spring-boot:run
```

#### Hata B: Yetki hatası
```
GET http://localhost:8080/api/logs/api 403 (Forbidden)
```

**Çözüm:** Logout yapıp ADMIN olarak tekrar login olun

#### Hata C: Service bulunamadı
```
Error creating bean with name 'logController': 
Unsatisfied dependency... FrontendLogService
```

**Çözüm:** Backend'i temizleyip yeniden başlatın:
```bash
mvn clean install
mvn spring-boot:run
```

### Adım 2: Backend Log Kontrol

Başka bir terminal'de:
```bash
cd C:\Users\MET\Training_Tracking
type boot.log
```

Son satırlarda şunu görmeli:
```
Started EgitimTakipApplication in X.XXX seconds
```

Göremiyorsanız backend başlamamış demektir.

### Adım 3: Network Tab Kontrol

1. **F12** > **Network** sekmesi
2. Log sayfasına gidin
3. API çağrısını bulun (örn: `api?page=0&size=20`)
4. Tıklayın ve:
   - **Status:** 200 OK (başarılı)
   - **Status:** 403 Forbidden (yetki yok)
   - **Status:** 500 Internal Server Error (backend hatası)
   - **Status:** Failed (backend çalışmıyor)

## 📊 Beklenen Davranışlar

### İlk Kurulumda
- Loglar boş olacak: "Log kaydı bulunamadı"
- Bu NORMAL

### Log Oluşturmak İçin
1. **API Logları:** Herhangi bir sayfaya gidin
2. **Activity Logları:** Bir eğitim oluşturun/düzenleyin
3. **Error Logları:** Geçersiz bir istek gönderin
4. **Performance Logları:** Backend'de yavaş bir işlem yapın

### Filtreleme Test
1. API Logları sayfasında
2. "Status Code" → `200` seçin
3. "Filtrele" butonuna tıklayın
4. Sadece 200 OK logları görünmeli

### Detay Modal Test
1. Bir log satırında 👁️ (göz) iconuna tıklayın
2. JSON modal açılmalı
3. Log detaylarını görmeli
4. "Kapat" butonu çalışmalı

## 🎯 Başarı Kriterleri

✅ Dashboard 5 kart gösteriyor
✅ Her kart tıklanabiliyor
✅ Log sayfası açılıyor (boş veya dolu)
✅ "Log yüklenirken hata oluştu" yok
✅ Filtreleme çalışıyor
✅ Pagination çalışıyor
✅ Detay modal açılıyor

## 📝 Hata Raporlama

Hata devam ediyorsa şunları paylaşın:

1. **Browser Console** ekran görüntüsü (F12 > Console)
2. **Network Tab** ekran görüntüsü (F12 > Network > başarısız istek)
3. **Backend log** son 50 satır:
   ```bash
   powershell -Command "Get-Content boot.log -Tail 50"
   ```

## 🚀 Hızlı Başlatma

Tüm sistemi başlatmak için:

### Terminal 1: Backend
```bash
cd C:\Users\MET\Training_Tracking
mvn clean spring-boot:run
```

Bekleyin: `Started EgitimTakipApplication`

### Terminal 2: Frontend
```bash
cd C:\Users\MET\Training_Tracking\frontend
npm start
```

Bekleyin: `webpack compiled successfully`

### Tarayıcı
```
http://localhost:3000
```

Login: `admin@akademi.com` / `admin123`

Navbar > "Log Yönetimi" (mor buton)

## 💡 İpuçları

- Backend başlamadan frontend açmayın
- ADMIN rolü olmadan log yönetimine erişemezsiniz
- İlk açılışta loglar boş normal
- MUI paketleri yüklü olmalı (`npm list @mui/material`)
- Her şeyi temizlemek için: `mvn clean` + `rm -rf frontend/node_modules` + `npm install`

---

**Sonraki Adım:** Yukarıdaki test adımlarını uygulayın ve hangi aşamada hata aldığınızı paylaşın.





