package com.example.footballmanagement.service.impl;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.footballmanagement.dto.pricing.PriceRuleCondition;
import com.example.footballmanagement.dto.pricing.PriceRuleEffect;
import com.example.footballmanagement.dto.request.BookingSlotRequest;
import com.example.footballmanagement.dto.response.BookingPriceResponse;
import com.example.footballmanagement.entity.BasePrice;
import com.example.footballmanagement.entity.PriceRule;
import com.example.footballmanagement.entity.Voucher;
import com.example.footballmanagement.exception.ErrorCode;
import com.example.footballmanagement.exception.custom.VoucherException;
import com.example.footballmanagement.repository.BasePriceRepository;
import com.example.footballmanagement.repository.PriceRuleRepository;
import com.example.footballmanagement.service.PricingService;
import com.example.footballmanagement.service.VoucherService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PricingServiceImpl implements PricingService {

    private final BasePriceRepository basePriceRepo;
    private final PriceRuleRepository priceRuleRepo;
    private final VoucherService voucherService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional(readOnly = true)
    public BookingPriceResponse calculatePrice(UUID pitchId,
                                               List<BookingSlotRequest> slots,
                                               String voucherCode,
                                               UUID userId) {

        // 1. Tính giá gốc
        BigDecimal basePrice = calculateBasePrice(pitchId, slots);

        // 2. Áp rule ưu tiên cao nhất
        BigDecimal adjustedPrice = applyPriceRules(pitchId, slots, basePrice);

        // 3. Áp voucher (nếu có)
        BigDecimal discount = BigDecimal.ZERO;
        if (voucherCode != null && !voucherCode.isBlank()) {
            Voucher voucher = voucherService.findActiveByCode(voucherCode)
                    .orElseThrow(() -> new VoucherException(ErrorCode.VOUCHER_NOT_FOUND));
            
            OffsetDateTime bookingDate = slots.get(0).getStartAt();

            voucherService.validateVoucher(voucher, userId, adjustedPrice, bookingDate);

            if ("PERCENT".equalsIgnoreCase(voucher.getType().name())) {
                discount = adjustedPrice.multiply(voucher.getValue().divide(BigDecimal.valueOf(100)));
                if (voucher.getMaxDiscount() != null) {
                    discount = discount.min(voucher.getMaxDiscount());
                }
            } else { // FIXED
                discount = voucher.getValue();
            }

            if (discount.compareTo(adjustedPrice) > 0) {
                discount = adjustedPrice; // không cho âm
            }
        }

        BigDecimal finalPrice = adjustedPrice.subtract(discount);

        return BookingPriceResponse.builder()
                .basePrice(basePrice)
                .voucherDiscount(discount)
                .finalPrice(finalPrice)
                .currency("VND")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateBasePrice(UUID pitchId, List<BookingSlotRequest> slots) {
        BigDecimal total = BigDecimal.ZERO;

        for (BookingSlotRequest slot : slots) {
            DayOfWeek dow = slot.getStartAt().getDayOfWeek();
            short dayOfWeek = (short) dow.getValue(); // 1=Mon ... 7=Sun

            LocalTime start = slot.getStartAt().toLocalTime();
            LocalTime end = slot.getEndAt().toLocalTime();

            // ✅ chỉ lấy đúng block 45 phút cấu hình trong DB
            List<BasePrice> basePrices = basePriceRepo
                    .findByPitch_IdAndDayOfWeekAndTimeStartGreaterThanEqualAndTimeEndLessThan(
                            pitchId, dayOfWeek, start, end);

            if (basePrices.isEmpty()) {
                throw new IllegalStateException("No base price configured for pitch/time");
            }

            for (BasePrice bp : basePrices) {
                total = total.add(bp.getPrice());
            }
        }

        return total;
    }

    private BigDecimal applyPriceRules(UUID pitchId, List<BookingSlotRequest> slots, BigDecimal basePrice) {
        if (slots.isEmpty()) return basePrice;

        LocalDate date = slots.get(0).getStartAt().toLocalDate();
        List<PriceRule> rules = priceRuleRepo.findValidRules(pitchId, date);

        if (rules.isEmpty()) return basePrice;

        // ✅ Chỉ lấy rule có priority cao nhất
        rules.sort((a, b) -> Integer.compare(a.getPriority(), b.getPriority()));
        PriceRule rule = rules.get(0);

        try {
            PriceRuleCondition condition = objectMapper.readValue(rule.getConditions(), PriceRuleCondition.class);
            PriceRuleEffect effect = objectMapper.readValue(rule.getEffect(), PriceRuleEffect.class);

            boolean applies = slots.stream().anyMatch(slot -> {
                int dow = slot.getStartAt().getDayOfWeek().getValue();
                int startHour = slot.getStartAt().getHour();
                int endHour = slot.getEndAt().getHour();

                boolean matchDow = (condition.getDaysOfWeek() == null || condition.getDaysOfWeek().contains(dow));
                boolean matchHour = (condition.getStartHour() == null || endHour > condition.getStartHour())
                        && (condition.getEndHour() == null || startHour < condition.getEndHour());

                return matchDow && matchHour;
            });

            if (applies) {
                if ("percent".equalsIgnoreCase(effect.getType())) {
                    basePrice = basePrice.subtract(basePrice.multiply(effect.getValue().divide(BigDecimal.valueOf(100))));
                } else if ("fixed".equalsIgnoreCase(effect.getType())) {
                    basePrice = basePrice.subtract(effect.getValue());
                }
            }

        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Invalid price rule JSON for rule id=" + rule.getId(), e);
        }

        if (basePrice.compareTo(BigDecimal.ZERO) < 0) {
            basePrice = BigDecimal.ZERO;
        }

        return basePrice;
    }
}
