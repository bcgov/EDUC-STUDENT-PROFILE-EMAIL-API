package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.props.MacroProperties;
import ca.bc.gov.educ.api.student.profile.email.struct.macro.MacroEditNotificationEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
@Slf4j
public class MacroEmailService {
  @Getter(AccessLevel.PRIVATE)
  private final MacroProperties props;

  @Getter(AccessLevel.PRIVATE)
  private final CHESEmailService chesEmailService;

  public MacroEmailService(final MacroProperties props, final CHESEmailService chesEmailService) {
    this.props = props;
    this.chesEmailService = chesEmailService;
  }

  public void notifyMacroEdit(final MacroEditNotificationEntity macroEditNotificationEntity, final boolean newMacro) {
    log.debug("Sending macro edit email");
    final var bodyTemplate = newMacro ? this.props.getMacroCreateEmailTemplate() : this.props.getMacroUpdateEmailTemplate();
    final var body = MessageFormat.format(bodyTemplate.replace("'", "''"), macroEditNotificationEntity.getBusinessUseTypeName(), macroEditNotificationEntity.getMacroCode(), macroEditNotificationEntity.getMacroTypeCode(), macroEditNotificationEntity.getMacroText());

    final var subjectTemplate = newMacro ? this.props.getMacroCreateEmailSubject() : this.props.getMacroUpdateEmailSubject();
    final String subject = MessageFormat.format(subjectTemplate, macroEditNotificationEntity.getAppName());

    this.getChesEmailService().sendEmail(macroEditNotificationEntity.getFromEmail(), macroEditNotificationEntity.getToEmail(), body, subject);
    log.debug("Completed macro edit email successfully");
  }
}
