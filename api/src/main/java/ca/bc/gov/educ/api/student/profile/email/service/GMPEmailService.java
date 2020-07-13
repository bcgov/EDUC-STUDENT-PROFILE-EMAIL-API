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


@Service
@Slf4j
public class GMPEmailService {

  private static final String PERSONAL_EDUCATION_NUMBER_PEN_REQUEST = "Your Personal Education Number (PEN) Request";
  private static final String VERIFY_EMAIL_SUBJECT = "Activate your GetMyPEN request within 24 hours of receiving this email";
  private final ApplicationProperties props;

  @Getter
  private final CHESEmailService chesEmailService;

  @Autowired
  public GMPEmailService(final ApplicationProperties props, CHESEmailService chesEmailService) {
    this.props = props;
    this.chesEmailService = chesEmailService;
  }

  public void sendCompletedPENRequestEmail(GMPRequestCompleteEmailEntity penRequest, boolean demographicsChanged) {
    String loginUrl = getLoginUrl(penRequest);
    log.debug("Sending completed PEN email");
    if (demographicsChanged) {
      getChesEmailService().sendEmail(penRequest, MessageFormat.format(props.getEmailTemplateCompleteRequestDemographicChangeGMP().replace("'", "''"), penRequest.getFirstName(), loginUrl, loginUrl, loginUrl), PERSONAL_EDUCATION_NUMBER_PEN_REQUEST);
    } else {
      getChesEmailService().sendEmail(penRequest, MessageFormat.format(props.getEmailTemplateCompletedRequestGMP().replace("'", "''"), penRequest.getFirstName(), loginUrl, loginUrl, loginUrl), PERSONAL_EDUCATION_NUMBER_PEN_REQUEST);
    }
    log.debug("Completed PEN email sent successfully");
  }


  public void sendRejectedPENRequestEmail(GMPRequestRejectedEmailEntity penRequest) {
    String loginUrl = getLoginUrl(penRequest);
    log.debug("Sending rejected PEN email");
    getChesEmailService().sendEmail(penRequest, MessageFormat.format(props.getEmailTemplateRejectedRequestGMP().replace("'", "''"), penRequest.getRejectionReason(), loginUrl, loginUrl, loginUrl), PERSONAL_EDUCATION_NUMBER_PEN_REQUEST);
    log.debug("Rejected PEN email sent successfully");
  }

  public void sendAdditionalInfoEmail(GMPRequestAdditionalInfoEmailEntity penRequest) {
    String loginUrl = getLoginUrl(penRequest);
    log.debug("Sending additional info PEN email");
    getChesEmailService().sendEmail(penRequest, MessageFormat.format(props.getEmailTemplateAdditionalInfoGMP().replace("'", "''"), loginUrl, loginUrl, loginUrl), PERSONAL_EDUCATION_NUMBER_PEN_REQUEST);
    log.debug("Additional info PEN email sent successfully");
  }

  public void sendStaleReturnedRequestNotificationEmail(GMPRequestAdditionalInfoEmailEntity penRequest) {
    String loginUrl = getLoginUrl(penRequest);
    log.debug("Sending sendStaleReturnedRequestNotificationEmail info GMP email");
    getChesEmailService().sendEmail(penRequest, MessageFormat.format(props.getEmailTemplateNotifyStaleReturnGMP().replace("'", "''"), loginUrl, loginUrl, loginUrl), PERSONAL_EDUCATION_NUMBER_PEN_REQUEST);
    log.debug("Stale Return Notification GMP email sent successfully");
  }

  /**
   * This method is responsible to send verification email.
   * the replacement starts with index 0 , so there are eight replacements in the template.
   *
   * @param emailVerificationEntity the payload containing the pen request id and email.
   */
  public void sendVerifyEmail(GMPRequestEmailVerificationEntity emailVerificationEntity) {
    log.debug("sending verify email.");
    final String emailBody = MessageFormat.format(props.getEmailTemplateVerifyEmailGMP().replace("'", "''"),
        emailVerificationEntity.getIdentityTypeLabel(), emailVerificationEntity.getVerificationUrl(), emailVerificationEntity.getJwtToken(),
        emailVerificationEntity.getIdentityTypeLabel(), emailVerificationEntity.getVerificationUrl(), emailVerificationEntity.getJwtToken(),
        emailVerificationEntity.getVerificationUrl(), emailVerificationEntity.getJwtToken());
    getChesEmailService().sendEmail(emailVerificationEntity, emailBody, VERIFY_EMAIL_SUBJECT);
    log.debug("verification email sent successfully.");
  }


  private String getLoginUrl(BaseEmailEntity baseEmailEntity) {
    if (BCSC.toString().equalsIgnoreCase(baseEmailEntity.getIdentityType())) {
      return props.getLoginBcscGMP();
    } else if (BASIC.toString().equalsIgnoreCase(baseEmailEntity.getIdentityType())) {
      return props.getLoginBasicGMP();
    } else {
      throw new InvalidParameterException("IdentityType provided, could not be resolved. :: " + baseEmailEntity.getIdentityType());
    }

  }
}
