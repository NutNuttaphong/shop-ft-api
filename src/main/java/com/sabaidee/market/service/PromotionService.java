package com.sabaidee.market.service;

import com.sabaidee.market.dto.request.PromotionRequest;
import com.sabaidee.market.exception.DuplicateResourceException;
import com.sabaidee.market.exception.ResourceNotFoundException;
import com.sabaidee.market.model.Promotion;
import com.sabaidee.market.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;

    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    public List<Promotion> getActivePromotions() {
        return promotionRepository.findByIsActiveTrue();
    }

    public Promotion getPromotionById(String id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบโปรโมชันรหัส: " + id));
    }

    public Promotion validatePromoCode(String code) {
        Promotion promotion = promotionRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบรหัสโปรโมชัน: " + code));

        if (!promotion.isActive()) {
            throw new IllegalStateException("โปรโมชันนี้หมดอายุหรือไม่ได้เปิดใช้งาน");
        }

        return promotion;
    }

    public Promotion createPromotion(PromotionRequest request) {
        if (promotionRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new DuplicateResourceException("รหัสโปรโมชัน \"" + request.getCode() + "\" ถูกใช้งานแล้ว");
        }

        Promotion promotion = Promotion.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .minPurchase(request.getMinPurchase())
                .isActive(request.isActive())
                .imageUrl(request.getImageUrl())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        log.info("สร้างโปรโมชันใหม่: {}", promotion.getName());
        return promotionRepository.save(promotion);
    }

    public Promotion updatePromotion(String id, PromotionRequest request) {
        Promotion promotion = getPromotionById(id);

        promotion.setCode(request.getCode());
        promotion.setName(request.getName());
        promotion.setDescription(request.getDescription());
        promotion.setDiscountType(request.getDiscountType());
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setMinPurchase(request.getMinPurchase());
        promotion.setActive(request.isActive());
        promotion.setImageUrl(request.getImageUrl());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());

        log.info("อัพเดทโปรโมชัน: {}", promotion.getName());
        return promotionRepository.save(promotion);
    }

    public void deletePromotion(String id) {
        Promotion promotion = getPromotionById(id);
        promotionRepository.delete(promotion);
        log.info("ลบโปรโมชัน: {}", promotion.getName());
    }
}
