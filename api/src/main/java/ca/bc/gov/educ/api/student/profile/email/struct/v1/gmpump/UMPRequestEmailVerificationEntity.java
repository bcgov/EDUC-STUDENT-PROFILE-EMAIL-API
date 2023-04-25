package ca.bc.gov.educ.api.student.profile.email.struct.v1.gmpump;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import jakarta.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString(callSuper = true)
public class UMPRequestEmailVerificationEntity extends BaseEmailEntity {
  @NotNull(message = "studentRequestId can not be null.")
  private String studentRequestId;
  @NotNull(message = "Identity Type Label can not be null.")
  private String identityTypeLabel;
  @NotNull(message = "verificationUrl can not be null.")
  private String verificationUrl; // this holds the url link.
  @NotNull(message = "jwt Token can not be null.")
  @ToString.Exclude
  private String jwtToken;
}
