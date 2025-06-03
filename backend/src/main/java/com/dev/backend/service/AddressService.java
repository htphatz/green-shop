package com.dev.backend.service;

import com.dev.backend.entity.District;
import com.dev.backend.entity.Province;
import com.dev.backend.entity.Ward;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface AddressService {
    Province getById(Integer provinceId) throws JsonProcessingException;
    List<Province> getAllProvinces() throws JsonProcessingException;
    List<District> getByProvinceId(Integer provinceId) throws JsonProcessingException;
    List<Ward> getByDistrictId(Integer districtId) throws JsonProcessingException;
}
