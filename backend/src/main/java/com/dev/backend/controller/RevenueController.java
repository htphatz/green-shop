package com.dev.backend.controller;

import com.dev.backend.dto.request.RevenueReq;
import com.dev.backend.dto.response.APIResponse;
import com.dev.backend.dto.response.RevenueRes;
import com.dev.backend.service.RevenueService;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("revenue")
@RequiredArgsConstructor
@Tag(name = "Revenue APIs")
public class RevenueController {
    private final RevenueService revenueService;

    @PostMapping
    @Operation(summary = "Get revenue by date range")
    public APIResponse<RevenueRes> getRevenue(@Valid @RequestBody RevenueReq request) {
        RevenueRes result = revenueService.getRevenue(request.getStartDate(), request.getEndDate());
        return APIResponse.<RevenueRes>builder().result(result).build();
    }

    @GetMapping("by-month")
    @Operation(summary = "Get revenue by month")
    public APIResponse<RevenueRes> getRevenueByMonth(
            @RequestParam("month") Integer month,
            @RequestParam("year") Integer year
    ) {
        RevenueRes result = revenueService.getRevenueByMonth(month, year);
        return APIResponse.<RevenueRes>builder().result(result).build();
    }

    @GetMapping("by-year")
    @Operation(summary = "Get revenue by year")
    public APIResponse<RevenueRes> getRevenueByYear(@RequestParam("year") Integer year) {
        RevenueRes result = revenueService.getRevenueByYear(year);
        return APIResponse.<RevenueRes>builder().result(result).build();
    }

    @GetMapping("by-product")
    @Operation(summary = "Get revenue by product")
    public APIResponse<List<Object[]>> getRevenueByProduct() {
        List<Object[]> result = revenueService.getRevenueByProduct();
        return APIResponse.<List<Object[]>>builder().result(result).build();
    }

    @GetMapping("by-category")
    @Operation(summary = "Get revenue by category")
    public APIResponse<List<Object[]>> getRevenueByCategory() {
        List<Object[]> result = revenueService.getRevenueByCategory();
        return APIResponse.<List<Object[]>>builder().result(result).build();
    }
}
