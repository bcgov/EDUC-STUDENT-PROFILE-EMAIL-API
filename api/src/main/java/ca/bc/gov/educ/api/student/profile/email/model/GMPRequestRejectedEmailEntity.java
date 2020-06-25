package ca.bc.gov.educ.api.student.profile.email.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GMPRequestRejectedEmailEntity extends BaseEmailEntity {

  @NotNull(message = "Rejection Reason can not be null")
  private String rejectionReason;

}
