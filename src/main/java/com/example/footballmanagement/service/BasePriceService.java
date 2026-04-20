package com.example.footballmanagement.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.example.footballmanagement.dto.request.ApplyBasePriceTemplateRequest;
import com.example.footballmanagement.dto.request.UpdateBasePriceCellRequest;
import com.example.footballmanagement.dto.response.ApplyBasePriceTemplateResponse;
import com.example.footballmanagement.dto.response.BasePriceWeeklyGridResponse;
import com.example.footballmanagement.dto.response.UpdateBasePriceCellResponse;

public interface BasePriceService {

    ApplyBasePriceTemplateResponse applyBasePriceTemplate(ApplyBasePriceTemplateRequest request);

    BasePriceWeeklyGridResponse getWeeklyGrid(UUID pitchId);

    UpdateBasePriceCellResponse updateBasePriceCell(UpdateBasePriceCellRequest request);

    boolean isPitchPriceConfigComplete(UUID pitchId);

    Set<UUID> getFullyConfiguredPitchIds(List<UUID> pitchIds);
}