package com.akademi.egitimtakip.controller;

import com.akademi.egitimtakip.dto.DurumRequestDTO;
import com.akademi.egitimtakip.dto.DurumResponseDTO;
import com.akademi.egitimtakip.service.DurumService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Durum Controller
 * 
 * Durum CRUD endpoint'lerini sağlar.
 * JWT authentication ile korunur, sayfalama ve filtreleme desteği vardır.
 */
@RestController
@RequestMapping("/durum")
@CrossOrigin(origins = "*")
public class DurumController {

    @Autowired
    private DurumService durumService;

    @GetMapping
    public ResponseEntity<Page<DurumResponseDTO>> getAllDurumlar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort,
            @RequestParam(required = false) Long egitimId,
            @RequestParam(required = false) String durum) {
        
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc") 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;
        String sortField = sortParams[0];
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        
        Page<DurumResponseDTO> durumlar = durumService.getAllDurumlar(pageable, egitimId, durum);
        return ResponseEntity.ok(durumlar);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DurumResponseDTO> getDurumById(@PathVariable Long id) {
        try {
            DurumResponseDTO durum = durumService.getDurumById(id);
            return ResponseEntity.ok(durum);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SORUMLU')")
    public ResponseEntity<DurumResponseDTO> createDurum(@Valid @RequestBody DurumRequestDTO requestDTO) {
        try {
            DurumResponseDTO response = durumService.createDurum(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SORUMLU')")
    public ResponseEntity<DurumResponseDTO> updateDurum(
            @PathVariable Long id,
            @Valid @RequestBody DurumRequestDTO requestDTO) {
        try {
            DurumResponseDTO response = durumService.updateDurum(id, requestDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDurum(@PathVariable Long id) {
        try {
            durumService.deleteDurum(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

