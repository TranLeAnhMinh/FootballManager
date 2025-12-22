        package com.example.footballmanagement.service.impl;

        import java.util.List;
        import java.util.UUID;

        import org.springframework.data.domain.Page;
        import org.springframework.data.domain.Pageable;
        import org.springframework.stereotype.Service;
        import org.springframework.transaction.annotation.Transactional;

        import com.example.footballmanagement.dto.request.BookingRequest;
        import com.example.footballmanagement.dto.response.BookingHistoryResponse;
        import com.example.footballmanagement.dto.response.BookingPriceResponse;
        import com.example.footballmanagement.dto.response.BookingResponse;
        import com.example.footballmanagement.entity.Booking;
        import com.example.footballmanagement.entity.BookingSlot;
        import com.example.footballmanagement.entity.Pitch;
        import com.example.footballmanagement.entity.User;
        import com.example.footballmanagement.entity.enums.BookingStatus;
        import com.example.footballmanagement.repository.BookingRepository;
        import com.example.footballmanagement.repository.UserRepository;
        import com.example.footballmanagement.service.BookingService;
        import com.example.footballmanagement.service.BookingSlotService;
        import com.example.footballmanagement.service.PitchService;
        import com.example.footballmanagement.service.PricingService;
        import com.example.footballmanagement.utils.ConverterUtil;

        import lombok.RequiredArgsConstructor;

        @Service
        @RequiredArgsConstructor
        public class BookingServiceImpl implements BookingService {

        private final BookingRepository bookingRepo;
        private final BookingSlotService slotService;
        private final PitchService pitchService;
        private final PricingService pricingService;
        private final UserRepository userRepo;

        @Override
        @Transactional
        public BookingResponse createBooking(BookingRequest request, UUID userId) {
                // ✅ lấy user từ DB
                User user = userRepo.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));

                // 1. Lấy pitch từ service
                Pitch pitch = pitchService.getPitchEntity(request.getPitchId());

                // 2. Tạo booking (chưa set finalPrice ở đây)
                Booking booking = Booking.builder()
                        .user(user)
                        .pitch(pitch)
                        .branch(pitch.getBranch())
                        .note(request.getNote())
                        .status(BookingStatus.PENDING)
                        .build();

                // 3. Tạo các booking slots
                List<BookingSlot> slots = slotService.createSlots(request.getSlots(), booking);
                booking.setSlots(slots);

                // 4. Tính giá + voucher (nếu có)
                var pricing = pricingService.calculatePrice(
                        pitch.getId(),
                        request.getSlots(),
                        request.getVoucherCode(),
                        user.getId()
                );

                // ⚡ [SỬA] Lưu finalPrice trực tiếp vào DB
                booking.setFinalPrice(pricing.getFinalPrice());

                // 5. Lưu DB
                bookingRepo.save(booking);

                // 6. Trả về DTO
                return ConverterUtil.toBookingResponse(booking, pricing);
        }

        @Override
        @Transactional(readOnly = true)
        public BookingResponse getBookingResponseById(UUID bookingId) {
                Booking booking = bookingRepo.findById(bookingId)
                        .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

                // ⚡ [SỬA] Không cần tính lại bằng pricingService nữa,
                // chỉ wrap finalPrice đã lưu vào Booking thành BookingPriceResponse
                var pricing = BookingPriceResponse.builder()
                        .basePrice(null) // nếu muốn show thêm thì lưu cả basePrice lúc createBooking
                        .voucherDiscount(null)
                        .finalPrice(booking.getFinalPrice()) // lấy trực tiếp từ DB
                        .currency("VND")
                        .build();

                return ConverterUtil.toBookingResponse(booking, pricing);
        }

        @Override
        public Page<BookingHistoryResponse> getBookingHistory(UUID userId, Pageable pageable) {
                Page<Booking> bookingsPage = bookingRepo.findBookingsWithSlotsByUser(userId, pageable);

        // dùng map() của Page để convert từng phần tử
        return bookingsPage.map(ConverterUtil::toBookingHistoryResponse);
        }

        }

