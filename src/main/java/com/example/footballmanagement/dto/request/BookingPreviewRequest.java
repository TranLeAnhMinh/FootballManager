package com.example.footballmanagement.dto.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingPreviewRequest {
    @NotNull
    private UUID pitchId;

    @NotNull
    private List<BookingSlotRequest> slots;

    private String voucherCode;
}
