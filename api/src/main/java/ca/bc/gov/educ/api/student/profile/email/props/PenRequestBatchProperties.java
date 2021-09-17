package ca.bc.gov.educ.api.student.profile.email.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class PenRequestBatchProperties {
  @Value("${url.login.penRequestBatch.penCoordinator}")
  private String penCoordinatorLoginUrl;

  @Value("${email.subject.penRequestBatch.archive.hasSchoolContact}")
  private String archivePrbHasSchoolContactEmailSubject;

  @Value("${email.subject.penRequestBatch.archive.hasNoSchoolContact}")
  private String archivePrbHasNoSchoolContactEmailSubject;
}
