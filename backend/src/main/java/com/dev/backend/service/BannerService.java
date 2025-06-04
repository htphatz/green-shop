package com.dev.backend.service;

import com.dev.backend.dto.request.BannerReq;
import com.dev.backend.dto.response.BannerRes;
import com.dev.backend.dto.response.PageDto;

public interface BannerService {
    BannerRes createBanner(BannerReq request);
    BannerRes getBannerById(String id);
    PageDto<BannerRes> getAllBanners(Integer pageNumber, Integer pageSize);
    BannerRes updateBanner(String id, BannerReq request);
    void deleteBanner(String id);
}
