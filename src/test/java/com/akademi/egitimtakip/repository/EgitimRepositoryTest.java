package com.akademi.egitimtakip.repository;

import com.akademi.egitimtakip.entity.Egitim;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * EgitimRepository Integration Test
 * 
 * EgitimRepository için CRUD işlemlerini test eder.
 * H2 in-memory database kullanır, her test @Transactional ile rollback yapar.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EgitimRepositoryTest {

    @Autowired
    private EgitimRepository egitimRepository;

    private Egitim testEgitim;

    /**
     * Her test öncesi temiz test verisi oluşturulur.
     */
    @BeforeEach
    void setUp() {
        testEgitim = new Egitim();
        testEgitim.setAd("Spring Boot Temel Eğitimi");
        testEgitim.setAciklama("Spring Boot framework'ünün temel kavramlarını öğrenmek için hazırlanmış eğitim.");
        testEgitim.setBaslangicTarihi(LocalDate.of(2024, 3, 1));
        testEgitim.setBitisTarihi(LocalDate.of(2024, 3, 5));
        testEgitim.setEgitimSaati(480); // 8 saat x 60 dakika
        testEgitim.setDurum("Planlandı");
    }

    /**
     * CREATE Test: Yeni bir Egitim kaydı oluşturulur ve veritabanına kaydedilir.
     * Neden: Repository'nin save() metodunun çalıştığını ve ID'nin otomatik oluşturulduğunu doğrular.
     */
    @Test
    @DisplayName("CREATE: Yeni eğitim kaydı oluşturulmalı")
    void testCreateEgitim() {
        // Given: testEgitim objesi hazır (setUp'da oluşturuldu)

        // When: Egitim kaydedilir
        Egitim savedEgitim = egitimRepository.save(testEgitim);

        // Then: Kayıt başarılı olmalı ve ID atanmış olmalı
        assertThat(savedEgitim.getId()).isNotNull();
        assertThat(savedEgitim.getAd()).isEqualTo("Spring Boot Temel Eğitimi");
        assertThat(savedEgitim.getEgitimSaati()).isEqualTo(480);
        assertThat(savedEgitim.getDurum()).isEqualTo("Planlandı");
        
        // Veritabanında kayıt var mı kontrol et
        assertThat(egitimRepository.count()).isEqualTo(1);
    }

    /**
     * READ Test: Kaydedilen Egitim'i ID ile çeker.
     * Neden: Repository'nin findById() metodunun doğru çalıştığını ve kaydın okunabildiğini doğrular.
     */
    @Test
    @DisplayName("READ: ID ile eğitim kaydı okunmalı")
    void testReadEgitim() {
        // Given: Egitim kaydedilir
        Egitim savedEgitim = egitimRepository.save(testEgitim);
        Long egitimId = savedEgitim.getId();

        // When: ID ile Egitim çekilir
        Optional<Egitim> foundEgitim = egitimRepository.findById(egitimId);

        // Then: Kayıt bulunmalı ve değerler doğru olmalı
        assertThat(foundEgitim).isPresent();
        assertThat(foundEgitim.get().getId()).isEqualTo(egitimId);
        assertThat(foundEgitim.get().getAd()).isEqualTo("Spring Boot Temel Eğitimi");
        assertThat(foundEgitim.get().getBaslangicTarihi()).isEqualTo(LocalDate.of(2024, 3, 1));
        assertThat(foundEgitim.get().getBitisTarihi()).isEqualTo(LocalDate.of(2024, 3, 5));
    }

    /**
     * UPDATE Test: Mevcut bir Egitim kaydının adını günceller.
     * Neden: Repository'nin save() metodunun mevcut kayıtları güncelleyebildiğini doğrular.
     */
    @Test
    @DisplayName("UPDATE: Eğitim adı güncellenmeli")
    void testUpdateEgitim() {
        // Given: Egitim kaydedilir
        Egitim savedEgitim = egitimRepository.save(testEgitim);
        Long egitimId = savedEgitim.getId();

        // When: Ad güncellenir
        savedEgitim.setAd("Spring Boot İleri Seviye Eğitimi");
        savedEgitim.setDurum("Devam Ediyor");
        Egitim updatedEgitim = egitimRepository.save(savedEgitim);

        // Then: Güncelleme başarılı olmalı, ID aynı kalmalı, ad değişmeli
        assertThat(updatedEgitim.getId()).isEqualTo(egitimId);
        assertThat(updatedEgitim.getAd()).isEqualTo("Spring Boot İleri Seviye Eğitimi");
        assertThat(updatedEgitim.getDurum()).isEqualTo("Devam Ediyor");
        
        // Veritabanından tekrar çekerek doğrula
        Optional<Egitim> foundEgitim = egitimRepository.findById(egitimId);
        assertThat(foundEgitim).isPresent();
        assertThat(foundEgitim.get().getAd()).isEqualTo("Spring Boot İleri Seviye Eğitimi");
    }

    /**
     * DELETE Test: Bir Egitim kaydını siler ve silindiğini doğrular.
     * Neden: Repository'nin deleteById() metodunun çalıştığını ve kaydın silindiğini doğrular.
     */
    @Test
    @DisplayName("DELETE: Eğitim kaydı silinmeli")
    void testDeleteEgitim() {
        // Given: Egitim kaydedilir
        Egitim savedEgitim = egitimRepository.save(testEgitim);
        Long egitimId = savedEgitim.getId();
        assertThat(egitimRepository.count()).isEqualTo(1);

        // When: Egitim silinir
        egitimRepository.deleteById(egitimId);

        // Then: Kayıt silinmiş olmalı
        assertThat(egitimRepository.count()).isEqualTo(0);
        Optional<Egitim> foundEgitim = egitimRepository.findById(egitimId);
        assertThat(foundEgitim).isEmpty();
    }

    /**
     * Custom Query Test: findByAdContainingIgnoreCase metodunu test eder.
     * Neden: Repository'deki custom query metodlarının çalıştığını doğrular.
     */
    @Test
    @DisplayName("Custom Query: Ada göre eğitim arama çalışmalı")
    void testFindByAdContainingIgnoreCase() {
        // Given: Birden fazla eğitim kaydedilir
        Egitim egitim1 = new Egitim();
        egitim1.setAd("Spring Boot Eğitimi");
        egitim1.setDurum("Planlandı");
        egitimRepository.save(egitim1);

        Egitim egitim2 = new Egitim();
        egitim2.setAd("Java Temel Eğitimi");
        egitim2.setDurum("Planlandı");
        egitimRepository.save(egitim2);

        Egitim egitim3 = new Egitim();
        egitim3.setAd("Spring Boot İleri Seviye");
        egitim3.setDurum("Planlandı");
        egitimRepository.save(egitim3);

        // When: "Spring Boot" içeren eğitimler aranır
        var results = egitimRepository.findByAdContainingIgnoreCase("spring boot");

        // Then: Sadece "Spring Boot" içeren eğitimler bulunmalı
        assertThat(results).hasSize(2);
        assertThat(results).extracting(Egitim::getAd)
                .containsExactlyInAnyOrder("Spring Boot Eğitimi", "Spring Boot İleri Seviye");
    }
}

