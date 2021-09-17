package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.struct.EmailNotificationEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.Map;

@Service
@Slf4j
public class EmailNotificationService {
  /**
   * the Thymeleaf template engine
   */
  @Getter(AccessLevel.PRIVATE)
  private SpringTemplateEngine templateEngine;

  @Getter(AccessLevel.PRIVATE)
  private final CHESEmailService chesEmailService;

  @Getter(AccessLevel.PRIVATE)
  private final Map<String, String> templateConfig;

  public EmailNotificationService(final Map<String, String> templateConfig, final SpringTemplateEngine templateEngine, final CHESEmailService chesEmailService) {
    this.templateEngine = templateEngine;
    this.chesEmailService = chesEmailService;
    this.templateConfig = templateConfig;
  }

  public void sendEmail(final EmailNotificationEntity emailNotificationEntity) {
    log.debug("Sending email");

    final var ctx = new Context();
    emailNotificationEntity.getEmailFields().forEach(ctx::setVariable);

    final var body = this.templateEngine.process(this.templateConfig.get(emailNotificationEntity.getTemplateName()), ctx);   //emailNotificationEntity.getTemplateName() + ".html"

    this.getChesEmailService().sendEmail(emailNotificationEntity.getFromEmail(), emailNotificationEntity.getToEmail(), body, emailNotificationEntity.getSubject());
    log.debug("Email sent successfully");
  }
}
