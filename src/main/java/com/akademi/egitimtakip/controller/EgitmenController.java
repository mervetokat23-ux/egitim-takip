package com.akademi.egitimtakip.controller;

import com.akademi.egitimtakip.annotation.RequirePermission;
import com.akademi.egitimtakip.dto.EgitmenDTO;
import com.akademi.egitimtakip.service.EgitmenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Egitmen Controller
 * 
 * Eğitmen CRUD endpoint'leri.
 * Permission-based authorization: trainer module
 */
@RestController
@RequestMapping("/egitmen")
@CrossOrigin(origins = "*")
public class EgitmenController {

    @Autowired
    private EgitmenService egitmenService;

    /**
     * GET /egitmen - Tüm eğitmenleri listele
     * Required Permission: trainer.view
     */
    @GetMapping
    @RequirePermission(module = "trainer", action = "view", description = "View all trainers")
    public ResponseEntity<List<EgitmenDTO>> getAllEgitmenler() {
        return ResponseEntity.ok(egitmenService.getAll());
    }

    /**
     * GET /egitmen/page - Sayfalama ile listele
     * Required Permission: trainer.view
     */
    @GetMapping("/page")
    @RequirePermission(module = "trainer", action = "view", description = "View trainers with pagination")
    public ResponseEntity<Page<EgitmenDTO>> getEgitmenlerPage(Pageable pageable) {
        return ResponseEntity.ok(egitmenService.getAll(pageable));
    }

    /**
     * GET /egitmen/{id} - ID ile eğitmen getir
     * Required Permission: trainer.view
     */
    @GetMapping("/{id}")
    @RequirePermission(module = "trainer", action = "view", description = "View trainer by ID")
    public ResponseEntity<EgitmenDTO> getEgitmenById(@PathVariable Long id) {
        return ResponseEntity.ok(egitmenService.getById(id));
    }

    /**
     * POST /egitmen - Yeni eğitmen oluştur
     * Required Permission: trainer.create
     */
    @PostMapping
    @RequirePermission(module = "trainer", action = "create", description = "Create new trainer")
    public ResponseEntity<EgitmenDTO> createEgitmen(@Valid @RequestBody EgitmenDTO dto) {
        return ResponseEntity.ok(egitmenService.create(dto));
    }

    /**
     * PUT /egitmen/{id} - Eğitmen güncelle
     * Required Permission: trainer.update
     */
    @PutMapping("/{id}")
    @RequirePermission(module = "trainer", action = "update", description = "Update trainer")
    public ResponseEntity<EgitmenDTO> updateEgitmen(@PathVariable Long id, @Valid @RequestBody EgitmenDTO dto) {
        return ResponseEntity.ok(egitmenService.update(id, dto));
    }

    /**
     * DELETE /egitmen/{id} - Eğitmen sil
     * Required Permission: trainer.delete
     */
    @DeleteMapping("/{id}")
    @RequirePermission(module = "trainer", action = "delete", description = "Delete trainer")
    public ResponseEntity<Void> deleteEgitmen(@PathVariable Long id) {
        egitmenService.delete(id);
        return ResponseEntity.ok().build();
    }
}
