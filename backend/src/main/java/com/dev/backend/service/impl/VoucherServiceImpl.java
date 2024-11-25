package com.dev.backend.service.impl;

import com.dev.backend.dto.request.VoucherReq;
import com.dev.backend.dto.response.PageDto;
import com.dev.backend.dto.response.VoucherRes;
import com.dev.backend.entity.Voucher;
import com.dev.backend.exception.AppException;
import com.dev.backend.exception.ErrorCode;
import com.dev.backend.mapper.VoucherMapper;
import com.dev.backend.repository.VoucherRepository;
import com.dev.backend.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {
    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;

    @Override
    public VoucherRes createVoucher(VoucherReq request) {
        Voucher voucher = Voucher.builder()
                .code(request.getCode())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .expirationDate(request.getExpirationDate())
                .build();

        try {
            voucherRepository.save(voucher);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.VOUCHER_EXISTED);
        }
        return voucherMapper.toVoucherRes(voucherRepository.save(voucher));
    }

    @Override
    public VoucherRes findByCode(String code) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
        return voucherMapper.toVoucherRes(voucher);
    }

    @Override
    public PageDto<VoucherRes> findAll(Integer pageNumber, Integer pageSize) {
        pageNumber--;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Voucher> vouchers = voucherRepository.findAll(pageable);
        return PageDto.of(vouchers).map(voucherMapper::toVoucherRes);
    }

    @Override
    public VoucherRes updateVoucher(String code, VoucherReq request) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
        voucher.setCode(request.getCode());
        voucher.setDiscountType(request.getDiscountType());
        voucher.setDiscountValue(request.getDiscountValue());
        voucher.setExpirationDate(request.getExpirationDate());
        return voucherMapper.toVoucherRes(voucherRepository.save(voucher));
    }

    @Override
    public void deleteByCode(String code) {
        voucherRepository.deleteById(code);
    }
}
