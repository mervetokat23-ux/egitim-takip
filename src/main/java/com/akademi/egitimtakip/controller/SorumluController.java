package com.akademi.egitimtakip.controller;

import com.akademi.egitimtakip.annotation.RequirePermission;
import com.akademi.egitimtakip.dto.SorumluDTO;
import com.akademi.egitimtakip.service.SorumluService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Sorumlu Controller
 * 
 * Sorumlu CRUD endpoint'leri.
 * Permission-based authorization: responsible module
 */
@RestController
@RequestMapping("/sorumlu")
@CrossOrigin(origins = "*")
public class SorumluController {

    @Autowired
    private SorumluService sorumluService;

    /**
     * GET /sorumlu - Tüm sorumluları listele
     * Required Permission: responsible.view
     */
    @GetMapping
    @RequirePermission(module = "responsible", action = "view", description = "View all responsible persons")
    public ResponseEntity<List<SorumluDTO>> getAllSorumlular() {
        return ResponseEntity.ok(sorumluService.getAll());
    }

    /**
     * GET /sorumlu/page - Sayfalama ile listele
     * Required Permission: responsible.view
     */
    @GetMapping("/page")
    @RequirePermission(module = "responsible", action = "view", description = "View responsible persons with pagination")
    public ResponseEntity<Page<SorumluDTO>> getSorumlularPage(Pageable pageable) {
        return ResponseEntity.ok(sorumluService.getAll(pageable));
    }

    /**
     * GET /sorumlu/{id} - ID ile sorumlu getir
     * Required Permission: responsible.view
     */
    @GetMapping("/{id}")
    @RequirePermission(module = "responsible", action = "view", description = "View responsible by ID")
    public ResponseEntity<SorumluDTO> getSorumluById(@PathVariable Long id) {
        return ResponseEntity.ok(sorumluService.getById(id));
    }

    /**
     * POST /sorumlu - Yeni sorumlu oluştur
     * Required Permission: responsible.create
     */
    @PostMapping
    @RequirePermission(module = "responsible", action = "create", description = "Create new responsible person")
    public ResponseEntity<SorumluDTO> createSorumlu(@Valid @RequestBody SorumluDTO dto) {
        return ResponseEntity.ok(sorumluService.create(dto));
    }

    /**
     * PUT /sorumlu/{id} - Sorumlu güncelle
     * Required Permission: responsible.update
     */
    @PutMapping("/{id}")
    @RequirePermission(module = "responsible", action = "update", description = "Update responsible person")
    public ResponseEntity<SorumluDTO> updateSorumlu(@PathVariable Long id, @Valid @RequestBody SorumluDTO dto) {
        return ResponseEntity.ok(sorumluService.update(id, dto));
    }

    /**
     * DELETE /sorumlu/{id} - Sorumlu sil
     * Required Permission: responsible.delete
     */
    @DeleteMapping("/{id}")
    @RequirePermission(module = "responsible", action = "delete", description = "Delete responsible person")
    public ResponseEntity<Void> deleteSorumlu(@PathVariable Long id) {
        sorumluService.delete(id);
        return ResponseEntity.ok().build();
    }
}
