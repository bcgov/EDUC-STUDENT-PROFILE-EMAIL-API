package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.exception.InvalidParameterException;
import ca.bc.gov.educ.api.student.profile.email.model.*;
import ca.bc.gov.educ.api.student.profile.email.props.ApplicationProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

import static ca.bc.gov.educ.api.student.profile.email.constants.IdentityType.BASIC;
import static ca.bc.gov.educ.api.student.profile.email.constants.IdentityType.BCSC;
import static lombok.AccessLevel.PRIVATE;

@Service
@Slf4j
public class StudentEmailService {

  private static final String PERSONAL_EDUCATION_NUMBER_PEN_REQUEST = "Your Personal Education Number (PEN) Request";
  private static final String VERIFY_EMAIL_SUBJECT = "Activate your GetMyPEN request within 24 hours of receiving this email";
  private final ApplicationProperties props;
  @Getter(PRIVATE)
  private final CHESEmailService chesService;

  @Autowired
  public StudentEmailService(final ApplicationProperties props, final CHESEmailService chesService) {
    this.props = props;
    this.chesService = chesService;
  }

  public void sendCompletedRequestEmail(RequestCompleteEmailEntity email) {
    String loginUrl = getLoginUrl(email);
    log.debug("Sending completed PEN email");
    getChesService().sendEmail(email, MessageFormat.format(props.getEmailTemplateCompletedRequest().replace("'", "''"), email.getFirstName(), loginUrl, loginUrl, loginUrl), PERSONAL_EDUCATION_NUMBER_PEN_REQUEST);
    log.debug("Completed PEN email sent successfully");
  }


  public void sendRejectedRequestEmail(RequestRejectedEmailEntity email) {
    String loginUrl = getLoginUrl(email);
    log.debug("Sending rejected PEN email");
    getChesService().sendEmail(email, MessageFormat.format(props.getEmailTemplateRejectedRequest().replace("'", "''"), email.getRejectionReason(), loginUrl, loginUrl, loginUrl), PERSONAL_EDUCATION_NUMBER_PEN_REQUEST);
    log.debug("Rejected PEN email sent successfully");
  }

  public void sendAdditionalInfoEmail(RequestAdditionalInfoEmailEntity email) {
    String loginUrl = getLoginUrl(email);
    log.debug("Sending additional info PEN email");
    getChesService().sendEmail(email, MessageFormat.format(props.getEmailTemplateAdditionalInfo().replace("'", "''"), loginUrl, loginUrl, loginUrl), PERSONAL_EDUCATION_NUMBER_PEN_REQUEST);
    log.debug("Additional info PEN email sent successfully");
  }

  /**
   * This method is responsible to send verification email.
   * the replacement starts with index 0 , so there are eight replacements in the template.
   *
   * @param emailVerificationEntity the payload containing the pen request id and email.
   */
  public void sendVerifyEmail(RequestEmailVerificationEntity emailVerificationEntity) {
    log.debug("sending verify email.");
    final String emailBody = MessageFormat.format(props.getEmailTemplateVerifyEmail().replace("'", "''"),
        emailVerificationEntity.getIdentityTypeLabel(), emailVerificationEntity.getVerificationUrl(), emailVerificationEntity.getJwtToken(),
        emailVerificationEntity.getIdentityTypeLabel(), emailVerificationEntity.getVerificationUrl(), emailVerificationEntity.getJwtToken(),
        emailVerificationEntity.getVerificationUrl(), emailVerificationEntity.getJwtToken());
    getChesService().sendEmail(emailVerificationEntity, emailBody, VERIFY_EMAIL_SUBJECT);
    log.debug("verification email sent successfully.");
  }


  private String getLoginUrl(BaseEmailEntity baseEmailEntity) {
    if (BCSC.toString().equalsIgnoreCase(baseEmailEntity.getIdentityType())) {
      return props.getLoginBcsc();
    } else if (BASIC.toString().equalsIgnoreCase(baseEmailEntity.getIdentityType())) {
      return props.getLoginBasic();
    } else {
      throw new InvalidParameterException("IdentityType provided, could not be resolved. :: " + baseEmailEntity.getIdentityType());
    }

  }
}
