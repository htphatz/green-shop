package com.dev.backend.schedule;

import com.dev.backend.entity.InvalidatedToken;
import com.dev.backend.repository.InvalidatedTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvalidTokenCleanupScheduler {
    private final InvalidatedTokenRepository invalidatedTokenRepository;

    /*
        ---VD---: "0 0 0 * * ?" trong đó
        0: Giây (bắt đầu tại giây 0).
        0: Phút (bắt đầu tại phút 0).
        0: Giờ (vào lúc 12 giờ đêm, tức 00:00).
        *: Mọi ngày trong tháng.
        *: Mọi tháng.
        ?: Bỏ qua thông số ngày trong tuần (không cần thiết ở đây)
    */
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredTokens(){
        log.info("Start scheduler delete invalid tokens");
        List<InvalidatedToken> invalidatedTokens = invalidatedTokenRepository.findAll();
        if(!invalidatedTokens.isEmpty()){
            invalidatedTokenRepository.deleteAll();
        }
    }
}
