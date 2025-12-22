package com.example.footballmanagement.service;

import java.time.OffsetDateTime;

import com.example.footballmanagement.entity.Booking;
import com.example.footballmanagement.entity.Pitch;

public interface EmailTemplateService {
    void sendMaintenanceRefundNotice(Booking booking, Pitch pitch, OffsetDateTime startAt, OffsetDateTime endAt, String reason);
     // ⚙️ Case 2: Hủy sân vì lý do khác (thời tiết, sự cố, v.v.)
    void sendWaitingRefundNotice(Booking booking, String reason);

    // ⚙️ Case 3: Xác nhận hoàn tiền thành công
    void sendRefundedNotice(Booking booking, String note);
}
