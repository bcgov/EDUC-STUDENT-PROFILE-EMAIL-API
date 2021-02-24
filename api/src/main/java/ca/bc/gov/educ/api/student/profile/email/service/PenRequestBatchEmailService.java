package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.props.ApplicationProperties;
import ca.bc.gov.educ.api.student.profile.email.struct.penrequestbatch.PenRequestBatchSchoolErrorNotificationEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
@Slf4j
public class PenRequestBatchEmailService {
  @Getter(AccessLevel.PRIVATE)
  private final ApplicationProperties props;

  @Getter(AccessLevel.PRIVATE)
  private final CHESEmailService chesEmailService;

  public PenRequestBatchEmailService(final ApplicationProperties props, final CHESEmailService chesEmailService) {
    this.props = props;
    this.chesEmailService = chesEmailService;
  }

  public void notifySchoolFileFormatIncorrect(final PenRequestBatchSchoolErrorNotificationEntity errorNotificationEntity) {
    final String body = MessageFormat.format(this.props.getEmailTemplateNotifySchoolIncorrectFormatFile().replace("'", "''"), errorNotificationEntity.getSubmissionNumber(), errorNotificationEntity.getDateTime(), errorNotificationEntity.getFailReason(), errorNotificationEntity.getFromEmail());
    this.getChesEmailService().sendEmail(errorNotificationEntity.getFromEmail(), errorNotificationEntity.getToEmail(), body, errorNotificationEntity.getSubjectLine());
  }
}
