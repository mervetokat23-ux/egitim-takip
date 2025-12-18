## Akademi Eğitim Takip Sistemi - Backend

Spring Boot ve **H2 Database** üzerinde çalışan **Akademi Eğitim Takip Sistemi** backend projesi.  
Eğitim, eğitmen, sorumlu, proje, faaliyet, paydaş ve ödeme yönetimi için REST API altyapısını sağlar.

### Teknolojiler
- Spring Boot 3.3.4 (Java 17)
- Maven
- **H2 Database** (kurulum gerektirmez, dosya tabanlı)
- Flyway (migration yönetimi)
- SpringDoc OpenAPI (Swagger) - API dokümantasyonu

### Geliştirme Ortamı Gereksinimleri
- Java 17 (veya uyumlu bir JDK)
- Maven 3.8+
- **H2 Database kurulumu gerekmez** - Spring Boot ile otomatik olarak gelir

### H2 Database Özellikleri
- **Kurulum gerektirmez** - Proje ile birlikte gelir
- **Dosya tabanlı** - Veriler `./data/egitim_takip_dev.mv.db` dosyasında saklanır
- **H2 Console** - Web arayüzü ile veritabanını görüntüleyebilirsiniz (`http://localhost:8080/h2-console`)
- **JPA/Hibernate uyumlu** - PostgreSQL'e geçiş kolay (sadece dependency değişir)

### Nasıl Çalıştırılır

1. **Proje kök klasörüne gel**
   ```bash
   cd egitim-takip
   ```

2. **Maven ile bağımlılıkları indir ve projeyi derle**
   ```bash
   mvn clean install
   ```

3. **Uygulamayı başlat**
   ```bash
   mvn spring-boot:run
   ```

   Varsayılan olarak:
   - Uygulama `dev` profili ile başlar
   - Sunucu `http://localhost:8080` adresinde dinler
   - H2 veritabanı otomatik olarak `./data/` klasöründe oluşturulur

4. **H2 Console'a erişim** (opsiyonel)
   - Tarayıcıda `http://localhost:8080/h2-console` adresine git
   - JDBC URL: `jdbc:h2:file:./data/egitim_takip_dev`
   - Username: `sa`
   - Password: (boş bırak)
   - Connect butonuna tıkla

5. **API Dokümantasyonu** (Swagger)
   - Tarayıcıda `http://localhost:8080/swagger-ui.html` adresine git
   - Tüm REST endpoint'lerini görüntüleyebilir ve test edebilirsin

### Veritabanı Yapısı

- **Migration dosyaları**: `src/main/resources/db/migration/` klasörüne eklenir
- **Flyway** otomatik olarak migration'ları çalıştırır
- Örnek migration: `V1__init_schema.sql`

### Sonraki Adımlar

1. **Migration dosyalarını oluştur**
   - `src/main/resources/db/migration/V1__init_schema.sql` dosyasını oluştur
   - Tabloları, ilişkileri ve index'leri tanımla

2. **Domain katmanlarını ekle**
   - Entity sınıfları (Eğitim, Eğitmen, Sorumlu, Proje, Faaliyet, Paydaş, Ödeme)
   - Repository interface'leri
   - Service sınıfları
   - Controller sınıfları (REST endpoints)

3. **Test yaz**
   - Unit testler (JUnit)
   - Integration testler
   - E2E testler (Cypress - frontend için)

### PostgreSQL'e Geçiş (İsteğe Bağlı)

Eğer ileride PostgreSQL kullanmak isterseniz:

1. `pom.xml` içinde H2 dependency'sini kaldır, PostgreSQL ekle:
   ```xml
   <dependency>
       <groupId>org.postgresql</groupId>
       <artifactId>postgresql</artifactId>
       <scope>runtime</scope>
   </dependency>
   ```

2. `application-dev.properties` içinde datasource ayarlarını güncelle:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/egitim_takip_dev
   spring.datasource.username=your_user
   spring.datasource.password=your_password
   spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
   ```

3. Migration dosyaları aynı kalır (Flyway PostgreSQL ile de çalışır)

