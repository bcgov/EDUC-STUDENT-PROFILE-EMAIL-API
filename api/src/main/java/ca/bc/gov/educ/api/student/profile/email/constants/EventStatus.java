package ca.bc.gov.educ.api.student.profile.email.constants;

import lombok.Getter;

public enum EventStatus {
  PENDING_EMAIL_ACK("PENDING_EMAIL_ACK"),
  DB_COMMITTED("DB_COMMITTED"),
  MESSAGE_PUBLISHED("MESSAGE_PUBLISHED");

  @Getter
  private final String code;

  EventStatus(String code) {
    this.code = code;
  }
}
