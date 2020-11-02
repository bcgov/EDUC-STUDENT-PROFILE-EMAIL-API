package ca.bc.gov.educ.api.student.profile.email.validator;

import ca.bc.gov.educ.api.student.profile.email.model.UMPRequestEmailVerificationEntity;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class EmailValidatorTest {
  private final EmailValidator validator = new EmailValidator();

  @Test
  @Parameters({
      "a@b.c, 1",
      ".username@yahoo.com,1",
      "username@yahoo.com.,1",
      "username@yahoo..com,1",
      "1@2.3,1",
      "1@gov.bc.ca,0",
      "username@test.ca,0",
      "username@yahoo.com,0",
  })
  public void testValidateEmail_givenDifferentInputs_shouldReturnExpectedResult(String email, int expectedErrorSize) {
    UMPRequestEmailVerificationEntity entity = new UMPRequestEmailVerificationEntity("123", "BCSC", "", "");
    entity.setEmailAddress(email);
    val result = validator.validateEmail(entity);
    assert (result.size() == expectedErrorSize);
  }
}
