package ca.bc.gov.educ.api.student.profile.email.service;


import ca.bc.gov.educ.api.student.profile.email.exception.InvalidParameterException;
import ca.bc.gov.educ.api.student.profile.email.props.ApplicationProperties;
import ca.bc.gov.educ.api.student.profile.email.struct.gmpump.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

import static ca.bc.gov.educ.api.student.profile.email.constants.IdentityType.BASIC;
import static ca.bc.gov.educ.api.student.profile.email.constants.IdentityType.BCSC;


@Service
@Slf4j
public class GMPEmailService {

  private static final String PERSONAL_EDUCATION_NUMBER_PEN_REQUEST = "Your Personal Education Number (PEN) Request";
  private static final String VERIFY_EMAIL_SUBJECT = "Activate your GetMyPEN request within 24 hours of receiving this email";
  private final ApplicationProperties props;

  @Getter
  private final CHESEmailService chesEmailService;

  @Autowired
  public GMPEmailService(final ApplicationProperties props, final CHESEmailService chesEmailService) {
    this.props = props;
    this.chesEmailService = chesEmailService;
  }

  public void sendCompletedPENRequestEmail(final GMPRequestCompleteEmailEntity penRequest, final boolean demographicsChanged) {
    final String loginUrl = this.getLoginUrl(penRequest);
    log.debug("Sending completed PEN email");
    if (demographicsChanged) {
      this.getChesEmailService().sendEmail(penRequest, MessageFormat.format(this.props.getEmailTemplateCompleteRequestDemographicChangeGMP().replace("'", "''"), penRequest.getFirstName(), loginUrl, loginUrl, loginUrl), PERSONAL_EDUCATION_NUMBER_PEN_REQUEST);
    } else {
      this.getChesEmailService().sendEmail(penRequest, MessageFormat.format(this.props.getEmailTemplateCompletedRequestGMP().replace("'", "''"), penRequest.getFirstName(), loginUrl, loginUrl, loginUrl), PERSONAL_EDUCATION_NUMBER_PEN_REQUEST);
    }
    log.debug("Completed PEN email sent successfully");
  }


  public void sendRejectedPENRequestEmail(final GMPRequestRejectedEmailEntity penRequest) {
    final String loginUrl = this.getLoginUrl(penRequest);
    log.debug("Sending rejected PEN email");
    this.getChesEmailService().sendEmail(penRequest, MessageFormat.format(this.props.getEmailTemplateRejectedRequestGMP().replace("'", "''"), penRequest.getRejectionReason(), loginUrl, loginUrl, loginUrl), PERSONAL_EDUCATION_NUMBER_PEN_REQUEST);
    log.debug("Rejected PEN email sent successfully");
  }

  public void sendAdditionalInfoEmail(final GMPRequestAdditionalInfoEmailEntity penRequest) {
    final String loginUrl = this.getLoginUrl(penRequest);
    log.debug("Sending additional info PEN email");
    this.getChesEmailService().sendEmail(penRequest, MessageFormat.format(this.props.getEmailTemplateAdditionalInfoGMP().replace("'", "''"), loginUrl, loginUrl, loginUrl), PERSONAL_EDUCATION_NUMBER_PEN_REQUEST);
    log.debug("Additional info PEN email sent successfully");
  }

  public void sendStaleReturnedRequestNotificationEmail(final GMPRequestAdditionalInfoEmailEntity penRequest) {
    final String loginUrl = this.getLoginUrl(penRequest);
    log.debug("Sending sendStaleReturnedRequestNotificationEmail info GMP email");
    this.getChesEmailService().sendEmail(penRequest, MessageFormat.format(this.props.getEmailTemplateNotifyStaleReturnGMP().replace("'", "''"), loginUrl, loginUrl, loginUrl), PERSONAL_EDUCATION_NUMBER_PEN_REQUEST);
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
    final String emailBody = MessageFormat.format(this.props.getEmailTemplateVerifyEmailGMP().replace("'", "''"),
        emailVerificationEntity.getIdentityTypeLabel(), emailVerificationEntity.getVerificationUrl(), emailVerificationEntity.getJwtToken(),
        emailVerificationEntity.getIdentityTypeLabel(), emailVerificationEntity.getVerificationUrl(), emailVerificationEntity.getJwtToken(),
        emailVerificationEntity.getVerificationUrl(), emailVerificationEntity.getJwtToken());
    this.getChesEmailService().sendEmail(emailVerificationEntity, emailBody, VERIFY_EMAIL_SUBJECT);
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
