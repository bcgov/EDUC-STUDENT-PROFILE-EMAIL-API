package ca.bc.gov.educ.api.student.profile.email.controller.v1;

import ca.bc.gov.educ.api.student.profile.email.endpoint.v1.UpdateMyProfileEmailEndpoint;
import ca.bc.gov.educ.api.student.profile.email.exception.InvalidPayloadException;
import ca.bc.gov.educ.api.student.profile.email.exception.errors.ApiError;
import ca.bc.gov.educ.api.student.profile.email.service.UMPEmailService;
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
public class UpdateMyProfileEmailController implements UpdateMyProfileEmailEndpoint {

  private final UMPEmailService service;
  private final EmailValidator emailValidator;

  @Autowired
  UpdateMyProfileEmailController(final UMPEmailService penRequest, final EmailValidator emailValidator) {
    this.service = penRequest;
    this.emailValidator = emailValidator;
  }

  @Override
  public ResponseEntity<Void> sendCompletedRequestEmail(final UMPRequestCompleteEmailEntity request) {
    this.validateEmail(request);
    this.service.sendCompletedRequestEmail(request);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> sendRejectedRequestEmail(final UMPRequestRejectedEmailEntity request) {
    this.validateEmail(request);
    this.service.sendRejectedRequestEmail(request);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> sendAdditionalInfoRequestEmail(final UMPAdditionalInfoEmailEntity request) {
    this.validateEmail(request);
    this.service.sendAdditionalInfoEmail(request);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> verifyEmail(final UMPRequestEmailVerificationEntity request) {
    this.validateEmail(request);
    this.service.sendVerifyEmail(request);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> notifyStudentForStaleReturnedRequests(final UMPAdditionalInfoEmailEntity umpAdditionalInfoEmailEntity) {
    this.service.sendStaleReturnedRequestNotificationEmail(umpAdditionalInfoEmailEntity);
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
