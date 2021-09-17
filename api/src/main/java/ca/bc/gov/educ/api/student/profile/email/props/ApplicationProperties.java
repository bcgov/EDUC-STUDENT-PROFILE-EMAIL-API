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
  public static final String CORRELATION_ID = "correlationID";
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
  @Value("${url.login.basic.ump}")
  private String loginBasicUMP;

  @Value("${url.login.bcsc.ump}")
  private String loginBcscUMP;

  //GMP fields
  @Value("${url.login.basic.gmp}")
  private String loginBasicGMP;

  @Value("${url.login.bcsc.gmp}")
  private String loginBcscGMP;

  @Value("${nats.server}")
  private String server;

  @Value("${nats.maxReconnect}")
  private int maxReconnect;

  @Value("${nats.connectionName}")
  private String connectionName;

  @Value("${notification.email.switch.on}")
  private Boolean isEmailNotificationSwitchedOn;
}
