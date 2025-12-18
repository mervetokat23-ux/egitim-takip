package com.akademi.egitimtakip.controller;

import com.akademi.egitimtakip.annotation.RequirePermission;
import com.akademi.egitimtakip.dto.KategoriDTO;
import com.akademi.egitimtakip.service.KategoriService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Kategori Controller
 * 
 * Kategori CRUD endpoint'leri.
 * Permission-based authorization: category module
 */
@RestController
@RequestMapping("/kategori")
@CrossOrigin(origins = "*")
public class KategoriController {

    @Autowired
    private KategoriService kategoriService;

    /**
     * GET /kategori - Tüm kategorileri listele
     * Required Permission: category.view
     */
    @GetMapping
    @RequirePermission(module = "category", action = "view", description = "View all categories")
    public ResponseEntity<List<KategoriDTO>> getAllKategoriler() {
        return ResponseEntity.ok(kategoriService.getAll());
    }

    /**
     * GET /kategori/page - Sayfalama ile listele
     * Required Permission: category.view
     */
    @GetMapping("/page")
    @RequirePermission(module = "category", action = "view", description = "View categories with pagination")
    public ResponseEntity<Page<KategoriDTO>> getKategorilerPage(Pageable pageable) {
        return ResponseEntity.ok(kategoriService.getAll(pageable));
    }

    /**
     * GET /kategori/{id} - ID ile kategori getir
     * Required Permission: category.view
     */
    @GetMapping("/{id}")
    @RequirePermission(module = "category", action = "view", description = "View category by ID")
    public ResponseEntity<KategoriDTO> getKategoriById(@PathVariable Long id) {
        return ResponseEntity.ok(kategoriService.getById(id));
    }

    /**
     * POST /kategori - Yeni kategori oluştur
     * Required Permission: category.create
     */
    @PostMapping
    @RequirePermission(module = "category", action = "create", description = "Create new category")
    public ResponseEntity<KategoriDTO> createKategori(@Valid @RequestBody KategoriDTO dto) {
        return ResponseEntity.ok(kategoriService.create(dto));
    }

    /**
     * PUT /kategori/{id} - Kategori güncelle
     * Required Permission: category.update
     */
    @PutMapping("/{id}")
    @RequirePermission(module = "category", action = "update", description = "Update category")
    public ResponseEntity<KategoriDTO> updateKategori(@PathVariable Long id, @Valid @RequestBody KategoriDTO dto) {
        return ResponseEntity.ok(kategoriService.update(id, dto));
    }

    /**
     * DELETE /kategori/{id} - Kategori sil
     * Required Permission: category.delete
     */
    @DeleteMapping("/{id}")
    @RequirePermission(module = "category", action = "delete", description = "Delete category")
    public ResponseEntity<Void> deleteKategori(@PathVariable Long id) {
        kategoriService.delete(id);
        return ResponseEntity.ok().build();
    }
}
