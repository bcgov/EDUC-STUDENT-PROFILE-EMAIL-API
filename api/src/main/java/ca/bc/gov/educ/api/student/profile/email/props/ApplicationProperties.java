package ca.bc.gov.educ.api.student.profile.email.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Class holds all application properties
 *
 * @author Marco Villeneuve
 */
@Component
@Getter
@Setter
public class ApplicationProperties {

  //common props
  @Value("${ches.client.id}")
  private String chesClientID;
  @Value("${ches.client.secret}")
  private String chesClientSecret;
  @Value("${ches.token.url}")
  private String chesTokenURL;
  @Value("${ches.endpoint.url}")
  private String chesEndpointURL;

  //UMP fields

  @Value("${email.template.completedRequest.ump}")
  private String emailTemplateCompletedRequestUMP;

  @Value("${email.template.rejectedRequest.ump}")
  private String emailTemplateRejectedRequestUMP;

  @Value("${email.template.additionalInfoRequested.ump}")
  private String emailTemplateAdditionalInfoUMP;

  @Value("${email.template.verifyEmail.ump}")
  private String emailTemplateVerifyEmailUMP;

  @Value("${url.login.basic.ump}")
  private String loginBasicUMP;

  @Value("${url.login.bcsc.ump}")
  private String loginBcscUMP;

  @Value("${email.template.notify.stale.return.ump}")
  private String emailTemplateNotifyStaleReturnUMP;

  //GMP fields

  @Value("${email.template.completedRequest.demographicChange.gmp}")
  private String emailTemplateCompleteRequestDemographicChangeGMP;

  @Value("${email.template.completedRequest.gmp}")
  private String emailTemplateCompletedRequestGMP;

  @Value("${email.template.rejectedRequest.gmp}")
  private String emailTemplateRejectedRequestGMP;

  @Value("${email.template.additionalInfoRequested.gmp}")
  private String emailTemplateAdditionalInfoGMP;

  @Value("${email.template.verifyEmail.gmp}")
  private String emailTemplateVerifyEmailGMP;

  @Value("${url.login.basic.gmp}")
  private String loginBasicGMP;

  @Value("${url.login.bcsc.gmp}")
  private String loginBcscGMP;

  @Value("${email.template.notify.stale.return.gmp}")
  private String emailTemplateNotifyStaleReturnGMP;

  @Value("${nats.server}")
  private String server;

  @Value("${nats.maxReconnect}")
  private int maxReconnect;

  @Value("${nats.connectionName}")
  private String connectionName;

  @Value("${notification.email.switch.on}")
  private Boolean isEmailNotificationSwitchedOn;
}
