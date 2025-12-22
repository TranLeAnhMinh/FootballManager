package com.example.footballmanagement.service.impl;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.footballmanagement.entity.Booking;
import com.example.footballmanagement.entity.Pitch;
import com.example.footballmanagement.service.EmailTemplateService;
import com.example.footballmanagement.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailTemplateServiceImpl implements EmailTemplateService {

    private final NotificationService notificationService;

    @Async  // ✅ gửi mail ở thread khác
    @Override
    public void sendMaintenanceRefundNotice(Booking booking, Pitch pitch, OffsetDateTime startAt, OffsetDateTime endAt, String reason) {
         log.info("📧 [ASYNC THREAD] Sending email for {} on thread: {}", 
             booking.getUser().getEmail(), 
             Thread.currentThread().getName());
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
            String subject = "Thông báo bảo trì sân bóng";

            String text = String.format("""
                Xin chào %s,

                Sân bạn đã đặt (%s) đang bị bảo trì trong khung giờ:
                - Từ: %s
                - Đến: %s

                Đơn đặt sân của bạn đã được chuyển sang trạng thái "Chờ hoàn tiền".
                Vui lòng phản hồi email này kèm thông tin tài khoản ngân hàng để chúng tôi tiến hành hoàn tiền sớm nhất.

                Lý do bảo trì: %s

                Trân trọng,
                Quản lý chi nhánh %s
                """,
                booking.getUser().getFullName(),
                pitch.getName(),
                startAt.format(fmt),
                endAt.format(fmt),
                reason,
                pitch.getBranch().getName()
            );

            notificationService.sendSimpleMessage(booking.getUser().getEmail(), subject, text);
            log.info("✅ Đã gửi mail cho {}", booking.getUser().getEmail());

        } catch (Exception e) {
            log.error("❌ Lỗi khi gửi mail async: {}", e.getMessage());
        }
    }
     @Async
    @Override
    public void sendWaitingRefundNotice(Booking booking, String reason) {
        try {
            String subject = "Thông báo hủy đặt sân - Chờ hoàn tiền";
            String text = String.format("""
                Xin chào %s,

                Rất tiếc, đơn đặt sân (%s) của bạn đã được hủy do lý do sau:
                "%s"

                Đơn hàng hiện đang ở trạng thái "Chờ hoàn tiền".
                Vui lòng phản hồi email này với thông tin tài khoản ngân hàng để chúng tôi xử lý hoàn tiền sớm nhất.

                Trân trọng,
                Quản lý chi nhánh %s
                """,
                    booking.getUser().getFullName(),
                    booking.getPitch().getName(),
                    reason != null ? reason : "(Không có lý do cụ thể)",
                    booking.getPitch().getBranch().getName()
            );

            notificationService.sendSimpleMessage(booking.getUser().getEmail(), subject, text);
            log.info("✅ Đã gửi mail chờ hoàn tiền cho {}", booking.getUser().getEmail());
        } catch (Exception e) {
            log.error("❌ Lỗi khi gửi mail WAITING_REFUND: {}", e.getMessage());
        }
    }

    // ==============================================================
    // ✅ 3. Xác nhận đã hoàn tiền
    // ==============================================================
    @Async
    @Override
    public void sendRefundedNotice(Booking booking, String note) {
        try {
            String subject = "Xác nhận hoàn tiền thành công";
            String text = String.format("""
                Xin chào %s,

                Đơn đặt sân (%s) của bạn đã được hoàn tiền thành công.

                Ghi chú từ quản lý: %s

                Trân trọng cảm ơn bạn đã thông cảm và tiếp tục ủng hộ hệ thống sân bóng %s.
                """,
                    booking.getUser().getFullName(),
                    booking.getPitch().getName(),
                    note != null ? note : "(Không có ghi chú)",
                    booking.getPitch().getBranch().getName()
            );

            notificationService.sendSimpleMessage(booking.getUser().getEmail(), subject, text);
            log.info("✅ Đã gửi mail REFUNDED cho {}", booking.getUser().getEmail());
        } catch (Exception e) {
            log.error("❌ Lỗi khi gửi mail REFUNDED: {}", e.getMessage());
        }
    }
}
