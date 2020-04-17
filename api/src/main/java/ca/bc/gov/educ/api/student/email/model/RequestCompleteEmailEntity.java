package ca.bc.gov.educ.api.student.email.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestCompleteEmailEntity extends BaseEmailEntity {

  @NotNull(message = "First Name can not be null")
  private String firstName;

}
