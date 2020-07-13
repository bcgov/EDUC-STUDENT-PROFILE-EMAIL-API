package ca.bc.gov.educ.api.student.profile.email.endpoint;

import ca.bc.gov.educ.api.student.profile.email.model.GMPRequestAdditionalInfoEmailEntity;
import ca.bc.gov.educ.api.student.profile.email.model.GMPRequestCompleteEmailEntity;
import ca.bc.gov.educ.api.student.profile.email.model.GMPRequestEmailVerificationEntity;
import ca.bc.gov.educ.api.student.profile.email.model.GMPRequestRejectedEmailEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/gmp")
@Tag(name = "Get My Pen Email")
public interface GetMyPenEmailEndpoint {

  @PostMapping("/complete")
  @PreAuthorize("#oauth2.hasScope('SEND_STUDENT_PROFILE_EMAIL')")
  @Operation(description = "send complete email", method = "sendCompletedPENRequestEmail", responses = {@ApiResponse(responseCode = "204", description = "NO CONTENT"), @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR.")})
  ResponseEntity<Void> sendCompletedPENRequestEmail(@Validated @RequestBody GMPRequestCompleteEmailEntity gmpRequestCompleteEmailEntity, @RequestParam("demographicsChanged") boolean demographicsChanged);

  @PostMapping("/reject")
  @PreAuthorize("#oauth2.hasScope('SEND_STUDENT_PROFILE_EMAIL')")
  @Operation(description = "send reject notification email", responses = {@ApiResponse(responseCode = "204", description = "NO CONTENT"), @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR.")})
  ResponseEntity<Void> sendRejectedPENRequestEmail(@Validated @RequestBody GMPRequestRejectedEmailEntity gmpRequestRejectedEmailEntity);

  @PostMapping("/info")
  @PreAuthorize("#oauth2.hasScope('SEND_STUDENT_PROFILE_EMAIL')")
  @Operation(description = "send additional info request email", responses = {@ApiResponse(responseCode = "204", description = "NO CONTENT"), @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR.")})
  ResponseEntity<Void> sendAdditionalInfoRequestEmail(@Validated @RequestBody GMPRequestAdditionalInfoEmailEntity gmpRequestAdditionalInfoEmailEntity);

  @PostMapping("/verify")
  @PreAuthorize("#oauth2.hasScope('SEND_STUDENT_PROFILE_EMAIL')")
  @Operation(description = "verify student email", responses = {@ApiResponse(responseCode = "204", description = "NO CONTENT"), @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR.")})
  ResponseEntity<Void> verifyEmail(@Validated @RequestBody GMPRequestEmailVerificationEntity gmpRequestEmailVerificationEntity);

  @PostMapping("/notify-stale-return")
  @PreAuthorize("#oauth2.hasScope('SEND_STUDENT_PROFILE_EMAIL')")
  @Operation(description = "notifies student that their request is in return state for a long time and they have to action it.", responses = {@ApiResponse(responseCode = "204", description = "NO CONTENT"), @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR.")})
  ResponseEntity<Void> notifyStudentForStaleReturnedRequests(@Validated @RequestBody GMPRequestAdditionalInfoEmailEntity gmpRequestAdditionalInfoEmailEntity);
}
