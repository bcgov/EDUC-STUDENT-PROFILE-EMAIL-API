package ca.bc.gov.educ.api.student.profile.email.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class PenRequestBatchProperties {
  @Value("${email.template.notify.school.incorrect.format.file}")
  private String emailTemplateNotifySchoolIncorrectFormatFile;

  @Value("${email.template.penRequestBatch.archive.hasSchoolContact}")
  private String archivedPenRequestBatchToSchoolEmailTemplate;

  @Value("${email.template.penRequestBatch.archive.hasNoSchoolContact}")
  private String archivedPenRequestBatchNoSchoolContactEmailTemplate;

  @Value("${url.login.penRequestBatch.penCoordinator}")
  private String penCoordinatorLoginUrl;

  @Value("${email.subject.penRequestBatch.archive.hasSchoolContact}")
  private String archivePrbHasSchoolContactEmailSubject;

  @Value("${email.subject.penRequestBatch.archive.hasNoSchoolContact}")
  private String archivePrbHasNoSchoolContactEmailSubject;

  @Value("${email.template.penRequestBatch.archive.hasSchoolContact.pending.all}")
  private String archivedPenRequestBatchToSchoolEmailTemplateAllPending;

  @Value("${email.template.penRequestBatch.archive.hasSchoolContact.pending.some}")
  private String archivedPenRequestBatchToSchoolEmailTemplateSomePending;
}
