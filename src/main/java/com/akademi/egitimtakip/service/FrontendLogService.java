package com.akademi.egitimtakip.service;

import com.akademi.egitimtakip.entity.FrontendLog;
import com.akademi.egitimtakip.repository.FrontendLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * FrontendLogService
 * 
 * Frontend kullanıcı aksiyonlarını yöneten servis.
 * - Frontend loglarını kaydetme
 * - Filtreleme ve sorgulama
 * - Eski logları silme
 */
@Service
public class FrontendLogService {

    @Autowired
    private FrontendLogRepository frontendLogRepository;

    /**
     * Frontend log kaydeder (asenkron)
     */
    @Async
    @Transactional
    public void saveFrontendLog(Long userId, String action, String page, String details) {
        FrontendLog log = new FrontendLog();
        log.setUserId(userId);
        log.setAction(action);
        log.setPage(page);
        log.setDetails(details);
        frontendLogRepository.save(log);
    }

    /**
     * Filtreli frontend loglarını getirir
     */
    @Transactional(readOnly = true)
    public Page<FrontendLog> getLogsByFilters(
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String action,
            String page,
            Pageable pageable) {

        Specification<FrontendLog> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (userId != null) {
                predicates.add(cb.equal(root.get("userId"), userId));
            }

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }

            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }

            if (action != null && !action.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("action")), "%" + action.toLowerCase() + "%"));
            }

            if (page != null && !page.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("page")), "%" + page.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return frontendLogRepository.findAll(spec, pageable);
    }

    /**
     * Tüm frontend loglarını getirir
     */
    @Transactional(readOnly = true)
    public Page<FrontendLog> getAllLogs(Pageable pageable) {
        return frontendLogRepository.findAll(pageable);
    }

    /**
     * Belirtilen tarihten eski logları siler
     */
    @Transactional
    public void deleteOldLogs(LocalDateTime beforeDate) {
        List<FrontendLog> oldLogs = frontendLogRepository.findByCreatedAtBefore(beforeDate);
        frontendLogRepository.deleteAll(oldLogs);
    }

    /**
     * Belirli bir kullanıcının loglarını getirir
     */
    @Transactional(readOnly = true)
    public Page<FrontendLog> getLogsByUserId(Long userId, Pageable pageable) {
        return frontendLogRepository.findByUserId(userId, pageable);
    }

    /**
     * Belirli bir sayfa için logları getirir
     */
    @Transactional(readOnly = true)
    public Page<FrontendLog> getLogsByPage(String page, Pageable pageable) {
        return frontendLogRepository.findByPage(page, pageable);
    }

    /**
     * Belirli bir aksiyon türü için logları getirir
     */
    @Transactional(readOnly = true)
    public Page<FrontendLog> getLogsByAction(String action, Pageable pageable) {
        return frontendLogRepository.findByAction(action, pageable);
    }
}
