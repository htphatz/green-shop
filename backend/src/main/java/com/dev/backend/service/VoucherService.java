package com.dev.backend.service;

import com.dev.backend.dto.request.VoucherReq;
import com.dev.backend.dto.response.PageDto;
import com.dev.backend.dto.response.VoucherRes;

public interface VoucherService {
    VoucherRes createVoucher(VoucherReq request);
    VoucherRes findByCode(String code);
    PageDto<VoucherRes> findAll(Integer pageNumber, Integer pageSize);
    VoucherRes updateVoucher(String code, VoucherReq request);
    void deleteByCode(String code);
}
