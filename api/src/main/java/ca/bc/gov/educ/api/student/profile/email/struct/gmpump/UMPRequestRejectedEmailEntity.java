package ca.bc.gov.educ.api.student.profile.email.struct.gmpump;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UMPRequestRejectedEmailEntity extends BaseEmailEntity {

  @NotNull(message = "Rejection Reason can not be null")
  private String rejectionReason;

}
