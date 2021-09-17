package ca.bc.gov.educ.api.student.profile.email.struct.v1.gmpump;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString(callSuper = true)
public class GMPRequestRejectedEmailEntity extends BaseEmailEntity {

  @NotNull(message = "Rejection Reason can not be null")
  private String rejectionReason;

}
