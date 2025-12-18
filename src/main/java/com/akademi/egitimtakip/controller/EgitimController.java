package com.akademi.egitimtakip.controller;

import com.akademi.egitimtakip.annotation.RequirePermission;
import com.akademi.egitimtakip.dto.EgitimRequestDTO;
import com.akademi.egitimtakip.dto.EgitimResponseDTO;
import com.akademi.egitimtakip.service.EgitimService;
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
 * Egitim Controller
 * 
 * Eğitim CRUD endpoint'lerini sağlar.
 * JWT authentication ile korunur, sayfalama ve filtreleme desteği vardır.
 * Permission-based authorization: education module
 */
@RestController
@RequestMapping("/egitim")
@CrossOrigin(origins = "*")
public class EgitimController {

    @Autowired
    private EgitimService egitimService;

    /**
     * GET /egitim
     * Tüm eğitimleri sayfalama ve filtreleme ile getirir
     * Required Permission: education.view
     */
    @GetMapping
    @RequirePermission(module = "education", action = "view", description = "View all educations")
    public ResponseEntity<Page<EgitimResponseDTO>> getAllEgitimler(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort,
            @RequestParam(required = false) String il,
            @RequestParam(required = false) Integer yil,
            @RequestParam(required = false) String durum) {
        
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc") 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;
        String sortField = sortParams[0];
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        
        Page<EgitimResponseDTO> egitimler = egitimService.getAllEgitimler(pageable, il, yil, durum);
        return ResponseEntity.ok(egitimler);
    }

    /**
     * GET /egitim/{id}
     * ID ile eğitim detayını getirir
     * Required Permission: education.view
     */
    @GetMapping("/{id}")
    @RequirePermission(module = "education", action = "view", description = "View education by ID")
    public ResponseEntity<EgitimResponseDTO> getEgitimById(@PathVariable Long id) {
        try {
            EgitimResponseDTO egitim = egitimService.getEgitimById(id);
            return ResponseEntity.ok(egitim);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * POST /egitim
     * Yeni eğitim oluşturur
     * Required Permission: education.create
     */
    @PostMapping
    @RequirePermission(module = "education", action = "create", description = "Create new education")
    public ResponseEntity<EgitimResponseDTO> createEgitim(@Valid @RequestBody EgitimRequestDTO requestDTO) {
        try {
            EgitimResponseDTO response = egitimService.createEgitim(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * PUT /egitim/{id}
     * Mevcut eğitimi günceller
     * Required Permission: education.update
     */
    @PutMapping("/{id}")
    @RequirePermission(module = "education", action = "update", description = "Update existing education")
    public ResponseEntity<EgitimResponseDTO> updateEgitim(
            @PathVariable Long id,
            @Valid @RequestBody EgitimRequestDTO requestDTO) {
        try {
            EgitimResponseDTO response = egitimService.updateEgitim(id, requestDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * DELETE /egitim/{id}
     * Eğitimi siler
     * Required Permission: education.delete
     */
    @DeleteMapping("/{id}")
    @RequirePermission(module = "education", action = "delete", description = "Delete education")
    public ResponseEntity<Void> deleteEgitim(@PathVariable Long id) {
        try {
            egitimService.deleteEgitim(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
