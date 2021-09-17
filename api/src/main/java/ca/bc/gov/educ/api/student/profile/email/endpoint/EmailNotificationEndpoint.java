package ca.bc.gov.educ.api.student.profile.email.endpoint;

import ca.bc.gov.educ.api.student.profile.email.struct.EmailNotificationEntity;
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

@RequestMapping("/")
@Tag(name = "Email Notification")
@OpenAPIDefinition(info = @Info(title = "API for Student Profile Email.", description = "This API is responsible for sending different type of email notification to students, schools or Helpdesk.", version = "1"), security = {@SecurityRequirement(name = "OAUTH2", scopes = {"SEND_STUDENT_PROFILE_EMAIL"})})
public interface EmailNotificationEndpoint {
  @PostMapping("/send-email")
  @PreAuthorize("hasAuthority('SCOPE_SEND_STUDENT_PROFILE_EMAIL')")
  @Operation(description = "send additional info request email", responses = {@ApiResponse(responseCode = "204", description = "NO CONTENT"), @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR.")})
  ResponseEntity<Void> sendEmail(@Validated @RequestBody EmailNotificationEntity emailNotificationEntity);
}
