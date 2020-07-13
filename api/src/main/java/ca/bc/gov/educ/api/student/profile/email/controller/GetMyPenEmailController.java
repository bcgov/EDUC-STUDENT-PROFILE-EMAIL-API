package ca.bc.gov.educ.api.student.profile.email.controller;


import ca.bc.gov.educ.api.student.profile.email.endpoint.GetMyPenEmailEndpoint;
import ca.bc.gov.educ.api.student.profile.email.exception.InvalidPayloadException;
import ca.bc.gov.educ.api.student.profile.email.exception.errors.ApiError;
import ca.bc.gov.educ.api.student.profile.email.model.*;
import ca.bc.gov.educ.api.student.profile.email.service.GMPEmailService;
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
public class GetMyPenEmailController implements GetMyPenEmailEndpoint {

  private final GMPEmailService service;
  private final EmailValidator emailValidator;

  @Autowired
  GetMyPenEmailController(final GMPEmailService service, final EmailValidator emailValidator) {
    this.service = service;
    this.emailValidator = emailValidator;
  }

  @Override
  public ResponseEntity<Void> sendCompletedPENRequestEmail(GMPRequestCompleteEmailEntity penRequest, boolean demographicsChanged) {
    validateEmail(penRequest);
    service.sendCompletedPENRequestEmail(penRequest, demographicsChanged);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> sendRejectedPENRequestEmail(GMPRequestRejectedEmailEntity penRequest) {
    validateEmail(penRequest);
    service.sendRejectedPENRequestEmail(penRequest);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> sendAdditionalInfoRequestEmail(GMPRequestAdditionalInfoEmailEntity penRequest) {
    validateEmail(penRequest);
    service.sendAdditionalInfoEmail(penRequest);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> verifyEmail(GMPRequestEmailVerificationEntity gmpRequestEmailVerificationEntity) {
    validateEmail(gmpRequestEmailVerificationEntity);
    service.sendVerifyEmail(gmpRequestEmailVerificationEntity);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> notifyStudentForStaleReturnedRequests(GMPRequestAdditionalInfoEmailEntity gmpRequestAdditionalInfoEmailEntity) {
    service.sendStaleReturnedRequestNotificationEmail(gmpRequestAdditionalInfoEmailEntity);
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
