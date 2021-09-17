package ca.bc.gov.educ.api.student.profile.email.controller.v2;

import ca.bc.gov.educ.api.student.profile.email.endpoint.v2.EmailNotificationEndpoint;
import ca.bc.gov.educ.api.student.profile.email.exception.InvalidPayloadException;
import ca.bc.gov.educ.api.student.profile.email.exception.errors.ApiError;
import ca.bc.gov.educ.api.student.profile.email.service.EmailNotificationService;
import ca.bc.gov.educ.api.student.profile.email.struct.v2.EmailNotificationEntity;
import ca.bc.gov.educ.api.student.profile.email.validator.EmailValidator;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@Slf4j
public class EmailNotificationController implements EmailNotificationEndpoint {

  private final EmailNotificationService service;
  private final EmailValidator emailValidator;

  @Autowired
  EmailNotificationController(final EmailNotificationService service, final EmailValidator emailValidator) {
    this.service = service;
    this.emailValidator = emailValidator;
  }

  @Override
  public ResponseEntity<Void> sendEmail(final EmailNotificationEntity emailNotificationEntity) {
    this.validateEmail(emailNotificationEntity);
    this.service.sendEmail(emailNotificationEntity);
    return ResponseEntity.noContent().build();
  }

  private void validateEmail(final EmailNotificationEntity emailEntity) {
    val validationResult = this.emailValidator.validateEmail(emailEntity);
    if (!validationResult.isEmpty()) {
      final ApiError error = ApiError.builder().timestamp(LocalDateTime.now()).message("Payload contains invalid data.").status(BAD_REQUEST).build();
      error.addValidationErrors(validationResult);
      throw new InvalidPayloadException(error);
    }
  }

}
