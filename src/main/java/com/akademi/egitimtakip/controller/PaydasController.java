package com.akademi.egitimtakip.controller;

import com.akademi.egitimtakip.annotation.RequirePermission;
import com.akademi.egitimtakip.dto.PaydasDTO;
import com.akademi.egitimtakip.service.PaydasService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Paydas Controller
 * 
 * Paydaş CRUD endpoint'leri.
 * Permission-based authorization: stakeholder module
 */
@RestController
@RequestMapping("/paydas")
@CrossOrigin(origins = "*")
public class PaydasController {

    @Autowired
    private PaydasService paydasService;

    /**
     * GET /paydas - Tüm paydaşları listele
     * Required Permission: stakeholder.view
     */
    @GetMapping
    @RequirePermission(module = "stakeholder", action = "view", description = "View all stakeholders")
    public ResponseEntity<List<PaydasDTO>> getAllPaydaslar() {
        return ResponseEntity.ok(paydasService.getAll());
    }

    /**
     * GET /paydas/page - Sayfalama ile listele
     * Required Permission: stakeholder.view
     */
    @GetMapping("/page")
    @RequirePermission(module = "stakeholder", action = "view", description = "View stakeholders with pagination")
    public ResponseEntity<Page<PaydasDTO>> getPaydaslarPage(Pageable pageable) {
        return ResponseEntity.ok(paydasService.getAll(pageable));
    }

    /**
     * GET /paydas/{id} - ID ile paydaş getir
     * Required Permission: stakeholder.view
     */
    @GetMapping("/{id}")
    @RequirePermission(module = "stakeholder", action = "view", description = "View stakeholder by ID")
    public ResponseEntity<PaydasDTO> getPaydasById(@PathVariable Long id) {
        return ResponseEntity.ok(paydasService.getById(id));
    }

    /**
     * POST /paydas - Yeni paydaş oluştur
     * Required Permission: stakeholder.create
     */
    @PostMapping
    @RequirePermission(module = "stakeholder", action = "create", description = "Create new stakeholder")
    public ResponseEntity<PaydasDTO> createPaydas(@Valid @RequestBody PaydasDTO dto) {
        return ResponseEntity.ok(paydasService.create(dto));
    }

    /**
     * PUT /paydas/{id} - Paydaş güncelle
     * Required Permission: stakeholder.update
     */
    @PutMapping("/{id}")
    @RequirePermission(module = "stakeholder", action = "update", description = "Update stakeholder")
    public ResponseEntity<PaydasDTO> updatePaydas(@PathVariable Long id, @Valid @RequestBody PaydasDTO dto) {
        return ResponseEntity.ok(paydasService.update(id, dto));
    }

    /**
     * DELETE /paydas/{id} - Paydaş sil
     * Required Permission: stakeholder.delete
     */
    @DeleteMapping("/{id}")
    @RequirePermission(module = "stakeholder", action = "delete", description = "Delete stakeholder")
    public ResponseEntity<Void> deletePaydas(@PathVariable Long id) {
        paydasService.delete(id);
        return ResponseEntity.ok().build();
    }
}
