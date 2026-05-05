package com.pharma.notification;

import com.pharma.order.PharmacyOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {
  public void orderUpdate(PharmacyOrder order, String message) {
    log.info("notification.order user={} order={} status={} message={}", order.getPatient().getEmail(), order.getId(), order.getStatus(), message);
  }

  public void refillReminder(String email, String message) {
    log.info("notification.refill user={} message={}", email, message);
  }
}
