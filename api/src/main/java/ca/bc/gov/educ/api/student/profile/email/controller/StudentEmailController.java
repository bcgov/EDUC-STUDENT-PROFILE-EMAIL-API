package ca.bc.gov.educ.api.student.profile.email.controller;

import ca.bc.gov.educ.api.student.profile.email.endpoint.StudentEmailEndpoint;
import ca.bc.gov.educ.api.student.profile.email.exception.InvalidPayloadException;
import ca.bc.gov.educ.api.student.profile.email.exception.errors.ApiError;
import ca.bc.gov.educ.api.student.profile.email.model.*;
import ca.bc.gov.educ.api.student.profile.email.service.StudentEmailService;
import ca.bc.gov.educ.api.student.profile.email.validator.EmailValidator;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@EnableResourceServer
@Slf4j
public class StudentEmailController implements StudentEmailEndpoint {

  private final StudentEmailService service;
  private final EmailValidator emailValidator;

  @Autowired
  StudentEmailController(final StudentEmailService penRequest, final EmailValidator emailValidator) {
    this.service = penRequest;
    this.emailValidator = emailValidator;
  }

  @Override
  public ResponseEntity<Void> sendCompletedRequestEmail(RequestCompleteEmailEntity request) {
    validateEmail(request);
    service.sendCompletedRequestEmail(request);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> sendRejectedRequestEmail(RequestRejectedEmailEntity request) {
    validateEmail(request);
    service.sendRejectedRequestEmail(request);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> sendAdditionalInfoRequestEmail(RequestAdditionalInfoEmailEntity request) {
    validateEmail(request);
    service.sendAdditionalInfoEmail(request);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> verifyEmail(RequestEmailVerificationEntity request) {
    validateEmail(request);
    service.sendVerifyEmail(request);
    return ResponseEntity.noContent().build();
  }


  private void validateEmail(BaseEmailEntity emailEntity) {
    val validationResult = emailValidator.validateEmail(emailEntity);
    if (!validationResult.isEmpty()) {
      ApiError error = ApiError.builder().timestamp(LocalDateTime.now()).message("Payload contains invalid data.").status(BAD_REQUEST).build();
      error.addValidationErrors(validationResult);
      throw new InvalidPayloadException(error);
    }
  }

}
