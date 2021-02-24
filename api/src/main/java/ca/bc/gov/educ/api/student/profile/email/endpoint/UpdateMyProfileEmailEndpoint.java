package ca.bc.gov.educ.api.student.profile.email.endpoint;

import ca.bc.gov.educ.api.student.profile.email.struct.gmpump.UMPAdditionalInfoEmailEntity;
import ca.bc.gov.educ.api.student.profile.email.struct.gmpump.UMPRequestCompleteEmailEntity;
import ca.bc.gov.educ.api.student.profile.email.struct.gmpump.UMPRequestEmailVerificationEntity;
import ca.bc.gov.educ.api.student.profile.email.struct.gmpump.UMPRequestRejectedEmailEntity;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/ump")
@Tag(name = "Update My Pen Email")
@OpenAPIDefinition(info = @Info(title = "API for Student Profile Email.", description = "This API is responsible for sending different type of email notification to students for both GMP and UMP application.", version = "1"), security = {@SecurityRequirement(name = "OAUTH2", scopes = {"SEND_STUDENT_PROFILE_EMAIL"})})
public interface UpdateMyProfileEmailEndpoint {

  @PostMapping("/complete")
  @PreAuthorize("hasAuthority('SCOPE_SEND_STUDENT_PROFILE_EMAIL')")
  @Operation(description = "send complete email", method = "sendCompletedRequestEmail", responses = {@ApiResponse(responseCode = "204", description = "NO CONTENT"), @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR.")})
  ResponseEntity<Void> sendCompletedRequestEmail(@Validated @RequestBody UMPRequestCompleteEmailEntity umpRequestCompleteEmailEntity);

  @PostMapping("/reject")
  @PreAuthorize("hasAuthority('SCOPE_SEND_STUDENT_PROFILE_EMAIL')")
  @Operation(description = "send reject notification email", responses = {@ApiResponse(responseCode = "204", description = "NO CONTENT"), @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR.")})
  ResponseEntity<Void> sendRejectedRequestEmail(@Validated @RequestBody UMPRequestRejectedEmailEntity umpRequestRejectedEmailEntity);

  @PostMapping("/info")
  @PreAuthorize("hasAuthority('SCOPE_SEND_STUDENT_PROFILE_EMAIL')")
  @Operation(description = "send additional info request email", responses = {@ApiResponse(responseCode = "204", description = "NO CONTENT"), @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR.")})
  ResponseEntity<Void> sendAdditionalInfoRequestEmail(@Validated @RequestBody UMPAdditionalInfoEmailEntity umpAdditionalInfoEmailEntity);

  @PostMapping("/verify")
  @PreAuthorize("hasAuthority('SCOPE_SEND_STUDENT_PROFILE_EMAIL')")
  @Operation(description = "verify student email", responses = {@ApiResponse(responseCode = "204", description = "NO CONTENT"), @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR.")})
  ResponseEntity<Void> verifyEmail(@Validated @RequestBody UMPRequestEmailVerificationEntity umpRequestEmailVerificationEntity);

  @PostMapping("/notify-stale-return")
  @PreAuthorize("hasAuthority('SCOPE_SEND_STUDENT_PROFILE_EMAIL')")
  @Operation(description = "send additional info request email", responses = {@ApiResponse(responseCode = "204", description = "NO CONTENT"), @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR.")})
  ResponseEntity<Void> notifyStudentForStaleReturnedRequests(@Validated @RequestBody UMPAdditionalInfoEmailEntity umpAdditionalInfoEmailEntity);

}
