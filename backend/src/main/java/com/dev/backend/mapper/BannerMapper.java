package com.dev.backend.mapper;

import com.dev.backend.dto.request.BannerReq;
import com.dev.backend.dto.response.BannerRes;
import com.dev.backend.entity.Banner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BannerMapper {
    @Mapping(target = "imageUrl", ignore = true)
    Banner toBanner(BannerReq request);

    BannerRes toBannerRes(Banner banner);
}
