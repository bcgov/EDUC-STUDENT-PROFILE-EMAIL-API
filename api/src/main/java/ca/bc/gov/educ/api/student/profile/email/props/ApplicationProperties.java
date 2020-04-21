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

  @Value("${ches.client.id}")
  private String chesClientID;
  @Value("${ches.client.secret}")
  private String chesClientSecret;
  @Value("${ches.token.url}")
  private String chesTokenURL;
  @Value("${ches.endpoint.url}")
  private String chesEndpointURL;
  @Value("${email.template.completedRequest}")
  private String emailTemplateCompletedRequest;
  @Value("${email.template.rejectedRequest}")
  private String emailTemplateRejectedRequest;
  @Value("${email.template.additionalInfoRequested}")
  private String emailTemplateAdditionalInfo;
  @Value("${email.template.verifyEmail}")
  private String emailTemplateVerifyEmail;

  /**
   * Expected property value in minutes.
   */
  @Value("${jwt.token.ttl}")
  private Integer timeToLive;

  @Value("${url.login.basic}")
  private String loginBasic;

  @Value("${url.login.bcsc}")
  private String loginBcsc;
}
