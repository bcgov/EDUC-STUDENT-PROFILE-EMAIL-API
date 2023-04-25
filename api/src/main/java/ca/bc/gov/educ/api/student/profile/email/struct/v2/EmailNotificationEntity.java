package ca.bc.gov.educ.api.student.profile.email.struct.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailNotificationEntity {
  @NotNull(message = "fromEmail can not be null.")
  private String fromEmail;
  @NotNull(message = "toEmail can not be null.")
  private String toEmail;
  @NotNull(message = "subject can not be null.")
  private String subject;

  @NotNull(message = "templateName can not be null.")
  private String templateName;

  @NotNull(message = "emailFields can not be null.")
  private Map<String, String> emailFields;
}

