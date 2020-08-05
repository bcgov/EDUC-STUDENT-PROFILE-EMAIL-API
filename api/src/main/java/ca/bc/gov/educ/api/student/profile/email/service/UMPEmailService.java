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
public class UMPEmailService {

  private static final String STUDENT_PROFILE_REQUEST = "Your Personal Education Number(PEN) Info Update Request";
  private static final String VERIFY_EMAIL_SUBJECT = "Activate your UpdateMyPENInfo request within 24 hours of receiving this email";
  private final ApplicationProperties props;
  @Getter(PRIVATE)
  private final CHESEmailService chesService;

  @Autowired
  public UMPEmailService(final ApplicationProperties props, final CHESEmailService chesService) {
    this.props = props;
    this.chesService = chesService;
  }

  public void sendCompletedRequestEmail(UMPRequestCompleteEmailEntity email) {
    String loginUrl = getLoginUrl(email);
    log.debug("Sending completed UMP email");
    getChesService().sendEmail(email, MessageFormat.format(props.getEmailTemplateCompletedRequestUMP().replace("'", "''"), email.getFirstName(), loginUrl, loginUrl, loginUrl), STUDENT_PROFILE_REQUEST);
    log.debug("Completed UMP email sent successfully");
  }


  public void sendRejectedRequestEmail(UMPRequestRejectedEmailEntity email) {
    String loginUrl = getLoginUrl(email);
    log.debug("Sending rejected UMP email");
    getChesService().sendEmail(email, MessageFormat.format(props.getEmailTemplateRejectedRequestUMP().replace("'", "''"), email.getRejectionReason(), loginUrl, loginUrl, loginUrl), STUDENT_PROFILE_REQUEST);
    log.debug("Rejected UMP email sent successfully");
  }

  public void sendAdditionalInfoEmail(UMPAdditionalInfoEmailEntity email) {
    String loginUrl = getLoginUrl(email);
    log.debug("Sending additional info UMP email");
    getChesService().sendEmail(email, MessageFormat.format(props.getEmailTemplateAdditionalInfoUMP().replace("'", "''"), loginUrl, loginUrl, loginUrl), STUDENT_PROFILE_REQUEST);
    log.debug("Additional info UMP email sent successfully");
  }
  public void sendStaleReturnedRequestNotificationEmail(UMPAdditionalInfoEmailEntity emailEntity) {
    String loginUrl = getLoginUrl(emailEntity);
    log.debug("Sending sendStaleReturnedRequestNotificationEmail info UMP email");
    getChesService().sendEmail(emailEntity, MessageFormat.format(props.getEmailTemplateNotifyStaleReturnUMP().replace("'", "''"), loginUrl, loginUrl, loginUrl), STUDENT_PROFILE_REQUEST);
    log.debug("Stale Return Notification UMP email sent successfully");
  }

  /**
   * This method is responsible to send verification email.
   * the replacement starts with index 0 , so there are eight replacements in the template.
   *
   * @param emailVerificationEntity the payload containing the pen request id and email.
   */
  public void sendVerifyEmail(UMPRequestEmailVerificationEntity emailVerificationEntity) {
    log.debug("sending verify email for UMP.");
    final String emailBody = MessageFormat.format(props.getEmailTemplateVerifyEmailUMP().replace("'", "''"),
        emailVerificationEntity.getIdentityTypeLabel(), emailVerificationEntity.getVerificationUrl(), emailVerificationEntity.getJwtToken(),
        emailVerificationEntity.getIdentityTypeLabel(), emailVerificationEntity.getVerificationUrl(), emailVerificationEntity.getJwtToken(),
        emailVerificationEntity.getVerificationUrl(), emailVerificationEntity.getJwtToken());
    getChesService().sendEmail(emailVerificationEntity, emailBody, VERIFY_EMAIL_SUBJECT);
    log.debug("verification email sent successfully for UMP.");
  }


  private String getLoginUrl(BaseEmailEntity baseEmailEntity) {
    if (BCSC.toString().equalsIgnoreCase(baseEmailEntity.getIdentityType())) {
      return props.getLoginBcscUMP();
    } else if (BASIC.toString().equalsIgnoreCase(baseEmailEntity.getIdentityType())) {
      return props.getLoginBasicUMP();
    } else {
      throw new InvalidParameterException("IdentityType provided, could not be resolved. :: " + baseEmailEntity.getIdentityType());
    }

  }
}
