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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;
    private final BaseRedisServiceImpl<String, String, String> baseRedisService;
    private final ObjectMapper objectMapper;

    @Override
    public Province getById(Integer provinceId) throws JsonProcessingException {
        String key = String.format("province:%d", provinceId);
        if (baseRedisService.get(key) == null) {
            Province province = provinceRepository.findById(provinceId)
                    .orElseThrow(() -> new AppException(ErrorCode.PROVINCE_NOT_FOUND));;
            String json = objectMapper.writeValueAsString(province);
            baseRedisService.set(key, json);
            baseRedisService.setTimeToLive(key, 10L);
            return province;
        } else {
            String json = baseRedisService.get(key);
            return objectMapper.readValue(json, Province.class);
        }
    }

    @Override
    public List<Province> getAllProvinces() throws JsonProcessingException {
        String key = "all_provinces";
        if (baseRedisService.get(key) == null) {
            List<Province> result = provinceRepository.findAll();
            String json = objectMapper.writeValueAsString(result);
            baseRedisService.set(key, json);
            baseRedisService.setTimeToLive(key, 10L);
            return result;
        } else {
            String json = baseRedisService.get(key);
            return objectMapper.readValue(json, new TypeReference<List<Province>>() {});
        }
    }

    @Override
    public List<District> getByProvinceId(Integer provinceId) throws JsonProcessingException {
        String key = String.format("districts_by_provinceId:%d", provinceId);
        if (baseRedisService.get(key) == null) {
            List<District> result =districtRepository.findByProvinceId(provinceId);
            String json = objectMapper.writeValueAsString(result);
            baseRedisService.set(key, json);
            baseRedisService.setTimeToLive(key, 10L);
            return result;
        } else {
            String json = baseRedisService.get(key);
            return objectMapper.readValue(json, new TypeReference<List<District>>() {});
        }
    }

    @Override
    public List<Ward> getByDistrictId(Integer districtId) throws JsonProcessingException {
        String key = String.format("wards_by_districtId:%d", districtId);
        if (baseRedisService.get(key) == null) {
            List<Ward> result = wardRepository.findByDistrictId(districtId);
            String json = objectMapper.writeValueAsString(result);
            baseRedisService.set(key, json);
            baseRedisService.setTimeToLive(key, 10L);
            return result;
        } else {
            String json = baseRedisService.get(key);
            return objectMapper.readValue(json, new TypeReference<List<Ward>>() {});
        }
    }
}
