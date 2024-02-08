package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.props.PenRequestBatchProperties;
import ca.bc.gov.educ.api.student.profile.email.struct.v2.*;
import ca.bc.gov.educ.api.student.profile.email.struct.v1.penrequestbatch.ArchivePenRequestBatchNotificationEntity;
import ca.bc.gov.educ.api.student.profile.email.struct.v1.penrequestbatch.PenRequestBatchSchoolErrorNotificationEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Map;

@Service
@Slf4j
public class PenRequestBatchEmailService {
  private static final String SUBMISSION_NUMBER = "submissionNumber";
  @Getter(AccessLevel.PRIVATE)
  private final PenRequestBatchProperties props;

  @Getter(AccessLevel.PRIVATE)
  private final EmailNotificationService emailNotificationService;

  public PenRequestBatchEmailService(final PenRequestBatchProperties props, final EmailNotificationService emailNotificationService) {
    this.props = props;
    this.emailNotificationService = emailNotificationService;
  }

  public void notifySchoolFileFormatIncorrect(final PenRequestBatchSchoolErrorNotificationEntity errorNotificationEntity) {
    final String subject = "PEN Request could not be processed for File: ".concat(errorNotificationEntity.getFileName()).concat(" with Submission Number : ").concat(errorNotificationEntity.getSubmissionNumber());
    final var emailNotificationEntity = PenRequestBatchEmailNotificationEntity.builder() //TODO This will also need to accept a list of strings for the To Email field
      .fromEmail(errorNotificationEntity.getFromEmail())
      .toEmail(errorNotificationEntity.getToEmail())
      .subject(subject)
      .templateName("notify.school.incorrect.format.file")
      .emailFields(Map.of(SUBMISSION_NUMBER, errorNotificationEntity.getSubmissionNumber(), "dateTime", errorNotificationEntity.getDateTime(), "failReason", errorNotificationEntity.getFailReason(), "fromEmail", errorNotificationEntity.getFromEmail()))
      .build();
    this.getEmailNotificationService().sendEmail(emailNotificationEntity);
  }

  public void sendArchivePenRequestBatchHasSchoolContactEmail(final ArchivePenRequestBatchNotificationEntity archivePenRequestBatchEmailEntity) {
    log.debug("Sending archive pen request batch has school contact email");
    this.getEmailNotificationService().sendEmail(this.getEmailBody(archivePenRequestBatchEmailEntity));
    log.debug("Completed archive pen request batch has school contact email successfully");
  }

  private PenRequestBatchEmailNotificationEntity getEmailBody(final ArchivePenRequestBatchNotificationEntity archivePenRequestBatchEmailEntity) {
    final String templateName;
    if (archivePenRequestBatchEmailEntity.getPendingRecords() != null) {
      switch (archivePenRequestBatchEmailEntity.getPendingRecords()) {
        case ALL:
          templateName = "penRequestBatch.archive.hasSchoolContact.pending.all";
          break;
        case SOME:
          templateName = "penRequestBatch.archive.hasSchoolContact.pending.some";
          break;
        default:
          templateName = "penRequestBatch.archive.hasSchoolContact";
          break;
      }
    } else {
      templateName = "penRequestBatch.archive.hasSchoolContact";
    }

    final String subject = MessageFormat.format(this.props.getArchivePrbHasSchoolContactEmailSubject(), archivePenRequestBatchEmailEntity.getSubmissionNumber(), archivePenRequestBatchEmailEntity.getMincode(), archivePenRequestBatchEmailEntity.getSchoolName());

//    Do we need to change this to BATCHEMAILNOTIFICATIONENTITY?
    return PenRequestBatchEmailNotificationEntity.builder()
      .fromEmail(archivePenRequestBatchEmailEntity.getFromEmail())
      .toEmail(archivePenRequestBatchEmailEntity.getToEmail())
      .subject(subject)
      .templateName(templateName)
      .emailFields(Map.of(SUBMISSION_NUMBER, archivePenRequestBatchEmailEntity.getSubmissionNumber(), "penCoordinatorLoginUrl", this.props.getPenCoordinatorLoginUrl()))
      .build();
  }

  public void sendArchivePenRequestBatchHasNoSchoolContactEmail(final ArchivePenRequestBatchNotificationEntity archivePenRequestBatchEmailEntity) {
    log.debug("Sending archive pen request batch has school contact email");
    final String subject = MessageFormat.format(this.props.getArchivePrbHasNoSchoolContactEmailSubject(), archivePenRequestBatchEmailEntity.getSubmissionNumber(), archivePenRequestBatchEmailEntity.getMincode());
    final var emailNotificationEntity =  PenRequestBatchEmailNotificationEntity.builder()
      .fromEmail(archivePenRequestBatchEmailEntity.getFromEmail())
      .toEmail(archivePenRequestBatchEmailEntity.getToEmail())
      .subject(subject)
      .templateName("penRequestBatch.archive.hasNoSchoolContact")
      .emailFields(Map.of("mincode", archivePenRequestBatchEmailEntity.getMincode(), SUBMISSION_NUMBER, archivePenRequestBatchEmailEntity.getSubmissionNumber(), "penCoordinatorLoginUrl", this.props.getPenCoordinatorLoginUrl()))
      .build();
    this.getEmailNotificationService().sendEmail(emailNotificationEntity);
    log.debug("Completed archive pen request batch has school contact email successfully");
  }
}
