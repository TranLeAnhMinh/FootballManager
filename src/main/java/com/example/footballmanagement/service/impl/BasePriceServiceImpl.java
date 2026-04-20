package com.example.footballmanagement.service.impl;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.footballmanagement.dto.request.ApplyBasePriceTemplateRequest;
import com.example.footballmanagement.dto.request.UpdateBasePriceCellRequest;
import com.example.footballmanagement.dto.response.ApplyBasePriceTemplateResponse;
import com.example.footballmanagement.dto.response.BasePriceGridCellResponse;
import com.example.footballmanagement.dto.response.BasePriceGridRowResponse;
import com.example.footballmanagement.dto.response.BasePriceWeeklyGridResponse;
import com.example.footballmanagement.dto.response.UpdateBasePriceCellResponse;
import com.example.footballmanagement.entity.BasePrice;
import com.example.footballmanagement.entity.Pitch;
import com.example.footballmanagement.repository.BasePriceRepository;
import com.example.footballmanagement.repository.PitchRepository;
import com.example.footballmanagement.service.BasePriceService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasePriceServiceImpl implements BasePriceService {

    private static final int SLOT_MINUTES = 45;
    private static final int MINUTES_PER_DAY = 24 * 60;
    private static final int DAYS_PER_WEEK = 7;

    private final BasePriceRepository basePriceRepository;
    private final PitchRepository pitchRepository;

    @Override
    @Transactional
    public ApplyBasePriceTemplateResponse applyBasePriceTemplate(ApplyBasePriceTemplateRequest request) {
        validate(request);

        int inserted = 0;
        int updated = 0;

        for (UUID pitchId : request.getPitchIds()) {
            Pitch pitch = pitchRepository.findById(pitchId)
                    .orElseThrow(() -> new RuntimeException("Pitch not found: " + pitchId));

            for (Short dayOfWeek : request.getDayOfWeeks()) {
                int startMinutes = toMinutes(request.getStartTime());
                int endMinutes = toMinutes(request.getEndTime());

                if (endMinutes == 0) {
                    endMinutes = MINUTES_PER_DAY;
                }

                for (int current = startMinutes; current < endMinutes; current += SLOT_MINUTES) {
                    int next = current + SLOT_MINUTES;

                    LocalTime blockStart = minutesToLocalTime(current);
                    LocalTime blockEnd = (next == MINUTES_PER_DAY)
                            ? LocalTime.MIDNIGHT
                            : minutesToLocalTime(next);

                    BasePrice existing = basePriceRepository
                            .findByPitch_IdAndDayOfWeekAndTimeStartAndTimeEnd(
                                    pitchId,
                                    dayOfWeek,
                                    blockStart,
                                    blockEnd
                            )
                            .orElse(null);

                    if (existing != null) {
                        existing.setPrice(request.getPrice());
                        updated++;
                        basePriceRepository.save(existing);
                    } else {
                        BasePrice basePrice = BasePrice.builder()
                                .pitch(pitch)
                                .dayOfWeek(dayOfWeek)
                                .timeStart(blockStart)
                                .timeEnd(blockEnd)
                                .price(request.getPrice())
                                .build();

                        inserted++;
                        basePriceRepository.save(basePrice);
                    }
                }
            }
        }

        return new ApplyBasePriceTemplateResponse(inserted, updated);
    }

    @Override
    @Transactional(readOnly = true)
    public BasePriceWeeklyGridResponse getWeeklyGrid(UUID pitchId) {
        Pitch pitch = pitchRepository.findById(pitchId)
                .orElseThrow(() -> new RuntimeException("Pitch not found: " + pitchId));

        List<BasePrice> basePrices = basePriceRepository.findByPitch_IdOrderByDayOfWeekAscTimeStartAsc(pitchId);

        List<BasePriceGridRowResponse> rows = new ArrayList<>();

        for (int current = 0; current < MINUTES_PER_DAY; current += SLOT_MINUTES) {
            int next = current + SLOT_MINUTES;

            LocalTime rowStart = minutesToLocalTime(current);
            LocalTime rowEnd = (next == MINUTES_PER_DAY)
                    ? LocalTime.MIDNIGHT
                    : minutesToLocalTime(next);

            List<BasePriceGridCellResponse> cells = new ArrayList<>();

            for (short dayOfWeek = 1; dayOfWeek <= DAYS_PER_WEEK; dayOfWeek++) {
                final short currentDayOfWeek = dayOfWeek;

                BasePrice matched = basePrices.stream()
                        .filter(bp -> bp.getDayOfWeek() == currentDayOfWeek
                                && bp.getTimeStart().equals(rowStart)
                                && bp.getTimeEnd().equals(rowEnd))
                        .findFirst()
                        .orElse(null);

                if (matched != null) {
                    cells.add(BasePriceGridCellResponse.builder()
                            .basePriceId(matched.getId())
                            .dayOfWeek(currentDayOfWeek)
                            .price(matched.getPrice())
                            .configured(true)
                            .build());
                } else {
                    cells.add(BasePriceGridCellResponse.builder()
                            .basePriceId(null)
                            .dayOfWeek(currentDayOfWeek)
                            .price(null)
                            .configured(false)
                            .build());
                }
            }

            rows.add(BasePriceGridRowResponse.builder()
                    .timeStart(rowStart)
                    .timeEnd(rowEnd)
                    .cells(cells)
                    .build());
        }

        return BasePriceWeeklyGridResponse.builder()
                .pitchId(pitch.getId())
                .pitchName(pitch.getName())
                .rows(rows)
                .build();
    }

    @Override
    @Transactional
    public UpdateBasePriceCellResponse updateBasePriceCell(UpdateBasePriceCellRequest request) {
        validateUpdateCellRequest(request);

        Pitch pitch = pitchRepository.findById(request.getPitchId())
                .orElseThrow(() -> new RuntimeException("Pitch not found: " + request.getPitchId()));

        BasePrice basePrice = basePriceRepository
                .findByPitch_IdAndDayOfWeekAndTimeStartAndTimeEnd(
                        request.getPitchId(),
                        request.getDayOfWeek(),
                        request.getTimeStart(),
                        request.getTimeEnd()
                )
                .orElse(null);

        if (basePrice != null) {
            basePrice.setPrice(request.getPrice());
            basePriceRepository.save(basePrice);

            return UpdateBasePriceCellResponse.builder()
                    .basePriceId(basePrice.getId())
                    .pitchId(pitch.getId())
                    .dayOfWeek(basePrice.getDayOfWeek())
                    .timeStart(basePrice.getTimeStart())
                    .timeEnd(basePrice.getTimeEnd())
                    .price(basePrice.getPrice())
                    .message("Update base price successfully")
                    .build();
        }

        BasePrice newBasePrice = BasePrice.builder()
                .pitch(pitch)
                .dayOfWeek(request.getDayOfWeek())
                .timeStart(request.getTimeStart())
                .timeEnd(request.getTimeEnd())
                .price(request.getPrice())
                .build();

        BasePrice saved = basePriceRepository.save(newBasePrice);

        return UpdateBasePriceCellResponse.builder()
                .basePriceId(saved.getId())
                .pitchId(pitch.getId())
                .dayOfWeek(saved.getDayOfWeek())
                .timeStart(saved.getTimeStart())
                .timeEnd(saved.getTimeEnd())
                .price(saved.getPrice())
                .message("Create base price successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPitchPriceConfigComplete(UUID pitchId) {
        if (pitchId == null) {
            throw new RuntimeException("pitchId không được để trống");
        }

        long configuredCount = basePriceRepository.countByPitch_Id(pitchId);

        return configuredCount == 224;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<UUID> getFullyConfiguredPitchIds(List<UUID> pitchIds) {
        if (pitchIds == null || pitchIds.isEmpty()) {
            return Set.of();
        }

        return new HashSet<>(basePriceRepository.findFullyConfiguredPitchIds(pitchIds));
    }

    private void validate(ApplyBasePriceTemplateRequest request) {
        int startMinutes = toMinutes(request.getStartTime());
        int endMinutes = toMinutes(request.getEndTime());

        if (!isValid45(startMinutes)) {
            throw new RuntimeException("startTime không đúng mốc 45 phút");
        }

        if (!(endMinutes == 0 || isValid45(endMinutes))) {
            throw new RuntimeException("endTime không đúng mốc 45 phút");
        }

        int normalizedEnd = (endMinutes == 0) ? MINUTES_PER_DAY : endMinutes;

        if (normalizedEnd <= startMinutes) {
            throw new RuntimeException("endTime phải lớn hơn startTime");
        }

        if ((normalizedEnd - startMinutes) % SLOT_MINUTES != 0) {
            throw new RuntimeException("Khoảng thời gian phải chia hết cho 45 phút");
        }

        if (request.getPitchIds() == null || request.getPitchIds().isEmpty()) {
            throw new RuntimeException("Danh sách pitch không được rỗng");
        }

        if (request.getDayOfWeeks() == null || request.getDayOfWeeks().isEmpty()) {
            throw new RuntimeException("Danh sách dayOfWeek không được rỗng");
        }

        for (Short day : request.getDayOfWeeks()) {
            if (day == null || day < 1 || day > 7) {
                throw new RuntimeException("dayOfWeek phải từ 1 đến 7");
            }
        }
    }

    private void validateUpdateCellRequest(UpdateBasePriceCellRequest request) {
        if (request.getPitchId() == null) {
            throw new RuntimeException("pitchId không được để trống");
        }

        if (request.getDayOfWeek() == null || request.getDayOfWeek() < 1 || request.getDayOfWeek() > 7) {
            throw new RuntimeException("dayOfWeek phải từ 1 đến 7");
        }

        if (request.getTimeStart() == null || request.getTimeEnd() == null) {
            throw new RuntimeException("timeStart/timeEnd không được để trống");
        }

        if (request.getPrice() == null || request.getPrice().signum() <= 0) {
            throw new RuntimeException("price phải lớn hơn 0");
        }

        int startMinutes = toMinutes(request.getTimeStart());
        int endMinutes = toMinutes(request.getTimeEnd());

        if (!isValid45(startMinutes)) {
            throw new RuntimeException("timeStart không đúng mốc 45 phút");
        }

        if (!(endMinutes == 0 || isValid45(endMinutes))) {
            throw new RuntimeException("timeEnd không đúng mốc 45 phút");
        }

        int normalizedEnd = (endMinutes == 0) ? MINUTES_PER_DAY : endMinutes;

        if (normalizedEnd <= startMinutes) {
            throw new RuntimeException("timeEnd phải lớn hơn timeStart");
        }

        if ((normalizedEnd - startMinutes) != SLOT_MINUTES) {
            throw new RuntimeException("Mỗi cell chỉ được đúng 1 block 45 phút");
        }
    }

    private int toMinutes(LocalTime time) {
        return time.getHour() * 60 + time.getMinute();
    }

    private boolean isValid45(int minutes) {
        return minutes % SLOT_MINUTES == 0;
    }

    private LocalTime minutesToLocalTime(int minutes) {
        int normalized = minutes % MINUTES_PER_DAY;
        int hour = normalized / 60;
        int minute = normalized % 60;
        return LocalTime.of(hour, minute);
    }
}