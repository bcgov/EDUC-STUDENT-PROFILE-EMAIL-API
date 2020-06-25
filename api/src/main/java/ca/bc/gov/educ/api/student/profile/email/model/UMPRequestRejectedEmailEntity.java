package ca.bc.gov.educ.api.student.profile.email.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
public class UMPRequestRejectedEmailEntity extends BaseEmailEntity {

  @NotNull(message = "Rejection Reason can not be null")
  private String rejectionReason;

}
