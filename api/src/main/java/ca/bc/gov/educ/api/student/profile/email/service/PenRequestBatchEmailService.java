package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.props.PenRequestBatchProperties;
import ca.bc.gov.educ.api.student.profile.email.struct.penrequestbatch.ArchivePenRequestBatchNotificationEntity;
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
  private final PenRequestBatchProperties props;

  @Getter(AccessLevel.PRIVATE)
  private final CHESEmailService chesEmailService;

  public PenRequestBatchEmailService(final PenRequestBatchProperties props, final CHESEmailService chesEmailService) {
    this.props = props;
    this.chesEmailService = chesEmailService;
  }

  public void notifySchoolFileFormatIncorrect(final PenRequestBatchSchoolErrorNotificationEntity errorNotificationEntity) {
    final String body = MessageFormat.format(this.props.getEmailTemplateNotifySchoolIncorrectFormatFile().replace("'", "''"), errorNotificationEntity.getSubmissionNumber(), errorNotificationEntity.getDateTime(), errorNotificationEntity.getFailReason(), errorNotificationEntity.getFromEmail());
    final String subject = "PEN Request could not be processed for File: ".concat(errorNotificationEntity.getFileName()).concat(" with Submission Number : ").concat(errorNotificationEntity.getSubmissionNumber());
    this.getChesEmailService().sendEmail(errorNotificationEntity.getFromEmail(), errorNotificationEntity.getToEmail(), body, subject);
  }

  public void sendArchivePenRequestBatchHasSchoolContactEmail(final ArchivePenRequestBatchNotificationEntity archivePenRequestBatchEmailEntity) {
    log.debug("Sending archive pen request batch has school contact email");
    final String body = MessageFormat.format(this.props.getArchivedPenRequestBatchToSchoolEmailTemplate().replace("'", "''"), archivePenRequestBatchEmailEntity.getSubmissionNumber(), this.props.getPenCoordinatorLoginUrl(), this.props.getPenCoordinatorLoginUrl());
    final String subject = MessageFormat.format(this.props.getArchivePrbHasSchoolContactEmailSubject(), archivePenRequestBatchEmailEntity.getMincode(), archivePenRequestBatchEmailEntity.getSchoolName());
    this.getChesEmailService().sendEmail(archivePenRequestBatchEmailEntity.getFromEmail(), archivePenRequestBatchEmailEntity.getToEmail(), body, subject);
    log.debug("Completed archive pen request batch has school contact email successfully");
  }

  public void sendArchivePenRequestBatchHasNoSchoolContactEmail(final ArchivePenRequestBatchNotificationEntity archivePenRequestBatchEmailEntity) {
    log.debug("Sending archive pen request batch has school contact email");
    final String body = MessageFormat.format(this.props.getArchivedPenRequestBatchNoSchoolContactEmailTemplate().replace("'", "''"), archivePenRequestBatchEmailEntity.getMincode(), archivePenRequestBatchEmailEntity.getSubmissionNumber(), this.props.getPenCoordinatorLoginUrl(), this.props.getPenCoordinatorLoginUrl());
    final String subject = MessageFormat.format(this.props.getArchivePrbHasNoSchoolContactEmailSubject(), archivePenRequestBatchEmailEntity.getMincode());
    this.getChesEmailService().sendEmail(archivePenRequestBatchEmailEntity.getFromEmail(), archivePenRequestBatchEmailEntity.getToEmail(), body, subject);
    log.debug("Completed archive pen request batch has school contact email successfully");
  }
}
