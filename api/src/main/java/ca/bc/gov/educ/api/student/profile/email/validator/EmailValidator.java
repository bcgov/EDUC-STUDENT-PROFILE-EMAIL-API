package ca.bc.gov.educ.api.student.profile.email.validator;

import ca.bc.gov.educ.api.student.profile.email.struct.v2.EmailNotificationEntity;
import ca.bc.gov.educ.api.student.profile.email.struct.v1.gmpump.BaseEmailEntity;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class EmailValidator {
  private static final String REGEX = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
  private static final Pattern PATTERN = Pattern.compile(REGEX);

  @Getter(AccessLevel.PRIVATE)
  private final Map<String, String> templateConfig;

  public EmailValidator(final Map<String, String> templateConfig) {
    this.templateConfig = templateConfig;
  }

  public List<FieldError> validateEmail(final BaseEmailEntity emailEntity) {
    final List<FieldError> apiValidationErrors = new ArrayList<>();
    if (!PATTERN.matcher(emailEntity.getEmailAddress()).matches()) {
      apiValidationErrors.add(this.createEmailFieldError(emailEntity.getEmailAddress(), "emailAddress"));
    }
    return apiValidationErrors;
  }

  public List<FieldError> validateEmail(final EmailNotificationEntity emailEntity) {
    final List<FieldError> apiValidationErrors = new ArrayList<>();
    if(!this.templateConfig.containsKey(emailEntity.getTemplateName())) {
      apiValidationErrors.add(this.createTemplateFieldError(emailEntity.getTemplateName(), "templateName"));
    }
    if (!PATTERN.matcher(emailEntity.getFromEmail()).matches()) {
      apiValidationErrors.add(this.createEmailFieldError(emailEntity.getFromEmail(), "fromEmail"));
    }
    if (!PATTERN.matcher(emailEntity.getToEmail()).matches()) {
      apiValidationErrors.add(this.createEmailFieldError(emailEntity.getToEmail(), "toEmail"));
    }
    return apiValidationErrors;
  }

  private FieldError createEmailFieldError(final Object rejectedValue, final String fieldName) {
    return new FieldError("email", fieldName, rejectedValue, false, null, null, fieldName + " should be a valid email address.");
  }

  private FieldError createTemplateFieldError(final Object rejectedValue, final String fieldName) {
    return new FieldError("email", fieldName, rejectedValue, false, null, null, "Not found the email template.");
  }
}
