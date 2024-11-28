package com.dev.backend.service;

import com.dev.backend.entity.District;
import com.dev.backend.entity.Province;
import com.dev.backend.entity.Ward;

import java.util.List;

public interface AddressService {
    Province getById(Integer provinceId);
    List<Province> getAllProvinces();
    List<District> getByProvinceId(Integer provinceId);
    List<Ward> getByDistrictId(Integer districtId);
}
