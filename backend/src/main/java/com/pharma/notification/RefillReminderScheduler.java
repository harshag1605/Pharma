package com.pharma.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefillReminderScheduler {
  @Scheduled(cron = "0 0 9 * * *")
  void sendRefillReminders() {
    log.info("refill-reminder scan completed; wire this to refill plans or EMR integrations in production");
  }
}
