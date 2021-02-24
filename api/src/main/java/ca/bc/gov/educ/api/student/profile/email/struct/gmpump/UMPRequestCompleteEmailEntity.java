package ca.bc.gov.educ.api.student.profile.email.struct.gmpump;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UMPRequestCompleteEmailEntity extends BaseEmailEntity {

  @NotNull(message = "First Name can not be null")
  private String firstName;

}
