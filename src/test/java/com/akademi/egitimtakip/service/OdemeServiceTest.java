package com.akademi.egitimtakip.service;

import com.akademi.egitimtakip.dto.OdemeRequestDTO;
import com.akademi.egitimtakip.dto.OdemeResponseDTO;
import com.akademi.egitimtakip.entity.Egitim;
import com.akademi.egitimtakip.entity.Odeme;
import com.akademi.egitimtakip.entity.Sorumlu;
import com.akademi.egitimtakip.mapper.OdemeMapper;
import com.akademi.egitimtakip.repository.EgitimRepository;
import com.akademi.egitimtakip.repository.OdemeRepository;
import com.akademi.egitimtakip.repository.SorumluRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * OdemeService Unit Tests
 * 
 * Tests payment service business logic, validation, and calculations.
 */
@ExtendWith(MockitoExtension.class)
class OdemeServiceTest {

    @Mock
    private OdemeRepository odemeRepository;

    @Mock
    private EgitimRepository egitimRepository;

    @Mock
    private SorumluRepository sorumluRepository;

    @Mock
    private OdemeMapper odemeMapper;

    @InjectMocks
    private OdemeService odemeService;

    private Egitim testEgitim;
    private Sorumlu testSorumlu;
    private Odeme testOdeme;
    private OdemeRequestDTO testRequestDTO;
    private OdemeResponseDTO testResponseDTO;

    @BeforeEach
    void setUp() {
        // Setup test data
        testEgitim = new Egitim();
        testEgitim.setId(1L);
        testEgitim.setAd("Test Eğitim");

        testSorumlu = new Sorumlu();
        testSorumlu.setId(1L);
        testSorumlu.setAd("Test Sorumlu");

        testOdeme = new Odeme();
        testOdeme.setId(1L);
        testOdeme.setBirimUcret(new BigDecimal("100.00"));
        testOdeme.setToplamUcret(new BigDecimal("500.00"));
        testOdeme.setOdemeKaynagi("Test Kaynak");
        testOdeme.setDurum("Ödendi");
        testOdeme.setOperasyon("Havale");
        testOdeme.setEgitim(testEgitim);
        testOdeme.setSorumlu(testSorumlu);
        testOdeme.setIsDeleted(false);

        testRequestDTO = new OdemeRequestDTO();
        testRequestDTO.setEgitimId(1L);
        testRequestDTO.setBirimUcret(new BigDecimal("100.00"));
        testRequestDTO.setToplamUcret(new BigDecimal("500.00"));
        testRequestDTO.setOdemeKaynagi("Test Kaynak");
        testRequestDTO.setDurum("Ödendi");
        testRequestDTO.setOperasyon("Havale");
        testRequestDTO.setSorumluId(1L);
        testRequestDTO.setMiktar(5);

        testResponseDTO = new OdemeResponseDTO();
        testResponseDTO.setId(1L);
        testResponseDTO.setBirimUcret(new BigDecimal("100.00"));
        testResponseDTO.setToplamUcret(new BigDecimal("500.00"));
    }

    @Test
    void testCalculateTotalPrice_WithValidInputs() {
        // Given
        BigDecimal unitPrice = new BigDecimal("100.00");
        Integer quantity = 5;

        // When
        BigDecimal result = odemeService.calculateTotalPrice(unitPrice, quantity);

        // Then
        assertEquals(new BigDecimal("500.00"), result);
    }

    @Test
    void testCalculateTotalPrice_WithDefaultQuantity() {
        // Given
        BigDecimal unitPrice = new BigDecimal("100.00");

        // When
        BigDecimal result = odemeService.calculateTotalPrice(unitPrice, null);

        // Then
        assertEquals(new BigDecimal("100.00"), result);
    }

    @Test
    void testCalculateTotalPrice_WithInvalidUnitPrice() {
        // Given
        BigDecimal invalidUnitPrice = new BigDecimal("-10.00");
        Integer quantity = 5;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            odemeService.calculateTotalPrice(invalidUnitPrice, quantity);
        });
    }

    @Test
    void testCreateOdeme_WithValidData() {
        // Given
        when(egitimRepository.findById(1L)).thenReturn(Optional.of(testEgitim));
        when(sorumluRepository.findById(1L)).thenReturn(Optional.of(testSorumlu));
        when(odemeMapper.toEntity(any(OdemeRequestDTO.class))).thenReturn(testOdeme);
        when(odemeRepository.save(any(Odeme.class))).thenReturn(testOdeme);
        when(odemeMapper.toResponseDTO(any(Odeme.class))).thenReturn(testResponseDTO);

        // When
        OdemeResponseDTO result = odemeService.createOdeme(testRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals(testResponseDTO.getId(), result.getId());
        verify(odemeRepository, times(1)).save(any(Odeme.class));
    }

    @Test
    void testCreateOdeme_WithInvalidEgitimId() {
        // Given
        when(egitimRepository.findById(999L)).thenReturn(Optional.empty());
        testRequestDTO.setEgitimId(999L);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            odemeService.createOdeme(testRequestDTO);
        });
    }

    @Test
    void testCreateOdeme_WithInvalidBirimUcret() {
        // Given
        testRequestDTO.setBirimUcret(new BigDecimal("-10.00"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            odemeService.createOdeme(testRequestDTO);
        });
    }

    @Test
    void testGetOdemeById_WithValidId() {
        // Given
        when(odemeRepository.findById(1L)).thenReturn(Optional.of(testOdeme));
        when(odemeMapper.toResponseDTO(any(Odeme.class))).thenReturn(testResponseDTO);

        // When
        OdemeResponseDTO result = odemeService.getOdemeById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testResponseDTO.getId(), result.getId());
    }

    @Test
    void testGetOdemeById_WithInvalidId() {
        // Given
        when(odemeRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            odemeService.getOdemeById(999L);
        });
    }

    @Test
    void testUpdateOdeme_WithValidData() {
        // Given
        when(odemeRepository.findById(1L)).thenReturn(Optional.of(testOdeme));
        when(egitimRepository.findById(1L)).thenReturn(Optional.of(testEgitim));
        when(sorumluRepository.findById(1L)).thenReturn(Optional.of(testSorumlu));
        when(odemeRepository.save(any(Odeme.class))).thenReturn(testOdeme);
        when(odemeMapper.toResponseDTO(any(Odeme.class))).thenReturn(testResponseDTO);

        // When
        OdemeResponseDTO result = odemeService.updateOdeme(1L, testRequestDTO);

        // Then
        assertNotNull(result);
        verify(odemeRepository, times(1)).save(any(Odeme.class));
    }

    @Test
    void testDeleteOdeme_WithValidId() {
        // Given
        when(odemeRepository.existsById(1L)).thenReturn(true);

        // When
        odemeService.deleteOdeme(1L);

        // Then
        verify(odemeRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteOdeme_WithInvalidId() {
        // Given
        when(odemeRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            odemeService.deleteOdeme(999L);
        });
    }

    @Test
    void testGetAllOdemeler_WithFilters() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Odeme> odemePage = new PageImpl<>(Arrays.asList(testOdeme));
        when(odemeRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageable)))
                .thenReturn(odemePage);
        when(odemeMapper.toResponseDTO(any(Odeme.class))).thenReturn(testResponseDTO);

        // When
        Page<OdemeResponseDTO> result = odemeService.getAllOdemeler(
                pageable, 1L, "Ödendi", 1L, "Test Kaynak");

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }
}


