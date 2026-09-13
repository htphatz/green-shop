package com.dev.backend.controller;

import com.dev.backend.dto.response.APIResponse;
import com.dev.backend.entity.District;
import com.dev.backend.entity.Province;
import com.dev.backend.entity.Ward;
import com.dev.backend.service.AddressService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("address")
@RequiredArgsConstructor
@Tag(name = "Address APIs")
public class AddressController {
    private final AddressService addressService;

    @GetMapping("province")
    @Operation(summary = "Get all provinces")
    public APIResponse<List<Province>> getAllProvince() throws JsonProcessingException {
        List<Province> result = addressService.getAllProvinces();
        return APIResponse.<List<Province>>builder().result(result).build();
    }

    @GetMapping("district")
    @Operation(summary = "Get all districts by province's id")
    public APIResponse<List<District>> getDistrictByProvinceId(@RequestParam("provinceId") Integer provinceId) throws JsonProcessingException {
        List<District> result = addressService.getByProvinceId(provinceId);
        return APIResponse.<List<District>>builder().result(result).build();
    }

    @GetMapping("ward")
    @Operation(summary = "Get all wards by district's id")
    public APIResponse<List<Ward>> getWardByDistrictId(@RequestParam("districtId") Integer districtId) throws JsonProcessingException {
        List<Ward> result = addressService.getByDistrictId(districtId);
        return APIResponse.<List<Ward>>builder().result(result).build();
    }
}
