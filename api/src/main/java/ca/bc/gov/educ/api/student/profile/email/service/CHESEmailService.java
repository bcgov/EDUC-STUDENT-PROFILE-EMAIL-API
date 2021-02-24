package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.rest.RestUtils;
import ca.bc.gov.educ.api.student.profile.email.struct.gmpump.BaseEmailEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CHESEmailService {
  private final RestUtils restUtils;

  @Autowired
  public CHESEmailService(final RestUtils restUtils) {
    this.restUtils = restUtils;
  }

  // Retry logic, if server encounters any issue such as communication failure etc..
  @Retryable(value = {Exception.class}, maxAttempts = 10, backoff = @Backoff(multiplier = 2, delay = 2000))
  public void sendEmail(final BaseEmailEntity baseEmailEntity, final String body, final String subject) {
    this.restUtils.sendEmail(baseEmailEntity.getEmailAddress(), body, subject);
  }

  @Retryable(value = {Exception.class}, maxAttempts = 10, backoff = @Backoff(multiplier = 2, delay = 2000))
  public void sendEmail(final String fromEmail, final String toEmail, final String body, final String subject) {
    this.restUtils.sendEmail(fromEmail, toEmail, body, subject);
  }


}
