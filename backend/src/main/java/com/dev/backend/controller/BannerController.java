package com.dev.backend.controller;

import com.dev.backend.dto.request.BannerReq;
import com.dev.backend.dto.response.APIResponse;
import com.dev.backend.dto.response.BannerRes;
import com.dev.backend.dto.response.PageDto;
import com.dev.backend.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("banners")
@RequiredArgsConstructor
@Tag(name = "Banner APIs")
public class BannerController {
    private final BannerService bannerService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create banner")
    public APIResponse<BannerRes> createBanner(@Valid BannerReq request) {
        BannerRes result = bannerService.createBanner(request);
        return APIResponse.<BannerRes>builder().result(result).build();
    }

    @GetMapping
    @Operation(summary = "Get all banners")
    public APIResponse<PageDto<BannerRes>> getBanners(
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize
    ) {
        PageDto<BannerRes> result = bannerService.getAllBanners(pageNumber, pageSize);
        return APIResponse.<PageDto<BannerRes>>builder().result(result).build();
    }

    @GetMapping("{id}")
    @Operation(summary = "Create banner by id")
    public APIResponse<BannerRes> getCategoryById(@PathVariable("id") String id) {
        BannerRes result = bannerService.getBannerById(id);
        return APIResponse.<BannerRes>builder().result(result).build();
    }

    @PutMapping(value = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update banner")
    public APIResponse<BannerRes> updateBanner(@PathVariable("id") String id, @Valid BannerReq request) {
        BannerRes result = bannerService.updateBanner(id, request);
        return APIResponse.<BannerRes>builder().result(result).build();
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete banner")
    public APIResponse<Void> deleteBanner(@PathVariable("id") String id) {
        bannerService.deleteBanner(id);
        return APIResponse.<Void>builder().build();
    }
}
