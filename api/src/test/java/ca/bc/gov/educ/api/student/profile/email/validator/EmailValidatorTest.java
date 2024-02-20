package ca.bc.gov.educ.api.student.profile.email.validator;

import ca.bc.gov.educ.api.student.profile.email.struct.v1.gmpump.UMPRequestEmailVerificationEntity;
import ca.bc.gov.educ.api.student.profile.email.struct.v2.*;
import java.util.*;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;

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

  @Test
  public void testValidateEmail_givenMultipleToEmailsWithIncorrectInformation_shouldReturnErrors() {
    List<String> toEmail = List.of("username@test.ca", "username@test.ca", "a@b.c", "username@yahoo.com.");
    final EmailNotificationEntity entity = new EmailNotificationEntity("email@email.com", toEmail, "test subject", "template1", Map.of("test", "test") );
    val result = this.validator.validateEmail(entity);

    assert (result.size() == 2);
  }
}
