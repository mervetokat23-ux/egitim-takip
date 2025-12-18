package com.akademi.egitimtakip.controller;

import com.akademi.egitimtakip.annotation.RequirePermission;
import com.akademi.egitimtakip.dto.ProjeRequestDTO;
import com.akademi.egitimtakip.dto.ProjeResponseDTO;
import com.akademi.egitimtakip.service.ProjeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Proje Controller
 * 
 * Proje CRUD endpoint'leri.
 * Permission-based authorization: project module
 */
@RestController
@RequestMapping("/proje")
@CrossOrigin(origins = "*")
public class ProjeController {

    @Autowired
    private ProjeService projeService;

    /**
     * GET /proje - Tüm projeleri listele
     * Required Permission: project.view
     */
    @GetMapping
    @RequirePermission(module = "project", action = "view", description = "View all projects")
    public ResponseEntity<List<ProjeResponseDTO>> getAllProjeler() {
        return ResponseEntity.ok(projeService.getAll());
    }

    /**
     * GET /proje/page - Sayfalama ile listele
     * Required Permission: project.view
     */
    @GetMapping("/page")
    @RequirePermission(module = "project", action = "view", description = "View projects with pagination")
    public ResponseEntity<Page<ProjeResponseDTO>> getProjelerPage(Pageable pageable) {
        return ResponseEntity.ok(projeService.getAll(pageable));
    }

    /**
     * GET /proje/{id} - ID ile proje getir
     * Required Permission: project.view
     */
    @GetMapping("/{id}")
    @RequirePermission(module = "project", action = "view", description = "View project by ID")
    public ResponseEntity<ProjeResponseDTO> getProjeById(@PathVariable Long id) {
        return ResponseEntity.ok(projeService.getById(id));
    }

    /**
     * POST /proje - Yeni proje oluştur
     * Required Permission: project.create
     */
    @PostMapping
    @RequirePermission(module = "project", action = "create", description = "Create new project")
    public ResponseEntity<ProjeResponseDTO> createProje(@Valid @RequestBody ProjeRequestDTO dto) {
        return ResponseEntity.ok(projeService.create(dto));
    }

    /**
     * PUT /proje/{id} - Proje güncelle
     * Required Permission: project.update
     */
    @PutMapping("/{id}")
    @RequirePermission(module = "project", action = "update", description = "Update project")
    public ResponseEntity<ProjeResponseDTO> updateProje(@PathVariable Long id, @Valid @RequestBody ProjeRequestDTO dto) {
        return ResponseEntity.ok(projeService.update(id, dto));
    }

    /**
     * DELETE /proje/{id} - Proje sil
     * Required Permission: project.delete
     */
    @DeleteMapping("/{id}")
    @RequirePermission(module = "project", action = "delete", description = "Delete project")
    public ResponseEntity<Void> deleteProje(@PathVariable Long id) {
        projeService.delete(id);
        return ResponseEntity.ok().build();
    }
}
