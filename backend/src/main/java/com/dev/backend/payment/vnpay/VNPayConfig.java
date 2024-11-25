package com.dev.backend.payment.vnpay;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Getter
@Configuration
public class VNPayConfig {
    @Value("${payment.vnpay.pay-url}")
    private String vnpPayUrl;

    @Value("${payment.vnpay.return-url}")
    private String vnpReturnUrl;

    @Value("${payment.vnpay.tmn-code}")
    private String vnpTmnCode;

    @Value("${payment.vnpay.secret-key}")
    private String secretKey;

    @Value("${payment.vnpay.version}")
    private String vnpVersion;

    @Value("${payment.vnpay.command}")
    private String vnpCommand;

    @Value("${payment.vnpay.order-type}")
    private String orderType;

    public Map<String, String> getVNPayConfig() {
        Map<String, String> vnpParamsMap = new HashMap<>();
        vnpParamsMap.put("vnp_Version", vnpVersion);
        vnpParamsMap.put("vnp_Command", vnpCommand);
        vnpParamsMap.put("vnp_TmnCode", vnpTmnCode);
        vnpParamsMap.put("vnp_CurrCode", "VND");
        vnpParamsMap.put("vnp_IpAddr", "127.0.0.1");
        vnpParamsMap.put("vnp_BankCode", "NCB");
        vnpParamsMap.put("vnp_OrderInfo", "Thanh toan don hang:" + VNPayUtil.getRandomNumber(8));
        vnpParamsMap.put("vnp_OrderType", orderType);
        vnpParamsMap.put("vnp_Locale", "vn");
        vnpParamsMap.put("vnp_ReturnUrl", vnpReturnUrl);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_CreateDate", vnpCreateDate);
        calendar.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_ExpireDate", vnp_ExpireDate);
        return vnpParamsMap;
    }
}
