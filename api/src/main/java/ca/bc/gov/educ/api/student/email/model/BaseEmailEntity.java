package ca.bc.gov.educ.api.student.email.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Setter
@Getter
public abstract class BaseEmailEntity {

  @NotNull(message = "Email address can not be null.")
  @Email(message = "Email address should be a valid email address")
  private String emailAddress;
  /**
   * This holds the identity type code of the student , BASIC or BCSC or PERSONAL
   */
  private String identityType;
}
