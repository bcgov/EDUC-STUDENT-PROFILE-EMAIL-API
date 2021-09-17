package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.exception.InvalidParameterException;
import ca.bc.gov.educ.api.student.profile.email.props.ApplicationProperties;
import ca.bc.gov.educ.api.student.profile.email.struct.EmailNotificationEntity;
import ca.bc.gov.educ.api.student.profile.email.struct.gmpump.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static ca.bc.gov.educ.api.student.profile.email.constants.IdentityType.BASIC;
import static ca.bc.gov.educ.api.student.profile.email.constants.IdentityType.BCSC;
import static lombok.AccessLevel.PRIVATE;

@Service
@Slf4j
public class UMPEmailService {

  private static final String STUDENT_PROFILE_REQUEST = "Your Personal Education Number(PEN) Info Update Request";
  private static final String VERIFY_EMAIL_SUBJECT = "Activate your UpdateMyPENInfo request within 24 hours of receiving this email";
  private static final String FROM_EMAIL = "noreply.getmypen@gov.bc.ca";
  private static final String LOGIN_URL = "loginUrl";
  private final ApplicationProperties props;
  @Getter(PRIVATE)
  private final EmailNotificationService emailNotificationService;

  @Autowired
  public UMPEmailService(final ApplicationProperties props, final EmailNotificationService emailNotificationService) {
    this.props = props;
    this.emailNotificationService = emailNotificationService;
  }

  public void sendCompletedRequestEmail(final UMPRequestCompleteEmailEntity email) {
    final String loginUrl = this.getLoginUrl(email);
    log.debug("Sending completed UMP email");
    final var emailNotificationEntity = EmailNotificationEntity.builder()
      .fromEmail(FROM_EMAIL)
      .toEmail(email.getEmailAddress())
      .subject(STUDENT_PROFILE_REQUEST)
      .templateName("completedRequest.ump")
      .emailFields(Map.of("firstName", email.getFirstName(), LOGIN_URL, loginUrl))
      .build();
    this.getEmailNotificationService().sendEmail(emailNotificationEntity);
    log.debug("Completed UMP email sent successfully");
  }


  public void sendRejectedRequestEmail(final UMPRequestRejectedEmailEntity email) {
    final String loginUrl = this.getLoginUrl(email);
    log.debug("Sending rejected UMP email");
    final var emailNotificationEntity = EmailNotificationEntity.builder()
      .fromEmail(FROM_EMAIL)
      .toEmail(email.getEmailAddress())
      .subject(STUDENT_PROFILE_REQUEST)
      .templateName("rejectedRequest.ump")
      .emailFields(Map.of("rejectionReason", email.getRejectionReason(), LOGIN_URL, loginUrl))
      .build();
    this.getEmailNotificationService().sendEmail(emailNotificationEntity);
    log.debug("Rejected UMP email sent successfully");
  }

  public void sendAdditionalInfoEmail(final UMPAdditionalInfoEmailEntity email) {
    final String loginUrl = this.getLoginUrl(email);
    log.debug("Sending additional info UMP email");
    final var emailNotificationEntity = EmailNotificationEntity.builder()
      .fromEmail(FROM_EMAIL)
      .toEmail(email.getEmailAddress())
      .subject(STUDENT_PROFILE_REQUEST)
      .templateName("additionalInfoRequested.ump")
      .emailFields(Map.of(LOGIN_URL, loginUrl))
      .build();
    this.getEmailNotificationService().sendEmail(emailNotificationEntity);
    log.debug("Additional info UMP email sent successfully");
  }
  public void sendStaleReturnedRequestNotificationEmail(final UMPAdditionalInfoEmailEntity emailEntity) {
    final String loginUrl = this.getLoginUrl(emailEntity);
    log.debug("Sending sendStaleReturnedRequestNotificationEmail info UMP email");
    final var emailNotificationEntity = EmailNotificationEntity.builder()
      .fromEmail(FROM_EMAIL)
      .toEmail(emailEntity.getEmailAddress())
      .subject(STUDENT_PROFILE_REQUEST)
      .templateName("notify.stale.return.ump")
      .emailFields(Map.of(LOGIN_URL, loginUrl))
      .build();
    this.getEmailNotificationService().sendEmail(emailNotificationEntity);
    log.debug("Stale Return Notification UMP email sent successfully");
  }

  /**
   * This method is responsible to send verification email.
   * the replacement starts with index 0 , so there are eight replacements in the template.
   *
   * @param emailVerificationEntity the payload containing the pen request id and email.
   */
  public void sendVerifyEmail(final UMPRequestEmailVerificationEntity emailVerificationEntity) {
    log.debug("sending verify email for UMP.");
    final var emailNotificationEntity = EmailNotificationEntity.builder()
      .fromEmail(FROM_EMAIL)
      .toEmail(emailVerificationEntity.getEmailAddress())
      .subject(VERIFY_EMAIL_SUBJECT)
      .templateName("verifyEmail.ump")
      .emailFields(Map.of("identityTypeLabel", emailVerificationEntity.getIdentityTypeLabel(), "verificationUrl", emailVerificationEntity.getVerificationUrl(), "jwtToken", emailVerificationEntity.getJwtToken()))
      .build();
    this.getEmailNotificationService().sendEmail(emailNotificationEntity);
    log.debug("verification email sent successfully for UMP.");
  }


  private String getLoginUrl(final BaseEmailEntity baseEmailEntity) {
    if (BCSC.toString().equalsIgnoreCase(baseEmailEntity.getIdentityType())) {
      return this.props.getLoginBcscUMP();
    } else if (BASIC.toString().equalsIgnoreCase(baseEmailEntity.getIdentityType())) {
      return this.props.getLoginBasicUMP();
    } else {
      throw new InvalidParameterException("IdentityType provided, could not be resolved. :: " + baseEmailEntity.getIdentityType());
    }

  }
}
