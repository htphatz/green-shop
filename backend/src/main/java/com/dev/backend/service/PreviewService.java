package com.dev.backend.service;

import com.dev.backend.dto.request.PreviewOrderItemReq;
import com.dev.backend.dto.request.PreviewOrderReq;
import com.dev.backend.dto.response.PreviewOrderItemRes;
import com.dev.backend.dto.response.PreviewOrderRes;

public interface PreviewService {
    PreviewOrderItemRes previewOrderItem(PreviewOrderItemReq request);
    PreviewOrderRes previewOrder(PreviewOrderReq request);
}
