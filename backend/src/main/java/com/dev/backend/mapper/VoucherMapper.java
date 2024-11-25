package com.dev.backend.mapper;

import com.dev.backend.dto.request.VoucherReq;
import com.dev.backend.dto.response.VoucherRes;
import com.dev.backend.entity.Voucher;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VoucherMapper {
    Voucher toVoucher(VoucherReq request);
    VoucherRes toVoucherRes(Voucher voucher);
}
