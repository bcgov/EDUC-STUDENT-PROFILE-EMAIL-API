package ca.bc.gov.educ.api.student.profile.email.struct.gmpump;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString(callSuper = true)
public class GMPRequestEmailVerificationEntity extends BaseEmailEntity {
  @NotNull(message = "Pen Request ID can not be null.")
  private String penRequestId;
  @NotNull(message = "Identity Type Label can not be null.")
  private String identityTypeLabel;
  @NotNull(message = "verificationUrl can not be null.")
  private String verificationUrl; // this holds the url link.
  @NotNull(message = "jwt Token can not be null.")
  @ToString.Exclude
  private String jwtToken;
}
