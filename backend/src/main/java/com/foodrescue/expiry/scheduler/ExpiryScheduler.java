package com.foodrescue.expiry.scheduler;

import com.foodrescue.expiry.service.ExpiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpiryScheduler {

    private final ExpiryService expiryService;

    @Scheduled(initialDelayString = "${app.expiry.initial-delay-ms}", fixedDelayString = "${app.expiry.check-interval-ms}")
    public void run() {
        try {
            ExpiryService.ExpiryResult r = expiryService.runCycle();
            if (r.expired() > 0 || r.warned() > 0) {
                log.info("Expiry cycle: {} donation(s) expired, {} expiry warning(s) sent", r.expired(), r.warned());
            }
        } catch (Exception e) {
            log.error("Expiry cycle failed", e);
        }
    }
}
