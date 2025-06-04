package com.dev.backend.service.impl;

import com.dev.backend.dto.request.BannerReq;
import com.dev.backend.dto.response.BannerRes;
import com.dev.backend.dto.response.PageDto;
import com.dev.backend.entity.Banner;
import com.dev.backend.exception.AppException;
import com.dev.backend.exception.ErrorCode;
import com.dev.backend.mapper.BannerMapper;
import com.dev.backend.repository.BannerRepository;
import com.dev.backend.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {
    private final BannerRepository bannerRepository;
    private final CloudinaryService cloudinaryService;
    private final BannerMapper bannerMapper;

    @Value("${resource.defaultImage}")
    private String defaultImage;

    @Override
    public BannerRes createBanner(BannerReq request) {
        Banner banner = bannerMapper.toBanner(request);
        String imageUrl = defaultImage;
        banner.setImageUrl(imageUrl);
        if (request.getFileImage() != null && !request.getFileImage().isEmpty()) {
            Map data = this.cloudinaryService.upload(request.getFileImage());
            imageUrl = (String) data.get("secure_url");
            banner.setImageUrl(imageUrl);
        }
        return bannerMapper.toBannerRes(bannerRepository.save(banner));
    }

    @Override
    public BannerRes getBannerById(String id) {
        Banner banner = bannerRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.BANNER_NOT_FOUND));
        return bannerMapper.toBannerRes(banner);
    }

    @Override
    public PageDto<BannerRes> getAllBanners(Integer pageNumber, Integer pageSize) {
        pageNumber--;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Banner> banners = bannerRepository.findAll(pageable);
        return PageDto.of(banners).map(bannerMapper::toBannerRes);
    }

    @Override
    @Transactional
    public BannerRes updateBanner(String id, BannerReq request) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BANNER_NOT_FOUND));
        banner.setName(request.getName());
        if (request.getFileImage() != null && !request.getFileImage().isEmpty()) {
            Map data = this.cloudinaryService.upload(request.getFileImage());
            String newImageUrl = (String) data.get("secure_url");
            banner.setImageUrl(newImageUrl);
        }
        return bannerMapper.toBannerRes(bannerRepository.save(banner));
    }

    @Override
    public void deleteBanner(String id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BANNER_NOT_FOUND));
        bannerRepository.delete(banner);
    }
}