package ca.bc.gov.educ.api.student.profile.email.struct.v2;

import jakarta.validation.constraints.*;
import java.util.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PenRequestBatchEmailNotificationEntity {
  @NotNull(message = "fromEmail can not be null.")
  private String fromEmail;
  @NotNull(message = "toEmail can not be null.")
  private List<String> toEmail;
  @NotNull(message = "subject can not be null.")
  private String subject;

  @NotNull(message = "templateName can not be null.")
  private String templateName;

  @NotNull(message = "emailFields can not be null.")
  private Map<String, String> emailFields;
}

