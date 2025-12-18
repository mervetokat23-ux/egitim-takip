package com.akademi.egitimtakip.controller;

import com.akademi.egitimtakip.annotation.RequirePermission;
import com.akademi.egitimtakip.dto.FaaliyetRequestDTO;
import com.akademi.egitimtakip.dto.FaaliyetResponseDTO;
import com.akademi.egitimtakip.service.FaaliyetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Faaliyet Controller
 * 
 * Faaliyet CRUD endpoint'lerini sağlar.
 * Permission-based authorization: activity module
 */
@RestController
@RequestMapping("/faaliyet")
@CrossOrigin(origins = "*")
public class FaaliyetController {

    @Autowired
    private FaaliyetService faaliyetService;

    /**
     * GET /faaliyet - Tüm faaliyetleri listele
     * Required Permission: activity.view
     */
    @GetMapping
    @RequirePermission(module = "activity", action = "view", description = "View all activities")
    public ResponseEntity<Page<FaaliyetResponseDTO>> getAllFaaliyetler(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort,
            @RequestParam(required = false) Long projeId,
            @RequestParam(required = false) String turu) {
        
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc") 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;
        String sortField = sortParams[0];
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        
        Page<FaaliyetResponseDTO> faaliyetler = faaliyetService.getAllFaaliyetler(pageable, projeId, turu);
        return ResponseEntity.ok(faaliyetler);
    }

    /**
     * GET /faaliyet/{id} - ID ile faaliyet getir
     * Required Permission: activity.view
     */
    @GetMapping("/{id}")
    @RequirePermission(module = "activity", action = "view", description = "View activity by ID")
    public ResponseEntity<FaaliyetResponseDTO> getFaaliyetById(@PathVariable Long id) {
        try {
            FaaliyetResponseDTO faaliyet = faaliyetService.getFaaliyetById(id);
            return ResponseEntity.ok(faaliyet);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * POST /faaliyet - Yeni faaliyet oluştur
     * Required Permission: activity.create
     */
    @PostMapping
    @RequirePermission(module = "activity", action = "create", description = "Create new activity")
    public ResponseEntity<FaaliyetResponseDTO> createFaaliyet(@Valid @RequestBody FaaliyetRequestDTO requestDTO) {
        try {
            FaaliyetResponseDTO response = faaliyetService.createFaaliyet(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * PUT /faaliyet/{id} - Faaliyet güncelle
     * Required Permission: activity.update
     */
    @PutMapping("/{id}")
    @RequirePermission(module = "activity", action = "update", description = "Update activity")
    public ResponseEntity<FaaliyetResponseDTO> updateFaaliyet(
            @PathVariable Long id,
            @Valid @RequestBody FaaliyetRequestDTO requestDTO) {
        try {
            FaaliyetResponseDTO response = faaliyetService.updateFaaliyet(id, requestDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * DELETE /faaliyet/{id} - Faaliyet sil
     * Required Permission: activity.delete
     */
    @DeleteMapping("/{id}")
    @RequirePermission(module = "activity", action = "delete", description = "Delete activity")
    public ResponseEntity<Void> deleteFaaliyet(@PathVariable Long id) {
        try {
            faaliyetService.deleteFaaliyet(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
