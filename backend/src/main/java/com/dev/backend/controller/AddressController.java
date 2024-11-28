package com.dev.backend.controller;

import com.dev.backend.entity.District;
import com.dev.backend.entity.Province;
import com.dev.backend.entity.Ward;
import com.dev.backend.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("address")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @GetMapping("province")
    public List<Province> getAllProvince() {
        return addressService.getAllProvinces();
    }

    @GetMapping("district")
    public List<District> getDistrictByProvinceId(@RequestParam("provinceId") Integer provinceId) {
        return addressService.getByProvinceId(provinceId);
    }

    @GetMapping("ward")
    public List<Ward> getWardByDistrictId(@RequestParam("districtId") Integer districtId) {
        return addressService.getByDistrictId(districtId);
    }
}
