package ca.bc.gov.educ.api.student.profile.email.struct.macro;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MacroEditNotificationEntity {
  private String fromEmail;
  private String toEmail;

  private String macroCode;
  private String macroText;
  private String macroTypeCode;
  private String businessUseTypeName;
  private String appName;
}

