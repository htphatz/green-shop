package com.dev.backend.service.impl;

import com.dev.backend.entity.District;
import com.dev.backend.entity.Province;
import com.dev.backend.entity.Ward;
import com.dev.backend.exception.AppException;
import com.dev.backend.exception.ErrorCode;
import com.dev.backend.repository.DistrictRepository;
import com.dev.backend.repository.ProvinceRepository;
import com.dev.backend.repository.WardRepository;
import com.dev.backend.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;

    @Override
    public Province getById(Integer provinceId) {
        return provinceRepository.findById(provinceId)
                .orElseThrow(() -> new AppException(ErrorCode.PROVINCE_FOUND));
    }

    @Override
    public List<Province> getAllProvinces() {
        return provinceRepository.findAll();
    }

    @Override
    public List<District> getByProvinceId(Integer provinceId) {
        return districtRepository.findByProvinceId(provinceId);
    }

    @Override
    public List<Ward> getByDistrictId(Integer districtId) {
        return wardRepository.findByDistrictId(districtId);
    }
}
