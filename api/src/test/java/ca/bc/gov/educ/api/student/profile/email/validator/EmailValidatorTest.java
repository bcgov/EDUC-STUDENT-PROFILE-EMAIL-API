package ca.bc.gov.educ.api.student.profile.email.validator;

import ca.bc.gov.educ.api.student.profile.email.model.RequestEmailVerificationEntity;
import lombok.val;
import org.junit.Test;

public class EmailValidatorTest {
  private final EmailValidator validator = new EmailValidator();

  @Test
  public void testValidateEmail_GivenInvalidEmail_ShouldAddAnErrorToTheList() {
    RequestEmailVerificationEntity entity = new RequestEmailVerificationEntity("123", "BCSC");
    entity.setEmailAddress("a@b.c");
    val result = validator.validateEmail(entity);
    assert (result.size() == 1);
  }

  @Test
  public void testValidateEmail1_GivenInvalidEmail_ShouldAddAnErrorToTheList() {
    RequestEmailVerificationEntity entity = new RequestEmailVerificationEntity("123", "BCSC");
    entity.setEmailAddress(".username@yahoo.com");
    val result = validator.validateEmail(entity);
    assert (result.size() == 1);
  }

  @Test
  public void testValidateEmail2_GivenInvalidEmail_ShouldAddAnErrorToTheList() {
    RequestEmailVerificationEntity entity = new RequestEmailVerificationEntity("123", "BCSC");
    entity.setEmailAddress("username@yahoo.com.");
    val result = validator.validateEmail(entity);
    assert (result.size() == 1);
  }
  @Test
  public void testValidateEmail2_GivenValidEmail_ShouldNotAddAnErrorToTheList() {
    RequestEmailVerificationEntity entity = new RequestEmailVerificationEntity("123", "BCSC");
    entity.setEmailAddress("username@yahoo.com");
    val result = validator.validateEmail(entity);
    assert (result.size() == 0);
  }
  @Test
  public void testValidateEmail3_GivenValidEmail_ShouldNotAddAnErrorToTheList() {
    RequestEmailVerificationEntity entity = new RequestEmailVerificationEntity("123", "BCSC");
    entity.setEmailAddress("username@test.ca");
    val result = validator.validateEmail(entity);
    assert (result.size() == 0);
  }
  @Test
  public void testValidateEmail3_GivenInValidEmail_ShouldNotAddAnErrorToTheList() {
    RequestEmailVerificationEntity entity = new RequestEmailVerificationEntity("123", "BCSC");
    entity.setEmailAddress("username@yahoo..com");
    val result = validator.validateEmail(entity);
    assert (result.size() == 1);
  }

  @Test
  public void testValidateEmail4_GivenInValidEmail_ShouldNotAddAnErrorToTheList() {
    RequestEmailVerificationEntity entity = new RequestEmailVerificationEntity("123", "BCSC");
    entity.setEmailAddress("1@2.3");
    val result = validator.validateEmail(entity);
    assert (result.size() == 1);
  }

  @Test
  public void testValidateEmail5_GivenValidEmail_ShouldNotAddAnErrorToTheList() {
    RequestEmailVerificationEntity entity = new RequestEmailVerificationEntity("123", "BCSC");
    entity.setEmailAddress("1@gov.bc.ca");
    val result = validator.validateEmail(entity);
    assert (result.size() == 0);
  }
}
