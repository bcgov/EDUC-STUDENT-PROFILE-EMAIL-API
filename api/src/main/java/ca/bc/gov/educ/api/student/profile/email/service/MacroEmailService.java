package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.props.MacroProperties;
import ca.bc.gov.educ.api.student.profile.email.struct.v2.EmailNotificationEntity;
import ca.bc.gov.educ.api.student.profile.email.struct.v1.macro.MacroEditNotificationEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Map;

@Service
@Slf4j
public class MacroEmailService {
  @Getter(AccessLevel.PRIVATE)
  private final MacroProperties props;

  @Getter(AccessLevel.PRIVATE)
  private final EmailNotificationService emailNotificationService;

  public MacroEmailService(final MacroProperties props, final EmailNotificationService emailNotificationService) {
    this.props = props;
    this.emailNotificationService = emailNotificationService;
  }

  public void notifyMacroEdit(final MacroEditNotificationEntity macroEditNotificationEntity, final boolean newMacro) {
    log.debug("Sending macro edit email");

    final var subjectTemplate = newMacro ? this.props.getMacroCreateEmailSubject() : this.props.getMacroUpdateEmailSubject();
    final String subject = MessageFormat.format(subjectTemplate, macroEditNotificationEntity.getAppName());

    final var emailNotificationEntity = EmailNotificationEntity.builder()
      .fromEmail(macroEditNotificationEntity.getFromEmail())
      .toEmail(macroEditNotificationEntity.getToEmail())
      .subject(subject)
      .templateName(newMacro ? "macro.create" : "macro.update")
      .emailFields(Map.of("businessUseTypeName", macroEditNotificationEntity.getBusinessUseTypeName(), "macroCode", macroEditNotificationEntity.getMacroCode(), "macroTypeCode", macroEditNotificationEntity.getMacroTypeCode(), "macroText", macroEditNotificationEntity.getMacroText()))
      .build();

    this.getEmailNotificationService().sendEmail(emailNotificationEntity);
    log.debug("Completed macro edit email successfully");
  }
}
