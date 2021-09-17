package ca.bc.gov.educ.api.student.profile.email.validator;

import ca.bc.gov.educ.api.student.profile.email.struct.gmpump.UMPRequestEmailVerificationEntity;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

@RunWith(JUnitParamsRunner.class)
public class EmailValidatorTest {
  private final EmailValidator validator = new EmailValidator(Map.of("template1", "test template"));

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
  public void testValidateEmail_givenDifferentInputs_shouldReturnExpectedResult(final String email, final int expectedErrorSize) {
    final UMPRequestEmailVerificationEntity entity = new UMPRequestEmailVerificationEntity("123", "BCSC", "", "");
    entity.setEmailAddress(email);
    val result = this.validator.validateEmail(entity);
    assert (result.size() == expectedErrorSize);
  }
}
