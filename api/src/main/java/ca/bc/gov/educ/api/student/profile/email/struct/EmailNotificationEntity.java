package ca.bc.gov.educ.api.student.profile.email.struct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailNotificationEntity {
  private String fromEmail;
  private String toEmail;
  private String subject;

  private String templateName;
  private Map<String, String> emailFields;
}

