package ca.bc.gov.educ.api.student.profile.email.service;


import ca.bc.gov.educ.api.student.profile.email.exception.InvalidParameterException;
import ca.bc.gov.educ.api.student.profile.email.props.ApplicationProperties;
import ca.bc.gov.educ.api.student.profile.email.struct.v2.EmailNotificationEntity;
import ca.bc.gov.educ.api.student.profile.email.struct.v1.gmpump.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static ca.bc.gov.educ.api.student.profile.email.constants.IdentityType.BASIC;
import static ca.bc.gov.educ.api.student.profile.email.constants.IdentityType.BCSC;


@Service
@Slf4j
public class GMPEmailService {

  private static final String PERSONAL_EDUCATION_NUMBER_PEN_REQUEST = "Your Personal Education Number (PEN) Request";
  private static final String VERIFY_EMAIL_SUBJECT = "Activate your GetMyPEN request within 24 hours of receiving this email";
  private static final String FROM_EMAIL = "noreply.getmypen@gov.bc.ca";
  private static final String LOGIN_URL = "loginUrl";
  private final ApplicationProperties props;

  @Getter
  private final EmailNotificationService emailNotificationService;

  @Autowired
  public GMPEmailService(final ApplicationProperties props, final EmailNotificationService emailNotificationService) {
    this.props = props;
    this.emailNotificationService = emailNotificationService;
  }

  public void sendCompletedPENRequestEmail(final GMPRequestCompleteEmailEntity penRequest, final boolean demographicsChanged) {
    final String loginUrl = this.getLoginUrl(penRequest);
    log.debug("Sending completed PEN email");
    final var emailNotificationEntity = EmailNotificationEntity.builder()
      .fromEmail(FROM_EMAIL)
      .toEmail(penRequest.getEmailAddress())
      .subject(PERSONAL_EDUCATION_NUMBER_PEN_REQUEST)
      .templateName(demographicsChanged ? "completedRequest.demographicChange.gmp" : "completedRequest.gmp")
      .emailFields(Map.of("firstName", penRequest.getFirstName(), LOGIN_URL, loginUrl))
      .build();
    this.getEmailNotificationService().sendEmail(emailNotificationEntity);
    log.debug("Completed PEN email sent successfully");
  }


  public void sendRejectedPENRequestEmail(final GMPRequestRejectedEmailEntity penRequest) {
    final String loginUrl = this.getLoginUrl(penRequest);
    log.debug("Sending rejected PEN email");
    final var emailNotificationEntity = EmailNotificationEntity.builder()
      .fromEmail(FROM_EMAIL)
      .toEmail(penRequest.getEmailAddress())
      .subject(PERSONAL_EDUCATION_NUMBER_PEN_REQUEST)
      .templateName("rejectedRequest.gmp")
      .emailFields(Map.of("rejectionReason", penRequest.getRejectionReason(), LOGIN_URL, loginUrl))
      .build();
    this.getEmailNotificationService().sendEmail(emailNotificationEntity);
    log.debug("Rejected PEN email sent successfully");
  }

  public void sendAdditionalInfoEmail(final GMPRequestAdditionalInfoEmailEntity penRequest) {
    final String loginUrl = this.getLoginUrl(penRequest);
    log.debug("Sending additional info PEN email");
    final var emailNotificationEntity = EmailNotificationEntity.builder()
      .fromEmail(FROM_EMAIL)
      .toEmail(penRequest.getEmailAddress())
      .subject(PERSONAL_EDUCATION_NUMBER_PEN_REQUEST)
      .templateName("additionalInfoRequested.gmp")
      .emailFields(Map.of(LOGIN_URL, loginUrl))
      .build();
    this.getEmailNotificationService().sendEmail(emailNotificationEntity);
    log.debug("Additional info PEN email sent successfully");
  }

  public void sendStaleReturnedRequestNotificationEmail(final GMPRequestAdditionalInfoEmailEntity penRequest) {
    final String loginUrl = this.getLoginUrl(penRequest);
    log.debug("Sending sendStaleReturnedRequestNotificationEmail info GMP email");
    final var emailNotificationEntity = EmailNotificationEntity.builder()
      .fromEmail(FROM_EMAIL)
      .toEmail(penRequest.getEmailAddress())
      .subject(PERSONAL_EDUCATION_NUMBER_PEN_REQUEST)
      .templateName("notify.stale.return.gmp")
      .emailFields(Map.of(LOGIN_URL, loginUrl))
      .build();
    this.getEmailNotificationService().sendEmail(emailNotificationEntity);
    log.debug("Stale Return Notification GMP email sent successfully");
  }

  /**
   * This method is responsible to send verification email.
   * the replacement starts with index 0 , so there are eight replacements in the template.
   *
   * @param emailVerificationEntity the payload containing the pen request id and email.
   */
  public void sendVerifyEmail(final GMPRequestEmailVerificationEntity emailVerificationEntity) {
    log.debug("sending verify email.");
    final var emailNotificationEntity = EmailNotificationEntity.builder()
      .fromEmail(FROM_EMAIL)
      .toEmail(emailVerificationEntity.getEmailAddress())
      .subject(VERIFY_EMAIL_SUBJECT)
      .templateName("verifyEmail.gmp")
      .emailFields(Map.of("identityTypeLabel", emailVerificationEntity.getIdentityTypeLabel(), "verificationUrl", emailVerificationEntity.getVerificationUrl(), "jwtToken", emailVerificationEntity.getJwtToken()))
      .build();

    this.getEmailNotificationService().sendEmail(emailNotificationEntity);
    log.debug("verification email sent successfully.");
  }


  private String getLoginUrl(final BaseEmailEntity baseEmailEntity) {
    if (BCSC.toString().equalsIgnoreCase(baseEmailEntity.getIdentityType())) {
      return this.props.getLoginBcscGMP();
    } else if (BASIC.toString().equalsIgnoreCase(baseEmailEntity.getIdentityType())) {
      return this.props.getLoginBasicGMP();
    } else {
      throw new InvalidParameterException("IdentityType provided, could not be resolved. :: " + baseEmailEntity.getIdentityType());
    }

  }
}
