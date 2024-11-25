package com.dev.backend.controller;

import com.dev.backend.dto.request.VoucherReq;
import com.dev.backend.dto.response.APIResponse;
import com.dev.backend.dto.response.PageDto;
import com.dev.backend.dto.response.VoucherRes;
import com.dev.backend.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("vouchers")
@RequiredArgsConstructor
public class VoucherController {
    private final VoucherService voucherService;

    @PostMapping
    public APIResponse<VoucherRes> createVoucher(@RequestBody VoucherReq request) {
        VoucherRes result = voucherService.createVoucher(request);
        return APIResponse.<VoucherRes>builder().result(result).build();
    }

    @GetMapping
    public APIResponse<PageDto<VoucherRes>> getAllVouchers(
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize
    ) {
        PageDto<VoucherRes> result = voucherService.findAll(pageNumber, pageSize);
        return APIResponse.<PageDto<VoucherRes>>builder().result(result).build();
    }

    @GetMapping("{code}")
    public APIResponse<VoucherRes> getVoucherByCode(@PathVariable("code") String code) {
        VoucherRes result = voucherService.findByCode(code);
        return APIResponse.<VoucherRes>builder().result(result).build();
    }

    @PutMapping("{code}")
    public APIResponse<VoucherRes> updateVoucher(@PathVariable("code") String code,@RequestBody VoucherReq request) {
        VoucherRes result = voucherService.updateVoucher(code, request);
        return APIResponse.<VoucherRes>builder().result(result).build();
    }

    @DeleteMapping("{code}")
    public APIResponse<Void> deleteVoucher(@PathVariable("code") String code) {
        voucherService.deleteByCode(code);
        return APIResponse.<Void>builder().build();
    }
}
