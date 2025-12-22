package com.example.footballmanagement.dto.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequest {

    @NotNull
    private UUID pitchId;

    @Size(max = 500)
    private String note;

    @NotNull
    private List<BookingSlotRequest> slots;

    private String voucherCode;
}
