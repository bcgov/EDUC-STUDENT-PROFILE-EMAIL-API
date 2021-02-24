package ca.bc.gov.educ.api.student.profile.email.validator;

import ca.bc.gov.educ.api.student.profile.email.struct.gmpump.BaseEmailEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class EmailValidator {
  private static final String REGEX = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
  private static final Pattern PATTERN = Pattern.compile(REGEX);

  public List<FieldError> validateEmail(final BaseEmailEntity emailEntity) {
    final List<FieldError> apiValidationErrors = new ArrayList<>();
    if (!PATTERN.matcher(emailEntity.getEmailAddress()).matches()) {
      apiValidationErrors.add(this.createFieldError(emailEntity.getEmailAddress()));
    }
    return apiValidationErrors;
  }

  private FieldError createFieldError(final Object rejectedValue) {
    return new FieldError("email", "emailAddress", rejectedValue, false, null, null, "Email address should be a valid email address.");
  }
}
