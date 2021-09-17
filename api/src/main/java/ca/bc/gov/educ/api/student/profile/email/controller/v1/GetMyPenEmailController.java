package ca.bc.gov.educ.api.student.profile.email.controller.v1;


import ca.bc.gov.educ.api.student.profile.email.endpoint.v1.GetMyPenEmailEndpoint;
import ca.bc.gov.educ.api.student.profile.email.exception.InvalidPayloadException;
import ca.bc.gov.educ.api.student.profile.email.exception.errors.ApiError;
import ca.bc.gov.educ.api.student.profile.email.service.GMPEmailService;
import ca.bc.gov.educ.api.student.profile.email.struct.v1.gmpump.*;
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
public class GetMyPenEmailController implements GetMyPenEmailEndpoint {

  private final GMPEmailService service;
  private final EmailValidator emailValidator;

  @Autowired
  GetMyPenEmailController(final GMPEmailService service, final EmailValidator emailValidator) {
    this.service = service;
    this.emailValidator = emailValidator;
  }

  @Override
  public ResponseEntity<Void> sendCompletedPENRequestEmail(final GMPRequestCompleteEmailEntity penRequest, final boolean demographicsChanged) {
    this.validateEmail(penRequest);
    this.service.sendCompletedPENRequestEmail(penRequest, demographicsChanged);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> sendRejectedPENRequestEmail(final GMPRequestRejectedEmailEntity penRequest) {
    this.validateEmail(penRequest);
    this.service.sendRejectedPENRequestEmail(penRequest);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> sendAdditionalInfoRequestEmail(final GMPRequestAdditionalInfoEmailEntity penRequest) {
    this.validateEmail(penRequest);
    this.service.sendAdditionalInfoEmail(penRequest);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> verifyEmail(final GMPRequestEmailVerificationEntity gmpRequestEmailVerificationEntity) {
    this.validateEmail(gmpRequestEmailVerificationEntity);
    this.service.sendVerifyEmail(gmpRequestEmailVerificationEntity);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> notifyStudentForStaleReturnedRequests(final GMPRequestAdditionalInfoEmailEntity gmpRequestAdditionalInfoEmailEntity) {
    this.service.sendStaleReturnedRequestNotificationEmail(gmpRequestAdditionalInfoEmailEntity);
    return ResponseEntity.noContent().build();
  }

  private void validateEmail(final BaseEmailEntity emailEntity) {
    val validationResult = this.emailValidator.validateEmail(emailEntity);
    if (!validationResult.isEmpty()) {
      final ApiError error = ApiError.builder().timestamp(LocalDateTime.now()).message("Payload contains invalid data.").status(BAD_REQUEST).build();
      error.addValidationErrors(validationResult);
      throw new InvalidPayloadException(error);
    }
  }

}
